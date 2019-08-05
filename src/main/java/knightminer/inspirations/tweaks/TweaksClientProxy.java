package knightminer.inspirations.tweaks;

import com.google.common.collect.Maps;
import knightminer.inspirations.common.ClientProxy;
import knightminer.inspirations.common.Config;
import knightminer.inspirations.common.PulseBase;
import knightminer.inspirations.library.Util;
import knightminer.inspirations.library.client.ClientUtil;
import knightminer.inspirations.library.client.NameStateMapper;
import knightminer.inspirations.library.client.PropertyStateMapper;
import knightminer.inspirations.recipes.RecipesClientProxy;
import knightminer.inspirations.tweaks.block.BlockBetterFlowerPot;
import knightminer.inspirations.tweaks.block.BlockFittedCarpet;
import knightminer.inspirations.tweaks.client.PortalColorHandler;
import net.minecraft.block.BlockCarpet;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemEnchantedBook;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFlowerPot;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeColorHelper;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.LinkedHashMap;

public class TweaksClientProxy extends ClientProxy {

	private static final ResourceLocation CARPET_MODEL = Util.getResource("carpet");
	private static final ResourceLocation CAULDRON_ITEM_MODEL = Util.getResource("cauldron_item");
	private static final ResourceLocation ENCHANTED_BOOK = Util.getResource("enchanted_book");
	private static final ResourceLocation FIREWORKS = Util.getResource("fireworks");
	private static final ResourceLocation PORTAL = Util.getResource("portal");
	private static final ModelResourceLocation FLOWER_POT_MODEL = new ModelResourceLocation(Util.getResource("flower_pot"), "normal");

	@SubscribeEvent
	public void registerModels(ModelRegistryEvent event) {
		setModelStateMapper(InspirationsTweaks.carpet, new PropertyStateMapper(CARPET_MODEL,
				BlockCarpet.COLOR,
				BlockFittedCarpet.NORTHWEST, BlockFittedCarpet.NORTHEAST, BlockFittedCarpet.SOUTHWEST, BlockFittedCarpet.SOUTHEAST
				));
		setModelStateMapper(InspirationsTweaks.flowerPot, new FlowerPotStateMapper());
		if (Config.customPortalColor) {
			setModelStateMapper(Blocks.PORTAL, new NameStateMapper(PORTAL));
		}

		registerItemModel(InspirationsTweaks.cactusSeeds);
		registerItemModel(InspirationsTweaks.carrotSeeds);
		registerItemModel(InspirationsTweaks.potatoSeeds);
		registerItemModel(InspirationsTweaks.sugarCaneSeeds);

		if(Config.betterCauldronItem) {
			// if recipes is loaded, pull that model as there is a chance the two are different
			// the extended cauldron needed to replace the model to add tintindex's
			ResourceLocation model = CAULDRON_ITEM_MODEL;
			if(PulseBase.isRecipesLoaded() && Config.enableExtendedCauldron) {
				model = RecipesClientProxy.CAULDRON_MODEL;
			}
			registerItemModel(Items.CAULDRON, 0, model);
		}

		if(Config.coloredEnchantedRibbons) {
			registerItemModel(Items.ENCHANTED_BOOK, ENCHANTED_BOOK);
		}
		if(Config.coloredFireworkItems) {
			registerItemModel(Items.FIREWORKS, 0, FIREWORKS);
		}
	}

