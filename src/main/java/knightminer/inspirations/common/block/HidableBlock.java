package knightminer.inspirations.common.block;

import knightminer.inspirations.common.IHidable;
import net.minecraft.block.Block;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

import javax.annotation.Nonnull;
import java.util.function.Supplier;

public class HidableBlock extends Block implements IHidable {
    private final Supplier<Boolean> enabled;

    public HidableBlock(Properties properties, Supplier<Boolean> isEnabled) {
        super(properties);
        this.enabled = isEnabled;
    }

    public boolean isEnabled() {
        return enabled.get();
    }

    @Override
    public void fillItemGroup(@Nonnull ItemGroup group, @Nonnull NonNullList<ItemStack> items) {
        if(group == ItemGroup.SEARCH || isEnabled()) {
            super.fillItemGroup(group, items);
        }
    }
}
