package knightminer.inspirations.library.recipe.cauldron.recipe;

import knightminer.inspirations.library.recipe.cauldron.contents.ICauldronContents;
import knightminer.inspirations.library.recipe.cauldron.util.TemperaturePredicate;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import java.util.Collections;
import java.util.List;

/**
 * Interface for recipes to display in JEI
 * @deprecated Needs rewrite to work with the new cauldron behavior
 */
@Deprecated
public interface ICauldronRecipeDisplay {
  /* Inputs */

  /**
   * Gets options for the item stack recipe input
   * @return  Options for item recipe input
   */
  List<ItemStack> getItemInputs();

  /**
   * Gets options for the cauldron contents input
   * @return  Options for item recipe input
   */
  List<ICauldronContents> getContentInputs();

  /**
   * Gets options for fluid inputs
   * @return  Fluid input options
   */
  default List<FluidStack> getFluidInputs() {
    return Collections.emptyList();
  }

  /**
   * Gets the number of levels to display in input
   * @return  Input levels
   */
  int getLevelInput();

  /**
   * Gets the temperature required for input
   * @return  Input temperature
   */
  default TemperaturePredicate getTemperature() {
    return TemperaturePredicate.ANY;
  }

  /**
   * Gets the duration of this recipe. Return -1 for no duration
   * @return  Recipe duration
   */
  default int getTime() {
    return -1;
  }


  /* Output */

  /**
   * Gets the number of levels to display on the output
   * @return  Output levels
   */
  int getLevelOutput();

  /**
   * Gets the item stack output of the recipe
   * @return  Item stack output
   */
  ItemStack getItemOutput();

  /**
   * Gets the cauldron contents output of the recipe
   * @return  Cauldron contents output
   */
  ICauldronContents getContentOutput();

  /**
   * Gets the cauldron contents output of the recipe
   * @return  Cauldron contents output
   */
  default FluidStack getFluidOutput() {
    return FluidStack.EMPTY;
  }

  /**
   * Method to override for recipes that are simple, but have a non-simple variant
   * @return  True if the recipe is simple and should be shown
   */
  default boolean isSimple() {
    return true;
  }
}
