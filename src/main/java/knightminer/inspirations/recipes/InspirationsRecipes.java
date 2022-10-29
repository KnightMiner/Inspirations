package knightminer.inspirations.recipes;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import knightminer.inspirations.common.ModuleBase;
import knightminer.inspirations.library.MiscUtil;
import knightminer.inspirations.library.recipe.cauldron.CauldronContentTypes;
import knightminer.inspirations.library.recipe.cauldron.contents.ICauldronContents;
import knightminer.inspirations.library.recipe.cauldron.recipe.CauldronRecipe;
import knightminer.inspirations.library.recipe.cauldron.recipe.CauldronTransform;
import knightminer.inspirations.library.recipe.cauldron.special.DyeableCauldronRecipe;
import knightminer.inspirations.library.recipe.cauldron.special.EmptyPotionCauldronRecipe;
import knightminer.inspirations.library.recipe.cauldron.special.FillPotionCauldronRecipe;
import knightminer.inspirations.recipes.block.FourLayerCauldronBlock;
import knightminer.inspirations.recipes.cauldron.DecreaseLayerCauldronInteraction;
import knightminer.inspirations.recipes.cauldron.EmptyCauldronInteraction;
import knightminer.inspirations.recipes.cauldron.FillCauldronInteraction;
import knightminer.inspirations.recipes.cauldron.FirstCauldronInteraction;
import knightminer.inspirations.recipes.cauldron.IncreaseLayerCauldronInteraction;
import knightminer.inspirations.recipes.cauldron.TransformCauldronInteraction;
import knightminer.inspirations.recipes.data.RecipesRecipeProvider;
import knightminer.inspirations.recipes.item.EmptyBottleItem;
import knightminer.inspirations.recipes.item.MixedDyedBottleItem;
import knightminer.inspirations.recipes.item.SimpleDyedBottleItem;
import knightminer.inspirations.recipes.recipe.cauldron.BrewingCauldronRecipe;
import knightminer.inspirations.recipes.recipe.cauldron.DyeCauldronWaterRecipe;
import knightminer.inspirations.recipes.recipe.cauldron.EmptyBucketCauldronRecipe;
import knightminer.inspirations.recipes.recipe.cauldron.FillBucketCauldronRecipe;
import knightminer.inspirations.recipes.recipe.cauldron.FillDyedBottleRecipe;
import knightminer.inspirations.recipes.recipe.cauldron.MixCauldronDyeRecipe;
import knightminer.inspirations.recipes.recipe.cauldron.PotionFermentCauldronTransform;
import knightminer.inspirations.recipes.recipe.cauldron.RemoveBannerPatternCauldronRecipe;
import knightminer.inspirations.utility.InspirationsUtility;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.data.DataGenerator;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BowlFoodItem;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleRecipeSerializer;
import net.minecraft.world.level.block.AbstractCauldronBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.GameData;
import slimeknights.mantle.registration.FluidBuilder;
import slimeknights.mantle.registration.ModelFluidAttributes;
import slimeknights.mantle.registration.adapter.BlockRegistryAdapter;
import slimeknights.mantle.registration.adapter.FluidRegistryAdapter;
import slimeknights.mantle.registration.adapter.ItemRegistryAdapter;
import slimeknights.mantle.registration.adapter.RegistryAdapter;
import slimeknights.mantle.registration.object.EnumObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

@SuppressWarnings({"WeakerAccess", "unused"})
public class InspirationsRecipes extends ModuleBase {
  /** Interactions for the mushroom stew cauldron */
  public static final Map<Item,CauldronInteraction> MUSHROOM_STEW_CAULDRON_INTERACTIONS = CauldronInteraction.newInteractionMap();
  /** Interactions for the beetroot soup cauldron */
  public static final Map<Item,CauldronInteraction> BEETROOT_SOUP_CAULDRON_INTERACTIONS = CauldronInteraction.newInteractionMap();
  /** Interactions for the rabbit stew cauldron */
  public static final Map<Item,CauldronInteraction> RABBIT_STEW_CAULDRON_INTERACTION = CauldronInteraction.newInteractionMap();
  /** Interactions for the potato soup cauldron */
  public static final Map<Item,CauldronInteraction> POTATO_SOUP_CAULDRON_INTERACTIONS = CauldronInteraction.newInteractionMap();
  /** Interactions for the honey cauldron */
  public static final Map<Item,CauldronInteraction> HONEY_CAULDRON_INTERACTIONS = CauldronInteraction.newInteractionMap();

