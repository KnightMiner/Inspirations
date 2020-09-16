package knightminer.inspirations.recipes;

import knightminer.inspirations.Inspirations;
import knightminer.inspirations.common.Config;
import knightminer.inspirations.common.ModuleBase;
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
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.block.material.Material;
import net.minecraft.data.DataGenerator;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BucketItem;
import net.minecraft.item.DyeColor;
import net.minecraft.item.DyeItem;
import net.minecraft.item.Food;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.Items;
import net.minecraft.item.SoupItem;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.SpecialRecipeSerializer;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.particles.ParticleType;
import net.minecraft.potion.Potions;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;
import slimeknights.mantle.registration.FluidBuilder;
import slimeknights.mantle.registration.adapter.BlockRegistryAdapter;
import slimeknights.mantle.registration.adapter.FluidRegistryAdapter;
import slimeknights.mantle.registration.adapter.ItemRegistryAdapter;
import slimeknights.mantle.registration.adapter.RegistryAdapter;
import slimeknights.mantle.registration.adapter.TileEntityTypeRegistryAdapter;
import slimeknights.mantle.registration.object.EnumObject;

@SuppressWarnings({"WeakerAccess", "unused"})
public class InspirationsRecipes extends ModuleBase {
  public static final ResourceLocation STILL_FLUID = Inspirations.getResource("block/fluid/colorless");
  public static final ResourceLocation FLOWING_FLUID = Inspirations.getResource("block/fluid/colorless_flow");
  public static final ResourceLocation STILL_TRANSPARENT = Inspirations.getResource("block/fluid/transparent");
  public static final ResourceLocation FLOWING_TRANSPARENT = Inspirations.getResource("block/fluid/transparent_flow");
  public static final ResourceLocation STILL_MILK = Inspirations.getResource("block/fluid/milk");
  public static final ResourceLocation FLOWING_MILK = Inspirations.getResource("block/fluid/milk_flow");

  // blocks
  public static Block fullAnvil;
  public static Block chippedAnvil;
  public static Block damagedAnvil;

  public static EnhancedCauldronBlock cauldron;
  public static TileEntityType<CauldronTileEntity> tileCauldron;

  // items
  public static Item splashBottle;
  public static Item lingeringBottle;
  public static EnumObject<DyeColor,SimpleDyedBottleItem> simpleDyedWaterBottle = EnumObject.empty();
  public static MixedDyedBottleItem mixedDyedWaterBottle;
  public static SoupItem potatoSoupItem;

  // fluids
  public static ForgeFlowingFluid milk;
  // mushroom
  public static ForgeFlowingFluid mushroomStew;
  public static BucketItem mushroomStewBucket;
  public static FlowingFluidBlock mushroomStewBlock;
  // beetroot
  public static ForgeFlowingFluid beetrootSoup;
  public static BucketItem beetrootSoupBucket;
  public static FlowingFluidBlock beetrootSoupBlock;
  // rabbit
  public static ForgeFlowingFluid rabbitStew;
  public static BucketItem rabbitStewBucket;
  public static FlowingFluidBlock rabbitStewBlock;
  // potato
  public static ForgeFlowingFluid potatoSoup;
  public static BucketItem potatoSoupBucket;
  public static FlowingFluidBlock potatoSoupBlock;
  // honey
  public static ForgeFlowingFluid honey;
  public static BucketItem honeyBucket;
  public static FlowingFluidBlock honeyFluidBlock;

  public static BasicParticleType boilingParticle;

  @SubscribeEvent
  void registerFluids(Register<Fluid> event) {
    FluidRegistryAdapter adapter = new FluidRegistryAdapter(event.getRegistry());

    mushroomStew = adapter.register(new FluidBuilder(coloredFluid().color(0xFFCD8C6F).temperature(373).viscosity(1200))
                                        .block(() -> mushroomStewBlock)
                                        .bucket(() -> mushroomStewBucket), "mushroom_stew");
    beetrootSoup = adapter.register(new FluidBuilder(coloredFluid().color(0xFF84160D).temperature(373).viscosity(1100))
                                        .block(() -> beetrootSoupBlock)
                                        .bucket(() -> beetrootSoupBucket), "beetroot_soup");
    rabbitStew = adapter.register(new FluidBuilder(coloredFluid().color(0xFF984A2C).temperature(373).viscosity(1400))
                                      .block(() -> rabbitStewBlock)
                                      .bucket(() -> rabbitStewBucket), "rabbit_stew");
    potatoSoup = adapter.register(new FluidBuilder(coloredFluid().color(0xFFF2DA9F).temperature(373).viscosity(1300))
                                      .block(() -> potatoSoupBlock)
                                      .bucket(() -> potatoSoupBucket), "potato_soup");
    honey = adapter.register(new FluidBuilder(FluidAttributes.builder(STILL_TRANSPARENT, FLOWING_TRANSPARENT).color(0xFFFF9116).viscosity(4000).temperature(373))
                                      .block(() -> honeyFluidBlock)
                                      .bucket(() -> honeyBucket), "honey");
    milk = adapter.register(new FluidBuilder(FluidAttributes.builder(STILL_MILK, FLOWING_MILK).density(1024).viscosity(1024))
                                      .bucket(Items.MILK_BUCKET.delegate), "milk");
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
    if (Config.extendedCauldron.get()) {
      cauldron = registry.registerOverride(EnhancedCauldronBlock::new, Blocks.CAULDRON);
    }
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
        new SoupItem(new Item.Properties().maxStackSize(1)
                                          .group(ItemGroup.FOOD)
                                          .food(new Food.Builder().hunger(8).saturation(0.6F).build())),
        "potato_soup");

