package knightminer.inspirations.tweaks;

import knightminer.inspirations.common.ClientProxy;
import knightminer.inspirations.common.Config;
import knightminer.inspirations.library.Util;
import knightminer.inspirations.tweaks.client.PortalColorHandler;
import net.minecraft.block.Blocks;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.biome.BiomeColors;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Map;

public class TweaksClientProxy extends ClientProxy {
	private static final ResourceLocation ENCHANTED_BOOK_VANILLA = new ModelResourceLocation("enchanted_book", "inventory");
	private static final ResourceLocation ENCHANTED_BOOK_TINTED = Util.getResource("item/enchanted_book");

	private static final ResourceLocation FIREWORKS_VANILLA = new ModelResourceLocation("firework_rocket", "inventory");
	private static final ResourceLocation FIREWORKS_TINTED = Util.getResource("item/fireworks");

	private static final ResourceLocation PORTAL_EW_VANILLA = new ModelResourceLocation("minecraft:nether_portal", "axis=z");
	private static final ResourceLocation PORTAL_NS_VANILLA = new ModelResourceLocation("minecraft:nether_portal", "axis=x");
	private static final ResourceLocation PORTAL_EW_TINTED = Util.getResource("block/nether_portal_tinted_ew");
	private static final ResourceLocation PORTAL_NS_TINTED = Util.getResource("block/nether_portal_tinted_ns");

	private static final ResourceLocation CAULDRON_MODEL_VANILLA = new ModelResourceLocation("cauldron", "inventory");
	private static final ResourceLocation CAULDRON_ITEM_MODEL = new ModelResourceLocation(Util.getResource("cauldron"), "inventory");

	@SubscribeEvent
	public void clientSetup(FMLClientSetupEvent event) {
		RenderType cutout = RenderType.getCutout();
		RenderTypeLookup.setRenderLayer(InspirationsTweaks.cactus, cutout);
		RenderTypeLookup.setRenderLayer(InspirationsTweaks.sugarCane, cutout);
	}

	@SubscribeEvent
	public void loadCustomModels(ModelRegistryEvent event) {
		// Register these models to be loaded in directly.
		ModelLoader.addSpecialModel(PORTAL_EW_TINTED);
		ModelLoader.addSpecialModel(PORTAL_NS_TINTED);
		ModelLoader.addSpecialModel(ENCHANTED_BOOK_TINTED);
		ModelLoader.addSpecialModel(FIREWORKS_TINTED);
		ModelLoader.addSpecialModel(CAULDRON_ITEM_MODEL);
	}


	@SubscribeEvent
	public void swapModels(ModelBakeEvent event) {
		// Switch to the custom versions when loading models.
		Map<ResourceLocation, IBakedModel>map = event.getModelRegistry();

		if(Config.betterCauldronItem.get()) {
			map.put(CAULDRON_MODEL_VANILLA, map.get(CAULDRON_ITEM_MODEL));
		}

		if (Config.customPortalColor.get()) {
			map.put(PORTAL_EW_VANILLA, map.get(PORTAL_EW_TINTED));
			map.put(PORTAL_NS_VANILLA, map.get(PORTAL_NS_TINTED));
		}

		if (Config.coloredEnchantedRibbons.get()) {
			map.put(ENCHANTED_BOOK_VANILLA, map.get(ENCHANTED_BOOK_TINTED));
		}

		if (Config.coloredFireworkItems.get()) {
			map.put(FIREWORKS_VANILLA, map.get(FIREWORKS_TINTED));
		}
	}

	@SubscribeEvent
	public void registerBlockColors(ColorHandlerEvent.Block event) {
		BlockColors colors = event.getBlockColors();

		// coloring on sugar cane crop to match reeds
		registerBlockColors(colors, (state, world, pos, index) -> {
			if(world == null || pos == null) {
				return -1;
			}
			return BiomeColors.getGrassColor(world, pos);
		}, InspirationsTweaks.sugarCane);

		// portal tinting
		registerBlockColors(colors, PortalColorHandler.INSTANCE, Blocks.NETHER_PORTAL);
	}

	@SubscribeEvent
	public void registerItemColors(ColorHandlerEvent.Item event) {
		ItemColors itemColors = event.getItemColors();

		// colored ribbons on enchanted books
		registerItemColors(itemColors, (stack, tintIndex) -> {
			if(tintIndex == 0 && Config.coloredEnchantedRibbons.get()) {
				// find the rarest enchantment we have
				Enchantment.Rarity rarity = Enchantment.Rarity.COMMON;
				for(INBT tag : EnchantedBookItem.getEnchantments(stack)) {
					if(tag.getId() == Constants.NBT.TAG_COMPOUND) {
						ResourceLocation id = new ResourceLocation(((CompoundNBT) tag).getString("id"));
						Enchantment enchantment = ForgeRegistries.ENCHANTMENTS.getValue(id);
						if(enchantment != null) {
							Enchantment.Rarity newRarity = enchantment.getRarity();
							if(newRarity != null && newRarity.getWeight() < rarity.getWeight()) {
								rarity = newRarity;
							}
						}
					}
				}

				// color by that rarity
				switch(rarity) {
					case COMMON:    return 0xFF2151;
					case UNCOMMON:  return 0xE2882D;
					case RARE:      return 0x00FF21;
					case VERY_RARE: return 0x9F7FFF;
				}
			}
			return -1;
		}, Items.ENCHANTED_BOOK);

		registerItemColors(itemColors, (stack, tintIndex) -> {
			if (!Config.coloredFireworkItems.get()) {
				return -1;
			}
			CompoundNBT nbt = stack.getChildTag("Fireworks");
			// string is darker with more gunpowder
			if(tintIndex == 2) {
				if (nbt != null && nbt.contains("Flight", Constants.NBT.TAG_ANY_NUMERIC)) {
					byte flight = nbt.getByte("Flight");
					switch(flight) {
						case 1:
							return 0x808080;
						case 2:
							return 0x606060;
						case 3:
							return 0x303030;
					}
					if(flight > 3) {
						return 0x000000;
					}
				}
				return 0xA0A0A0;
			}
			// color the stripes and the top
			if(tintIndex == 0 || tintIndex == 1) {
				// no NBT?
				int missing = tintIndex == 1 ? 0xCCA190 : 0xC0C0C0;
				if(nbt == null) {
					return missing;
				}

				ListNBT stars = nbt.getList("Explosions", 10);
				// not enough stars?
				if(tintIndex >= stars.size()) {
					return missing;
				}

				// grab the proper star's first color
				CompoundNBT star = stars.getCompound(tintIndex);
				int[] colors = star.getIntArray("Colors");
				if (colors.length > 0) {
					return colors[0];
				}

				return missing;
			}

			return -1;
		}, Items.FIREWORK_ROCKET);
	}

	@SubscribeEvent
	public static void fixShieldTooltip(ItemTooltipEvent event) {
		if (!Config.fixShieldTooltip.get()) {
			return;
		}
		ItemStack stack = event.getItemStack();
		if (stack.getItem() != Items.SHIELD) {
			return;
		}
		CompoundNBT tags = stack.getChildTag("BlockEntityTag");
		if (tags != null && tags.contains("Patterns") && stack.isEnchanted()) {
			ListNBT patterns = tags.getList("Patterns", 10);
			event.getToolTip().add(patterns.size() + 1, new StringTextComponent(""));
		}
	}
}
