package knightminer.inspirations.library.recipe.cauldron.recipe;

import com.google.gson.JsonObject;
import knightminer.inspirations.library.recipe.RecipeSerializers;
import knightminer.inspirations.library.recipe.cauldron.CauldronContentTypes;
import knightminer.inspirations.library.recipe.cauldron.CauldronIngredients;
import knightminer.inspirations.library.recipe.cauldron.contents.ICauldronContents;
import knightminer.inspirations.library.recipe.cauldron.ingredient.ICauldronIngredient;
import knightminer.inspirations.library.recipe.cauldron.util.LevelPredicate;
import knightminer.inspirations.library.recipe.cauldron.util.LevelUpdate;
import knightminer.inspirations.library.recipe.cauldron.util.TemperaturePredicate;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.Advancement.Builder;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.potion.Potion;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import slimeknights.mantle.recipe.SizedIngredient;
import slimeknights.mantle.recipe.data.AbstractRecipeBuilder;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * Builder for a standard cauldron recipe
 */
public class CauldronRecipeBuilder extends AbstractRecipeBuilder<CauldronRecipeBuilder> {
  private final SizedIngredient input;
  private final ICauldronIngredient contents;
  private LevelPredicate levels;
  private TemperaturePredicate temperature = TemperaturePredicate.ANY;
  private ItemStack output = ItemStack.EMPTY;
  private boolean copyNBT = false;
  @Nullable
  private ICauldronContents newContents = null;
  private LevelUpdate levelUpdate = LevelUpdate.IDENTITY;
  @Nullable
  private ItemStack container = null;
  private SoundEvent sound = null;

  private CauldronRecipeBuilder(SizedIngredient input, ICauldronIngredient contents) {
    this.input = input;
    this.contents = contents;
  }

  /**
   * Creates a recipe matching the given input
   * @param input     Input item
   * @param contents  Contents to match
   * @return  Builder instance
   */
  public static CauldronRecipeBuilder cauldron(SizedIngredient input, ICauldronIngredient contents) {
    return new CauldronRecipeBuilder(input, contents);
  }

  /**
   * Creates a recipe matching no input
   * @param contents  Contents to match
   * @return  Builder instance
   */
  public static CauldronRecipeBuilder cauldron(ICauldronIngredient contents) {
    return cauldron(SizedIngredient.EMPTY, contents);
  }


  /* Inputs */

  /**
   * Sets the minimum number of levels required to match
   * @param min  Minimum levels
   * @return  Builder instance
   */
  public CauldronRecipeBuilder minLevels(int min) {
    this.levels = LevelPredicate.min(min);
    return this;
  }

  /**
   * Sets the minimum number of levels required to match
   * @param max  Maximum levels
   * @return  Builder instance
   */
  public CauldronRecipeBuilder maxLevels(int max) {
    this.levels = LevelPredicate.max(max);
    return this;
  }

  /**
   * Sets the required number of levels to be a empty cauldron
   * @return  Builder instance
   */
  public CauldronRecipeBuilder matchEmpty() {
    return maxLevels(0);
  }

  /**
   * Sets the required number of levels to be a full cauldron
   * @return  Builder instance
   */
  public CauldronRecipeBuilder matchFull() {
    return minLevels(ICauldronRecipe.MAX);
  }

  /**
   * Sets the range of levels required to match
   * @param min  Minimum levels
   * @param max  Maximum levels
   * @return  Builder instance
   */
  public CauldronRecipeBuilder levelRange(int min, int max) {
    this.levels = LevelPredicate.range(min, max);
    return this;
  }

  /**
   * Sets the required temperature
   * @param temp  Temperature
   * @return  Builder instance
   */
  public CauldronRecipeBuilder setTemperature(TemperaturePredicate temp) {
    this.temperature = temp;
    return this;
  }


  /* Outputs */

  /**
   * Sets the recipe output
   * @param output  Output
   * @return  Builder instance
   */
  public CauldronRecipeBuilder setOutput(ItemStack output) {
    this.output = output;
    return this;
  }

