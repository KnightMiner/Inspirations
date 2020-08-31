package knightminer.inspirations.recipes.tileentity;

import knightminer.inspirations.Inspirations;
import knightminer.inspirations.common.network.CauldronContentUpatePacket;
import knightminer.inspirations.common.network.CauldronTransformUpatePacket;
import knightminer.inspirations.common.network.InspirationsNetwork;
import knightminer.inspirations.library.recipe.RecipeTypes;
import knightminer.inspirations.library.recipe.cauldron.CauldronContentTypes;
import knightminer.inspirations.library.recipe.cauldron.contents.EmptyCauldronContents;
import knightminer.inspirations.library.recipe.cauldron.contents.ICauldronContents;
import knightminer.inspirations.library.recipe.cauldron.recipe.CauldronTransform;
import knightminer.inspirations.library.recipe.cauldron.recipe.ICauldronRecipe;
import knightminer.inspirations.recipes.InspirationsRecipes;
import knightminer.inspirations.recipes.block.EnhancedCauldronBlock;
import knightminer.inspirations.recipes.recipe.inventory.CauldronItemInventory;
import knightminer.inspirations.recipes.recipe.inventory.TileCauldronInventory;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Potion;
import net.minecraft.tags.FluidTags;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.data.ModelProperty;
import net.minecraftforge.common.util.Constants.BlockFlags;
import net.minecraftforge.common.util.Constants.NBT;
import slimeknights.mantle.client.model.data.SinglePropertyData;
import slimeknights.mantle.recipe.RecipeHelper;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * Tile entity logic for the cauldron, handles more complex content types
 */
public class CauldronTileEntity extends TileEntity implements ITickableTileEntity {
  private static final DamageSource DAMAGE_BOIL = new DamageSource(Inspirations.prefix("boiling")).setDamageBypassesArmor();
  public static final ModelProperty<ResourceLocation> TEXTURE = new ModelProperty<>();

  // data objects
  private final IModelData data = new SinglePropertyData<>(TEXTURE, EmptyCauldronContents.INSTANCE.getTextureName());
  private final TileCauldronInventory craftingInventory = new TileCauldronInventory(this);

  // mutable properties
  private ICauldronContents contents;
  private EnhancedCauldronBlock cauldronBlock;
  private ICauldronRecipe lastRecipe;

  // transform recipes
  private int timer;
  /** Name of the in progress transform recipe */
  private ResourceLocation currentTransformName;
  /** Transform recipe currently in progress */
  private CauldronTransform currentTransform;
  /** Last successful transform found */
  private CauldronTransform lastTransform;
  /** If true, updates the transform recipe during the next tick */
  private boolean updateTransform;

  /**
   * Creates a new cauldron with no block set
   */
  public CauldronTileEntity() {
    this(InspirationsRecipes.cauldron);
  }

  /**
   * Creates a new cauldron for the given block
   * @param block  Parent block
   */
  public CauldronTileEntity(EnhancedCauldronBlock block) {
    this(InspirationsRecipes.tileCauldron, block);
  }

  /**
   * Extendable constructor to swap tile entity type
   * @param type   Tile entity type
   * @param block  Parent block
   */
  protected CauldronTileEntity(TileEntityType<?> type, EnhancedCauldronBlock block) {
    super(type);
    this.contents = EmptyCauldronContents.INSTANCE;
    this.data.setData(TEXTURE, contents.getTextureName());
    this.cauldronBlock = block;
  }

  @Override
  public IModelData getModelData() {
    return data;
  }

  /**
   * Gets the level of fluid in the cauldron
   * @return  Cauldron fluid level
   */
  public int getLevel() {
    return craftingInventory.getLevel();
  }

  /**
   * Sets the fluid level in the cauldron
   * @param level  New fluid level
   */
  public void setLevel(int level) {
    if (world != null) {
      cauldronBlock.setWaterLevel(world, pos, getBlockState(), level);
    }
  }

  /**
   * Gets the current cauldron contents
   * @return current contents
   */
  public ICauldronContents getContents() {
    if (getLevel() == 0) {
      return EmptyCauldronContents.INSTANCE;
    }
    return contents;
  }

  /**
   * Gets the block instance responsible for this cauldron
   * @return  Block instance
   */
  public EnhancedCauldronBlock getBlock() {
    return cauldronBlock;
  }

