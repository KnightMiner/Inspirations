package knightminer.inspirations.tweaks.block;

import knightminer.inspirations.common.Config;
import knightminer.inspirations.tweaks.InspirationsTweaks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.common.PlantType;

import java.util.function.Supplier;
import java.util.stream.IntStream;

public class CactusCropBlock extends BlockCropBlock {

  private static final VoxelShape[] BOUNDS = IntStream.range(1, 16).mapToObj(i -> box(1, 0, 1, 15, i, 15)).toArray(VoxelShape[]::new);
  public CactusCropBlock(Block base, PlantType plant) {
    super(base, plant);
  }

  public CactusCropBlock(Supplier<Block> base, PlantType plant, Block.Properties properties) {
    super(base, plant, properties);
  }

  @Override
  protected IItemProvider getBaseSeedId() {
    return InspirationsTweaks.cactusSeeds;
  }

  @Override
  public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
    return BOUNDS[this.getAge(state)];
  }

  /* spiky! */
  @Override
  public void entityInside(BlockState state, World worldIn, BlockPos pos, Entity entity) {
    entity.hurt(DamageSource.CACTUS, 1.0F);
  }

  @SuppressWarnings("deprecation")
  @Deprecated
  @Override
  public boolean canSurvive(BlockState state, IWorldReader world, BlockPos pos) {
    // if true, vanilla cactus farms will now produce cactus seeds rather than full blocks
    if (Config.nerfCactusFarms.get()) {
      return super.canSurvive(state, world, pos);
    }

    // if not above cactus, also use base block logic
    // prevents planting seeds in spots where they will break on growth
    BlockPos down = pos.below();
    BlockState soil = world.getBlockState(down);
    if (soil.getBlock() != Blocks.CACTUS) {
      return super.canSurvive(state, world, pos);
    }

    // otherwise, do cactus logic, but without the horizontal checks
    return soil.canSustainPlant(world, down, Direction.UP, getPlant()) && !world.getBlockState(pos.above()).getMaterial().isLiquid();
  }
}
