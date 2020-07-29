package knightminer.inspirations.library.recipe;

import com.google.gson.JsonObject;
import knightminer.inspirations.Inspirations;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapelessRecipe;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nullable;

public class ShapelessNoContainerRecipe extends ShapelessRecipe {

	public ShapelessNoContainerRecipe(ResourceLocation id, String group, ItemStack result, NonNullList<Ingredient> input) {
		super(id, group, result, input);
	}

	private ShapelessNoContainerRecipe(ShapelessRecipe orig) {
		super(orig.getId(), orig.getGroup(), orig.getRecipeOutput(), orig.getIngredients());
	}

	@Override
	public NonNullList<ItemStack> getRemainingItems(CraftingInventory inv) {
		return NonNullList.withSize(inv.getSizeInventory(), ItemStack.EMPTY);
	}

	@Override
	public IRecipeSerializer<?> getSerializer() {
		return SERIALIZER;
	}

	public static final IRecipeSerializer<?> SERIALIZER = new Serializer().setRegistryName(new ResourceLocation(Inspirations.modID, "shapeless_no_container"));

	// This recipe has the exact same options as the parent type, redirect to that code.
	private static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<ShapelessNoContainerRecipe> {
		@Nullable
		@Override
		public ShapelessNoContainerRecipe read(ResourceLocation recipeID, PacketBuffer buffer) {
			ShapelessRecipe recipe = CRAFTING_SHAPELESS.read(recipeID, buffer);
			if (recipe != null) {
				return new ShapelessNoContainerRecipe(recipe);
			}
			return null;
		}

		@Override
		public ShapelessNoContainerRecipe read(ResourceLocation recipeID, JsonObject json) {
			return new ShapelessNoContainerRecipe(CRAFTING_SHAPELESS.read(recipeID, json));
		}

		@Override
		public void write(PacketBuffer buffer, ShapelessNoContainerRecipe recipe) {
			Serializer.CRAFTING_SHAPELESS.write(buffer, recipe);
		}
	}
}
