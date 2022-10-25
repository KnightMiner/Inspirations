package knightminer.inspirations.library.recipe.cauldron.recipe;

import com.google.gson.JsonObject;
import knightminer.inspirations.library.recipe.RecipeSerializers;
import knightminer.inspirations.library.recipe.cauldron.CauldronContentTypes;
import knightminer.inspirations.library.recipe.cauldron.CauldronIngredients;
import knightminer.inspirations.library.recipe.cauldron.contents.ICauldronContents;
import knightminer.inspirations.library.recipe.cauldron.ingredient.ICauldronIngredient;
import knightminer.inspirations.library.recipe.cauldron.inventory.ICauldronState;
import knightminer.inspirations.library.recipe.cauldron.util.LevelPredicate;
import knightminer.inspirations.library.recipe.cauldron.util.TemperaturePredicate;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import slimeknights.mantle.recipe.helper.AbstractRecipeSerializer;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Base cauldron transform implementation
 */
public class CauldronTransform extends AbstractCauldronRecipe implements ICauldronTransform {
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
  public boolean matches(ICauldronState inv, Level worldIn) {
    return inv.getLevel() != 0 && matches(inv);
  }

  @Override
  public ICauldronContents getContentOutput(ICauldronState inv) {
    if (outputContents == null) {
      return inv.getContents();
    }
    return outputContents;
  }

  @Override
  public SoundEvent getSound() {
    return sound;
  }

  @Override
  public int getTime() {
    return time;
  }


  /* Display */

  @Override
  public List<ItemStack> getItemInputs() {
    return Collections.emptyList();
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
  public RecipeSerializer<?> getSerializer() {
    return RecipeSerializers.CAULDRON_TRANSFORM;
  }

  /**
   * Serializer for standard cauldron transforms
   */
  public static class Serializer extends AbstractRecipeSerializer<CauldronTransform> {
    @Override
    public CauldronTransform fromJson(ResourceLocation id, JsonObject json) {
      String group = GsonHelper.getAsString(json, "group", "");
      // input
      ICauldronIngredient ingredient = CauldronIngredients.read(GsonHelper.getAsJsonObject(json, "input"));
      TemperaturePredicate temperature = getBoiling(json, "temperature");
      LevelPredicate level;
      if (json.has("level")) {
        level = LevelPredicate.read(GsonHelper.getAsJsonObject(json, "level"));
      } else {
        level = LevelPredicate.range(1, ICauldronRecipe.MAX);
      }

      // output
      ICauldronContents output = CauldronContentTypes.read(GsonHelper.getAsJsonObject(json, "output"));
      int time = GsonHelper.getAsInt(json, "time");

      // sound
      SoundEvent sound = SoundEvents.BREWING_STAND_BREW;
      if (json.has("sound")) {
        sound = CauldronRecipe.Serializer.getSound(new ResourceLocation(GsonHelper.getAsString(json, "sound")), sound);
      }
      return new CauldronTransform(id, group, ingredient, level, temperature, output, time, sound);
    }

    @Nullable
    @Override
    public CauldronTransform fromNetwork(ResourceLocation id, FriendlyByteBuf buffer) {
      String group = buffer.readUtf(Short.MAX_VALUE);
      ICauldronIngredient ingredient = CauldronIngredients.read(buffer);
      TemperaturePredicate temperature = buffer.readEnum(TemperaturePredicate.class);
      LevelPredicate level = LevelPredicate.read(buffer);
      ICauldronContents output = CauldronContentTypes.read(buffer);
      int time = buffer.readVarInt();
      SoundEvent sound = CauldronRecipe.Serializer.getSound(buffer.readResourceLocation(), SoundEvents.BREWING_STAND_BREW);
      return new CauldronTransform(id, group, ingredient, level, temperature, output, time, sound);
    }

    @Override
    public void toNetwork(FriendlyByteBuf buffer, CauldronTransform recipe) {
      buffer.writeUtf(recipe.group);
      CauldronIngredients.write(recipe.ingredient, buffer);
      buffer.writeEnum(recipe.temperature);
      recipe.level.write(buffer);
      ICauldronContents contents = recipe.outputContents == null ? CauldronContentTypes.DEFAULT.get() : recipe.outputContents;
      contents.write(buffer);
      buffer.writeVarInt(recipe.time);
      buffer.writeResourceLocation(Objects.requireNonNull(recipe.sound.getRegistryName()));
    }
  }
}
