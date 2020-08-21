package knightminer.inspirations.recipes.recipe.cauldron;

import knightminer.inspirations.library.recipe.cauldron.CauldronContentTypes;
import knightminer.inspirations.library.recipe.cauldron.contents.ICauldronFluid;
import knightminer.inspirations.library.recipe.cauldron.inventory.ICauldronInventory;
import knightminer.inspirations.library.recipe.cauldron.inventory.IModifyableCauldronInventory;
import knightminer.inspirations.library.recipe.cauldron.recipe.ICauldronRecipe;
import knightminer.inspirations.recipes.InspirationsRecipes;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
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
    // drain the fluid from the handler
    FluidStack drained;

    // if empty, drain anything
    if (inv.getLevel() == 0) {
      return handler.drain(BUCKET_VOLUME, action);
    }

    // if filled, drain a specific fluid
    return inv.getContents()
              .as(CauldronContentTypes.FLUID)
              .map(ICauldronFluid::getFluid)
              .map(fluid -> handler.drain(new FluidStack(fluid, BUCKET_VOLUME), action))
              .orElse(FluidStack.EMPTY);
  }

  @Override
  public boolean matches(ICauldronInventory inv, World worldIn) {
    // if full, no fill
    int level = inv.getLevel();
    if (level == MAX) {
      return false;
    }

    // must have a fluid handler
    return FluidUtil.getFluidHandler(inv.getStack()).map(handler -> {
      // drain the fluid from the handler
      FluidStack drained = drain(inv, handler, FluidAction.SIMULATE);

      // ensure the fluid is valid
      return !drained.isEmpty() && drained.getAmount() == BUCKET_VOLUME && !drained.hasTag();
    }).orElse(false);
  }

  @Override
  public void handleRecipe(IModifyableCauldronInventory inv) {
    FluidUtil.getFluidHandler(inv.getStack()).ifPresent(handler -> {
      // if we drain less or more, unfortunately the non bucket amount is lost. It passed in simulate, so this is a mod problem
      FluidStack drained = drain(inv, handler, FluidAction.EXECUTE);
      if (drained.getAmount() >= BUCKET_VOLUME) {
        // fill cauldron
        inv.setLevel(3);
        // update contents
        inv.setContents(CauldronContentTypes.FLUID.of(drained.getFluid()));
        // replace held item with container
        inv.setStack(handler.getContainer());
      }
    });
  }

  @Override
  public ResourceLocation getId() {
    return id;
  }

  @Override
  public IRecipeSerializer<?> getSerializer() {
    return InspirationsRecipes.emptyBucketSerializer;
  }


  /* Extra methods for completion, even though they are mostly unneeded */

  /** @deprecated Use {@link #getCraftingResult(ICauldronInventory)} */
  @Deprecated
  @Override
  public ItemStack getRecipeOutput() {
    return new ItemStack(Items.BUCKET);
  }

  @Override
  public ItemStack getCraftingResult(ICauldronInventory inv) {
    return inv.getStack().getContainerItem().copy();
  }
}
