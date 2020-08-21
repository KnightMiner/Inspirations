package knightminer.inspirations.recipes;

import knightminer.inspirations.Inspirations;
import knightminer.inspirations.common.ClientEvents;
import knightminer.inspirations.recipes.item.MixedDyedBottleItem;
import knightminer.inspirations.recipes.recipe.cauldron.contents.CauldronColor;
import knightminer.inspirations.recipes.recipe.cauldron.contents.CauldronPotion;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(modid = Inspirations.modID, value = Dist.CLIENT, bus = Bus.MOD)
public class RecipesClientEvents extends ClientEvents {
	/* TODO: reimplement
	@SubscribeEvent
	static void registerModels(ModelRegistryEvent event) {
		setModelStateMapper(InspirationsRecipes.cauldron, new CauldronStateMapper(CAULDRON_MODEL));
	}

	@SubscribeEvent
	static void registerBlockColors(ColorHandlerEvent.Block event) {
		BlockColors blockColors = event.getBlockColors();

		// coloring of liquid inside, either for potions or dyes
		registerBlockColors(blockColors, (state, world, pos, tintIndex) -> {
			if(tintIndex == 1) {
				TileEntity te = world.getTileEntity(pos);
				if(te instanceof CauldronTileEntity) {
					return ((CauldronTileEntity) te).getColor();
				}
			}

			return -1;
		}, InspirationsRecipes.cauldron);
	}
	*/

	@SubscribeEvent
	static void registerItemColors(ColorHandlerEvent.Item event) {
		ItemColors itemColors = event.getItemColors();

		// dyed water bottles
    InspirationsRecipes.simpleDyedWaterBottle.forEach((color, bottle) -> {
      itemColors.register((stack, index) -> index == 1 ? color.getColorValue() : -1, bottle);
    });
    registerItemColors(itemColors, (stack, index) -> index == 1 ? MixedDyedBottleItem.dyeFromBottle(stack) : -1, InspirationsRecipes.mixedDyedWaterBottle);
	}

	@SubscribeEvent
	static void registerTextures(TextureStitchEvent.Pre event) {
	  if (PlayerContainer.LOCATION_BLOCKS_TEXTURE.equals(event.getMap().getTextureLocation())) {
	    event.addSprite(InspirationsRecipes.STILL_FLUID);
      event.addSprite(InspirationsRecipes.FLOWING_FLUID);
      event.addSprite(CauldronColor.TEXTURE);
      event.addSprite(CauldronPotion.TEXTURE);
    }
	}
}
