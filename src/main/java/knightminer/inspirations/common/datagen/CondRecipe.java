package knightminer.inspirations.common.datagen;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import knightminer.inspirations.library.Util;
import knightminer.inspirations.library.recipe.TextureRecipe;
import net.minecraft.data.CustomRecipeBuilder;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.ShapedRecipeBuilder;
import net.minecraft.data.ShapelessRecipeBuilder;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.item.crafting.SpecialRecipeSerializer;
import net.minecraft.tags.Tag;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.conditions.ICondition;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Builder for creating recipes which are conditionally enabled.
 */
public class CondRecipe {
	public static ShapedBuilder shaped(IItemProvider result) {
		return new ShapedBuilder(result, 1);
	}
	public static ShapedBuilder shaped(IItemProvider result, int count) {
		return new ShapedBuilder(result, count);
	}
	public static ShapelessBuilder shapeless(IItemProvider result) {
		return new ShapelessBuilder(result, 1);
	}
	public static ShapelessBuilder shapeless(IItemProvider result, int count) {
		return new ShapelessBuilder(result, count);
	}
	public static CustomBuilder custom(SpecialRecipeSerializer<?> serializer) {
		return new CustomBuilder(serializer);
	}

	/**
	 * Recipe wrapper which adds conditions when serialized.
	 */
	private static class Finished implements IFinishedRecipe {
		private final IFinishedRecipe recipe;
		private final List<ICondition> condList;
		private final ResourceLocation id;
		@Nullable
		private IRecipeSerializer<?> custSerial;

		private Finished(ResourceLocation id, IFinishedRecipe recipe, @Nullable IRecipeSerializer<?> custSerial, List<ICondition> cond) {
			this.recipe = recipe;
			this.condList = cond;
			this.id = id;
			this.custSerial = custSerial;
		}

		@Nonnull
		@Override
		public JsonObject getRecipeJson() {
			JsonObject json = new JsonObject();
			serialize(json);
			json.addProperty("type", Registry.RECIPE_SERIALIZER.getKey(getSerializer()).toString());
			return json;
		}

		@Override
		public void serialize(@Nonnull JsonObject json) {
			JsonArray jsonCond = new JsonArray();
			for(ICondition cond : condList) {
				jsonCond.add(CraftingHelper.serialize(cond));
			}
			json.add("conditions", jsonCond);
			recipe.serialize(json);
			if (custSerial != null) {
				json.addProperty("type", Registry.RECIPE_SERIALIZER.getKey(getSerializer()).toString());
			}
		}

		@Nonnull
		@Override
		public ResourceLocation getID() {
			return id;
		}

		@Nonnull
		@Override
		public IRecipeSerializer<?> getSerializer() {
			if (custSerial != null) {
				return custSerial;
			}
			return recipe.getSerializer();
		}

		@Nullable
		@Override
		public JsonObject getAdvancementJson() {
			return recipe.getAdvancementJson();
		}

		@Nullable
		@Override
		public ResourceLocation getAdvancementID() {
			return recipe.getAdvancementID();
		}
	}

	/**
	 * Only applicable to shaped crafting, modifies it into a
	 * retexturing recipe.
	 */
	private static class FinishedTexture extends Finished {
		private final Ingredient texSource;
		private final boolean matchFirst;

		private FinishedTexture(ResourceLocation id, IFinishedRecipe recipe, Ingredient texSource, boolean matchFirst, List<ICondition> cond) {
			super(id, recipe, TextureRecipe.SERIALIZER, cond);
			assert recipe.getSerializer() == ShapedRecipe.Serializer.CRAFTING_SHAPED;
			this.texSource = texSource;
			this.matchFirst = matchFirst;
		}

		@Nonnull
		@Override
		public JsonObject getRecipeJson() {
			JsonObject json = super.getRecipeJson();
			json.add("texture", texSource.serialize());
			json.addProperty("match_first", matchFirst);
			return json;
		}
	}

	public static class ShapedBuilder extends ShapedRecipeBuilder {
		private ArrayList<ICondition> conditions;
		@Nullable
		private IRecipeSerializer<?> custSerial;
		@Nullable
		private Ingredient textureSource;
		private boolean textureMatchFirst;

