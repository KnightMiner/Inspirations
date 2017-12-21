package knightminer.inspirations.building;

import knightminer.inspirations.building.block.BlockBookshelf;
import knightminer.inspirations.building.block.BlockBookshelf.BookshelfType;
import knightminer.inspirations.building.block.BlockRope;
import knightminer.inspirations.building.block.BlockRope.RopeType;
import knightminer.inspirations.building.client.BookshelfModel;
import knightminer.inspirations.building.tileentity.TileBookshelf;
import knightminer.inspirations.common.ClientProxy;
import knightminer.inspirations.library.client.ClientUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMap;
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
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class BuildingClientProxy extends ClientProxy {
	private static final Minecraft mc = Minecraft.getMinecraft();

	@SubscribeEvent
	public void registerModels(ModelRegistryEvent event) {
		setModelStateMapper(InspirationsBuilding.glassDoor, new StateMap.Builder().ignore(BlockDoor.POWERED).build());
		
		// items
		registerItemMetaDynamic(InspirationsBuilding.books);

		// blocks
		registerItemModel(InspirationsBuilding.bookshelf);
		registerItemModel(InspirationsBuilding.glassDoorItem);
		registerItemModel(InspirationsBuilding.glassTrapdoor);
		registerRopeModels(InspirationsBuilding.rope);
	}

	private void registerRopeModels(Block rope) {
		if(rope != null) {
			for(RopeType type : RopeType.values()) {
				registerItemModel(rope, type.getMeta(), "bottom=item,type=" + type.getName());
			}
		}
	}

	@SubscribeEvent
	public void registerBlockColors(ColorHandlerEvent.Block event) {
		BlockColors blockColors = event.getBlockColors();

		// coloring of books for normal bookshelf
		registerBlockColors(blockColors, (state, world, pos, tintIndex) -> {
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
		registerBlockColors(blockColors, (state, world, pos, tintIndex) -> {
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

		// book covers, too lazy to make 16 cover textures
		registerItemColors(itemColors, (stack, tintIndex) -> {
			int meta = stack.getItemDamage();
			if(tintIndex == 0 && meta < 16) {
				return EnumDyeColor.byMetadata(meta).getColorValue();
			}
			return -1;
		}, InspirationsBuilding.books);

		// vine coloring for rope vine
		registerItemColors(itemColors, (stack, tintIndex) -> {
			@SuppressWarnings("deprecation")
			IBlockState iblockstate = ((ItemBlock)stack.getItem()).getBlock().getStateFromMeta(stack.getMetadata());
			return blockColors.colorMultiplier(iblockstate, null, null, tintIndex);
		}, InspirationsBuilding.rope);
	}

	/**
	 * Replaces the bookshelf models with the dynamic texture model, which also handles books
	 */
	@SubscribeEvent
	public void onModelBake(ModelBakeEvent event) {
		ResourceLocation bookshelfLoc = InspirationsBuilding.bookshelf.getRegistryName();
		for(BlockBookshelf.BookshelfType type : BlockBookshelf.BookshelfType.values()) {
			for(EnumFacing facing : EnumFacing.HORIZONTALS) {
				replaceBookshelfModel(event, new ModelResourceLocation(bookshelfLoc,
						String.format("facing=%s,type=%s", facing.getName(), type.getName())));
			}
		}
		replaceBookshelfModel(event, new ModelResourceLocation(bookshelfLoc, "inventory"));
	}

	private static void replaceBookshelfModel(ModelBakeEvent event, ModelResourceLocation location) {
		IModel model = ModelLoaderRegistry.getModelOrLogError(location, "Error loading model for " + location);
		IBakedModel standard = event.getModelRegistry().getObject(location);
		IBakedModel finalModel = new BookshelfModel(standard, model, DefaultVertexFormats.BLOCK);

		event.getModelRegistry().putObject(location, finalModel);
	}
}
