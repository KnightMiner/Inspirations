package knightminer.inspirations.library.recipe.cauldron.recipe;

import com.google.gson.JsonObject;
import knightminer.inspirations.library.recipe.RecipeSerializer;
import knightminer.inspirations.library.recipe.RecipeSerializers;
import knightminer.inspirations.library.recipe.RecipeTypes;
import knightminer.inspirations.library.recipe.cauldron.CauldronContentTypes;
import knightminer.inspirations.library.recipe.cauldron.CauldronIngredients;
import knightminer.inspirations.library.recipe.cauldron.contents.ICauldronContents;
import knightminer.inspirations.library.recipe.cauldron.ingredient.ICauldronIngredient;
import knightminer.inspirations.library.recipe.cauldron.inventory.ICauldronState;
import knightminer.inspirations.library.recipe.cauldron.util.LevelPredicate;
import knightminer.inspirations.library.recipe.cauldron.util.TemperaturePredicate;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;
import slimeknights.mantle.recipe.ICustomOutputRecipe;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Base cauldron transform implementation
 */
public class CauldronTransform extends AbstractCauldronRecipe implements ICustomOutputRecipe<ICauldronState> {
  private final ResourceLocation id;
  private final String group;
  private final int time;
  private final SoundEvent sound;

  /**
   * Creates a new cauldron recipe
   * @param id           Recipe ID
   * @param group        Recipe group
   * @param ingredient   Cauldron contents
   * @param level        Input level
   * @param temperature  Predicate for required cauldron temperature
   * @param output       Output stack, use empty for no output
   * @param time         Time it takes the recipe
   * @param sound        Sound to play after transforming
   */
  public CauldronTransform(ResourceLocation id, String group, ICauldronIngredient ingredient, LevelPredicate level, TemperaturePredicate temperature, ICauldronContents output, int time, SoundEvent sound) {
    super(ingredient, level, temperature, output);
    this.id = id;
    this.group = group;
    this.time = time;
    this.sound = sound;
  }

  @Override
  public boolean matches(ICauldronState inv, World worldIn) {
    return inv.getLevel() != 0 && matches(inv);
  }

  /**
   * Gets the sound to play for this recipe
   * @return  Recipe sound
   */
  public SoundEvent getSound() {
    return sound;
  }


  /* Display */

  @Override
  public List<ItemStack> getItemInputs() {
    return Collections.emptyList();
  }

  @Override
  public int getTime() {
    return time;
  }

  @Override
  public int getLevelInput() {
    return level.getMax();
  }

  @Override
  public int getLevelOutput() {
    return level.getMax();
  }

  @Override
  public ItemStack getItemOutput() {
    return ItemStack.EMPTY;
  }


  /* Recipe basics */

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
    return RecipeSerializers.CAULDRON_TRANSFORM;
  }

  /**
   * Serializer for standard cauldron transforms
   */
  public static class Serializer extends RecipeSerializer<CauldronTransform> {
    @Override
    public CauldronTransform read(ResourceLocation id, JsonObject json) {
      String group = JSONUtils.getString(json, "group", "");
      // input
      ICauldronIngredient ingredient = CauldronIngredients.read(JSONUtils.getJsonObject(json, "input"));
      TemperaturePredicate temperature = getBoiling(json, "temperature");
      LevelPredicate level;
      if (json.has("level")) {
        level = LevelPredicate.read(JSONUtils.getJsonObject(json, "level"));
      } else {
        level = LevelPredicate.range(1, ICauldronRecipe.MAX);
      }

      // output
      ICauldronContents output = CauldronContentTypes.read(JSONUtils.getJsonObject(json, "output"));
      int time = JSONUtils.getInt(json, "time");

      // sound
      SoundEvent sound = SoundEvents.BLOCK_BREWING_STAND_BREW;
      if (json.has("sound")) {
        sound = CauldronRecipe.Serializer.getSound(new ResourceLocation(JSONUtils.getString(json, "sound")), sound);
      }
      return new CauldronTransform(id, group, ingredient, level, temperature, output, time, sound);
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
      SoundEvent sound = CauldronRecipe.Serializer.getSound(buffer.readResourceLocation(), SoundEvents.BLOCK_BREWING_STAND_BREW);
      return new CauldronTransform(id, group, ingredient, level, temperature, output, time, sound);
    }

    @Override
    public void write(PacketBuffer buffer, CauldronTransform recipe) {
      buffer.writeString(recipe.group);
      CauldronIngredients.write(recipe.ingredient, buffer);
      buffer.writeEnumValue(recipe.temperature);
      recipe.level.write(buffer);
      ICauldronContents contents = recipe.outputContents == null ? CauldronContentTypes.DEFAULT.get() : recipe.outputContents;
      contents.write(buffer);
      buffer.writeVarInt(recipe.time);
      buffer.writeResourceLocation(Objects.requireNonNull(recipe.sound.getRegistryName()));
    }
  }
}
