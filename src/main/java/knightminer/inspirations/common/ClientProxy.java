package knightminer.inspirations.common;

import knightminer.inspirations.shared.client.TextureModel;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.util.function.BiFunction;

public class ClientProxy {

	public ClientProxy() {
		MinecraftForge.EVENT_BUS.register(this);
		FMLJavaModLoadingContext.get().getModEventBus().register(this);
	}

	/*
	 * Item and block color handlers
	 */

	protected static void registerBlockColors(BlockColors blockColors, IBlockColor handler, Block ... blocks) {
		for(Block block : blocks) {
			if(block != null) {
				blockColors.register(handler, block);
			}
		}
	}

	protected static void registerItemColors(ItemColors itemColors, IItemColor handler, Block ... blocks) {
		for(Block block : blocks) {
			if(block != null) {
				Item item = Item.getItemFromBlock(block);
				if(item != Items.AIR) {
					itemColors.register(handler, item);
				}
			}
		}
	}
	protected static void registerItemColors(ItemColors itemColors, IItemColor handler, Item ... items) {
		for(Item item : items) {
			if(item != null) {
				itemColors.register(handler, item);
			}
		}
	}

	protected static void replaceModel(ModelBakeEvent event, ModelResourceLocation location, BiFunction<IBakedModel, IModel, IBakedModel> modelMaker) {
		IModel model = ModelLoaderRegistry.getModelOrLogError(location, "Error loading model for " + location);
		IBakedModel standard = event.getModelRegistry().get(location);
		IBakedModel finalModel = modelMaker.apply(standard, model);
		event.getModelRegistry().put(location, finalModel);
	}

	protected static void replaceTexturedModel(ModelBakeEvent event, ModelResourceLocation location, String key, boolean item) {
		replaceModel(
				event, location,
				(orig, model) -> new TextureModel(orig, model, item ? DefaultVertexFormats.ITEM : DefaultVertexFormats.BLOCK, key, item)
		);
	}

	protected static void replaceBothTexturedModels(ModelBakeEvent event, ResourceLocation loc, String key) {
		replaceTexturedModel(event, new ModelResourceLocation(loc, ""), key, false);
		replaceTexturedModel(event, new ModelResourceLocation(loc, "inventory"), "texture", true);
	}
}
