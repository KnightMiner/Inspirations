package knightminer.inspirations.tweaks.block;

import knightminer.inspirations.common.Config;
import knightminer.inspirations.common.IHidable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CropsBlock;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.PlantType;

import java.util.Random;
import java.util.function.Supplier;

import net.minecraft.block.AbstractBlock.Properties;

@SuppressWarnings("WeakerAccess")
public abstract class BlockCropBlock extends CropsBlock implements IHidable, IPlantable {
  public static final IntegerProperty LARGE_AGE = IntegerProperty.create("age", 0, 14);

  protected Supplier<Block> block;
  protected PlantType type;
  protected BlockCropBlock(Supplier<Block> block, PlantType type, Properties props) {
    super(props);
    this.block = block;
    this.type = type;
  }

  protected BlockCropBlock(Block block, PlantType type) {
    this(block.delegate, type, Properties.copy(block));
  }

  /* Age logic */

  @Override
  protected void createBlockStateDefinition(StateContainer.Builder<Block,BlockState> builder) {
    builder.add(getAgeProperty());
    // No super, we want a different age size!
  }

  @Override
  public IntegerProperty getAgeProperty() {
    return LARGE_AGE;
  }

  @Override
  public int getMaxAge() {
    return 15;
  }

  @Override
  public BlockState getStateForAge(int age) {
    if (age == getMaxAge()) {
      return block.get().defaultBlockState();
    }
    return super.getStateForAge(age);
  }

  @Override
  public boolean isMaxAge(BlockState state) {
    // never get to max age, our max is the block
    return false;
  }

  @Override
  public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
    // Forge: prevent loading unloaded chunks when checking neighbor's light
    if (!world.isAreaLoaded(pos, 1)) return;
    // age will always be less than max, but safe to check
    int age = this.getAge(state);
    int max = this.getMaxAge();
    if (age < max) {
      if (ForgeHooks.onCropsGrowPre(world, pos, state, true)) {
        age++;
        BlockState newState = this.getStateForAge(age);
        // update again if max age, for the sake of cactus placement
        if (age == max) {
          world.setBlock(pos, newState, 3);
          if (!newState.canSurvive(world, pos)) {
            world.getBlockTicks().scheduleTick(pos, block.get(), 1);
          }
        } else {
          world.setBlock(pos, newState, 2);
        }
        ForgeHooks.onCropsGrowPost(world, pos, state);
      }
    }
  }

  /* Crop logic */
  @Override
  public PlantType getPlantType(IBlockReader world, BlockPos pos) {
    return type;
  }

  @Deprecated
  @Override
  public boolean canSurvive(BlockState state, IWorldReader world, BlockPos pos) {
    return block.get().canSurvive(block.get().defaultBlockState(), world, pos);
  }


  /* Bonemeal */

  @Override
  public boolean isValidBonemealTarget(IBlockReader worldIn, BlockPos pos, BlockState state, boolean isClient) {
    return Config.bonemealBlockCrop.get();
  }

  @Override
  public boolean isBonemealSuccess(World worldIn, Random rand, BlockPos pos, BlockState state) {
    return Config.bonemealBlockCrop.get();
  }


  /**
   * Gets an IPlantable for this plant
   * @return The base block's plantable, or this if the base block is not plantable
   */
  protected IPlantable getPlant() {
    Block block = this.block.get();
    if (block instanceof IPlantable) {
      return (IPlantable)block;
    }
    return this;
  }

  /* Hidable */
  @Override
  public boolean isEnabled() {
    return Config.enableBlockCrops.get();
  }
}
