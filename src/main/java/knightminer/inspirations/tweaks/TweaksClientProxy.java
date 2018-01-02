package knightminer.inspirations.tweaks;

import java.util.LinkedHashMap;

import com.google.common.collect.Maps;

import knightminer.inspirations.common.ClientProxy;
import knightminer.inspirations.common.Config;
import knightminer.inspirations.common.PulseBase;
import knightminer.inspirations.library.Util;
import knightminer.inspirations.library.client.PropertyStateMapper;
import knightminer.inspirations.recipes.RecipesClientProxy;
import knightminer.inspirations.tweaks.block.BlockBetterFlowerPot;
import knightminer.inspirations.tweaks.block.BlockFittedCarpet;
import net.minecraft.block.BlockCarpet;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.init.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class TweaksClientProxy extends ClientProxy {

	private static final ResourceLocation CARPET_MODEL = Util.getResource("carpet");
	private static final ResourceLocation CAULDRON_ITEM_MODEL = Util.getResource("cauldron_item");
	private static final ModelResourceLocation FLOWER_POT_MODEL = new ModelResourceLocation(Util.getResource("flower_pot"), "normal");

	@SubscribeEvent
	public void registerModels(ModelRegistryEvent event) {
		setModelStateMapper(InspirationsTweaks.carpet, new PropertyStateMapper(CARPET_MODEL,
				BlockCarpet.COLOR,
				BlockFittedCarpet.NORTHWEST, BlockFittedCarpet.NORTHEAST, BlockFittedCarpet.SOUTHWEST, BlockFittedCarpet.SOUTHEAST
				));
		setModelStateMapper(InspirationsTweaks.flowerPot, new FlowerPotStateMapper());

		if(Config.betterCauldronItem) {
			// if recipes is loaded, pull that model as there is a chance the two are different
			// the extended cauldron needed to replace the model to add tintindex's
			ResourceLocation model = CAULDRON_ITEM_MODEL;
			if(PulseBase.isRecipesLoaded() && Config.enableExtendedCauldron) {
				model = RecipesClientProxy.CAULDRON_MODEL;
			}
			registerItemModel(Items.CAULDRON, 0, model);
		}
	}

	/**
	 * Replaces the bookshelf models with the dynamic texture model, which also handles books
	 */
	@SubscribeEvent
	public void onModelBake(ModelBakeEvent event) {
		if(InspirationsTweaks.flowerPot != null) {
			replaceTexturedModel(event, FLOWER_POT_MODEL, "plant", false);
		}
	}

	private static class FlowerPotStateMapper extends StateMapperBase {
		@Override
		protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
			if(state.getValue(BlockBetterFlowerPot.EXTRA)) {
				return FLOWER_POT_MODEL;
			}

			LinkedHashMap<IProperty<?>, Comparable<?>> map = Maps.newLinkedHashMap(state.getProperties());
			map.remove(BlockBetterFlowerPot.EXTRA);
			map.remove(BlockBetterFlowerPot.LEGACY_DATA);

			return new ModelResourceLocation(state.getBlock().getRegistryName(), this.getPropertyString(map));
		}
	}
}
