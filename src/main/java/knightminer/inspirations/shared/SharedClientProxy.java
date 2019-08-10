package knightminer.inspirations.shared;

import knightminer.inspirations.Inspirations;
import knightminer.inspirations.common.ClientProxy;
import knightminer.inspirations.library.Util;
import knightminer.inspirations.library.client.ClientUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.IReloadableResourceManager;
import net.minecraft.resources.IResourceManager;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.concurrent.CompletableFuture;

public class SharedClientProxy extends ClientProxy {

	@Override
	public void preInit() {
		super.preInit();

		// listener to clear color cache from client utils
		IResourceManager manager = Minecraft.getInstance().getResourceManager();
		// should always be true, but just in case
		if(manager instanceof IReloadableResourceManager) {
			((IReloadableResourceManager) manager).addReloadListener(
					(stage, resMan, prepProp, reloadProf, bgExec, gameExec) -> CompletableFuture
									.runAsync(ClientUtil::clearCache, gameExec)
									.thenCompose(stage::markCompleteAwaitingOthers)
			);
		} else {
			Inspirations.log.error("Failed to register resource reload listener, expected instance of IReloadableResourceManager but got {}", manager.getClass());
		}
	}

	@SubscribeEvent
	public void registerModels(ModelRegistryEvent event) {
		registerItemMetaDynamic(InspirationsShared.materials);
		registerItemMetaDynamic(InspirationsShared.edibles);
	}

	@SubscribeEvent
	public void registerTextures(TextureStitchEvent.Pre event) {
		// ensures the colorless fluid texture is loaded
		TextureMap map = event.getMap();
		map.registerSprite(Util.getResource("blocks/fluid_colorless"));
		map.registerSprite(Util.getResource("blocks/fluid_colorless_flow"));
	}
}
