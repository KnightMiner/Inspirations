package knightminer.inspirations.recipes.data;

import knightminer.inspirations.common.data.ConfigEnabledCondition;
import knightminer.inspirations.common.datagen.AnvilRecipeBuilder;
import knightminer.inspirations.common.datagen.IInspirationsRecipeBuilder;
import knightminer.inspirations.common.datagen.NBTIngredient;
import knightminer.inspirations.library.InspirationsTags;
import knightminer.inspirations.library.recipe.BlockIngredient;
import knightminer.inspirations.library.recipe.RecipeSerializers;
import knightminer.inspirations.library.recipe.cauldron.CauldronContentTypes;
import knightminer.inspirations.library.recipe.cauldron.CauldronIngredients;
import knightminer.inspirations.library.recipe.cauldron.contents.ICauldronContents;
import knightminer.inspirations.library.recipe.cauldron.ingredient.ICauldronIngredient;
import knightminer.inspirations.library.recipe.cauldron.recipe.CauldronRecipeBuilder;
import knightminer.inspirations.library.recipe.cauldron.recipe.CauldronTransformBuilder;
import knightminer.inspirations.library.recipe.cauldron.special.DyeableCauldronRecipe;
import knightminer.inspirations.library.recipe.cauldron.special.EmptyPotionCauldronRecipe;
import knightminer.inspirations.library.recipe.cauldron.special.FillPotionCauldronRecipe;
import knightminer.inspirations.library.recipe.cauldron.util.TemperaturePredicate;
import knightminer.inspirations.recipes.InspirationsRecipes;
import knightminer.inspirations.recipes.recipe.cauldron.BrewingCauldronRecipe;
import knightminer.inspirations.recipes.recipe.cauldron.DyeCauldronWaterRecipe;
import knightminer.inspirations.recipes.recipe.cauldron.MixCauldronDyeRecipe;
import knightminer.inspirations.recipes.recipe.cauldron.PotionFermentCauldronTransform;
import knightminer.inspirations.shared.InspirationsShared;
import knightminer.inspirations.utility.InspirationsUtility;
import net.minecraft.advancements.ICriterionInstance;
import net.minecraft.advancements.criterion.ItemPredicate;
import net.minecraft.advancements.criterion.StatePropertiesPredicate;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.SoundType;
import net.minecraft.data.CustomRecipeBuilder;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.RecipeProvider;
import net.minecraft.data.ShapedRecipeBuilder;
import net.minecraft.data.ShapelessRecipeBuilder;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.DyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.loot.ItemLootEntry;
import net.minecraft.loot.RandomValueRange;
import net.minecraft.loot.functions.SetCount;
import net.minecraft.potion.PotionUtils;
import net.minecraft.potion.Potions;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.Half;
import net.minecraft.state.properties.SlabType;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.crafting.CompoundIngredient;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.IConditionBuilder;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistryEntry;
import slimeknights.mantle.recipe.SizedIngredient;
import slimeknights.mantle.recipe.data.ConsumerWrapperBuilder;
import slimeknights.mantle.registration.object.EnumObject;

import javax.annotation.Nullable;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

import static knightminer.inspirations.library.recipe.cauldron.recipe.ICauldronRecipe.MAX;
import static knightminer.inspirations.library.recipe.cauldron.recipe.ICauldronRecipe.QUARTER;
import static knightminer.inspirations.library.recipe.cauldron.recipe.ICauldronRecipe.THIRD;
import static knightminer.inspirations.library.recipe.cauldron.recipe.ICauldronRecipe.TWELFTH;

