package knightminer.inspirations.library.recipe;

import com.google.gson.JsonObject;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;

/**
 * Simple implementation of {@link IFinishedRecipe} to implement methods for a dynamic recipe. Can extended to add extra JSON data.
 * Use a traditional builder instead if you need advancements
 */
public class DynamicFinishedRecipe implements IFinishedRecipe {
  private final ResourceLocation id;
  private final IRecipeSerializer<?> serializer;
  public DynamicFinishedRecipe(ResourceLocation id, IRecipeSerializer<?> serializer) {
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
  public IRecipeSerializer<?> getType() {
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
