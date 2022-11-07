package knightminer.inspirations.common;

import knightminer.inspirations.Inspirations;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.client.renderer.item.ItemPropertyFunction;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;

import javax.annotation.Nullable;

public abstract class AbstractClientEvents {
  /*
   * Null safe handlers
   */

  /**
   * Null safe method to register block colors. Works around an issue where registry events do not fire
   * @param blockColors Block colors instance
   * @param handler     Color handler logic
   * @param blocks      List of blocks to register
   */
  protected static void registerBlockColors(BlockColors blockColors, BlockColor handler, Block... blocks) {
    for (Block block : blocks) {
      if (block != null) {
        blockColors.register(handler, block);
      }
    }
  }

  /**
   * Null safe method to register item colors. Works around an issue where registry events do not fire
   * @param itemColors Item colors instance
   * @param handler    Color handler logic
   * @param items      List of items to register
   */
  protected static void registerItemColors(ItemColors itemColors, ItemColor handler, ItemLike... items) {
    for (ItemLike item : items) {
      if (item != null && item.asItem() != Items.AIR) {
        itemColors.register(handler, item);
      }
    }
  }

  /**
   * Null safe way to register a model property. Works around an issue where registry events do not fire
   * @param item Item with the property
   * @param name Property name, will be namespaced under Inspirations
   * @param prop Property getter instance
   */
  protected static void registerModelProperty(@Nullable ItemLike item, String name, ItemPropertyFunction prop) {
    if (item != null) {
      ItemProperties.register(item.asItem(), Inspirations.getResource(name), prop);
    }
  }

  /**
   * Null safe way to register a render layer. Works around an issue where registry events do not fire
   * @param block       Block to register
   * @param renderLayer Render layer
   */
  protected static void setRenderLayer(@Nullable Block block, RenderType renderLayer) {
    if (block != null) {
      ItemBlockRenderTypes.setRenderLayer(block, renderLayer);
    }
  }

  /**
   * Null safe way to reigster a screen factory
   * @param type    Container type
   * @param factory Screen factory
   * @param <M>     Container type
   * @param <U>     Screen type
   */
  protected static <M extends AbstractContainerMenu, U extends Screen & MenuAccess<M>> void registerScreenFactory(@Nullable MenuType<? extends M> type, MenuScreens.ScreenConstructor<M,U> factory) {
    if (type != null) {
      MenuScreens.register(type, factory);
    }
  }
}
