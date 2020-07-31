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
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.PlantType;

import java.util.Random;
import java.util.function.Supplier;

public abstract class BlockCropBlock extends CropsBlock implements IHidable, IPlantable {

  public static final IntegerProperty SMALL_AGE = IntegerProperty.create("age", 0, 6);

  protected Supplier<Block> block;
  protected PlantType type;

  public BlockCropBlock(Supplier<Block> block, PlantType type, Properties props) {
    super(props);
    this.block = block;
    this.type = type;
  }

  public BlockCropBlock(Block block, PlantType type) {
    this(block.delegate, type, Properties.from(block));
  }

  /* Age logic */

  @Override
  protected void fillStateContainer(StateContainer.Builder<Block,BlockState> builder) {
    builder.add(getAgeProperty());
    // No super, we want a different age size!
  }

  @Override
  public IntegerProperty getAgeProperty() {
    return SMALL_AGE;
  }

  @Override
  public BlockState withAge(int age) {
    if (age == getMaxAge()) {
      return block.get().getDefaultState();
    }
    return super.withAge(age);
  }

  @Override
  public boolean isMaxAge(BlockState state) {
    // never get to max age, our max is the block
    return false;
  }

  /* Crop logic */
  @Override
  public PlantType getPlantType(IBlockReader world, BlockPos pos) {
    return type;
  }

  @Deprecated
  @Override
  public boolean isValidPosition(BlockState state, IWorldReader world, BlockPos pos) {
    return block.get().isValidPosition(block.get().getDefaultState(), world, pos);
  }

  @Override
  public boolean canUseBonemeal(World worldIn, Random rand, BlockPos pos, BlockState state) {
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
