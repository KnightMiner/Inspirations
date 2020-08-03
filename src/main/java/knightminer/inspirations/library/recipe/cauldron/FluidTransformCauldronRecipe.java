package knightminer.inspirations.library.recipe.cauldron;

import knightminer.inspirations.common.Config;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemStack;
import slimeknights.mantle.recipe.match.RecipeMatch;

import javax.annotation.Nullable;

/**
 * Cauldron recipe to transform the fluid inside a cauldron into another fluid. Use primarily for soups, stews, and juices
 */
@Deprecated
public class FluidTransformCauldronRecipe extends FluidCauldronRecipe {

  private CauldronState result;
  private int maxLevel;

  /**
   * Constructor with default max level of 3
   * @param input   Input item stack for transformation
   * @param fluid   Input fluid. Use EMPTY for any "water"
   * @param result  Resulting fluid
   * @param boiling If true, cauldron must be above fire. If false, cauldron must not be above fire. Use null to ignore fire
   */
  public FluidTransformCauldronRecipe(RecipeMatch input, Fluid fluid, Fluid result, @Nullable Boolean boiling) {
    this(input, fluid, result, boiling, Config.getCauldronMax());
  }

  /**
   * Full constructor
   * @param input    Input item stack for transformation
   * @param fluid    Input fluid. Use EMPTY for any "water"
   * @param result   Resulting fluid
   * @param boiling  If true, cauldron must be above fire. If false, cauldron must not be above fire. Use null to ignore fire
   * @param maxLevel Maximum level at which this recipe works. Used for some recipes which cost more for more full cauldrons
   */
  public FluidTransformCauldronRecipe(RecipeMatch input, Fluid fluid, Fluid result, @Nullable Boolean boiling, int maxLevel) {
    super(input, fluid, ItemStack.EMPTY, boiling);
    this.result = CauldronState.fluid(result);
    this.maxLevel = maxLevel;
  }

  @Override
  public boolean matches(ItemStack stack, boolean boiling, int level, CauldronState state) {
    return level > 0 && level <= maxLevel && super.matches(stack, boiling, level, state);
  }

  @Override
  public int getLevel(int level) {
    return level;
  }

  @Override
  public CauldronState getState(ItemStack stack, boolean boiling, int level, CauldronState state) {
    return result;
  }

  @Override
  public Object getState() {
    return result.getFluid();
  }

  @Override
  public int getInputLevel() {
    return maxLevel;
  }

  @Override
  public String toString() {
    assert fluid != null;
    return String.format(
        "FluidTransformCauldronRecipe: %s from %s",
        result.getFluid().getRegistryName(),
        fluid == CauldronState.WATER ? "water" : fluid.getFluid().getRegistryName());
  }
}
