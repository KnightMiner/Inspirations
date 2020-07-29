package knightminer.inspirations.library.recipe;

import com.google.gson.JsonObject;
import knightminer.inspirations.Inspirations;
import knightminer.inspirations.library.util.TagUtil;
import knightminer.inspirations.library.util.TextureBlockUtil;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;

import javax.annotation.Nullable;

public class TextureRecipe extends ShapedRecipe {

	public final Ingredient texture; // first one found of these determines the output block used
	private final boolean matchFirst;

	/**
	 * Creates a new recipe using an existing shaped recipe
	 * @param orig        Shaped recipe to copy
	 * @param texture     Ingredient to use for the texture
	 * @param matchFirst  If true, the first ingredient match
	 */
	protected TextureRecipe(ShapedRecipe orig, Ingredient texture, boolean matchFirst) {
		super(orig.getId(), orig.getGroup(), orig.getWidth(), orig.getHeight(), orig.getIngredients(), orig.getRecipeOutput());
		this.texture = texture;
		this.matchFirst = matchFirst;
	}

	@Override
	public ItemStack getCraftingResult(CraftingInventory craftMatrix) {
		ItemStack result = super.getCraftingResult(craftMatrix);
		Block texBlock = null;
		for(int i = 0; i < craftMatrix.getSizeInventory(); i++) {
			ItemStack stack = craftMatrix.getStackInSlot(i);
			if(!stack.isEmpty() && texture.test(stack)) {
				Block block;
				// special case for enlightened bushes: get the stored texture rather than the bush itself
				if (stack.getItem() == result.getItem()) {
					block = TextureBlockUtil.getTextureBlock(stack);
				} else {
					 block = Block.getBlockFromItem(stack.getItem());
				}
				// if no texture, skip
				if (block == Blocks.AIR) {
					continue;
				}

				// if we have not found one yet, store it
				if(texBlock == null) {
					texBlock = block;
					if (matchFirst) {
						break;
					}

					// if we found one, ensure it matches
				} else if (texBlock != block) {
					texBlock = null;
					break;
				}
			}
		}

		if (texBlock != null) {
			return TextureBlockUtil.setStackTexture(result, texBlock);
		}
		return result;
	}

	public static final IRecipeSerializer<?> SERIALIZER = new TextureRecipe.Serializer().setRegistryName(new ResourceLocation(Inspirations.modID, "texture_recipe"));

	private static class Serializer extends net.minecraftforge.registries.ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<TextureRecipe> {

		@Override
		public TextureRecipe read(ResourceLocation recipeId, JsonObject json) {
			ShapedRecipe recipe = CRAFTING_SHAPED.read(recipeId, json);

			Ingredient texture = CraftingHelper.getIngredient(TagUtil.getElement(json, "texture"));
			boolean matchFirst = false;
			if (json.has("match_first")) {
				matchFirst = json.get("match_first").getAsBoolean();
			};

			return new TextureRecipe(recipe, texture, matchFirst);
		}

		@Nullable
		@Override
		public TextureRecipe read(ResourceLocation recipeId, PacketBuffer buffer) {
			ShapedRecipe recipe = CRAFTING_SHAPED.read(recipeId, buffer);
			return recipe == null ? null : new TextureRecipe(recipe, Ingredient.read(buffer), buffer.readBoolean());
		}

		@Override
		public void write(PacketBuffer buffer, TextureRecipe recipe) {
			CRAFTING_SHAPED.write(buffer, recipe);
			recipe.texture.write(buffer);
			buffer.writeBoolean(recipe.matchFirst);
		}
	}
}
