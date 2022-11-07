package knightminer.inspirations.tweaks;

import com.google.common.collect.ImmutableSet;
import knightminer.inspirations.common.Config;
import knightminer.inspirations.common.InspirationsCommons;
import knightminer.inspirations.common.ModuleBase;
import knightminer.inspirations.common.item.HidableItem;
import knightminer.inspirations.tweaks.block.BlockCropBlock;
import knightminer.inspirations.tweaks.block.CactusCropBlock;
import knightminer.inspirations.tweaks.block.DryHopperBlock;
import knightminer.inspirations.tweaks.block.FittedCarpetBlock;
import knightminer.inspirations.tweaks.block.FlatCarpetBlock;
import knightminer.inspirations.tweaks.block.SugarCaneCropBlock;
import knightminer.inspirations.tweaks.block.WetHopperBlock;
import knightminer.inspirations.tweaks.datagen.TweaksRecipeProvider;
import knightminer.inspirations.tweaks.item.SeedItem;
import knightminer.inspirations.tweaks.recipe.NormalBrewingRecipe;
import knightminer.inspirations.tweaks.util.SmoothGrowthListener;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.data.DataGenerator;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.context.DirectionalPlaceContext;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ComposterBlock;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.HopperBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.PlantType;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;
import net.minecraftforge.registries.IForgeRegistry;
import slimeknights.mantle.registration.adapter.BlockRegistryAdapter;
import slimeknights.mantle.registration.adapter.ItemRegistryAdapter;
import slimeknights.mantle.registration.object.EnumObject;

import java.util.Objects;

@SuppressWarnings({"WeakerAccess", "unused"})
public class InspirationsTweaks extends ModuleBase {
  public static final String pulseID = "InspirationsTweaks";

  // blocks
  public static BlockCropBlock cactus;
  public static BlockCropBlock sugarCane;
  public static HopperBlock wetHopper;
  public static HopperBlock dryHopper;
  // enum
  public static EnumObject<DyeColor,FittedCarpetBlock> fitCarpets = EnumObject.empty();
  public static EnumObject<DyeColor,FlatCarpetBlock> flatCarpets = EnumObject.empty();

  // items
  public static Item sugarCaneSeeds;
  public static Item cactusSeeds;
  //public static Item silverfishPowder;
  public static Item heartbeet;


  @SubscribeEvent
  void registerBlocks(Register<Block> event) {
    BlockRegistryAdapter registry = new BlockRegistryAdapter(event.getRegistry());
    IForgeRegistry<Block> r = event.getRegistry();

    boolean replaceVanilla = Config.enableFittedCarpets.getAsBoolean();
    EnumObject.Builder<DyeColor,FlatCarpetBlock> flatBuilder = new EnumObject.Builder<>(DyeColor.class);
    EnumObject.Builder<DyeColor,FittedCarpetBlock> fittedBuilder = new EnumObject.Builder<>(DyeColor.class);
    for (DyeColor color : DyeColor.values()) {
      Block original = InspirationsCommons.VANILLA_CARPETS.get(color);
      Block.Properties props = Block.Properties.copy(original);
      if (replaceVanilla) {
        flatBuilder.putDelegate(color, registry.register(new FlatCarpetBlock(color, props), original).delegate);
      } else {
        flatBuilder.putDelegate(color, original.delegate);
      }
      // bounding box messes with sprinting on stairs, so disable
      fittedBuilder.putDelegate(color, registry.register(new FittedCarpetBlock(color, props.noCollission()), color.getSerializedName() + "_fitted_carpet").delegate);
    }
    flatCarpets = flatBuilder.build();
    fitCarpets = fittedBuilder.build();

    if (Config.waterlogHopper.getAsBoolean()) {
      dryHopper = registry.registerOverride(DryHopperBlock::new, Blocks.HOPPER);
      wetHopper = registry.register(new WetHopperBlock(Block.Properties.copy(Blocks.HOPPER)), "wet_hopper");
    }

    cactus = registry.register(new CactusCropBlock(Blocks.CACTUS, PlantType.DESERT), "cactus");
    sugarCane = registry.register(new SugarCaneCropBlock(Blocks.SUGAR_CANE, PlantType.BEACH), "sugar_cane");
  }

