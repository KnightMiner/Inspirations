package knightminer.inspirations.recipes.data;

import knightminer.inspirations.common.data.ConfigEnabledCondition;
import knightminer.inspirations.common.datagen.IInspirationsRecipeBuilder;
import knightminer.inspirations.library.InspirationsTags;
import knightminer.inspirations.library.recipe.RecipeSerializers;
import knightminer.inspirations.library.recipe.cauldron.CauldronContentTypes;
import knightminer.inspirations.library.recipe.cauldron.CauldronIngredients;
import knightminer.inspirations.library.recipe.cauldron.ingredient.ICauldronIngredient;
import knightminer.inspirations.library.recipe.cauldron.recipe.CauldronRecipeBuilder;
import knightminer.inspirations.library.recipe.cauldron.recipe.CauldronTransformBuilder;
import knightminer.inspirations.library.recipe.cauldron.util.TemperaturePredicate;
import knightminer.inspirations.recipes.InspirationsRecipes;
import knightminer.inspirations.recipes.recipe.cauldron.BrewingCauldronRecipe;
import knightminer.inspirations.recipes.recipe.cauldron.PotionFermentCauldronTransform;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.IConditionBuilder;
import net.minecraftforge.registries.IForgeRegistryEntry;
import slimeknights.mantle.recipe.data.ConsumerWrapperBuilder;
import slimeknights.mantle.recipe.ingredient.SizedIngredient;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

import static knightminer.inspirations.library.recipe.cauldron.recipe.ICauldronRecipe.MAX;

public class RecipesRecipeProvider extends RecipeProvider implements IConditionBuilder, IInspirationsRecipeBuilder {
  private Consumer<FinishedRecipe> consumer;

  public RecipesRecipeProvider(DataGenerator generatorIn) {
    super(generatorIn);
  }


  @Override
  public String getName() {
    return "Inspirations Recipes - Recipes";
  }

  @Override
  public Consumer<FinishedRecipe> getConsumer() {
    return consumer;
  }

  @Override
  public ICondition baseCondition() {
    return ConfigEnabledCondition.MODULE_RECIPES;
  }

  @Override
  protected void buildCraftingRecipes(Consumer<FinishedRecipe> consumer) {
    this.consumer = consumer;
    this.addCauldronRecipes();
  }

