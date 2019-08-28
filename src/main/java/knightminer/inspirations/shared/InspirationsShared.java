package knightminer.inspirations.shared;

import knightminer.inspirations.common.CommonProxy;
import knightminer.inspirations.common.Config;
import knightminer.inspirations.common.PulseBase;
import knightminer.inspirations.common.item.HidableItem;
import net.minecraft.item.Food;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
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
	public static Object proxy = DistExecutor.callWhenOn(Dist.CLIENT, ()->()->new SharedClientProxy());

	// materials
	public static Item lock;
	public static Item key;
	public static Item splashBottle;
	public static Item lingeringBottle;
	public static Item mushrooms;
	public static Item rabbitStewMix;
	public static Item silverfishPowder;

	// edibles
	public static Item heartbeet;

	@SubscribeEvent
	public void registerItems(Register<Item> event) {
		IForgeRegistry<Item> r = event.getRegistry();

		// add items from modules
		if(isToolsLoaded()) {
			lock = registerItem(r, new HidableItem(
				new Item.Properties().group(ItemGroup.MATERIALS),
				Config.enableLock::get
			), "lock");
			key = registerItem(r, new HidableItem(
				new Item.Properties().group(ItemGroup.MATERIALS),
				Config.enableLock::get
			),  "key");
		}

		if(isTweaksLoaded()) {
			heartbeet = registerItem(r, new HidableItem(
					new Item.Properties().group(ItemGroup.FOOD).food(
					new Food.Builder().hunger(2).saturation(2.4f).effect(new EffectInstance(Effects.REGENERATION, 100), 1).build()),
					Config.enableHeartbeet::get
			), "heartbeet");
			silverfishPowder = registerItem(r, new HidableItem(
					new Item.Properties().group(ItemGroup.BREWING),
					() -> false
			),  "silverfish_powder");
		}

		if(isRecipesLoaded()) {
			splashBottle = registerItem(r, new HidableItem(
				new Item.Properties().group(ItemGroup.BREWING),
				Config::enableCauldronPotions
			), "splash_bottle");
			lingeringBottle = registerItem(r, new HidableItem(
				new Item.Properties().group(ItemGroup.BREWING),
				Config::enableCauldronPotions
			), "lingering_bottle");

			mushrooms = registerItem(r, new HidableItem(
				new Item.Properties().group(ItemGroup.MATERIALS),
				Config::enableCauldronFluids
			), "mushrooms");
			rabbitStewMix = registerItem(r, new HidableItem(
				new Item.Properties().group(ItemGroup.MATERIALS),
				Config::enableCauldronFluids
			), "rabbit_stew_mix");
		}
	}

	@SubscribeEvent
	public void setup(FMLCommonSetupEvent event) {
		MinecraftForge.EVENT_BUS.addListener(SharedEvents::updateMilkCooldown);
	}
}
