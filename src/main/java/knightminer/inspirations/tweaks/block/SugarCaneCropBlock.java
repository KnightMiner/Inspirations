package knightminer.inspirations.tweaks.block;

import knightminer.inspirations.tweaks.InspirationsTweaks;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.ItemLike;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.BlockGetter;
import net.minecraftforge.common.PlantType;

import java.util.function.Supplier;
import java.util.stream.IntStream;

import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

public class SugarCaneCropBlock extends BlockCropBlock {

  private static final VoxelShape[] BOUNDS = IntStream.range(1, 16).mapToObj(i -> box(2, 0, 2, 14, i, 14)).toArray(VoxelShape[]::new);
  public SugarCaneCropBlock(Supplier<Block> block, PlantType type, Properties props) {
    super(block, type, props);
  }

  public SugarCaneCropBlock(Block block, PlantType type) {
    super(block, type);
  }

  @Override
  protected ItemLike getBaseSeedId() {
    return InspirationsTweaks.sugarCaneSeeds;
  }

  @Override
  public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
    return BOUNDS[this.getAge(state)];
  }
}
