package knightminer.inspirations.recipes.recipe.anvil.conditions;

import knightminer.inspirations.library.Util;
import knightminer.inspirations.recipes.recipe.anvil.IBlockStateCondition;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.Objects;

/**
 * Matches the block to an ore dictionary name.
 */
public class OreDictBlockStateCondition implements IBlockStateCondition {
    private final String oreDictName;

    private NonNullList<ItemStack> blocks;

    public OreDictBlockStateCondition(@Nonnull String oreDictName) {
        this.oreDictName = oreDictName;
    }

    @Override
    public boolean matches(World world, BlockPos pos, @Nonnull IBlockState state) {
        initBlocks();

        if (blocks.isEmpty()) {
            return false;
        }

        // match the block to the ore dictionary entries
        return blocks.stream().anyMatch(itemStack -> OreDictionary.itemMatches(itemStack, new ItemStack(state.getBlock()), false));
    }

    public NonNullList<ItemStack> getBlocks() {
        this.initBlocks();
        return blocks;
    }

    private void initBlocks() {
        if (blocks == null) {
            NonNullList<ItemStack> ores = OreDictionary.getOres(oreDictName);
            NonNullList<ItemStack> filtered = NonNullList.create();
            ores.stream().filter(this::isBlock).forEach(filtered::add);
            blocks = filtered;
        }
    }

    private boolean isBlock(ItemStack stack) {
        if (!stack.isEmpty()) {
            return Block.getBlockFromItem(stack.getItem()) != Blocks.AIR;
        }
        return false;
    }

    @Override
    public String getTooltip() {
        return Util
                .translateFormatted("gui.jei.anvil_smashing.condition.oredict", oreDictName);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        OreDictBlockStateCondition that = (OreDictBlockStateCondition) o;
        return Objects.equals(oreDictName, that.oreDictName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(oreDictName);
    }

    public static OreDictBlockStateCondition fromConfig(@Nonnull String config) {
        return new OreDictBlockStateCondition(config);
    }
}
