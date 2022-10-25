package knightminer.inspirations.shared;

import com.google.common.collect.ImmutableSet;
import knightminer.inspirations.Inspirations;
import knightminer.inspirations.common.ClientEvents;
import knightminer.inspirations.library.client.ClientUtil;
import knightminer.inspirations.library.client.ConfigurableResourcePack;
import knightminer.inspirations.recipes.RecipesClientEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

@SuppressWarnings("unused")
@EventBusSubscriber(modid = Inspirations.modID, value = Dist.CLIENT, bus = Bus.MOD)
public class SharedClientEvents extends ClientEvents {
  public static ConfigurableResourcePack configPack;

  /**
   * Called during mod constructor to run early events
   */
  public static void onConstruct() {
    Minecraft minecraft = Minecraft.getInstance();
    //noinspection ConstantConditions  Not constant as minecraft is null during datagen
    if (minecraft != null) {
      configPack = new ConfigurableResourcePack(Inspirations.class, Inspirations.getResource("config_resources"), "Inspirations Config", ImmutableSet.of("minecraft"));
      minecraft.getResourcePackRepository().addPackFinder(configPack);
      RecipesClientEvents.onConstruct();
    }
  }

  @SubscribeEvent
  static void setup(FMLCommonSetupEvent event) {
    // listener to clear color cache from client utils
    ResourceManager manager = Minecraft.getInstance().getResourceManager();
    if (manager instanceof ReloadableResourceManager) {
      ((ReloadableResourceManager)manager).registerReloadListener(ClientUtil.RELOAD_LISTENER);
    } else {
      Inspirations.log.error("Failed to register resource reload listener, expected instance of IReloadableResourceManager but got {}", manager.getClass());
    }
  }

  // For the textured blocks, we need to rebake the blocks with the new texture.
  // Those are private, so grab copies from these two events when they fire.
  public static ModelBakery modelLoader;

  @SubscribeEvent
  public void collectBakeParameters(ModelBakeEvent event) {
    modelLoader = event.getModelLoader();
  }
}
