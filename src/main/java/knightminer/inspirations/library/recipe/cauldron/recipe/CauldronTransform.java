package knightminer.inspirations.library.recipe.cauldron.recipe;

import com.google.gson.JsonObject;
import knightminer.inspirations.library.recipe.RecipeSerializer;
import knightminer.inspirations.library.recipe.RecipeTypes;
import knightminer.inspirations.library.recipe.cauldron.CauldronContentTypes;
import knightminer.inspirations.library.recipe.cauldron.CauldronIngredients;
import knightminer.inspirations.library.recipe.cauldron.contents.ICauldronContents;
import knightminer.inspirations.library.recipe.cauldron.ingredient.ICauldronIngredient;
import knightminer.inspirations.library.recipe.cauldron.inventory.ICauldronState;
import knightminer.inspirations.library.recipe.cauldron.util.LevelPredicate;
import knightminer.inspirations.library.recipe.cauldron.util.TemperaturePredicate;
import knightminer.inspirations.recipes.InspirationsRecipes;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import slimeknights.mantle.recipe.ICustomOutputRecipe;

import javax.annotation.Nullable;

/**
 * Base cauldron transform implementation
 */
@SuppressWarnings("unused")
public class CauldronTransform implements ICustomOutputRecipe<ICauldronState> {
  private final ResourceLocation id;
  private final String group;
  private final ICauldronIngredient ingredient;
  private final LevelPredicate level;
  private final TemperaturePredicate temperature;
  private final ICauldronContents output;
  private final int time;

  /**
   * Creates a new cauldron recipe
   * @param id           Recipe ID
   * @param group        Recipe group
   * @param ingredient   Cauldron contents
   * @param level        Input level
   * @param temperature  Predicate for required cauldron temperature
   * @param output       Output stack, use empty for no output
   * @param time         Time it takes the recipe
   */
  public CauldronTransform(ResourceLocation id, String group, ICauldronIngredient ingredient, LevelPredicate level, TemperaturePredicate temperature, ICauldronContents output, int time) {
    this.id = id;
    this.group = group;
    this.ingredient = ingredient;
    this.level = level;
    this.temperature = temperature;
    this.output = output;
    this.time = time;
  }

  @Override
  public boolean matches(ICauldronState inv, World worldIn) {
    return temperature.test(inv.isBoiling()) && level.test(inv.getLevel()) && ingredient.test(inv.getContents());
  }

  /**
   * Gets the new contents after this recipe
   * @param state  Existing cauldron state
   * @return  New cauldron contents
   */
  public ICauldronContents getOutput(ICauldronState state) {
    return output;
  }

  /**
   * Gets the time it will take this recipe
   * @param state  Cauldron state
   * @return  Time it takes the recipe to finish in ticks
   */
  public int getTime(ICauldronState state) {
    return time;
  }

  @Override
  public ResourceLocation getId() {
    return id;
  }

  @Override
  public String getGroup() {
    return group;
  }

  @Override
  public IRecipeType<?> getType() {
    return RecipeTypes.CAULDRON_TRANSFORM;
  }

  @Override
  public IRecipeSerializer<?> getSerializer() {
    return InspirationsRecipes.cauldronTransformSerializer;
  }

  /**
   * Serializer for standard cauldron transforms
   */
  public static class Serializer extends RecipeSerializer<CauldronTransform> {
    @Override
    public CauldronTransform read(ResourceLocation id, JsonObject json) {
      String group = JSONUtils.getString(json, "group", "");
      ICauldronIngredient ingredient = CauldronIngredients.read(JSONUtils.getJsonObject(json, "input"));
      TemperaturePredicate temperature = CauldronRecipe.getBoiling(json, "temperature");
      LevelPredicate level;
      if (json.has("level")) {
        level = LevelPredicate.read(JSONUtils.getJsonObject(json, "level"));
      } else {
        level = LevelPredicate.range(1, ICauldronRecipe.MAX);
      }
      ICauldronContents output = CauldronContentTypes.read(JSONUtils.getJsonObject(json, "output"));
      int time = JSONUtils.getInt(json, "time");
      return new CauldronTransform(id, group, ingredient, level, temperature, output, time);
    }

    @Nullable
    @Override
    public CauldronTransform read(ResourceLocation id, PacketBuffer buffer) {
      String group = buffer.readString(Short.MAX_VALUE);
      ICauldronIngredient ingredient = CauldronIngredients.read(buffer);
      TemperaturePredicate temperature = buffer.readEnumValue(TemperaturePredicate.class);
      LevelPredicate level = LevelPredicate.read(buffer);
      ICauldronContents output = CauldronContentTypes.read(buffer);
      int time = buffer.readVarInt();
      return new CauldronTransform(id, group, ingredient, level, temperature, output, time);
    }

    @Override
    public void write(PacketBuffer buffer, CauldronTransform recipe) {
      buffer.writeString(recipe.group);
      CauldronIngredients.write(recipe.ingredient, buffer);
      buffer.writeEnumValue(recipe.temperature);
      recipe.level.write(buffer);
      CauldronContentTypes.write(recipe.output, buffer);
      buffer.writeVarInt(recipe.time);
    }
  }
}
