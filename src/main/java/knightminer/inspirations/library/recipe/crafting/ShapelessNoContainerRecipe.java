package knightminer.inspirations.library.recipe.crafting;

import com.google.gson.JsonObject;
import knightminer.inspirations.library.recipe.RecipeSerializer;
import knightminer.inspirations.library.recipe.RecipeSerializers;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapelessRecipe;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;

/**
 * Shapeless recipe variant that suppresses container returns. Use {@link slimeknights.mantle.recipe.data.ConsumerWrapperBuilder} for data gen.
 */
public class ShapelessNoContainerRecipe extends ShapelessRecipe {
  /**
   * Creates a new recipe using all required properties
   * @param id      Recipe ID
   * @param group   Recipe group
   * @param result  Recipe result
   * @param inputs  Recipe inputs
   */
  public ShapelessNoContainerRecipe(ResourceLocation id, String group, ItemStack result, NonNullList<Ingredient> inputs) {
    super(id, group, result, inputs);
  }

  /**
   * Create a new recipe instance by copying an existing shapeless recipe
   * @param orig  Shapeless recipe to copy
   */
  private ShapelessNoContainerRecipe(ShapelessRecipe orig) {
    super(orig.getId(), orig.getGroup(), orig.getResultItem(), orig.getIngredients());
  }

  @Override
  public NonNullList<ItemStack> getRemainingItems(CraftingInventory inv) {
    return NonNullList.withSize(inv.getContainerSize(), ItemStack.EMPTY);
  }

  @Override
  public IRecipeSerializer<?> getSerializer() {
    return RecipeSerializers.SHAPELESS_NO_CONTAINER;
  }

  /**
   * Serializer to redirect to the shapeless serializer
   */
  public static class Serializer extends RecipeSerializer<ShapelessNoContainerRecipe> {
    @Nullable
    @Override
    public ShapelessNoContainerRecipe fromNetwork(ResourceLocation recipeID, PacketBuffer buffer) {
      ShapelessRecipe recipe = SHAPELESS_RECIPE.fromNetwork(recipeID, buffer);
      if (recipe != null) {
        return new ShapelessNoContainerRecipe(recipe);
      }
      return null;
    }

    @Override
    public ShapelessNoContainerRecipe fromJson(ResourceLocation recipeID, JsonObject json) {
      return new ShapelessNoContainerRecipe(SHAPELESS_RECIPE.fromJson(recipeID, json));
    }

    @Override
    public void toNetwork(PacketBuffer buffer, ShapelessNoContainerRecipe recipe) {
      Serializer.SHAPELESS_RECIPE.toNetwork(buffer, recipe);
    }
  }
}
