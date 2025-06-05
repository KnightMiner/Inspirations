package knightminer.inspirations.tweaks;

import knightminer.inspirations.Inspirations;
import knightminer.inspirations.common.AbstractClientEvents;
import knightminer.inspirations.common.Config;
import knightminer.inspirations.library.client.model.TrimModel;
import knightminer.inspirations.tweaks.client.PortalColorHandler;
import knightminer.inspirations.tweaks.client.TintedLecternRenderer;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.ModelEvent.RegisterGeometryLoaders;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;

@EventBusSubscriber(modid = Inspirations.modID, value = Dist.CLIENT, bus = Bus.MOD)
public class TweaksClientEvents extends AbstractClientEvents {
  @SubscribeEvent
  static void clientSetup(FMLClientSetupEvent event) {
    MinecraftForge.EVENT_BUS.addListener(TweaksClientEvents::fixShieldTooltip);
  }

  @SubscribeEvent
  static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
    if (Config.tintLecternBook.getAsBoolean()) {
      event.registerBlockEntityRenderer(BlockEntityType.LECTERN, TintedLecternRenderer::new);
    }
  }

  @SubscribeEvent
  static void registerModelLoaders(RegisterGeometryLoaders event) {
    event.register("trim", TrimModel.LOADER);
  }

  @SubscribeEvent
  static void registerBlockColors(RegisterColorHandlersEvent.Block event) {
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
  static void registerItemColors(RegisterColorHandlersEvent.Item event) {
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
        if (component.getContents() instanceof TranslatableContents translatable && translatable.getKey().contains("banner")) {
          text.add(i + 1, Component.empty());
          break;
        }
      }
    }
  }
}
