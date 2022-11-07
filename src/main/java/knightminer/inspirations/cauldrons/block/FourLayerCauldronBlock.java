package knightminer.inspirations.cauldrons.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractCauldronBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

import java.util.Map;

/** Cauldron with four levels */
public class FourLayerCauldronBlock extends AbstractCauldronBlock {
	public static final int MAX_FILL_LEVEL = 4;
	public static final IntegerProperty LEVEL = IntegerProperty.create("level", 1, MAX_FILL_LEVEL);

	public FourLayerCauldronBlock(Properties pProperties, Map<Item,CauldronInteraction> pInteractions) {
		super(pProperties, pInteractions);
		this.registerDefaultState(this.stateDefinition.any().setValue(LEVEL, 1));
	}

	@Override
	public boolean isFull(BlockState state) {
		return state.getValue(LEVEL) == MAX_FILL_LEVEL;
	}

	@Override
	protected double getContentHeight(BlockState state) {
		return (5.0D + state.getValue(LEVEL) * 2.5D) / 16.0D;
	}

	@Override
	public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
		return state.getValue(LEVEL);
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(LEVEL);
	}
}
