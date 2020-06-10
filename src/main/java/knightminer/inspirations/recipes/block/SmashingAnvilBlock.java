package knightminer.inspirations.recipes.block;

import knightminer.inspirations.library.InspirationsRegistry;
import knightminer.inspirations.library.recipe.RecipeSerializers;
import knightminer.inspirations.library.recipe.RecipeTypes;
import knightminer.inspirations.library.recipe.anvil.AnvilInventory;
import knightminer.inspirations.library.recipe.anvil.AnvilRecipe;
import knightminer.inspirations.recipes.InspirationsRecipes;
import net.minecraft.block.AnvilBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.item.FallingBlockEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;
import java.util.stream.Collectors;

public class SmashingAnvilBlock extends AnvilBlock {

  public SmashingAnvilBlock(Block.Properties props) {
    super(props);
  }

  // Replace this to handle our different blocks.
  @Nullable
  public static BlockState damage(BlockState state) {
    Block block = state.getBlock();
    if (block == Blocks.ANVIL || block == InspirationsRecipes.fullAnvil) {
      return InspirationsRecipes.chippedAnvil.getDefaultState().with(FACING, state.get(FACING));
    } else {
      if (block == Blocks.CHIPPED_ANVIL || block == InspirationsRecipes.chippedAnvil)
        return InspirationsRecipes.damagedAnvil.getDefaultState().with(FACING, state.get(FACING));
      else return null;
    }
  }

  @Override
  public void onEndFalling(World world, BlockPos pos, BlockState anvil, BlockState target, FallingBlockEntity entity) {
    BlockPos down = pos.down();
    if (!smashBlock(world, down, world.getBlockState(down))) {
      super.onEndFalling(world, pos, anvil, target, entity);
    }
  }

  /**
   * Base logic to smash a block
   * @param world World instance
   * @param pos   Position target
   * @param state State being smashed
   * @return True if somethign was smashed
   */
  @SuppressWarnings("WeakerAccess")
  public static boolean smashBlock(World world, BlockPos pos, BlockState state) {
    // if we started on air, just return true
    if (state.getBlock() == Blocks.AIR) {
      return true;
    }
    // if the block is unbreakable, leave it
    if (state.getBlockHardness(world, pos) == -1) {
      return false;
    }

    // Find all the items on this block, plus the one above (where the anvil is).
    List<ItemEntity> items = world.getEntitiesWithinAABB(ItemEntity.class, new AxisAlignedBB(
        pos.getX(), pos.getY() + 0.5, pos.getZ(),
        pos.getX() + 1, pos.getY() + 2, pos.getZ() + 1)
    );

    // Dummy inventory, used to pass the items/state to the recipes.
    AnvilInventory inv = new AnvilInventory(
        items.stream().map(ItemEntity::getItem).collect(Collectors.toList()),
        state
    );
    AnvilRecipe recipe = world.getRecipeManager()
            .getRecipe(RecipeTypes.ANVIL, inv, world).orElse(null);

    if(recipe == null) {
        return false;
    }

    // Kill the entities used in the recipe.
    for(int i = 0; i < items.size(); i++) {
      if (inv.used[i]) {
        items.get(i).remove();
      }
    }

    BlockState transformation = recipe.getBlockResult(inv);

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
