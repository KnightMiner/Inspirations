package knightminer.inspirations.recipes.data;

import knightminer.inspirations.common.data.ConfigEnabledCondition;
import knightminer.inspirations.common.datagen.IInspirationsRecipeBuilder;
import knightminer.inspirations.common.datagen.NBTIngredient;
import knightminer.inspirations.library.InspirationsTags;
import knightminer.inspirations.library.recipe.cauldron.CauldronContentTypes;
import knightminer.inspirations.library.recipe.cauldron.CauldronIngredients;
import knightminer.inspirations.library.recipe.cauldron.contents.ICauldronContents;
import knightminer.inspirations.library.recipe.cauldron.ingredient.ContentMatchIngredient;
import knightminer.inspirations.library.recipe.cauldron.ingredient.FluidCauldronIngredient;
import knightminer.inspirations.library.recipe.cauldron.ingredient.ICauldronIngredient;
import knightminer.inspirations.library.recipe.cauldron.recipe.CauldronRecipeBuilder;
import knightminer.inspirations.library.recipe.cauldron.recipe.DyeableCauldronRecipe;
import knightminer.inspirations.library.recipe.cauldron.recipe.EmptyPotionCauldronRecipeBuilder;
import knightminer.inspirations.library.recipe.cauldron.recipe.FillPotionCauldronRecipeBuilder;
import knightminer.inspirations.library.recipe.cauldron.util.TemperaturePredicate;
import knightminer.inspirations.recipes.InspirationsRecipes;
import knightminer.inspirations.recipes.recipe.cauldron.DyeCauldronWaterRecipe;
import knightminer.inspirations.recipes.recipe.cauldron.MixCauldronDyeRecipe;
import knightminer.inspirations.shared.InspirationsShared;
import knightminer.inspirations.utility.InspirationsUtility;
import net.minecraft.advancements.ICriterionInstance;
import net.minecraft.advancements.criterion.ItemPredicate;
import net.minecraft.block.Blocks;
import net.minecraft.data.CustomRecipeBuilder;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.RecipeProvider;
import net.minecraft.data.ShapelessRecipeBuilder;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.DyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.potion.PotionUtils;
import net.minecraft.potion.Potions;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.IConditionBuilder;
import net.minecraftforge.registries.IForgeRegistryEntry;
import slimeknights.mantle.registration.object.EnumObject;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.function.Consumer;

public class RecipesRecipeProvider extends RecipeProvider implements IConditionBuilder, IInspirationsRecipeBuilder {
  private Consumer<IFinishedRecipe> consumer;

  public RecipesRecipeProvider(DataGenerator generatorIn) {
    super(generatorIn);
  }


  @Override
  public String getName() {
    return "Inspirations Recipes - Recipes";
  }

  @Override
  public Consumer<IFinishedRecipe> getConsumer() {
    return consumer;
  }

  @Override
  public ICondition baseCondition() {
    return ConfigEnabledCondition.MODULE_RECIPES;
  }