  // blocks
  public static FourLayerCauldronBlock mushroomStewCauldron, beetrootSoupCauldron, rabbitStewCauldron, potatoSoupCauldron, honeyCauldron;

  // items
  public static Item splashBottle;
  public static Item lingeringBottle;
  public static EnumObject<DyeColor,SimpleDyedBottleItem> simpleDyedWaterBottle = EnumObject.empty();
  public static MixedDyedBottleItem mixedDyedWaterBottle;
  public static BowlFoodItem potatoSoupItem;

  // fluids
  public static ForgeFlowingFluid milk;
  // mushroom
  public static ForgeFlowingFluid mushroomStew;
  public static BucketItem mushroomStewBucket;
  public static LiquidBlock mushroomStewBlock;
  // beetroot
  public static ForgeFlowingFluid beetrootSoup;
  public static BucketItem beetrootSoupBucket;
  public static LiquidBlock beetrootSoupBlock;
  // rabbit
  public static ForgeFlowingFluid rabbitStew;
  public static BucketItem rabbitStewBucket;
  public static LiquidBlock rabbitStewBlock;
  // potato
  public static ForgeFlowingFluid potatoSoup;
  public static BucketItem potatoSoupBucket;
  public static LiquidBlock potatoSoupBlock;
  // honey
  public static ForgeFlowingFluid honey;
  public static BucketItem honeyBucket;
  public static LiquidBlock honeyFluidBlock;

  public static SimpleParticleType boilingParticle;

  public InspirationsRecipes() {
    ForgeMod.enableMilkFluid();
  }

  @SubscribeEvent
  void registerFluids(Register<Fluid> event) {
    FluidRegistryAdapter adapter = new FluidRegistryAdapter(event.getRegistry());

    mushroomStew = adapter.register(new FluidBuilder(fluidBuilder().temperature(373).viscosity(1200))
                                        .block(() -> mushroomStewBlock)
                                        .bucket(() -> mushroomStewBucket), "mushroom_stew");
    beetrootSoup = adapter.register(new FluidBuilder(fluidBuilder().temperature(373).viscosity(1100))
                                        .block(() -> beetrootSoupBlock)
                                        .bucket(() -> beetrootSoupBucket), "beetroot_soup");
    rabbitStew = adapter.register(new FluidBuilder(fluidBuilder().temperature(373).viscosity(1400))
                                      .block(() -> rabbitStewBlock)
                                      .bucket(() -> rabbitStewBucket), "rabbit_stew");
    potatoSoup = adapter.register(new FluidBuilder(fluidBuilder().temperature(373).viscosity(1300))
                                      .block(() -> potatoSoupBlock)
                                      .bucket(() -> potatoSoupBucket), "potato_soup");
    honey = adapter.register(new FluidBuilder(fluidBuilder().viscosity(4000).temperature(373))
                                      .block(() -> honeyFluidBlock)
                                      .bucket(() -> honeyBucket), "honey");
  }

  @SubscribeEvent
  void registerBlocks(Register<Block> event) {
    BlockRegistryAdapter registry = new BlockRegistryAdapter(event.getRegistry());

    mushroomStewCauldron = registry.register(new FourLayerCauldronBlock(Properties.copy(Blocks.CAULDRON), MUSHROOM_STEW_CAULDRON_INTERACTIONS), "mushroom_stew_cauldron");
    beetrootSoupCauldron = registry.register(new FourLayerCauldronBlock(Properties.copy(Blocks.CAULDRON), BEETROOT_SOUP_CAULDRON_INTERACTIONS), "beetroot_soup_cauldron");
    rabbitStewCauldron   = registry.register(new FourLayerCauldronBlock(Properties.copy(Blocks.CAULDRON), RABBIT_STEW_CAULDRON_INTERACTION), "rabbit_stew_cauldron");
    potatoSoupCauldron   = registry.register(new FourLayerCauldronBlock(Properties.copy(Blocks.CAULDRON), POTATO_SOUP_CAULDRON_INTERACTIONS), "potato_soup_cauldron");
    honeyCauldron        = registry.register(new FourLayerCauldronBlock(Properties.copy(Blocks.CAULDRON), HONEY_CAULDRON_INTERACTIONS), "honey_cauldron");

    mushroomStewBlock = registry.registerFluidBlock(() -> mushroomStew, Material.WATER, 0, "mushroom_stew");
    beetrootSoupBlock = registry.registerFluidBlock(() -> beetrootSoup, Material.WATER, 0, "beetroot_soup");
    rabbitStewBlock = registry.registerFluidBlock(() -> rabbitStew, Material.WATER, 0, "rabbit_stew");
    potatoSoupBlock = registry.registerFluidBlock(() -> potatoSoup, Material.WATER, 0, "potato_soup");
    honeyFluidBlock = registry.registerFluidBlock(() -> honey, Material.WATER, 0, "honey");
  }

