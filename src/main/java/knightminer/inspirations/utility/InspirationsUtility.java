package knightminer.inspirations.utility;


import knightminer.inspirations.common.ModuleBase;
import knightminer.inspirations.utility.block.CarpetedPressurePlateBlock;
import knightminer.inspirations.utility.block.CarpetedTrapdoorBlock;
import knightminer.inspirations.utility.block.CollectorBlock;
import knightminer.inspirations.utility.block.PipeBlock;
import knightminer.inspirations.utility.block.TorchLevelBlock;
import knightminer.inspirations.utility.block.TorchLeverWallBlock;
import knightminer.inspirations.utility.datagen.UtilityRecipeProvider;
import knightminer.inspirations.utility.inventory.CollectorContainer;
import knightminer.inspirations.utility.inventory.PipeContainer;
import knightminer.inspirations.utility.item.TorchLeverItem;
import knightminer.inspirations.utility.tileentity.CollectorTileEntity;
import knightminer.inspirations.utility.tileentity.PipeTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.material.Material;
import net.minecraft.data.DataGenerator;
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
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;
import net.minecraftforge.fml.network.IContainerFactory;
import net.minecraftforge.registries.IForgeRegistry;
import slimeknights.mantle.registration.adapter.BlockRegistryAdapter;
import slimeknights.mantle.registration.adapter.ContainerTypeRegistryAdapter;
import slimeknights.mantle.registration.adapter.ItemRegistryAdapter;
import slimeknights.mantle.registration.adapter.TileEntityTypeRegistryAdapter;

@SuppressWarnings("unused")
public class InspirationsUtility extends ModuleBase {

	@SuppressWarnings("Convert2MethodRef")
	public static Object proxy = DistExecutor.callWhenOn(Dist.CLIENT, ()->()->new UtilityClientProxy());

	// blocks
	public static Block torchLeverWall;
	public static Block torchLeverFloor;
	//public static Block bricksButton;
	//public static Block netherBricksButton;
	public static Block[] carpetedTrapdoors = new Block[16];
	public static CarpetedPressurePlateBlock[] carpetedPressurePlates = new CarpetedPressurePlateBlock[16];
	public static Block collector;
	public static Block pipe;

	// Items
	public static Item torchLeverItem;
	public static Item pipeItem;

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

		torchLeverFloor = registry.register(new TorchLevelBlock(), "torch_lever");
		torchLeverWall = registry.register(new TorchLeverWallBlock(), "wall_torch_lever");

		//bricksButton = registerBlock(r, new BricksButtonBlock(BricksButtonBlock.BRICK_BUTTON), "bricks_button");
		//netherBricksButton = registerBlock(r, new BricksButtonBlock(BricksButtonBlock.NETHER_BUTTON), "nether_bricks_button");

		for(DyeColor color : DyeColor.values()) {
			carpetedTrapdoors[color.getId()] = registry.register(new CarpetedTrapdoorBlock(color), color.getString() + "_carpeted_trapdoor");
			carpetedPressurePlates[color.getId()] = registry.register(new CarpetedPressurePlateBlock(color), color.getString() + "_carpeted_pressure_plate");
		}
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

		contCollector = registry.register((IContainerFactory<CollectorContainer>)CollectorContainer::new, "collector");
		contPipe = registry.register((IContainerFactory<PipeContainer>)PipeContainer::new, "pipe");
	}

	@SubscribeEvent
	public void registerItems(Register<Item> event) {
		ItemRegistryAdapter registry = new ItemRegistryAdapter(event.getRegistry(), new Item.Properties().group(ItemGroup.REDSTONE));

		// itemblocks
		torchLeverItem = registry.register(new TorchLeverItem(), "torch_lever");
		//registerBlockItem(r, bricksButton, ItemGroup.REDSTONE);
		//registerBlockItem(r, netherBricksButton, ItemGroup.REDSTONE);
		for(Block trapdoor : carpetedTrapdoors) {
			registry.registerBlockItem(trapdoor);
		}
		registry.registerBlockItem(collector);
		pipeItem = registry.registerBlockItem(pipe);
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
