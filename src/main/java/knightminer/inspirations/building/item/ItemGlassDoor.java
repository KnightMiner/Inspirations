package knightminer.inspirations.building.item;

import knightminer.inspirations.common.Config;
import knightminer.inspirations.common.IHidable;
import net.minecraft.block.Block;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.TallBlockItem;
import net.minecraft.util.NonNullList;

import javax.annotation.Nonnull;

public class ItemGlassDoor extends TallBlockItem implements IHidable {
    public ItemGlassDoor(Block blockIn, Properties builder) {
        super(blockIn, builder);
    }

    @Override
    public int getBurnTime(ItemStack itemStack) {
        return 0;
    }

    public boolean isEnabled() {
        return Config.enableGlassDoor.get();
    }

    @Override
    public void fillItemGroup(@Nonnull ItemGroup group, @Nonnull NonNullList<ItemStack> items) {
        if(group == ItemGroup.SEARCH || isEnabled()) {
            super.fillItemGroup(group, items);
        }
    }
}
