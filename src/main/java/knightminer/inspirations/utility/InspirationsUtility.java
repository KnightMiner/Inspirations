package knightminer.inspirations.utility;


import knightminer.inspirations.common.PulseBase;
import knightminer.inspirations.utility.block.BricksButtonBlock;
import knightminer.inspirations.utility.block.CarpetedPressurePlateBlock;
import knightminer.inspirations.utility.block.CarpetedTrapdoorBlock;
import knightminer.inspirations.utility.block.CollectorBlock;
import knightminer.inspirations.utility.block.PipeBlock;
import knightminer.inspirations.utility.block.TorchLevelBlock;
import knightminer.inspirations.utility.block.TorchLeverWallBlock;
import knightminer.inspirations.utility.inventory.CollectorContainer;
import knightminer.inspirations.utility.inventory.PipeContainer;
import knightminer.inspirations.utility.item.TorchLeverItem;
import knightminer.inspirations.utility.tileentity.CollectorTileEntity;
import knightminer.inspirations.utility.tileentity.PipeTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.material.Material;
import net.minecraft.dispenser.IDispenseItemBehavior;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.DyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.IForgeRegistry;
import slimeknights.mantle.pulsar.pulse.Pulse;

@Pulse(id = InspirationsUtility.pulseID, description = "Adds various utilities")
public class InspirationsUtility extends PulseBase {
	public static final String pulseID = "InspirationsUtility";

	public static Object proxy = DistExecutor.callWhenOn(Dist.CLIENT, ()->()->new UtilityClientProxy());

	// blocks
	public static Block torchLeverWall;
	public static Block torchLeverFloor;
	public static Block bricksButton;
	public static Block netherBricksButton;
	public static Block[] carpetedTrapdoors = new Block[16];
	public static Block[] carpetedPressurePlates = new Block[16];
	public static Block collector;
	public static Block pipe;
	public static Item pipeItem;

	// Tile entities
	public static TileEntityType<CollectorTileEntity> tileCollector;
	public static TileEntityType<PipeTileEntity> tilePipe;

	// Inventory containers
	public static ContainerType<CollectorContainer> contCollector;
	public static ContainerType<PipeContainer> contPipe;

	@SubscribeEvent
	public void registerBlocks(Register<Block> event) {
		IForgeRegistry<Block> r = event.getRegistry();

		torchLeverFloor = registerBlock(r, new TorchLevelBlock(), "torch_lever");
		torchLeverWall = registerBlock(r, new TorchLeverWallBlock(), "wall_torch_lever");

		bricksButton = registerBlock(r, new BricksButtonBlock(BricksButtonBlock.BRICK_BUTTON), "bricks_button");
		netherBricksButton = registerBlock(r, new BricksButtonBlock(BricksButtonBlock.NETHER_BUTTON), "nether_bricks_button");

		for(DyeColor color : DyeColor.values()) {
			carpetedTrapdoors[color.getId()] = registerBlock(r, new CarpetedTrapdoorBlock(color), color.getName() + "_carpeted_trapdoor");
			carpetedPressurePlates[color.getId()] = registerBlock(r, new CarpetedPressurePlateBlock(color), color.getName() + "_carpeted_pressure_plate");
		}
		collector = registerBlock(r, new CollectorBlock(), "collector");
		pipe = registerBlock(r, new PipeBlock(), "pipe");
	}

	@SubscribeEvent
	public void registerTEs(Register<TileEntityType<?>> event) {
		IForgeRegistry<TileEntityType<?>> r = event.getRegistry();

		tileCollector = register(r, TileEntityType.Builder.create(
				CollectorTileEntity::new, collector
		).build(null), "collector");

		tilePipe = register(r, TileEntityType.Builder.create(
				PipeTileEntity::new, pipe
		).build(null), "pipe");
	}

	@SubscribeEvent
	public void registerContainers(Register<ContainerType<?>> event) {
		IForgeRegistry<ContainerType<?>> r = event.getRegistry();

		contCollector = register(r, new ContainerType<>(new CollectorContainer.Factory()), "collector");
		contPipe = register(r, new ContainerType<>(new PipeContainer.Factory()), "pipe");
	}

	@SubscribeEvent
	public void registerItems(Register<Item> event) {
		IForgeRegistry<Item> r = event.getRegistry();

		// itemblocks
		register(r, new TorchLeverItem(), "torch_lever");
		registerBlockItem(r, bricksButton, ItemGroup.REDSTONE);
		registerBlockItem(r, netherBricksButton, ItemGroup.REDSTONE);
		for(Block trapdoor : carpetedTrapdoors) {
			registerBlockItem(r, trapdoor, ItemGroup.REDSTONE);
		}
		registerBlockItem(r, collector, ItemGroup.REDSTONE);
		pipeItem = registerBlockItem(r, pipe, ItemGroup.REDSTONE);
	}

	@SubscribeEvent
	public void setup(FMLCommonSetupEvent event) {
		registerDispenserBehavior();
		MinecraftForge.EVENT_BUS.register(UtilityEvents.class);
	}

	// Get access to the existing behaviours.
	private static class DispenserRegAccess extends DispenserBlock {
		DispenserRegAccess() { super(Block.Properties.create(Material.AIR));}
		IDispenseItemBehavior getRegisteredBehaviour(Item item) {
			return super.getBehavior(new ItemStack(item));
		}
	}
	private DispenserRegAccess dispenserReg = new DispenserRegAccess();

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
