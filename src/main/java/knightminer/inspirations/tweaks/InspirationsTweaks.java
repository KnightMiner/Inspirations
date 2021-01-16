package knightminer.inspirations.tweaks;

import com.google.common.collect.ImmutableSet;
import knightminer.inspirations.common.Config;
import knightminer.inspirations.common.ModuleBase;
import knightminer.inspirations.common.item.HidableItem;
import knightminer.inspirations.shared.InspirationsShared;
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
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.ComposterBlock;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.HopperBlock;
import net.minecraft.data.DataGenerator;
import net.minecraft.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.dispenser.IDispenseItemBehavior;
import net.minecraft.item.BlockItem;
import net.minecraft.item.DirectionalPlaceContext;
import net.minecraft.item.DyeColor;
import net.minecraft.item.Food;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.potion.Potions;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.PlantType;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;
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

    if (Config.enableFittedCarpets.get()) {
      EnumObject.Builder<DyeColor,FlatCarpetBlock> flatBuilder = new EnumObject.Builder<>(DyeColor.class);
      EnumObject.Builder<DyeColor,FittedCarpetBlock> fittedBuilder = new EnumObject.Builder<>(DyeColor.class);
      for (DyeColor color : DyeColor.values()) {
        Block original = InspirationsShared.VANILLA_CARPETS.get(color);
        Block.Properties props = Block.Properties.from(original);
        flatBuilder.putDelegate(color, registry.register(new FlatCarpetBlock(color, props), original).delegate);
        // bounding box messes with sprinting on stairs, so disable
        fittedBuilder.putDelegate(color, registry.register(new FittedCarpetBlock(color, props.doesNotBlockMovement()), color.getString() + "_fitted_carpet").delegate);
      }
      flatCarpets = flatBuilder.build();
      fitCarpets = fittedBuilder.build();
    }

    if (Config.waterlogHopper.get()) {
      dryHopper = registry.registerOverride(DryHopperBlock::new, Blocks.HOPPER);
      wetHopper = registry.register(new WetHopperBlock(Block.Properties.from(Blocks.HOPPER)), "wet_hopper");
    }

    cactus = registry.register(new CactusCropBlock(Blocks.CACTUS, PlantType.DESERT), "cactus");
    sugarCane = registry.register(new SugarCaneCropBlock(Blocks.SUGAR_CANE, PlantType.BEACH), "sugar_cane");
  }

  @SubscribeEvent
  void registerItem(Register<Item> event) {
    ItemRegistryAdapter registry = new ItemRegistryAdapter(event.getRegistry());
    Item.Properties decorationProps = new Item.Properties().group(ItemGroup.DECORATIONS);
    IForgeRegistry<Item> r = event.getRegistry();

    if (Config.enableFittedCarpets.get()) {
      for (DyeColor color : DyeColor.values()) {
        Block carpet = InspirationsShared.VANILLA_CARPETS.get(color);
        BlockItem item = registry.registerBlockItem(carpet, decorationProps);
        Item.BLOCK_TO_ITEM.put(carpet, item);
        Item.BLOCK_TO_ITEM.put(Objects.requireNonNull(flatCarpets.get(color)), item);
      }
    }

    if (Config.waterlogHopper.get()) {
      registry.register(new BlockItem(dryHopper, new Item.Properties().group(ItemGroup.REDSTONE)), Items.HOPPER);
    }

    Item.Properties props = new Item.Properties().group(ItemGroup.FOOD);
    cactusSeeds = registry.register(new SeedItem(cactus, props), "cactus_seeds");
    sugarCaneSeeds = registry.register(new SeedItem(sugarCane, props), "sugar_cane_seeds");
    heartbeet = registry.register(new HidableItem(new Item.Properties().group(ItemGroup.FOOD)
                                                                       .food(new Food.Builder().hunger(2).saturation(2.4f).effect(() -> new EffectInstance(Effects.REGENERATION, 100), 1).build()
                                                                            ), Config.enableHeartbeet), "heartbeet");

    //		silverfishPowder = registerItem(r, new HidableItem(
    //				new Item.Properties().group(ItemGroup.BREWING),
    //				() -> false // TODO: Make this have a purpose...
    //		),  "silverfish_powder");
  }

  @SubscribeEvent
  public void registerTileEntities(Register<TileEntityType<?>> event) {
    if (Config.waterlogHopper.get()) {
      // We need to inject our replacement hopper blocks into the valid ones for the TE type.
      // It's an immutable set, so we need to replace it entirely.
      synchronized (TileEntityType.HOPPER) {
        TileEntityType.HOPPER.validBlocks = new ImmutableSet.Builder<Block>()
            .addAll(TileEntityType.HOPPER.validBlocks)
            .add(dryHopper)
            .add(wetHopper)
            .build();
      }
    }
  }

  @SubscribeEvent
  public void setup(FMLCommonSetupEvent event) {
    // brew heartbeets into regen potions
    Ingredient heartbeet = Ingredient.fromItems(InspirationsTweaks.heartbeet);
    BrewingRecipeRegistry.addRecipe(
        new NormalBrewingRecipe(Potions.WATER, heartbeet, Potions.MUNDANE, Config.brewHeartbeet)
                                   );
    BrewingRecipeRegistry.addRecipe(
        new NormalBrewingRecipe(Potions.AWKWARD, heartbeet, Potions.REGENERATION, Config.brewHeartbeet
        ));

    registerCompostables();
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

  private static final IDispenseItemBehavior DEFAULT = new DefaultDispenseItemBehavior();

  private void registerCompostables() {
    ComposterBlock.registerCompostable(0.3F, cactusSeeds);
    ComposterBlock.registerCompostable(0.3F, sugarCaneSeeds);
    ComposterBlock.registerCompostable(0.8F, heartbeet);
  }

  private void registerDispenserBehavior() {
    IDispenseItemBehavior behavior = (source, stack) -> {
      if (!Config.dispensersPlaceAnvils.get()) {
        DEFAULT.dispense(source, stack);
      }
      // get basic data
      Direction facing = source.getBlockState().get(DispenserBlock.FACING);
      World world = source.getWorld();
      BlockPos pos = source.getBlockPos().offset(facing);

      DirectionalPlaceContext context = new DirectionalPlaceContext(world, pos, facing, stack, facing.getOpposite());

      if (((BlockItem)stack.getItem()).tryPlace(context) == ActionResultType.SUCCESS) {
        return stack;
      } else {
        // if we cannot place it, toss the item
        return DEFAULT.dispense(source, stack);
      }
    };

    DispenserBlock.registerDispenseBehavior(Blocks.ANVIL, behavior);
    DispenserBlock.registerDispenseBehavior(Blocks.CHIPPED_ANVIL, behavior);
    DispenserBlock.registerDispenseBehavior(Blocks.DAMAGED_ANVIL, behavior);

  }
}