		private ShapedBuilder(IItemProvider result, int count) {
			super(result, count);
			conditions = new ArrayList<>();
			textureSource = null;
			textureMatchFirst = false;
		}

		public ShapedBuilder addCondition(ICondition cond) {
			conditions.add(cond);
			return this;
		}

		public ShapedBuilder custom(IRecipeSerializer<?> serializer) {
			custSerial = serializer;
			return this;
		}

		public ShapedBuilder textureSource(Ingredient ingredient) {
			textureSource = ingredient;
			return this;
		}

		public ShapedBuilder textureSource(IItemProvider item) {
			return textureSource(Ingredient.fromItems(item));
		}

		public ShapedBuilder textureSource(Tag<Item> tag) {
			return textureSource(Ingredient.fromTag(tag));
		}

		public ShapedBuilder textureMatchFirst() {
			textureMatchFirst = true;
			return this;
		}

		@Override
		public void build(Consumer<IFinishedRecipe> consumer, ResourceLocation recipeLoc) {
			// Capture the finished recipe in the consumer, then wrap in our own class.
			super.build((result) -> consumer.accept(textureSource != null ?
					new FinishedTexture(recipeLoc, result, textureSource, textureMatchFirst, conditions) :
					new Finished(recipeLoc, result, custSerial, conditions)
			), recipeLoc);
		}

		@Override
		public void build(@Nonnull Consumer<IFinishedRecipe> consumer, String path) {
			build(consumer, Util.getResource(path));
		}

		@Override
		public void build(@Nonnull Consumer<IFinishedRecipe> consumer) {
			// Capture the finished recipe in the consumer, then wrap in our own class.
			super.build((result) -> consumer.accept(textureSource != null ?
					new FinishedTexture(result.getID(), result, textureSource, textureMatchFirst, conditions) :
					new Finished(result.getID(), result, custSerial, conditions)
			));
		}
	}

	public static class ShapelessBuilder extends ShapelessRecipeBuilder {
		private ArrayList<ICondition> conditions;
		@Nullable
		private IRecipeSerializer<?> custSerial;

		private ShapelessBuilder(IItemProvider result, int count) {
			super(result, count);
			conditions = new ArrayList<>();
		}

		public ShapelessBuilder addCondition(ICondition cond) {
			conditions.add(cond);
			return this;
		}

		public ShapelessBuilder custom(IRecipeSerializer<?> serializer) {
			custSerial = serializer;
			return this;
		}

		@Override
		public void build(Consumer<IFinishedRecipe> consumer, ResourceLocation recipeLoc) {
			// Capture the finished recipe in the consumer, then wrap in our own class.
			super.build((result) -> consumer.accept(
					new Finished(recipeLoc, result, custSerial, conditions)
			), recipeLoc);
		}

		@Override
		public void build(@Nonnull Consumer<IFinishedRecipe> consumer, String path) {
			build(consumer, Util.getResource(path));
		}
	}

	public static class CustomBuilder {
		private final SpecialRecipeSerializer<?> serializer;
		private ArrayList<ICondition> conditions;

		private CustomBuilder(@Nonnull SpecialRecipeSerializer<?> serializer) {
			this.serializer = serializer;
			conditions = new ArrayList<>();
		}

		public CustomBuilder addCondition(ICondition cond) {
			conditions.add(cond);
			return this;
		}

		public void build(@Nonnull Consumer<IFinishedRecipe> consumer, ResourceLocation path) {
			// Reuse vanilla's builder - which just writes "type".
			CustomRecipeBuilder builder = CustomRecipeBuilder.customRecipe(serializer);

			// Capture the finished recipe in the consumer, then wrap in our own class.
			builder.build((result) ->
					consumer.accept(new Finished(path, result, serializer, conditions)),
					path.toString()
			);
		}

		public void build(@Nonnull Consumer<IFinishedRecipe> consumer, String path) {
			build(consumer, Util.getResource(path));
		}

		public void build(@Nonnull Consumer<IFinishedRecipe> consumer) {
			build(consumer, serializer.getRegistryName());
		}
	}
}
