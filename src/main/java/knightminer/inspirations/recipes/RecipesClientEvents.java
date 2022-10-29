package knightminer.inspirations.recipes;

import knightminer.inspirations.Inspirations;
import knightminer.inspirations.common.ClientEvents;
import knightminer.inspirations.common.Config;
import knightminer.inspirations.library.MiscUtil;
import knightminer.inspirations.library.client.CustomTextureLoader;
import knightminer.inspirations.library.client.model.CauldronModel;
import knightminer.inspirations.recipes.client.BoilingParticle;
import knightminer.inspirations.recipes.item.MixedDyedBottleItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@SuppressWarnings("unused")
@EventBusSubscriber(modid = Inspirations.modID, value = Dist.CLIENT, bus = Bus.MOD)
public class RecipesClientEvents extends ClientEvents {
  /**
   * Listener handling custom cauldron textures
   */
  public static CustomTextureLoader cauldronTextures = new CustomTextureLoader(Inspirations.getResource("cauldron_textures"));

  /** Called during mod construct to register early listeners */
  public static void onConstruct() {
    // listener to clear color cache from client utils
    ResourceManager manager = Minecraft.getInstance().getResourceManager();
    if (manager instanceof ReloadableResourceManager) {
      ((ReloadableResourceManager)manager).registerReloadListener(cauldronTextures);
    } else {
      Inspirations.log.error("Failed to register resource reload listener, expected instance of IReloadableResourceManager but got {}", manager.getClass());
    }
  }

  @SubscribeEvent
  static void clientSetup(FMLClientSetupEvent event) {
    ItemBlockRenderTypes.setRenderLayer(InspirationsRecipes.honey, RenderType.translucent());
    ItemBlockRenderTypes.setRenderLayer(InspirationsRecipes.honey.getFlowing(), RenderType.translucent());
    if (Config.extendedCauldron.get()) {
      ItemBlockRenderTypes.setRenderLayer(Blocks.CAULDRON, RenderType.cutout());
    }
  }

  @SubscribeEvent
  static void registerModelLoaders(ModelRegistryEvent event) {
    ModelLoaderRegistry.registerLoader(Inspirations.getResource("cauldron"), CauldronModel.LOADER);
  }

  @SubscribeEvent
  static void registerParticleFactories(ParticleFactoryRegisterEvent event) {
    Minecraft.getInstance().particleEngine.register(InspirationsRecipes.boilingParticle, BoilingParticle.Factory::new);
  }

  @SubscribeEvent
  static void registerItemColors(ColorHandlerEvent.Item event) {
    ItemColors itemColors = event.getItemColors();

    // dyed water bottles
    InspirationsRecipes.simpleDyedWaterBottle.forEach((color, bottle) -> itemColors.register((stack, index) -> index == 0 ? MiscUtil.getColor(color) : -1, bottle));
    registerItemColors(itemColors, (stack, index) -> index == 0 ? MixedDyedBottleItem.dyeFromBottle(stack) : -1, InspirationsRecipes.mixedDyedWaterBottle);
  }
}
