package knightminer.inspirations.utility;

import com.google.common.eventbus.Subscribe;

import knightminer.inspirations.common.CommonProxy;
import knightminer.inspirations.common.Config;
import knightminer.inspirations.common.PulseBase;
import knightminer.inspirations.library.InspirationsRegistry;
import knightminer.inspirations.utility.block.BlockBricksButton;
import knightminer.inspirations.utility.block.BlockCarpetedPressurePlate;
import knightminer.inspirations.utility.block.BlockCarpetedPressurePlate.BlockCarpetedPressurePlate2;
import knightminer.inspirations.utility.tileentity.TileCollector;
import knightminer.inspirations.utility.tileentity.TilePipe;
import knightminer.inspirations.utility.block.BlockCarpetedTrapdoor;
import knightminer.inspirations.utility.block.BlockCollector;
import knightminer.inspirations.utility.block.BlockPipe;
import knightminer.inspirations.utility.block.BlockRedstoneBarrel;
import knightminer.inspirations.utility.block.BlockRedstoneTorchLever;
import knightminer.inspirations.utility.block.BlockTorchLever;
import knightminer.inspirations.utility.dispenser.DispenseFluidTank;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDispenser;
import net.minecraft.block.Blocks;
import net.minecraft.block.CarpetBlock;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.material.Material;
import net.minecraft.dispenser.IDispenseItemBehavior;
import net.minecraft.init.Items;
import net.minecraft.item.DyeColor;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.IForgeRegistry;
import slimeknights.mantle.pulsar.pulse.Pulse;

@Pulse(id = InspirationsUtility.pulseID, description = "Adds various utilities")
public class InspirationsUtility extends PulseBase {
	public static final String pulseID = "InspirationsUtility";

	public static CommonProxy proxy = DistExecutor.runForDist(()->()->new UtilityClientProxy(), ()->()-> new CommonProxy());

	// blocks
	public static Block torchLever;
	public static Block redstoneBarrel;
	public static BlockBricksButton bricksButton;
	public static Block redstoneTorchLever;
	public static Block redstoneTorchLeverPowered;
	public static Block[] carpetedTrapdoors = new Block[16];
	public static Block[] carpetedPressurePlate = new Block[16];
	public static Block collector;
	public static Block pipe;
	public static Item pipeItem;

	@Subscribe
	public void preInit(FMLPreInitializationEvent event) {
		proxy.preInit();
	}

	@SubscribeEvent
	public void registerBlocks(Register<Block> event) {
		IForgeRegistry<Block> r = event.getRegistry();

		if(Config.enableTorchLever) {
			torchLever = registerBlock(r, new BlockTorchLever(), "torch_lever");
		}

		if(Config.enableBricksButton) {
			bricksButton = registerBlock(r, new BlockBricksButton(), "bricks_button");
		}
		if(Config.enableRedstoneBarrel) {
			redstoneBarrel = registerBlock(r, new BlockRedstoneBarrel(), "redstone_barrel");
		}
		if(Config.enableRedstoneTorchLever) {
			redstoneTorchLever = registerBlock(r, new BlockRedstoneTorchLever(false), "redstone_torch_lever");
			redstoneTorchLeverPowered = registerBlock(r, new BlockRedstoneTorchLever(true), "redstone_torch_lever_powered");
		}
		if(Config.enableCarpetedTrapdoor) {
			carpetedTrapdoors = new Block[16];
			for(EnumDyeColor color : EnumDyeColor.values()) {
				carpetedTrapdoors[color.getMetadata()] = registerBlock(r, new BlockCarpetedTrapdoor(), "carpeted_trapdoor_" + color.getName());
			}
		}
		if(Config.enableCarpetedPressurePlate.get()) {
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
				carpetedPressurePlate[carpet.getColor().getId()] = registerBlock(r,
						new BlockCarpetedPressurePlate(carpet),
						carpet.getColor().getName() + "_carpeted_pressure_plate"
				);
			}
		}

		if(Config.enableCollector.get()) {
			collector = registerBlock(r, new BlockCollector(), "collector");
			registerTE(TileCollector.class, "collector");
		}
		if(Config.enablePipe.get()) {
			pipe = registerBlock(r, new BlockPipe(), "pipe");
			registerTE(TilePipe.class, "pipe");
		}
	}

	@SubscribeEvent
	public void registerItems(Register<Item> event) {
		IForgeRegistry<Item> r = event.getRegistry();

		// itemblocks
		if(torchLever != null) {
			registerItemBlock(r, torchLever);
		}
		if(bricksButton != null) {
			registerEnumItemBlock(r, bricksButton);
		}
		if(redstoneBarrel != null) {
			registerItemBlock(r, redstoneBarrel);
		}
		if(redstoneTorchLever != null) {
			registerItemBlock(r, redstoneTorchLever);
		}
		if(carpetedTrapdoors != null) {
			for(Block trapdoor : carpetedTrapdoors) {
				registerItemBlock(r, trapdoor);
			}
		}
		if(collector != null) {
			registerItemBlock(r, collector);
		}
		if(pipe != null) {
			pipeItem = registerItemBlock(r, pipe);
		}
	}

	@Subscribe
	public void init(FMLInitializationEvent event) {
		proxy.init();
		registerDispenserBehavior();
	}

	@Subscribe
	public void postInit(FMLPostInitializationEvent event) {
		proxy.postInit();
		MinecraftForge.EVENT_BUS.register(UtilityEvents.class);
	}

	private void registerDispenserBehavior() {
		if(Config.enableDispenserFluidTanks) {
			for(String container : Config.fluidContainers) {
				Item item = GameRegistry.findRegistry(Item.class).getValue(new ResourceLocation(container));
				if(item != null && item != Items.AIR) {
					registerDispenseTankLogic(item);
				}
			}
		}
	}

	private static void registerDispenseTankLogic(Item item) {
		registerDispenserBehavior(item, new DispenseFluidTank(BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.getObject(item)));
	}
}
