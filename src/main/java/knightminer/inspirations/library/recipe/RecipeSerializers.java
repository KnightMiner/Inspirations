package knightminer.inspirations.library.recipe;

import knightminer.inspirations.Inspirations;
import knightminer.inspirations.library.recipe.crafting.ShapelessNoContainerRecipe;
import net.minecraftforge.registries.ObjectHolder;

import static slimeknights.mantle.registration.RegistrationHelper.injected;

/** Class containing all mod recipe serializers */
@ObjectHolder(Inspirations.modID)
public class RecipeSerializers {
  /*
   * Crafting Table
   */
  public static final ShapelessNoContainerRecipe.Serializer SHAPELESS_NO_CONTAINER = injected();
}