  private void addCauldronRecipes() {
    String folder = "cauldron/";

    ICauldronIngredient waterIngredient = CauldronIngredients.FLUID.of(Fluids.WATER);

    // custom recipes //

    // melt ice if boiling, use fancier recipe if cauldron ice is enabled
    String iceFolder = folder + "ice/";
    CauldronRecipeBuilder.cauldron(SizedIngredient.fromItems(Blocks.ICE), waterIngredient)
                         .maxLevels(MAX - 1)
                         .setFull()
                         .setTemperature(TemperaturePredicate.BOILING)
                         .setOutput(CauldronContentTypes.FLUID.of(Fluids.WATER))
                         .setSound(SoundEvents.BUCKET_FILL)
                         .unlockedBy("has_item", has(Blocks.ICE))
                         .save(withCondition(ConfigEnabledCondition.CAULDRON_RECIPES, not(ConfigEnabledCondition.CAULDRON_ICE)), wrapE(Fluids.WATER, iceFolder, "_from_ice"));

    // freeze water into ice when cold
    Consumer<FinishedRecipe> cauldronIce = withCondition(ConfigEnabledCondition.CAULDRON_ICE);
    ResourceLocation ice = Objects.requireNonNull(Blocks.ICE.getRegistryName());
    CauldronTransformBuilder.transform(CauldronIngredients.FLUID.of(Fluids.WATER), CauldronContentTypes.CUSTOM.of(ice), 500)
                            .setTemperature(TemperaturePredicate.FREEZING)
                            .unlockedBy("has_item", has(Items.ICE))
                            .setSound(SoundType.GLASS.getPlaceSound())
                            .save(cauldronIce, resource(iceFolder + "ice_from_water"));
    // freeze wet ice into packed ice
    ResourceLocation wetIce = Objects.requireNonNull(resource("wet_ice"));
    ResourceLocation packedIce = Objects.requireNonNull(Blocks.PACKED_ICE.getRegistryName());
    CauldronTransformBuilder.transform(CauldronIngredients.CUSTOM.of(wetIce), CauldronContentTypes.CUSTOM.of(packedIce), 2000)
                            .setTemperature(TemperaturePredicate.FREEZING)
                            .unlockedBy("has_item", has(Items.PACKED_ICE))
                            .setSound(SoundType.GLASS.getPlaceSound())
                            .save(cauldronIce, resource(iceFolder + "packed_ice_from_wet_ice"));
    // melt back again when hot
    CauldronTransformBuilder.transform(CauldronIngredients.CUSTOM.of(ice), CauldronContentTypes.FLUID.of(Fluids.WATER), 500)
                            .setTemperature(TemperaturePredicate.BOILING)
                            .unlockedBy("has_item", has(Items.ICE))
                            .setSound(SoundEvents.BUCKET_EMPTY)
                            .save(cauldronIce, resource(iceFolder + "water_from_ice_melting"));
    CauldronTransformBuilder.transform(CauldronIngredients.CUSTOM.of(wetIce), CauldronContentTypes.FLUID.of(Fluids.WATER), 500)
                            .setTemperature(TemperaturePredicate.BOILING)
                            .unlockedBy("has_item", has(Items.ICE))
                            .setSound(SoundType.GLASS.getPlaceSound())
                            .save(cauldronIce, resource(iceFolder + "water_from_wet_ice"));
    CauldronTransformBuilder.transform(CauldronIngredients.CUSTOM.of(packedIce), CauldronContentTypes.CUSTOM.of(wetIce), 2000)
                            .setTemperature(TemperaturePredicate.BOILING)
                            .unlockedBy("has_item", has(Items.PACKED_ICE))
                            .setSound(SoundType.GLASS.getPlaceSound())
                            .save(cauldronIce, resource(iceFolder + "wet_ice_from_packed_ice"));

    // fill and empty ice
    CauldronRecipeBuilder.cauldron(CauldronIngredients.CUSTOM.of(ice))
                         .matchFull()
                         .setEmpty()
                         .setOutput(Blocks.ICE)
                         .setSound(SoundType.GLASS.getBreakSound())
                         .unlockedBy("has_item", has(Items.ICE))
                         .save(cauldronIce, resource(iceFolder + "pickup_ice"));
    CauldronRecipeBuilder.cauldron(SizedIngredient.fromItems(Blocks.ICE), CauldronIngredients.CUSTOM.of(ice))
                         .matchEmpty()
                         .setFull()
                         .setOutput(CauldronContentTypes.CUSTOM.of(ice))
                         .setSound(SoundType.GLASS.getPlaceSound())
                         .unlockedBy("has_item", has(Items.ICE))
                         .save(cauldronIce, resource(iceFolder + "place_ice"));
    // fill and empty wet ice
    CauldronRecipeBuilder.cauldron(CauldronIngredients.CUSTOM.of(wetIce))
                         .matchFull()
                         .setOutput(Blocks.ICE)
                         .setOutput(CauldronContentTypes.FLUID.of(Fluids.WATER))
                         .setSound(SoundType.GLASS.getBreakSound())
                         .unlockedBy("has_item", has(Items.ICE))
                         .save(cauldronIce, resource(iceFolder + "pickup_wet_ice"));
    CauldronRecipeBuilder.cauldron(SizedIngredient.fromItems(Blocks.ICE), CauldronIngredients.FLUID.of(Fluids.WATER))
                         .matchFull()
                         .setOutput(CauldronContentTypes.CUSTOM.of(wetIce))
                         .setSound(SoundType.GLASS.getPlaceSound())
                         .unlockedBy("has_item", has(Items.ICE))
                         .save(cauldronIce, resource(iceFolder + "place_wet_ice"));
    // fill and empty packed ice
    CauldronRecipeBuilder.cauldron(CauldronIngredients.CUSTOM.of(packedIce))
                         .matchFull()
                         .setEmpty()
                         .setOutput(Blocks.PACKED_ICE)
                         .setSound(SoundType.GLASS.getBreakSound())
                         .unlockedBy("has_item", has(Items.PACKED_ICE))
                         .save(cauldronIce, resource(iceFolder + "pickup_packed_ice"));
    CauldronRecipeBuilder.cauldron(SizedIngredient.fromItems(Blocks.PACKED_ICE), CauldronIngredients.CUSTOM.of(packedIce))
                         .matchEmpty()
                         .setFull()
                         .setOutput(CauldronContentTypes.CUSTOM.of(packedIce))
                         .setSound(SoundType.GLASS.getPlaceSound())
                         .unlockedBy("has_item", has(Items.ICE))
                         .save(cauldronIce, resource(iceFolder + "place_packed_ice"));


    // dyes //

    // mix dyed bottles
    String mixFolder = folder + "bottle/mix/";
    addDyedBottleMix(mixFolder, DyeColor.CYAN, DyeColor.BLUE, DyeColor.GREEN);
    addDyedBottleMix(mixFolder, DyeColor.GRAY, DyeColor.BLACK, DyeColor.WHITE);
    addDyedBottleMix(mixFolder, DyeColor.LIGHT_BLUE, DyeColor.BLUE, DyeColor.WHITE);
    addDyedBottleMix(mixFolder, DyeColor.LIGHT_GRAY, DyeColor.BLACK, DyeColor.WHITE, DyeColor.WHITE);
    addDyedBottleMix(mixFolder, DyeColor.LIGHT_GRAY, DyeColor.GRAY, DyeColor.WHITE);
    addDyedBottleMix(mixFolder, DyeColor.LIME, DyeColor.GREEN, DyeColor.WHITE);
    addDyedBottleMix(mixFolder, DyeColor.MAGENTA, DyeColor.BLUE, DyeColor.RED, DyeColor.PINK);
    addDyedBottleMix(mixFolder, DyeColor.MAGENTA, DyeColor.BLUE, DyeColor.RED, DyeColor.RED, DyeColor.WHITE);
    addDyedBottleMix(mixFolder, DyeColor.MAGENTA, DyeColor.PURPLE, DyeColor.PINK);
    addDyedBottleMix(mixFolder, DyeColor.ORANGE, DyeColor.RED, DyeColor.YELLOW);
    addDyedBottleMix(mixFolder, DyeColor.PINK, DyeColor.RED, DyeColor.WHITE);
    addDyedBottleMix(mixFolder, DyeColor.PURPLE, DyeColor.BLUE, DyeColor.RED);


    // extra color mixes not supported by vanilla
    //addDyedBottleMix(mixFolder, DyeColor.GREEN, ConfigEnabledCondition.EXTRA_DYE_RECIPES, DyeColor.BLUE, DyeColor.YELLOW);
    //addDyedBottleMix(mixFolder, DyeColor.GREEN, ConfigEnabledCondition.EXTRA_DYE_RECIPES, DyeColor.BLUE, DyeColor.YELLOW);

    // craft vanilla blocks using dyed bottles, as Forge did not reimplement the tags
    // since I have to add recipes, a little more generous with them
    Consumer<FinishedRecipe> bottleConsumer = withCondition(ConfigEnabledCondition.CAULDRON_DYEING);
    String bottleFolder = folder + "bottle/";
    InspirationsRecipes.simpleDyedWaterBottle.forEach((dye, bottle) -> {
      String name = dye.getSerializedName();
      CriterionTriggerInstance hasBottle = has(bottle);

      // wool
      addComboRecipe(bottleConsumer, VanillaEnum.WOOL.get(dye), "wool", ItemTags.WOOL, bottle, resource(bottleFolder + "wool/" + name));
      addSurroundRecipe(bottleConsumer, VanillaEnum.CARPET.get(dye), "carpet", InspirationsTags.Items.CARPETS, bottle, resource(bottleFolder + "carpet/" + name));
      addComboRecipe(bottleConsumer, VanillaEnum.BED.get(dye), "dyed_bed", ItemTags.BEDS, bottle, resource(bottleFolder + "beds/" + name));

      // stained glass
      addSurroundRecipe(bottleConsumer, VanillaEnum.STAINED_GLASS.get(dye), "stained_glass", Tags.Items.GLASS_COLORLESS, bottle, resource(bottleFolder + "stained_glass/" + name));
      addSurroundRecipe(bottleConsumer, VanillaEnum.STAINED_GLASS_PANE.get(dye), "stained_glass_pane", Tags.Items.GLASS_PANES_COLORLESS, bottle, resource(bottleFolder + "stained_glass_pane/" + name));
      // terracotta
      addSurroundRecipe(bottleConsumer, VanillaEnum.TERRACOTTA.get(dye), "stained_terracotta", InspirationsTags.Items.TERRACOTTA, bottle, resource(bottleFolder + "terracotta/" + name));
      // concrete powder
      ShapelessRecipeBuilder.shapeless(InspirationsRecipes.getConcretePowder(dye), 8)
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
                            .save(bottleConsumer, resource(bottleFolder + "concrete_powder/" + name));
    });

    // use ink bottle for book and quill
    ItemLike blackBottle = InspirationsRecipes.simpleDyedWaterBottle.get(DyeColor.BLACK);
    ShapelessRecipeBuilder.shapeless(Items.WRITABLE_BOOK)
                          .group(Objects.requireNonNull(Items.WRITABLE_BOOK.getRegistryName()).getPath())
                          .requires(blackBottle)
                          .requires(Tags.Items.FEATHERS)
                          .requires(Items.BOOK)
                          .unlockedBy("has_item", has(blackBottle))
                          .save(bottleConsumer, resource(bottleFolder + "writable_book"));

    // potions //
    String potionFolder = folder + "potion/";
    Consumer<FinishedRecipe> potionConsumer = withCondition(ConfigEnabledCondition.CAULDRON_POTIONS);

    // craft the bottles
    // TODO: brew the bottles
    ShapelessRecipeBuilder.shapeless(InspirationsRecipes.splashBottle)
                          .requires(Items.GLASS_BOTTLE)
                          .requires(Items.GLASS_BOTTLE)
                          .requires(Items.GLASS_BOTTLE)
                          .requires(Tags.Items.GUNPOWDER)
                          .unlockedBy("has_item", has(Tags.Items.GUNPOWDER))
                          .save(potionConsumer, prefix(InspirationsRecipes.splashBottle, potionFolder));
    ShapelessRecipeBuilder.shapeless(InspirationsRecipes.lingeringBottle)
                          .requires(InspirationsTags.Items.SPLASH_BOTTLES)
                          .requires(InspirationsTags.Items.SPLASH_BOTTLES)
                          .requires(InspirationsTags.Items.SPLASH_BOTTLES)
                          .requires(Items.DRAGON_BREATH)
                          .unlockedBy("has_item", has(Items.DRAGON_BREATH))
                          .save(potionConsumer, prefix(InspirationsRecipes.lingeringBottle, potionFolder));

    // brew the potions
    Consumer<FinishedRecipe> brewingConsumer = withCondition(ConfigEnabledCondition.CAULDRON_BREWING);
    brewingConsumer.accept(new BrewingCauldronRecipe.FinishedRecipe(resource(potionFolder + "potion_brewing"), RecipeSerializers.CAULDRON_POTION_BREWING, false));
    brewingConsumer.accept(new BrewingCauldronRecipe.FinishedRecipe(resource(potionFolder + "forge_brewing"), RecipeSerializers.CAULDRON_FORGE_BREWING, false));
    brewingConsumer.accept(new PotionFermentCauldronTransform.FinishedRecipe(resource(potionFolder + "potion_ferment"), 600));

    // normal potato soup crafting
    ShapelessRecipeBuilder.shapeless(InspirationsRecipes.potatoSoupItem)
                          .requires(Items.BOWL)
                          .requires(Items.BAKED_POTATO)
                          .requires(Items.BAKED_POTATO)
                          .requires(Tags.Items.MUSHROOMS)
                          .unlockedBy("has_item", has(Items.BAKED_POTATO))
                          .save(withCondition(), resource(folder + "potato_soup/item"));
  }

