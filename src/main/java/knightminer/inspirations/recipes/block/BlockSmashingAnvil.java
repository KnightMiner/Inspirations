package knightminer.inspirations.recipes.block;

import com.google.common.collect.Iterables;
import knightminer.inspirations.library.InspirationsRegistry;
import knightminer.inspirations.library.recipe.anvil.IAnvilRecipe;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAnvil;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class BlockSmashingAnvil extends BlockAnvil {

	private static final int MAX_RECIPE_APPLICATIONS = 64;

	public BlockSmashingAnvil() {
		this.setHardness(5.0F);
		this.setSoundType(SoundType.ANVIL);
		this.setResistance(2000.0F);
		this.setUnlocalizedName("anvil");
	}

	@Override
	public void onEndFalling(World world, BlockPos pos, IBlockState anvil, IBlockState state) {
		BlockPos down = pos.down();
		IBlockState blockState = world.getBlockState(down);
		// smash the items and then the block and do both in combination
		if(!smashItem(world, down.up(), blockState) & !smashBlock(world, down, blockState)) {
			super.onEndFalling(world, pos, anvil, state);
		}
	}

	/**
	 * Smash an item stack in the world with an anvil
	 * @param world the world
	 * @param pos the position the anvil landed on
	 * @param state the state of the block the anvil landed on
	 * @return true if the item was smashed
	 */
	public static boolean smashItem(World world, BlockPos pos, IBlockState state) {
		// find item entities that can be smashed
		List<EntityItem> entityItem = getItemEntity(world, pos);
		if(entityItem.isEmpty()) {
			return false;
		}

		NonNullList<ItemStack> inputs = NonNullList.create();
		inputs.addAll(entityItem.stream().map(EntityItem::getItem).collect(Collectors.toList()));

		int fallHeight = getFallHeight(world, pos);

		// repeat as long as a recipe matches
		List<ItemStack> results = new ArrayList<>();
		boolean recipeApplied = false;
		int iterations = 0;
		while(iterations++ < MAX_RECIPE_APPLICATIONS) {
			// find next match
			IAnvilRecipe recipe = InspirationsRegistry.getAnvilItemSmashingRecipe(inputs, fallHeight, state);
			if(recipe == null) {
				// no more match
				break;
			}

			// at least one recipe was applied
			recipeApplied = true;

			// apply the recipe once
			List<ItemStack> itemStackRemaining = recipe.transformInput(inputs, fallHeight, state);
			results.addAll(itemStackRemaining);
		}

		// Output the result stacks
		results.forEach(itemStack -> {
			if(!itemStack.isEmpty()) {
				spawnAsEntity(world, pos, itemStack);
			}
		});

		// Remove all empty input stacks
		entityItem.forEach(entity -> {
			if(entity.getItem().isEmpty()) {
				entity.setDead();
			}
		});
		return recipeApplied;
	}

	/**
	 * Find the fall height of the anvil
	 * @param world the world
	 * @param pos the position of the anvil
	 * @return the fall height
	 */
	private static int getFallHeight(World world, BlockPos pos) {
		// since there is no direct access to the entity use the first one in the given position
		EntityFallingBlock entity = Iterables.getFirst(world
				.getEntitiesWithinAABB(EntityFallingBlock.class, new AxisAlignedBB(pos),
						BlockSmashingAnvil::isAnvilEntity), null);
		return entity != null ? getYDifference(entity.getOrigin(), pos) : 0;
	}

	/**
	 * Check if the falling block entity is an anvil
	 * @param input the entity
	 * @return true if the entity is a falling anvil
	 */
	private static boolean isAnvilEntity(EntityFallingBlock input) {
		IBlockState state = input.getBlock();
		return state != null && state.getBlock() == Blocks.ANVIL;
	}

	/**
	 * Find the difference in y position between the two positions
	 * @param high the higher position
	 * @param low the lower position
	 * @return the Y level difference or 0 if the upper position is lower
	 */
	private static int getYDifference(BlockPos high, BlockPos low) {
		int highY = high.getY();
		int lowY = low.getY();
		return highY < lowY ? 0 : highY - lowY;
	}

	/**
	 * Retrieve the first item entity in the world at the given position.
	 *
	 * @param world the world to look in
	 * @param pos   the position to look in
	 * @return all item entities
	 */
	private static List<EntityItem> getItemEntity(World world, BlockPos pos) {
		return world.getEntitiesWithinAABB(EntityItem.class, new AxisAlignedBB(pos));
	}

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

		// if the result is air, break the block
		if(transformation.getBlock() == Blocks.AIR) {
			world.destroyBlock(pos, true);
		} else {
			// breaking particles
			world.playEvent(2001, pos, Block.getStateId(state));
			world.setBlockState(pos, transformation);
		}
		return true;
	}
}
