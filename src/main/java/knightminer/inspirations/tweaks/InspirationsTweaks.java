package knightminer.inspirations.tweaks;

import com.google.common.eventbus.Subscribe;

import knightminer.inspirations.common.CommonProxy;
import knightminer.inspirations.common.Config;
import knightminer.inspirations.common.PulseBase;
import knightminer.inspirations.library.InspirationsRegistry;
import knightminer.inspirations.shared.InspirationsShared;
import knightminer.inspirations.tweaks.block.BlockFittedCarpet;
import knightminer.inspirations.tweaks.block.BlockSmashingAnvil;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.PotionTypes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemCloth;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.potion.PotionHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;
import slimeknights.mantle.pulsar.pulse.Pulse;

@Pulse(id = InspirationsTweaks.pulseID, description = "Various vanilla tweaks")
public class InspirationsTweaks extends PulseBase {
	public static final String pulseID = "InspirationsTweaks";

	@SidedProxy(clientSide = "knightminer.inspirations.tweaks.TweaksClientProxy", serverSide = "knightminer.inspirations.common.CommonProxy")
	public static CommonProxy proxy;

	// blocks
	public static Block carpet;
	public static Block anvil;

	@Subscribe
	public void preInit(FMLPreInitializationEvent event) {
		proxy.preInit();
	}

	@SubscribeEvent
	public void registerBlocks(Register<Block> event) {
		IForgeRegistry<Block> r = event.getRegistry();

		if(Config.enableFittedCarpets) {
			carpet = register(r, new BlockFittedCarpet(), new ResourceLocation("carpet"));
		}
		if(Config.enableAnvilSmashing) {
			anvil = register(r, new BlockSmashingAnvil(), new ResourceLocation("anvil"));
		}
	}

	@SubscribeEvent
	public void registerItems(Register<Item> event) {
		IForgeRegistry<Item> r = event.getRegistry();

		if(carpet != null) {
			registerItemBlock(r, new ItemCloth(carpet));
		}
	}

	@Subscribe
	public void init(FMLInitializationEvent event) {
		proxy.init();

		// brew heartroots into regen potions
		if(Config.brewHeartbeet) {
			Ingredient heartbeet = Ingredient.fromStacks(InspirationsShared.heartbeet);
			PotionHelper.addMix(PotionTypes.WATER, heartbeet, PotionTypes.MUNDANE);
			PotionHelper.addMix(PotionTypes.AWKWARD, heartbeet, PotionTypes.REGENERATION);
		}
	}

	@Subscribe
	public void postInit(FMLPostInitializationEvent event) {
		proxy.postInit();
		MinecraftForge.EVENT_BUS.register(TweaksEvents.class);
		InspirationsRegistry.registerAnvilBreaking(Material.GLASS);
	}
}
