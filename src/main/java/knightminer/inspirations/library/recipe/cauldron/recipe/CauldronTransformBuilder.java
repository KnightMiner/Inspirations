package knightminer.inspirations.library.recipe.cauldron.recipe;

import com.google.gson.JsonObject;
import knightminer.inspirations.library.recipe.RecipeSerializers;
import knightminer.inspirations.library.recipe.cauldron.CauldronIngredients;
import knightminer.inspirations.library.recipe.cauldron.contents.ICauldronContents;
import knightminer.inspirations.library.recipe.cauldron.ingredient.ICauldronIngredient;
import knightminer.inspirations.library.recipe.cauldron.util.LevelPredicate;
import knightminer.inspirations.library.recipe.cauldron.util.TemperaturePredicate;
import net.minecraft.advancements.Advancement;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import slimeknights.mantle.recipe.data.AbstractRecipeBuilder;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Builds a recipe that converts one fluid in the cauldron to another over time
 */
public class CauldronTransformBuilder extends AbstractRecipeBuilder<CauldronTransformBuilder> {
  private final ICauldronIngredient ingredient;
  private final ICauldronContents output;
  private final int time;
  @Nullable
  private LevelPredicate level;
  private TemperaturePredicate temperature = TemperaturePredicate.ANY;
  @Nullable
  private SoundEvent sound = null;

  private CauldronTransformBuilder(ICauldronIngredient ingredient, ICauldronContents output, int time) {
    this.ingredient = ingredient;
    this.output = output;
    this.time = time;
  }

  /**
   * Creates a new builder instance
   * @param ingredient  Input ingredient
   * @param output      Output contents
   * @param time        Recipe duration
   * @return  Builder instance
   */
  public static CauldronTransformBuilder transform(ICauldronIngredient ingredient, ICauldronContents output, int time) {
    if (time <= 0) {
      throw new IllegalArgumentException("Time must be greater than zero");
    }
    return new CauldronTransformBuilder(ingredient, output, time);
  }

  /**
   * Sets the required temperature
   * @param temp  Temperature
   * @return  Builder instance
   */
  public CauldronTransformBuilder setTemperature(TemperaturePredicate temp) {
    this.temperature = temp;
    return this;
  }

  /* Levels */

  /**
   * Sets the minimum number of levels required to match
   * @param min  Minimum levels
   * @return  Builder instance
   */
  public CauldronTransformBuilder minLevels(int min) {
    if (min <= 0) {
      throw new IllegalArgumentException("Cannot match 0 levels");
    }
    this.level = LevelPredicate.min(min);
    return this;
  }

  /**
   * Sets the minimum number of levels required to match
   * @param max  Maximum levels
   * @return  Builder instance
   */
  public CauldronTransformBuilder maxLevels(int max) {
    if (max <= 0) {
      throw new IllegalArgumentException("Cannot match 0 levels");
    }
    this.level = LevelPredicate.range(1, max);
    return this;
  }

  /**
   * Sets the required number of levels to be a full cauldron
   * @return  Builder instance
   */
  public CauldronTransformBuilder matchFull() {
    return minLevels(ICauldronRecipe.MAX);
  }

  /**
   * Sets the range of levels required to match
   * @param min  Minimum levels
   * @param max  Maximum levels
   * @return  Builder instance
   */
  public CauldronTransformBuilder levelRange(int min, int max) {
    if (min <= 0) {
      throw new IllegalArgumentException("Cannot match 0 levels");
    }
    this.level = LevelPredicate.range(min, max);
    return this;
  }

  /**
   * Sets the sound to play upon performing this recipe
   * @param sound  Recipe sound
   * @return  Sound played after recipe is done
   */
  public CauldronTransformBuilder setSound(SoundEvent sound) {
    this.sound = sound;
    return this;
  }

  @Override
  public void build(Consumer<IFinishedRecipe> consumer) {
    ResourceLocation name = CauldronRecipeBuilder.nameFromContents(output);
    if (name != null) {
      build(consumer, name);
    }
    throw new IllegalStateException("Unable to create automatic recipe ID");
  }

  @Override
  public void build(Consumer<IFinishedRecipe> consumer, ResourceLocation id) {
    ResourceLocation advancementId = this.buildAdvancement(id, "cauldron");
    consumer.accept(new Result(id, ingredient, level, temperature, output, time, sound, advancementBuilder, advancementId));
  }

  /**
   * Result class
   */
  private static class Result implements IFinishedRecipe {
    private final ResourceLocation id;
    private final ICauldronIngredient ingredient;
    @Nullable
    private final LevelPredicate level;
    private final TemperaturePredicate temperature;
    private final ICauldronContents output;
    private final int time;
    @Nullable
    private final SoundEvent sound;
    private final Advancement.Builder advancementBuilder;
    private final ResourceLocation advancementId;

    private Result(ResourceLocation id, ICauldronIngredient ingredient, @Nullable LevelPredicate level, TemperaturePredicate temperature, ICauldronContents output, int time, @Nullable SoundEvent sound, Advancement.Builder advancementBuilder, ResourceLocation advancementId) {
      this.id = id;
      this.ingredient = ingredient;
      this.level = level;
      this.temperature = temperature;
      this.output = output;
      this.time = time;
      this.sound = sound;
      this.advancementBuilder = advancementBuilder;
      this.advancementId = advancementId;
    }

    @Override
    public void serialize(JsonObject json) {
      json.add("input", CauldronIngredients.toJson(ingredient));
      if (level != null) {
        json.add("level", level.toJson());
      }
      if (temperature != TemperaturePredicate.ANY) {
        json.addProperty("temperature", temperature.getName());
      }
      json.add("output", output.toJson());
      json.addProperty("time", time);
      if (sound != null) {
        json.addProperty("sound", Objects.requireNonNull(sound.getRegistryName()).toString());
      }
    }

    @Override
    public ResourceLocation getID() {
      return id;
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
      return RecipeSerializers.CAULDRON_TRANSFORM;
    }

    @Nullable
    @Override
    public JsonObject getAdvancementJson() {
      return advancementBuilder.serialize();
    }

    @Nullable
    @Override
    public ResourceLocation getAdvancementID() {
      return advancementId;
    }
  }
}
