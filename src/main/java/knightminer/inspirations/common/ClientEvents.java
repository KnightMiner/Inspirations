package knightminer.inspirations.common;

import knightminer.inspirations.Inspirations;
import knightminer.inspirations.shared.client.TextureModel;
import net.minecraft.block.Block;
import net.minecraft.client.gui.IHasContainer;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.ItemModelsProperties;
import net.minecraft.item.Items;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelBakeEvent;

import javax.annotation.Nullable;
import java.util.function.BiFunction;

public abstract class ClientEvents {
	/*
	 * Null safe handlers
	 */

	/**
	 * Null safe method to register block colors. Works around an issue where registry events do not fire
	 * @param blockColors  Block colors instance
	 * @param handler      Color handler logic
	 * @param blocks       List of blocks to register
	 */
	protected static void registerBlockColors(BlockColors blockColors, IBlockColor handler, Block ... blocks) {
		for(Block block : blocks) {
			if(block != null) {
				blockColors.register(handler, block);
			}
		}
	}

	/**
	 * Null safe method to register item colors. Works around an issue where registry events do not fire
	 * @param itemColors  Item colors instance
	 * @param handler     Color handler logic
	 * @param items       List of items to register
	 */
	protected static void registerItemColors(ItemColors itemColors, IItemColor handler, IItemProvider... items) {
		for(IItemProvider item : items) {
			if(item != null && item.asItem() != Items.AIR) {
				itemColors.register(handler, item);
			}
		}
	}

	/**
	 * Null safe way to register a model property. Works around an issue where registry events do not fire
	 * @param item  Item with the property
	 * @param name  Property name, will be namespaced under Inspirations
	 * @param prop  Property getter instance
	 */
	protected static void registerModelProperty(@Nullable IItemProvider item, String name, IItemPropertyGetter prop) {
		if (item != null) {
			ItemModelsProperties.func_239418_a_(item.asItem(), Inspirations.getResource(name), prop);
		}
	}

	/**
	 * Null safe way to register a render layer. Works around an issue where registry events do not fire
	 * @param block        Block to register
	 * @param renderLayer  Render layer
	 */
	protected static void setRenderLayer(@Nullable Block block, RenderType renderLayer) {
		if (block != null) {
			RenderTypeLookup.setRenderLayer(block, renderLayer);
		}
	}

	/**
	 * Null safe way to reigster a screen factory
	 * @param type     Container type
	 * @param factory  Screen factory
	 * @param <M>      Container type
	 * @param <U>      Screen type
	 */
	protected static <M extends Container, U extends Screen & IHasContainer<M>> void registerScreenFactory(@Nullable ContainerType<? extends M> type, ScreenManager.IScreenFactory<M, U> factory) {
		if (type != null) {
			ScreenManager.registerFactory(type, factory);
		}
	}


	/* Deprecated model methods */

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
