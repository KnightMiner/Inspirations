package knightminer.inspirations.recipes.recipe.anvil.conditions;

import knightminer.inspirations.library.Util;
import knightminer.inspirations.recipes.recipe.anvil.IBlockStateCondition;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.Arrays;

/**
 * Condition that is true if the block can be broken by a specific tool.
 */
public class HarvestToolBlockStateCondition implements IBlockStateCondition {
    private final String[] tools;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HarvestToolBlockStateCondition that = (HarvestToolBlockStateCondition) o;
        return Arrays.equals(tools, that.tools);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(tools);
    }

    public HarvestToolBlockStateCondition(String tool) {
        this(new String[]{tool});
    }

    public HarvestToolBlockStateCondition(String[] tools) {
        this.tools = tools;
    }

    @Override
    public boolean matches(World world, BlockPos pos, @Nonnull IBlockState state) {
        String tool = state.getBlock().getHarvestTool(state);
        return Arrays.stream(tools).anyMatch(tool::equals);
    }

    @Override
    public String getTooltip() {
        return Util.translateFormatted("gui.jei.anvil_smashing.condition.harvesttool", String.join(", ", tools));
    }

    public static HarvestToolBlockStateCondition fromConfig(@Nonnull String config) {
        String[] tools = config.split(",");
        return new HarvestToolBlockStateCondition(tools);
    }
}
