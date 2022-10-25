package knightminer.inspirations.tweaks.block;

import knightminer.inspirations.common.Config;
import knightminer.inspirations.tweaks.InspirationsTweaks;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.core.Direction;
import net.minecraft.world.level.ItemLike;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.Level;
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
  protected ItemLike getBaseSeedId() {
    return InspirationsTweaks.cactusSeeds;
  }

  @Override
  public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
    return BOUNDS[this.getAge(state)];
  }

  /* spiky! */
  @Override
  public void entityInside(BlockState state, Level worldIn, BlockPos pos, Entity entity) {
    entity.hurt(DamageSource.CACTUS, 1.0F);
  }

  @SuppressWarnings("deprecation")
  @Deprecated
  @Override
  public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
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
