package knightminer.inspirations.library.recipe.crafting;

import com.google.gson.JsonObject;
import knightminer.inspirations.library.recipe.RecipeSerializers;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.ShapedRecipeBuilder;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.ITag;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.util.function.Consumer;

public class TextureRecipeBuilder {
  private final ShapedRecipeBuilder parent;
  private Ingredient texture;
  private boolean matchFirst;

  private TextureRecipeBuilder(ShapedRecipeBuilder parent) {
    this.parent = parent;
  }

  /**
   * Creates a new builder instance based on the given builder
   * @param parent  Parent recipe builder
   * @return  Builder instance
   */
  public static TextureRecipeBuilder fromShaped(ShapedRecipeBuilder parent) {
    return new TextureRecipeBuilder(parent);
  }

  /**
   * Sets the texture source to the given ingredient
   * @param texture  Ingredient to use for texture
   * @return  Builder instance
   */
  public TextureRecipeBuilder setSource(Ingredient texture) {
    this.texture = texture;
    return this;
  }

  /**
   * Sets the texture source to the given tag
   * @param tag  Tag to use for texture
   * @return  Builder instance
   */
  public TextureRecipeBuilder setSource(ITag<Item> tag) {
    this.texture = Ingredient.fromTag(tag);
    return this;
  }

  /**
   * Sets the match first property on the recipe.
   * If set, the recipe uses the first ingredient match for the texture. If unset, all items that match the ingredient must be the same or no texture is applied
   * @return  Builder instance
   */
  public TextureRecipeBuilder setMatchFirst() {
    this.matchFirst = true;
    return this;
  }

  /**
   * Builds the recipe with the default name using the given consumer
   * @param consumer  Recipe consumer
   */
  public void build(Consumer<IFinishedRecipe> consumer) {
    this.validate();
    parent.build(base -> consumer.accept(new Result(base, texture, matchFirst)));
  }

  /**
   * Builds the recipe using the given consumer
   * @param consumer  Recipe consumer
   * @param location  Recipe location
   */
  public void build(Consumer<IFinishedRecipe> consumer, ResourceLocation location) {
    this.validate();
    parent.build(base -> consumer.accept(new Result(base, texture, matchFirst)), location);
  }

  /**
   * Ensures this recipe can be built
   * @throws IllegalStateException If the recipe cannot be built
   */
  private void validate() {
    if (texture == null) {
      throw new IllegalStateException("No texture defined for texture recipe");
    }
  }

  private static class Result implements IFinishedRecipe {
    private final IFinishedRecipe base;
    private final Ingredient texture;
    private final boolean matchFirst;

    private Result(IFinishedRecipe base, Ingredient texture, boolean matchFirst) {
      this.base = base;
      this.texture = texture;
      this.matchFirst = matchFirst;
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
      return RecipeSerializers.TEXTURE_RECIPE;
    }

    @Override
    public ResourceLocation getID() {
      return base.getID();
    }

    @Override
    public void serialize(JsonObject json) {
      base.serialize(json);
      json.add("texture", texture.serialize());
      json.addProperty("match_first", matchFirst);
    }

    @Nullable
    @Override
    public JsonObject getAdvancementJson() {
      return base.getAdvancementJson();
    }

    @Nullable
    @Override
    public ResourceLocation getAdvancementID() {
      return base.getAdvancementID();
    }
  }
}
