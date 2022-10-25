package knightminer.inspirations.library.recipe.crafting;

import com.google.gson.JsonObject;
import knightminer.inspirations.library.recipe.RecipeSerializers;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import slimeknights.mantle.recipe.helper.AbstractRecipeSerializer;

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
  public NonNullList<ItemStack> getRemainingItems(CraftingContainer inv) {
    return NonNullList.withSize(inv.getContainerSize(), ItemStack.EMPTY);
  }

  @Override
  public RecipeSerializer<?> getSerializer() {
    return RecipeSerializers.SHAPELESS_NO_CONTAINER;
  }

  /**
   * Serializer to redirect to the shapeless serializer
   */
  public static class Serializer extends AbstractRecipeSerializer<ShapelessNoContainerRecipe> {
    @Nullable
    @Override
    public ShapelessNoContainerRecipe fromNetwork(ResourceLocation recipeID, FriendlyByteBuf buffer) {
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
    public void toNetwork(FriendlyByteBuf buffer, ShapelessNoContainerRecipe recipe) {
      Serializer.SHAPELESS_RECIPE.toNetwork(buffer, recipe);
    }
  }
}
