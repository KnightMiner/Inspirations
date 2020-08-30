package knightminer.inspirations.tweaks;

import knightminer.inspirations.Inspirations;
import knightminer.inspirations.common.ClientEvents;
import knightminer.inspirations.common.Config;
import knightminer.inspirations.library.client.model.TrimModel;
import knightminer.inspirations.shared.SharedClientEvents;
import knightminer.inspirations.tweaks.client.PortalColorHandler;
import net.minecraft.block.Blocks;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.biome.BiomeColors;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.registries.ForgeRegistries;

@SuppressWarnings({"unused", "WeakerAccess"})
@EventBusSubscriber(modid = Inspirations.modID, value = Dist.CLIENT, bus = Bus.MOD)
public class TweaksClientEvents extends ClientEvents {
  @SubscribeEvent
  static void clientSetup(FMLClientSetupEvent event) {
    RenderType cutout = RenderType.getCutout();
    RenderTypeLookup.setRenderLayer(InspirationsTweaks.cactus, cutout);
    RenderTypeLookup.setRenderLayer(InspirationsTweaks.sugarCane, cutout);

    MinecraftForge.EVENT_BUS.addListener(TweaksClientEvents::fixShieldTooltip);
  }

  @SubscribeEvent
  static void modelRegistry(ModelRegistryEvent event) {
    // add model replacements to the config pack
    SharedClientEvents.configPack.addBlockstateReplacement(Config.customPortalColor, Blocks.NETHER_PORTAL, "nether_portal");
    SharedClientEvents.configPack.addItemModelReplacement(Config.coloredEnchantedRibbons, Items.ENCHANTED_BOOK, "enchanted_book");
    SharedClientEvents.configPack.addItemModelReplacement(Config.coloredFireworkItems, Items.FIREWORK_ROCKET, "fireworks");
    SharedClientEvents.configPack.addItemModelReplacement(Config.betterCauldronItem, Items.CAULDRON, "cauldron");
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
      return BiomeColors.getGrassColor(world, pos);
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
        for (INBT tag : EnchantedBookItem.getEnchantments(stack)) {
          if (tag.getId() == Constants.NBT.TAG_COMPOUND) {
            ResourceLocation id = new ResourceLocation(((CompoundNBT)tag).getString("id"));
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
        switch (rarity) {
          case COMMON:
            return 0xFF2151;
          case UNCOMMON:
            return 0xE2882D;
          case RARE:
            return 0x00FF21;
          case VERY_RARE:
            return 0x9F7FFF;
        }
      }
      return -1;
    }, Items.ENCHANTED_BOOK);

    registerItemColors(itemColors, (stack, tintIndex) -> {
      if (!Config.coloredFireworkItems.get()) {
        return -1;
      }
      CompoundNBT nbt = stack.getChildTag("Fireworks");
      // string is darker with more gunpowder
      if (tintIndex == 2) {
        if (nbt != null && nbt.contains("Flight", Constants.NBT.TAG_ANY_NUMERIC)) {
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

        ListNBT stars = nbt.getList("Explosions", 10);
        // not enough stars?
        if (tintIndex >= stars.size()) {
          return missing;
        }

        // grab the proper star's first color
        CompoundNBT star = stars.getCompound(tintIndex);
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
    if (!Config.fixShieldTooltip.get()) {
      return;
    }
    ItemStack stack = event.getItemStack();
    if (stack.getItem() != Items.SHIELD) {
      return;
    }
    CompoundNBT tags = stack.getChildTag("BlockEntityTag");
    if (tags != null && tags.contains("Patterns") && stack.isEnchanted()) {
      ListNBT patterns = tags.getList("Patterns", 10);
      event.getToolTip().add(patterns.size() + 1, new StringTextComponent(""));
    }
  }
}
