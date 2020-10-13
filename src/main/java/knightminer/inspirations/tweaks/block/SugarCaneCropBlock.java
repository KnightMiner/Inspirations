package knightminer.inspirations.tweaks.block;

import knightminer.inspirations.tweaks.InspirationsTweaks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.common.PlantType;

import java.util.function.Supplier;
import java.util.stream.IntStream;

public class SugarCaneCropBlock extends BlockCropBlock {

  private static final VoxelShape[] BOUNDS = IntStream.range(1, 16).mapToObj(i -> makeCuboidShape(2, 0, 2, 14, i, 14)).toArray(VoxelShape[]::new);
  public SugarCaneCropBlock(Supplier<Block> block, PlantType type, Properties props) {
    super(block, type, props);
  }

  public SugarCaneCropBlock(Block block, PlantType type) {
    super(block, type);
  }

  @Override
  protected IItemProvider getSeedsItem() {
    return InspirationsTweaks.sugarCaneSeeds;
  }

  @Override
  public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
    return BOUNDS[this.getAge(state)];
  }
}
