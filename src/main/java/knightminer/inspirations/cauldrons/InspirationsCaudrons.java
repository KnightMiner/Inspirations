package knightminer.inspirations.cauldrons;

import knightminer.inspirations.Inspirations;
import knightminer.inspirations.cauldrons.block.BoilingFourLayerCauldronBlock;
import knightminer.inspirations.cauldrons.block.BoilingThreeLayerCauldronBlock;
import knightminer.inspirations.cauldrons.block.DyeCauldronBlock;
import knightminer.inspirations.cauldrons.block.FourLayerCauldronBlock;
import knightminer.inspirations.cauldrons.block.PotionCauldronBlock;
import knightminer.inspirations.cauldrons.block.SuspiciousStewCauldronBlock;
import knightminer.inspirations.cauldrons.block.entity.DyeCauldronBlockEntity;
import knightminer.inspirations.cauldrons.block.entity.PotionCauldronBlockEntity;
import knightminer.inspirations.cauldrons.block.entity.SuspiciousStewCauldronBlockEntity;
import knightminer.inspirations.cauldrons.data.FluidBlockstateModelProvider;
import knightminer.inspirations.cauldrons.data.FluidBucketModelProvider;
import knightminer.inspirations.cauldrons.data.FluidTextureProvider;
import knightminer.inspirations.cauldrons.data.RecipesRecipeProvider;
import knightminer.inspirations.cauldrons.interaction.DecreaseLayerCauldronInteraction;
import knightminer.inspirations.cauldrons.interaction.EmptyCauldronInteraction;
import knightminer.inspirations.cauldrons.interaction.FillCauldronInteraction;
import knightminer.inspirations.cauldrons.interaction.FirstCauldronInteraction;
import knightminer.inspirations.cauldrons.interaction.IncreaseLayerCauldronInteraction;
import knightminer.inspirations.cauldrons.interaction.TransformCauldronInteraction;
import knightminer.inspirations.cauldrons.interaction.dye.DyeItemCauldronInteraction;
import knightminer.inspirations.cauldrons.interaction.dye.DyeLeatherItemCauldronInteraction;
import knightminer.inspirations.cauldrons.interaction.dye.DyeWaterCauldronInteraction;
import knightminer.inspirations.cauldrons.interaction.dye.DyedBottleIntoDyeCauldronInteraction;
import knightminer.inspirations.cauldrons.interaction.dye.DyedBottleIntoEmptyCauldronInteraction;
import knightminer.inspirations.cauldrons.interaction.dye.DyedBottleIntoWaterCauldronInteraction;
import knightminer.inspirations.cauldrons.interaction.dye.FillDyedBottleCauldronInteraction;
import knightminer.inspirations.cauldrons.interaction.dye.MixDyeCauldronInteraction;
import knightminer.inspirations.cauldrons.interaction.dye.WaterBottleIntoDyeCauldronInteraction;
import knightminer.inspirations.cauldrons.interaction.potion.BrewingCauldronInteraction;
import knightminer.inspirations.cauldrons.interaction.potion.FillPotionCauldronInteraction;
import knightminer.inspirations.cauldrons.interaction.potion.PotionIntoEmptyInteraction;
import knightminer.inspirations.cauldrons.interaction.potion.PotionIntoPotionCauldron;
import knightminer.inspirations.cauldrons.interaction.potion.TipArrowCauldronInteraction;
import knightminer.inspirations.cauldrons.interaction.potion.WaterBottleIntoWaterInteraction;
import knightminer.inspirations.cauldrons.interaction.stew.DecreaseSuspiciousStewCauldronInteraction;
import knightminer.inspirations.cauldrons.interaction.stew.MixSuspiciousStewCauldronInteraction;
import knightminer.inspirations.cauldrons.interaction.stew.SuspiciousStewIntoEmptyCauldronInteraction;
import knightminer.inspirations.cauldrons.interaction.stew.SuspiciousStewingCauldronInteraction;
import knightminer.inspirations.cauldrons.item.EmptyBottleItem;
import knightminer.inspirations.cauldrons.item.MilkBottleItem;
import knightminer.inspirations.cauldrons.item.MixedDyedBottleItem;
import knightminer.inspirations.cauldrons.item.SimpleDyedBottleItem;
import knightminer.inspirations.cauldrons.recipe.BottleBrewingRecipe;
import knightminer.inspirations.common.Config;
import knightminer.inspirations.common.ModuleBase;
import knightminer.inspirations.library.MiscUtil;
import knightminer.inspirations.library.recipe.cauldron.CauldronRegistry;
import knightminer.inspirations.tools.InspirationsTools;
import knightminer.inspirations.utility.InspirationsUtility;
import net.minecraft.Util;
import net.minecraft.core.Registry;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.ai.village.poi.PoiTypes;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.DyeableLeatherItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.AbstractCauldronBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.SoundActions;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.GameData;
import net.minecraftforge.registries.RegisterEvent;
import slimeknights.mantle.datagen.MantleTags;
import slimeknights.mantle.datagen.MantleValues;
import slimeknights.mantle.fluid.TextureFluidType;
import slimeknights.mantle.item.ContainerFoodItem.FluidContainerFoodItem;
import slimeknights.mantle.registration.FluidBuilder;
import slimeknights.mantle.registration.adapter.BlockEntityTypeRegistryAdapter;
import slimeknights.mantle.registration.adapter.BlockRegistryAdapter;
import slimeknights.mantle.registration.adapter.FluidRegistryAdapter;
import slimeknights.mantle.registration.adapter.ItemRegistryAdapter;
import slimeknights.mantle.registration.adapter.RegistryAdapter;
import slimeknights.mantle.registration.object.EnumObject;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

import static knightminer.inspirations.library.recipe.cauldron.CauldronRegistry.ALL_CAULDRONS;
import static knightminer.inspirations.library.recipe.cauldron.CauldronRegistry.exactBlock;
import static knightminer.inspirations.library.recipe.cauldron.CauldronRegistry.fluidTag;
import static knightminer.inspirations.library.recipe.cauldron.CauldronRegistry.itemTag;

