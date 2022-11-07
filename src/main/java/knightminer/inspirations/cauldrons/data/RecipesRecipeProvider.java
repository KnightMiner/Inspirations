package knightminer.inspirations.cauldrons.data;

import knightminer.inspirations.common.data.ConfigEnabledCondition;
import knightminer.inspirations.common.datagen.IInspirationsRecipeBuilder;
import knightminer.inspirations.library.InspirationsTags;
import knightminer.inspirations.library.recipe.RecipeSerializers;
import knightminer.inspirations.cauldrons.InspirationsCaudrons;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.data.recipes.UpgradeRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.IConditionBuilder;
import slimeknights.mantle.recipe.data.ConsumerWrapperBuilder;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

public class RecipesRecipeProvider extends RecipeProvider implements IConditionBuilder, IInspirationsRecipeBuilder {
  public RecipesRecipeProvider(DataGenerator generatorIn) {
    super(generatorIn);
  }


  @Override
  public String getName() {
    return "Inspirations Recipes - Recipes";
  }

  @Override
  protected void buildCraftingRecipes(Consumer<FinishedRecipe> consumer) {
    String folder = "cauldron/";

    // dyes //

    // mix dyed bottles
    String mixFolder = folder + "bottle/mix/";
    addDyedBottleMix(consumer, mixFolder, DyeColor.CYAN, DyeColor.BLUE, DyeColor.GREEN);
    addDyedBottleMix(consumer, mixFolder, DyeColor.GRAY, DyeColor.BLACK, DyeColor.WHITE);
    addDyedBottleMix(consumer, mixFolder, DyeColor.LIGHT_BLUE, DyeColor.BLUE, DyeColor.WHITE);
    addDyedBottleMix(consumer, mixFolder, DyeColor.LIGHT_GRAY, DyeColor.BLACK, DyeColor.WHITE, DyeColor.WHITE);
    addDyedBottleMix(consumer, mixFolder, DyeColor.LIGHT_GRAY, DyeColor.GRAY, DyeColor.WHITE);
    addDyedBottleMix(consumer, mixFolder, DyeColor.LIME, DyeColor.GREEN, DyeColor.WHITE);
    addDyedBottleMix(consumer, mixFolder, DyeColor.MAGENTA, DyeColor.BLUE, DyeColor.RED, DyeColor.PINK);
    addDyedBottleMix(consumer, mixFolder, DyeColor.MAGENTA, DyeColor.BLUE, DyeColor.RED, DyeColor.RED, DyeColor.WHITE);
    addDyedBottleMix(consumer, mixFolder, DyeColor.MAGENTA, DyeColor.PURPLE, DyeColor.PINK);
    addDyedBottleMix(consumer, mixFolder, DyeColor.ORANGE, DyeColor.RED, DyeColor.YELLOW);
    addDyedBottleMix(consumer, mixFolder, DyeColor.PINK, DyeColor.RED, DyeColor.WHITE);
    addDyedBottleMix(consumer, mixFolder, DyeColor.PURPLE, DyeColor.BLUE, DyeColor.RED);


    // extra color mixes not supported by vanilla
    addDyedBottleMix(consumer, mixFolder, DyeColor.GREEN, ConfigEnabledCondition.EXTRA_BOTTLE_RECIPES, DyeColor.BLUE, DyeColor.YELLOW);
    addDyedBottleMix(consumer, mixFolder, DyeColor.BROWN, ConfigEnabledCondition.EXTRA_BOTTLE_RECIPES, DyeColor.RED, DyeColor.YELLOW, DyeColor.BLUE);

    // craft vanilla blocks using dyed bottles, as Forge did not reimplement the tags
    // since I have to add recipes, a little more generous with them
    Consumer<FinishedRecipe> bottleConsumer = withCondition(consumer, ConfigEnabledCondition.CAULDRON_DYEING);
    String bottleFolder = folder + "bottle/";
    InspirationsCaudrons.simpleDyedWaterBottle.forEach((dye, bottle) -> {
      String name = dye.getSerializedName();
      CriterionTriggerInstance hasBottle = has(bottle);

      // wool
      addComboRecipe(bottleConsumer, VanillaEnum.WOOL.get(dye), "wool", ItemTags.WOOL, bottle, modResource(bottleFolder + "wool/" + name));
      addSurroundRecipe(bottleConsumer, VanillaEnum.CARPET.get(dye), "carpet", InspirationsTags.Items.CARPETS, bottle, modResource(bottleFolder + "carpet/" + name));
      addComboRecipe(bottleConsumer, VanillaEnum.BED.get(dye), "dyed_bed", ItemTags.BEDS, bottle, modResource(bottleFolder + "beds/" + name));

      // stained glass
      addSurroundRecipe(bottleConsumer, VanillaEnum.STAINED_GLASS.get(dye), "stained_glass", Tags.Items.GLASS_COLORLESS, bottle, modResource(bottleFolder + "stained_glass/" + name));
      addSurroundRecipe(bottleConsumer, VanillaEnum.STAINED_GLASS_PANE.get(dye), "stained_glass_pane", Tags.Items.GLASS_PANES_COLORLESS, bottle, modResource(bottleFolder + "stained_glass_pane/" + name));
      // terracotta
      addSurroundRecipe(bottleConsumer, VanillaEnum.TERRACOTTA.get(dye), "stained_terracotta", InspirationsTags.Items.TERRACOTTA, bottle, modResource(bottleFolder + "terracotta/" + name));
      // concrete powder
      ShapelessRecipeBuilder.shapeless(InspirationsCaudrons.getConcretePowder(dye), 8)
                            .group("concrete_powder")
                            .requires(bottle)
                            .requires(ItemTags.SAND)
                            .requires(ItemTags.SAND)
                            .requires(ItemTags.SAND)
                            .requires(ItemTags.SAND)
                            .requires(Tags.Items.GRAVEL)
                            .requires(Tags.Items.GRAVEL)
                            .requires(Tags.Items.GRAVEL)
                            .requires(Tags.Items.GRAVEL)
                            .unlockedBy("has_item", hasBottle)
                            .save(bottleConsumer, modResource(bottleFolder + "concrete_powder/" + name));
    });

    // use ink bottle for book and quill
    ItemLike blackBottle = InspirationsCaudrons.simpleDyedWaterBottle.get(DyeColor.BLACK);
    ShapelessRecipeBuilder.shapeless(Items.WRITABLE_BOOK)
                          .group(Objects.requireNonNull(Items.WRITABLE_BOOK.getRegistryName()).getPath())
                          .requires(blackBottle)
                          .requires(Tags.Items.FEATHERS)
                          .requires(Items.BOOK)
                          .unlockedBy("has_item", has(blackBottle))
                          .save(bottleConsumer, modResource(bottleFolder + "writable_book"));

    // potions //
    Consumer<FinishedRecipe> potionConsumer = withCondition(consumer, ConfigEnabledCondition.CAULDRON_POTIONS);

    // smith the bottle
    UpgradeRecipeBuilder.smithing(Ingredient.of(Items.GLASS_BOTTLE), Ingredient.of(Tags.Items.GUNPOWDER), InspirationsCaudrons.splashBottle)
                        .unlocks("has_gunpowder", has(Tags.Items.GUNPOWDER))
                        .save(potionConsumer, modPrefix(bottleFolder + "splash_bottle"));
    UpgradeRecipeBuilder.smithing(Ingredient.of(InspirationsTags.Items.SPLASH_BOTTLES), Ingredient.of(Items.DRAGON_BREATH), InspirationsCaudrons.lingeringBottle)
                        .unlocks("has_the_dragon", has(Items.DRAGON_BREATH))
                        .save(potionConsumer, modPrefix(bottleFolder + "lingering_bottle"));

    // normal potato soup crafting
    ShapelessRecipeBuilder.shapeless(InspirationsCaudrons.potatoSoupItem)
                          .requires(Items.BOWL)
                          .requires(Items.BAKED_POTATO)
                          .requires(Items.BAKED_POTATO)
                          .requires(Tags.Items.MUSHROOMS)
                          .unlockedBy("has_item", has(Items.BAKED_POTATO))
                          .save(withCondition(consumer, ConfigEnabledCondition.CAULDRON_SOUPS), modResource(folder + "potato_soup"));
  }