  @SubscribeEvent
  void registerItems(Register<Item> event) {
    ItemRegistryAdapter registry = new ItemRegistryAdapter(event.getRegistry());

    // buckets
    mushroomStewBucket = registry.registerBucket(() -> mushroomStew, "mushroom_stew");
    beetrootSoupBucket = registry.registerBucket(() -> beetrootSoup, "beetroot_soup");
    rabbitStewBucket = registry.registerBucket(() -> rabbitStew, "rabbit_stew");
    potatoSoupBucket = registry.registerBucket(() -> potatoSoup, "potato_soup");
    honeyBucket = registry.registerBucket(() -> honey, "honey");

    // potato soup
    potatoSoupItem = registry.register(
        new BowlFoodItem(new Item.Properties().stacksTo(1)
                                          .tab(CreativeModeTab.TAB_FOOD)
                                          .food(new FoodProperties.Builder().nutrition(8).saturationMod(0.6F).build())),
        "potato_soup");

    // empty bottles
    Item.Properties brewingProps = new Item.Properties().tab(CreativeModeTab.TAB_BREWING);
    splashBottle = registry.register(new EmptyBottleItem(brewingProps, Items.SPLASH_POTION.delegate), "splash_bottle");
    lingeringBottle = registry.register(new EmptyBottleItem(brewingProps, Items.LINGERING_POTION.delegate), "lingering_bottle");

    // dyed bottles
    Item.Properties bottleProps = new Item.Properties()
        .tab(CreativeModeTab.TAB_MATERIALS)
        .stacksTo(16)
        .craftRemainder(Items.GLASS_BOTTLE);
    simpleDyedWaterBottle = registry.registerEnum(color -> new SimpleDyedBottleItem(bottleProps, DyeItem.byColor(color)), DyeColor.values(), "dyed_bottle");
    mixedDyedWaterBottle = registry.register(new MixedDyedBottleItem(bottleProps), "mixed_dyed_bottle");
  }

  @SubscribeEvent
  void registerParticleTypes(Register<ParticleType<?>> event) {
    RegistryAdapter<ParticleType<?>> registry = new RegistryAdapter<>(event.getRegistry());
    boilingParticle = registry.register(new SimpleParticleType(false), "boiling");
  }

  @SubscribeEvent
  void registerSerializers(Register<RecipeSerializer<?>> event) {
    RegistryAdapter<RecipeSerializer<?>> registry = new RegistryAdapter<>(event.getRegistry());
    registry.register(new CauldronRecipe.Serializer(), "cauldron");
    registry.register(new EmptyPotionCauldronRecipe.Serializer(), "cauldron_empty_potion");
    registry.register(new FillPotionCauldronRecipe.Serializer(), "cauldron_fill_potion");
    registry.register(new DyeCauldronWaterRecipe.Serializer(), "cauldron_dye_water");
    registry.register(new MixCauldronDyeRecipe.Serializer(), "cauldron_mix_dye");
    registry.register(new DyeableCauldronRecipe.Serializer(DyeableCauldronRecipe.Dye::new), "cauldron_dye_dyeable");
    registry.register(new DyeableCauldronRecipe.Serializer(DyeableCauldronRecipe.Clear::new), "cauldron_clear_dyeable");
    registry.register(new CauldronTransform.Serializer(), "cauldron_transform");
    registry.register(new PotionFermentCauldronTransform.Serializer(), "cauldron_potion_ferment");

    registry.register(new SimpleRecipeSerializer<>(EmptyBucketCauldronRecipe::new), "cauldron_empty_bucket");
    registry.register(new SimpleRecipeSerializer<>(FillBucketCauldronRecipe::new), "cauldron_fill_bucket");
    registry.register(new SimpleRecipeSerializer<>(FillDyedBottleRecipe::new), "cauldron_fill_dyed_bottle");
    registry.register(new SimpleRecipeSerializer<>(RemoveBannerPatternCauldronRecipe::new), "cauldron_remove_banner_pattern");
    registry.register(new BrewingCauldronRecipe.Serializer(BrewingCauldronRecipe.Vanilla::new), "cauldron_potion_brewing");
    registry.register(new BrewingCauldronRecipe.Serializer(BrewingCauldronRecipe.Forge::new), "cauldron_forge_brewing");

    // add water as an override to potions
    ICauldronContents water = CauldronContentTypes.FLUID.of(Fluids.WATER);
    CauldronContentTypes.POTION.setResult(Potions.WATER, water);

    // add all dyes as overrides into color
    for (DyeColor color : DyeColor.values()) {
      CauldronContentTypes.COLOR.setResult(MiscUtil.getColor(color), CauldronContentTypes.DYE.of(color));
    }
  }

