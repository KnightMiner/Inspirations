package knightminer.inspirations.shared;

import knightminer.inspirations.common.ClientProxy;
import knightminer.inspirations.shared.client.RenderModArrow;
import knightminer.inspirations.shared.entity.EntityModArrow;
import knightminer.inspirations.shared.item.ItemModArrow.ArrowType;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class SharedClientProxy extends ClientProxy {

	@Override
	public void preInit() {
		super.preInit();

		RenderingRegistry.registerEntityRenderingHandler(EntityModArrow.class, RenderModArrow::new);
	}

	@SubscribeEvent
	public void registerModels(ModelRegistryEvent event) {
		registerItemMetaDynamic(InspirationsShared.materials);
		registerItemMetaDynamic(InspirationsShared.edibles);

		// items
		for(ArrowType type : ArrowType.values()) {
			registerItemModel(InspirationsShared.arrow, type.getMeta(), type.getName());
		}
	}
}
