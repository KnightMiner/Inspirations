package knightminer.inspirations.common;

import com.google.common.collect.ImmutableSet;
import knightminer.inspirations.Inspirations;
import knightminer.inspirations.library.client.ClientUtil;
import knightminer.inspirations.library.client.ConfigurableResourcePack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelEvent.BakingCompleted;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(modid = Inspirations.modID, value = Dist.CLIENT, bus = Bus.MOD)
public class CommonsClientEvents extends AbstractClientEvents {
  public static ConfigurableResourcePack configPack;

  /**
   * Called during mod constructor to run early events
   */
  public static void onConstruct() {
    Minecraft minecraft = Minecraft.getInstance();
    //noinspection ConstantValue  null during datagen
    if (minecraft != null) {
      configPack = new ConfigurableResourcePack(
        Inspirations.class,
        Inspirations.getResource("config_resources"),
        Component.translatable("pack.inspirations.config.name"),
        Component.translatable("pack.inspirations.config.description"),
        ImmutableSet.of("minecraft"));
      minecraft.getResourcePackRepository().addPackFinder(configPack);

      // add model replacements to the config pack
      configPack.addBlockstateReplacement(Config.customPortalColor, Blocks.NETHER_PORTAL, "nether_portal");
      configPack.addItemModelReplacement(Config.coloredEnchantedRibbons, Items.ENCHANTED_BOOK, "enchanted_book");
      configPack.addItemModelReplacement(Config.coloredFireworkItems, Items.FIREWORK_ROCKET, "fireworks");
      configPack.addItemModelReplacement(Config.betterCauldronItem, Items.CAULDRON, "cauldron");
    }
  }

  @SubscribeEvent
  static void reloadListeners(RegisterClientReloadListenersEvent event) {
    event.registerReloadListener(ClientUtil.RELOAD_LISTENER);
  }

  // For the textured blocks, we need to rebake the blocks with the new texture.
  // Those are private, so grab copies from these two events when they fire.
  public static ModelBakery modelLoader;

  @SubscribeEvent
  public void collectBakeParameters(BakingCompleted event) {
    modelLoader = event.getModelBakery();
  }
}