  /**
   * Sets the recipe output
   * @param output  Output
   * @return  Builder instance
   */
  public CauldronRecipeBuilder setOutput(IItemProvider output) {
    return setOutput(new ItemStack(output));
  }

  /**
   * Sets the recipe to copy input NBT to the output
   * @return  Builder instance
   */
  public CauldronRecipeBuilder setCopyNBT() {
    copyNBT = true;
    return this;
  }

  /**
   * Sets the container result
   * @param container  Container, use {@link ItemStack#EMPTY} to block vanilla containers
   * @return  Builder instance
   */
  public CauldronRecipeBuilder setContainer(ItemStack container) {
    this.container = container;
    return this;
  }

  /**
   * Prevents a container from being returned from the input
   * @return  Builder instance
   */
  public CauldronRecipeBuilder noContainer() {
    return setContainer(ItemStack.EMPTY);
  }

  /**
   * Sets the container result
   * @param container  Container item
   * @return  Builder instance
   */
  public CauldronRecipeBuilder setContainer(IItemProvider container) {
    return setContainer(new ItemStack(container));
  }


  /* Cauldron output */

  /**
   * Sets the cauldron contents output
   * @param contents  Contents output
   * @return  Builder instance
   */
  public CauldronRecipeBuilder setOutput(ICauldronContents contents) {
    this.newContents = contents;
    return this;
  }

  /**
   * Sets the cauldron levels
   * @param levels  New levels
   * @return  Builder instance
   */
  public CauldronRecipeBuilder setLevels(int levels) {
    this.levelUpdate = LevelUpdate.set(levels);
    return this;
  }

  /**
   * Sets the cauldron to full
   * @return  Builder instance
   */
  public CauldronRecipeBuilder setFull() {
    return setLevels(ICauldronRecipe.MAX);
  }

  /**
   * Sets the cauldron to empty
   * @return  Builder instance
   */
  public CauldronRecipeBuilder setEmpty() {
    return setLevels(0);
  }

  /**
   * Adds levels to the cauldron
   * @param levels  Levels to add
   * @return  Builder instance
   */
  public CauldronRecipeBuilder addLevels(int levels) {
    this.levelUpdate = LevelUpdate.add(levels);
    return this;
  }

  /**
   * Sets the sound to play upon performing this recipe
   * @param sound  Recipe sound
   * @return  Sound played after recipe is done
   */
  public CauldronRecipeBuilder setSound(SoundEvent sound) {
    this.sound = sound;
    return this;
  }

  @Override
  public void build(Consumer<IFinishedRecipe> consumer) {
    // try output first
    if (!output.isEmpty()) {
      build(consumer, Objects.requireNonNull(output.getItem().getRegistryName()));
      return;
    }
    if (newContents != null) {
      ResourceLocation name = nameFromContents(newContents);
      if (name != null) {
        build(consumer, name);
      }
    }
    throw new IllegalStateException("Unable to create automatic recipe ID");
  }

  @Nullable
  public static ResourceLocation nameFromContents(ICauldronContents contents) {
    // try fluid next
    Optional<Fluid> fluid = contents.get(CauldronContentTypes.FLUID);
    if (fluid.isPresent()) {
      return Objects.requireNonNull(fluid.get().getRegistryName());
    }
    // try potion
    Optional<Potion> potion = contents.get(CauldronContentTypes.POTION);
    if (potion.isPresent()) {
      return Objects.requireNonNull(potion.get().getRegistryName());
    }
    // try custom
    Optional<ResourceLocation> custom = contents.get(CauldronContentTypes.CUSTOM);
    return custom.orElse(null);
  }

  @Override
  public void build(Consumer<IFinishedRecipe> consumer, ResourceLocation id) {
    if (levels == null) {
      throw new IllegalStateException("No levels defined for cauldron recipe " + id + "!");
    }

    ResourceLocation advancementId = this.buildAdvancement(id, "cauldron");
    consumer.accept(new Result(id, group, input, contents, levels, temperature, output, copyNBT,
                               newContents, levelUpdate, container, sound, advancementBuilder, advancementId));
  }

