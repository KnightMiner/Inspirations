package knightminer.inspirations.tweaks.block;

import knightminer.inspirations.tweaks.InspirationsTweaks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HopperBlock;
import net.minecraft.block.IWaterLoggable;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
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

// Hopper block with waterlogged=False.
import net.minecraft.block.AbstractBlock.Properties;

public class DryHopperBlock extends HopperBlock implements IWaterLoggable {
  public DryHopperBlock(Properties props) {
    super(props);
  }

  @Override
  public BlockState getStateForPlacement(BlockItemUseContext context) {
    Direction side = context.getClickedFace().getOpposite();
    Block block = context.getLevel().getFluidState(context.getClickedPos()).getType() == Fluids.WATER
                  ? InspirationsTweaks.wetHopper : InspirationsTweaks.dryHopper;
    return block.defaultBlockState()
                .setValue(FACING, side.getAxis() == Axis.Y ? Direction.DOWN : side)
                .setValue(ENABLED, true);
  }

  @Override
  public void onRemove(BlockState state1, World world, BlockPos pos, BlockState state2, boolean moving) {
    if (state2.getBlock() != state1.getBlock() &&
        state2.getBlock() != InspirationsTweaks.dryHopper &&
        state2.getBlock() != InspirationsTweaks.wetHopper
    ) {
      TileEntity te = world.getBlockEntity(pos);
      if (te instanceof HopperTileEntity) {
        InventoryHelper.dropContents(world, pos, (HopperTileEntity)te);
        world.updateNeighbourForOutputSignal(pos, this);
      }

      super.onRemove(state1, world, pos, state2, moving);
    }
  }

  // Duplicate IWaterLoggable's code, but don't use the property.
  @Override
  public boolean canPlaceLiquid(IBlockReader world, BlockPos pos, BlockState state, Fluid fluid) {
    return fluid == Fluids.WATER;
  }

  @Override
  public boolean placeLiquid(IWorld world, BlockPos pos, BlockState state, FluidState fluid) {
    if (fluid.getType() == Fluids.WATER) {
      if (!world.isClientSide()) {
        // Swap the block but don't alter the properties itself.
        world.setBlock(pos, InspirationsTweaks.wetHopper.defaultBlockState()
                                                             .setValue(FACING, state.getValue(FACING))
                                                             .setValue(ENABLED, state.getValue(ENABLED))
            , 3);
        world.getLiquidTicks().scheduleTick(pos, fluid.getType(), fluid.getType().getTickDelay(world));
      }
      return true;
    } else {
      return false;
    }
  }

  @Override
  public Fluid takeLiquid(IWorld world, BlockPos pos, BlockState state) {
    return Fluids.EMPTY;
  }
}