  /**
   * Updates the cauldron contents
   * @param contents  New contents
   */
  public void setContents(ICauldronContents contents) {
    // noting to do
    if (this.contents.equals(contents)) {
      return;
    }

    // normalize empty into water
    if (contents == EmptyCauldronContents.INSTANCE) {
      contents = CauldronContentTypes.FLUID.of(Fluids.WATER);
    }
    this.contents = contents;

    // update display client side, sync to client serverside
    if (world != null) {
      if (world.isRemote) {
        this.data.setData(TEXTURE, contents.getTextureName());
        this.requestModelDataUpdate();
        BlockState state = getBlockState();
        world.notifyBlockUpdate(pos, state, state, BlockFlags.NO_RERENDER | BlockFlags.NO_NEIGHBOR_DROPS);
      } else {
        InspirationsNetwork.sendToClients(world, pos, new CauldronContentUpatePacket(pos, contents));
        this.updateTransform();
      }
    }
  }


  /* behavior */

  /**
   * Handles a cauldron recipe. Will do everything except update the cauldron level
   * @param stack       Stack to match for recipes
   * @param itemSetter  Logic to update the stack in the context. If null, have to manually handle item setting (for dispensers)
   * @param itemAdder   Logic to add a new stack to the context
   * @return  True if the recipe matched, false otherwise
   */
  private boolean handleRecipe(ItemStack stack, @Nullable Consumer<ItemStack> itemSetter, Consumer<ItemStack> itemAdder) {
    if (world == null) {
      return false;
    }

    // update the stack context
    craftingInventory.setItemContext(stack, itemSetter, itemAdder);

    // grab recipe
    ICauldronRecipe recipe;
    if (lastRecipe != null && lastRecipe.matches(craftingInventory, world)) {
      recipe = lastRecipe;
    } else {
      recipe = world.getRecipeManager().getRecipe(RecipeTypes.CAULDRON, craftingInventory, world).orElse(null);
    }

    // if we found a match
    boolean success = false;
    if (recipe != null) {
      lastRecipe = recipe;
      success = true;
      if (!world.isRemote) {
        recipe.handleRecipe(craftingInventory);
        // TODO: play sound based on (old?) contents
      }
    }

    // clear any extra context and return
    craftingInventory.clearContext();
    return success;
  }

  /**
   * Method to run cauldron interaction code
   * @return True if successful, false for pass
   */
  public boolean interact(PlayerEntity player, Hand hand) {
    // ensure we have a stack, or we can be done
    if (world == null || player.getHeldItem(hand).isEmpty()) {
      return false;
    }

    // handle the recipe using the common function
    boolean success = handleRecipe(player.getHeldItem(hand), stack -> player.setHeldItem(hand, stack), CauldronItemInventory.getPlayerAdder(player));
    if (success) {
      setLevel(craftingInventory.getLevel());
      return true;
    }
    return false;
  }

  /**
   * Logic to run when a dispenser interacts with the cauldron
   * @param stack      Stack in the dispenser
   * @param itemAdder  Logic to add items to the dispenser
   * @return  Item stack after running the recipe, or null if no recipe ran
   */
  @Nullable
  public ItemStack handleDispenser(ItemStack stack, Consumer<ItemStack> itemAdder) {
    if (world == null) {
      return null;
    }

    // update level from the recipe and return the updated stack
    if (handleRecipe(stack, null, itemAdder)) {
      setLevel(craftingInventory.getLevel());
      return craftingInventory.getStack();
    }
    return null;
  }

  private static final String TAG_CAULDRON_CRAFTED = "cauldron_crafted";
  private static final String TAG_CAULDRON_COOLDOWN = "cauldron_cooldown";

