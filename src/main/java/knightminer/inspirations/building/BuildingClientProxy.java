package knightminer.inspirations.building;

import knightminer.inspirations.Inspirations;
import knightminer.inspirations.building.block.BlockBookshelf;
import knightminer.inspirations.building.block.BlockBookshelf.BookshelfType;
import knightminer.inspirations.building.block.BlockEnlightenedBush;
import knightminer.inspirations.building.block.BlockEnlightenedBush.LightsType;
import knightminer.inspirations.building.block.BlockFlower.FlowerType;
import knightminer.inspirations.building.block.BlockRope;
import knightminer.inspirations.building.block.BlockRope.RopeType;
import knightminer.inspirations.building.client.BookshelfModel;
import knightminer.inspirations.building.tileentity.TileBookshelf;
import knightminer.inspirations.common.ClientProxy;
import knightminer.inspirations.common.Config;
import knightminer.inspirations.library.Util;
import knightminer.inspirations.library.client.BlockItemStateMapper;
import knightminer.inspirations.library.client.ClientUtil;
import knightminer.inspirations.library.util.TextureBlockUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
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
	public static final Minecraft mc = Minecraft.getMinecraft();

	@Override
	public void preInit() {
		super.preInit();

		if (Config.enableBookshelf) {
			// listener to clear bookshelf model cache as its shared by all bookshelf model files
			IResourceManager manager = Minecraft.getMinecraft().getResourceManager();
			// should always be true, but just in case
			if(manager instanceof IReloadableResourceManager) {
				((IReloadableResourceManager) manager).registerReloadListener((l) -> BookshelfModel.BOOK_CACHE.invalidateAll());
			} else {
				Inspirations.log.error("Failed to register resource reload listener, expected instance of IReloadableResourceManager but got {}", manager.getClass());
			}
		}
	}

	@SubscribeEvent
	public void registerModels(ModelRegistryEvent event) {
		setModelStateMapper(InspirationsBuilding.glassDoor, new StateMap.Builder().ignore(BlockDoor.POWERED).build());
		setModelStateMapper(InspirationsBuilding.flower, new BlockItemStateMapper());

		// items
		registerItemMetaDynamic(InspirationsBuilding.books);

		// blocks
		registerItemModel(InspirationsBuilding.glassDoorItem);
		registerItemModel(InspirationsBuilding.glassTrapdoor);
		registerRopeModels(InspirationsBuilding.rope);
		registerItemBlockMeta(InspirationsBuilding.mulch);
		registerItemBlockMeta(InspirationsBuilding.path);
		registerItemBlockMeta(InspirationsBuilding.enlightenedBush);
		registerFlowerModels(InspirationsBuilding.flower);
		registerBookshelfModels(InspirationsBuilding.bookshelf);
	}

	private void registerBookshelfModels(Block bookshelf) {
		if(bookshelf != null) {
			for(BookshelfType type : BookshelfType.values()) {
				registerItemModel(bookshelf, type.getMeta(), "facing=south,type=" + type.getName());
			}
		}
	}

	private void registerRopeModels(Block rope) {
		if(rope != null) {
			for(RopeType type : RopeType.values()) {
				registerItemModel(rope, type.getMeta(), "bottom=item,type=" + type.getName());
			}
		}
	}

	private void registerFlowerModels(Block flower) {
		if(flower != null) {
			for(FlowerType type : FlowerType.values()) {
				registerItemModel(flower, type.getMeta(), "block=false,type=" + type.getName());
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
							color = Util.combineColors(color, itemColors, 3);
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

		// bush block coloring
		registerBlockColors(blockColors, (state, world, pos, tintIndex) -> {
			if(tintIndex != 0) {
				return -1;
			}
			int color = state.getValue(BlockEnlightenedBush.LIGHTS).getColor();
			if(color > -1) {
				return color;
			}

			TileEntity te = world.getTileEntity(pos);
			if(te != null) {
				ItemStack stack = new ItemStack(TextureBlockUtil.getTextureBlock(te));
				return ClientUtil.getStackBlockColorsSafe(stack, world, pos, 0);
			}
			return ColorizerFoliage.getFoliageColorBasic();
		}, InspirationsBuilding.enlightenedBush);
	}

	@SubscribeEvent
	public void registerItemColors(ColorHandlerEvent.Item event) {
		ItemColors itemColors = event.getItemColors();

		// coloring of books for normal bookshelf
		registerItemColors(itemColors, (stack, tintIndex) -> {
			if(BookshelfType.fromMeta(stack.getMetadata()) == BookshelfType.NORMAL && tintIndex > 0 && tintIndex <= 14) {
				return 0x654B17;
			}

			return -1;
		}, InspirationsBuilding.bookshelf);

		// book covers, too lazy to make 16 cover textures
		registerItemColors(itemColors, (stack, tintIndex) -> {
			int meta = stack.getItemDamage();
			if(tintIndex == 0 && meta < 16) {
				return EnumDyeColor.byMetadata(meta).getColorValue();
			}
			return -1;
		}, InspirationsBuilding.books);

		// bush block colors
		registerItemColors(itemColors, (stack, tintIndex) -> {
			if(tintIndex != 0) {
				return -1;
			}

			// if the type has it's own color, use that
			int color = LightsType.fromMeta(stack.getMetadata()).getColor();
			if(color > -1) {
				return color;
			}

			ItemStack textureStack = TextureBlockUtil.getStackTexture(stack);
			if(!textureStack.isEmpty() && textureStack.getItem() != Item.getItemFromBlock(InspirationsBuilding.enlightenedBush)) {
				return itemColors.colorMultiplier(textureStack, 0);
			}
			return ColorizerFoliage.getFoliageColorBasic();
		}, InspirationsBuilding.enlightenedBush);

		// redirect to block colors
		registerItemColors(itemColors, (stack, tintIndex) -> ClientUtil.getStackBlockColors(stack, null, null, tintIndex),
				InspirationsBuilding.rope);
	}

	/**
	 * Replaces the bookshelf models with the dynamic texture model, which also handles books
	 */
	@SubscribeEvent
	public void onModelBake(ModelBakeEvent event) {
		if(InspirationsBuilding.bookshelf != null) {
			ResourceLocation bookshelfLoc = InspirationsBuilding.bookshelf.getRegistryName();
			for(BlockBookshelf.BookshelfType type : BlockBookshelf.BookshelfType.values()) {
				for(EnumFacing facing : EnumFacing.HORIZONTALS) {
					replaceBookshelfModel(event, new ModelResourceLocation(bookshelfLoc,
							String.format("facing=%s,type=%s", facing.getName(), type.getName())));
				}
			}
		}
		if(InspirationsBuilding.enlightenedBush != null) {
			ResourceLocation location = InspirationsBuilding.enlightenedBush.getRegistryName();
			for(BlockEnlightenedBush.LightsType type : BlockEnlightenedBush.LightsType.values()) {
				replaceTexturedModel(event, new ModelResourceLocation(location, String.format("lights=%s", type.getName())), "leaves", true);
			}
		}
	}

	private static void replaceBookshelfModel(ModelBakeEvent event, ModelResourceLocation location) {
		IModel model = ModelLoaderRegistry.getModelOrLogError(location, "Error loading model for " + location);
		IBakedModel standard = event.getModelRegistry().getObject(location);
		IBakedModel finalModel = new BookshelfModel(standard, model, DefaultVertexFormats.BLOCK);

		event.getModelRegistry().putObject(location, finalModel);
	}
}