  /**
   * Adds a shapless recipe to mix dyed bottles
   * @param folder          Output folder
   * @param output          Output color
   * @param inputs          List of color inputs
   */
  private void addDyedBottleMix(String folder, DyeColor output, DyeColor... inputs) {
    addDyedBottleMix(folder, output, null, inputs);
  }

  /**
   * Adds a shapless recipe to mix dyed bottles
   * @param folder          Output folder
   * @param output          Output color
   * @param extraCondition  Additional condition to add to the recipe
   * @param inputs          List of color inputs
   */
  private void addDyedBottleMix(String folder, DyeColor output, @Nullable ICondition extraCondition, DyeColor... inputs) {
    // set serializer to shapeless no container and add conditions
    ConsumerWrapperBuilder consumerBuilder = ConsumerWrapperBuilder
        .wrap(RecipeSerializers.SHAPELESS_NO_CONTAINER)
        .addCondition(ConfigEnabledCondition.CAULDRON_DYEING);
    if (extraCondition != null) {
      consumerBuilder.addCondition(extraCondition);
    }
    Consumer<FinishedRecipe> consumer = consumerBuilder.build(getConsumer());

    // build recipe name
    StringBuilder name = new StringBuilder(folder + output.getSerializedName() + "_from");
    Item outputItem = InspirationsRecipes.simpleDyedWaterBottle.get(output);
    ShapelessRecipeBuilder builder = ShapelessRecipeBuilder.shapeless(outputItem, inputs.length)
        .group(Objects.requireNonNull(outputItem.getRegistryName()).toString());
    Set<DyeColor> seen = EnumSet.noneOf(DyeColor.class);
    for (DyeColor input : inputs) {
      ItemLike bottle = InspirationsRecipes.simpleDyedWaterBottle.get(input);
      builder.requires(bottle);
      // only add each color to the name and criteria once
      if (!seen.contains(input)) {
        builder.unlockedBy("has_" + input.getSerializedName(), has(bottle));
        name.append("_").append(input.getSerializedName());
        seen.add(input);
      }
    }
    // build the recipe with the built name
    builder.save(consumer, resourceName(name.toString()));
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

  private ResourceLocation prefixE(IForgeRegistryEntry<?> entry, String prefix) {
    return this.resource(prefix + Objects.requireNonNull(entry.getRegistryName()).getPath());
  }

  private ResourceLocation wrapE(IForgeRegistryEntry<?> entry, String prefix, String suffix) {
    return this.resource(prefix + Objects.requireNonNull(entry.getRegistryName()).getPath() + suffix);
  }
}
