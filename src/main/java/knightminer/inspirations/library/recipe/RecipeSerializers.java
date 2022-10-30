package knightminer.inspirations.library.recipe;

import knightminer.inspirations.Inspirations;
import knightminer.inspirations.library.recipe.cauldron.recipe.CauldronRecipe;
import knightminer.inspirations.library.recipe.cauldron.recipe.CauldronTransform;
import knightminer.inspirations.library.recipe.cauldron.special.EmptyPotionCauldronRecipe;
import knightminer.inspirations.library.recipe.cauldron.special.FillPotionCauldronRecipe;
import knightminer.inspirations.library.recipe.crafting.ShapelessNoContainerRecipe;
import knightminer.inspirations.recipes.recipe.cauldron.BrewingCauldronRecipe;
import knightminer.inspirations.recipes.recipe.cauldron.PotionFermentCauldronTransform;
import net.minecraftforge.registries.ObjectHolder;

import static slimeknights.mantle.registration.RegistrationHelper.injected;

/** Class containing all mod recipe serializers */
@ObjectHolder(Inspirations.modID)
public class RecipeSerializers {
  /*
   * Crafting Table
   */
  public static final ShapelessNoContainerRecipe.Serializer SHAPELESS_NO_CONTAINER = injected();

  /*
   * Cauldron
   */
  public static final CauldronRecipe.Serializer CAULDRON = injected();
  public static final CauldronTransform.Serializer CAULDRON_TRANSFORM = injected();
  // advanced recipes
  public static final EmptyPotionCauldronRecipe.Serializer CAULDRON_EMPTY_POTION = injected();
  public static final FillPotionCauldronRecipe.Serializer CAULDRON_FILL_POTION = injected();
  // special recipes
  public static final BrewingCauldronRecipe.Serializer CAULDRON_POTION_BREWING = injected();
  public static final BrewingCauldronRecipe.Serializer CAULDRON_FORGE_BREWING = injected();
  public static final PotionFermentCauldronTransform.Serializer CAULDRON_POTION_FERMENT = injected();
}
