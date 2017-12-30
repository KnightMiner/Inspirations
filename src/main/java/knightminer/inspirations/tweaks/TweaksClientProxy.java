package knightminer.inspirations.tweaks;

import knightminer.inspirations.common.ClientProxy;
import knightminer.inspirations.common.Config;
import knightminer.inspirations.library.Util;
import knightminer.inspirations.library.client.NameStateMapper;
import knightminer.inspirations.library.client.PropertyStateMapper;
import knightminer.inspirations.library.recipe.cauldron.ICauldronRecipe;
import knightminer.inspirations.tweaks.block.BlockEnhancedCauldron;
import knightminer.inspirations.tweaks.block.BlockFittedCarpet;
import knightminer.inspirations.tweaks.client.CauldronModel;
import knightminer.inspirations.tweaks.tileentity.TileCauldron;
import net.minecraft.block.BlockCarpet;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.init.Items;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class TweaksClientProxy extends ClientProxy {
	private static final ResourceLocation POTION_MODEL = new ResourceLocation("bottle_drinkable");
	private static final ResourceLocation CARPET_MODEL = Util.getResource("carpet");
	private static final ResourceLocation CAULDRON_MODEL = Util.getResource("cauldron");

	@SubscribeEvent
	public void registerModels(ModelRegistryEvent event) {
		setModelStateMapper(InspirationsTweaks.carpet, new PropertyStateMapper(CARPET_MODEL,
				BlockCarpet.COLOR,
				BlockFittedCarpet.NORTHWEST, BlockFittedCarpet.NORTHEAST, BlockFittedCarpet.SOUTHWEST, BlockFittedCarpet.SOUTHEAST
				));
		setModelStateMapper(InspirationsTweaks.cauldron, new NameStateMapper(CAULDRON_MODEL));

		if(Config.betterCauldronItem) {
			registerItemModel(Items.CAULDRON, 0, CAULDRON_MODEL);
		}
		if(Config.enableCauldronDyeing) {
			registerItemModel(InspirationsTweaks.dyedWaterBottle, POTION_MODEL);
		}
	}

	@SubscribeEvent
	public void registerBlockColors(ColorHandlerEvent.Block event) {
		BlockColors blockColors = event.getBlockColors();

		// coloring of liquid inside, either for potions or dyes
		registerBlockColors(blockColors, (state, world, pos, tintIndex) -> {
			if(state.getValue(BlockEnhancedCauldron.CONTENTS) != ICauldronRecipe.CauldronContents.WATER && tintIndex == 1) {
				TileEntity te = world.getTileEntity(pos);
				if(te instanceof TileCauldron) {
					return ((TileCauldron) te).getColor();
				}
			}

			return -1;
		}, InspirationsTweaks.cauldron);
	}

	@SubscribeEvent
	public void registerItemColors(ColorHandlerEvent.Item event) {
		ItemColors itemColors = event.getItemColors();

		// dyed water bottles
		registerItemColors(itemColors, (stack, tintIndex) -> {
			if(tintIndex == 0) {
				return InspirationsTweaks.dyedWaterBottle.getColor(stack);
			}
			return -1;
		}, InspirationsTweaks.dyedWaterBottle);
	}


	/**
	 * Replaces the bookshelf models with the dynamic texture model, which also handles books
	 */
	@SubscribeEvent
	public void onModelBake(ModelBakeEvent event) {
		if(InspirationsTweaks.cauldron == null) {
			return;
		}

		for(int i = 1; i <= 3; i++) {
			replaceCauldronModel(event, new ModelResourceLocation(CAULDRON_MODEL,
					String.format("contents=fluid,level=%s", i)));
		}
	}

	private static void replaceCauldronModel(ModelBakeEvent event, ModelResourceLocation location) {
		IModel model = ModelLoaderRegistry.getModelOrLogError(location, "Error loading model for " + location);
		IBakedModel standard = event.getModelRegistry().getObject(location);
		IBakedModel finalModel = new CauldronModel(standard, model, DefaultVertexFormats.BLOCK);

		event.getModelRegistry().putObject(location, finalModel);
	}
}
