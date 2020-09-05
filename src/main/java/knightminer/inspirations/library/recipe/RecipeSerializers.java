package knightminer.inspirations.library.recipe;

import knightminer.inspirations.Inspirations;
import knightminer.inspirations.library.recipe.cauldron.recipe.CauldronRecipe;
import knightminer.inspirations.library.recipe.cauldron.recipe.CauldronTransform;
import knightminer.inspirations.library.recipe.cauldron.special.DyeableCauldronRecipe;
import knightminer.inspirations.library.recipe.cauldron.special.EmptyPotionCauldronRecipe;
import knightminer.inspirations.library.recipe.cauldron.special.FillPotionCauldronRecipe;
import knightminer.inspirations.library.recipe.crafting.ShapelessNoContainerRecipe;
import knightminer.inspirations.recipes.recipe.cauldron.DyeCauldronWaterRecipe;
import knightminer.inspirations.recipes.recipe.cauldron.EmptyBucketCauldronRecipe;
import knightminer.inspirations.recipes.recipe.cauldron.FillBucketCauldronRecipe;
import knightminer.inspirations.recipes.recipe.cauldron.FillDyedBottleRecipe;
import knightminer.inspirations.recipes.recipe.cauldron.MixCauldronDyeRecipe;
import knightminer.inspirations.recipes.recipe.cauldron.RemoveBannerPatternCauldronRecipe;
import knightminer.inspirations.tools.recipe.CopyWaypointCompassRecipe;
import knightminer.inspirations.tools.recipe.DyeWaypointCompassRecipe;
import net.minecraft.item.crafting.SpecialRecipeSerializer;
import net.minecraftforge.registries.ObjectHolder;

import static slimeknights.mantle.registration.RegistrationHelper.injected;

/** Class containing all mod recipe serializers */
@ObjectHolder(Inspirations.modID)
public class RecipeSerializers {
  /*
   * Crafting Table
   */
  public static final SpecialRecipeSerializer<CopyWaypointCompassRecipe> COPY_WAYPOINT_COMPASS = injected();
  public static final DyeWaypointCompassRecipe.Serializer DYE_WAYPOINT_COMPASS = injected();
  public static final ShapelessNoContainerRecipe.Serializer SHAPELESS_NO_CONTAINER = injected();

  /*
   * Cauldron
   */
  public static final CauldronRecipe.Serializer CAULDRON = injected();
  public static final CauldronTransform.Serializer CAULDRON_TRANSFORM = injected();
  // advanced recipes
  public static final EmptyPotionCauldronRecipe.Serializer CAULDRON_EMPTY_POTION = injected();
  public static final FillPotionCauldronRecipe.Serializer CAULDRON_FILL_POTION = injected();
  public static final DyeCauldronWaterRecipe.Serializer CAULDRON_DYE_WATER = injected();
  public static final MixCauldronDyeRecipe.Serializer CAULDRON_MIX_DYE = injected();
  public static final DyeableCauldronRecipe.Serializer CAULDRON_DYE_DYEABLE = injected();
  public static final DyeableCauldronRecipe.Serializer CAULDRON_CLEAR_DYEABLE = injected();
  // special recipes
  public static final SpecialRecipeSerializer<EmptyBucketCauldronRecipe> CAULDRON_EMPTY_BUCKET = injected();
  public static final SpecialRecipeSerializer<FillBucketCauldronRecipe> CAULDRON_FILL_BUCKET = injected();
  public static final SpecialRecipeSerializer<FillDyedBottleRecipe> CAULDRON_FILL_DYED_BOTTLE = injected();
  public static final SpecialRecipeSerializer<RemoveBannerPatternCauldronRecipe> CAULDRON_REMOVE_BANNER_PATTERN = injected();
}
