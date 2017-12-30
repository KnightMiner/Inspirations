package knightminer.inspirations.tweaks;

import knightminer.inspirations.common.ClientProxy;
import knightminer.inspirations.common.Config;
import knightminer.inspirations.common.PulseBase;
import knightminer.inspirations.library.Util;
import knightminer.inspirations.library.client.PropertyStateMapper;
import knightminer.inspirations.recipes.RecipesClientProxy;
import knightminer.inspirations.tweaks.block.BlockFittedCarpet;
import net.minecraft.block.BlockCarpet;
import net.minecraft.init.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class TweaksClientProxy extends ClientProxy {
	private static final ResourceLocation CARPET_MODEL = Util.getResource("carpet");
	private static final ResourceLocation CAULDRON_ITEM_MODEL = Util.getResource("cauldron_item");

	@SubscribeEvent
	public void registerModels(ModelRegistryEvent event) {
		setModelStateMapper(InspirationsTweaks.carpet, new PropertyStateMapper(CARPET_MODEL,
				BlockCarpet.COLOR,
				BlockFittedCarpet.NORTHWEST, BlockFittedCarpet.NORTHEAST, BlockFittedCarpet.SOUTHWEST, BlockFittedCarpet.SOUTHEAST
				));

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
}
