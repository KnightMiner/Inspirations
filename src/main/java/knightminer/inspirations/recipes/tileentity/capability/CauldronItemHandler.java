package knightminer.inspirations.recipes.tileentity.capability;

import knightminer.inspirations.Inspirations;
import knightminer.inspirations.library.recipe.cauldron.recipe.ICauldronRecipe;
import knightminer.inspirations.recipes.recipe.inventory.TileCauldronInventory;
import knightminer.inspirations.recipes.tileentity.CauldronTileEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.IItemHandler;

import java.util.function.Consumer;

/**
 * Item handler capability for the cauldron. Allows hoppers to perform recipes that do not require an item input (removing ice for instance)
 */
@Deprecated
public class CauldronItemHandler implements IItemHandler {
  // properties from the TE
  private final CauldronTileEntity te;
  private final TileCauldronInventory inventory;
  /** Recipe for the to the given stack. May be null for empty stacks */
  private ICauldronRecipe currentRecipe;
  /** Stack cache. If null, has not been fetched. If nonnull, should not try to find a recipe */
  private ItemStack stack;

  /** If true, recipe has side effects */
  private boolean hasSideEffects = false;
  /** Consumer to mark side-effects */
  private final Consumer<ItemStack> sideEffects = stack -> hasSideEffects = true;

  /**
   * Creates a new handler instance
   * @param te         Tile instance
   * @param inventory  Inventory instance
   */
  public CauldronItemHandler(CauldronTileEntity te, TileCauldronInventory inventory) {
    this.te = te;
    this.inventory = inventory;
  }

  /**
   * Called to handle a recipe for the cauldron
   * @param recipe    Recipe to handle
   * @param execute   If true, executes the recipe
   * @return  Stack result of recipe
   */
  private ItemStack handleRecipe(ICauldronRecipe recipe, boolean execute) {
    // try the recipe
    hasSideEffects = false;
    inventory.setItemHandlerContext(sideEffects, execute);
    recipe.handleRecipe(inventory);

    // skip recipe if it has side effects
    if (hasSideEffects) {
      stack = ItemStack.EMPTY;
      currentRecipe = null;
      return stack;
    }

    // cache the recipe, it worked
    currentRecipe = recipe;

    // if extracting items, ensure we have enough
    ItemStack result = stack = inventory.getStack();
    if (execute && !stack.isEmpty()) {
      result = stack.copy();
      te.updateStateAndBlock(inventory.getContents(), inventory.getLevel());
    }

    // clear contents and return
    inventory.clearContext();
    return result;
  }

  /**
   * Gets the current item stack result from the cauldron. Unlike {@link #handleRecipe(ICauldronRecipe, boolean)}, will actually fetch a recipe
   * @return  Item result
   */
  private ItemStack getResult() {
    // if stack is null, find a recipe and update the stack
    if (stack == null) {
      ICauldronRecipe recipe = te.findRecipe();
      if (recipe != null) {
        handleRecipe(recipe, false);
      } else {
        stack = ItemStack.EMPTY;
      }
    }
    return stack;
  }

  /**
   * Called by the cauldron to clear the cached result if the cauldron contents change
   */
  public void clearCache() {
    stack = null;
    currentRecipe = null;
  }

  @Override
  public ItemStack getStackInSlot(int slot) {
    return getResult();
  }

  @Override
  public ItemStack extractItem(int slot, int amount, boolean simulate) {
    // simulate can use get result as we do not have to change the cauldron at all
    if (simulate) {
      // if the result is too big, do not return
      ItemStack result = getResult();
      if (result.getCount() > amount) {
        return ItemStack.EMPTY;
      }
      return result;
    }

    // use the current recipe if we have one.
    // validate it later though, don't want to validate if they never ask us to perform the recipe
    ICauldronRecipe recipe = currentRecipe;
    boolean needsValidation = true;
    // try fetching if no recipe
    // however, if cache is set it means we already failed to find one
    if (recipe == null && stack == null) {
      recipe = te.findRecipe();
      needsValidation = false; // no need to validate, this one matched
    }

    // if no recipe, return empty
    if (recipe == null) {
      stack = ItemStack.EMPTY;
      return stack;
    }

    // if the stack is too big, give up
    if (stack.getCount() > amount) {
      return ItemStack.EMPTY;
    }

    // validate the cached recipe as we are about to extract
    if (needsValidation) {
      Level world = te.getLevel();
      if (world == null || !currentRecipe.matches(inventory, world)) {
        clearCache();
        Inspirations.log.error("Attempted to extract items from a recipe that does not match the cauldron.");
        return ItemStack.EMPTY;
      }
    }

    // run the recipe if possible
    return handleRecipe(recipe, true);
  }

  @Override
  public int getSlotLimit(int slot) {
    return 64;
  }

  @Override
  public int getSlots() {
    return 1;
  }

  @Override
  public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
    return stack;
  }

  @Override
  public boolean isItemValid(int slot, ItemStack stack) {
    return false;
  }
}