  @Override
  protected void registerRecipes(Consumer<IFinishedRecipe> consumer) {
    this.consumer = consumer;
    String folder = "recipes/cauldron/";

    ICauldronIngredient waterIngredient = FluidCauldronIngredient.of(Fluids.WATER);
    Consumer<IFinishedRecipe> cauldronRecipes = withCondition(ConfigEnabledCondition.CAULDRON_RECIPES);

    // vanilla recipes //

    // fill containers
    // bucket
    CustomRecipeBuilder.customRecipe(InspirationsRecipes.fillBucketSerializer)
                       .build(withCondition(ConfigEnabledCondition.CAULDRON_RECIPES), resourceName(folder + "fill_bucket"));
    // glass bottles
    ItemStack waterBottle = PotionUtils.addPotionToItemStack(new ItemStack(Items.POTION), Potions.WATER);
    CauldronRecipeBuilder.cauldron(Ingredient.fromItems(Items.GLASS_BOTTLE), waterIngredient)
                         .minLevels(1)
                         .addLevels(-1)
                         .setOutput(waterBottle.copy())
                         .addCriterion("has_item", hasItem(Items.GLASS_BOTTLE))
                         .build(cauldronRecipes, resource(folder + "fill_water_bottle"));

    // empty container
    // bucket
    CustomRecipeBuilder.customRecipe(InspirationsRecipes.emptyBucketSerializer)
                       .build(cauldronRecipes, resourceName(folder + "empty_bucket"));
    // water bottle
    CauldronRecipeBuilder.cauldron(new NBTIngredient(waterBottle), waterIngredient)
                         .maxLevels(2)
                         .addLevels(1)
                         .setOutput(Items.GLASS_BOTTLE)
                         .setOutput(CauldronContentTypes.FLUID.of(Fluids.WATER))
                         .noContainer()
                         .addCriterion("has_item", hasItem(
                             ItemPredicate.Builder.create()
                                                  .item(Items.POTION)
                                                  .nbt(Objects.requireNonNull(waterBottle.getTag()))
                                                  .build()))
                         .build(cauldronRecipes, resource(folder + "empty_water_bottle"));

    // TODO: remove dye recipe
    // TODO: remove banner pattern
    // TODO: clean shulker box


    // custom recipes //

    // sponge dry cauldron
    CauldronRecipeBuilder.cauldron(Ingredient.fromItems(Blocks.SPONGE), waterIngredient)
                         .minLevels(1)
                         .setEmpty()
                         .setOutput(Blocks.WET_SPONGE)
                         .addCriterion("has_item", hasItem(Blocks.SPONGE))
                         .build(cauldronRecipes, resource(folder + "dry_cauldron"));

    // melt ice if boiling
    CauldronRecipeBuilder.cauldron(Ingredient.fromItems(Blocks.ICE), waterIngredient)
                         .maxLevels(2)
                         .setFull()
                         .setOutput(CauldronContentTypes.FLUID.of(Fluids.WATER))
                         .addCriterion("has_item", hasItem(Blocks.ICE))
                         .build(cauldronRecipes, wrapE(Fluids.WATER, folder, "_from_ice"));

    // clean sticky pistons
    CauldronRecipeBuilder.cauldron(Ingredient.fromItems(Blocks.STICKY_PISTON), waterIngredient)
                         .minLevels(1)
                         .addLevels(-1)
                         .setOutput(Blocks.PISTON)
                         .addCriterion("has_item", hasItem(Blocks.STICKY_PISTON))
                         .build(cauldronRecipes, wrap(Blocks.STICKY_PISTON, folder, "_cleaning"));

    // concrete powder
    String concreteFolder = folder + "concrete/";
    Consumer<IFinishedRecipe> concrete = withCondition(ConfigEnabledCondition.CAULDRON_CONCRETE);
    VanillaEnum.CONCRETE_POWDER.forEach((color, powder) -> {
      CauldronRecipeBuilder.cauldron(Ingredient.fromItems(powder), waterIngredient)
                           .minLevels(1)
                           .addLevels(-1)
                           .setOutput(VanillaEnum.CONCRETE.get(color))
                           .addCriterion("has_item", hasItem(powder))
                           .build(concrete, resource(concreteFolder + color.getString()));
    });


    // dyes //

    // dye cauldron water
    String dyeFolder = folder + "dye/";
    Consumer<IFinishedRecipe> dyeConsumer = withCondition(ConfigEnabledCondition.CAULDRON_DYEING);
    for (DyeColor color : DyeColor.values()) {
      // normal dye to set color
      dyeConsumer.accept(new DyeCauldronWaterRecipe.FinishedRecipe(resource(dyeFolder + "dye_" + color.getString()), color));
      // dyed bottle to mix color
      dyeConsumer.accept(new MixCauldronDyeRecipe.FinishedRecipe(
          resource(dyeFolder + "bottle_" + color.getString()),
          Ingredient.fromItems(InspirationsRecipes.simpleDyedWaterBottle.get(color)),
          color.getColorValue()));
    }
    // mixed dyed bottle
    dyeConsumer.accept(new MixCauldronDyeRecipe.FinishedRecipe(resource(dyeFolder + "bottle_mixed"), Ingredient.fromItems(InspirationsRecipes.mixedDyedWaterBottle)));
    // fill dyed bottle
    CustomRecipeBuilder.customRecipe(InspirationsRecipes.fillDyedBottleSerializer).build(dyeConsumer, resourceName(dyeFolder + "fill_bottle"));

    // undye and dye vanilla blocks
    addColoredRecipes(ItemTags.WOOL, VanillaEnum.WOOL, folder + "wool/", null);
    addColoredRecipes(ItemTags.BEDS, VanillaEnum.BED, folder + "bed/", null);
    addColoredRecipes(ItemTags.CARPETS, InspirationsShared.VANILLA_CARPETS, folder + "carpet/", null);
    addColoredRecipes(InspirationsTags.Items.SHULKER_BOXES, VanillaEnum.SHULKER_BOX, Items.SHULKER_BOX, folder + "shulker_box/", true, null);
    // Inspirations blocks
    addColoredRecipes(InspirationsTags.Items.CARPETED_TRAPDOORS, InspirationsUtility.carpetedTrapdoors, folder + "carpeted_trapdoor/", ConfigEnabledCondition.CARPETED_TRAPDOOR);

    // leather dyeing and clearing
    addDyeableRecipes(Items.LEATHER_HELMET, folder);
    addDyeableRecipes(Items.LEATHER_CHESTPLATE, folder);
    addDyeableRecipes(Items.LEATHER_LEGGINGS, folder);
    addDyeableRecipes(Items.LEATHER_BOOTS, folder);
    addDyeableRecipes(Items.LEATHER_HORSE_ARMOR, folder);

    // TODO: banners


    // potions //
    String potionFolder = folder + "potion/";
    Consumer<IFinishedRecipe> potionConsumer = withCondition(ConfigEnabledCondition.CAULDRON_POTIONS);
    // normal
    EmptyPotionCauldronRecipeBuilder.empty(Items.POTION, Items.GLASS_BOTTLE)
                                    .addCriterion("has_item", hasItem(Items.POTION))
                                    .build(potionConsumer, resource(potionFolder + "normal_empty"));
    FillPotionCauldronRecipeBuilder.fill(Ingredient.fromItems(Items.GLASS_BOTTLE), Items.POTION)
                                   .addCriterion("has_item", hasItem(Items.GLASS_BOTTLE))
                                   .build(potionConsumer, resource(potionFolder + "normal_fill"));
    // splash
    EmptyPotionCauldronRecipeBuilder.empty(Items.SPLASH_POTION, InspirationsRecipes.splashBottle)
                                    .addCriterion("has_item", hasItem(Items.SPLASH_POTION))
                                    .build(potionConsumer, resource(potionFolder + "splash_empty"));
    FillPotionCauldronRecipeBuilder.fill(Ingredient.fromTag(InspirationsTags.Items.SPLASH_BOTTLES), Items.SPLASH_POTION)
                                   .addCriterion("has_item", hasItem(InspirationsTags.Items.SPLASH_BOTTLES))
                                   .build(potionConsumer, resource(potionFolder + "splash_fill"));
    // lingering
    EmptyPotionCauldronRecipeBuilder.empty(Items.LINGERING_POTION, InspirationsRecipes.lingeringBottle)
                                    .addCriterion("has_item", hasItem(Items.LINGERING_POTION))
                                    .build(potionConsumer, resource(potionFolder + "lingering_empty"));
    FillPotionCauldronRecipeBuilder.fill(Ingredient.fromTag(InspirationsTags.Items.LINGERING_BOTTLES), Items.LINGERING_POTION)
                                   .addCriterion("has_item", hasItem(InspirationsTags.Items.LINGERING_BOTTLES))
                                   .build(potionConsumer, resource(potionFolder + "lingering_fill"));
    // tipped arrows
    FillPotionCauldronRecipeBuilder.fill(Ingredient.fromItems(Items.ARROW), 16, Items.TIPPED_ARROW)
                                   .addCriterion("has_item", hasItem(Items.ARROW))
                                   .build(potionConsumer, resource(potionFolder + "tipped_arrow"));

    // craft the bottles
    ShapelessRecipeBuilder.shapelessRecipe(InspirationsRecipes.splashBottle)
                          .addIngredient(Items.GLASS_BOTTLE)
                          .addIngredient(Items.GLASS_BOTTLE)
                          .addIngredient(Items.GLASS_BOTTLE)
                          .addIngredient(Tags.Items.GUNPOWDER)
                          .addCriterion("has_item", hasItem(Tags.Items.GUNPOWDER))
                          .build(potionConsumer, prefix(InspirationsRecipes.splashBottle, potionFolder));
    ShapelessRecipeBuilder.shapelessRecipe(InspirationsRecipes.lingeringBottle)
                          .addIngredient(InspirationsTags.Items.SPLASH_BOTTLES)
                          .addIngredient(InspirationsTags.Items.SPLASH_BOTTLES)
                          .addIngredient(InspirationsTags.Items.SPLASH_BOTTLES)
                          .addIngredient(Items.DRAGON_BREATH)
                          .addCriterion("has_item", hasItem(Items.DRAGON_BREATH))
                          .build(potionConsumer, prefix(InspirationsRecipes.lingeringBottle, potionFolder));

    // TODO: potion brewing

    // fluid recipes //
    // beetroot is just water based
    addStewRecipe(Items.BEETROOT_SOUP, Ingredient.fromTag(Tags.Items.CROPS_BEETROOT), 6, InspirationsRecipes.beetrootSoup, waterIngredient, folder + "beetroot_soup/");
    // mushroom water based
    addStewRecipe(Items.MUSHROOM_STEW, Ingredient.fromTag(Tags.Items.MUSHROOMS), 2, InspirationsRecipes.mushroomStew, waterIngredient, folder + "mushroom_stew/");
    // potato comes from mushroom
    addStewRecipe(InspirationsRecipes.potatoSoupItem, Ingredient.fromItems(Items.BAKED_POTATO), 2, InspirationsRecipes.potatoSoup, FluidCauldronIngredient.of(InspirationsRecipes.mushroomStew), folder + "potato_soup/");
    // add in some rabbit for rabbit stew
    addStewRecipe(Items.RABBIT_STEW, Ingredient.fromItems(Items.RABBIT_STEW), 1, InspirationsRecipes.rabbitStew, FluidCauldronIngredient.of(InspirationsRecipes.potatoSoup), folder + "rabbit_stew/");

    // normal potato soup crafting
    ShapelessRecipeBuilder.shapelessRecipe(InspirationsRecipes.potatoSoupItem)
                          .addIngredient(Items.BOWL)
                          .addIngredient(Items.BAKED_POTATO)
                          .addIngredient(Items.BAKED_POTATO)
                          .addIngredient(Tags.Items.MUSHROOMS)
                          .addCriterion("has_item", hasItem(Items.BAKED_POTATO))
                          .build(withCondition(), resource(folder + "potato_soup/item"));
  }

