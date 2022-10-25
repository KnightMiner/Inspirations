package knightminer.inspirations.library.recipe;

import com.google.gson.JsonObject;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;

/**
 * Simple implementation of {@link IFinishedRecipe} to implement methods for a dynamic recipe. Can extended to add extra JSON data.
 * Use a traditional builder instead if you need advancements
 */
public class DynamicFinishedRecipe implements FinishedRecipe {
  private final ResourceLocation id;
  private final RecipeSerializer<?> serializer;
  public DynamicFinishedRecipe(ResourceLocation id, RecipeSerializer<?> serializer) {
    this.id = id;
    this.serializer = serializer;
  }

  @Override
  public void serializeRecipeData(JsonObject json) {}

  @Override
  public ResourceLocation getId() {
    return id;
  }

  @Override
  public RecipeSerializer<?> getType() {
    return serializer;
  }

  @Override
  @Nullable
  public JsonObject serializeAdvancement() {
    return null;
  }

  @Override
  @Nullable
  public ResourceLocation getAdvancementId() {
    return null;
  }
}
