package knightminer.inspirations.common;

import knightminer.inspirations.Inspirations;
import net.minecraft.item.ItemGroup;

import javax.annotation.Nonnull;

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
    default boolean shouldAddtoItemGroup(@Nonnull ItemGroup group) {
        // We can't check isEnabled() safely before configs are loaded.
        // MC tries to fill item groups early for the search dictionary, so just
        // return true in that situation.
        if(Inspirations.configLoaded) {
            return isEnabled();
        } else {
            return group == ItemGroup.SEARCH;
        }
    }
}
