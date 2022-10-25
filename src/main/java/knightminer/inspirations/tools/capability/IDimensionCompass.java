package knightminer.inspirations.tools.capability;

import net.minecraft.core.BlockPos;

import javax.annotation.Nullable;

/**
 * Data holder for dimensional position
 */
public interface IDimensionCompass {
	/**
	 * Gets the position the entity entered the dimension
	 * @return  Position
	 */
	@Nullable
	BlockPos getEnteredPosition();

	/**
	 * Sets the position the entity entered the dimension
	 * @param pos  Entered position
	 */
	void setEnteredPosition(@Nullable BlockPos pos);
}
