package knightminer.inspirations.cauldrons.block;

import knightminer.inspirations.cauldrons.InspirationsCaudrons;
import knightminer.inspirations.cauldrons.block.entity.DyeCauldronBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

/** Cauldron that contains a data instance for the color */
public class DyeCauldronBlock extends LayeredCauldronBlock implements EntityBlock {
	public DyeCauldronBlock(Properties props) {
		super(props, precipitation -> false, InspirationsCaudrons.DYE_CAULDRON_INTERACTIONS);
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new DyeCauldronBlockEntity(pos, state);
	}
}