  @SubscribeEvent
  void gatherData(GatherDataEvent event) {
    DataGenerator gen = event.getGenerator();
    if (event.includeServer()) {
      gen.addProvider(new RecipesRecipeProvider(gen));
    }
  }

  @SubscribeEvent
  void commonSetup(FMLCommonSetupEvent event) {
    // get a list of all cauldrons
    List<AbstractCauldronBlock> allCauldrons = new ArrayList<>();
    for (Block block : ForgeRegistries.BLOCKS) {
      if (block instanceof AbstractCauldronBlock cauldron) {
        allCauldrons.add(cauldron);
      }
    }

    // add cauldron interactions
    event.enqueueWork(() -> {
      // helper to add to all cauldrons
      BiConsumer<Item,CauldronInteraction> addToAll = (item, interaction) -> {
        for (AbstractCauldronBlock cauldron : allCauldrons) {
          cauldron.interactions.put(item, interaction);
        }
      };

      // any cauldron can be filled
      // TODO: tags?
      addToAll.accept(mushroomStewBucket, new FillCauldronInteraction(mushroomStewCauldron));
      addToAll.accept(potatoSoupBucket,   new FillCauldronInteraction(potatoSoupCauldron));
      addToAll.accept(rabbitStewBucket,   new FillCauldronInteraction(rabbitStewCauldron));
      addToAll.accept(beetrootSoupBucket, new FillCauldronInteraction(beetrootSoupCauldron));
      addToAll.accept(honeyBucket,        new FillCauldronInteraction(honeyCauldron));
      // empty buckets
      MUSHROOM_STEW_CAULDRON_INTERACTIONS.put(Items.BUCKET, new EmptyCauldronInteraction(mushroomStewBucket, SoundEvents.BUCKET_FILL));
      POTATO_SOUP_CAULDRON_INTERACTIONS  .put(Items.BUCKET, new EmptyCauldronInteraction(potatoSoupBucket,   SoundEvents.BUCKET_FILL));
      RABBIT_STEW_CAULDRON_INTERACTION   .put(Items.BUCKET, new EmptyCauldronInteraction(rabbitStewBucket,   SoundEvents.BUCKET_FILL));
      BEETROOT_SOUP_CAULDRON_INTERACTIONS.put(Items.BUCKET, new EmptyCauldronInteraction(beetrootSoupBucket, SoundEvents.BUCKET_FILL));
      HONEY_CAULDRON_INTERACTIONS        .put(Items.BUCKET, new EmptyCauldronInteraction(honeyBucket,        SoundEvents.BUCKET_FILL));

      // empty bowls
      CauldronInteraction.EMPTY.put(Items.MUSHROOM_STEW, new FillCauldronInteraction(mushroomStewCauldron, 1, Items.BOWL));
      CauldronInteraction.EMPTY.put(potatoSoupItem,      new FillCauldronInteraction(potatoSoupCauldron,   1, Items.BOWL));
      CauldronInteraction.EMPTY.put(Items.RABBIT_STEW,   new FillCauldronInteraction(rabbitStewCauldron,   1, Items.BOWL));
      CauldronInteraction.EMPTY.put(Items.BEETROOT_SOUP, new FillCauldronInteraction(beetrootSoupCauldron, 1, Items.BOWL));
      IncreaseLayerCauldronInteraction increaseIntoBowl = new IncreaseLayerCauldronInteraction(Items.BOWL);
      MUSHROOM_STEW_CAULDRON_INTERACTIONS.put(Items.MUSHROOM_STEW, increaseIntoBowl);
      POTATO_SOUP_CAULDRON_INTERACTIONS  .put(potatoSoupItem,      increaseIntoBowl);
      RABBIT_STEW_CAULDRON_INTERACTION   .put(Items.RABBIT_STEW,   increaseIntoBowl);
      BEETROOT_SOUP_CAULDRON_INTERACTIONS.put(Items.BEETROOT_SOUP, increaseIntoBowl);

      // fill bowls
      MUSHROOM_STEW_CAULDRON_INTERACTIONS.put(Items.BOWL, new DecreaseLayerCauldronInteraction(Items.MUSHROOM_STEW, FourLayerCauldronBlock.LEVEL));
      POTATO_SOUP_CAULDRON_INTERACTIONS  .put(Items.BOWL, new DecreaseLayerCauldronInteraction(potatoSoupItem, FourLayerCauldronBlock.LEVEL));
      RABBIT_STEW_CAULDRON_INTERACTION   .put(Items.BOWL, new DecreaseLayerCauldronInteraction(Items.RABBIT_STEW, FourLayerCauldronBlock.LEVEL));
      BEETROOT_SOUP_CAULDRON_INTERACTIONS.put(Items.BOWL, new DecreaseLayerCauldronInteraction(Items.BEETROOT_SOUP, FourLayerCauldronBlock.LEVEL));

      // making the soup
      // mushroom: slight discount (6 mushrooms for 4 bowls) and can use either mushroom (but not both)
      CauldronInteraction mushroomTransform = new TransformCauldronInteraction(2, LayeredCauldronBlock.LEVEL, mushroomStewCauldron);
      CauldronInteraction.WATER.put(Items.BROWN_MUSHROOM, mushroomTransform);
      CauldronInteraction.WATER.put(Items.RED_MUSHROOM, mushroomTransform);
      // potato: slight discount, 4 bowls only costs 6 potatoes instead of 8. Uses 2 more mushrooms
      MUSHROOM_STEW_CAULDRON_INTERACTIONS.put(Items.BAKED_POTATO, new TransformCauldronInteraction(2, FourLayerCauldronBlock.LEVEL, potatoSoupCauldron));
      // rabbit: slight discount, 4 bowls only costs 3 rabbit instead of 4 and does not need carrot. Uses 2 more mushroom
      POTATO_SOUP_CAULDRON_INTERACTIONS.put(Items.COOKED_RABBIT, new TransformCauldronInteraction(1, FourLayerCauldronBlock.LEVEL, rabbitStewCauldron));
      // rabbit: slight discount, 4 bowls only costs 18 beetroot instead of 24
      CauldronInteraction.WATER.put(Items.BEETROOT, new TransformCauldronInteraction(6, LayeredCauldronBlock.LEVEL, beetrootSoupCauldron));

      // honey bottles
      CauldronInteraction.EMPTY.put(Items.HONEY_BOTTLE,   new FillCauldronInteraction(honeyCauldron, 1, Items.GLASS_BOTTLE));
      HONEY_CAULDRON_INTERACTIONS.put(Items.HONEY_BOTTLE, new IncreaseLayerCauldronInteraction(Items.GLASS_BOTTLE));
      HONEY_CAULDRON_INTERACTIONS.put(Items.GLASS_BOTTLE, new DecreaseLayerCauldronInteraction(Items.HONEY_BOTTLE, FourLayerCauldronBlock.LEVEL));
      // not sure if I will bring back solid honey in cauldrons, for now just act like its liquid
      CauldronInteraction honeyToBlock = new EmptyCauldronInteraction(Blocks.HONEY_BLOCK, false, SoundEvents.HONEY_BLOCK_BREAK);
      CauldronInteraction honeyToSugar = new DecreaseLayerCauldronInteraction(new ItemStack(Items.SUGAR, 3), FourLayerCauldronBlock.LEVEL, false, SoundEvents.HONEY_BLOCK_BREAK);
      HONEY_CAULDRON_INTERACTIONS.put(Items.AIR, new FirstCauldronInteraction(honeyToBlock, honeyToSugar));
      HONEY_CAULDRON_INTERACTIONS.put(Items.HONEY_BLOCK, honeyToBlock);
      HONEY_CAULDRON_INTERACTIONS.put(Items.SUGAR, honeyToSugar);
      CauldronInteraction.EMPTY.put(Items.HONEY_BLOCK, new FillCauldronInteraction(honeyCauldron, 4, Items.AIR, SoundEvents.HONEY_BLOCK_PLACE));

      // wet the sponge
      CauldronInteraction.WATER.put(Items.SPONGE, new EmptyCauldronInteraction(Blocks.WET_SPONGE, SoundEvents.GRASS_PLACE));
      // clean the piston
      CauldronInteraction.WATER.put(Items.STICKY_PISTON, new DecreaseLayerCauldronInteraction(Items.PISTON, LayeredCauldronBlock.LEVEL, SoundEvents.GENERIC_SPLASH));

      // make concrete in a cauldron
      for (DyeColor color : DyeColor.values()) {
        CauldronInteraction.WATER.put(getConcretePowder(color).asItem(), new DecreaseLayerCauldronInteraction(getConcrete(color), LayeredCauldronBlock.LEVEL));
      }

      // wash the wool
      addToList(CauldronInteraction.WATER, new DecreaseLayerCauldronInteraction(Blocks.WHITE_WOOL, LayeredCauldronBlock.LEVEL),
                Items.ORANGE_WOOL, Items.MAGENTA_WOOL, Items.LIGHT_BLUE_WOOL, Items.YELLOW_WOOL, Items.LIME_WOOL,
                Items.PINK_WOOL, Items.GRAY_WOOL, Items.LIGHT_GRAY_WOOL, Items.CYAN_WOOL, Items.PURPLE_WOOL,
                Items.BLUE_WOOL, Items.BROWN_WOOL, Items.GREEN_WOOL, Items.RED_WOOL, Items.BLACK_WOOL);
      // wash the bed
      addToList(CauldronInteraction.WATER, new DecreaseLayerCauldronInteraction(Blocks.WHITE_BED, LayeredCauldronBlock.LEVEL),
                Items.ORANGE_BED, Items.MAGENTA_BED, Items.LIGHT_BLUE_BED, Items.YELLOW_BED, Items.LIME_BED,
                Items.PINK_BED, Items.GRAY_BED, Items.LIGHT_GRAY_BED, Items.CYAN_BED, Items.PURPLE_BED,
                Items.BLUE_BED, Items.BROWN_BED, Items.GREEN_BED, Items.RED_BED, Items.BLACK_BED);
      // wash the carpet
      addToList(CauldronInteraction.WATER, new DecreaseLayerCauldronInteraction(Blocks.WHITE_CARPET, LayeredCauldronBlock.LEVEL),
                Items.ORANGE_CARPET, Items.MAGENTA_CARPET, Items.LIGHT_BLUE_CARPET, Items.YELLOW_CARPET, Items.LIME_CARPET,
                Items.PINK_CARPET, Items.GRAY_CARPET, Items.LIGHT_GRAY_CARPET, Items.CYAN_CARPET, Items.PURPLE_CARPET,
                Items.BLUE_CARPET, Items.BROWN_CARPET, Items.GREEN_CARPET, Items.RED_CARPET, Items.BLACK_CARPET);
      // wash the carpeted trapdoor
      addToList(CauldronInteraction.WATER, new DecreaseLayerCauldronInteraction(InspirationsUtility.carpetedTrapdoors.get(DyeColor.WHITE), LayeredCauldronBlock.LEVEL),
                Arrays.stream(DyeColor.values()).filter(dye -> dye != DyeColor.WHITE).map(color -> InspirationsUtility.carpetedTrapdoors.get(color).asItem()).toArray(Item[]::new));

    });

    // inject new cauldron blocks into the leatherworker point of interest
    // it should be as simple as injecting it into the map, but people keep reporting issues with this so just over do it
    List<AbstractCauldronBlock> newCauldrons = ImmutableList.of(honeyCauldron, mushroomStewCauldron, potatoSoupCauldron, beetrootSoupCauldron, rabbitStewCauldron);
    Map<BlockState, PoiType> map = GameData.getBlockStatePointOfInterestTypeMap();
    synchronized (map) {
      Consumer<BlockState> consumer = state -> map.put(state, PoiType.LEATHERWORKER);
      for (AbstractCauldronBlock cauldron : newCauldrons) {
        cauldron.getStateDefinition().getPossibleStates().forEach(consumer);
      }
    }
    synchronized (PoiType.LEATHERWORKER) {
      ImmutableSet.Builder<BlockState> builder = ImmutableSet.builder();
      builder.addAll(PoiType.LEATHERWORKER.matchingStates);
      for (AbstractCauldronBlock cauldron : newCauldrons) {
        builder.addAll(cauldron.getStateDefinition().getPossibleStates());
      }
      PoiType.LEATHERWORKER.matchingStates = builder.build();
    }
  }


