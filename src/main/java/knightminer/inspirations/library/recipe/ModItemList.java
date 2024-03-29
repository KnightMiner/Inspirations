package knightminer.inspirations.library.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.GsonHelper;
import net.minecraftforge.common.crafting.CompoundIngredient;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.IIngredientSerializer;
import net.minecraftforge.fml.ModList;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SuppressWarnings("WeakerAccess")
public class ModItemList extends CompoundIngredient {

  protected ModItemList(List<Ingredient> children) {
    super(children);
  }

  @Override
  public IIngredientSerializer<? extends Ingredient> getSerializer() {
    return new Factory();
  }

  public static final Factory SERIALIZER = new Factory();


  public static class Factory implements IIngredientSerializer<ModItemList> {
    @Override
    public ModItemList parse(JsonObject json) {
      List<Ingredient> ingredientList = new LinkedList<>();
      // start by iterating the array of ingredients
      JsonArray ingredients = GsonHelper.getAsJsonArray(json, "ingredients");
      for (JsonElement ingredient : ingredients) {
        JsonObject object = ingredient.getAsJsonObject();

        // if supplied with a mod ID, only parse if that mod is loaded
        if (GsonHelper.isValidNode(object, "modid")) {
          String mod = GsonHelper.getAsString(object, "modid");
          if (!ModList.get().isLoaded(mod)) {
            continue;
          }
        }

        // if supplied with ingredient, parse that as the ingredent, otherwise parse the object itself
        if (GsonHelper.isValidNode(object, "ingredient")) {
          ingredientList.add(CraftingHelper.getIngredient(object.get("ingredient")));
        } else {
          ingredientList.add(CraftingHelper.getIngredient(object));
        }
      }

      return new ModItemList(ingredientList);
    }

    @Override
    public void write(FriendlyByteBuf buffer, ModItemList ingredient) {
      buffer.writeVarInt(ingredient.getChildren().size());
      ingredient.getChildren().forEach((child) -> child.toNetwork(buffer));
    }

    @Override
    public ModItemList parse(FriendlyByteBuf buffer) {
      return new ModItemList(Stream
                                 .generate(() -> Ingredient.fromNetwork(buffer))
                                 .limit(buffer.readVarInt())
                                 .collect(Collectors.toList())
      );
    }
  }
}
