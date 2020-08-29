package knightminer.inspirations.shared;

import knightminer.inspirations.Inspirations;
import knightminer.inspirations.common.ClientEvents;
import knightminer.inspirations.library.client.ClientUtil;
import knightminer.inspirations.library.client.model.BookshelfModel;
import knightminer.inspirations.library.client.model.TrimModel;
import net.minecraft.client.Minecraft;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.resources.IReloadableResourceManager;
import net.minecraft.resources.IResourceManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

import java.util.concurrent.CompletableFuture;

@SuppressWarnings("unused")
@EventBusSubscriber(modid = Inspirations.modID, value = Dist.CLIENT, bus = Bus.MOD)
public class SharedClientEvents extends ClientEvents {
  @SubscribeEvent
  static void registerModelLoaders(ModelRegistryEvent event) {
    ModelLoaderRegistry.registerLoader(Inspirations.getResource("bookshelf"), BookshelfModel.LOADER);
    ModelLoaderRegistry.registerLoader(Inspirations.getResource("trim"), TrimModel.LOADER);
  }

  @SubscribeEvent
  static void setup(FMLCommonSetupEvent event) {
    // listener to clear color cache from client utils
    IResourceManager manager = Minecraft.getInstance().getResourceManager();
    if (manager instanceof IReloadableResourceManager) {
      ((IReloadableResourceManager)manager).addReloadListener(ClientUtil.RELOAD_LISTENER);
    } else {
      Inspirations.log.error("Failed to register resource reload listener, expected instance of IReloadableResourceManager but got {}", manager.getClass());
    }
  }

  // For the textured blocks, we need to rebake the blocks with the new texture.
  // Those are private, so grab copies from these two events when they fire.
  public static ModelLoader modelLoader;

  @SubscribeEvent
  public void collectBakeParameters(ModelBakeEvent event) {
    modelLoader = event.getModelLoader();
  }

  @SubscribeEvent
  static void registerTextures(TextureStitchEvent.Pre event) {
    // ensures the colorless fluid texture is loaded.
    if (PlayerContainer.LOCATION_BLOCKS_TEXTURE.equals(event.getMap().getTextureLocation())) {
      event.addSprite(Inspirations.getResource("block/fluid_colorless"));
      event.addSprite(Inspirations.getResource("block/fluid_colorless_flow"));
    }
  }
}
