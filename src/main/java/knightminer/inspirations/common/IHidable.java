package knightminer.inspirations.common;

import net.minecraft.world.item.CreativeModeTab;

/**
 * Used for blocks/items that are disableable in the config.
 */
public interface IHidable {
  /**
   * Is this block/item enabled in configuration?
   * @return true if the block/item is enabled.
   */
  boolean isEnabled();

  /**
   * Predicate for fillItemGroup(), to centralise code.
   * @param group The ItemGroup to potentially add to.
   * @return If super().fillItemGroup() should be called.
   */
  default boolean shouldAddtoItemGroup(CreativeModeTab group) {
    // We can't check isEnabled() safely before configs are loaded.
    // MC tries to fill item groups early for the search dictionary, so just
    // return true in that situation.
    if (Config.isLoaded()) {
      return isEnabled();
    } else {
      return group == CreativeModeTab.TAB_SEARCH;
    }
  }
}
