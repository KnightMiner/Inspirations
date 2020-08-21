package knightminer.inspirations.library.recipe.cauldron.recipe;

import com.google.gson.JsonObject;
import knightminer.inspirations.library.recipe.cauldron.CauldronContentTypes;
import knightminer.inspirations.library.recipe.cauldron.CauldronIngredients;
import knightminer.inspirations.library.recipe.cauldron.contents.EmptyCauldronContents;
import knightminer.inspirations.library.recipe.cauldron.contents.ICauldronContents;
import knightminer.inspirations.library.recipe.cauldron.contents.ICauldronFluid;
import knightminer.inspirations.library.recipe.cauldron.contents.ICauldronPotion;
import knightminer.inspirations.library.recipe.cauldron.ingredient.ICauldronIngredient;
import knightminer.inspirations.library.recipe.cauldron.util.LevelPredicate;
import knightminer.inspirations.library.recipe.cauldron.util.LevelUpdate;
import knightminer.inspirations.library.recipe.cauldron.util.TemperaturePredicate;
import knightminer.inspirations.recipes.InspirationsRecipes;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.Advancement.Builder;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.potion.Potion;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import slimeknights.mantle.recipe.data.AbstractRecipeBuilder;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * Builder for a standard cauldron recipe
 */
public class CauldronRecipeBuilder extends AbstractRecipeBuilder<CauldronRecipeBuilder> {
  private final Ingredient input;
  private final int amount;
  private final ICauldronIngredient contents;
  private LevelPredicate levels;
  private TemperaturePredicate temperature = TemperaturePredicate.ANY;
  private ItemStack output = ItemStack.EMPTY;
  private ICauldronContents newContents = EmptyCauldronContents.INSTANCE;
  private LevelUpdate levelUpdate = LevelUpdate.IDENTITY;
  @Nullable
  private ItemStack container = null;

  private CauldronRecipeBuilder(Ingredient input, int amount, ICauldronIngredient contents) {
    this.input = input;
    this.amount = amount;
    this.contents = contents;
  }

  /**
   * Creates a recipe matching the given input
   * @param input     Input item
   * @param amount    Number to match
   * @param contents  Contents to match
   * @return  Builder instance
   */
  public static CauldronRecipeBuilder cauldron(Ingredient input, int amount, ICauldronIngredient contents) {
    return new CauldronRecipeBuilder(input, amount, contents);
  }

  /**
   * Creates a recipe matching one of the given input
   * @param input     Input item
   * @param contents  Contents to match
   * @return  Builder instance
   */
  public static CauldronRecipeBuilder cauldron(Ingredient input, ICauldronIngredient contents) {
    return cauldron(input, 1, contents);
  }

  /**
   * Creates a recipe matching no input
   * @param contents  Contents to match
   * @return  Builder instance
   */
  public static CauldronRecipeBuilder cauldron(ICauldronIngredient contents) {
    return cauldron(Ingredient.EMPTY, 0, contents);
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

  @Override
  public void build(Consumer<IFinishedRecipe> consumer) {
    // try output first
    if (!output.isEmpty()) {
      build(consumer, Objects.requireNonNull(output.getItem().getRegistryName()));
      return;
    }
    if (newContents != EmptyCauldronContents.INSTANCE) {
      // try fluid next
      Optional<Fluid> fluid = newContents.as(CauldronContentTypes.FLUID).map(ICauldronFluid::getFluid);
      if (fluid.isPresent()) {
        build(consumer, Objects.requireNonNull(fluid.get().getRegistryName()));
        return;
      }
      // try potion
      Optional<Potion> potion = newContents.as(CauldronContentTypes.POTION).map(ICauldronPotion::getPotion);
      if (potion.isPresent()) {
        build(consumer, Objects.requireNonNull(potion.get().getRegistryName()));
        return;
      }
    }
    throw new IllegalStateException("Unable to create automatic recipe ID");
  }

  @Override
  public void build(Consumer<IFinishedRecipe> consumer, ResourceLocation id) {
    if (levels == null) {
      throw new IllegalStateException("No levels defined for cauldron recipe " + id + "!");
    }

    ResourceLocation advancementId = this.buildAdvancement(id, "cauldron");
    consumer.accept(new Result(id, getGroup(), input, amount, contents, levels, temperature, output,
                               newContents, levelUpdate, container, advancementBuilder, advancementId));
  }

  private static class Result implements IFinishedRecipe {
    private final ResourceLocation id;
    private final String group;
    private final Ingredient input;
    private final int amount;
    private final ICauldronIngredient contents;
    private final LevelPredicate level;
    private final TemperaturePredicate temperature;
    private final ItemStack output;
    private final ICauldronContents newContents;
    private final LevelUpdate levelUpdate;
    @Nullable
    private final ItemStack container;
    private final Advancement.Builder advancementBuilder;
    private final ResourceLocation advancementId;

    private Result(ResourceLocation id, String group, Ingredient input, int amount, ICauldronIngredient contents, LevelPredicate level, TemperaturePredicate temperature, ItemStack output, ICauldronContents newContents, LevelUpdate levelUpdate, @Nullable ItemStack container, Builder advancementBuilder, ResourceLocation advancementId) {
      this.id = id;
      this.group = group;
      this.input = input;
      this.amount = amount;
      this.contents = contents;
      this.level = level;
      this.temperature = temperature;
      this.output = output;
      this.newContents = newContents;
      this.levelUpdate = levelUpdate;
      this.container = container;
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
    public void serialize(JsonObject json) {
      if (!group.isEmpty()) {
        json.addProperty("group", group);
      }

      // inputs
      JsonObject inputJson = new JsonObject();
      if (input != Ingredient.EMPTY) {
        inputJson.add("item", input.serialize());
        if (amount != 1) {
          inputJson.addProperty("amount", amount);
        }
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
      if (newContents != EmptyCauldronContents.INSTANCE) {
        outputJson.add("contents", CauldronContentTypes.toJson(newContents));
      }
      if (levelUpdate != LevelUpdate.IDENTITY) {
        outputJson.add("level", levelUpdate.toJson());
      }
      json.add("output", outputJson);
    }

    @Override
    public ResourceLocation getID() {
      return id;
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
      return InspirationsRecipes.cauldronSerializer;
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
