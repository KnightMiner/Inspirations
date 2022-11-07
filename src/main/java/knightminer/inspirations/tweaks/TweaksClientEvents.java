package knightminer.inspirations.tweaks;

import knightminer.inspirations.Inspirations;
import knightminer.inspirations.common.AbstractClientEvents;
import knightminer.inspirations.common.CommonsClientEvents;
import knightminer.inspirations.common.Config;
import knightminer.inspirations.library.client.model.TrimModel;
import knightminer.inspirations.tweaks.client.PortalColorHandler;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;

@SuppressWarnings({"unused", "WeakerAccess"})
@EventBusSubscriber(modid = Inspirations.modID, value = Dist.CLIENT, bus = Bus.MOD)
public class TweaksClientEvents extends AbstractClientEvents {
  @SubscribeEvent
  static void clientSetup(FMLClientSetupEvent event) {
    RenderType cutout = RenderType.cutout();
    ItemBlockRenderTypes.setRenderLayer(InspirationsTweaks.cactus, cutout);
    ItemBlockRenderTypes.setRenderLayer(InspirationsTweaks.sugarCane, cutout);

    MinecraftForge.EVENT_BUS.addListener(TweaksClientEvents::fixShieldTooltip);
  }

  @SubscribeEvent
  static void modelRegistry(ModelRegistryEvent event) {
    // add model replacements to the config pack
    CommonsClientEvents.configPack.addBlockstateReplacement(Config.customPortalColor, Blocks.NETHER_PORTAL, "nether_portal");
    CommonsClientEvents.configPack.addItemModelReplacement(Config.coloredEnchantedRibbons, Items.ENCHANTED_BOOK, "enchanted_book");
    CommonsClientEvents.configPack.addItemModelReplacement(Config.coloredFireworkItems, Items.FIREWORK_ROCKET, "fireworks");
    CommonsClientEvents.configPack.addItemModelReplacement(Config.betterCauldronItem, Items.CAULDRON, "cauldron");
  }

  @SubscribeEvent
  static void registerModelLoaders(ModelRegistryEvent event) {
    ModelLoaderRegistry.registerLoader(Inspirations.getResource("trim"), TrimModel.LOADER);
  }

  @SubscribeEvent
  static void registerBlockColors(ColorHandlerEvent.Block event) {
    BlockColors colors = event.getBlockColors();

    // coloring on sugar cane crop to match reeds
    registerBlockColors(colors, (state, world, pos, index) -> {
      if (world == null || pos == null) {
        return -1;
      }
      return BiomeColors.getAverageGrassColor(world, pos);
    }, InspirationsTweaks.sugarCane);

    // portal tinting
    registerBlockColors(colors, PortalColorHandler.INSTANCE, Blocks.NETHER_PORTAL);
  }

  @SubscribeEvent
  static void registerItemColors(ColorHandlerEvent.Item event) {
    ItemColors itemColors = event.getItemColors();

    // colored ribbons on enchanted books
    registerItemColors(itemColors, (stack, tintIndex) -> {
      if (tintIndex == 0 && Config.coloredEnchantedRibbons.get()) {
        // find the rarest enchantment we have
        Enchantment.Rarity rarity = Enchantment.Rarity.COMMON;
        for (Tag tag : EnchantedBookItem.getEnchantments(stack)) {
          if (tag.getId() == Tag.TAG_COMPOUND) {
            ResourceLocation id = new ResourceLocation(((CompoundTag)tag).getString("id"));
            Enchantment enchantment = ForgeRegistries.ENCHANTMENTS.getValue(id);
            if (enchantment != null) {
              Enchantment.Rarity newRarity = enchantment.getRarity();
              if (newRarity.getWeight() < rarity.getWeight()) {
                rarity = newRarity;
              }
            }
          }
        }

        // color by that rarity
        return switch (rarity) {
          case COMMON -> 0xFF2151;
          case UNCOMMON -> 0xE2882D;
          case RARE -> 0x00FF21;
          case VERY_RARE -> 0x9F7FFF;
        };
      }
      return -1;
    }, Items.ENCHANTED_BOOK);

    registerItemColors(itemColors, (stack, tintIndex) -> {
      if (!Config.coloredFireworkItems.get()) {
        return -1;
      }
      CompoundTag nbt = stack.getTagElement("Fireworks");
      // string is darker with more gunpowder
      if (tintIndex == 2) {
        if (nbt != null && nbt.contains("Flight", Tag.TAG_ANY_NUMERIC)) {
          byte flight = nbt.getByte("Flight");
          switch (flight) {
            case 1:
              return 0x808080;
            case 2:
              return 0x606060;
            case 3:
              return 0x303030;
          }
          if (flight > 3) {
            return 0x000000;
          }
        }
        return 0xA0A0A0;
      }
      // color the stripes and the top
      if (tintIndex == 0 || tintIndex == 1) {
        // no NBT?
        int missing = tintIndex == 1 ? 0xCCA190 : 0xC0C0C0;
        if (nbt == null) {
          return missing;
        }

        ListTag stars = nbt.getList("Explosions", 10);
        // not enough stars?
        if (tintIndex >= stars.size()) {
          return missing;
        }

        // grab the proper star's first color
        CompoundTag star = stars.getCompound(tintIndex);
        int[] colors = star.getIntArray("Colors");
        if (colors.length > 0) {
          return colors[0];
        }

        return missing;
      }

      return -1;
    }, Items.FIREWORK_ROCKET);
  }

  // registered with Forge bus
  private static void fixShieldTooltip(ItemTooltipEvent event) {
    if (!Config.fixShieldTooltip.getAsBoolean()) return;
    ItemStack stack = event.getItemStack();
    if (stack.getItem() != Items.SHIELD) return;

    // only need to run if it has patterns and is enchanted
    CompoundTag tags = stack.getTagElement("BlockEntityTag");
    if (tags != null && tags.contains("Patterns") && stack.isEnchanted()) {
      // find the last banner pattern line in the tooltip
      List<Component> text = event.getToolTip();
      int i = text.size() - 1;
      for (; i >= 0; i--) {
        Component component = text.get(i);
        if (component instanceof TranslatableComponent && ((TranslatableComponent)component).getKey().contains("banner")) {
          text.add(i + 1, TextComponent.EMPTY);
          break;
        }
      }
    }
  }
}
