package knightminer.inspirations.recipes.recipe.anvil;

import knightminer.inspirations.recipes.block.BlockSmashingAnvil;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Objects;

/**
 * Recipe for anvil smashing.
 */
public class AnvilSmashingItemRecipe {
  private final ItemStack stackInput;

  private final IBlockStateCondition blockInput;

  private final ItemStack stackOutput;

  private final IBlockState blockOutput;

  private final boolean alwaysTransformBlock;

  private final int minFallHeight;

  private final int maxFallHeight;


  public AnvilSmashingItemRecipe(ItemStack stackInput,
                                 IBlockStateCondition blockInput, ItemStack stackOutput,
                                 IBlockState blockOutput,
                                 boolean alwaysTransformBlock, int minFallHeight, int maxFallHeight) {
    this.stackInput = stackInput;
    this.blockInput = blockInput;
    this.stackOutput = stackOutput;
    this.blockOutput = blockOutput;
    this.alwaysTransformBlock = alwaysTransformBlock;
    this.minFallHeight = minFallHeight;
    this.maxFallHeight = maxFallHeight;
  }

  public ItemStack getStackInput() {
    return stackInput;
  }

  public IBlockStateCondition getBlockInput() {
    return blockInput;
  }

  public ItemStack getStackOutput() {
    return stackOutput;
  }

  public IBlockState getBlockOutput() {
    return blockOutput;
  }

  public boolean isAlwaysTransformBlock() {
    return alwaysTransformBlock;
  }

  public int getMinFallHeight() {
    return minFallHeight;
  }

