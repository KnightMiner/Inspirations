package knightminer.inspirations.recipes;

import knightminer.inspirations.common.ClientProxy;
import knightminer.inspirations.common.Config;
import knightminer.inspirations.library.Util;
import knightminer.inspirations.library.client.NameStateMapper;
import knightminer.inspirations.recipes.tileentity.TileCauldron;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class RecipesClientProxy extends ClientProxy {
	private static final ResourceLocation POTION_MODEL = new ResourceLocation("bottle_drinkable");
	public static final ResourceLocation CAULDRON_MODEL = Util.getResource("cauldron");

	@SubscribeEvent
	public void registerModels(ModelRegistryEvent event) {
		setModelStateMapper(InspirationsRecipes.cauldron, new NameStateMapper(CAULDRON_MODEL));

		if(Config.enableCauldronDyeing) {
			registerItemModel(InspirationsRecipes.dyedWaterBottle, POTION_MODEL);
		}
	}

	@SubscribeEvent
	public void registerBlockColors(ColorHandlerEvent.Block event) {
		BlockColors blockColors = event.getBlockColors();

		// coloring of liquid inside, either for potions or dyes
		registerBlockColors(blockColors, (state, world, pos, tintIndex) -> {
			if(tintIndex == 1) {
				TileEntity te = world.getTileEntity(pos);
				if(te instanceof TileCauldron) {
					return ((TileCauldron) te).getColor();
				}
			}

			return -1;
		}, InspirationsRecipes.cauldron);
	}

	@SubscribeEvent
	public void registerItemColors(ColorHandlerEvent.Item event) {
		ItemColors itemColors = event.getItemColors();

		// dyed water bottles
		registerItemColors(itemColors, (stack, tintIndex) -> {
			if(tintIndex == 0) {
				return InspirationsRecipes.dyedWaterBottle.getColor(stack);
			}
			return -1;
		}, InspirationsRecipes.dyedWaterBottle);
	}


	/**
	 * Replaces the bookshelf models with the dynamic texture model, which also handles books
	 */
	@SubscribeEvent
	public void onModelBake(ModelBakeEvent event) {
		if(InspirationsRecipes.cauldron == null) {
			return;
		}

		boolean boiling = false;
		do {
			for(int i = 1; i <= 3; i++) {
				replaceTexturedModel(event, new ModelResourceLocation(CAULDRON_MODEL, String.format("boiling=%s,contents=fluid,level=%s", boiling, i)), "water", false);
			}
			boiling = !boiling;
		} while(boiling);
	}
}
