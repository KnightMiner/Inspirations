package knightminer.inspirations.recipes.tileentity;

import knightminer.inspirations.Inspirations;
import knightminer.inspirations.common.network.CauldronStateUpdatePacket;
import knightminer.inspirations.common.network.CauldronTransformUpatePacket;
import knightminer.inspirations.common.network.InspirationsNetwork;
import knightminer.inspirations.library.InspirationsTags;
import knightminer.inspirations.library.MiscUtil;
import knightminer.inspirations.library.recipe.RecipeTypes;
import knightminer.inspirations.library.recipe.cauldron.CauldronContentTypes;
import knightminer.inspirations.library.recipe.cauldron.contents.ICauldronContents;
import knightminer.inspirations.library.recipe.cauldron.recipe.ICauldronRecipe;
import knightminer.inspirations.library.recipe.cauldron.recipe.ICauldronTransform;
import knightminer.inspirations.library.recipe.cauldron.util.CauldronTemperature;
import knightminer.inspirations.recipes.block.EnhancedCauldronBlock;
import knightminer.inspirations.recipes.recipe.inventory.CauldronItemInventory;
import knightminer.inspirations.recipes.recipe.inventory.TileCauldronInventory;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.data.ModelDataMap;
import net.minecraftforge.client.model.data.ModelProperty;
import slimeknights.mantle.block.entity.MantleBlockEntity;
import slimeknights.mantle.datagen.MantleTags;
import slimeknights.mantle.recipe.helper.RecipeHelper;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * Tile entity logic for the cauldron, handles more complex content types
 * @deprecated Replaced by cauldron type having its own block
 */
@Deprecated
public class CauldronTileEntity extends MantleBlockEntity {
  /** Ticking on the server side */
  public static final BlockEntityTicker<CauldronTileEntity> SERVER_TICKER = (level, pos, state, be) -> be.serverTick(level);
  /** Ticking on the client side */
  public static final BlockEntityTicker<CauldronTileEntity> CLIENT_TICKER = (level, pos, state, be) -> be.clientTick(level);

  private static final DamageSource DAMAGE_BOIL = new DamageSource(Inspirations.prefix("boiling")).bypassArmor();
  public static final ModelProperty<ResourceLocation> TEXTURE = new ModelProperty<>();
  public static final ModelProperty<Boolean> FROSTED = new ModelProperty<>();
  public static final ModelProperty<Integer> OFFSET = new ModelProperty<>();

  // data objects
  private final IModelData data = new ModelDataMap.Builder()
      .withInitial(TEXTURE, CauldronContentTypes.DEFAULT.get().getTextureName())
      .withInitial(FROSTED, false)
      .withInitial(OFFSET, 0).build();
  private final TileCauldronInventory craftingInventory = new TileCauldronInventory(this);

  // capabilities
  /* TODO: have to determine what sort of automation I want, waiting on dispenser stuff
  private final CauldronItemHandler itemHandler = new CauldronItemHandler(this, craftingInventory);
  private final LazyOptional<IItemHandler> itemHandlerCap = LazyOptional.of(() -> itemHandler);
  private final CauldronFluidHandler fluidHandler = new CauldronFluidHandler(this);
  private final LazyOptional<IFluidHandler> fluidHandlerCap = LazyOptional.of(() -> fluidHandler);
   */

  // cauldron properties
  /** Current cauldron contents */
  private ICauldronContents contents;
  /** Offset amount for liquid. Formula is LEVEL * 4 + offset */
  private int levelOffset;

  // helper properties
  private EnhancedCauldronBlock cauldronBlock;
  /** Last recipe used in the cauldron */
  private ICauldronRecipe lastRecipe;

  // temperature cache
  /** If true, the cauldron is above a block that makes it boil */
  private Boolean isBoiling;
  /** If true, the cauldron is surrounded by blocks that make it freeze */
  private Boolean isFreezing;
  /** Last temperature of the cauldron */
  private CauldronTemperature temperature;

  // transform recipes
  private int timer;
  /** Name of the in progress transform recipe */
  private ResourceLocation currentTransformName;
  /** Transform recipe currently in progress */
  private ICauldronTransform currentTransform;
  /** Last successful transform found */
  private ICauldronTransform lastTransform;
  /** If true, updates the transform recipe during the next tick */
  private boolean updateTransform;

