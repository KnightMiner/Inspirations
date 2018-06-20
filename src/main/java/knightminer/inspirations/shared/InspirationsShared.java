package knightminer.inspirations.shared;

import com.google.common.eventbus.Subscribe;

import knightminer.inspirations.common.CommonProxy;
import knightminer.inspirations.common.Config;
import knightminer.inspirations.common.PulseBase;
import knightminer.inspirations.shared.item.ItemMaterials;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.MobEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;
import slimeknights.mantle.item.ItemEdible;
import slimeknights.mantle.pulsar.pulse.Pulse;

@Pulse(id = InspirationsShared.pulseID, description = "Blocks and items used by all modules", forced = true)
public class InspirationsShared extends PulseBase {
	public static final String pulseID = "InspirationsShared";

	@SidedProxy(clientSide = "knightminer.inspirations.shared.SharedClientProxy", serverSide = "knightminer.inspirations.common.CommonProxy")
	public static CommonProxy proxy;

	// items
	public static ItemMaterials materials;
	public static ItemEdible edibles;

	// materials
	public static ItemStack lock;
	public static ItemStack key;
	public static ItemStack splashBottle;
	public static ItemStack lingeringBottle;
	public static ItemStack mushrooms;
	public static ItemStack rabbitStewMix;
	public static ItemStack silverfishPowder;
	public static ItemStack witherBone;

	// edibles
	public static ItemStack heartbeet;
	public static ItemStack boiledEgg;

	@Subscribe
	public void preInit(FMLPreInitializationEvent event) {
		proxy.preInit();
	}

	@SubscribeEvent
	public void registerItems(Register<Item> event) {
		IForgeRegistry<Item> r = event.getRegistry();

		materials = registerItem(r, new ItemMaterials(), "materials");
		materials.setCreativeTab(CreativeTabs.MATERIALS);

		edibles = registerItem(r, new ItemEdible(), "edibles");
		edibles.setCreativeTab(CreativeTabs.FOOD);

		// add items from modules
		if(isToolsLoaded() && Config.enableLock) {
			lock = materials.addMeta(0, "lock");
			key = materials.addMeta(1, "key");
		}

		if(isTweaksLoaded()) {
			if(Config.enableHeartbeet) {
				heartbeet = edibles.addFood(0, 2, 2.4f, "heartbeet", new PotionEffect(MobEffects.REGENERATION, 100));
			}
			if(Config.brewMissingPotions) {
				silverfishPowder = materials.addMeta(6, "silverfish_powder", CreativeTabs.BREWING);
				witherBone = materials.addMeta(7, "wither_bone", CreativeTabs.BREWING);
			}
		}
		if(isRecipesLoaded()) {
			if(Config.enableCauldronPotions) {
				splashBottle = materials.addMeta(2, "splash_bottle", CreativeTabs.BREWING);
				lingeringBottle = materials.addMeta(3, "lingering_bottle", CreativeTabs.BREWING);
			}
			if(Config.enableCauldronFluids) {
				mushrooms = materials.addMeta(4, "mushrooms");
				rabbitStewMix = materials.addMeta(5, "rabbit_stew_mix");
			}
		}
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
}
