package knightminer.inspirations.common;

import javax.annotation.Nonnull;

import knightminer.inspirations.library.Util;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.IStateMapper;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.Fluid;
import slimeknights.mantle.item.ItemBlockMeta;
import slimeknights.mantle.item.ItemMetaDynamic;

public class ClientProxy extends CommonProxy {
	public static final String VARIANT_INVENTORY = "inventory";

	@Override
	public void preInit() {
		super.preInit();

		MinecraftForge.EVENT_BUS.register(this);
	}

	/*
	 * Model registering
	 */

	/** Registers the item of the given block with its registry name for all metadata values for the inventory variant */
	public static ResourceLocation registerItemModel(Block block) {
		return registerItemModel(Item.getItemFromBlock(block));
	}

	/** Registers the given item with its registry name for all metadata values for the inventory variant */
	public static ResourceLocation registerItemModel(Item item) {
		ResourceLocation itemLocation = null;
		if(item != null) {
			itemLocation = item.getRegistryName();
		}
		if(itemLocation != null) {
			itemLocation = registerIt(item, itemLocation);
		}

		return itemLocation;
	}

	/** Registers the item with the given metadata and its registry name for the inventory variant */
	public static void registerItemModel(Item item, int meta) {
		registerItemModel(item, meta, VARIANT_INVENTORY);
	}

	/** Registers the given item with the given meta and its registry name for the given variant */
	public static void registerItemModel(Block block, int meta, String variant) {
		if(block != null) {
			registerItemModel(Item.getItemFromBlock(block), meta, block.getRegistryName(), variant);
		}
	}

	/** Registers the given item with the given meta and its registry name for the given variant */
	public static void registerItemModel(Item item, int meta, String variant) {
		if(item != null) {
			registerItemModel(item, meta, item.getRegistryName(), variant);
		}
	}

	/** Registers the given item/meta combination with the model at the given location, and the given variant */
	public static void registerItemModel(Item item, int meta, ResourceLocation location, String variant) {
		if(item != null && !StringUtils.isNullOrEmpty(variant)) {
			//ModelLoader.registerItemVariants(item, location);
			ModelLoader.setCustomModelResourceLocation(item, meta, new ModelResourceLocation(item.getRegistryName(), variant));
		}
	}

	/** Registers an itemblockmeta model for the blocks inventory variant. */
	public static void registerItemBlockMeta(Block block) {
		if(block != null) {
			Item item = Item.getItemFromBlock(block);
			((ItemBlockMeta) item).registerItemModels();
		}
	}

	/** Registers an itemblockmeta model for the blocks inventory variant. */
	public static void registerItemMetaDynamic(ItemMetaDynamic item) {
		if(item != null) {
			item.registerItemModels();
		}
	}

	public static void registerFluidModels(Fluid fluid) {
		if(fluid == null) {
			return;
		}

		Block block = fluid.getBlock();
		if(block != null) {
			Item item = Item.getItemFromBlock(block);
			FluidStateMapper mapper = new FluidStateMapper(fluid);

			// item-model
			if(item != Items.AIR) {
				ModelLoader.registerItemVariants(item);
				ModelLoader.setCustomMeshDefinition(item, mapper);
			}
			// block-model
			ModelLoader.setCustomStateMapper(block, mapper);
		}
	}

	protected static void setModelStateMapper(Block block, IStateMapper mapper) {
		if(block != null) {
			ModelLoader.setCustomStateMapper(block, mapper);
		}
	}

	/*
	 * Item and block color handlers
	 */

	protected static void registerBlockColors(BlockColors blockColors, IBlockColor handler, Block ... blocks) {
		for(Block block : blocks) {
			if(block != null) {
				blockColors.registerBlockColorHandler(handler, block);
			}
		}
	}

	protected static void registerItemColors(ItemColors itemColors, IItemColor handler, Block ... blocks) {
		for(Block block : blocks) {
			if(block != null) {
				Item item = Item.getItemFromBlock(block);
				if(item != Items.AIR) {
					itemColors.registerItemColorHandler(handler, item);
				}
			}
		}
	}
	protected static void registerItemColors(ItemColors itemColors, IItemColor handler, Item ... items) {
		for(Item item : items) {
			if(item != null) {
				itemColors.registerItemColorHandler(handler, item);
			}
		}
	}

	private static ResourceLocation registerIt(Item item, final ResourceLocation location) {
		// plop it in.
		// This here is needed for the model to be found ingame when the game looks for a model to render an Itemstack
		// we use an ItemMeshDefinition because it allows us to do it no matter what metadata we use
		ModelLoader.setCustomMeshDefinition(item, new ItemMeshDefinition() {
			@Nonnull
			@Override
			public ModelResourceLocation getModelLocation(@Nonnull ItemStack stack) {
				return new ModelResourceLocation(location, VARIANT_INVENTORY);
			}
		});

		// We have to readd the default variant if we have custom variants, since it wont be added otherwise and therefore not loaded
		ModelLoader.registerItemVariants(item, location);

		return location;
	}

	private static class FluidStateMapper extends StateMapperBase implements ItemMeshDefinition {

		public final ModelResourceLocation location;

		public FluidStateMapper(Fluid fluid) {
			// have each block hold its fluid per nbt? hm
			this.location = new ModelResourceLocation(Util.getResource("fluid_block"), fluid.getName());
		}

		@Nonnull
		@Override
		protected ModelResourceLocation getModelResourceLocation(@Nonnull IBlockState state) {
			return location;
		}

		@Nonnull
		@Override
		public ModelResourceLocation getModelLocation(@Nonnull ItemStack stack) {
			return location;
		}
	}
}
