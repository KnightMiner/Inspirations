package knightminer.inspirations.tools.recipe;

import com.google.gson.JsonObject;
import knightminer.inspirations.tools.item.ItemWaypointCompass;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapelessRecipe;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;

public class WaypointCompassDyeingRecipe extends ShapelessRecipe {

	public WaypointCompassDyeingRecipe(ResourceLocation id, String group, ItemStack output, NonNullList<Ingredient> inputs) {
		super(id, group, output, inputs);
	}

	public WaypointCompassDyeingRecipe(ShapelessRecipe recipe) {
		super(recipe.getId(), recipe.getGroup(),
			recipe.getRecipeOutput(),
			recipe.getIngredients()
		);
	}

	@Override
	@Nonnull
	public ItemStack getCraftingResult(@Nonnull CraftingInventory crafting){
		ItemStack output = this.getRecipeOutput().copy();
		for (int i = 0; i < crafting.getSizeInventory(); i++) {
			ItemStack stack = crafting.getStackInSlot(i);
			if(stack.getItem() instanceof ItemWaypointCompass) {
				if (stack.hasTag()) {
					output.setTag(stack.getOrCreateTag().copy());
				}
				break;
			}
		}
		return output;
	}

	public static Serializer SERIALIZER = new Serializer();

	public static class Serializer extends ShapelessRecipe.Serializer {
		// This recipe has the exact same options as the parent type, redirect to that code.
		@Nonnull
		@Override
		public WaypointCompassDyeingRecipe read(@Nonnull ResourceLocation recipeID, PacketBuffer buffer) {
			return new WaypointCompassDyeingRecipe(CRAFTING_SHAPELESS.read(recipeID, buffer));
		}

		@Nonnull
		@Override
		public WaypointCompassDyeingRecipe read(@Nonnull ResourceLocation recipeID, JsonObject json) {
			return new WaypointCompassDyeingRecipe(CRAFTING_SHAPELESS.read(recipeID, json));
		}

		@Override
		public void write(PacketBuffer buffer, ShapelessRecipe recipe) {
			Serializer.CRAFTING_SHAPELESS.write(buffer, recipe);
		}
	}
}