  /**
   * Adds cauldron recipes to dye and undye colored blocks
   * @param tag         Tag for colored block
   * @param enumObject  Object of items
   * @param folder      Folder for output
   * @param condition   Extra condition to add, if null uses base conditions
   */
  private void addColoredRecipes(ITag<Item> tag, EnumObject<DyeColor,? extends IItemProvider> enumObject, String folder, @Nullable ICondition condition) {
    addColoredRecipes(tag, enumObject, enumObject.get(DyeColor.WHITE), folder, false, condition);
  }

  /**
   * Adds cauldron recipes to dye and undye colored blocks
   * @param tag         Tag for colored block
   * @param enumObject  Object of items
   * @param folder      Folder for output
   * @param undyedItem  Undyed variant
   * @param copyNBT     If true, copies NBT
   * @param condition   Extra condition to add, if null uses base conditions
   */
  private void addColoredRecipes(ITag<Item> tag, EnumObject<DyeColor,? extends IItemProvider> enumObject, IItemProvider undyedItem, String folder, boolean copyNBT, @Nullable ICondition condition) {
    // add condition if present
    Consumer<IFinishedRecipe> dyed, undyed;
    if (condition == null) {
      undyed = withCondition(ConfigEnabledCondition.CAULDRON_RECIPES);
      dyed = withCondition(ConfigEnabledCondition.CAULDRON_DYEING);
    } else {
      undyed = withCondition(ConfigEnabledCondition.CAULDRON_RECIPES, condition);
      dyed = withCondition(ConfigEnabledCondition.CAULDRON_DYEING, condition);
    }

    // undyed
    Ingredient ingredient = Ingredient.fromTag(tag);
    ICriterionInstance criteria = hasItem(tag);
    CauldronRecipeBuilder undyedBuilder = CauldronRecipeBuilder
        .cauldron(ingredient, FluidCauldronIngredient.of(Fluids.WATER))
        .minLevels(1)
        .addLevels(-1)
        .setOutput(undyedItem)
        .addCriterion("has_item", criteria);
    if (copyNBT) {
      undyedBuilder.setCopyNBT();
    }
    undyedBuilder.build(undyed, resource(folder + "undye"));

    // dyed recipes need one more condition
    enumObject.forEach((color, block) -> {
      CauldronRecipeBuilder coloredBuilder = CauldronRecipeBuilder
          .cauldron(ingredient, ContentMatchIngredient.of(CauldronIngredients.DYE, color))
          .minLevels(1)
          .addLevels(-1)
          .setOutput(block)
          .addCriterion("has_item", criteria);
      if (copyNBT) {
        coloredBuilder.setCopyNBT();
      }
      coloredBuilder.build(dyed, resource(folder + color.getString()));
    });
  }