public class InspirationsCaudrons extends ModuleBase {
  /** Interactions for the mushroom stew cauldron */
  public static final Map<Item,CauldronInteraction> MUSHROOM_STEW_CAULDRON_INTERACTIONS = CauldronInteraction.newInteractionMap();
  /** Interactions for the beetroot soup cauldron */
  public static final Map<Item,CauldronInteraction> BEETROOT_SOUP_CAULDRON_INTERACTIONS = CauldronInteraction.newInteractionMap();
  /** Interactions for the rabbit stew cauldron */
  public static final Map<Item,CauldronInteraction> RABBIT_STEW_CAULDRON_INTERACTIONS = CauldronInteraction.newInteractionMap();
  /** Interactions for the potato soup cauldron */
  public static final Map<Item,CauldronInteraction> POTATO_SOUP_CAULDRON_INTERACTIONS = CauldronInteraction.newInteractionMap();
  /** Interactions for the honey cauldron */
  public static final Map<Item,CauldronInteraction> HONEY_CAULDRON_INTERACTIONS = CauldronInteraction.newInteractionMap();
  /** Interactions for the milk cauldron */
  public static final Map<Item,CauldronInteraction> MILK_CAULDRON_INTERACTIONS = CauldronInteraction.newInteractionMap();
  /** Interactions for the dye cauldron */
  public static final Map<Item,CauldronInteraction> DYE_CAULDRON_INTERACTIONS = CauldronInteraction.newInteractionMap();
  /** Interactions for the dye cauldron */
  public static final Map<Item,CauldronInteraction> POTION_CAULDRON_INTERACTIONS = CauldronInteraction.newInteractionMap();
  /** Interactions for the suspicious stew cauldron */
  public static final Map<Item,CauldronInteraction> SUSPICIOUS_STEW_CAULDRON_INTERACTIONS = CauldronInteraction.newInteractionMap();

  // blocks
  public static FourLayerCauldronBlock mushroomStewCauldron, beetrootSoupCauldron, rabbitStewCauldron, potatoSoupCauldron;
  public static FourLayerCauldronBlock honeyCauldron, milkCauldron, suspiciousStewCauldron;
  public static LayeredCauldronBlock dyeCauldron, potionCauldron;
  @Nullable
  private static LayeredCauldronBlock waterCauldron;

  public static BlockEntityType<DyeCauldronBlockEntity> dyeCauldronEntity;
  public static BlockEntityType<PotionCauldronBlockEntity> potionCauldronEntity;
  public static BlockEntityType<SuspiciousStewCauldronBlockEntity> suspiciousStewCauldronEntity;

  /** Standard damage source for melting most mobs */
  public static final ResourceKey<DamageType> DAMAGE_BOIL = ResourceKey.create(Registries.DAMAGE_TYPE, Inspirations.getResource("boiling"));

  // items
  public static Item splashBottle;
  public static Item lingeringBottle;
  public static EnumObject<DyeColor,SimpleDyedBottleItem> simpleDyedWaterBottle = EnumObject.empty();
  public static MixedDyedBottleItem mixedDyedWaterBottle;
  public static Item potatoSoupItem;
  public static Item milkBottle;

  // fluids
  // mushroom
  public static FluidType mushroomStewType;
  public static ForgeFlowingFluid mushroomStew;
  public static BucketItem mushroomStewBucket;
  public static LiquidBlock mushroomStewBlock;
  // beetroot
  public static FluidType beetrootSoupType;
  public static ForgeFlowingFluid beetrootSoup;
  public static BucketItem beetrootSoupBucket;
  public static LiquidBlock beetrootSoupBlock;
  // rabbit
  public static FluidType rabbitStewType;
  public static ForgeFlowingFluid rabbitStew;
  public static BucketItem rabbitStewBucket;
  public static LiquidBlock rabbitStewBlock;
  // potato
  public static FluidType potatoSoupType;
  public static ForgeFlowingFluid potatoSoup;
  public static BucketItem potatoSoupBucket;
  public static LiquidBlock potatoSoupBlock;
  // honey
  public static FluidType honeyType;
  public static ForgeFlowingFluid honey;
  public static BucketItem honeyBucket;
  public static LiquidBlock honeyFluidBlock;

  public static SimpleParticleType boilingParticle;

  public InspirationsCaudrons() {
    ForgeMod.enableMilkFluid();
  }

