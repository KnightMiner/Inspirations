package knightminer.inspirations.recipes.dispenser;

import knightminer.inspirations.common.Config;
import knightminer.inspirations.library.InspirationsTags;
import knightminer.inspirations.recipes.recipe.inventory.VanillaCauldronInventory;
import knightminer.inspirations.recipes.tileentity.CauldronTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CauldronBlock;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.entity.DispenserBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import slimeknights.mantle.util.BlockEntityHelper;

import java.util.Optional;
import java.util.function.Consumer;

/**
 * Dispenser behavior logic for cauldron recipes
 */
public class DispenseCauldronRecipe extends DefaultDispenseItemBehavior {
  private static final DefaultDispenseItemBehavior DEFAULT = new DefaultDispenseItemBehavior();
  private final DispenseItemBehavior fallback;

  /**
   * Creates a new instance of the dispenser logic
   * @param fallback  Fallback if no cauldron is in front of the dispenser
   */
  public DispenseCauldronRecipe(DispenseItemBehavior fallback) {
    this.fallback = fallback;
  }

  @Override
  protected ItemStack execute(BlockSource source, ItemStack stack) {
    if (!stack.is(InspirationsTags.Items.DISP_CAULDRON_RECIPES)) {
      return fallback.dispense(source, stack);
    }

    // find cauldron, quit if missing
    Level world = source.getLevel();
    BlockPos pos = source.getPos().relative(source.getBlockState().getValue(DispenserBlock.FACING));
    BlockState state = world.getBlockState(pos);
    if (!(state.getBlock() instanceof CauldronBlock)) {
      return fallback.dispense(source, stack);
    }

    // create consumer to add items
    DispenserBlockEntity dispenser = source.getEntity();
    Consumer<ItemStack> addItems = item -> {
      if (dispenser.addItem(stack) < 0) {
        DEFAULT.dispense(source, stack);
      }
    };

    // use tile entity if extended, it handles everything
    if (Config.extendedCauldron.get()) {
      // if we have the tile entity, run. If missing, fallback to vanilla logic
      Optional<CauldronTileEntity> cauldron = BlockEntityHelper.get(CauldronTileEntity.class, world, pos);
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