  /**
   * Adds a shapless recipe to mix dyed bottles
   * @param folder          Output folder
   * @param output          Output color
   * @param inputs          List of color inputs
   */
  private void addDyedBottleMix(Consumer<FinishedRecipe> consumer, String folder, DyeColor output, DyeColor... inputs) {
    addDyedBottleMix(consumer, folder, output, null, inputs);
  }

  /**
   * Adds a shapless recipe to mix dyed bottles
   * @param folder          Output folder
   * @param output          Output color
   * @param extraCondition  Additional condition to add to the recipe
   * @param inputs          List of color inputs
   */
  private void addDyedBottleMix(Consumer<FinishedRecipe> baseConsumer, String folder, DyeColor output, @Nullable ICondition extraCondition, DyeColor... inputs) {
    // set serializer to shapeless no container and add conditions
    ConsumerWrapperBuilder consumerBuilder = ConsumerWrapperBuilder
        .wrap(RecipeSerializers.SHAPELESS_NO_CONTAINER)
        .addCondition(ConfigEnabledCondition.CAULDRON_DYEING);
    if (extraCondition != null) {
      consumerBuilder.addCondition(extraCondition);
    }
    Consumer<FinishedRecipe> consumer = consumerBuilder.build(baseConsumer);

    // build recipe name
    StringBuilder name = new StringBuilder(folder + output.getSerializedName() + "_from");
    Item outputItem = InspirationsCaudrons.simpleDyedWaterBottle.get(output);
    ShapelessRecipeBuilder builder = ShapelessRecipeBuilder.shapeless(outputItem, inputs.length)
        .group(Objects.requireNonNull(outputItem.getRegistryName()).toString());
    Set<DyeColor> seen = EnumSet.noneOf(DyeColor.class);
    for (DyeColor input : inputs) {
      ItemLike bottle = InspirationsCaudrons.simpleDyedWaterBottle.get(input);
      builder.requires(bottle);
      // only add each color to the name and criteria once
      if (!seen.contains(input)) {
        builder.unlockedBy("has_" + input.getSerializedName(), has(bottle));
        name.append("_").append(input.getSerializedName());
        seen.add(input);
      }
    }
    // build the recipe with the built name
    builder.save(consumer, modPrefix(name.toString()));
  }