	@SubscribeEvent
	public void registerBlockColors(ColorHandlerEvent.Block event) {
		BlockColors colors = event.getBlockColors();

		// the vanilla flower pot handler is pretty dumb, it only uses the item and not the specific stack with meta
		if(Config.betterFlowerPot) {
			registerBlockColors(colors, (state, world, pos, index) -> {
				if (world != null && pos != null) {
					TileEntity tileentity = world.getTileEntity(pos);
					if (tileentity instanceof TileEntityFlowerPot) {
						ItemStack stack = ((TileEntityFlowerPot)tileentity).getFlowerItemStack();
						return ClientUtil.getStackBlockColorsSafe(stack, world, pos, 0);
					}
				}
				return -1;
			}, Blocks.FLOWER_POT);
		}

		// portal tinting
		if (Config.customPortalColor) {
			registerBlockColors(colors, PortalColorHandler.INSTANCE, Blocks.PORTAL);
		}

		// coloring on sugar cane crop to match reeds
		registerBlockColors(colors, (state, world, pos, index) -> {
			if(world == null || pos == null) {
				return -1;
			}
			return BiomeColorHelper.getGrassColorAtPos(world, pos);
		}, InspirationsTweaks.sugarCaneCrop);
	}

	@SubscribeEvent
	public void registerItemColors(ColorHandlerEvent.Item event) {
		ItemColors itemColors = event.getItemColors();

		// colored ribbons on enchanted books
		if(Config.coloredEnchantedRibbons) {
			registerItemColors(itemColors, (stack, tintIndex) -> {
				if(tintIndex == 0) {
					// find the rarest enchantment we have
					Enchantment.Rarity rarity = Enchantment.Rarity.COMMON;
					for(NBTBase tag : ItemEnchantedBook.getEnchantments(stack)) {
						if(tag.getId() == 10) {
							int id = ((NBTTagCompound) tag).getShort("id");
							Enchantment enchantment = Enchantment.getEnchantmentByID(id);
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
		}

		if(Config.coloredFireworkItems) {
			registerItemColors(itemColors, (stack, tintIndex) -> {
				NBTTagCompound nbt = stack.getSubCompound("Fireworks");
				// string is darker with more gunpowder
				if(tintIndex == 2) {
					if (nbt != null && nbt.hasKey("Flight", 99)) {
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

					NBTTagList stars = nbt.getTagList("Explosions", 10);
					// not enough stars?
					if(tintIndex >= stars.tagCount()) {
						return missing;
					}

					// grab the proper star's first color
					NBTTagCompound star = stars.getCompoundTagAt(tintIndex);
					int[] colors = star.getIntArray("Colors");
					if (colors.length > 0) {
						return colors[0];
					}

					return missing;
				}

				return -1;
			}, Items.FIREWORKS);
		}
	}

	/**
	 * Replaces the bookshelf models with the dynamic texture model, which also handles books
	 */
	@SubscribeEvent
	public void onModelBake(ModelBakeEvent event) {
		if(InspirationsTweaks.flowerPot != null) {
			replaceTexturedModel(event, FLOWER_POT_MODEL, "plant", false);
		}
	}

	@SubscribeEvent
	public void onWorldLoad(WorldEvent.Load event) {
		if(!Config.customPortalColor) {
			return;
		}
		World world = event.getWorld();
		int dimension = world.provider.getDimension();
		// only matters in the overworld and nether, portals invalid elsewhere
		if (dimension == DimensionType.OVERWORLD.getId() || dimension == DimensionType.NETHER.getId()) {
			world.addEventListener(PortalColorHandler.INSTANCE);
		}
	}

	@SubscribeEvent
	public void onWorldUnload(WorldEvent.Unload event) {
		if(!Config.customPortalColor) {
			return;
		}
		event.getWorld().removeEventListener(PortalColorHandler.INSTANCE);
	}

	private static class FlowerPotStateMapper extends StateMapperBase {
		@Override
		protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
			if(state.getValue(BlockBetterFlowerPot.EXTRA)) {
				return FLOWER_POT_MODEL;
			}

			LinkedHashMap<IProperty<?>, Comparable<?>> map = Maps.newLinkedHashMap(state.getProperties());
			map.remove(BlockBetterFlowerPot.EXTRA);
			map.remove(BlockBetterFlowerPot.LEGACY_DATA);

			return new ModelResourceLocation(state.getBlock().getRegistryName(), this.getPropertyString(map));
		}
	}
}