  @SubscribeEvent
  void register(RegisterEvent event) {
    ResourceKey<? extends Registry<?>> registryKey = event.getRegistryKey();
    if (registryKey == ForgeRegistries.Keys.FLUID_TYPES) {
      RegistryAdapter<FluidType> adapter = new RegistryAdapter<>(ForgeRegistries.FLUID_TYPES.get());
      mushroomStewType = adapter.register(new TextureFluidType(fluidBuilder("mushroom_stew").temperature(373).viscosity(1200)), "mushroom_stew");
      beetrootSoupType = adapter.register(new TextureFluidType(fluidBuilder("beetroot_soup").temperature(373).viscosity(1100)), "beetroot_soup");
      rabbitStewType = adapter.register(new TextureFluidType(fluidBuilder("rabbit_stew").temperature(373).viscosity(1400)), "rabbit_stew");
      potatoSoupType = adapter.register(new TextureFluidType(fluidBuilder("potato_soup").temperature(373).viscosity(4000)), "potato_soup");
      honeyType = adapter.register(new TextureFluidType(fluidBuilder("honey").temperature(373).viscosity(4000)), "honey");
    }
    else if (registryKey == Registries.FLUID) {
      FluidRegistryAdapter adapter = new FluidRegistryAdapter(ForgeRegistries.FLUIDS);

      mushroomStew = adapter.register(FluidBuilder.create(() -> mushroomStewType).block(() -> mushroomStewBlock).bucket(() -> mushroomStewBucket), "mushroom_stew");
      beetrootSoup = adapter.register(FluidBuilder.create(() -> beetrootSoupType).block(() -> beetrootSoupBlock).bucket(() -> beetrootSoupBucket), "beetroot_soup");
      rabbitStew = adapter.register(FluidBuilder.create(() -> rabbitStewType).block(() -> rabbitStewBlock).bucket(() -> rabbitStewBucket), "rabbit_stew");
      potatoSoup = adapter.register(FluidBuilder.create(() -> potatoSoupType).block(() -> potatoSoupBlock).bucket(() -> potatoSoupBucket), "potato_soup");
      honey = adapter.register(FluidBuilder.create(() -> honeyType).block(() -> honeyFluidBlock).bucket(() -> honeyBucket), "honey");
    }
    else if (registryKey == Registries.BLOCK) {
      BlockRegistryAdapter registry = new BlockRegistryAdapter(ForgeRegistries.BLOCKS);

      BlockBehaviour.Properties cauldronProps = Properties.copy(Blocks.CAULDRON);
      mushroomStewCauldron = registry.register(new BoilingFourLayerCauldronBlock(cauldronProps, MUSHROOM_STEW_CAULDRON_INTERACTIONS), "mushroom_stew_cauldron");
      beetrootSoupCauldron = registry.register(new BoilingFourLayerCauldronBlock(cauldronProps, BEETROOT_SOUP_CAULDRON_INTERACTIONS), "beetroot_soup_cauldron");
      rabbitStewCauldron = registry.register(new BoilingFourLayerCauldronBlock(cauldronProps, RABBIT_STEW_CAULDRON_INTERACTIONS), "rabbit_stew_cauldron");
      potatoSoupCauldron = registry.register(new BoilingFourLayerCauldronBlock(cauldronProps, POTATO_SOUP_CAULDRON_INTERACTIONS), "potato_soup_cauldron");
      honeyCauldron = registry.register(new FourLayerCauldronBlock(cauldronProps, HONEY_CAULDRON_INTERACTIONS), "honey_cauldron");
      milkCauldron = registry.register(new BoilingFourLayerCauldronBlock(cauldronProps, MILK_CAULDRON_INTERACTIONS), "milk_cauldron");

      mushroomStewBlock = registry.registerFluidBlock(() -> mushroomStew, MapColor.TERRACOTTA_LIGHT_GRAY, 0, "mushroom_stew");
      beetrootSoupBlock = registry.registerFluidBlock(() -> beetrootSoup, MapColor.NETHER, 0, "beetroot_soup");
      rabbitStewBlock = registry.registerFluidBlock(() -> rabbitStew, MapColor.PODZOL, 0, "rabbit_stew");
      potatoSoupBlock = registry.registerFluidBlock(() -> potatoSoup, MapColor.TERRACOTTA_WHITE, 0, "potato_soup");
      honeyFluidBlock = registry.registerFluidBlock(() -> honey, MapColor.COLOR_ORANGE, 0, "honey");

      dyeCauldron = registry.register(new DyeCauldronBlock(cauldronProps), "dye_cauldron");
      potionCauldron = registry.register(new PotionCauldronBlock(cauldronProps), "potion_cauldron");
      suspiciousStewCauldron = registry.register(new SuspiciousStewCauldronBlock(cauldronProps), "suspicious_stew_cauldron");

      if (Config.replaceVanillaCauldrons.getAsBoolean()) {
        waterCauldron = registry.registerOverride(props -> new BoilingThreeLayerCauldronBlock(props, LayeredCauldronBlock.RAIN, CauldronInteraction.WATER), Blocks.WATER_CAULDRON);
      }
    }
    else if (registryKey == Registries.BLOCK_ENTITY_TYPE) {
      BlockEntityTypeRegistryAdapter registry = new BlockEntityTypeRegistryAdapter(ForgeRegistries.BLOCK_ENTITY_TYPES);

      dyeCauldronEntity = registry.register(DyeCauldronBlockEntity::new, dyeCauldron, "dye_cauldron");
      potionCauldronEntity = registry.register(PotionCauldronBlockEntity::new, potionCauldron, "potion_cauldron");
      suspiciousStewCauldronEntity = registry.register(SuspiciousStewCauldronBlockEntity::new, suspiciousStewCauldron, "suspicious_stew_cauldron");
    }
    else if (registryKey == Registries.ITEM) {
      ItemRegistryAdapter registry = new ItemRegistryAdapter(ForgeRegistries.ITEMS);

      // buckets
      mushroomStewBucket = registry.registerBucket(() -> mushroomStew, "mushroom_stew");
      beetrootSoupBucket = registry.registerBucket(() -> beetrootSoup, "beetroot_soup");
      rabbitStewBucket = registry.registerBucket(() -> rabbitStew, "rabbit_stew");
      potatoSoupBucket = registry.registerBucket(() -> potatoSoup, "potato_soup");
      honeyBucket = registry.registerBucket(() -> honey, "honey");

      // potato soup
      potatoSoupItem = registry.register(new FluidContainerFoodItem(
        new Item.Properties().stacksTo(1).food(new FoodProperties.Builder().nutrition(8).saturationMod(0.6F).build()),
        () -> new FluidStack(potatoSoup, MantleValues.BOWL)
      ), "potato_soup");

      // empty bottles
      Item.Properties props = new Item.Properties();
      splashBottle = registry.register(new EmptyBottleItem(props, () -> Items.SPLASH_POTION), "splash_bottle");
      lingeringBottle = registry.register(new EmptyBottleItem(props, () -> Items.LINGERING_POTION), "lingering_bottle");
      Item.Properties bottleProps = new Item.Properties().stacksTo(16).craftRemainder(Items.GLASS_BOTTLE);
      milkBottle = registry.register(new MilkBottleItem(bottleProps), "milk_bottle");

      // dyed bottles
      simpleDyedWaterBottle = registry.registerEnum(color -> new SimpleDyedBottleItem(bottleProps, DyeItem.byColor(color)), DyeColor.values(), "dyed_bottle");
      mixedDyedWaterBottle = registry.register(new MixedDyedBottleItem(bottleProps), "mixed_dyed_bottle");
    }
    else if (registryKey == Registries.PARTICLE_TYPE) {
      RegistryAdapter<ParticleType<?>> registry = new RegistryAdapter<>(ForgeRegistries.PARTICLE_TYPES);
      boilingParticle = registry.register(new SimpleParticleType(false), "boiling");
    }
  }

