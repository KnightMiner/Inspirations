package knightminer.inspirations.utility;


import knightminer.inspirations.common.CommonProxy;
import knightminer.inspirations.common.PulseBase;
import knightminer.inspirations.utility.block.*;
import knightminer.inspirations.utility.inventory.ContainerCollector;
import knightminer.inspirations.utility.inventory.ContainerPipe;
import knightminer.inspirations.utility.item.TorchLeverItem;
import knightminer.inspirations.utility.tileentity.TileCollector;
import knightminer.inspirations.utility.tileentity.TilePipe;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.dispenser.IDispenseItemBehavior;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.DyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityType;
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

	public static CommonProxy proxy = DistExecutor.runForDist(()->()->new UtilityClientProxy(), ()->()-> new CommonProxy());

	// blocks
	public static Block torchLeverWall;
	public static Block torchLeverFloor;
	public static Block redstoneBarrel;
	public static Block bricksButton;
	public static Block netherBricksButton;
	public static Block[] carpetedTrapdoors = new Block[16];
	public static Block[] carpetedPressurePlates = new Block[16];
	public static Block collector;
	public static Block pipe;
	public static Item pipeItem;

	// Tile entities
	public static TileEntityType<TileCollector> tileCollector;
	public static TileEntityType<TilePipe> tilePipe;

	// Inventory containers
	public static ContainerType<ContainerCollector> contCollector;
	public static ContainerType<ContainerPipe> contPipe;

	@SubscribeEvent
	public void preInit(FMLCommonSetupEvent event) {
		proxy.preInit();
	}

	@SubscribeEvent
	public void registerBlocks(Register<Block> event) {
		IForgeRegistry<Block> r = event.getRegistry();

		torchLeverFloor = registerBlock(r, new BlockTorchLever(), "torch_lever");
		torchLeverWall = registerBlock(r, new BlockWallTorchLever(), "wall_torch_lever");

		bricksButton = registerBlock(r, new BlockBricksButton(BlockBricksButton.BRICK_BUTTON), "bricks_button");
		netherBricksButton = registerBlock(r, new BlockBricksButton(BlockBricksButton.NETHER_BUTTON), "nether_bricks_button");

		redstoneBarrel = registerBlock(r, new BlockRedstoneBarrel(), "redstone_barrel");

		for(DyeColor color : DyeColor.values()) {
			carpetedTrapdoors[color.getId()] = registerBlock(r, new BlockCarpetedTrapdoor(color),  color.getName() + "_carpeted_trapdoor");
		}
		CarpetBlock[] carpets = new CarpetBlock[] {
			(CarpetBlock)Blocks.WHITE_CARPET,
			(CarpetBlock)Blocks.ORANGE_CARPET,
			(CarpetBlock)Blocks.MAGENTA_CARPET,
			(CarpetBlock)Blocks.LIGHT_BLUE_CARPET,
			(CarpetBlock)Blocks.YELLOW_CARPET,
			(CarpetBlock)Blocks.LIME_CARPET,
			(CarpetBlock)Blocks.PINK_CARPET,
			(CarpetBlock)Blocks.GRAY_CARPET,
			(CarpetBlock)Blocks.LIGHT_GRAY_CARPET,
			(CarpetBlock)Blocks.CYAN_CARPET,
			(CarpetBlock)Blocks.PURPLE_CARPET,
			(CarpetBlock)Blocks.BLUE_CARPET,
			(CarpetBlock)Blocks.BROWN_CARPET,
			(CarpetBlock)Blocks.GREEN_CARPET,
			(CarpetBlock)Blocks.RED_CARPET,
			(CarpetBlock)Blocks.BLACK_CARPET,
		};
		for(CarpetBlock carpet : carpets) {
			carpetedPressurePlates[carpet.getColor().getId()] = registerBlock(r,
					new BlockCarpetedPressurePlate(carpet),
					carpet.getColor().getName() + "_carpeted_pressure_plate"
			);
		}
		collector = registerBlock(r, new BlockCollector(), "collector");
		pipe = registerBlock(r, new BlockPipe(), "pipe");
	}

	@SubscribeEvent
	public void registerTEs(Register<TileEntityType<?>> event) {
		IForgeRegistry<TileEntityType<?>> r = event.getRegistry();

		tileCollector = register(r, TileEntityType.Builder.create(
				TileCollector::new, collector
		).build(null), "collector");

		tilePipe = register(r, TileEntityType.Builder.create(
				TilePipe::new, pipe
		).build(null), "pipe");
	}

	@SubscribeEvent
	public void registerContainers(Register<ContainerType<?>> event) {
		IForgeRegistry<ContainerType<?>> r = event.getRegistry();

		contCollector = register(r, new ContainerType<>(new ContainerCollector.Factory()), "collector");
		contPipe = register(r, new ContainerType<>(new ContainerPipe.Factory()), "pipe");
	}

	@SubscribeEvent
	public void registerItems(Register<Item> event) {
		IForgeRegistry<Item> r = event.getRegistry();

		// itemblocks
		register(r, new TorchLeverItem(), "torch_lever");
		registerItemBlock(r, bricksButton, ItemGroup.REDSTONE);
		registerItemBlock(r, netherBricksButton, ItemGroup.REDSTONE);
		registerItemBlock(r, redstoneBarrel, ItemGroup.REDSTONE);
		for(Block trapdoor : carpetedTrapdoors) {
			registerItemBlock(r, trapdoor, ItemGroup.REDSTONE);
		}
		for(Block pressurePlate : carpetedPressurePlates) {
			registerItemBlock(r, pressurePlate, ItemGroup.REDSTONE);
		}
		registerItemBlock(r, collector, ItemGroup.REDSTONE);
		pipeItem = registerItemBlock(r, pipe, ItemGroup.REDSTONE);
	}

	@SubscribeEvent
	public void init(FMLCommonSetupEvent event) {
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
