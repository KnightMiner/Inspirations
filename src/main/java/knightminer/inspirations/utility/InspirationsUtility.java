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
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.data.DataGenerator;
import net.minecraft.dispenser.IDispenseItemBehavior;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.DyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;
import net.minecraftforge.registries.IForgeRegistry;
import slimeknights.mantle.registration.adapter.BlockRegistryAdapter;
import slimeknights.mantle.registration.adapter.ContainerTypeRegistryAdapter;
import slimeknights.mantle.registration.adapter.ItemRegistryAdapter;
import slimeknights.mantle.registration.adapter.TileEntityTypeRegistryAdapter;
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
  public static TileEntityType<CollectorTileEntity> tileCollector;
  public static TileEntityType<PipeTileEntity> tilePipe;

  // Inventory containers
  public static ContainerType<CollectorContainer> contCollector;
  public static ContainerType<PipeContainer> contPipe;

  @SubscribeEvent
  public void registerBlocks(Register<Block> event) {
    BlockRegistryAdapter registry = new BlockRegistryAdapter(event.getRegistry());
    IForgeRegistry<Block> r = event.getRegistry();

    AbstractBlock.Properties torchLeverProps = Block.Properties
            .create(Material.MISCELLANEOUS)
            .doesNotBlockMovement()
            .zeroHardnessAndResistance()
            .setLightLevel(state -> 14)
            .tickRandomly()
            .sound(SoundType.WOOD);

    AbstractBlock.Properties soulLeverProps = Block.Properties
            .create(Material.MISCELLANEOUS)
            .doesNotBlockMovement()
            .zeroHardnessAndResistance()
            .setLightLevel(state -> 10)
            .sound(SoundType.WOOD);

    torchLeverFloor = registry.register(new TorchLeverBlock(torchLeverProps, ParticleTypes.FLAME), "torch_lever");
    torchLeverWall = registry.register(new TorchLeverWallBlock(torchLeverProps, ParticleTypes.FLAME), "wall_torch_lever");

    soulLeverFloor = registry.register(new TorchLeverBlock(soulLeverProps, ParticleTypes.SOUL_FIRE_FLAME), "soul_torch_lever");
    soulLeverWall = registry.register(new TorchLeverWallBlock(soulLeverProps, ParticleTypes.SOUL_FIRE_FLAME), "wall_soul_torch_lever");

    //bricksButton = registerBlock(r, new BricksButtonBlock(BricksButtonBlock.BRICK_BUTTON), "bricks_button");
    //netherBricksButton = registerBlock(r, new BricksButtonBlock(BricksButtonBlock.NETHER_BUTTON), "nether_bricks_button");

    carpetedTrapdoors = registry.registerEnum((color) -> new CarpetedTrapdoorBlock(), DyeColor.values(), "carpeted_trapdoor");
    carpetedPressurePlates = registry.registerEnum(CarpetedPressurePlateBlock::new, DyeColor.values(), "carpeted_pressure_plate");

    collector = registry.register(new CollectorBlock(), "collector");
    pipe = registry.register(new PipeBlock(), "pipe");
  }

  @SubscribeEvent
  public void registerTEs(Register<TileEntityType<?>> event) {
    TileEntityTypeRegistryAdapter registry = new TileEntityTypeRegistryAdapter(event.getRegistry());

    tileCollector = registry.register(CollectorTileEntity::new, collector, "collector");
    tilePipe = registry.register(PipeTileEntity::new, pipe, "pipe");
  }

  @SubscribeEvent
  public void registerContainers(Register<ContainerType<?>> event) {
    ContainerTypeRegistryAdapter registry = new ContainerTypeRegistryAdapter(event.getRegistry());
    IForgeRegistry<ContainerType<?>> r = event.getRegistry();

    contCollector = registry.registerType(CollectorContainer::new, "collector");
    contPipe = registry.registerType(PipeContainer::new, "pipe");
  }

  @SubscribeEvent
  public void registerItems(Register<Item> event) {
    Item.Properties props = new Item.Properties().group(ItemGroup.REDSTONE);
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
    DispenserRegAccess() { super(Block.Properties.create(Material.AIR));}

    IDispenseItemBehavior getRegisteredBehaviour(Item item) {
      return super.getBehavior(new ItemStack(item));
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