  /**
   * Adds a recipe that surrounds an item with a tag
   * @param consumer  Recipe consumer
   * @param output    Recipe output
   * @param group     Recipe group
   * @param surround  Item for surrounding
   * @param center    Center item
   * @param location  Recipe output location
   */
  private void addSurroundRecipe(Consumer<FinishedRecipe> consumer, ItemLike output, String group, TagKey<Item> surround, ItemLike center, ResourceLocation location) {
    ShapedRecipeBuilder.shaped(output, 8)
                       .group(group)
                       .define('#', surround)
                       .define('x', center)
                       .pattern("###")
                       .pattern("#x#")
                       .pattern("###")
                       .unlockedBy("has_item", has(center))
                       .save(consumer, location);
  }

  /**
   * Adds a recipe that combines two items in a shapeless manner
   * @param consumer  Recipe consumer
   * @param output    Recipe output
   * @param group     Recipe group
   * @param input     Tag like the output
   * @param modifier  Item consumed to modify it
   * @param location  Recipe output location
   */
  private void addComboRecipe(Consumer<FinishedRecipe> consumer, ItemLike output, String group, TagKey<Item> input, ItemLike modifier, ResourceLocation location) {
    ShapelessRecipeBuilder.shapeless(output)
                          .group(group)
                          .requires(input)
                          .requires(modifier)
                          .unlockedBy("has_item", has(modifier))
                          .save(consumer, location);
  }
}
