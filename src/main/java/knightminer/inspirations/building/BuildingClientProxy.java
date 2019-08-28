package knightminer.inspirations.building;

import knightminer.inspirations.Inspirations;
import knightminer.inspirations.building.block.BlockBookshelf;
import knightminer.inspirations.building.block.BlockEnlightenedBush;
import knightminer.inspirations.building.client.BookshelfModel;
import knightminer.inspirations.building.client.GuiBookshelf;
import knightminer.inspirations.building.tileentity.TileBookshelf;
import knightminer.inspirations.common.ClientProxy;
import knightminer.inspirations.library.Util;
import knightminer.inspirations.library.client.ClientUtil;
import knightminer.inspirations.library.util.TextureBlockUtil;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.item.BlockItem;
import net.minecraft.item.DyeColor;
import net.minecraft.resources.IReloadableResourceManager;
import net.minecraft.resources.IResourceManager;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.FoliageColors;
import net.minecraft.world.biome.BiomeColors;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class BuildingClientProxy extends ClientProxy {
	public static final Minecraft mc = Minecraft.getInstance();

	@Override
	public void preInit() {
		super.preInit();

		// listener to clear bookshelf model cache as its shared by all bookshelf model files
		IResourceManager manager = Minecraft.getInstance().getResourceManager();
		// should always be true, but just in case
		if(manager instanceof IReloadableResourceManager) {
			((IReloadableResourceManager) manager).addReloadListener(
					(stage, resMan, prepProp, reloadProf, bgExec, gameExec) -> CompletableFuture
							.runAsync(BookshelfModel.BOOK_CACHE::invalidateAll, gameExec)
							.thenCompose(stage::markCompleteAwaitingOthers)
			);
		} else {
			Inspirations.log.error("Failed to register resource reload listener, expected instance of IReloadableResourceManager but got {}", manager.getClass());
		}

		// Register GUIs.
		ScreenManager.registerFactory(InspirationsBuilding.contBookshelf, GuiBookshelf::new);
	}


	@SubscribeEvent
	public void registerBlockColors(ColorHandlerEvent.Block event) {
		BlockColors blockColors = event.getBlockColors();

		// coloring of books for normal bookshelf
		blockColors.register((state, world, pos, tintIndex) -> {
			if(tintIndex > 0 && tintIndex <= 14 && world != null && pos != null) {
				TileEntity te = world.getTileEntity(pos);
				if(te instanceof TileBookshelf) {
					ItemStack stack = ((TileBookshelf) te).getStackInSlot(tintIndex - 1);
					if(!stack.isEmpty()) {
						int color = ClientUtil.getStackColor(stack);
						int itemColors = mc.getItemColors().getColor(stack, 0);
						if(itemColors > -1) {
							// combine twice to make sure the item colors result is cominant
							color = Util.combineColors(color, itemColors, 3);
						}
						return color;
					}
				}
			}

			return -1;
		}, InspirationsBuilding.shelf_normal);

		// rope vine coloring
		blockColors.register((state, world, pos, tintIndex) -> {
			if(world != null && pos != null) {
				return BiomeColors.getFoliageColor(world, pos);
			}
			return FoliageColors.getDefault();
		}, InspirationsBuilding.vine);

		// bush block coloring
//		blockColors.register((state, world, pos, tintIndex) -> {
//			if(tintIndex != 0 || world == null || pos == null) {
//				return -1;
//			}
//			int color = state.get(BlockEnlightenedBush.LIGHTS).getColor();
//			if(color > -1) {
//				return color;
//			}
//
//			TileEntity te = world.getTileEntity(pos);
//			if(te != null) {
//				ItemStack stack = ItemStack.read(TextureBlockUtil.getTextureBlock(te));
//				return ClientUtil.getStackBlockColorsSafe(stack, world, pos, 0);
//			}
//			return FoliageColors.getDefault();
//		}, InspirationsBuilding.enlightenedBush);
	}

	@SubscribeEvent
	public void registerItemColors(ColorHandlerEvent.Item event) {
		ItemColors itemColors = event.getItemColors();

		// coloring of books for normal bookshelf
		itemColors.register((stack, tintIndex) -> {
			if(tintIndex > 0 && tintIndex <= 14) {
				return 0x654B17;
			}
			return -1;
		}, InspirationsBuilding.shelf_normal);

		// book covers, too lazy to make 16 cover textures
		for (DyeColor color: DyeColor.values()) {
			int hexColor = color.colorValue;
			itemColors.register(
					(stack, tintIndex) -> (tintIndex == 1) ? hexColor : -1,
					InspirationsBuilding.coloredBooks[color.getId()]
			);
		}

		// bush block colors
		registerItemColors(itemColors, (stack, tintIndex) -> {
			if(tintIndex != 0) {
				return -1;
			}

			Block block = Block.getBlockFromItem(stack.getItem());
			if (!(block instanceof BlockEnlightenedBush)) {
				return -1;
			}
			
			// if the type has it's own color, use that
			int color = ((BlockEnlightenedBush) block).getColor();
			if(color > -1) {
				return color;
			}

			ItemStack textureStack = TextureBlockUtil.getStackTexture(stack);
			if(!textureStack.isEmpty() && textureStack.getItem() != Item.getItemFromBlock(InspirationsBuilding.enlightenedBush)) {
				return itemColors.getColor(textureStack, 0);
			}
			return FoliageColors.getDefault();
		}, InspirationsBuilding.enlightenedBush);

		// We can't get the world position of the item, so use the default tint.
		registerItemColors(itemColors, (stack, tintIndex) -> FoliageColors.getDefault(), InspirationsBuilding.vine);
	}

	/**
	 * Replaces the bookshelf models with the dynamic texture model, which also handles books
	 */
	@SubscribeEvent
	public void onModelBake(ModelBakeEvent event) {
		replaceBookshelfModel(event, InspirationsBuilding.shelf_normal);
		replaceBookshelfModel(event, InspirationsBuilding.shelf_ancient);
		replaceBookshelfModel(event, InspirationsBuilding.shelf_rainbow);
		replaceBookshelfModel(event, InspirationsBuilding.shelf_tomes);

		if(InspirationsBuilding.enlightenedBush != null) {
			ResourceLocation location = InspirationsBuilding.enlightenedBush.getRegistryName();
			replaceTexturedModel(event, new ModelResourceLocation(location, ""), "leaves", true);
		}
	}

	private static void replaceBookshelfModel(ModelBakeEvent event, BlockBookshelf shelf) {
		for(Direction facing : Direction.Plane.HORIZONTAL){
			ModelResourceLocation location = new ModelResourceLocation(shelf.getRegistryName(), String.format("facing=%s", facing.getName()));
			IModel model=ModelLoaderRegistry.getModelOrLogError(location,"Error loading model for " + location);
			IBakedModel standard = event.getModelRegistry().get(location);
			IBakedModel finalModel = new BookshelfModel(standard, model);

			event.getModelRegistry().put(location, finalModel);
		}
	}
}