  /* Helpers */

  /** Creates a fluid builder */
  private static FluidAttributes.Builder fluidBuilder() {
    return ModelFluidAttributes.builder().sound(SoundEvents.BUCKET_FILL, SoundEvents.BUCKET_EMPTY);
  }

  /** Gets concrete powder for the given color */
  public static Block getConcretePowder(DyeColor color) {
    return switch(color) {
      case WHITE      -> Blocks.WHITE_CONCRETE_POWDER;
      case ORANGE     -> Blocks.ORANGE_CONCRETE_POWDER;
      case MAGENTA    -> Blocks.MAGENTA_CONCRETE_POWDER;
      case LIGHT_BLUE -> Blocks.LIGHT_BLUE_CONCRETE_POWDER;
      case YELLOW     -> Blocks.YELLOW_CONCRETE_POWDER;
      case LIME       -> Blocks.LIME_CONCRETE_POWDER;
      case PINK       -> Blocks.PINK_CONCRETE_POWDER;
      case GRAY       -> Blocks.GRAY_CONCRETE_POWDER;
      case LIGHT_GRAY -> Blocks.LIGHT_GRAY_CONCRETE_POWDER;
      case CYAN       -> Blocks.CYAN_CONCRETE_POWDER;
      case PURPLE     -> Blocks.PURPLE_CONCRETE_POWDER;
      case BLUE       -> Blocks.BLUE_CONCRETE_POWDER;
      case BROWN      -> Blocks.BROWN_CONCRETE_POWDER;
      case GREEN      -> Blocks.GREEN_CONCRETE_POWDER;
      case RED        -> Blocks.RED_CONCRETE_POWDER;
      case BLACK      -> Blocks.BLACK_CONCRETE_POWDER;
    };
  }

