package knightminer.inspirations.shared;

import knightminer.inspirations.Inspirations;
import knightminer.inspirations.common.ClientProxy;
import knightminer.inspirations.library.client.ClientUtil;
import knightminer.inspirations.shared.client.RenderModArrow;
import knightminer.inspirations.shared.entity.EntityModArrow;
import knightminer.inspirations.shared.item.ItemModArrow.ArrowType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.IResourceManager;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class SharedClientProxy extends ClientProxy {

	@Override
	public void preInit() {
		super.preInit();

		RenderingRegistry.registerEntityRenderingHandler(EntityModArrow.class, RenderModArrow::new);

		// listener to clear color cache from client utils
		IResourceManager manager = Minecraft.getMinecraft().getResourceManager();
		// should always be true, but just in case
		if(manager instanceof IReloadableResourceManager) {
			((IReloadableResourceManager) manager).registerReloadListener(ClientUtil::onResourceReload);
		} else {
			Inspirations.log.error("Failed to register resource reload listener, expected instance of IReloadableResourceManager but got {}", manager.getClass());
		}
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
