package knightminer.inspirations.library.recipe;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraftforge.common.crafting.CompoundIngredient;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.IIngredientSerializer;
import net.minecraftforge.fml.ModList;

import javax.annotation.Nonnull;

public class ModItemList extends CompoundIngredient {

	protected ModItemList(List<Ingredient> children) {
		super(children);
	}

	@Nonnull
    @Override
    public IIngredientSerializer<? extends Ingredient> getSerializer()
    {
    	return new Factory();
    }

    public static final Factory SERIALIZER = new Factory();


	public static class Factory implements IIngredientSerializer<ModItemList> {

		@Nonnull
		@Override
		public ModItemList parse(@Nonnull JsonObject json) {
			List<Ingredient> ingredientList = new LinkedList<>();
			// start by iterating the array of ingredients
			JsonArray ingredients = JSONUtils.getJsonArray(json, "ingredients");
			for(JsonElement ingredient : ingredients) {
				JsonObject object = ingredient.getAsJsonObject();

				// if supplied with a mod ID, only parse if that mod is loaded
				if(JSONUtils.hasField(object, "modid")) {
					String mod = JSONUtils.getString(object, "modid");
					if(!ModList.get().isLoaded(mod)) {
						continue;
					}
				}

				// if supplied with ingredient, parse that as the ingredent, otherwise parse the object itself
				if(JSONUtils.hasField(object, "ingredient")) {
					ingredientList.add(CraftingHelper.getIngredient(object.get("ingredient")));
				} else {
					ingredientList.add(CraftingHelper.getIngredient(object));
				}
			}

			return new ModItemList(ingredientList);
		}

		@Override
		public void write(@Nonnull PacketBuffer buffer, @Nonnull ModItemList ingredient) {
			buffer.writeVarInt(ingredient.getChildren().size());
			ingredient.getChildren().forEach((child) -> child.write(buffer));
		}

		@Nonnull
		@Override
		public ModItemList parse(@Nonnull PacketBuffer buffer) {
			return new ModItemList(Stream
					.generate(() -> Ingredient.read(buffer))
					.limit(buffer.readVarInt())
					.collect(Collectors.toList())
			);
		}
	}
}