  /** Gets concrete for the given color */
  public static Block getConcrete(DyeColor color) {
    return switch(color) {
      case WHITE      -> Blocks.WHITE_CONCRETE;
      case ORANGE     -> Blocks.ORANGE_CONCRETE;
      case MAGENTA    -> Blocks.MAGENTA_CONCRETE;
      case LIGHT_BLUE -> Blocks.LIGHT_BLUE_CONCRETE;
      case YELLOW     -> Blocks.YELLOW_CONCRETE;
      case LIME       -> Blocks.LIME_CONCRETE;
      case PINK       -> Blocks.PINK_CONCRETE;
      case GRAY       -> Blocks.GRAY_CONCRETE;
      case LIGHT_GRAY -> Blocks.LIGHT_GRAY_CONCRETE;
      case CYAN       -> Blocks.CYAN_CONCRETE;
      case PURPLE     -> Blocks.PURPLE_CONCRETE;
      case BLUE       -> Blocks.BLUE_CONCRETE;
      case BROWN      -> Blocks.BROWN_CONCRETE;
      case GREEN      -> Blocks.GREEN_CONCRETE;
      case RED        -> Blocks.RED_CONCRETE;
      case BLACK      -> Blocks.BLACK_CONCRETE;
    };
  }

  /** Adds an interaction for both empty and the passed map */
  private static void addToMapAndCauldron(Map<Item,CauldronInteraction> map, Item item, CauldronInteraction interaction) {
    map.put(item, interaction);
    CauldronInteraction.EMPTY.put(item, interaction);
  }

  /** Adds an interaction for each item in the list */
  private static void addToList(Map<Item,CauldronInteraction> map, CauldronInteraction interaction, Item... items) {
    for (Item item : items) {
      map.put(item, interaction);
    }
  }
}
