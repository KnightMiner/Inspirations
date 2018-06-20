package knightminer.inspirations.tools;

import knightminer.inspirations.common.ClientProxy;
import knightminer.inspirations.tools.client.RenderModArrow;
import knightminer.inspirations.tools.entity.EntityModArrow;
import knightminer.inspirations.tools.item.ItemModArrow.ArrowType;
import knightminer.inspirations.utility.block.BlockRedstoneCharge;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ToolsClientProxy extends ClientProxy {

	@Override
	public void preInit() {
		super.preInit();
		RenderingRegistry.registerEntityRenderingHandler(EntityModArrow.class, RenderModArrow::new);
	}

	@SubscribeEvent
	public void registerModels(ModelRegistryEvent event) {
		setModelStateMapper(InspirationsTools.redstoneCharge, new StateMap.Builder().ignore(BlockRedstoneCharge.FACING, BlockRedstoneCharge.QUICK).build());

		// items
		registerItemModel(InspirationsTools.redstoneCharger);

		// items
		for(ArrowType type : ArrowType.values()) {
			registerItemModel(InspirationsTools.arrow, type.getMeta(), type.getName());
		}
	}
}
