package knightminer.inspirations.library.recipe;

import javax.annotation.Nonnull;

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
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;

public class TextureRecipe extends ShapedRecipe {

	public final Ingredient texture; // first one found of these determines the output block used

	public TextureRecipe(ResourceLocation id, String group, int width, int height, Ingredient texture, NonNullList<Ingredient> inputs, ItemStack output) {
		super(id, group, width, height, inputs, output);
		this.texture = texture;
	}

	private TextureRecipe(ShapedRecipe orig, Ingredient texture) {
		super(orig.getId(), orig.getGroup(), orig.getWidth(), orig.getHeight(), orig.getIngredients(), orig.getRecipeOutput());
		this.texture = texture;
	}

	@Nonnull
	@Override
	public ItemStack getCraftingResult(CraftingInventory craftMatrix) {
		ItemStack result = super.getCraftingResult(craftMatrix);
		for(int i = 0; i < craftMatrix.getSizeInventory(); i++) {
			for(ItemStack potential : texture.getMatchingStacks()) {
				ItemStack stack = craftMatrix.getStackInSlot(i);
				if(potential.isItemEqual(stack) && Block.getBlockFromItem(stack.getItem()) != Blocks.AIR) {
					Block outBlock = Block.getBlockFromItem(result.getItem());
					return TextureBlockUtil.createTexturedStack(outBlock, Block.getBlockFromItem(stack.getItem()));
				}
			}
		}

		return result;
	}

	@Nonnull
	@Override
	public ItemStack getRecipeOutput() {
		ItemStack output = super.getRecipeOutput();
		if(!(texture.getMatchingStacks().length == 0) && !output.isEmpty()) {
			ItemStack stack = texture.getMatchingStacks()[0];
			Block block = Block.getBlockFromItem(output.getItem());
			return TextureBlockUtil.createTexturedStack(block, Block.getBlockFromItem(stack.getItem()));
		}

		return super.getRecipeOutput();
	}

	public static final IRecipeSerializer<?> SERIALIZER = new TextureRecipe.Serializer().setRegistryName(new ResourceLocation(Inspirations.modID, "shaped_texturing"));

	private static class Serializer extends net.minecraftforge.registries.ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<TextureRecipe> {

		@Override
		public TextureRecipe read(ResourceLocation recipeId, JsonObject json) {
			ShapedRecipe recipe = CRAFTING_SHAPED.read(recipeId, json);

			Ingredient texture = CraftingHelper.getIngredient(TagUtil.getElement(json, "texture"));

			return new TextureRecipe(recipe, texture);
		}

		@Override
		public TextureRecipe read(ResourceLocation recipeId, PacketBuffer buffer) {
			ShapedRecipe recipe = CRAFTING_SHAPED.read(recipeId, buffer);
			return new TextureRecipe(recipe, Ingredient.read(buffer));
		}

		@Override
		public void write(PacketBuffer buffer, TextureRecipe recipe) {
			CRAFTING_SHAPED.write(buffer, recipe);
			recipe.texture.write(buffer);
		}
	}
}
