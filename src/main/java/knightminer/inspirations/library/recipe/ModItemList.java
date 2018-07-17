package knightminer.inspirations.library.recipe;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.JsonUtils;
import net.minecraftforge.common.crafting.CompoundIngredient;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.IIngredientFactory;
import net.minecraftforge.common.crafting.JsonContext;
import net.minecraftforge.fml.common.Loader;

public class ModItemList extends CompoundIngredient {

	protected ModItemList(Collection<Ingredient> children) {
		super(children);
	}

	public static class Factory implements IIngredientFactory {
		@Override
		public Ingredient parse(JsonContext context, JsonObject json) {
			List<Ingredient> ingredientList = new LinkedList<>();
			// start by iterating the array of ingredients
			JsonArray ingredients = JsonUtils.getJsonArray(json, "ingredients");
			for(JsonElement ingredient : ingredients) {
				JsonObject object = ingredient.getAsJsonObject();

				// if supplied with a mod ID, only parse if that mod is loaded
				if(JsonUtils.hasField(object, "modid")) {
					String mod = JsonUtils.getString(object, "modid");
					if(!Loader.isModLoaded(mod)) {
						continue;
					}
				}

				// if supplied with ingredient, parse that as the ingredent, otherwise parse the object itself
				if(JsonUtils.hasField(object, "ingredient")) {
					ingredientList.add(CraftingHelper.getIngredient(object.get("ingredient"), context));
				} else {
					ingredientList.add(CraftingHelper.getIngredient(object, context));
				}
			}

			return new ModItemList(ingredientList);
		}
	}
}
