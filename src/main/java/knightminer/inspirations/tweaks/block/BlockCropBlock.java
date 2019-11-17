package knightminer.inspirations.tweaks.block;

import knightminer.inspirations.common.Config;
import knightminer.inspirations.common.IHidable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CropsBlock;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.Direction;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.PlantType;
import net.minecraftforge.registries.IRegistryDelegate;

import javax.annotation.Nonnull;

public abstract class BlockCropBlock extends CropsBlock implements IHidable, IPlantable {
	protected IRegistryDelegate<Block> block;
	protected PlantType type;
	protected final VoxelShape[] shape;
	public static final IntegerProperty SMALL_AGE = IntegerProperty.create("age", 0, 6);

	public BlockCropBlock(Block block, PlantType type, VoxelShape[] shape, Block.Properties props) {
		super(props);
		this.block = block.delegate;
		this.shape = shape;
		this.type = type;
	}

	@Override
	public boolean isEnabled() {
		return Config.enableMoreSeeds.get();
	}

	@Deprecated
	@Override
	public boolean isValidPosition(@Nonnull BlockState state, IWorldReader world, BlockPos pos) {
		BlockState soil = world.getBlockState(pos.down());
		return soil.canSustainPlant(world, pos, Direction.UP, this);
	}

	/* Age logic */

	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(getAgeProperty());
		// No super, we want a different age size!
	}

	@Nonnull
	@Override
	public IntegerProperty getAgeProperty() {
		return SMALL_AGE;
	}

	@Nonnull
	@Override
	public BlockState withAge(int age) {
		if(age == getMaxAge()) {
			return block.get().getDefaultState();
		}
		return super.withAge(age);
	}

	@Override
	public boolean isMaxAge(BlockState state) {
		// never get to max age, our max is the block
		return false;
	}

	@Nonnull
	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		return shape[this.getAge(state)];
	}

	@Deprecated
	@Nonnull
	@Override
	public VoxelShape getRaytraceShape(@Nonnull BlockState state, @Nonnull IBlockReader world, @Nonnull BlockPos pos) {
		return shape[this.getAge(state)];
	}

	/* Crop drops */

	@Nonnull
	@Override
	protected abstract IItemProvider getSeedsItem();

	@Override
	public PlantType getPlantType(IBlockReader world, BlockPos pos) {
		return type;
	}
}
