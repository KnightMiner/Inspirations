package knightminer.inspirations.tools.recipe;

import com.google.gson.JsonObject;
import knightminer.inspirations.library.recipe.RecipeSerializers;
import knightminer.inspirations.tools.item.WaypointCompassItem;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapelessRecipe;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;

public class DyeWaypointCompassRecipe extends ShapelessRecipe {

	public DyeWaypointCompassRecipe(ResourceLocation id, String group, ItemStack output, NonNullList<Ingredient> inputs) {
		super(id, group, output, inputs);
	}

	public DyeWaypointCompassRecipe(ShapelessRecipe recipe) {
		super(recipe.getId(), recipe.getGroup(),
			recipe.getRecipeOutput(),
			recipe.getIngredients()
		);
	}

	@Override
	public ItemStack getCraftingResult(CraftingInventory crafting){
		ItemStack output = this.getRecipeOutput().copy();
		for (int i = 0; i < crafting.getSizeInventory(); i++) {
			ItemStack stack = crafting.getStackInSlot(i);
			if(stack.getItem() instanceof WaypointCompassItem) {
				if (stack.hasTag()) {
					output.setTag(stack.getOrCreateTag().copy());
				}
				break;
			}
		}
		return output;
	}

	@Override
	public IRecipeSerializer<?> getSerializer() {
		return RecipeSerializers.dye_waypoint_compass;
	}

	public static class Serializer extends ShapelessRecipe.Serializer {
		// This recipe has the exact same options as the parent type, redirect to that code.
		@Nullable
		@Override
		public DyeWaypointCompassRecipe read(ResourceLocation recipeID, PacketBuffer buffer) {
			ShapelessRecipe recipe = CRAFTING_SHAPELESS.read(recipeID, buffer);
			return recipe == null ? null : new DyeWaypointCompassRecipe(recipe);
		}

		@Override
		public DyeWaypointCompassRecipe read(ResourceLocation recipeID, JsonObject json) {
			return new DyeWaypointCompassRecipe(CRAFTING_SHAPELESS.read(recipeID, json));
		}

		@Override
		public void write(PacketBuffer buffer, ShapelessRecipe recipe) {
			Serializer.CRAFTING_SHAPELESS.write(buffer, recipe);
		}
	}
}
