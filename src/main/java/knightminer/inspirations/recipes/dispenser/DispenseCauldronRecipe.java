package knightminer.inspirations.recipes.dispenser;

import knightminer.inspirations.common.Config;
import knightminer.inspirations.library.InspirationsTags;
import knightminer.inspirations.recipes.recipe.inventory.VanillaCauldronInventory;
import knightminer.inspirations.recipes.tileentity.CauldronTileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.CauldronBlock;
import net.minecraft.block.DispenserBlock;
import net.minecraft.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.dispenser.IDispenseItemBehavior;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.DispenserTileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import slimeknights.mantle.util.TileEntityHelper;

import java.util.Optional;
import java.util.function.Consumer;

/**
 * Dispenser behavior logic for cauldron recipes
 */
public class DispenseCauldronRecipe extends DefaultDispenseItemBehavior {
  private static final DefaultDispenseItemBehavior DEFAULT = new DefaultDispenseItemBehavior();
  private final IDispenseItemBehavior fallback;

  /**
   * Creates a new instance of the dispenser logic
   * @param fallback  Fallback if no cauldron is in front of the dispenser
   */
  public DispenseCauldronRecipe(IDispenseItemBehavior fallback) {
    this.fallback = fallback;
  }

  @Override
  protected ItemStack execute(IBlockSource source, ItemStack stack) {
    if (!stack.getItem().is(InspirationsTags.Items.DISP_CAULDRON_RECIPES)) {
      return fallback.dispense(source, stack);
    }

    // find cauldron, quit if missing
    World world = source.getLevel();
    BlockPos pos = source.getPos().relative(source.getBlockState().getValue(DispenserBlock.FACING));
    BlockState state = world.getBlockState(pos);
    if (!(state.getBlock() instanceof CauldronBlock)) {
      return fallback.dispense(source, stack);
    }

    // create consumer to add items
    DispenserTileEntity dispenser = source.getEntity();
    Consumer<ItemStack> addItems = item -> {
      if (dispenser.addItem(stack) < 0) {
        DEFAULT.dispense(source, stack);
      }
    };

    // use tile entity if extended, it handles everything
    if (Config.extendedCauldron.getAsBoolean()) {
      // if we have the tile entity, run. If missing, fallback to vanilla logic
      Optional<CauldronTileEntity> cauldron = TileEntityHelper.getTile(CauldronTileEntity.class, world, pos);
      if (cauldron.isPresent()) {
        ItemStack newStack = cauldron.get().handleDispenser(stack, addItems);
        // nonnull means we did something
        if (newStack != null) {
          return newStack;
        }
        // null means we did nothing, dispense the stack
        return DEFAULT.dispense(source, stack);
      }
    }

    // use the vanilla logic to run the recipe if no TE
    VanillaCauldronInventory inventory = new VanillaCauldronInventory(world, pos, state, stack, addItems);
    if (inventory.handleRecipe()) {
      return inventory.getStack();
    }

    // no recipe? dispense directly
    return DEFAULT.dispense(source, stack);
  }
}
