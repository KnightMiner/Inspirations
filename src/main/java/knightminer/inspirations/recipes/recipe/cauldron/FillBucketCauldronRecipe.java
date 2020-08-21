package knightminer.inspirations.recipes.recipe.cauldron;

import knightminer.inspirations.library.recipe.cauldron.CauldronContentTypes;
import knightminer.inspirations.library.recipe.cauldron.contents.ICauldronFluid;
import knightminer.inspirations.library.recipe.cauldron.inventory.ICauldronInventory;
import knightminer.inspirations.library.recipe.cauldron.inventory.IModifyableCauldronInventory;
import knightminer.inspirations.library.recipe.cauldron.recipe.ICauldronRecipe;
import knightminer.inspirations.recipes.InspirationsRecipes;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;

/**
 * Recipe that fills a bucket from cauldron contents. Supports any generic fluid handler item.
 */
public class FillBucketCauldronRecipe implements ICauldronRecipe {
  private final ResourceLocation id;
  public FillBucketCauldronRecipe(ResourceLocation id) {
    this.id = id;
  }

  @Override
  public boolean matches(ICauldronInventory inv, World worldIn) {
    // must be full or no fill bucket
    if (inv.getLevel() < MAX) {
      return false;
    }

    // must be a fluid
    return inv.getContents()
              .as(CauldronContentTypes.FLUID)
              .map(ICauldronFluid::getFluid)
              // must have a fluid handler, I really wish you could flatmap a lazy optional
              .map(fluid -> FluidUtil.getFluidHandler(inv.getStack())
                                     // handler must be fillable with the given fluid and must take 1000mb
                                     .map(handler -> handler.fill(new FluidStack(fluid, FluidAttributes.BUCKET_VOLUME), FluidAction.SIMULATE) == FluidAttributes.BUCKET_VOLUME)
                                     .orElse(false))
              .orElse(false);
  }

  @Override
  public void handleRecipe(IModifyableCauldronInventory inv) {
    inv.getContents()
       .as(CauldronContentTypes.FLUID)
       .map(ICauldronFluid::getFluid)
       // must have a fluid handler, I really wish you could flatmap a lazy optional
       .ifPresent(fluid -> FluidUtil.getFluidHandler(inv.getStack()).ifPresent(handler -> {
         // if we successfully fill the handler, update the cauldron
         if (handler.fill(new FluidStack(fluid, FluidAttributes.BUCKET_VOLUME), FluidAction.EXECUTE) == FluidAttributes.BUCKET_VOLUME) {
           inv.setLevel(0);
           inv.setStack(handler.getContainer());
         }
       }));
  }

  @Override
  public ResourceLocation getId() {
    return id;
  }

  @Override
  public IRecipeSerializer<?> getSerializer() {
    return InspirationsRecipes.fillBucketSerializer;
  }

  /** @deprecated Use {@link #getCraftingResult(ICauldronInventory)} */
  @Deprecated
  @Override
  public ItemStack getRecipeOutput() {
    return new ItemStack(Items.WATER_BUCKET);
  }

  @Override
  public ItemStack getCraftingResult(ICauldronInventory inv) {
    // return filled bucket for the contained fluid, or empty bucket if invalid fluid
    return new ItemStack(inv.getContents()
                            .as(CauldronContentTypes.FLUID)
                            .map(ICauldronFluid::getFluid)
                            .map(Fluid::getFilledBucket)
                            .orElse(Items.BUCKET));
  }
}
