package knightminer.inspirations.library.recipe;

import knightminer.inspirations.Inspirations;
import knightminer.inspirations.tools.recipe.CopyWaypointCompassRecipe;
import knightminer.inspirations.tools.recipe.DyeWaypointCompassRecipe;
import net.minecraft.item.crafting.SpecialRecipeSerializer;
import net.minecraftforge.registries.ObjectHolder;

import javax.annotation.Nonnull;

@ObjectHolder(Inspirations.modID)
public class RecipeSerializers {
  public static final SpecialRecipeSerializer<CopyWaypointCompassRecipe> copy_waypoint_compass = injected();
  public static final DyeWaypointCompassRecipe.Serializer dye_waypoint_compass = injected();

  @SuppressWarnings("ConstantConditions")
  @Nonnull
  private static <T> T injected() {
    return null;
  }
}
