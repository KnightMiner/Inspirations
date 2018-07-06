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
 * Condition that is true if the block harvest level lies inside the interval
 */
public class HarvestLevelBlockStateCondition implements IBlockStateCondition {
    private final int minLevel;
    private final int maxLevel;

    public HarvestLevelBlockStateCondition(int minLevel, int maxLevel) {
        this.minLevel = minLevel;
        this.maxLevel = maxLevel;
    }

    @Override
    public boolean matches(World world, BlockPos pos, @Nonnull IBlockState state) {
        int level = state.getBlock().getHarvestLevel(state);
        return (minLevel < 0 || level >= minLevel) && (maxLevel < 0 || level <= maxLevel);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HarvestLevelBlockStateCondition that = (HarvestLevelBlockStateCondition) o;
        return minLevel == that.minLevel &&
                maxLevel == that.maxLevel;
    }

    @Override
    public int hashCode() {
        return Objects.hash(minLevel, maxLevel);
    }

    @Override
    public String getTooltip() {
        if (minLevel >= 0 && maxLevel < Integer.MAX_VALUE && maxLevel > 0) {
            return Util.translateFormatted("gui.jei.anvil_smashing.condition.harvestlevel.both",
                    getHarvestLevelName(minLevel), getHarvestLevelName(maxLevel));
        } else if (minLevel >= 0) {
            return Util.translateFormatted("gui.jei.anvil_smashing.condition.harvestlevel.min",
                    getHarvestLevelName(minLevel));
        } else if (maxLevel < Integer.MAX_VALUE && maxLevel > 0) {
            return Util.translateFormatted("gui.jei.anvil_smashing.condition.harvestlevel.max",
                    getHarvestLevelName(maxLevel));
        }
        return null;
    }

    private String getHarvestLevelName(int level) {
        String levelName = Util.translate("gui.jei.anvil_smashing.condition.harvestlevel." + level);
        if (levelName.isEmpty()) {
            levelName = "" + level;
        }
        return levelName;
    }

    public static HarvestLevelBlockStateCondition fromConfig(String config) {
        String[] levelParts = config.split(",");
        int minLevel = 0;
        int maxLevel = Integer.MAX_VALUE;
        int idx = 0;
        if (levelParts.length == 2 || !config.startsWith(",")) {
            minLevel = AnvilSmashingItemRecipeBuilder.parseInteger(levelParts[idx++]);
            if (minLevel < 0) {
                return null;
            }
        }

        if (levelParts.length > idx) {
            maxLevel = AnvilSmashingItemRecipeBuilder.parseInteger(levelParts[idx]);
            if (maxLevel < -1) {
                return null;
            }
        }

        return new HarvestLevelBlockStateCondition(minLevel, maxLevel);
    }
}
