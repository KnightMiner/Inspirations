package knightminer.inspirations.cauldrons.block;

import knightminer.inspirations.cauldrons.InspirationsCaudrons;
import knightminer.inspirations.cauldrons.block.entity.SuspiciousStewCauldronBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

/** Cauldron that contains suspicious stew effects */
public class SuspiciousStewCauldronBlock extends BoilingFourLayerCauldronBlock implements EntityBlock {
	public SuspiciousStewCauldronBlock(Properties props) {
		super(props, InspirationsCaudrons.SUSPICIOUS_STEW_CAULDRON_INTERACTIONS);
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new SuspiciousStewCauldronBlockEntity(pos, state);
	}
}