  private static class Result implements IFinishedRecipe {
    private final ResourceLocation id;
    private final String group;
    private final SizedIngredient input;
    private final ICauldronIngredient contents;
    private final LevelPredicate level;
    private final TemperaturePredicate temperature;
    private final ItemStack output;
    private final boolean copyNBT;
    @Nullable
    private final ICauldronContents newContents;
    private final LevelUpdate levelUpdate;
    @Nullable
    private final ItemStack container;
    @Nullable
    private final SoundEvent sound;
    private final Advancement.Builder advancementBuilder;
    private final ResourceLocation advancementId;

    private Result(ResourceLocation id, String group, SizedIngredient input, ICauldronIngredient contents, LevelPredicate level, TemperaturePredicate temperature, ItemStack output, boolean copyNBT, @Nullable ICauldronContents newContents, LevelUpdate levelUpdate, @Nullable ItemStack container, @Nullable SoundEvent sound, Builder advancementBuilder, ResourceLocation advancementId) {
      this.id = id;
      this.group = group;
      this.input = input;
      this.contents = contents;
      this.level = level;
      this.temperature = temperature;
      this.output = output;
      this.copyNBT = copyNBT;
      this.newContents = newContents;
      this.levelUpdate = levelUpdate;
      this.container = container;
      this.sound = sound;
      this.advancementBuilder = advancementBuilder;
      this.advancementId = advancementId;
    }

    private static JsonObject toJson(ItemStack stack) {
      JsonObject item = new JsonObject();
      item.addProperty("item", Objects.requireNonNull(stack.getItem().getRegistryName()).toString());
      if (stack.getCount() != 1) {
        item.addProperty("count", stack.getCount());
      }
      if (stack.hasTag()) {
        assert stack.getTag() != null;
        item.addProperty("nbt", stack.getTag().toString());
      }
      return item;
    }

    @Override
    public void serializeRecipeData(JsonObject json) {
      if (!group.isEmpty()) {
        json.addProperty("group", group);
      }

      // inputs
      JsonObject inputJson = new JsonObject();
      if (input != SizedIngredient.EMPTY) {
        inputJson.add("item", input.serialize());
      }
      inputJson.add("contents", CauldronIngredients.toJson(contents));
      inputJson.add("level", level.toJson());
      if (temperature != TemperaturePredicate.ANY) {
        inputJson.addProperty("temperature", temperature.getName());
      }
      json.add("input", inputJson);

      // output
      JsonObject outputJson = new JsonObject();
      if (!output.isEmpty()) {
        outputJson.add("item", toJson(output));
        if (copyNBT) {
          outputJson.addProperty("copy_nbt", true);
        }
      }
      // container
      if (container != null) {
        if (container.isEmpty()) {
          JsonObject item = new JsonObject();
          item.addProperty("empty", true);
          outputJson.add("container", item);
        } else {
          outputJson.add("container", toJson(container));
        }
      }
      if (newContents != null) {
        outputJson.add("contents", newContents.toJson());
      }
      if (levelUpdate != LevelUpdate.IDENTITY) {
        outputJson.add("level", levelUpdate.toJson());
      }
      json.add("output", outputJson);
      if (sound != null) {
        json.addProperty("sound", Objects.requireNonNull(sound.getRegistryName()).toString());
      }
    }

    @Override
    public ResourceLocation getId() {
      return id;
    }

    @Override
    public IRecipeSerializer<?> getType() {
      return RecipeSerializers.CAULDRON;
    }

    @Nullable
    @Override
    public JsonObject serializeAdvancement() {
      return advancementBuilder.serializeToJson();
    }

    @Nullable
    @Override
    public ResourceLocation getAdvancementId() {
      return advancementId;
    }
  }
}
