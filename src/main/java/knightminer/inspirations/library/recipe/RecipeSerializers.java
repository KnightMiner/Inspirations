package knightminer.inspirations.library.recipe;

import knightminer.inspirations.Inspirations;
import knightminer.inspirations.library.recipe.crafting.ShapelessNoContainerRecipe;
import knightminer.inspirations.tools.recipe.CopyWaypointCompassRecipe;
import knightminer.inspirations.tools.recipe.DyeWaypointCompassRecipe;
import net.minecraft.item.crafting.SpecialRecipeSerializer;
import net.minecraftforge.registries.ObjectHolder;

import static slimeknights.mantle.registration.RegistrationHelper.injected;

@ObjectHolder(Inspirations.modID)
public class RecipeSerializers {
  public static final SpecialRecipeSerializer<CopyWaypointCompassRecipe> COPY_WAYPOINT_COMPASS = injected();
  public static final DyeWaypointCompassRecipe.Serializer DYE_WAYPOINT_COMPASS = injected();
  public static final ShapelessNoContainerRecipe.Serializer SHAPELESS_NO_CONTAINER = injected();
}