public class RecipesRecipeProvider extends RecipeProvider implements IConditionBuilder, IInspirationsRecipeBuilder {
  private Consumer<IFinishedRecipe> consumer;
  private Set<Block> slabs;

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
    this.slabs = new HashSet<>();
    this.addCauldronRecipes();
    this.addAnvilRecipes();
  }

  private void addCauldronRecipes() {
    String folder = "cauldron/";

    ICauldronIngredient waterIngredient = CauldronIngredients.FLUID.of(Fluids.WATER);
    Consumer<IFinishedRecipe> cauldronRecipes = withCondition(ConfigEnabledCondition.CAULDRON_RECIPES);

    // vanilla recipes //

    // fill containers
    // bucket
    CustomRecipeBuilder.customRecipe(RecipeSerializers.CAULDRON_FILL_BUCKET)
                       .build(withCondition(ConfigEnabledCondition.CAULDRON_RECIPES), resourceName(folder + "fill_bucket"));
    // glass bottles
    ItemStack waterBottle = PotionUtils.addPotionToItemStack(new ItemStack(Items.POTION), Potions.WATER);
    CauldronRecipeBuilder.cauldron(SizedIngredient.fromItems(Items.GLASS_BOTTLE), waterIngredient)
                         .minLevels(THIRD)
                         .addLevels(-THIRD)
                         .setOutput(waterBottle.copy())
                         .setSound(SoundEvents.ITEM_BOTTLE_FILL)
                         .addCriterion("has_item", hasItem(Items.GLASS_BOTTLE))
                         .build(cauldronRecipes, resource(folder + "fill_water_bottle"));

    // empty container
    // bucket
    CustomRecipeBuilder.customRecipe(RecipeSerializers.CAULDRON_EMPTY_BUCKET)
                       .build(cauldronRecipes, resourceName(folder + "empty_bucket"));
    // water bottle
    CauldronRecipeBuilder.cauldron(SizedIngredient.of(new NBTIngredient(waterBottle)), waterIngredient)
                         .maxLevels(MAX - 1)
                         .addLevels(THIRD)
                         .setOutput(Items.GLASS_BOTTLE)
                         .setOutput(CauldronContentTypes.FLUID.of(Fluids.WATER))
                         .noContainer()
                         .setSound(SoundEvents.ITEM_BOTTLE_EMPTY)
                         .addCriterion("has_item", hasItem(
                             ItemPredicate.Builder.create()
                                                  .item(Items.POTION)
                                                  .nbt(Objects.requireNonNull(waterBottle.getTag()))
                                                  .build()))
                         .build(cauldronRecipes, resource(folder + "empty_water_bottle"));


    // custom recipes //

    // sponge dry cauldron
    CauldronRecipeBuilder.cauldron(SizedIngredient.fromItems(Blocks.SPONGE), waterIngredient)
                         .matchFull()
                         .setEmpty()
                         .setOutput(Blocks.WET_SPONGE)
                         .addCriterion("has_item", hasItem(Blocks.SPONGE))
                         .setSound(SoundType.PLANT.getPlaceSound())
                         .build(cauldronRecipes, resource(folder + "dry_cauldron"));

    // melt ice if boiling, use fancier recipe if cauldron ice is enabled
    String iceFolder = folder + "ice/";
    CauldronRecipeBuilder.cauldron(SizedIngredient.fromItems(Blocks.ICE), waterIngredient)
                         .maxLevels(MAX - 1)
                         .setFull()
                         .setTemperature(TemperaturePredicate.BOILING)
                         .setOutput(CauldronContentTypes.FLUID.of(Fluids.WATER))
                         .setSound(SoundEvents.ITEM_BUCKET_FILL)
                         .addCriterion("has_item", hasItem(Blocks.ICE))
                         .build(withCondition(ConfigEnabledCondition.CAULDRON_RECIPES, not(ConfigEnabledCondition.CAULDRON_ICE)), wrapE(Fluids.WATER, iceFolder, "_from_ice"));

    // freeze water into ice when cold
    Consumer<IFinishedRecipe> cauldronIce = withCondition(ConfigEnabledCondition.CAULDRON_ICE);
    ResourceLocation ice = Objects.requireNonNull(Blocks.ICE.getRegistryName());
    CauldronTransformBuilder.transform(CauldronIngredients.FLUID.of(Fluids.WATER), CauldronContentTypes.CUSTOM.of(ice), 500)
                            .setTemperature(TemperaturePredicate.FREEZING)
                            .addCriterion("has_item", hasItem(Items.ICE))
                            .setSound(SoundType.GLASS.getPlaceSound())
                            .build(cauldronIce, resource(iceFolder + "ice_from_water"));
    // freeze wet ice into packed ice
    ResourceLocation wetIce = Objects.requireNonNull(resource("wet_ice"));
    ResourceLocation packedIce = Objects.requireNonNull(Blocks.PACKED_ICE.getRegistryName());
    CauldronTransformBuilder.transform(CauldronIngredients.CUSTOM.of(wetIce), CauldronContentTypes.CUSTOM.of(packedIce), 2000)
                            .setTemperature(TemperaturePredicate.FREEZING)
                            .addCriterion("has_item", hasItem(Items.PACKED_ICE))
                            .setSound(SoundType.GLASS.getPlaceSound())
                            .build(cauldronIce, resource(iceFolder + "packed_ice_from_wet_ice"));
    // melt back again when hot
    CauldronTransformBuilder.transform(CauldronIngredients.CUSTOM.of(ice), CauldronContentTypes.FLUID.of(Fluids.WATER), 500)
                            .setTemperature(TemperaturePredicate.BOILING)
                            .addCriterion("has_item", hasItem(Items.ICE))
                            .setSound(SoundEvents.ITEM_BUCKET_EMPTY)
                            .build(cauldronIce, resource(iceFolder + "water_from_ice_melting"));
    CauldronTransformBuilder.transform(CauldronIngredients.CUSTOM.of(wetIce), CauldronContentTypes.FLUID.of(Fluids.WATER), 500)
                            .setTemperature(TemperaturePredicate.BOILING)
                            .addCriterion("has_item", hasItem(Items.ICE))
                            .setSound(SoundType.GLASS.getPlaceSound())
                            .build(cauldronIce, resource(iceFolder + "water_from_wet_ice"));
    CauldronTransformBuilder.transform(CauldronIngredients.CUSTOM.of(packedIce), CauldronContentTypes.CUSTOM.of(wetIce), 2000)
                            .setTemperature(TemperaturePredicate.BOILING)
                            .addCriterion("has_item", hasItem(Items.PACKED_ICE))
                            .setSound(SoundType.GLASS.getPlaceSound())
                            .build(cauldronIce, resource(iceFolder + "wet_ice_from_packed_ice"));

    // fill and empty ice
    CauldronRecipeBuilder.cauldron(CauldronIngredients.CUSTOM.of(ice))
                         .matchFull()
                         .setEmpty()
                         .setOutput(Blocks.ICE)
                         .setSound(SoundType.GLASS.getBreakSound())
                         .addCriterion("has_item", hasItem(Items.ICE))
                         .build(cauldronIce, resource(iceFolder + "pickup_ice"));
    CauldronRecipeBuilder.cauldron(SizedIngredient.fromItems(Blocks.ICE), CauldronIngredients.CUSTOM.of(ice))
                         .matchEmpty()
                         .setFull()
                         .setOutput(CauldronContentTypes.CUSTOM.of(ice))
                         .setSound(SoundType.GLASS.getPlaceSound())
                         .addCriterion("has_item", hasItem(Items.ICE))
                         .build(cauldronIce, resource(iceFolder + "place_ice"));
    // fill and empty wet ice
    CauldronRecipeBuilder.cauldron(CauldronIngredients.CUSTOM.of(wetIce))
                         .matchFull()
                         .setOutput(Blocks.ICE)
                         .setOutput(CauldronContentTypes.FLUID.of(Fluids.WATER))
                         .setSound(SoundType.GLASS.getBreakSound())
                         .addCriterion("has_item", hasItem(Items.ICE))
                         .build(cauldronIce, resource(iceFolder + "pickup_wet_ice"));
    CauldronRecipeBuilder.cauldron(SizedIngredient.fromItems(Blocks.ICE), CauldronIngredients.FLUID.of(Fluids.WATER))
                         .matchFull()
                         .setOutput(CauldronContentTypes.CUSTOM.of(wetIce))
                         .setSound(SoundType.GLASS.getPlaceSound())
                         .addCriterion("has_item", hasItem(Items.ICE))
                         .build(cauldronIce, resource(iceFolder + "place_wet_ice"));
    // fill and empty packed ice
    CauldronRecipeBuilder.cauldron(CauldronIngredients.CUSTOM.of(packedIce))
                         .matchFull()
                         .setEmpty()
                         .setOutput(Blocks.PACKED_ICE)
                         .setSound(SoundType.GLASS.getBreakSound())
                         .addCriterion("has_item", hasItem(Items.PACKED_ICE))
                         .build(cauldronIce, resource(iceFolder + "pickup_packed_ice"));
    CauldronRecipeBuilder.cauldron(SizedIngredient.fromItems(Blocks.PACKED_ICE), CauldronIngredients.CUSTOM.of(packedIce))
                         .matchEmpty()
                         .setFull()
                         .setOutput(CauldronContentTypes.CUSTOM.of(packedIce))
                         .setSound(SoundType.GLASS.getPlaceSound())
                         .addCriterion("has_item", hasItem(Items.ICE))
                         .build(cauldronIce, resource(iceFolder + "place_packed_ice"));



    // clean sticky pistons
    CauldronRecipeBuilder.cauldron(SizedIngredient.fromItems(Blocks.STICKY_PISTON), waterIngredient)
                         .minLevels(THIRD)
                         .addLevels(-THIRD)
                         .setOutput(Blocks.PISTON)
                         .addCriterion("has_item", hasItem(Blocks.STICKY_PISTON))
                         .build(cauldronRecipes, wrap(Blocks.STICKY_PISTON, folder, "_cleaning"));

    // concrete powder
    String concreteFolder = folder + "concrete/";
    Consumer<IFinishedRecipe> concrete = withCondition(ConfigEnabledCondition.CAULDRON_CONCRETE);
    VanillaEnum.CONCRETE_POWDER.forEach((color, powder) ->
      CauldronRecipeBuilder.cauldron(SizedIngredient.fromItems(powder), waterIngredient)
                           .minLevels(THIRD)
                           .addLevels(-THIRD)
                           .setOutput(VanillaEnum.CONCRETE.get(color))
                           .addCriterion("has_item", hasItem(powder))
                           .build(concrete, resource(concreteFolder + color.getString()))
    );

    // temporary milk recipes until Forge merges one of my milk bucket fixes
    Consumer<IFinishedRecipe> fluidConsumer = withCondition(ConfigEnabledCondition.CAULDRON_FLUIDS);
    ICauldronIngredient milkIngredient = CauldronIngredients.FLUID.of(InspirationsTags.Fluids.MILK);
    CauldronRecipeBuilder.cauldron(SizedIngredient.fromItems(Items.MILK_BUCKET), milkIngredient)
                         .maxLevels(MAX - 1)
                         .setFull()
                         .setOutput(Items.BUCKET)
                         .setOutput(CauldronContentTypes.FLUID.of(InspirationsRecipes.milk))
                         .noContainer()
                         .addCriterion("has_item", hasItem(Items.MILK_BUCKET))
                         .setSound(SoundEvents.ITEM_BUCKET_EMPTY)
                         .build(fluidConsumer, resource(folder + "empty_milk_bucket"));
    CauldronRecipeBuilder.cauldron(SizedIngredient.fromItems(Items.BUCKET), milkIngredient)
                         .matchFull()
                         .setEmpty()
                         .setOutput(Items.MILK_BUCKET)
                         .addCriterion("has_item", hasItem(Items.MILK_BUCKET))
                         .setSound(SoundEvents.ITEM_BUCKET_FILL)
                         .build(fluidConsumer, resource(folder + "fill_milk_bucket"));

    // honey //
    // fill and empty bottle, note unlike water this is in quarters
    String honeyFolder = folder + "honey/";
    CauldronRecipeBuilder.cauldron(SizedIngredient.fromItems(Items.HONEY_BOTTLE), CauldronIngredients.FLUID.of(InspirationsRecipes.honey))
                         .maxLevels(MAX - 1)
                         .addLevels(QUARTER)
                         .setOutput(Items.GLASS_BOTTLE)
                         .noContainer()
                         .setOutput(CauldronContentTypes.FLUID.of(InspirationsRecipes.honey))
                         .setSound(SoundEvents.ITEM_BOTTLE_EMPTY)
                         .addCriterion("has_item", hasItem(Items.HONEY_BOTTLE))
                         .build(fluidConsumer, resource(honeyFolder + "empty_bottle"));
    CauldronRecipeBuilder.cauldron(SizedIngredient.fromItems(Items.GLASS_BOTTLE), CauldronIngredients.FLUID.of(InspirationsRecipes.honey))
                         .minLevels(QUARTER)
                         .addLevels(-QUARTER)
                         .setOutput(Items.HONEY_BOTTLE)
                         .setSound(SoundEvents.ITEM_BUCKET_FILL)
                         .addCriterion("has_item", hasItem(Items.HONEY_BOTTLE))
                         .build(fluidConsumer, resource(honeyFolder + "fill_bottle"));
    // add and remove honey block
    ResourceLocation honeyBlock = new ResourceLocation("honey_block");
    CauldronRecipeBuilder.cauldron(SizedIngredient.fromItems(Blocks.HONEY_BLOCK), CauldronIngredients.CUSTOM.of(honeyBlock))
                         .matchEmpty()
                         .setFull()
                         .setOutput(CauldronContentTypes.CUSTOM.of(honeyBlock))
                         .setSound(SoundType.HONEY.getPlaceSound())
                         .addCriterion("has_item", hasItem(Blocks.HONEY_BLOCK))
                         .build(fluidConsumer, resource(honeyFolder + "place_block"));
    CauldronRecipeBuilder.cauldron(CauldronIngredients.CUSTOM.of(honeyBlock))
                         .matchFull()
                         .setEmpty()
                         .setOutput(Blocks.HONEY_BLOCK)
                         .setSound(SoundType.HONEY.getBreakSound())
                         .addCriterion("has_item", hasItem(Blocks.HONEY_BLOCK))
                         .build(fluidConsumer, resource(honeyFolder + "pickup_block"));
    // can grab 1 layer of honey for 1 sugar. Same rate as crafting table, but uses solid honey
    // if full block, gives block instead
    CauldronRecipeBuilder.cauldron(CauldronIngredients.CUSTOM.of(honeyBlock))
                         .levelRange(TWELFTH, MAX - 1)
                         .addLevels(-TWELFTH)
                         .setOutput(Items.SUGAR)
                         .setSound(SoundType.SAND.getBreakSound())
                         .addCriterion("has_item", hasItem(Items.HONEY_BOTTLE))
                         .build(fluidConsumer, resource(honeyFolder + "pickup_sugar"));
    // solidifies at room temp, melts under heat
    CauldronTransformBuilder.transform(CauldronIngredients.FLUID.of(InspirationsRecipes.honey), CauldronContentTypes.CUSTOM.of(honeyBlock), 300)
                            .setTemperature(TemperaturePredicate.COOL)
                            .setSound(SoundType.HONEY.getPlaceSound())
                            .addCriterion("has_item", hasItem(Blocks.HONEY_BLOCK))
                            .build(fluidConsumer, resource(honeyFolder + "solidify"));
    CauldronTransformBuilder.transform(CauldronIngredients.CUSTOM.of(honeyBlock), CauldronContentTypes.FLUID.of(InspirationsRecipes.honey), 300)
                            .setTemperature(TemperaturePredicate.BOILING)
                            .setSound(SoundType.HONEY.getBreakSound())
                            .addCriterion("has_item", hasItem(Blocks.HONEY_BLOCK))
                            .build(fluidConsumer, resource(honeyFolder + "melt"));

    // dyes //

    // dye cauldron water
    String dyeFolder = folder + "dye/";
    Consumer<IFinishedRecipe> dyeConsumer = withCondition(ConfigEnabledCondition.CAULDRON_DYEING);
    for (DyeColor color : DyeColor.values()) {
      // normal dye to set color
      dyeConsumer.accept(new DyeCauldronWaterRecipe.FinishedRecipe(resource(dyeFolder + "dye_" + color.getString()), color));
      // dyed bottle to mix color
      dyeConsumer.accept(new MixCauldronDyeRecipe.FinishedRecipe(
          resource(dyeFolder + "bottle/" + color.getString()),
          Ingredient.fromItems(InspirationsRecipes.simpleDyedWaterBottle.get(color)),
          color.getColorValue()));
    }
    // mixed dyed bottle
    dyeConsumer.accept(new MixCauldronDyeRecipe.FinishedRecipe(resource(dyeFolder + "bottle/mixed"), Ingredient.fromItems(InspirationsRecipes.mixedDyedWaterBottle)));
    // fill dyed bottle
    CustomRecipeBuilder.customRecipe(RecipeSerializers.CAULDRON_FILL_DYED_BOTTLE).build(dyeConsumer, resourceName(dyeFolder + "bottle/fill"));

    // mix dyed bottles
    String mixFolder = dyeFolder + "bottle/mix/";
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

    // undye and dye vanilla blocks
    addColoredRecipes(ItemTags.WOOL, VanillaEnum.WOOL, folder + "wool/", null);
    addColoredRecipes(ItemTags.BEDS, VanillaEnum.BED, folder + "bed/", null);
    addColoredRecipes(ItemTags.CARPETS, InspirationsShared.VANILLA_CARPETS, folder + "carpet/", null);
    addColoredRecipes(InspirationsTags.Items.SHULKER_BOXES, VanillaEnum.SHULKER_BOX, Items.SHULKER_BOX, folder + "shulker_box/", true, null);
    // Inspirations blocks
    addColoredRecipes(InspirationsTags.Items.CARPETED_TRAPDOORS, InspirationsUtility.carpetedTrapdoors, folder + "carpeted_trapdoor/", ConfigEnabledCondition.CARPETED_TRAPDOOR);

    // craft vanilla blocks using dyed bottles, as Forge did not reimplement the tags
    // since I have to add recipes, a little more generous with them
    Consumer<IFinishedRecipe> bottleConsumer = withCondition(ConfigEnabledCondition.CAULDRON_DYEING);
    String bottleFolder = folder + "bottle/";
    InspirationsRecipes.simpleDyedWaterBottle.forEach((dye, bottle) -> {
      String name = dye.getString();
      ICriterionInstance hasBottle = hasItem(bottle);

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
      ShapelessRecipeBuilder.shapelessRecipe(VanillaEnum.CONCRETE_POWDER.get(dye), 8)
                            .setGroup("concrete_powder")
                            .addIngredient(bottle)
                            .addIngredient(ItemTags.SAND)
                            .addIngredient(ItemTags.SAND)
                            .addIngredient(ItemTags.SAND)
                            .addIngredient(ItemTags.SAND)
                            .addIngredient(Tags.Items.GRAVEL)
                            .addIngredient(Tags.Items.GRAVEL)
                            .addIngredient(Tags.Items.GRAVEL)
                            .addIngredient(Tags.Items.GRAVEL)
                            .addCriterion("has_item", hasBottle)
                            .build(bottleConsumer, resource(bottleFolder + "concrete_powder/" + name));
    });

    // use ink bottle for book and quill
    IItemProvider blackBottle = InspirationsRecipes.simpleDyedWaterBottle.get(DyeColor.BLACK);
    ShapelessRecipeBuilder.shapelessRecipe(Items.WRITABLE_BOOK)
                          .setGroup(Objects.requireNonNull(Items.WRITABLE_BOOK.getRegistryName()).getPath())
                          .addIngredient(blackBottle)
                          .addIngredient(Tags.Items.FEATHERS)
                          .addIngredient(Items.BOOK)
                          .addCriterion("has_item", hasItem(blackBottle))
                          .build(bottleConsumer, resource(bottleFolder + "writable_book"));

    // leather dyeing and clearing
    addDyeableRecipes(Items.LEATHER_HELMET, folder);
    addDyeableRecipes(Items.LEATHER_CHESTPLATE, folder);
    addDyeableRecipes(Items.LEATHER_LEGGINGS, folder);
    addDyeableRecipes(Items.LEATHER_BOOTS, folder);
    addDyeableRecipes(Items.LEATHER_HORSE_ARMOR, folder);

    // banner pattern removing
    CustomRecipeBuilder.customRecipe(RecipeSerializers.CAULDRON_REMOVE_BANNER_PATTERN).build(cauldronRecipes, resourceName(folder + "remove_banner_pattern"));


    // potions //
    String potionFolder = folder + "potion/";
    Consumer<IFinishedRecipe> potionConsumer = withCondition(ConfigEnabledCondition.CAULDRON_POTIONS);
    // normal
    potionConsumer.accept(new EmptyPotionCauldronRecipe.FinishedRecipe(
        resource(potionFolder + "normal_empty"), Items.POTION, Items.GLASS_BOTTLE));
    potionConsumer.accept(new FillPotionCauldronRecipe.FinishedRecipe(
        resource(potionFolder + "normal_fill"), SizedIngredient.fromItems(Items.GLASS_BOTTLE), Items.POTION));

    // splash
    potionConsumer.accept(new EmptyPotionCauldronRecipe.FinishedRecipe(
        resource(potionFolder + "splash_empty"), Items.SPLASH_POTION, InspirationsRecipes.splashBottle));
    potionConsumer.accept(new FillPotionCauldronRecipe.FinishedRecipe(
        resource(potionFolder + "splash_fill"), SizedIngredient.fromTag(InspirationsTags.Items.SPLASH_BOTTLES), Items.SPLASH_POTION));

    // lingering
    potionConsumer.accept(new EmptyPotionCauldronRecipe.FinishedRecipe(
        resource(potionFolder + "lingering_empty"), Items.LINGERING_POTION, InspirationsRecipes.lingeringBottle));
    potionConsumer.accept(new FillPotionCauldronRecipe.FinishedRecipe(
        resource(potionFolder + "lingering_fill"), SizedIngredient.fromTag(InspirationsTags.Items.LINGERING_BOTTLES), Items.LINGERING_POTION));

    // tipped arrows
    withCondition(ConfigEnabledCondition.CAULDRON_TIP_ARROWS).accept(
        new FillPotionCauldronRecipe.FinishedRecipe(resource(potionFolder + "tipped_arrow"), SizedIngredient.fromItems(16, Items.ARROW), Items.TIPPED_ARROW));

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

    // brew the potions
    Consumer<IFinishedRecipe> brewingConsumer = withCondition(ConfigEnabledCondition.CAULDRON_BREWING);
    brewingConsumer.accept(new BrewingCauldronRecipe.FinishedRecipe(resource(potionFolder + "potion_brewing"), RecipeSerializers.CAULDRON_POTION_BREWING, false));
    brewingConsumer.accept(new BrewingCauldronRecipe.FinishedRecipe(resource(potionFolder + "forge_brewing"), RecipeSerializers.CAULDRON_FORGE_BREWING, false));
    brewingConsumer.accept(new PotionFermentCauldronTransform.FinishedRecipe(resource(potionFolder + "potion_ferment"), 600));

    // fluid recipes //
    // beetroot is just water based
    addStewRecipe(Items.BEETROOT_SOUP, Ingredient.fromTag(Tags.Items.CROPS_BEETROOT), 6, InspirationsRecipes.beetrootSoup, waterIngredient, folder + "beetroot_soup/");
    // mushroom water based
    addStewRecipe(Items.MUSHROOM_STEW, Ingredient.fromTag(Tags.Items.MUSHROOMS), 2, InspirationsRecipes.mushroomStew, waterIngredient, folder + "mushroom_stew/");
    // potato comes from mushroom
    addStewRecipe(InspirationsRecipes.potatoSoupItem, Ingredient.fromItems(Items.BAKED_POTATO), 2, InspirationsRecipes.potatoSoup, CauldronIngredients.FLUID.of(InspirationsRecipes.mushroomStew), folder + "potato_soup/");
    // add in some rabbit for rabbit stew
    addStewRecipe(Items.RABBIT_STEW, Ingredient.fromItems(Items.COOKED_RABBIT), 1, InspirationsRecipes.rabbitStew, CauldronIngredients.FLUID.of(InspirationsRecipes.potatoSoup), folder + "rabbit_stew/");

    // normal potato soup crafting
    ShapelessRecipeBuilder.shapelessRecipe(InspirationsRecipes.potatoSoupItem)
                          .addIngredient(Items.BOWL)
                          .addIngredient(Items.BAKED_POTATO)
                          .addIngredient(Items.BAKED_POTATO)
                          .addIngredient(Tags.Items.MUSHROOMS)
                          .addCriterion("has_item", hasItem(Items.BAKED_POTATO))
                          .build(withCondition(), resource(folder + "potato_soup/item"));
  }

  private void addAnvilRecipes() {
    AnvilRecipeBuilder.places(Blocks.COBBLESTONE).addIngredient(Blocks.STONE).build(consumer, "cobble_from_stone");
    AnvilRecipeBuilder.places(Blocks.COBBLESTONE).addIngredient(Blocks.STONE_BRICKS).build(consumer, "cobble_from_bricks");
    AnvilRecipeBuilder.places(Blocks.COBBLESTONE).addIngredient(Blocks.SMOOTH_STONE).build(consumer, "cobble_from_smooth_stone");
    AnvilRecipeBuilder.places(Blocks.MOSSY_COBBLESTONE).addIngredient(Blocks.MOSSY_STONE_BRICKS).build(consumer);
    AnvilRecipeBuilder.places(Blocks.PRISMARINE).addIngredient(Blocks.PRISMARINE_BRICKS).build(consumer);
    AnvilRecipeBuilder.places(Blocks.END_STONE).addIngredient(Blocks.END_STONE_BRICKS).build(consumer);
    AnvilRecipeBuilder.places(Blocks.GRAVEL).addIngredient(Blocks.COBBLESTONE).build(consumer);
    AnvilRecipeBuilder.places(Blocks.ANDESITE).addIngredient(Blocks.POLISHED_ANDESITE).build(consumer);
    AnvilRecipeBuilder.places(Blocks.GRANITE).addIngredient(Blocks.POLISHED_GRANITE).build(consumer);
    AnvilRecipeBuilder.places(Blocks.DIORITE).addIngredient(Blocks.POLISHED_DIORITE).build(consumer);
    AnvilRecipeBuilder.places(Blocks.BLACKSTONE).addIngredient(Blocks.POLISHED_BLACKSTONE).build(consumer, "blackstone_from_polished");
    AnvilRecipeBuilder.places(Blocks.BLACKSTONE).addIngredient(Blocks.POLISHED_BLACKSTONE_BRICKS).build(consumer, "blackstone_from_bricks");

    AnvilRecipeBuilder.smashes()
            .addIngredient(Blocks.GILDED_BLACKSTONE)
            .addLoot(ItemLootEntry.builder(Items.GOLD_NUGGET)
                    .acceptFunction(SetCount.builder(RandomValueRange.of(2, 5)))
            )
            .build(consumer, "gold_from_gilded_blackstone");

    AnvilRecipeBuilder.places(Blocks.SAND).addIngredient(Blocks.SANDSTONE).build(consumer);
    AnvilRecipeBuilder.places(Blocks.RED_SAND).addIngredient(Blocks.RED_SANDSTONE).build(consumer);

    AnvilRecipeBuilder.smashes().addIngredient(BlockTags.ICE).build(consumer);
    AnvilRecipeBuilder.smashes().addIngredient(BlockTags.LEAVES).build(consumer);
    AnvilRecipeBuilder.smashes().addIngredient(Blocks.BROWN_MUSHROOM_BLOCK).build(consumer);
    AnvilRecipeBuilder.smashes().addIngredient(Blocks.RED_MUSHROOM_BLOCK).build(consumer);
    AnvilRecipeBuilder.smashes().addIngredient(Blocks.PUMPKIN).build(consumer);
    AnvilRecipeBuilder.smashes().addIngredient(Blocks.CARVED_PUMPKIN).build(consumer);
    AnvilRecipeBuilder.smashes().addIngredient(Blocks.JACK_O_LANTERN).build(consumer);
    AnvilRecipeBuilder.smashes().addIngredient(Blocks.MELON).build(consumer);

    // Smash glass.
    AnvilRecipeBuilder.smashes().addIngredient(Tags.Blocks.GLASS).build(consumer, "glass_tag");
    AnvilRecipeBuilder.smashes().addIngredient(Tags.Blocks.GLASS_PANES).build(consumer, "glass_panes_tag");

    // Smash all silverfish blocks.
    AnvilRecipeBuilder.smashes().addIngredient(Blocks.INFESTED_CHISELED_STONE_BRICKS).build(consumer);
    AnvilRecipeBuilder.smashes().addIngredient(Blocks.INFESTED_COBBLESTONE).build(consumer);
    AnvilRecipeBuilder.smashes().addIngredient(Blocks.INFESTED_CRACKED_STONE_BRICKS).build(consumer);
    AnvilRecipeBuilder.smashes().addIngredient(Blocks.INFESTED_MOSSY_STONE_BRICKS).build(consumer);
    AnvilRecipeBuilder.smashes().addIngredient(Blocks.INFESTED_STONE).build(consumer);
    AnvilRecipeBuilder.smashes().addIngredient(Blocks.INFESTED_STONE_BRICKS).build(consumer);

    // Knock down slabs.
    addAnvilSlab(Blocks.COBBLESTONE_SLAB, Blocks.STONE_SLAB, "cobble_from_stone_slab");
    addAnvilSlab(Blocks.COBBLESTONE_SLAB, Blocks.STONE_BRICK_SLAB, "cobble_from_bricks_slab");
    addAnvilSlab(Blocks.COBBLESTONE_SLAB, Blocks.SMOOTH_STONE_SLAB, "cobble_from_smooth_stone_slab");
    addAnvilSlab(Blocks.MOSSY_COBBLESTONE_SLAB, Blocks.MOSSY_STONE_BRICK_SLAB);
    addAnvilSlab(Blocks.PRISMARINE_SLAB, Blocks.PRISMARINE_BRICK_SLAB);
    addAnvilSlab(Blocks.END_STONE_BRICK_SLAB, Blocks.END_STONE_BRICK_SLAB);
    addAnvilSlab(Blocks.ANDESITE_SLAB, Blocks.POLISHED_ANDESITE_SLAB);
    addAnvilSlab(Blocks.GRANITE_SLAB, Blocks.POLISHED_GRANITE_SLAB);
    addAnvilSlab(Blocks.DIORITE_SLAB, Blocks.POLISHED_DIORITE_SLAB);
    addAnvilSlab(Blocks.BLACKSTONE_SLAB, Blocks.POLISHED_BLACKSTONE_SLAB, "blackstone_from_polished_slab");
    addAnvilSlab(Blocks.BLACKSTONE_SLAB, Blocks.POLISHED_BLACKSTONE_BRICK_SLAB, "blackstone_from_bricks_slab");


    // Smash concrete into concrete powder.
    for(DyeColor dye: DyeColor.values()) {
      AnvilRecipeBuilder
              .places(ForgeRegistries.BLOCKS.getValue(new ResourceLocation(dye.getTranslationKey() + "_concrete_powder")))
              .addIngredient(ForgeRegistries.BLOCKS.getValue(new ResourceLocation(dye.getTranslationKey() + "_concrete")))
              .build(consumer);
    }
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
    SizedIngredient ingredient = SizedIngredient.fromTag(tag);
    ICriterionInstance criteria = hasItem(tag);
    CauldronRecipeBuilder undyedBuilder = CauldronRecipeBuilder
        .cauldron(ingredient, CauldronIngredients.FLUID.of(Fluids.WATER))
        .minLevels(THIRD)
        .addLevels(-THIRD)
        .setOutput(undyedItem)
        .addCriterion("has_item", criteria);
    if (copyNBT) {
      undyedBuilder.setCopyNBT();
    }
    undyedBuilder.build(undyed, resource(folder + "undye"));

    // dyed recipes need one more condition
    enumObject.forEach((color, block) -> {
      CauldronRecipeBuilder coloredBuilder = CauldronRecipeBuilder
          .cauldron(ingredient, CauldronIngredients.DYE.of(color))
          .minLevels(THIRD)
          .addLevels(-THIRD)
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
   * Add recipes to convert one type of slab to another.
   * Upper slabs are flattened to the bottom.
   * @param from Starting slab block
   * @param to Resultant slab block.
   * @param path Name of the recipe.
   */
  private void addAnvilSlab(Block to, Block from, String path) {
    // If on the bottom, the anvil can't actually hit it. So that doesn't
    // need to be accounted for.
    assert from instanceof SlabBlock;
    assert to instanceof SlabBlock;
    StatePropertiesPredicate isTop = StatePropertiesPredicate.Builder
            .newBuilder()
            .withProp(BlockStateProperties.SLAB_TYPE, SlabType.TOP)
            .build();
    StatePropertiesPredicate isBtm = StatePropertiesPredicate.Builder
            .newBuilder()
            .withProp(BlockStateProperties.SLAB_TYPE, SlabType.BOTTOM)
            .build();

    // single -> bottom
    AnvilRecipeBuilder.places(to)
            .addIngredient(from, isTop, isBtm)
            .copiesProperty(BlockStateProperties.WATERLOGGED)
            .setsProp(BlockStateProperties.SLAB_TYPE, SlabType.BOTTOM)
            .build(consumer, path + "_single");

    // Double -> double
    AnvilRecipeBuilder.places(to)
            .addIngredient(from, StatePropertiesPredicate.Builder
                    .newBuilder()
                    .withProp(BlockStateProperties.SLAB_TYPE, SlabType.DOUBLE)
                    .build())
            .copiesProperty(BlockStateProperties.WATERLOGGED)
            .setsProp(BlockStateProperties.SLAB_TYPE, SlabType.DOUBLE)
            .build(consumer, path + "_double");

    if (slabs.add(to) && to != from) {
      // Add recipe to knock this down to the bottom, but unchanged.
      AnvilRecipeBuilder.places(to)
              .addIngredient(to, isTop)
              .copiesProperty(BlockStateProperties.WATERLOGGED)
              .setsProp(BlockStateProperties.HALF, Half.BOTTOM)
              .build(consumer, to.getRegistryName().getPath() + "_knockdown");
    }
  }
  private void addAnvilSlab(Block to, Block from) {
    addAnvilSlab(to, from, to.getRegistryName().getPath());
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
    ICauldronIngredient stewIngredient = CauldronIngredients.FLUID.of(stewFluid);
    ICauldronContents stewContents = CauldronContentTypes.FLUID.of(stewFluid);

    // fill the bowl
    CauldronRecipeBuilder.cauldron(SizedIngredient.fromItems(Items.BOWL), stewIngredient)
                         .minLevels(THIRD)
                         .addLevels(-THIRD)
                         .setOutput(stewItem)
                         .setSound(SoundEvents.ITEM_BOTTLE_FILL)
                         .addCriterion("has_item", hasItem(Items.BOWL))
                         .build(consumer, resource(folder + "fill_bowl"));

    // empty the bowl
    CauldronRecipeBuilder.cauldron(SizedIngredient.fromItems(stewItem), stewIngredient)
                         .maxLevels(MAX - 1)
                         .addLevels(THIRD)
                         .setOutput(Items.BOWL)
                         .setOutput(stewContents)
                         .noContainer()
                         .setSound(SoundEvents.ITEM_BOTTLE_EMPTY)
                         .addCriterion("has_item", hasItem(stewItem))
                         .build(consumer, resource(folder + "empty_bowl"));

    // if the cauldron has 1 level, takes 1 item. If it has 2 or 3, take twice as many
    ICriterionInstance criteria = hasItem(Blocks.CAULDRON);
    CauldronRecipeBuilder.cauldron(SizedIngredient.of(ingredient, amount), base)
                         .levelRange(1, THIRD)
                         .setTemperature(TemperaturePredicate.BOILING)
                         .setOutput(stewContents)
                         .addCriterion("has_item", criteria)
                         .build(consumer, resource(folder + "stew_small"));
    CauldronRecipeBuilder.cauldron(SizedIngredient.of(ingredient, amount * 2), base)
                         .minLevels(THIRD + 1)
                         .setTemperature(TemperaturePredicate.BOILING)
                         .setOutput(stewContents)
                         .addCriterion("has_item", criteria)
                         .build(consumer, resource(folder + "stew_large"));
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
    Consumer<IFinishedRecipe> consumer = consumerBuilder.build(getConsumer());

    // build recipe name
    StringBuilder name = new StringBuilder(folder + output.getString() + "_from");
    Item outputItem = InspirationsRecipes.simpleDyedWaterBottle.get(output);
    ShapelessRecipeBuilder builder = ShapelessRecipeBuilder.shapelessRecipe(outputItem, inputs.length)
        .setGroup(Objects.requireNonNull(outputItem.getRegistryName()).toString());
    Set<DyeColor> seen = EnumSet.noneOf(DyeColor.class);
    for (DyeColor input : inputs) {
      IItemProvider bottle = InspirationsRecipes.simpleDyedWaterBottle.get(input);
      builder.addIngredient(bottle);
      // only add each color to the name and criteria once
      if (!seen.contains(input)) {
        builder.addCriterion("has_" + input.getString(), hasItem(bottle));
        name.append("_").append(input.getString());
        seen.add(input);
      }
    }
    // build the recipe with the built name
    builder.build(consumer, resourceName(name.toString()));
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
  private void addSurroundRecipe(Consumer<IFinishedRecipe> consumer, IItemProvider output, String group, ITag<Item> surround, IItemProvider center, ResourceLocation location) {
    ShapedRecipeBuilder.shapedRecipe(output, 8)
                       .setGroup(group)
                       .key('#', surround)
                       .key('x', center)
                       .patternLine("###")
                       .patternLine("#x#")
                       .patternLine("###")
                       .addCriterion("has_item", hasItem(center))
                       .build(consumer, location);
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
  private void addComboRecipe(Consumer<IFinishedRecipe> consumer, IItemProvider output, String group, ITag<Item> input, IItemProvider modifier, ResourceLocation location) {
    ShapelessRecipeBuilder.shapelessRecipe(output)
                          .setGroup(group)
                          .addIngredient(input)
                          .addIngredient(modifier)
                          .addCriterion("has_item", hasItem(modifier))
                          .build(consumer, location);
  }

  private ResourceLocation prefixE(IForgeRegistryEntry<?> entry, String prefix) {
    return this.resource(prefix + Objects.requireNonNull(entry.getRegistryName()).getPath());
  }

  private ResourceLocation wrapE(IForgeRegistryEntry<?> entry, String prefix, String suffix) {
    return this.resource(prefix + Objects.requireNonNull(entry.getRegistryName()).getPath() + suffix);
  }
}