  @SubscribeEvent
  void gatherData(GatherDataEvent event) {
    DataGenerator gen = event.getGenerator();
    PackOutput packOutput = gen.getPackOutput();
    gen.addProvider(event.includeServer(), new RecipesRecipeProvider(packOutput));
    boolean client = event.includeClient();
    gen.addProvider(client, new FluidBlockstateModelProvider(packOutput, Inspirations.modID));
    gen.addProvider(client, new FluidBucketModelProvider(packOutput, Inspirations.modID));
    gen.addProvider(client, new FluidTextureProvider(packOutput));
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

      // make concrete in a cauldron
      if (Config.cauldronConcrete.getAsBoolean()) {
        for (DyeColor dye : DyeColor.values()) {
          CauldronInteraction.WATER.put(getConcretePowder(dye).asItem(), new DecreaseLayerCauldronInteraction(getConcrete(dye), LayeredCauldronBlock.LEVEL));
        }
      }

      // clean the piston
      if (Config.cauldronCleanStickyPiston.getAsBoolean()) {
        CauldronInteraction.WATER.put(Items.STICKY_PISTON, new DecreaseLayerCauldronInteraction(Items.PISTON, LayeredCauldronBlock.LEVEL, SoundEvents.GENERIC_SPLASH));
      }

      // wet the sponge
      if (Config.cauldronWetSponge.getAsBoolean()) {
        CauldronInteraction spongeWet = new EmptyCauldronInteraction(Blocks.WET_SPONGE, SoundEvents.GRASS_PLACE);
        CauldronInteraction.WATER.put(Items.SPONGE, spongeWet);
        DYE_CAULDRON_INTERACTIONS.put(Items.SPONGE, spongeWet);
      }

      // wash the wool
      if (Config.cauldronWashWool.getAsBoolean()) {
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

        // wash the compass
        CauldronInteraction.WATER.put(InspirationsTools.dimensionCompass, CauldronInteraction.DYED_ITEM);
      }

      // milk
      if (Config.enableCauldronMilk.getAsBoolean()) {
        // milk buckets
        addToAll.accept(Items.MILK_BUCKET, new FillCauldronInteraction(milkCauldron));
        MILK_CAULDRON_INTERACTIONS.put(Items.BUCKET, new EmptyCauldronInteraction(Items.MILK_BUCKET, SoundEvents.BUCKET_FILL));

        // milk bottles
        CauldronInteraction.EMPTY.put(milkBottle, new FillCauldronInteraction(milkCauldron, 1, Items.GLASS_BOTTLE));
        MILK_CAULDRON_INTERACTIONS.put(milkBottle, IncreaseLayerCauldronInteraction.fourLevel(Items.GLASS_BOTTLE));
        MILK_CAULDRON_INTERACTIONS.put(Items.GLASS_BOTTLE, new DecreaseLayerCauldronInteraction(milkBottle, FourLayerCauldronBlock.LEVEL));
      }

      // honey
      if (Config.enableCauldronHoney.getAsBoolean()) {
        // honey buckets
        CauldronInteraction fillHoney = new FillCauldronInteraction(honeyCauldron);
        addToAll.accept(honeyBucket, fillHoney);
        CauldronRegistry.register(ALL_CAULDRONS, fluidTag(MantleTags.Fluids.HONEY), fillHoney);
        HONEY_CAULDRON_INTERACTIONS.put(Items.BUCKET, new EmptyCauldronInteraction(honeyBucket, SoundEvents.BUCKET_FILL));

        // honey bottles
        CauldronInteraction.EMPTY.put(Items.HONEY_BOTTLE,   new FillCauldronInteraction(honeyCauldron, 1, Items.GLASS_BOTTLE));
        HONEY_CAULDRON_INTERACTIONS.put(Items.HONEY_BOTTLE, IncreaseLayerCauldronInteraction.fourLevel(Items.GLASS_BOTTLE));
        HONEY_CAULDRON_INTERACTIONS.put(Items.GLASS_BOTTLE, new DecreaseLayerCauldronInteraction(Items.HONEY_BOTTLE, FourLayerCauldronBlock.LEVEL));

        // honey blocks
        // not sure if I will bring back solid honey in cauldrons, for now just act like its liquid
        CauldronInteraction honeyToBlock = new EmptyCauldronInteraction(Blocks.HONEY_BLOCK, false, SoundEvents.HONEY_BLOCK_BREAK);
        CauldronInteraction honeyToSugar = new DecreaseLayerCauldronInteraction(new ItemStack(Items.SUGAR, 3), FourLayerCauldronBlock.LEVEL, false, SoundEvents.HONEY_BLOCK_BREAK);
        HONEY_CAULDRON_INTERACTIONS.put(Items.AIR, new FirstCauldronInteraction(honeyToBlock, honeyToSugar));
        HONEY_CAULDRON_INTERACTIONS.put(Items.HONEY_BLOCK, honeyToBlock);
        HONEY_CAULDRON_INTERACTIONS.put(Items.SUGAR, honeyToSugar);
        CauldronInteraction.EMPTY.put(Items.HONEY_BLOCK, new FillCauldronInteraction(honeyCauldron, 4, Items.AIR, SoundEvents.HONEY_BLOCK_PLACE));
      }

      // soup
      if (Config.enableCauldronSoups.getAsBoolean()) {
        // mushroom
        CauldronInteraction fillMushroomStew = new FillCauldronInteraction(mushroomStewCauldron);
        addToAll.accept(mushroomStewBucket, fillMushroomStew);
        CauldronRegistry.register(ALL_CAULDRONS, fluidTag(MantleTags.Fluids.MUSHROOM_STEW), fillMushroomStew);
        // potato
        addToAll.accept(potatoSoupBucket, new FillCauldronInteraction(potatoSoupCauldron));
        // rabbit
        CauldronInteraction fillRabbitStew = new FillCauldronInteraction(rabbitStewCauldron);
        addToAll.accept(rabbitStewBucket, fillRabbitStew);
        CauldronRegistry.register(ALL_CAULDRONS, fluidTag(MantleTags.Fluids.RABBIT_STEW), fillRabbitStew);
        // beetroot
        CauldronInteraction fillBeetrootSoup = new FillCauldronInteraction(beetrootSoupCauldron);
        addToAll.accept(beetrootSoupBucket, fillBeetrootSoup);
        CauldronRegistry.register(ALL_CAULDRONS, fluidTag(MantleTags.Fluids.BEETROOT_SOUP), fillBeetrootSoup);
        // empty buckets
        MUSHROOM_STEW_CAULDRON_INTERACTIONS.put(Items.BUCKET, new EmptyCauldronInteraction(mushroomStewBucket, SoundEvents.BUCKET_FILL));
        POTATO_SOUP_CAULDRON_INTERACTIONS  .put(Items.BUCKET, new EmptyCauldronInteraction(potatoSoupBucket,   SoundEvents.BUCKET_FILL));
        RABBIT_STEW_CAULDRON_INTERACTIONS  .put(Items.BUCKET, new EmptyCauldronInteraction(rabbitStewBucket,   SoundEvents.BUCKET_FILL));
        BEETROOT_SOUP_CAULDRON_INTERACTIONS.put(Items.BUCKET, new EmptyCauldronInteraction(beetrootSoupBucket, SoundEvents.BUCKET_FILL));

        // empty bowls
        CauldronInteraction.EMPTY.put(Items.MUSHROOM_STEW, new FillCauldronInteraction(mushroomStewCauldron, 1, Items.BOWL));
        CauldronInteraction.EMPTY.put(potatoSoupItem,      new FillCauldronInteraction(potatoSoupCauldron,   1, Items.BOWL));
        CauldronInteraction.EMPTY.put(Items.RABBIT_STEW,   new FillCauldronInteraction(rabbitStewCauldron,   1, Items.BOWL));
        CauldronInteraction.EMPTY.put(Items.BEETROOT_SOUP, new FillCauldronInteraction(beetrootSoupCauldron, 1, Items.BOWL));
        IncreaseLayerCauldronInteraction increaseIntoBowl = IncreaseLayerCauldronInteraction.fourLevel(Items.BOWL);
        MUSHROOM_STEW_CAULDRON_INTERACTIONS.put(Items.MUSHROOM_STEW, increaseIntoBowl);
        POTATO_SOUP_CAULDRON_INTERACTIONS  .put(potatoSoupItem,      increaseIntoBowl);
        RABBIT_STEW_CAULDRON_INTERACTIONS  .put(Items.RABBIT_STEW,   increaseIntoBowl);
        BEETROOT_SOUP_CAULDRON_INTERACTIONS.put(Items.BEETROOT_SOUP, increaseIntoBowl);

        // fill bowls
        MUSHROOM_STEW_CAULDRON_INTERACTIONS.put(Items.BOWL, new DecreaseLayerCauldronInteraction(Items.MUSHROOM_STEW, FourLayerCauldronBlock.LEVEL));
        POTATO_SOUP_CAULDRON_INTERACTIONS  .put(Items.BOWL, new DecreaseLayerCauldronInteraction(potatoSoupItem,      FourLayerCauldronBlock.LEVEL));
        RABBIT_STEW_CAULDRON_INTERACTIONS  .put(Items.BOWL, new DecreaseLayerCauldronInteraction(Items.RABBIT_STEW,   FourLayerCauldronBlock.LEVEL));
        BEETROOT_SOUP_CAULDRON_INTERACTIONS.put(Items.BOWL, new DecreaseLayerCauldronInteraction(Items.BEETROOT_SOUP, FourLayerCauldronBlock.LEVEL));

        // making the soup
        // mushroom: slight discount (6 mushrooms for 4 bowls) and can use either mushroom (but not both)
        // use water if milk is disabled
        if (Config.enableCauldronMilk.getAsBoolean()) {
          CauldronInteraction mushroomTransform = new TransformCauldronInteraction(true, 2, FourLayerCauldronBlock.LEVEL, mushroomStewCauldron);
          MILK_CAULDRON_INTERACTIONS.put(Items.BROWN_MUSHROOM, mushroomTransform);
          MILK_CAULDRON_INTERACTIONS.put(Items.RED_MUSHROOM, mushroomTransform);
          CauldronRegistry.register(exactBlock(milkCauldron), itemTag(Tags.Items.MUSHROOMS), mushroomTransform);
        } else {
          CauldronInteraction mushroomTransform = new TransformCauldronInteraction(true, 2, LayeredCauldronBlock.LEVEL, mushroomStewCauldron);
          CauldronInteraction.WATER.put(Items.BROWN_MUSHROOM, mushroomTransform);
          CauldronInteraction.WATER.put(Items.RED_MUSHROOM, mushroomTransform);
          CauldronRegistry.register(exactBlock(Blocks.WATER_CAULDRON), itemTag(Tags.Items.MUSHROOMS), mushroomTransform);
        }
        // potato: slight discount, 4 bowls only costs 6 potatoes instead of 8. Uses 2 more mushrooms
        MUSHROOM_STEW_CAULDRON_INTERACTIONS.put(Items.BAKED_POTATO, new TransformCauldronInteraction(true, 2, FourLayerCauldronBlock.LEVEL, potatoSoupCauldron));
        // rabbit: slight discount, 4 bowls only costs 3 rabbit instead of 4 and does not need carrot. Uses 2 more mushroom
        POTATO_SOUP_CAULDRON_INTERACTIONS.put(Items.COOKED_RABBIT, new TransformCauldronInteraction(true, 1, FourLayerCauldronBlock.LEVEL, rabbitStewCauldron));
        // rabbit: slight discount, 4 bowls only costs 18 beetroot instead of 24
        CauldronInteraction.WATER.put(Items.BEETROOT, new TransformCauldronInteraction(true, 6, LayeredCauldronBlock.LEVEL, beetrootSoupCauldron));

        // suspicious stew
        CauldronInteraction.EMPTY.put(Items.SUSPICIOUS_STEW, SuspiciousStewIntoEmptyCauldronInteraction.INSTANCE);
        SUSPICIOUS_STEW_CAULDRON_INTERACTIONS.put(Items.BOWL, DecreaseSuspiciousStewCauldronInteraction.INSTANCE);
        SUSPICIOUS_STEW_CAULDRON_INTERACTIONS.put(Items.SUSPICIOUS_STEW, MixSuspiciousStewCauldronInteraction.INSTANCE);
        CauldronRegistry.register(exactBlock(mushroomStewCauldron), itemTag(ItemTags.SMALL_FLOWERS), SuspiciousStewingCauldronInteraction.INSTANCE);
      }

      // dye cauldrons
      if (Config.enableCauldronDyeing.getAsBoolean()) {
        for (DyeColor dye : DyeColor.values()) {
          Item dyeItem = switch (dye) {
            case WHITE -> Items.WHITE_DYE;
            case ORANGE -> Items.ORANGE_DYE;
            case MAGENTA -> Items.MAGENTA_DYE;
            case LIGHT_BLUE -> Items.LIGHT_BLUE_DYE;
            case YELLOW -> Items.YELLOW_DYE;
            case LIME -> Items.LIME_DYE;
            case PINK -> Items.PINK_DYE;
            case GRAY -> Items.GRAY_DYE;
            case LIGHT_GRAY -> Items.LIGHT_GRAY_DYE;
            case CYAN -> Items.CYAN_DYE;
            case PURPLE -> Items.PURPLE_DYE;
            case BLUE -> Items.BLUE_DYE;
            case BROWN -> Items.BROWN_DYE;
            case GREEN -> Items.GREEN_DYE;
            case RED -> Items.RED_DYE;
            case BLACK -> Items.BLACK_DYE;
          };

          // dyes into cauldrons
          CauldronInteraction.WATER.put(dyeItem, new DyeWaterCauldronInteraction(dye));
          DYE_CAULDRON_INTERACTIONS.put(dyeItem, new MixDyeCauldronInteraction(dye));

          // dyed bottles into cauldrons
          int color = MiscUtil.getColor(dye);
          CauldronInteraction.EMPTY.put(simpleDyedWaterBottle.get(dye), new DyedBottleIntoEmptyCauldronInteraction(color));
          CauldronInteraction.WATER.put(simpleDyedWaterBottle.get(dye), new DyedBottleIntoWaterCauldronInteraction(color));
          DYE_CAULDRON_INTERACTIONS.put(simpleDyedWaterBottle.get(dye), new DyedBottleIntoDyeCauldronInteraction(color));
        }

        // mixed bottle into cauldrons
        CauldronInteraction.EMPTY.put(mixedDyedWaterBottle, new DyedBottleIntoEmptyCauldronInteraction(null));
        CauldronInteraction.WATER.put(mixedDyedWaterBottle, new DyedBottleIntoWaterCauldronInteraction(null));
        DYE_CAULDRON_INTERACTIONS.put(mixedDyedWaterBottle, new DyedBottleIntoDyeCauldronInteraction(null));

        // water into dyed cauldrons
        DYE_CAULDRON_INTERACTIONS.put(Items.POTION, new WaterBottleIntoDyeCauldronInteraction(Items.GLASS_BOTTLE, 0x808080));
        DYE_CAULDRON_INTERACTIONS.put(Items.SPLASH_POTION, new WaterBottleIntoDyeCauldronInteraction(splashBottle, 0x808080));
        DYE_CAULDRON_INTERACTIONS.put(Items.LINGERING_POTION, new WaterBottleIntoDyeCauldronInteraction(lingeringBottle, 0x808080));

        // fill the bottle
        DYE_CAULDRON_INTERACTIONS.put(Items.GLASS_BOTTLE, FillDyedBottleCauldronInteraction.INSTANCE);

        // dye the wool
        addToList(DYE_CAULDRON_INTERACTIONS, new DyeItemCauldronInteraction(color -> switch (color) {
                    case WHITE      -> Items.WHITE_WOOL;
                    case ORANGE     -> Items.ORANGE_WOOL;
                    case MAGENTA    -> Items.MAGENTA_WOOL;
                    case LIGHT_BLUE -> Items.LIGHT_BLUE_WOOL;
                    case YELLOW     -> Items.YELLOW_WOOL;
                    case LIME       -> Items.LIME_WOOL;
                    case PINK       -> Items.PINK_WOOL;
                    case GRAY       -> Items.GRAY_WOOL;
                    case LIGHT_GRAY -> Items.LIGHT_GRAY_WOOL;
                    case CYAN       -> Items.CYAN_WOOL;
                    case PURPLE     -> Items.PURPLE_WOOL;
                    case BLUE       -> Items.BLUE_WOOL;
                    case BROWN      -> Items.BROWN_WOOL;
                    case GREEN      -> Items.GREEN_WOOL;
                    case RED        -> Items.RED_WOOL;
                    case BLACK      -> Items.BLACK_WOOL;
                  }), Items.WHITE_WOOL, Items.ORANGE_WOOL, Items.MAGENTA_WOOL, Items.LIGHT_BLUE_WOOL, Items.YELLOW_WOOL, Items.LIME_WOOL, Items.PINK_WOOL, Items.GRAY_WOOL,
                  Items.LIGHT_GRAY_WOOL, Items.CYAN_WOOL, Items.PURPLE_WOOL, Items.BLUE_WOOL, Items.BROWN_WOOL, Items.GREEN_WOOL, Items.RED_WOOL, Items.BLACK_WOOL);
        // dye the bed
        addToList(DYE_CAULDRON_INTERACTIONS, new DyeItemCauldronInteraction(color -> switch (color) {
                    case WHITE      -> Items.WHITE_BED;
                    case ORANGE     -> Items.ORANGE_BED;
                    case MAGENTA    -> Items.MAGENTA_BED;
                    case LIGHT_BLUE -> Items.LIGHT_BLUE_BED;
                    case YELLOW     -> Items.YELLOW_BED;
                    case LIME       -> Items.LIME_BED;
                    case PINK       -> Items.PINK_BED;
                    case GRAY       -> Items.GRAY_BED;
                    case LIGHT_GRAY -> Items.LIGHT_GRAY_BED;
                    case CYAN       -> Items.CYAN_BED;
                    case PURPLE     -> Items.PURPLE_BED;
                    case BLUE       -> Items.BLUE_BED;
                    case BROWN      -> Items.BROWN_BED;
                    case GREEN      -> Items.GREEN_BED;
                    case RED        -> Items.RED_BED;
                    case BLACK      -> Items.BLACK_BED;
                  }), Items.WHITE_BED, Items.ORANGE_BED, Items.MAGENTA_BED, Items.LIGHT_BLUE_BED, Items.YELLOW_BED, Items.LIME_BED, Items.PINK_BED, Items.GRAY_BED,
                  Items.LIGHT_GRAY_BED, Items.CYAN_BED, Items.PURPLE_BED, Items.BLUE_BED, Items.BROWN_BED, Items.GREEN_BED, Items.RED_BED, Items.BLACK_BED);
        // dye the carpet
        addToList(DYE_CAULDRON_INTERACTIONS, new DyeItemCauldronInteraction(color -> switch (color) {
                    case WHITE      -> Items.WHITE_CARPET;
                    case ORANGE     -> Items.ORANGE_CARPET;
                    case MAGENTA    -> Items.MAGENTA_CARPET;
                    case LIGHT_BLUE -> Items.LIGHT_BLUE_CARPET;
                    case YELLOW     -> Items.YELLOW_CARPET;
                    case LIME       -> Items.LIME_CARPET;
                    case PINK       -> Items.PINK_CARPET;
                    case GRAY       -> Items.GRAY_CARPET;
                    case LIGHT_GRAY -> Items.LIGHT_GRAY_CARPET;
                    case CYAN       -> Items.CYAN_CARPET;
                    case PURPLE     -> Items.PURPLE_CARPET;
                    case BLUE       -> Items.BLUE_CARPET;
                    case BROWN      -> Items.BROWN_CARPET;
                    case GREEN      -> Items.GREEN_CARPET;
                    case RED        -> Items.RED_CARPET;
                    case BLACK      -> Items.BLACK_CARPET;
                  }), Items.WHITE_CARPET, Items.ORANGE_CARPET, Items.MAGENTA_CARPET, Items.LIGHT_BLUE_CARPET, Items.YELLOW_CARPET, Items.LIME_CARPET, Items.PINK_CARPET, Items.GRAY_CARPET,
                  Items.LIGHT_GRAY_CARPET, Items.CYAN_CARPET, Items.PURPLE_CARPET, Items.BLUE_CARPET, Items.BROWN_CARPET, Items.GREEN_CARPET, Items.RED_CARPET, Items.BLACK_CARPET);
        // dye the carpeted trapdoor
        addToList(DYE_CAULDRON_INTERACTIONS, new DyeItemCauldronInteraction(dye -> InspirationsUtility.carpetedTrapdoors.get(dye)), InspirationsUtility.carpetedTrapdoors.values().stream().map(ItemLike::asItem).toArray(Item[]::new));
        // dye the shulker box
        addToList(DYE_CAULDRON_INTERACTIONS, new DyeItemCauldronInteraction(color -> switch (color) {
                    case WHITE      -> Items.WHITE_SHULKER_BOX;
                    case ORANGE     -> Items.ORANGE_SHULKER_BOX;
                    case MAGENTA    -> Items.MAGENTA_SHULKER_BOX;
                    case LIGHT_BLUE -> Items.LIGHT_BLUE_SHULKER_BOX;
                    case YELLOW     -> Items.YELLOW_SHULKER_BOX;
                    case LIME       -> Items.LIME_SHULKER_BOX;
                    case PINK       -> Items.PINK_SHULKER_BOX;
                    case GRAY       -> Items.GRAY_SHULKER_BOX;
                    case LIGHT_GRAY -> Items.LIGHT_GRAY_SHULKER_BOX;
                    case CYAN       -> Items.CYAN_SHULKER_BOX;
                    case PURPLE     -> Items.PURPLE_SHULKER_BOX;
                    case BLUE       -> Items.BLUE_SHULKER_BOX;
                    case BROWN      -> Items.BROWN_SHULKER_BOX;
                    case GREEN      -> Items.GREEN_SHULKER_BOX;
                    case RED        -> Items.RED_SHULKER_BOX;
                    case BLACK      -> Items.BLACK_SHULKER_BOX;
                  }, true), Items.WHITE_SHULKER_BOX, Items.ORANGE_SHULKER_BOX, Items.MAGENTA_SHULKER_BOX, Items.LIGHT_BLUE_SHULKER_BOX, Items.YELLOW_SHULKER_BOX, Items.LIME_SHULKER_BOX, Items.PINK_SHULKER_BOX, Items.GRAY_SHULKER_BOX,
                  Items.LIGHT_GRAY_SHULKER_BOX, Items.CYAN_SHULKER_BOX, Items.PURPLE_SHULKER_BOX, Items.BLUE_SHULKER_BOX, Items.BROWN_SHULKER_BOX, Items.GREEN_SHULKER_BOX, Items.RED_SHULKER_BOX, Items.BLACK_SHULKER_BOX);
        // dye the leather
        addToList(DYE_CAULDRON_INTERACTIONS, DyeLeatherItemCauldronInteraction.INSTANCE, ForgeRegistries.ITEMS.getValues().stream().filter(item -> item instanceof DyeableLeatherItem).toArray(Item[]::new));
      }

      // potion cauldrons
      if (Config.enableCauldronPotions.getAsBoolean()) {
        if (Config.brewPotionBottles.getAsBoolean()) {
          BrewingRecipeRegistry.addRecipe(new BottleBrewingRecipe(Ingredient.of(Items.GLASS_BOTTLE), Items.POTION, Items.SPLASH_POTION, new ItemStack(splashBottle)));
          BrewingRecipeRegistry.addRecipe(new BottleBrewingRecipe(Ingredient.of(MantleTags.Items.SPLASH_BOTTLE), Items.SPLASH_POTION, Items.LINGERING_POTION, new ItemStack(lingeringBottle)));
        }

        // fill the potion
        POTION_CAULDRON_INTERACTIONS.put(Items.GLASS_BOTTLE, new FillPotionCauldronInteraction(Items.POTION));
        // splash
        CauldronInteraction fillSplashPotion = new FillPotionCauldronInteraction(Items.SPLASH_POTION);
        POTION_CAULDRON_INTERACTIONS.put(splashBottle, fillSplashPotion);
        CauldronRegistry.register(exactBlock(potionCauldron), itemTag(MantleTags.Items.SPLASH_BOTTLE), fillSplashPotion);
        // lingering
        CauldronInteraction fillLingeringBottle = new FillPotionCauldronInteraction(Items.LINGERING_POTION);
        POTION_CAULDRON_INTERACTIONS.put(lingeringBottle, fillLingeringBottle);
        CauldronRegistry.register(exactBlock(potionCauldron), itemTag(MantleTags.Items.LINGERING_BOTTLE), fillLingeringBottle);

        // drain the potion
        POTION_CAULDRON_INTERACTIONS.put(Items.POTION, new PotionIntoPotionCauldron(Items.GLASS_BOTTLE));
        POTION_CAULDRON_INTERACTIONS.put(Items.SPLASH_POTION, new PotionIntoPotionCauldron(splashBottle));
        POTION_CAULDRON_INTERACTIONS.put(Items.LINGERING_POTION, new PotionIntoPotionCauldron(lingeringBottle));
        // drain the potion into empty, note this replaces the vanilla one, but it's okay as we still make water cauldrons
        CauldronInteraction.EMPTY.put(Items.POTION, new PotionIntoEmptyInteraction(Items.GLASS_BOTTLE));
        CauldronInteraction.EMPTY.put(Items.SPLASH_POTION, new PotionIntoEmptyInteraction(splashBottle));
        CauldronInteraction.EMPTY.put(Items.LINGERING_POTION, new PotionIntoEmptyInteraction(lingeringBottle));

        // water cauldrons work with potions, but not splash or lingering
        CauldronInteraction.WATER.put(Items.SPLASH_POTION, new WaterBottleIntoWaterInteraction(splashBottle));
        CauldronInteraction.WATER.put(Items.LINGERING_POTION, new WaterBottleIntoWaterInteraction(lingeringBottle));
        CauldronInteraction.WATER.put(splashBottle, new DecreaseLayerCauldronInteraction(PotionUtils.setPotion(new ItemStack(Items.SPLASH_POTION), Potions.WATER), LayeredCauldronBlock.LEVEL, true, SoundEvents.BOTTLE_FILL));
        CauldronInteraction.WATER.put(lingeringBottle, new DecreaseLayerCauldronInteraction(PotionUtils.setPotion(new ItemStack(Items.LINGERING_POTION), Potions.WATER), LayeredCauldronBlock.LEVEL, true, SoundEvents.BOTTLE_FILL));

        // make the tipped arrows, no water arrows tho
        if (Config.cauldronTipArrows.getAsBoolean()) {
          POTION_CAULDRON_INTERACTIONS.put(Items.ARROW, TipArrowCauldronInteraction.INSTANCE);
        }

        // potion brewing
        if (Config.cauldronBrewing.getAsBoolean()) {
          CauldronRegistry.register(exactBlock(Blocks.WATER_CAULDRON), CauldronRegistry.ALL_ITEMS, new BrewingCauldronInteraction(Potions.WATER));
          CauldronRegistry.register(exactBlock(potionCauldron), CauldronRegistry.ALL_ITEMS, new BrewingCauldronInteraction(null));
        }
      }

      Map<BlockState, PoiType> map = GameData.getBlockStatePointOfInterestTypeMap();
      register(map, PoiTypes.LEATHERWORKER,
               honeyCauldron, mushroomStewCauldron, potatoSoupCauldron, beetrootSoupCauldron, rabbitStewCauldron,
               dyeCauldron, potionCauldron, suspiciousStewCauldron,
               waterCauldron);
    });
  }


  /* Helpers */

  private static void register(Map<BlockState, PoiType> map, ResourceKey<PoiType> key, Block... blocks) {
    PoiType poi = ForgeRegistries.POI_TYPES.getValue(key.location());
    if (poi != null) {
      for (Block block : blocks) {
        if (block != null) {
          for (BlockState state : block.getStateDefinition().getPossibleStates()) {
            map.put(state, poi);
          }
        }
      }
    }
  }

  /** Creates a fluid builder */
  private static FluidType.Properties fluidBuilder(String name) {
    return FluidType.Properties.create()
                               .descriptionId(Util.makeDescriptionId("fluid", Inspirations.getResource(name)))
                               .sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL)
                               .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY)
                               .motionScale(0.0023333333333333335D)
                               .canExtinguish(true);
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
