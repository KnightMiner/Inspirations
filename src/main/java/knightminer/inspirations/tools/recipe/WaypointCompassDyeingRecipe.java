package knightminer.inspirations.tools.recipe;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import knightminer.inspirations.tools.InspirationsTools;
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

import javax.annotation.Nonnull;

public class WaypointCompassDyeingRecipe extends ShapelessOreRecipe {

	public WaypointCompassDyeingRecipe(ResourceLocation group, NonNullList<Ingredient> input, ItemStack result) {
		super(group, input, result);
	}

	@Override
	@Nonnull
	public ItemStack getCraftingResult(@Nonnull InventoryCrafting crafting){
		ItemStack output = this.output.copy();
		for (int i = 0; i < crafting.getSizeInventory(); i++) {
			ItemStack stack = crafting.getStackInSlot(i);
			if(stack.getItem() == InspirationsTools.waypointCompass) {
				if (stack.hasTagCompound()) {
					output.setTagCompound(stack.getTagCompound().copy());
				}
				break;
			}
		}
		return output;
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
			return new WaypointCompassDyeingRecipe(group.isEmpty() ? null : new ResourceLocation(group), ings, itemstack);
		}
	}
}