  /**
   * Called when an entity collides with the cauldron
   * @param entity Entity that collided
   * @param level  Cauldron level
   * @return New cauldron level after the collision
   */
  public int onEntityCollide(Entity entity, int level, BlockState currentState) {
    if (world == null) {
      return level;
    }

    // if an entity item, try crafting with it
    if (entity instanceof ItemEntity) {
      // skip items that we have already processed
      ItemEntity entityItem = (ItemEntity)entity;
      CompoundNBT entityTags = entity.getPersistentData();
      // if it was tagged, skip it
      if (entityTags.getBoolean(TAG_CAULDRON_CRAFTED)) {
        return level;
      }

      // otherwise, if it has a cooldown, reduce the cooldown
      int cooldown = entityTags.getInt(TAG_CAULDRON_COOLDOWN);
      if (cooldown > 0) {
        entityTags.putInt(TAG_CAULDRON_COOLDOWN, cooldown - 1);
        return level;
      }

      // run recipe
      boolean success = handleRecipe(entityItem.getItem(), stack -> {
        if (stack.isEmpty()) {
          entityItem.remove();
        } else {
          entityItem.setItem(stack);
        }
      }, stack -> {
        ItemEntity newItem = new ItemEntity(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, stack);
        newItem.getPersistentData().putBoolean(CauldronTileEntity.TAG_CAULDRON_CRAFTED, true);
        world.addEntity(newItem);
      });

      // on success, run the recipe a few more times
      if (success) {
        int matches = 0;
        while (lastRecipe.matches(craftingInventory, world) && matches < 10) {
          lastRecipe.handleRecipe(craftingInventory);
          matches++;
        }

        // safety check, recipes should never really match more than 4 times, but 10 just in case
        // basically, they should either be lowering/raising the level (max 4 times), or changing the state (not repeatable)
        if (matches == 10) {
          Inspirations.log.warn("Recipe '{}' matched too many times in a single tick. Either the level or the state should change to make it no longer match.", lastRecipe.getId());
        }
      }

      // if alive, prevent another craft
      if (entityItem.isAlive()) {
        if (success) {
          entityTags.putBoolean(TAG_CAULDRON_CRAFTED, true);
        } else {
          // set a cooldown to reduce lag, so we are not searching the registry every tick
          // we do not just set crafted as that would prevent dropping in items one at a time where multiple are required
          entityTags.putInt(TAG_CAULDRON_COOLDOWN, 60);
        }
      }

      // return the final level update
      return craftingInventory.getLevel();

    } else if (level > 0) {
      Optional<Fluid> fluidType = contents.get(CauldronContentTypes.FLUID);
      if (fluidType.isPresent()) {
        // water estinguishs fire
        Fluid fluid = fluidType.get();
        if (FluidTags.WATER.contains(fluid)) {
          if (entity.isBurning()) {
            entity.extinguish();
            level = level - 1;
          }
        }

        // hot fluids set fire to the entity
        else if (fluid.getAttributes().getTemperature() > 450 && !entity.isImmuneToFire()) {
          entity.attackEntityFrom(DamageSource.LAVA, 4.0F);
          entity.setFire(15);
          return level;
        }
      } else {
        // potions apply potion effects
        Optional<Potion> potionType = contents.get(CauldronContentTypes.POTION);
        if (potionType.isPresent() && entity instanceof LivingEntity) {
          LivingEntity living = (LivingEntity)entity;

          // if any of the effects are not currently on the player, apply it and lower the level
          List<EffectInstance> effects = potionType.get().getEffects();
          if (effects.stream().anyMatch(effect -> !living.isPotionActive(effect.getPotion()))) {
            for (EffectInstance effect : effects) {
              if (effect.getPotion().isInstant()) {
                effect.getPotion().affectEntity(null, null, living, effect.getAmplifier(), 1.0D);
              } else {
                living.addPotionEffect(new EffectInstance(effect));
              }
            }
            level = level - 1;
          }
          return level;
        }
      }

      // if the cauldron is boiling, boiling the entity
      if (cauldronBlock.isBoiling(currentState)) {
        entity.attackEntityFrom(DAMAGE_BOIL, 2.0F);
      }
    }
    return level;
  }

  /* Transform recipes */

  @Override
  public void updateContainingBlockInfo() {
    super.updateContainingBlockInfo();
    this.updateTransform = true;
  }

  /**
   * Update the in progress transform recipe
   */
  public void updateTransform() {
    // stop if we have a non-loaded transform
    if (currentTransformName != null) {
      return;
    }

    // if the current transform matches, do nothing
    craftingInventory.refreshLevel();
    if (world == null || (currentTransform != null && currentTransform.matches(craftingInventory, world))) {
      return;
    }

    // recipe changing means reset the timer
    timer = 0;

    // try to find a recipe
    CauldronTransform transform = null;
    if (getLevel() > 0) {
      if (lastTransform != null && lastTransform.matches(craftingInventory, world)) {
        transform = lastTransform;
      } else {
        Optional<CauldronTransform> newTransform = world.getRecipeManager().getRecipe(RecipeTypes.CAULDRON_TRANSFORM, craftingInventory, world);
        if (newTransform.isPresent()) {
          transform = lastTransform = newTransform.get();
        }
      }
    }

    // handles both null mostly, but also the odd case of it matching again
    if (currentTransform != transform) {
      // update and sync to clients
      currentTransform = transform;
      InspirationsNetwork.sendToClients(world, pos, new CauldronTransformUpatePacket(pos, transform));
    }
  }

  @Override
  public void tick() {
    if (world == null) {
      return;
    }

    // updates the transform recipe
    if (updateTransform && !world.isRemote) {
      this.updateTransform();
      updateTransform = false;
    }

    // if we have a recipe
    if (currentTransform == null) {
      return;
    }

    // timer updates on both sides, easier than syncing
    timer++;

    // if the recipe is done, run recipe
    if (!world.isRemote && timer >= currentTransform.getTime(craftingInventory)) {
      timer = 0;

      // set contents will clear the current transform if no longer current
      setContents(currentTransform.getOutput(craftingInventory));

      // play sound effect
      world.playSound(null, pos, SoundEvents.BLOCK_BREWING_STAND_BREW, SoundCategory.BLOCKS, 1.0f, 1.0f);
    }
  }

