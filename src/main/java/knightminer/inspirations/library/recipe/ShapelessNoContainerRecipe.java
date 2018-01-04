package knightminer.inspirations.library.recipe;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.IRecipeFactory;
import net.minecraftforge.common.crafting.JsonContext;
import net.minecraftforge.oredict.ShapelessOreRecipe;

public class ShapelessNoContainerRecipe extends ShapelessOreRecipe {

	public ShapelessNoContainerRecipe(ResourceLocation group, NonNullList<Ingredient> input, ItemStack result) {
		super(group, input, result);
	}

	@Override
	public NonNullList<ItemStack> getRemainingItems(InventoryCrafting inv) {
		return NonNullList.withSize(inv.getSizeInventory(), ItemStack.EMPTY);
	}

	public static class Factory implements IRecipeFactory {
		@Override
		public IRecipe parse(JsonContext context, JsonObject json) {
			String group = JsonUtils.getString(json, "group", "");

			NonNullList<Ingredient> ings = NonNullList.create();
			for (JsonElement ele : JsonUtils.getJsonArray(json, "ingredients")) {
				ings.add(CraftingHelper.getIngredient(ele, context));
			}

			if (ings.isEmpty()) {
				throw new JsonParseException("No ingredients for shapeless recipe");
			}

			ItemStack itemstack = CraftingHelper.getItemStack(JsonUtils.getJsonObject(json, "result"), context);
			return new ShapelessNoContainerRecipe(group.isEmpty() ? null : new ResourceLocation(group), ings, itemstack);
		}
	}
}
