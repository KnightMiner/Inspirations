package knightminer.inspirations.recipes;

import knightminer.inspirations.common.ModuleBase;
import knightminer.inspirations.library.MiscUtil;
import knightminer.inspirations.library.recipe.cauldron.CauldronContentTypes;
import knightminer.inspirations.library.recipe.cauldron.contents.ICauldronContents;
import knightminer.inspirations.library.recipe.cauldron.recipe.CauldronRecipe;
import knightminer.inspirations.library.recipe.cauldron.recipe.CauldronTransform;
import knightminer.inspirations.library.recipe.cauldron.special.DyeableCauldronRecipe;
import knightminer.inspirations.library.recipe.cauldron.special.EmptyPotionCauldronRecipe;
import knightminer.inspirations.library.recipe.cauldron.special.FillPotionCauldronRecipe;
import knightminer.inspirations.recipes.block.EnhancedCauldronBlock;
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
import knightminer.inspirations.recipes.tileentity.CauldronTileEntity;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.data.DataGenerator;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BowlFoodItem;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleRecipeSerializer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
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
import slimeknights.mantle.registration.FluidBuilder;
import slimeknights.mantle.registration.ModelFluidAttributes;
import slimeknights.mantle.registration.adapter.BlockEntityTypeRegistryAdapter;
import slimeknights.mantle.registration.adapter.BlockRegistryAdapter;
import slimeknights.mantle.registration.adapter.FluidRegistryAdapter;
import slimeknights.mantle.registration.adapter.ItemRegistryAdapter;
import slimeknights.mantle.registration.adapter.RegistryAdapter;
import slimeknights.mantle.registration.object.EnumObject;

@SuppressWarnings({"WeakerAccess", "unused"})
public class InspirationsRecipes extends ModuleBase {

  // blocks
  public static Block fullAnvil;
  public static Block chippedAnvil;
  public static Block damagedAnvil;

  public static EnhancedCauldronBlock cauldron;
  public static BlockEntityType<CauldronTileEntity> tileCauldron;

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

    mushroomStewBlock = registry.registerFluidBlock(() -> mushroomStew, Material.WATER, 0, "mushroom_stew");
    beetrootSoupBlock = registry.registerFluidBlock(() -> beetrootSoup, Material.WATER, 0, "beetroot_soup");
    rabbitStewBlock = registry.registerFluidBlock(() -> rabbitStew, Material.WATER, 0, "rabbit_stew");
    potatoSoupBlock = registry.registerFluidBlock(() -> potatoSoup, Material.WATER, 0, "potato_soup");
    honeyFluidBlock = registry.registerFluidBlock(() -> honey, Material.WATER, 0, "honey");

    /*
    if (Config.enableAnvilSmashing.get()) {
      registry.registerOverride(SmashingAnvilBlock::new, Blocks.ANVIL);
      registry.registerOverride(SmashingAnvilBlock::new, Blocks.CHIPPED_ANVIL);
      registry.registerOverride(SmashingAnvilBlock::new, Blocks.DAMAGED_ANVIL);
    }
    */
    // TODO: violates rules, probably would just want our own cauldron(s)
//    if (Config.extendedCauldron.get()) {
//      cauldron = registry.registerOverride(EnhancedCauldronBlock::new, Blocks.CAULDRON);
//    }
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

    // cauldron item
//    if (Config.extendedCauldron.get()) {
//      registry.registerBlockItem(cauldron, brewingProps);
//    }
  }

  @SubscribeEvent
  void registerTileEntities(Register<BlockEntityType<?>> event) {
    BlockEntityTypeRegistryAdapter registry = new BlockEntityTypeRegistryAdapter(event.getRegistry());

//    if (Config.extendedCauldron.get()) {
//      tileCauldron = registry.register(CauldronTileEntity::new, cauldron, "cauldron");
//    }
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
    // TODO: cauldrons
//    if (Config.extendedCauldron.get()) {
//      // inject new cauldron blocks into the leatherworker point of interest
//      // it should be as simple as injecting it into the map, but people keep reporting issues with this so just over do it
//      Map<BlockState, PoiType> map = GameData.getBlockStatePointOfInterestTypeMap();
//      synchronized (map) {
//        ImmutableList<BlockState> newStates = cauldron.getStateDefinition().getPossibleStates();
//        synchronized (PoiType.LEATHERWORKER) {
//          ImmutableSet.Builder<BlockState> builder = ImmutableSet.builder();
//          builder.addAll(PoiType.LEATHERWORKER.matchingStates);
//          builder.addAll(newStates);
//          PoiType.LEATHERWORKER.matchingStates = builder.build();
//        }
//        newStates.forEach(state -> map.put(state, PoiType.LEATHERWORKER));
//      }
//    }
  }


  /* Helpers */

  /** Creates a fluid builder */
  private static FluidAttributes.Builder fluidBuilder() {
    return ModelFluidAttributes.builder().sound(SoundEvents.BUCKET_FILL, SoundEvents.BUCKET_EMPTY);
  }
}
