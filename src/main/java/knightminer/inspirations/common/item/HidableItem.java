package knightminer.inspirations.common.item;

import knightminer.inspirations.common.IHidable;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.ForgeConfigSpec;

import javax.annotation.Nonnull;
import java.util.function.Supplier;

/**
 * Item which is hidden if the config value is disabled.
 */
public class HidableItem extends Item implements IHidable {
    private final Supplier<Boolean> enabled;

    public HidableItem(Properties properties, Supplier<Boolean> isEnabled) {
        super(properties);
        this.enabled = isEnabled;
    }

    public boolean isEnabled() {
        return enabled.get();
    }

    @Override
    public void fillItemGroup(@Nonnull ItemGroup group, @Nonnull NonNullList<ItemStack> items) {
        if(shouldAddtoItemGroup(group)) {
            super.fillItemGroup(group, items);
        }
    }
}