  /**
   * Called on the client to update the current transform recipe
   * @param recipe  New recipe
   */
  public void setTransformRecipe(@Nullable CauldronTransform recipe) {
    this.currentTransform = recipe;
    timer = 0;
  }

  /**
   * Gets the number of fluid transform particles to display
   * @return  Particle count, 0 for no particles
   */
  public int getTransformParticles() {
    if (currentTransform == null) {
      return 0;
    }
    return timer * 5 / currentTransform.getTime(craftingInventory);
  }

  /*
   * Called when the cauldron is broken
   * @param pos   Position of the cauldron
   * @param level Cauldron level
   *
  public void onBreak(BlockPos pos, int level) {
    if (world == null) {
      return;
    }
    switch (getContentType()) {
      case FLUID:
        BlockState block = state.getFluid().getAttributes().getBlock(null, null, state.getFluid().getDefaultState());
        if (block != null) {
          // height varies based on what is left. Will place a source if the cauldron is full
          if (level == (Config.enableBiggerCauldron() ? 4 : 3)) {
            world.setBlockState(pos, block);
          }
        }
        break;
      case POTION:
        Potion potion = state.getPotion();
        AreaEffectCloudEntity cloud = new AreaEffectCloudEntity(this.world, pos.getX() + 0.5F, pos.getY() + 0.5F, pos.getZ() + 0.5F);
        cloud.setRadius(0.5F * level + 0.5F);
        cloud.setDuration(20 * (level + 1));
        cloud.setRadiusOnUse(-0.5F);
        cloud.setWaitTime(10);
        cloud.setRadiusPerTick(-cloud.getRadius() / cloud.getDuration());
        cloud.setPotion(potion);

        for (EffectInstance effect : potion.getEffects()) {
          cloud.addEffect(new EffectInstance(effect));
        }

        this.world.addEntity(cloud);
        break;
    }
  }
   */

  /* NBT */
  private static final String TAG_CONTENTS = "contents";
  private static final String TAG_TRANSFORM = "transform";
  private static final String TAG_TIMER = "timer";

  @Override
  public void setWorldAndPos(World world, BlockPos pos) {
    super.setWorldAndPos(world, pos);
    // if we have a recipe name, swap recipe name for recipe instance
    if (currentTransformName != null) {
      loadTransform(world, currentTransformName);
      currentTransformName = null;
    }
  }

  /**
   * Updates the current transform based on the given name
   * @param world  World instance
   * @param name   Recipe name
   */
  private void loadTransform(World world, ResourceLocation name) {
    RecipeHelper.getRecipe(world.getRecipeManager(), name, CauldronTransform.class).ifPresent(recipe -> this.currentTransform = recipe);
  }

  @Override
  public CompoundNBT getUpdateTag() {
    // new tag instead of super since default implementation calls the super of writeToNBT
    return write(new CompoundNBT());
  }

  @Override
  public CompoundNBT write(CompoundNBT tags) {
    tags = super.write(tags);
    tags.put(TAG_CONTENTS, CauldronContentTypes.toNbt(getContents()));
    // write transform if present, or transform name if we somehow wrote before world is set
    if (currentTransform != null) {
      tags.putString(TAG_TRANSFORM, currentTransform.getId().toString());
    } else if (currentTransformName != null) {
      tags.putString(TAG_TRANSFORM, currentTransformName.toString());
    }
    // update the timer from NBT
    tags.putInt(TAG_TIMER, timer);
    return tags;
  }

  @Override
  public void read(BlockState state, CompoundNBT tags) {
    super.read(state, tags);

    // update block reference
    Block block = state.getBlock();
    if (block instanceof EnhancedCauldronBlock) {
      this.cauldronBlock = (EnhancedCauldronBlock)block;
    }

    // update current transform
    if (tags.contains(TAG_TRANSFORM, NBT.TAG_STRING)) {
      ResourceLocation name = new ResourceLocation(tags.getString(TAG_TRANSFORM));
      // if we have a world, fetch the recipe
      if (world != null) {
        loadTransform(world, name);
      } else {
        // otherwise fetch the recipe when the world is set
        currentTransformName = name;
      }
    }

    // update contents
    setContents(CauldronContentTypes.read(tags.getCompound(TAG_CONTENTS)));

    // update the timer from NBT
    timer = tags.getInt(TAG_TIMER);
  }
}
