package knightminer.inspirations.building;

import java.util.LinkedHashMap;

import javax.annotation.Nonnull;

import com.google.common.collect.Maps;

import knightminer.inspirations.building.block.BlockBookshelf;
import knightminer.inspirations.building.block.BlockBookshelf.BookshelfType;
import knightminer.inspirations.building.block.BlockRope;
import knightminer.inspirations.building.block.BlockTorchLever;
import knightminer.inspirations.building.block.BlockRope.RopeType;
import knightminer.inspirations.building.client.BookshelfModel;
import knightminer.inspirations.building.tileentity.TileBookshelf;
import knightminer.inspirations.common.ClientProxy;
import knightminer.inspirations.library.Util;
import knightminer.inspirations.library.client.ClientUtil;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.ColorizerFoliage;
import net.minecraft.world.biome.BiomeColorHelper;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class BuildingClientProxy extends ClientProxy {
	private static final Minecraft mc = Minecraft.getMinecraft();
	private static final ResourceLocation MODEL_BOOKSHELF = Util.getResource("bookshelf");

	@SubscribeEvent
	public void registerModels(ModelRegistryEvent event) {
		ModelLoader.setCustomStateMapper(InspirationsBuilding.torchLever, new TorchLeverStateMapper());

		// items
		InspirationsBuilding.books.registerItemModels();

		// blocks
		registerItemModel(InspirationsBuilding.torchLever);
		registerItemModel(InspirationsBuilding.bookshelf);
		registerRopeModels(InspirationsBuilding.rope);
	}

	private void registerRopeModels(Block rope) {
		for(RopeType type : RopeType.values()) {
			registerItemModel(rope, type.getMeta(), "bottom=item,type=" + type.getName());
		}
	}

	@SubscribeEvent
	public void registerBlockColors(ColorHandlerEvent.Block event) {
		BlockColors blockColors = event.getBlockColors();
		// coloring of books for normal bookshelf
		blockColors.registerBlockColorHandler((state, world, pos, tintIndex) -> {
			if(state.getValue(BlockBookshelf.TYPE) == BookshelfType.NORMAL && tintIndex > 0 && tintIndex <= 14) {
				TileEntity te = world.getTileEntity(pos);
				if(te instanceof TileBookshelf) {
					ItemStack stack = ((TileBookshelf) te).getStackInSlot(tintIndex - 1);
					if(!stack.isEmpty()) {
						int color = ClientUtil.getStackColor(stack);
						int itemColors = mc.getItemColors().colorMultiplier(stack, 0);
						if(itemColors > -1) {
							// combine twice to make sure the item colors result is cominant
							color = ClientUtil.combineColors(color, itemColors, 3);
						}
						return color;
					}
				}
			}

			return -1;
		}, InspirationsBuilding.bookshelf);

		// rope vine coloring
		blockColors.registerBlockColorHandler((state, world, pos, tintIndex) -> {
			if(state.getValue(BlockRope.TYPE) == RopeType.VINE) {
				if(world != null && pos != null) {
					return BiomeColorHelper.getFoliageColorAtPos(world, pos);
				}
				return ColorizerFoliage.getFoliageColorBasic();
			}

			return -1;
		}, InspirationsBuilding.rope);
	}

	@SubscribeEvent
	public void registerItemColors(ColorHandlerEvent.Item event) {
		BlockColors blockColors = event.getBlockColors();
		ItemColors itemColors = event.getItemColors();

		// book covers, to lazy to make 16 cover textures
		itemColors.registerItemColorHandler((stack, tintIndex) -> {
			int meta = stack.getItemDamage();
			if(tintIndex == 0 && meta < 16) {
				return EnumDyeColor.byMetadata(meta).getColorValue();
			}
			return -1;
		}, InspirationsBuilding.books);

		// vine coloring for rope vine
		itemColors.registerItemColorHandler((stack, tintIndex) -> {
			@SuppressWarnings("deprecation")
			IBlockState iblockstate = ((ItemBlock)stack.getItem()).getBlock().getStateFromMeta(stack.getMetadata());
			return blockColors.colorMultiplier(iblockstate, null, null, tintIndex);
		}, InspirationsBuilding.rope);
	}

	@SubscribeEvent
	public void onModelBake(ModelBakeEvent event) {
		for(BlockBookshelf.BookshelfType type : BlockBookshelf.BookshelfType.values()) {
			for(EnumFacing facing : EnumFacing.HORIZONTALS) {
				replaceBookshelfModel(event, new ModelResourceLocation(MODEL_BOOKSHELF,
						String.format("facing=%s,type=%s", facing.getName(), type.getName())));
			}
		}
		replaceBookshelfModel(event, new ModelResourceLocation(MODEL_BOOKSHELF, "inventory"));
	}

	private static void replaceBookshelfModel(ModelBakeEvent event, ModelResourceLocation location) {
		IModel model = ModelLoaderRegistry.getModelOrLogError(location, "Error loading model for " + location);
		IBakedModel standard = event.getModelRegistry().getObject(location);
		IBakedModel finalModel = new BookshelfModel(standard, model, DefaultVertexFormats.BLOCK);

		event.getModelRegistry().putObject(location, finalModel);
	}


	private static class TorchLeverStateMapper extends StateMapperBase {
		@Nonnull
		@Override
		protected ModelResourceLocation getModelResourceLocation(@Nonnull IBlockState state) {
			ResourceLocation base = state.getBlock().getRegistryName();
			LinkedHashMap<IProperty<?>, Comparable<?>> map = Maps.newLinkedHashMap(state.getProperties());
			String suffix = "";
			// if up, use the up file and ignore facing
			if(state.getValue(BlockTorchLever.FACING) == EnumFacing.UP) {
				map.remove(BlockTorchLever.FACING);
			} else {
				// otherwise ignore side
				map.remove(BlockTorchLever.SIDE);
				suffix = "_wall";
			}
			ResourceLocation res = new ResourceLocation(base.getResourceDomain(), base.getResourcePath() + suffix);
			return new ModelResourceLocation(res, this.getPropertyString(map));
		}
	}
}
