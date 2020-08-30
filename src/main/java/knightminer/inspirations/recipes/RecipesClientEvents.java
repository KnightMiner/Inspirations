package knightminer.inspirations.recipes;

import knightminer.inspirations.Inspirations;
import knightminer.inspirations.common.ClientEvents;
import knightminer.inspirations.common.Config;
import knightminer.inspirations.library.client.model.CauldronModel;
import knightminer.inspirations.library.recipe.cauldron.CauldronContentTypes;
import knightminer.inspirations.library.recipe.cauldron.contents.ICauldronContents;
import knightminer.inspirations.recipes.client.BoilingParticle;
import knightminer.inspirations.recipes.item.MixedDyedBottleItem;
import knightminer.inspirations.recipes.recipe.cauldron.contents.ColorContentType;
import knightminer.inspirations.recipes.recipe.cauldron.contents.PotionContentType;
import knightminer.inspirations.recipes.tileentity.CauldronTileEntity;
import knightminer.inspirations.shared.SharedClientEvents;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.biome.BiomeColors;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@SuppressWarnings("unused")
@EventBusSubscriber(modid = Inspirations.modID, value = Dist.CLIENT, bus = Bus.MOD)
public class RecipesClientEvents extends ClientEvents {
	@SubscribeEvent
	static void registerModelLoaders(ModelRegistryEvent event) {
		ModelLoaderRegistry.registerLoader(Inspirations.getResource("cauldron"), CauldronModel.LOADER);
		SharedClientEvents.configPack.addBlockstateReplacement(Config.extendedCauldron, Blocks.CAULDRON, "cauldron");
	}

	@SubscribeEvent
	static void registerBlockColors(ColorHandlerEvent.Block event) {
		BlockColors blockColors = event.getBlockColors();

		// coloring of liquid inside, for fluids, potions, and dyes
		registerBlockColors(blockColors, (state, world, pos, tintIndex) -> {
			// skip tint index 0, that is particles
			if (tintIndex > 0 && world != null && pos != null) {
				// must be cauldron TE
				TileEntity te = world.getTileEntity(pos);
				if(te instanceof CauldronTileEntity) {
					// if it contains water, run vanilla tinting
					ICauldronContents contents = ((CauldronTileEntity) te).getContents();
					if (!contents.matches(CauldronContentTypes.FLUID, Fluids.WATER)) {
						return contents.getTintColor();
					}
				}
				// water tinting if contains water or TE is missing
				return BiomeColors.getWaterColor(world, pos);
			}

			return -1;
		}, InspirationsRecipes.cauldron, InspirationsRecipes.boilingCauldron);
	}

	@SubscribeEvent
	static void clientSetup(FMLClientSetupEvent event) {
		Minecraft.getInstance().particles.registerFactory(InspirationsRecipes.boilingParticle, BoilingParticle.Factory::new);
	}

	@SubscribeEvent
	static void registerItemColors(ColorHandlerEvent.Item event) {
		ItemColors itemColors = event.getItemColors();

		// dyed water bottles
    InspirationsRecipes.simpleDyedWaterBottle.forEach((color, bottle) ->
			itemColors.register((stack, index) -> index == 1 ? color.getColorValue() : -1, bottle));
    registerItemColors(itemColors, (stack, index) -> index == 1 ? MixedDyedBottleItem.dyeFromBottle(stack) : -1, InspirationsRecipes.mixedDyedWaterBottle);
	}

	@SubscribeEvent
	static void registerTextures(TextureStitchEvent.Pre event) {
	  if (PlayerContainer.LOCATION_BLOCKS_TEXTURE.equals(event.getMap().getTextureLocation())) {
	    event.addSprite(InspirationsRecipes.STILL_FLUID);
      event.addSprite(InspirationsRecipes.FLOWING_FLUID);
      event.addSprite(ColorContentType.TEXTURE);
      event.addSprite(PotionContentType.TEXTURE);
    }
	}
}
