package knightminer.inspirations.recipes.block;

import com.google.common.collect.Iterables;
import knightminer.inspirations.library.InspirationsRegistry;
import knightminer.inspirations.recipes.recipe.anvil.AnvilSmashingItemRecipe;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAnvil;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Collection;
import java.util.List;

public class BlockSmashingAnvil extends BlockAnvil {

	public BlockSmashingAnvil() {
		this.setHardness(5.0F);
		this.setSoundType(SoundType.ANVIL);
		this.setResistance(2000.0F);
		this.setUnlocalizedName("anvil");
	}

	@Override
	public void onEndFalling(World world, BlockPos pos, IBlockState anvil, IBlockState state) {
		BlockPos down = pos.down();
		IBlockState stateDown = world.getBlockState(down);

		int fallHeight = getFallHeight(world, pos);

		// try smashing any items before the normal block smashing
		if (
				!smashItems(world, pos, down, stateDown, fallHeight) &&
						!smashBlock(world, down, stateDown)) {
			super.onEndFalling(world, pos, anvil, state);
		}
	}

  /**
   * Since the fall height is not given to the onEndFalling method we have to try and find it for ourselves.
   *
   * @param world the world
   * @param pos   the position the anvil ended it's fall in
   * @return the height the anvil fell from or 0 if no height could be determined
   */
  private int getFallHeight(World world, BlockPos pos) {
    // All falling block entities that are anvils in the given block space
    List<EntityFallingBlock> entities = world
        .getEntitiesWithinAABB(EntityFallingBlock.class, new AxisAlignedBB(pos),
            this::isAnvil);

    // since no other information is available, we just pick the first entity
    EntityFallingBlock entity = Iterables.getFirst(entities, null);
    if (entity == null) {
      return 0;
    }

    // the origin might have a lower y than the final position, so at the moment use a lower bound
    // of 0 to limit the fall height to a positive value
    return Math.max(0, entity.getOrigin().getY() - pos.getY());
  }

  /**
   * Check if the entity is a falling anvil.
   *
   * @param input the entity to check
   * @return true if the entity is a falling anvil
   */
  private boolean isAnvil(EntityFallingBlock input) {
    IBlockState state = input != null ? input.getBlock() : null;
    return state != null && state.getBlock() == Blocks.ANVIL;
  }

  /**
   * Retrieve the first item entity in the world at the given position.
   *
   * @param world the world to look in
   * @param pos   the position to look in
   * @return the first item entity. Standard rules of entity access for minecraft applies, so no
   * guarantees what entity will be picked!
   */
  private static EntityItem getItemEntity(World world, BlockPos pos) {
    List<EntityItem> entitiesWithinAABB = world
        .getEntitiesWithinAABB(EntityItem.class, new AxisAlignedBB(pos));
    return Iterables.getFirst(entitiesWithinAABB, null);
  }

  /**
   * Try and smash an item in the world.
   *
   * @param world      the world
   * @param posAnvil   the position the anvil landed in
   * @param posDown    the position of the block the anvil landed on
   * @param stateDown  the state of the block the anvil landed on
   * @param fallHeight the height of the fall for the anvil
   * @return true if items where smashed by the anvil
   */
  public static boolean smashItems(World world, BlockPos posAnvil, BlockPos posDown,
      IBlockState stateDown, int fallHeight) {
    if (stateDown.getBlock() == Blocks.AIR) {
      return true;
    }

    // Select the first item entity that is colliding with the anvil
    EntityItem entityItem = getItemEntity(world, posAnvil);
    if (entityItem == null) {
      return false;
    }
    ItemStack stack = entityItem.getItem();

    // Check the registry for a fitting recipe
    Collection<AnvilSmashingItemRecipe> recipes = InspirationsRegistry
        .getAnvilSmashItemRecipe(stack);
    if (recipes == null || recipes.isEmpty()) {
      return false;
    }

    return recipes.stream().anyMatch(
        recipe -> recipe.apply(world, entityItem, stack, posAnvil, posDown, stateDown, fallHeight));
  }

  /**
   * Standard logic for smashing blocks.
   *
   * @param world the world
   * @param pos   the position the anvil landed on
   * @param state the state of the block the anvil landed on
   * @return true if smashing happened
   */
	public static boolean smashBlock(World world, BlockPos pos, IBlockState state) {
		// if we started on air, just return true
		if(state.getBlock() == Blocks.AIR) {
			return true;
		}
		// if the block is unbreakable, leave it
		if(state.getBlockHardness(world, pos) == -1) {
			return false;
		}

		IBlockState transformation = InspirationsRegistry.getAnvilSmashResult(state);
		if(transformation == null) {
			return false;
		}
		performSmashBlock(world, pos, state, transformation);
		return true;
	}

  /**
   * Executes the smashing of a block in the world.
   *
   * @param world  the world
   * @param pos    the position the block smash should be applied to
   * @param state  the block state of the block the anvil landed on
   * @param transformation the block state that is the result of the transformation
   */
  public static void performSmashBlock(World world, BlockPos pos, IBlockState state,
      IBlockState transformation) {
		// if the result is air, break the block
		if(transformation.getBlock() == Blocks.AIR) {
			world.destroyBlock(pos, true);
		} else {
			// breaking particles
			world.playEvent(2001, pos, Block.getStateId(state));
			world.setBlockState(pos, transformation);
		}
	}
}
