package knightminer.inspirations.recipes.recipe.anvil.conditions;

import knightminer.inspirations.library.Util;
import knightminer.inspirations.recipes.recipe.anvil.AnvilSmashingItemRecipeBuilder;
import knightminer.inspirations.recipes.recipe.anvil.IBlockStateCondition;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.Objects;

/**
 * Condition that is true if the block hardness lies inside the interval
 */
public class HardnessBlockStateCondition implements IBlockStateCondition {
    private final float minHardness;
    private final float maxHardness;

    public HardnessBlockStateCondition(float minHardness, float maxHardness) {
        this.minHardness = minHardness;
        this.maxHardness = maxHardness;
    }

    @Override
    public boolean matches(World world, BlockPos pos, @Nonnull IBlockState state) {
        float hardness = state.getBlockHardness(world, pos);
        if (minHardness == -1 && hardness != -1) {
            return false;
        }
        return (hardness >= minHardness) && (maxHardness == -1 || hardness <= maxHardness);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HardnessBlockStateCondition that = (HardnessBlockStateCondition) o;
        return Float.compare(that.minHardness, minHardness) == 0 &&
                Float.compare(that.maxHardness, maxHardness) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(minHardness, maxHardness);
    }

    @Override
    public String getTooltip() {
        if (minHardness == -1) {
            return Util.translateFormatted("gui.jei.anvil_smashing.condition.hardness.unbreakable");
        } else if (maxHardness != -1) {
            return Util.translateFormatted("gui.jei.anvil_smashing.condition.hardness.both", minHardness, maxHardness);
        } else {
            return Util.translateFormatted("gui.jei.anvil_smashing.condition.hardness.min", minHardness);
        }
    }

    public static HardnessBlockStateCondition fromConfig(@Nonnull String config) {
        String[] hardnessParts = config.split(",");
        float minHardness = 0;
        float maxHardness = -1;
        int idx = 0;
        if (hardnessParts.length == 2 || !config.startsWith(",")) {
            minHardness = AnvilSmashingItemRecipeBuilder.parseFloat(hardnessParts[idx++]);
            if (minHardness < 0) {
                return null;
            }
        }

        if (hardnessParts.length > idx) {
            maxHardness = AnvilSmashingItemRecipeBuilder.parseFloat(hardnessParts[idx]);
            if (maxHardness < -1) {
                return null;
            }
        }

        return new HardnessBlockStateCondition(minHardness, maxHardness);
    }
}