  /**
   * Creates a new cauldron with no block set
   */
  public CauldronTileEntity(BlockPos pos, BlockState state) {
    this(pos, state, null);//InspirationsRecipes.cauldron);
  }

  /**
   * Creates a new cauldron for the given block
   * @param block  Parent block
   */
  public CauldronTileEntity(BlockPos pos, BlockState state, EnhancedCauldronBlock block) {
    this(null/*InspirationsRecipes.tileCauldron*/, pos, state, block);
  }

  /**
   * Extendable constructor to swap tile entity type
   * @param type   Tile entity type
   * @param block  Parent block
   */
  protected CauldronTileEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, EnhancedCauldronBlock block) {
    super(type, pos, state);
    this.contents = CauldronContentTypes.DEFAULT.get();
    this.data.setData(TEXTURE, contents.getTextureName());
    this.cauldronBlock = block;
  }

  @Override
  public IModelData getModelData() {
    // ensure temperature is fetched so data is accurate
    getTemperature(false);
    return data;
  }


  /* contents and level */

  /**
   * Gets the current cauldron contents
   * @return current contents
   */
  public ICauldronContents getContents() {
    return contents;
  }

  /**
   * Gets the level of fluid in the cauldron, between 0 and {@link ICauldronRecipe#MAX}
   * @return  Cauldron fluid level
   */
  public int getFluidLevel() {
    return cauldronBlock.getLevel(getBlockState()) * 4 + levelOffset;
  }

  /**
   * Checks if this cauldron can mimic vanilla, meaning other handlers are allowed to run. There are two problematic cases:
   * * Contents are not water
   * * Level is between 1 and 3, as internally is that 1 in the block state (has water), but not a full "bottle", causing other mods to see more water than expected
   * @return  True if this cauldron can redirect recipes to other handlers
   */
  public boolean canMimicVanilla() {
    return levelOffset >= 0 && contents.isSimple();
  }

  /**
   * Called when the state, contents, level offset, or temperature changes to handle recipe updates
   */
  private void contentsChanged() {
    this.updateTransform = true;
    //this.itemHandler.clearCache();
    //this.tank.clearCache();
  }

  /**
   * Updates the cauldron state, including the block state level
   * @param contents  New contents, null for no change
   * @param level     New levels
   */
  public void updateStateAndBlock(@Nullable ICauldronContents contents, int level) {
    int stateLevel = updateStateFromLevels(contents, level);
    if (this.level != null) {
      cauldronBlock.setWaterLevel(this.level, worldPosition, getBlockState(), stateLevel);
    }
  }

  /**
   * Updates the state of the cauldron based on the given contents and levels and syncs to client.
   * Used in a few cases where state level update must be delayed
   * @param contents  New contents, null for no change
   * @param level     New level between 0 and 12. Set to -1 for no change
   * @return  New level for the block state
   */
  protected int updateStateFromLevels(@Nullable ICauldronContents contents, int level) {
    // first, determine the new level and contents
    // empty is empty of course
    int stateLevel, newOffset;
    if (level == 0) {
      newOffset = 0;
      stateLevel = 0;
    }
    else if (level < 4) {
      // if between 1 and 3, apply a negative offset
      newOffset = level - 4;
      stateLevel = 1;
    } else {
      // apply positive offsets otherwise
      newOffset = level % 4;
      stateLevel = level / 4;
    }

    // update TE props
    updateState(contents, newOffset);

    // return new level value
    return stateLevel;
  }

  /**
   * Updates just TE internal properties.
   * Used for transform recipe updates.
   * @param contents     New contents, null for no change
   * @param levelOffset  New level offset, between -3 and 3
   */
  protected void updateState(@Nullable ICauldronContents contents, int levelOffset) {
    // if the contents changed, update
    if (contents == null || this.contents.equals(contents)) {
      // set to null to signify no change, saves packet size
      contents = null;
    } else {
      this.contents = contents;
    }

    // if either changed, send a packet
    if (levelOffset != this.levelOffset || contents != null) {
      this.levelOffset = levelOffset;
      InspirationsNetwork.sendToClients(level, worldPosition, new CauldronStateUpdatePacket(worldPosition, contents, levelOffset));
      this.contentsChanged();
    }
  }

  /**
   * Updates contents and level offset in the TE and model data
   * @param contents     New contents, null for no change
   * @param levelOffset  New level offset
   * @return  True if anything changed
   */
  public boolean updateStateAndData(@Nullable ICauldronContents contents, int levelOffset) {
    boolean updated = false;

    // update offset and contents in TE and model data
    if (levelOffset != this.levelOffset) {
      this.levelOffset = levelOffset;
      data.setData(OFFSET, levelOffset);
      updated = true;
    }
    if (contents != null && !this.contents.equals(contents)) {
      this.contents = contents;
      data.setData(TEXTURE, contents.getTextureName());
      updated = true;
    }

    // return if we changed anything
    return updated;
  }


  /* temperature */

  /**
   * Checks if a state is considered fire in a cauldron
   * @param state State to check
   * @return True if the state is considered fire
   */
  public static boolean isCauldronFire(BlockState state) {
    if (state.is(InspirationsTags.Blocks.CAULDRON_FIRE)) {
      // if it has a lit property, use that (campfires, furnaces). Otherwise just needs to be in the tag
      return !state.hasProperty(BlockStateProperties.LIT) || state.getValue(BlockStateProperties.LIT);
    }
    return false;
  }

  /**
   * Checks if the given direction has enough ice
   * @param world      World
   * @param pos        Position
   * @param direction  Direction to check. Also checks opposite direction
   * @return  True if the direction has enough ice
   */
  private static boolean isDirectionFreezing(Level world, BlockPos pos, Direction direction) {
    return world.getBlockState(pos.relative(direction)).is(InspirationsTags.Blocks.CAULDRON_ICE)
           && world.getBlockState(pos.relative(direction.getOpposite())).is(InspirationsTags.Blocks.CAULDRON_ICE);
  }

  /**
   * Checks if two ice blocks on opposite sides
   * @param world  World
   * @param pos    Cauldron position
   * @return True if the state is considered freezing
   */
  public static boolean isFreezing(Level world, BlockPos pos) {
    // either axis must have two ice blocks
    return isDirectionFreezing(world, pos, Direction.NORTH) || isDirectionFreezing(world, pos, Direction.WEST);
  }

  /**
   * Calculates and caches the temperature
   * @return  Calculated temperature for the world and positions
   */
  public static CauldronTemperature calcTemperature(LevelAccessor world, BlockPos pos, boolean boiling, boolean freezing) {
    // overrides from neighbors
    if (boiling) {
      return freezing ? CauldronTemperature.NORMAL : CauldronTemperature.BOILING;
    }
    // freezing is freezing of course
    if (freezing) return CauldronTemperature.FREEZING;

    // boil if water evaporates
    if (world.dimensionType().ultraWarm()) {
      return CauldronTemperature.BOILING;
    }
    // freeze if biome is cold enough for snow/ice. direct methods do a bunch of ice/snow checks
    if (world.getBiome(pos).value().getTemperature(pos) < 0.15F) {
      return CauldronTemperature.FREEZING;
    }
    // normal otherwise
    return CauldronTemperature.NORMAL;
  }

  /**
   * Gets the current cauldron temperature
   * @param updateModelData  If true, updates model data on change
   * @return  Temperature at current location
   */
  private CauldronTemperature getTemperature(boolean updateModelData) {
    if (level == null) {
      return CauldronTemperature.NORMAL;
    }
    // if no temperature cache, calculate
    if (temperature == null) {
      // ensure we have cached freezing and boiling
      if (isBoiling == null) isBoiling = isCauldronFire(level.getBlockState(worldPosition.below()));
      if (isFreezing == null) isFreezing = isFreezing(level, worldPosition);
      temperature = calcTemperature(level, worldPosition, isBoiling, isFreezing);
      data.setData(FROSTED, temperature == CauldronTemperature.FREEZING);
      if (updateModelData) requestModelDataUpdate();
    }
    // return cached value
    return temperature;
  }

  /**
   * Gets the current cauldron temperature
   * @return  Temperature at current location
   */
  public CauldronTemperature getTemperature() {
    return getTemperature(true);
  }


  /**
   * Gets the direction based on the given offset
   * @param offset  Offset
   * @return  Direction based on position offset
   */
  private static Direction getDirection(BlockPos offset) {
    for (Direction direction : Direction.values()) {
      if (direction.getNormal().equals(offset)) {
        return direction;
      }
    }
    return Direction.UP;
  }

  /**
   * Called when a neighbor changes to invalidate the temperature cache
   * @param neighbor  Neighbor that changed
   */
  public void neighborChanged(BlockPos neighbor) {
    Direction direction = getDirection(neighbor.subtract(worldPosition));
    CauldronTemperature oldTemperature = temperature;
    if (direction == Direction.DOWN) {
      isBoiling = null;
      temperature = null;
      this.contentsChanged();
    } else if (direction.getAxis() != Axis.Y) {
      isFreezing = null;
      temperature = null;
      this.contentsChanged();
    }
    // on the client, immediately update temperature
    if (level != null && level.isClientSide) {
      temperature = getTemperature();
      if (temperature != oldTemperature) {
        MiscUtil.notifyClientUpdate(this);
      }
    }
  }


  /* behavior */

  @Nullable
  public ICauldronRecipe findRecipe() {
    if (level == null) {
      return null;
    }
    // try last recipe first
    if (lastRecipe != null && lastRecipe.matches(craftingInventory, level)) {
      return lastRecipe;
    }
    // fall back to finding a new recipe
    ICauldronRecipe recipe = level.getRecipeManager().getRecipeFor(RecipeTypes.CAULDRON.get(), craftingInventory, level).orElse(null);
    if (recipe != null) {
      lastRecipe = recipe;
      return recipe;
    }
    // no recipe found
    return null;
  }

  /**
   * Handles a cauldron recipe. Will do everything except update the cauldron level and clear the context.
   * The caller is responsible for handling those (as each caller has different needs)
   * @param stack         Stack to match for recipes
   * @param itemSetter    Logic to update the stack in the context. If null, have to manually handle item setting (for dispensers)
   * @param itemAdder     Logic to add a new stack to the context
   * @return  True if the recipe matched, false otherwise
   */
  private boolean handleRecipe(ItemStack stack, @Nullable Consumer<ItemStack> itemSetter, Consumer<ItemStack> itemAdder) {
    if (level == null) {
      return false;
    }

    // update the stack context
    craftingInventory.setItemContext(stack, itemSetter, itemAdder);

    // grab recipe
    ICauldronRecipe recipe = findRecipe();
    boolean success = false;
    if (recipe != null) {
      success = true;
      if (!level.isClientSide) {
        recipe.handleRecipe(craftingInventory);
      }
    }
    return success;
  }

  /**
   * Method to run cauldron interaction code
   * @return True if successful, false for pass
   */
  public boolean interact(Player player, InteractionHand hand) {
    // ensure we have a stack, or we can be done
    if (level == null) {
      return false;
    }

    // handle the recipe using the common function
    boolean success = handleRecipe(player.getItemInHand(hand), stack -> player.setItemInHand(hand, stack), CauldronItemInventory.getPlayerAdder(player));
    if (success) {
      updateStateAndBlock(craftingInventory.getContents(), craftingInventory.getLevel());
    }
    craftingInventory.clearContext();
    return success;
  }

  /**
   * Logic to run when a dispenser interacts with the cauldron
   * @param stack      Stack in the dispenser
   * @param itemAdder  Logic to add items to the dispenser
   * @return  Item stack after running the recipe, or null if no recipe ran
   */
  @Nullable
  public ItemStack handleDispenser(ItemStack stack, Consumer<ItemStack> itemAdder) {
    if (level == null) {
      return null;
    }

    // update level from the recipe and return the updated stack
    ItemStack result = null;
    if (handleRecipe(stack, null, itemAdder)) {
      updateStateAndBlock(craftingInventory.getContents(), craftingInventory.getLevel());
      result = craftingInventory.getStack();
    }
    craftingInventory.clearContext();
    return result;
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
    if (this.level == null) {
      return level;
    }

    // if an entity item, try crafting with it
    if (entity instanceof ItemEntity entityItem) {
      // skip items that we have already processed
      CompoundTag entityTags = entity.getPersistentData();
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

      // run recipe.
      // We need to copy when setting the item, to force it to update.
      boolean success = handleRecipe(entityItem.getItem(), stack -> entityItem.setItem(stack.copy()), stack -> {
        ItemEntity newItem = new ItemEntity(this.level, worldPosition.getX() + 0.5, worldPosition.getY() + 0.5, worldPosition.getZ() + 0.5, stack);
        newItem.getPersistentData().putBoolean(CauldronTileEntity.TAG_CAULDRON_CRAFTED, true);
        newItem.setDefaultPickUpDelay();
        this.level.addFreshEntity(newItem);
      });

      // on success, run the recipe a few more times
      if (success) {
        int matches = 0;
        while (lastRecipe.matches(craftingInventory, this.level) && matches < 64) {
          lastRecipe.handleRecipe(craftingInventory);
          matches++;
        }

        // safety check, recipes should never really match more than 4 times, but 64 just in case someone does a item change without updating state (64 is a stack)
        // basically, they should either be lowering/raising the level (max 4 times), or changing the state (not repeatable)
        if (matches == 64) {
          Inspirations.log.warn("Recipe '{}' matched too many times in a single tick. Either the level or the state should change to make it no longer match.", lastRecipe.getId());
        }
      }

      // kill entity if empty
      if (entityItem.getItem().isEmpty()) {
        entityItem.remove(RemovalReason.DISCARDED);
      } else if (success) {
        // if the recipe worked, mark as crafted
        entityTags.putBoolean(TAG_CAULDRON_CRAFTED, true);
      } else {
        // set a cooldown to reduce lag, so we are not searching the registry every tick
        // we do not just set crafted as that would prevent dropping in items one at a time where multiple are required
        entityTags.putInt(TAG_CAULDRON_COOLDOWN, 60);
      }

      // return the final level update
      int stateLevel = updateStateFromLevels(craftingInventory.getContents(), craftingInventory.getLevel());
      craftingInventory.clearContext();
      return stateLevel;
    } else if (level > 0) {
      Optional<Fluid> fluidType = contents.get(CauldronContentTypes.FLUID);
      if (fluidType.isPresent()) {
        // water puts out fire
        Fluid fluid = fluidType.get();
        if (fluid.is(MantleTags.Fluids.WATER)) {
          if (entity.isOnFire()) {
            entity.clearFire();
            level = level - 1;
          }
        }

        // hot fluids set fire to the entity
        else if (fluid.getAttributes().getTemperature() > 450 && !entity.fireImmune()) {
          entity.hurt(DamageSource.LAVA, 4.0F);
          entity.setSecondsOnFire(15);
          return level;
        }
      } else {
        // potions apply potion effects
        Optional<Potion> potionType = contents.get(CauldronContentTypes.POTION);
        if (potionType.isPresent() && entity instanceof LivingEntity living) {

          // if any of the effects are not currently on the player, apply it and lower the level
          List<MobEffectInstance> effects = potionType.get().getEffects();
          if (effects.stream().anyMatch(effect -> !living.hasEffect(effect.getEffect()))) {
            for (MobEffectInstance effect : effects) {
              if (effect.getEffect().isInstantenous()) {
                effect.getEffect().applyInstantenousEffect(null, null, living, effect.getAmplifier(), 1.0D);
              } else {
                living.addEffect(new MobEffectInstance(effect));
              }
            }
            level = level - 1;
          }
          return level;
        }
      }

      // if the cauldron is boiling, boiling the entity
      if (getTemperature() == CauldronTemperature.BOILING) {
        entity.hurt(DAMAGE_BOIL, 2.0F);
      }
    }
    return level;
  }

  /* Transform recipes */

  @Override
  public void setBlockState(BlockState state) {
    super.setBlockState(state);
    this.contentsChanged();
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
    if (level == null || (currentTransform != null && currentTransform.matches(craftingInventory, level))) {
      return;
    }

    // recipe changing means reset the timer
    timer = 0;

    // try to find a recipe
    ICauldronTransform transform = null;
    if (getFluidLevel() > 0) {
      if (lastTransform != null && lastTransform.matches(craftingInventory, level)) {
        transform = lastTransform;
      } else {
        Optional<ICauldronTransform> newTransform = level.getRecipeManager().getRecipeFor(RecipeTypes.CAULDRON_TRANSFORM.get(), craftingInventory, level);
        if (newTransform.isPresent()) {
          transform = lastTransform = newTransform.get();
        }
      }
    }

    // handles both null mostly, but also the odd case of it matching again
    if (currentTransform != transform) {
      // update and sync to clients
      currentTransform = transform;
      InspirationsNetwork.sendToClients(level, worldPosition, new CauldronTransformUpatePacket(worldPosition, transform));
    }
  }

  /** Tick on the server side */
  private void serverTick(Level level) {
    assert !level.isClientSide;
    // updates the transform recipe
    if (updateTransform) {
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
    if (timer >= currentTransform.getTime()) {
      timer = 0;

      // play sound effect, note its before contents update
      SoundEvent sound = currentTransform.getSound();
      level.playSound(null, worldPosition, sound, SoundSource.BLOCKS, 1.0f, 1.0f);

      // set contents will clear the current transform if no longer current
      // have to pass in level offset as this function is reused a lot, so just use current
      updateState(currentTransform.getContentOutput(craftingInventory), levelOffset);
    }
  }

  /** Tick on the client side */
  private void clientTick(Level level) {
    // timer updates on both sides, easier than syncing
    if (currentTransform != null) {
      timer++;
    }
  }

  /**
   * Called on the client to update the current transform recipe
   * @param recipe  New recipe
   */
  public void setTransformRecipe(@Nullable ICauldronTransform recipe) {
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
    return timer * 5 / currentTransform.getTime();
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

  /* Automation */

  /* TODO: see above
  @Override
  public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side) {
    if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
      return itemHandlerCap.cast();
    }
    if (cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
      return fluidHandlerCap.cast();
    }
    return super.getCapability(cap, side);
  }

  @Override
  protected void invalidateCaps() {
    super.invalidateCaps();
    itemHandlerCap.invalidate();
    fluidHandlerCap.invalidate();
  }
  */

  /* NBT */
  private static final String TAG_CONTENTS = "contents";
  private static final String TAG_LEVEL_OFFSET = "level_offset";
  private static final String TAG_TRANSFORM = "transform";
  private static final String TAG_TIMER = "timer";

  @Override
  public void setLevel(Level level) {
    super.setLevel(level);
    // if we have a recipe name, swap recipe name for recipe instance
    if (currentTransformName != null) {
      loadTransform(level, currentTransformName);
      currentTransformName = null;
    }
  }

  /**
   * Updates the current transform based on the given name
   * @param world  World instance
   * @param name   Recipe name
   */
  private void loadTransform(Level world, ResourceLocation name) {
    RecipeHelper.getRecipe(world.getRecipeManager(), name, ICauldronTransform.class).ifPresent(recipe -> this.currentTransform = recipe);
  }

  @Override
  protected boolean shouldSyncOnUpdate() {
    return true;
  }

  @Override
  protected void saveSynced(CompoundTag tags) {
    super.saveSynced(tags);
    tags.put(TAG_CONTENTS, getContents().toNBT());
    // write transform if present, or transform name if we somehow wrote before world is set
    if (currentTransform != null) {
      tags.putString(TAG_TRANSFORM, currentTransform.getId().toString());
    } else if (currentTransformName != null) {
      tags.putString(TAG_TRANSFORM, currentTransformName.toString());
    }
    // update the timer from NBT
    tags.putInt(TAG_TIMER, timer);
    tags.putInt(TAG_LEVEL_OFFSET, levelOffset);
  }

  @Override
  public void load(CompoundTag tags) {
    super.load(tags);

    // update block reference
    Block block = getBlockState().getBlock();
    if (block instanceof EnhancedCauldronBlock) {
      this.cauldronBlock = (EnhancedCauldronBlock)block;
    }

    // update current transform
    if (tags.contains(TAG_TRANSFORM, Tag.TAG_STRING)) {
      ResourceLocation name = new ResourceLocation(tags.getString(TAG_TRANSFORM));
      // if we have a world, fetch the recipe
      if (level != null) {
        loadTransform(level, name);
      } else {
        // otherwise fetch the recipe when the world is set
        currentTransformName = name;
      }
    }

    // update contents
    updateStateAndData(CauldronContentTypes.read(tags.getCompound(TAG_CONTENTS)), tags.getInt(TAG_LEVEL_OFFSET));

    // update the timer from NBT
    timer = tags.getInt(TAG_TIMER);
  }
}
