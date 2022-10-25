package knightminer.inspirations.utility;


import knightminer.inspirations.common.ModuleBase;
import knightminer.inspirations.common.item.HidableBlockItem;
import knightminer.inspirations.utility.block.CarpetedPressurePlateBlock;
import knightminer.inspirations.utility.block.CarpetedTrapdoorBlock;
import knightminer.inspirations.utility.block.CollectorBlock;
import knightminer.inspirations.utility.block.PipeBlock;
import knightminer.inspirations.utility.block.TorchLeverBlock;
import knightminer.inspirations.utility.block.TorchLeverWallBlock;
import knightminer.inspirations.utility.datagen.UtilityRecipeProvider;
import knightminer.inspirations.utility.inventory.CollectorContainer;
import knightminer.inspirations.utility.inventory.PipeContainer;
import knightminer.inspirations.utility.item.TorchLeverItem;
import knightminer.inspirations.utility.tileentity.CollectorTileEntity;
import knightminer.inspirations.utility.tileentity.PipeTileEntity;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.data.DataGenerator;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;
import net.minecraftforge.registries.IForgeRegistry;
import slimeknights.mantle.registration.adapter.BlockEntityTypeRegistryAdapter;
import slimeknights.mantle.registration.adapter.BlockRegistryAdapter;
import slimeknights.mantle.registration.adapter.ContainerTypeRegistryAdapter;
import slimeknights.mantle.registration.adapter.ItemRegistryAdapter;
import slimeknights.mantle.registration.object.EnumObject;

@SuppressWarnings("unused")
public class InspirationsUtility extends ModuleBase {
  // blocks
  public static Block torchLeverWall;
  public static Block torchLeverFloor;
  public static Block soulLeverWall;
  public static Block soulLeverFloor;
  //public static Block bricksButton;
  //public static Block netherBricksButton;
  public static EnumObject<DyeColor,CarpetedTrapdoorBlock> carpetedTrapdoors = EnumObject.empty();
  public static EnumObject<DyeColor,CarpetedPressurePlateBlock> carpetedPressurePlates = EnumObject.empty();
  public static Block collector;
  public static Block pipe;

  // Items
  public static Item torchLeverItem;
  public static Item soulLeverItem;

  // Tile entities
  public static BlockEntityType<CollectorTileEntity> tileCollector;
  public static BlockEntityType<PipeTileEntity> tilePipe;

  // Inventory containers
  public static MenuType<CollectorContainer> contCollector;
  public static MenuType<PipeContainer> contPipe;

  @SubscribeEvent
  public void registerBlocks(Register<Block> event) {
    BlockRegistryAdapter registry = new BlockRegistryAdapter(event.getRegistry());
    IForgeRegistry<Block> r = event.getRegistry();

    torchLeverFloor = registry.register(new TorchLeverBlock(
            BlockBehaviour.Properties.copy(Blocks.TORCH).sound(SoundType.WOOD),
            ParticleTypes.FLAME
    ), "torch_lever");
    torchLeverWall = registry.register(new TorchLeverWallBlock(
            BlockBehaviour.Properties.copy(Blocks.WALL_TORCH).lootFrom(() -> torchLeverFloor),
            ParticleTypes.FLAME
    ), "wall_torch_lever");

    soulLeverFloor = registry.register(new TorchLeverBlock(
            BlockBehaviour.Properties.copy(Blocks.SOUL_TORCH),
            ParticleTypes.SOUL_FIRE_FLAME
    ), "soul_torch_lever");
    soulLeverWall = registry.register(new TorchLeverWallBlock(
            BlockBehaviour.Properties.copy(Blocks.SOUL_WALL_TORCH).lootFrom(() -> soulLeverFloor),
            ParticleTypes.SOUL_FIRE_FLAME
    ), "wall_soul_torch_lever");

    //bricksButton = registerBlock(r, new BricksButtonBlock(BricksButtonBlock.BRICK_BUTTON), "bricks_button");
    //netherBricksButton = registerBlock(r, new BricksButtonBlock(BricksButtonBlock.NETHER_BUTTON), "nether_bricks_button");

    carpetedTrapdoors = registry.registerEnum((color) -> new CarpetedTrapdoorBlock(), DyeColor.values(), "carpeted_trapdoor");
    carpetedPressurePlates = registry.registerEnum(CarpetedPressurePlateBlock::new, DyeColor.values(), "carpeted_pressure_plate");

    collector = registry.register(new CollectorBlock(), "collector");
    pipe = registry.register(new PipeBlock(), "pipe");
  }

  @SubscribeEvent
  public void registerTEs(Register<BlockEntityType<?>> event) {
    BlockEntityTypeRegistryAdapter registry = new BlockEntityTypeRegistryAdapter(event.getRegistry());

    tileCollector = registry.register(CollectorTileEntity::new, collector, "collector");
    tilePipe = registry.register(PipeTileEntity::new, pipe, "pipe");
  }

  @SubscribeEvent
  public void registerContainers(Register<MenuType<?>> event) {
    ContainerTypeRegistryAdapter registry = new ContainerTypeRegistryAdapter(event.getRegistry());
    IForgeRegistry<MenuType<?>> r = event.getRegistry();

    contCollector = registry.registerType(CollectorContainer::new, "collector");
    contPipe = registry.registerType(PipeContainer::new, "pipe");
  }

  @SubscribeEvent
  public void registerItems(Register<Item> event) {
    Item.Properties props = new Item.Properties().tab(CreativeModeTab.TAB_REDSTONE);
    ItemRegistryAdapter registry = new ItemRegistryAdapter(event.getRegistry(), props);

    // itemblocks
    torchLeverItem = registry.register(new TorchLeverItem(InspirationsUtility.torchLeverFloor, InspirationsUtility.torchLeverWall, props), "torch_lever");
    soulLeverItem = registry.register(new TorchLeverItem(InspirationsUtility.soulLeverFloor, InspirationsUtility.soulLeverWall, props), "soul_torch_lever");
    //registerBlockItem(r, bricksButton, ItemGroup.REDSTONE);
    //registerBlockItem(r, netherBricksButton, ItemGroup.REDSTONE);
    // TODO: never made a bifunction variant
    registry.registerBlockItem(carpetedTrapdoors, (block) -> new HidableBlockItem(block, props));
    registry.registerDefaultBlockItem(collector);
    registry.registerDefaultBlockItem(pipe);
  }

  @SubscribeEvent
  public void gatherData(GatherDataEvent event) {
    DataGenerator gen = event.getGenerator();
    if (event.includeServer()) {
      gen.addProvider(new UtilityRecipeProvider(gen));
    }
  }

  @SubscribeEvent
  public void setup(FMLCommonSetupEvent event) {
    registerDispenserBehavior();
  }

  // Get access to the existing behaviours.
  private static class DispenserRegAccess extends DispenserBlock {
    DispenserRegAccess() { super(Block.Properties.of(Material.AIR));}

    DispenseItemBehavior getRegisteredBehaviour(Item item) {
      return super.getDispenseMethod(new ItemStack(item));
    }
  }

  private final DispenserRegAccess dispenserReg = new DispenserRegAccess();

  private void registerDispenserBehavior() {
    //		if(Config.enableDispenserFluidTanks.get()) {
    //			for(Item item : InspirationsRegistry.TAG_DISP_FLUID_TANKS.getAllElements()) {
    //				if(item != null) {
    //					DispenserBlock.registerDispenseBehavior(item, new DispenseFluidTank(dispenserReg.getRegisteredBehaviour(item)));
    //				}
    //			}
    //		}
  }
}
