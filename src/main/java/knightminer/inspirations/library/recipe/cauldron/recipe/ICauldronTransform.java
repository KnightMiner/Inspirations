package knightminer.inspirations.library.recipe.cauldron.recipe;

import knightminer.inspirations.library.recipe.RecipeTypes;
import knightminer.inspirations.library.recipe.cauldron.contents.ICauldronContents;
import knightminer.inspirations.library.recipe.cauldron.inventory.ICauldronState;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.SoundEvent;
import slimeknights.mantle.recipe.ICustomOutputRecipe;

/**
 * Recipe that transforms contents in a cauldron given time and temperature
 */
public interface ICauldronTransform extends ICustomOutputRecipe<ICauldronState> {
  /**
   * Gets the output contents for this recipe based on the input contents
   * @param inv  Cauldron state
   * @return  New contents
   */
  ICauldronContents getContentOutput(ICauldronState inv);

  /**
   * Gets the sound to play upon completion of the recipe
   * @return  Recipe sound
   */
  SoundEvent getSound();

  /**
   * Gets the recipe duration in ticks (1/20 of a second)
   * @return  Recipe duration
   */
  int getTime();

  @Override
  default IRecipeType<?> getType() {
    return RecipeTypes.CAULDRON_TRANSFORM;
  }
}
