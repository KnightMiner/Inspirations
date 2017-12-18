package knightminer.inspirations.building;

import knightminer.inspirations.building.block.BlockBookshelf;
import knightminer.inspirations.building.block.BlockBookshelf.BookshelfType;
import knightminer.inspirations.building.client.BookshelfModel;
import knightminer.inspirations.building.tileentity.TileBookshelf;
import knightminer.inspirations.common.ClientProxy;
import knightminer.inspirations.library.Util;
import knightminer.inspirations.library.client.ClientUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class BuildingClientProxy extends ClientProxy {
	private static final Minecraft mc = Minecraft.getMinecraft();
	private static final ResourceLocation MODEL_BOOKSHELF = Util.getResource("bookshelf");

	@SubscribeEvent
	public void registerModels(ModelRegistryEvent event) {
		InspirationsBuilding.books.registerItemModels();
		registerItemModel(InspirationsBuilding.bookshelf);
	}

	@SubscribeEvent
	public void registerBlockColors(ColorHandlerEvent.Block event) {
		event.getBlockColors().registerBlockColorHandler((state, world, pos, tintIndex) -> {
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
	}

	@SubscribeEvent
	public void registerItemColors(ColorHandlerEvent.Item event) {
		event.getItemColors().registerItemColorHandler((stack, tintIndex) -> {
			int meta = stack.getItemDamage();
			if(tintIndex == 0 && meta < 16) {
				return EnumDyeColor.byMetadata(meta).getColorValue();
			}
			return -1;
		}, InspirationsBuilding.books);
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
}
