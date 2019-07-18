package knightminer.inspirations.shared;

import knightminer.inspirations.common.CommonProxy;
import knightminer.inspirations.common.Config;
import knightminer.inspirations.common.PulseBase;
import net.minecraft.item.Food;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.registries.IForgeRegistry;
import slimeknights.mantle.pulsar.pulse.Pulse;


@Pulse(id = InspirationsShared.pulseID, description = "Blocks and items used by all modules", forced = true)
public class InspirationsShared extends PulseBase {
	public static final String pulseID = "InspirationsShared";

	@SuppressWarnings("Convert2MethodRef")
	public static CommonProxy proxy = DistExecutor.runForDist(() -> () -> new SharedClientProxy(), () -> () -> new CommonProxy());

	// materials
	public static Item lock;
	public static Item key;
	public static Item splashBottle;
	public static Item lingeringBottle;
	public static Item mushrooms;
	public static Item rabbitStewMix;
	public static Item silverfishPowder;
	public static Item witherBone;
	public static Item stoneRod;

	// edibles
	public static Item heartbeet;
	public static Item boiledEgg;

	// flags
	private static boolean witherBoneDrop = false;
	public static boolean milkCooldownCow = false;
	public static boolean milkCooldownSquid = false;

	@SubscribeEvent
	public void preInit(FMLCommonSetupEvent event) {
		proxy.preInit();
	}

	@SubscribeEvent
	public void registerItems(Register<Item> event) {
		IForgeRegistry<Item> r = event.getRegistry();

		// add items from modules
		if(isToolsLoaded() && Config.enableLock) {
			lock = registerItem(r, new Item(new Item.Properties().group(ItemGroup.MATERIALS)), "lock");
			key = registerItem(r, new Item(new Item.Properties().group(ItemGroup.MATERIALS)),  "key");
		}

		if(isTweaksLoaded()) {
			if(Config.enableHeartbeet) {
				heartbeet = registerItem(r, new Item(new Item.Properties().group(ItemGroup.FOOD).food(
						new Food.Builder().hunger(2).saturation(2.4f).effect(new EffectInstance(Effects.REGENERATION, 100), 1).build()
				)), "heartbeet");
			}
			if(Config.brewMissingPotions) {
				silverfishPowder = registerItem(r, new Item(new Item.Properties().group(ItemGroup.BREWING)),  "silverfish_powder");
			}
		}
		// used in both extended brewing and nether crooks
		if((isTweaksLoaded() && Config.brewMissingPotions) || (isToolsLoaded() && Config.netherCrooks)) {
			witherBone = registerItem(r, new Item(new Item.Properties().group(ItemGroup.BREWING)), "wither_bone");
			witherBoneDrop = Config.witherBoneDrop;
			milkCooldownCow = Config.milkCooldown;
		}

		if(isRecipesLoaded()) {
			if(Config.enableCauldronPotions) {
				splashBottle =  registerItem(r, new Item(new Item.Properties().group(ItemGroup.BREWING)), "splash_bottle");
				lingeringBottle =  registerItem(r, new Item(new Item.Properties().group(ItemGroup.BREWING)), "lingering_bottle");
			}
			if(Config.enableCauldronFluids) {
				mushrooms =  registerItem(r, new Item(new Item.Properties().group(ItemGroup.MATERIALS)), "mushrooms");
				rabbitStewMix =  registerItem(r, new Item(new Item.Properties().group(ItemGroup.MATERIALS)), "rabbit_stew_mix");
			}
			milkCooldownSquid = Config.milkSquids;
		}
		if(isToolsLoaded() && Config.separateCrook) {
			stoneRod = registerItem(r, new Item(new Item.Properties().group(ItemGroup.MATERIALS)), "stone_rod");
		}
	}

	@SubscribeEvent
	public void init(InterModEnqueueEvent event) {
		proxy.init();
	}

	@SubscribeEvent
	public void postInit(InterModProcessEvent event) {
		proxy.postInit();

		// currently just used for milking
		if(milkCooldownCow || milkCooldownSquid) {
			MinecraftForge.EVENT_BUS.register(SharedEvents.class);
		}
	}

	private static final ResourceLocation WITHER_SKELETON_TABLE = new ResourceLocation("entities/wither_skeleton");
	@SubscribeEvent
	public void onLootTableLoad(LootTableLoadEvent event) {
		if(witherBoneDrop && WITHER_SKELETON_TABLE.equals(event.getName())) {
			addToVanillaLoot(event, "entities/wither_skeleton");
		}
	}
}
