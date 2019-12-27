package knightminer.inspirations.tweaks.block;

import knightminer.inspirations.tweaks.InspirationsTweaks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HopperBlock;
import net.minecraft.block.IWaterLoggable;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.IFluidState;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.tileentity.HopperTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Direction.Axis;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

// Hopper block with waterlogged=False.
public class DryHopperBlock extends HopperBlock implements IWaterLoggable {
	public DryHopperBlock(Properties props) {
		super(props);
	}

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		Direction side = context.getFace().getOpposite();
		Block block = context.getWorld().getFluidState(context.getPos()).getFluid() == Fluids.WATER
				? InspirationsTweaks.wetHopper : InspirationsTweaks.dryHopper;
		return block.getDefaultState()
		            .with(FACING, side.getAxis() == Axis.Y ? Direction.DOWN : side)
		            .with(ENABLED, true);
	}

	@Override
	public void onReplaced(BlockState state1, @Nonnull World world, @Nonnull BlockPos pos, BlockState state2, boolean moving) {
		if (state2.getBlock() != state1.getBlock() &&
		    state2.getBlock() != InspirationsTweaks.dryHopper &&
		    state2.getBlock() != InspirationsTweaks.wetHopper
		) {
			TileEntity te = world.getTileEntity(pos);
			if (te instanceof HopperTileEntity) {
				InventoryHelper.dropInventoryItems(world, pos, (HopperTileEntity)te);
				world.updateComparatorOutputLevel(pos, this);
			}

			super.onReplaced(state1, world, pos, state2, moving);
		}
	}

	// Duplicate IWaterLoggable's code, but don't use the property.
	@Override
	public boolean canContainFluid(IBlockReader world, BlockPos pos, BlockState state, Fluid fluid) {
		return fluid == Fluids.WATER;
	}

	@Override
	public boolean receiveFluid(@Nonnull IWorld world, @Nonnull BlockPos pos, BlockState state, @Nonnull IFluidState fluid) {
		if (fluid.getFluid() == Fluids.WATER) {
			if (!world.isRemote()) {
				// Swap the block but don't alter the properties itself.
				world.setBlockState(pos, InspirationsTweaks.wetHopper.getDefaultState()
						.with(FACING, state.get(FACING))
						.with(ENABLED, state.get(ENABLED))
				, 3);
				world.getPendingFluidTicks().scheduleTick(pos, fluid.getFluid(), fluid.getFluid().getTickRate(world));
			}
			return true;
		} else {
			return false;
		}
	}

	@Nonnull
	@Override
	public Fluid pickupFluid(@Nonnull IWorld world, @Nonnull BlockPos pos, BlockState state) {
		return Fluids.EMPTY;
	}
}