  public int getMaxFallHeight() {
    return maxFallHeight;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    AnvilSmashingItemRecipe that = (AnvilSmashingItemRecipe) o;
    return isAlwaysTransformBlock() == that.isAlwaysTransformBlock() &&
        getMinFallHeight() == that.getMinFallHeight() &&
        getMaxFallHeight() == that.getMaxFallHeight() &&
        Objects.equals(getStackInput(), that.getStackInput()) &&
        Objects.equals(getBlockInput(), that.getBlockInput()) &&
        Objects.equals(getStackOutput(), that.getStackOutput()) &&
        Objects.equals(getBlockOutput(), that.getBlockOutput());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getStackInput(), getBlockInput(), getStackOutput(), getBlockOutput(),
        isAlwaysTransformBlock(),
        getMinFallHeight(), getMaxFallHeight());
  }

  /**
   * Apply the recipe to the given item stack. Also transform the block the anvil landed on.
   *
   * @param world      the world this is happening in
   * @param entityItem the items as entities
   * @param stack      the items as a stack
   * @param posAnvil   the position of the anvil
   * @param posDown    the position of the block the anvil landed on
   * @param stateDown  the block state the anvil landed on
   * @param fallHeight the height from which the anvil dropped
   * @return whether the recipe was applied
   */
  public boolean apply(World world, EntityItem entityItem, ItemStack stack, BlockPos posAnvil, BlockPos posDown,
                       IBlockState stateDown, int fallHeight) {
    // check if all requirements are met
    if (!matchesStack(stack) || !matchesFallHeight(fallHeight) || !matches(world, posDown, stateDown)) {
      return false;
    }

    // all requirements are met, so do the transformation of the item stacks
    transformInputStack(entityItem, stack);

    // transform the item stacks and spawn them in the world
    boolean didItemTransform = transformOutputStack(world, stack, posAnvil);

    // affect the block the anvil landed on
    transformBlock(world, posDown, stateDown, didItemTransform);

    // recipe was applied
    return true;
  }

  /**
   * Does the input item stack match the input condition of the recipe
   *
   * @param stack the input item stack
   * @return true if the item stack matches the condition for the input
   */
  private boolean matchesStack(ItemStack stack) {
    return getInputStackCount() <= stack.getCount() && ItemStack.areItemStackTagsEqual(getStackInput(), stack);
  }

  /**
   * Perform the transformation of the block the anvil landed on.
   *
   * @param world            the world this is happening in
   * @param posDown          the position of the block the anvil landed on
   * @param stateDown        the stateExpected of the block the anvil landed on and that needs to be changed
   * @param didItemTransform whether an item stack was transformed
   */
  private void transformBlock(World world, BlockPos posDown, IBlockState stateDown, boolean didItemTransform) {
    if (tryLandingBlockTransformation(didItemTransform)) {
      // Null means there is no transformation
      IBlockState stateResult = this.getBlockOutput();
      if (stateResult != null) {
        // if the block is unbreakable, leave it
        if (canBreakBlock(world, posDown, stateResult)) {
          BlockSmashingAnvil.performSmashBlock(world, posDown, stateDown, stateResult);
        }
      }
    }
  }

  /**
   * Test the block at the given position if it can be broken.
   *
   * @param world   the world where the blocks exists in
   * @param posDown the position of the block
   * @param state   the stateExpected of the block
   * @return true if the block can be broken
   */
  public static boolean canBreakBlock(World world, BlockPos posDown, IBlockState state) {
    return state.getBlockHardness(world, posDown) != -1;
  }

  /**
   * Either an item is transformed or the recipe always causes the block transformation.
   *
   * @param didItemTransform whether there was an item stack transformation
   * @return true if the block transformation should be attempted
   */
  private boolean tryLandingBlockTransformation(boolean didItemTransform) {
    return didItemTransform || this.isAlwaysTransformBlock();
  }

  /**
   * Transform the input item stack according to this recipe.
   *
   * @param world    the world this is happening in
   * @param stack    the input stack that gets transformed
   * @param posAnvil the position of the anvil
   * @return whether the item stack could be transformed
   */
  private boolean transformOutputStack(World world, ItemStack stack, BlockPos posAnvil) {
    ItemStack output = this.getStackOutput();
    if (output != null && !output.isEmpty()) {
      int totalCount = getResultItemStackTotalCount(stack, output);
      if (totalCount > 0) {
        // Spawn the items on top of the anvil
        BlockPos posToSpawn = posAnvil.up();
        return spawnResultItemStacks(world, output, totalCount, posToSpawn);
      }
    }
    return false;
  }

  /**
   * Spawn the result item stacks in the world
   *
   * @param world      the world the items should be spawned into
   * @param output     the output item stack template
   * @param totalCount the total amount of items that are the result of the recipe
   * @param posToSpawn the position where the result items should be spawned
   * @return whether one or more items was transformed
   */
  private boolean spawnResultItemStacks(World world, ItemStack output, int totalCount,
                                        BlockPos posToSpawn) {
    // spawn stacks with the maximum stack size for the item
    int maxStackSize = output.getMaxStackSize();

    int remainingStackSize = totalCount;
    boolean didItemTransform = false;
    while (remainingStackSize > 0) {
      remainingStackSize = spawnResultItemStack(world, output, remainingStackSize, posToSpawn, maxStackSize);
      didItemTransform = true;
    }
    return didItemTransform;
  }

  /**
   * Calculate the total amount of items that are the result for the given item stack
   *
   * @param stack  the input item stack
   * @param output the output item stack from the recipe
   * @return the number of total items that are the result of the recipe
   */
  private int getResultItemStackTotalCount(ItemStack stack, ItemStack output) {
    return (stack.getCount() / this.getInputStackCount()) * output.getCount();
  }

  /**
   * Does spawn one item stack in the world. The stacks are limited to the max stack size for the item or the number
   * of remaining items. Returns the remainder of the items.
   *
   * @param world           the world the items should spawn in
   * @param output          the output item stack that is used to copy the result item stacks from
   * @param outputStackSize the remaining total stack size
   * @param posToSpawn      the position the entities should be spawned at
   * @param maxStackSize    the maximum allowed stack size of the item stacks
   * @return the number of items that remain from the input total
   */
  private int spawnResultItemStack(World world, ItemStack output, int outputStackSize, BlockPos posToSpawn,
                                   int maxStackSize) {
    // Copy everything from the recipe
    ItemStack stackResult = output.copy();

    // apply the item count according to the maximum item stack
    int newStackSize = Math.min(maxStackSize, outputStackSize);
    stackResult.setCount(newStackSize);

    // Spawn the item stack in the world
    Block.spawnAsEntity(world, posToSpawn, stackResult);

    // calculate the remaining item count
    return outputStackSize - newStackSize;
  }

  /**
   * Transform the input item stack and entity.
   *
   * @param entityItem the entity for the input item stack
   * @param stack      the input item stack
   */
  private void transformInputStack(EntityItem entityItem, ItemStack stack) {
    int remainder = stack.getCount() % getInputStackCount();
    if (remainder > 0) {
      // reduce stack size
      stack.setCount(remainder);
    } else {
      // all items are "processed", so kill the entire stack
      entityItem.setDead();
    }
  }

  /**
   * Get the stack size for the recipe input
   *
   * @return the stack size for the input stack
   */
  private int getInputStackCount() {
    return getStackInput().getCount();
  }

  /**
   * Check if the fall height matches the conditions.
   *
   * @param fallHeight the fall height of the anvil
   * @return true if the fall height matches
   */
  private boolean matchesFallHeight(int fallHeight) {
    return fallHeight >= minFallHeight && fallHeight <= maxFallHeight;
  }

  /**
   * Check the conditions for the input block state.
   *
   * @param world      the world this happens in
   * @param pos        the position of the block in the world
   * @param blockInput the block state
   * @return true if the condition is met
   */
  private boolean matches(World world, BlockPos pos, IBlockState blockInput) {
    return this.blockInput.matches(world, pos, blockInput);
  }
}
