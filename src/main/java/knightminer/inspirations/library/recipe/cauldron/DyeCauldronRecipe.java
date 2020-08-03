package knightminer.inspirations.library.recipe.cauldron;

import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import slimeknights.mantle.recipe.match.RecipeMatch;

/**
 * Cauldron recipe to dye an item stack from colored cauldron water
 */
@Deprecated
public class DyeCauldronRecipe extends RecipeMatchCauldronRecipe {

  private DyeColor color;

  /**
   * @param input  Input recipe match entry
   * @param color  Color to test for in the cauldron
   * @param result Resulting item stack
   */
  public DyeCauldronRecipe(RecipeMatch input, DyeColor color, ItemStack result, int levels) {
    super(input, result, null, levels);
    this.color = color;
  }

  public DyeCauldronRecipe(RecipeMatch input, DyeColor color, ItemStack result) {
    this(input, color, result, 1);
  }

  /**
   * @param input  Input item stack
   * @param color  Color to test for in the cauldron
   * @param result Resulting item stack
   */
  public DyeCauldronRecipe(ItemStack input, DyeColor color, ItemStack result) {
    this(RecipeMatch.of(input), color, result);
  }

  @Override
  protected boolean matches(CauldronState state) {
    return state.getColor() == color.colorValue;
  }

  @Override
  public Object getInputState() {
    return color;
  }

  @Override
  public String toString() {
    return String.format("DyeCauldronRecipe: %s dyed %s", getResult().toString(), color.getString());
  }
}
