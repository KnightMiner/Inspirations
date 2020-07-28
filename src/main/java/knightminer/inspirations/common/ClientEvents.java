package knightminer.inspirations.common;

import knightminer.inspirations.Inspirations;
import knightminer.inspirations.shared.client.TextureModel;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.ItemModelsProperties;
import net.minecraft.item.Items;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.util.function.BiFunction;

public class ClientEvents {

	public ClientEvents() {
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

	protected static void registerItemColors(ItemColors itemColors, IItemColor handler, IItemProvider... items) {
		for(IItemProvider item : items) {
			if(item != null && item.asItem() != Items.AIR) {
				itemColors.register(handler, item);
			}
		}
	}

	protected static void registerModelProperty(IItemProvider item, String name, IItemPropertyGetter prop) {
		if (item != null) {
			ItemModelsProperties.func_239418_a_(item.asItem(), Inspirations.getResource(name), prop);
		}
	}

	@Deprecated
	protected static void replaceModel(ModelBakeEvent event, ModelResourceLocation location, BiFunction<ModelBakery, IBakedModel, TextureModel> modelMaker) {
		try {
			// model to replace standard
			TextureModel finalModel = modelMaker.apply(event.getModelLoader(), event.getModelRegistry().get(location));
			event.getModelRegistry().put(location, finalModel);
		} catch(Exception e) {
			Inspirations.log.error("Caught exception trying to replace model for " + location, e);
		}
	}

	@Deprecated
	protected static void replaceTexturedModel(ModelBakeEvent event, ModelResourceLocation location, String key, boolean item) {
		replaceModel(
				event, location,
				(loader, model) -> new TextureModel(location, loader, model, key, item)
		);
	}

	@Deprecated
	protected static void replaceBothTexturedModels(ModelBakeEvent event, ResourceLocation loc, String key) {
		replaceTexturedModel(event, new ModelResourceLocation(loc, ""), key, false);
		replaceTexturedModel(event, new ModelResourceLocation(loc, "inventory"), key, true);
	}
}
