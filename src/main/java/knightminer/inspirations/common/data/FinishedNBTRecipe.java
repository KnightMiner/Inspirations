package knightminer.inspirations.common.data;

import com.google.gson.JsonObject;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import org.jetbrains.annotations.Nullable;
import slimeknights.mantle.data.loadable.common.NBTLoadable;

import java.util.function.Consumer;

/** Finished recipe that adds NBT */
public record FinishedNBTRecipe(FinishedRecipe recipe, CompoundTag tag) implements FinishedRecipe {
  @Override
  public void serializeRecipeData(JsonObject json) {
    recipe.serializeRecipeData(json);
    json.getAsJsonObject("result").add("nbt", NBTLoadable.ALLOW_STRING.serialize(tag));
  }

  @Override
  public ResourceLocation getId() {
    return recipe.getId();
  }

  @Override
  public RecipeSerializer<?> getType() {
    return recipe.getType();
  }

  @Nullable
  @Override
  public JsonObject serializeAdvancement() {
    return recipe.serializeAdvancement();
  }

  @Nullable
  @Override
  public ResourceLocation getAdvancementId() {
    return recipe.getAdvancementId();
  }

  /** Creates a consumer with NBT */
  public static Consumer<FinishedRecipe> withNBT(Consumer<FinishedRecipe> consumer, CompoundTag nbt) {
    return recipe -> consumer.accept(new FinishedNBTRecipe(recipe, nbt));
  }
}