  @SubscribeEvent
  void registerItem(Register<Item> event) {
    ItemRegistryAdapter registry = new ItemRegistryAdapter(event.getRegistry());
    Item.Properties decorationProps = new Item.Properties().tab(CreativeModeTab.TAB_DECORATIONS);
    IForgeRegistry<Item> r = event.getRegistry();

    if (Config.enableFittedCarpets.getAsBoolean()) {
      for (DyeColor color : DyeColor.values()) {
        Block carpet = InspirationsCommons.VANILLA_CARPETS.get(color);
        BlockItem item = registry.registerBlockItem(carpet, decorationProps);
        Item.BY_BLOCK.put(carpet, item);
        Item.BY_BLOCK.put(Objects.requireNonNull(flatCarpets.get(color)), item);
      }
    }

    if (Config.waterlogHopper.getAsBoolean()) {
      registry.register(new BlockItem(dryHopper, new Item.Properties().tab(CreativeModeTab.TAB_REDSTONE)), Items.HOPPER);
    }

    Item.Properties props = new Item.Properties().tab(CreativeModeTab.TAB_FOOD);
    cactusSeeds = registry.register(new SeedItem(cactus, props), "cactus_seeds");
    sugarCaneSeeds = registry.register(new SeedItem(sugarCane, props), "sugar_cane_seeds");
    heartbeet = registry.register(new HidableItem(new Item.Properties().tab(CreativeModeTab.TAB_FOOD)
                                                                       .food(new FoodProperties.Builder().nutrition(2).saturationMod(2.4f).effect(() -> new MobEffectInstance(MobEffects.REGENERATION, 100), 1).build()
                                                                            ), Config.enableHeartbeet), "heartbeet");

    //		silverfishPowder = registerItem(r, new HidableItem(
    //				new Item.Properties().group(ItemGroup.BREWING),
    //				() -> false // TODO: Make this have a purpose...
    //		),  "silverfish_powder");
  }

  @SubscribeEvent
  public void registerTileEntities(Register<BlockEntityType<?>> event) {
    if (Config.waterlogHopper.getAsBoolean()) {
      // We need to inject our replacement hopper blocks into the valid ones for the TE type.
      // It's an immutable set, so we need to replace it entirely.
      synchronized (BlockEntityType.HOPPER) {
        BlockEntityType.HOPPER.validBlocks = new ImmutableSet.Builder<Block>()
            .addAll(BlockEntityType.HOPPER.validBlocks)
            .add(dryHopper)
            .add(wetHopper)
            .build();
      }
    }
  }

  @SubscribeEvent
  public void setup(FMLCommonSetupEvent event) {
    // brew heartbeets into regen potions
    Ingredient heartbeet = Ingredient.of(InspirationsTweaks.heartbeet);
    BrewingRecipeRegistry.addRecipe(new NormalBrewingRecipe(Potions.WATER, heartbeet, Potions.MUNDANE, Config.brewHeartbeet));
    BrewingRecipeRegistry.addRecipe(new NormalBrewingRecipe(Potions.AWKWARD, heartbeet, Potions.REGENERATION, Config.brewHeartbeet));

    event.enqueueWork(this::registerCompostables);
    registerDispenserBehavior();

    MinecraftForge.EVENT_BUS.addListener(new SmoothGrowthListener(Blocks.CACTUS, cactus));
    MinecraftForge.EVENT_BUS.addListener(new SmoothGrowthListener(Blocks.SUGAR_CANE, sugarCane));
  }

  @SubscribeEvent
  public void gatherData(GatherDataEvent event) {
    DataGenerator gen = event.getGenerator();
    if (event.includeServer()) {
      gen.addProvider(new TweaksRecipeProvider(gen));
    }
  }


  @SubscribeEvent
  public static void loadLoot(LootTableLoadEvent event) {
    addToVanillaLoot(event, "entities/cave_spider");
    addToVanillaLoot(event, "entities/skeleton");
  }

  private static final DispenseItemBehavior DEFAULT = new DefaultDispenseItemBehavior();

  private void registerCompostables() {
    ComposterBlock.add(0.3F, cactusSeeds);
    ComposterBlock.add(0.3F, sugarCaneSeeds);
    ComposterBlock.add(0.8F, heartbeet);
  }

  private void registerDispenserBehavior() {
    DispenseItemBehavior behavior = (source, stack) -> {
      if (!Config.dispensersPlaceAnvils.getAsBoolean()) {
        DEFAULT.dispense(source, stack);
      }
      // get basic data
      Direction facing = source.getBlockState().getValue(DispenserBlock.FACING);
      Level world = source.getLevel();
      BlockPos pos = source.getPos().relative(facing);

      DirectionalPlaceContext context = new DirectionalPlaceContext(world, pos, facing, stack, facing.getOpposite());

      if (((BlockItem)stack.getItem()).place(context) == InteractionResult.SUCCESS) {
        return stack;
      } else {
        // if we cannot place it, toss the item
        return DEFAULT.dispense(source, stack);
      }
    };

    DispenserBlock.registerBehavior(Blocks.ANVIL, behavior);
    DispenserBlock.registerBehavior(Blocks.CHIPPED_ANVIL, behavior);
    DispenserBlock.registerBehavior(Blocks.DAMAGED_ANVIL, behavior);

  }
}
