package knightminer.inspirations.recipes.data;

import knightminer.inspirations.common.data.ConfigEnabledCondition;
import knightminer.inspirations.common.datagen.IInspirationsRecipeBuilder;
import knightminer.inspirations.common.datagen.NBTIngredient;
import knightminer.inspirations.library.InspirationsTags;
import knightminer.inspirations.library.recipe.cauldron.CauldronContentTypes;
import knightminer.inspirations.library.recipe.cauldron.CauldronIngredients;
import knightminer.inspirations.library.recipe.cauldron.contents.ICauldronContents;
import knightminer.inspirations.library.recipe.cauldron.ingredient.ContentMatchIngredient;
import knightminer.inspirations.library.recipe.cauldron.ingredient.ContentTypeIngredient;
import knightminer.inspirations.library.recipe.cauldron.ingredient.FluidCauldronIngredient;
import knightminer.inspirations.library.recipe.cauldron.ingredient.ICauldronIngredient;
import knightminer.inspirations.library.recipe.cauldron.recipe.CauldronRecipeBuilder;
import knightminer.inspirations.library.recipe.cauldron.util.TemperaturePredicate;
import knightminer.inspirations.recipes.InspirationsRecipes;
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

    ICauldronIngredient waterIngredient = ContentTypeIngredient.of(CauldronContentTypes.WATER);
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
                         .setOutput(CauldronContentTypes.WATER.get())
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

    // undye and dye vanilla blocks
    addColoredRecipes(ItemTags.WOOL, VanillaEnum.WOOL, folder + "wool/", null);
    addColoredRecipes(ItemTags.BEDS, VanillaEnum.BED, folder + "bed/", null);
    addColoredRecipes(ItemTags.CARPETS, InspirationsShared.VANILLA_CARPETS, folder + "carpet/", null);
    // Inspirations blocks
    addColoredRecipes(InspirationsTags.Items.CARPETED_TRAPDOORS, InspirationsUtility.carpetedTrapdoors, folder + "carpeted_trapdoor/", ConfigEnabledCondition.CARPETED_TRAPDOOR);
    // TODO: armor dyeing
    // TODO: banners
    // TODO: shulker boxes


    // potions //
    // TODO: potion bottling
    // TODO: tipped arrow recipe (might be same internal logic, just uses a stack of 16)

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
    CauldronRecipeBuilder.cauldron(ingredient, ContentTypeIngredient.of(CauldronContentTypes.WATER))
                         .minLevels(1)
                         .addLevels(-1)
                         .setOutput(enumObject.get(DyeColor.WHITE))
                         .addCriterion("has_item", criteria)
                         .build(undyed, resource(folder + "undye"));

    // dyed recipes need one more condition
    enumObject.forEach((color, block) ->
      CauldronRecipeBuilder.cauldron(ingredient, ContentMatchIngredient.of(CauldronIngredients.DYE, color))
                           .minLevels(1)
                           .addLevels(-1)
                           .setOutput(block)
                           .addCriterion("has_item", criteria)
                           .build(dyed, resource(folder + color.getString())));
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
