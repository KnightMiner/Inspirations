package knightminer.inspirations.redstone;

import com.google.common.eventbus.Subscribe;

import knightminer.inspirations.common.CommonProxy;
import knightminer.inspirations.common.PulseBase;
import knightminer.inspirations.redstone.block.BlockTorchLever;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;
import slimeknights.mantle.pulsar.pulse.Pulse;

@Pulse(id = InspirationsRedstone.pulseID, description = "Adds features related to redstone")
public class InspirationsRedstone extends PulseBase {
	public static final String pulseID = "InspirationsRedstone";

	@SidedProxy(clientSide = "knightminer.inspirations.redstone.RedstoneClientProxy", serverSide = "knightminer.inspirations.common.CommonProxy")
	public static CommonProxy proxy;

	// blocks
	public static Block torchLever;

	@Subscribe
	public void preInit(FMLPreInitializationEvent event) {
		proxy.preInit();
	}

	@SubscribeEvent
	public void registerBlocks(Register<Block> event) {
		IForgeRegistry<Block> r = event.getRegistry();

		torchLever = registerBlock(r, new BlockTorchLever(), "torch_lever");
	}

	@SubscribeEvent
	public void registerItems(Register<Item> event) {
		IForgeRegistry<Item> r = event.getRegistry();

		registerItemBlock(r, torchLever);
	}

	@Subscribe
	public void init(FMLInitializationEvent event) {
		proxy.init();
	}

	@Subscribe
	public void postInit(FMLPostInitializationEvent event) {
		proxy.postInit();
	}
}
