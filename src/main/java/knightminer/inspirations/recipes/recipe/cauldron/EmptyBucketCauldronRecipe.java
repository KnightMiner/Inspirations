package knightminer.inspirations.recipes.recipe.cauldron;

import knightminer.inspirations.library.recipe.RecipeSerializers;
import knightminer.inspirations.library.recipe.cauldron.CauldronContentTypes;
import knightminer.inspirations.library.recipe.cauldron.inventory.ICauldronInventory;
import knightminer.inspirations.library.recipe.cauldron.inventory.IModifyableCauldronInventory;
import knightminer.inspirations.library.recipe.cauldron.recipe.ICauldronRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;

import static net.minecraftforge.fluids.FluidAttributes.BUCKET_VOLUME;

/**
 * Recipe to empty a bucket into the cauldron. Supports any generic fluid handler item.
 */
public class EmptyBucketCauldronRecipe implements ICauldronRecipe {
  private final ResourceLocation id;
  public EmptyBucketCauldronRecipe(ResourceLocation id) {
    this.id = id;
  }

  /**
   * Drains a fluid handler of 1000mb fluid
   * @param inv      Cauldron inventory instnace
   * @param handler  Fluid handler
   * @return  Fluid stack drained, or empty if nothing drained
   */
  private static FluidStack drain(ICauldronInventory inv, IFluidHandlerItem handler, FluidAction action) {
    // if empty, drain anything
    if (inv.getLevel() == 0) {
      return handler.drain(BUCKET_VOLUME, action);
    }

    // if filled, drain a specific fluid
    return inv.getContents()
              .get(CauldronContentTypes.FLUID)
              .map(fluid -> handler.drain(new FluidStack(fluid, BUCKET_VOLUME), action))
              .orElse(FluidStack.EMPTY);
  }

  @Override
  public boolean matches(ICauldronInventory inv, Level worldIn) {
    // if full, no fill
    int level = inv.getLevel();
    if (level == MAX) {
      return false;
    }

    // on the chance the container is of size greater than 1, copy, as some containers don't process if not size 1
    ItemStack stack;
    if (inv.getStack().getCount() != 1) {
      stack = inv.getStack().copy();
      stack.setCount(1);
    } else {
      stack = inv.getStack();
    }
    // must have a fluid handler
    return FluidUtil.getFluidHandler(stack).map(handler -> {
      // drain the fluid from the handler
      FluidStack drained = drain(inv, handler, FluidAction.SIMULATE);

      // ensure the fluid is valid
      return !drained.isEmpty() && drained.getAmount() == BUCKET_VOLUME && !drained.hasTag() && (!inv.isSimple() || drained.getFluid() == Fluids.WATER);
    }).orElse(false);
  }

  @Override
  public void handleRecipe(IModifyableCauldronInventory inv) {
    ItemStack stack = inv.splitStack(1);
    FluidUtil.getFluidHandler(stack).ifPresent(handler -> {
      // if we drain less or more, unfortunately the non bucket amount is lost. It passed in simulate, so this is a mod problem
      FluidStack drained = drain(inv, handler, FluidAction.EXECUTE);
      if (drained.getAmount() >= BUCKET_VOLUME) {
        // update contents
        Fluid fluid = drained.getFluid();
        inv.setContents(CauldronContentTypes.FLUID.of(fluid));
        // fill cauldron
        inv.setLevel(MAX);
        // replace held item with container
        inv.setOrGiveStack(handler.getContainer());

        // play sound
        SoundEvent sound = drained.getFluid().getAttributes().getEmptySound(drained);
        if (sound == null) {
          sound = fluid.is(FluidTags.LAVA) ? SoundEvents.BUCKET_EMPTY_LAVA : SoundEvents.BUCKET_EMPTY;
        }
        inv.playSound(sound);
      }
    });
  }

  @Override
  public ResourceLocation getId() {
    return id;
  }

  @Override
  public RecipeSerializer<?> getSerializer() {
    return RecipeSerializers.CAULDRON_EMPTY_BUCKET;
  }
}