  /**
   * Adds recipes to dye and clear a dyeable item
   * @param dyeable  Dyeable item
   * @param folder   Folder for recipes
   */
  private void addDyeableRecipes(IItemProvider dyeable, String folder) {
    Ingredient ingredient = Ingredient.fromItems(dyeable);
    withCondition(ConfigEnabledCondition.CAULDRON_RECIPES)
        .accept(DyeableCauldronRecipe.FinishedRecipe.clear(prefix(dyeable, folder + "dyeable/clear_"), ingredient));
    withCondition(ConfigEnabledCondition.CAULDRON_DYEING)
        .accept(DyeableCauldronRecipe.FinishedRecipe.dye(prefix(dyeable, folder + "dyeable/dye_"), ingredient));
  }

  /**
   * Adds recipes for making stew
   * @param stewItem    Item of filled stew
   * @param ingredient  Ingredient to make stew
   * @param amount      Amount of stew ingredient
   * @param stewFluid   Fluid for making stew
   * @param base        Base fluid for stew
   * @param folder      Folder for recipes
   */
  private void addStewRecipe(IItemProvider stewItem, Ingredient ingredient, int amount, Fluid stewFluid, ICauldronIngredient base, String folder) {
    Consumer<IFinishedRecipe> consumer = withCondition(ConfigEnabledCondition.CAULDRON_FLUIDS);
    ICauldronIngredient stewIngredient = FluidCauldronIngredient.of(stewFluid);
    ICauldronContents stewContents = CauldronContentTypes.FLUID.of(stewFluid);

    // fill the bowl
    CauldronRecipeBuilder.cauldron(Ingredient.fromItems(Items.BOWL), stewIngredient)
                         .minLevels(1)
                         .addLevels(-1)
                         .setOutput(stewItem)
                         .addCriterion("has_item", hasItem(Items.BOWL))
                         .build(consumer, resource(folder + "fill_bowl"));

    // empty the bowl
    CauldronRecipeBuilder.cauldron(Ingredient.fromItems(stewItem), stewIngredient)
                         .maxLevels(2)
                         .addLevels(1)
                         .setOutput(Items.BOWL)
                         .setOutput(stewContents)
                         .noContainer()
                         .addCriterion("has_item", hasItem(stewItem))
                         .build(consumer, resource(folder + "empty_bowl"));

    // if the cauldron has 1 level, takes 1 item. If it has 2 or 3, take twice as many
    ICriterionInstance criteria = hasItem(Blocks.CAULDRON);
    CauldronRecipeBuilder.cauldron(ingredient, amount, base)
                         .levelRange(1, 1)
                         .setTemperature(TemperaturePredicate.HOT)
                         .setOutput(stewContents)
                         .addCriterion("has_item", criteria)
                         .build(consumer, resource(folder + "stew_small"));
    CauldronRecipeBuilder.cauldron(ingredient, amount * 2, base)
                         .minLevels(2)
                         .setTemperature(TemperaturePredicate.HOT)
                         .setOutput(stewContents)
                         .addCriterion("has_item", criteria)
                         .build(consumer, resource(folder + "stew_large"));
  }

  private ResourceLocation prefixE(IForgeRegistryEntry<?> entry, String prefix) {
    return this.resource(prefix + Objects.requireNonNull(entry.getRegistryName()).getPath());
  }

  private ResourceLocation wrapE(IForgeRegistryEntry<?> entry, String prefix, String suffix) {
    return this.resource(prefix + Objects.requireNonNull(entry.getRegistryName()).getPath() + suffix);
  }
}