    // empty bottles
    Item.Properties brewingProps = new Item.Properties().group(ItemGroup.BREWING);
    splashBottle = registry.register(new EmptyBottleItem(brewingProps, Items.SPLASH_POTION.delegate), "splash_bottle");
    lingeringBottle = registry.register(new EmptyBottleItem(brewingProps, Items.LINGERING_POTION.delegate), "lingering_bottle");

    // dyed bottles
    Item.Properties bottleProps = new Item.Properties()
        .group(ItemGroup.MATERIALS)
        .maxStackSize(16)
        .containerItem(Items.GLASS_BOTTLE);
    simpleDyedWaterBottle = registry.registerEnum(color -> new SimpleDyedBottleItem(bottleProps, DyeItem.getItem(color)), DyeColor.values(), "dyed_bottle");
    mixedDyedWaterBottle = registry.register(new MixedDyedBottleItem(bottleProps), "mixed_dyed_bottle");

    // cauldron item
    if (Config.extendedCauldron.getAsBoolean()) {
      registry.registerBlockItem(cauldron, brewingProps);
    }
  }

  @SubscribeEvent
  void registerTileEntities(Register<TileEntityType<?>> event) {
    TileEntityTypeRegistryAdapter registry = new TileEntityTypeRegistryAdapter(event.getRegistry());

    if (Config.extendedCauldron.get()) {
      tileCauldron = registry.register(CauldronTileEntity::new, cauldron, "cauldron");
    }
  }

  @SubscribeEvent
  void registerParticleTypes(Register<ParticleType<?>> event) {
    RegistryAdapter<ParticleType<?>> registry = new RegistryAdapter<>(event.getRegistry());
    boilingParticle = registry.register(new BasicParticleType(false), "boiling");
  }

  @SubscribeEvent
  void registerSerializers(Register<IRecipeSerializer<?>> event) {
    RegistryAdapter<IRecipeSerializer<?>> registry = new RegistryAdapter<>(event.getRegistry());
    registry.register(new CauldronRecipe.Serializer(), "cauldron");
    registry.register(new EmptyPotionCauldronRecipe.Serializer(), "cauldron_empty_potion");
    registry.register(new FillPotionCauldronRecipe.Serializer(), "cauldron_fill_potion");
    registry.register(new DyeCauldronWaterRecipe.Serializer(), "cauldron_dye_water");
    registry.register(new MixCauldronDyeRecipe.Serializer(), "cauldron_mix_dye");
    registry.register(new DyeableCauldronRecipe.Serializer(DyeableCauldronRecipe.Dye::new), "cauldron_dye_dyeable");
    registry.register(new DyeableCauldronRecipe.Serializer(DyeableCauldronRecipe.Clear::new), "cauldron_clear_dyeable");
    registry.register(new CauldronTransform.Serializer(), "cauldron_transform");
    registry.register(new PotionFermentCauldronTransform.Serializer(), "cauldron_potion_ferment");

    registry.register(new SpecialRecipeSerializer<>(EmptyBucketCauldronRecipe::new), "cauldron_empty_bucket");
    registry.register(new SpecialRecipeSerializer<>(FillBucketCauldronRecipe::new), "cauldron_fill_bucket");
    registry.register(new SpecialRecipeSerializer<>(FillDyedBottleRecipe::new), "cauldron_fill_dyed_bottle");
    registry.register(new SpecialRecipeSerializer<>(RemoveBannerPatternCauldronRecipe::new), "cauldron_remove_banner_pattern");
    registry.register(new BrewingCauldronRecipe.Serializer(BrewingCauldronRecipe.Vanilla::new), "cauldron_potion_brewing");
    registry.register(new BrewingCauldronRecipe.Serializer(BrewingCauldronRecipe.Forge::new), "cauldron_forge_brewing");

    // add water as an override to potions
    ICauldronContents water = CauldronContentTypes.FLUID.of(Fluids.WATER);
    CauldronContentTypes.POTION.setResult(Potions.WATER, water);

    // add all dyes as overrides into color
    for (DyeColor color : DyeColor.values()) {
      CauldronContentTypes.COLOR.setResult(color.getColorValue(), CauldronContentTypes.DYE.of(color));
    }
  }

  @SubscribeEvent
  void gatherData(GatherDataEvent event) {
    DataGenerator gen = event.getGenerator();
    if (event.includeServer()) {
      gen.addProvider(new RecipesRecipeProvider(gen));
    }
  }

  /**
   * Creates a fluid attribute for the generic colorless fluid
   * @return  Fluid attributes builder
   */
  private static FluidAttributes.Builder coloredFluid() {
    return FluidAttributes.builder(STILL_FLUID, FLOWING_FLUID);
  }
}
