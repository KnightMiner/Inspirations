package knightminer.inspirations.recipes.recipe.inventory;

import knightminer.inspirations.library.recipe.cauldron.contents.ICauldronContents;
import knightminer.inspirations.library.recipe.cauldron.recipe.ICauldronRecipe;
import knightminer.inspirations.library.recipe.cauldron.util.CauldronTemperature;
import knightminer.inspirations.recipes.tileentity.CauldronTileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.function.Consumer;

/**
 * Inventory used in a cauldron tile entity that supports generic contexts for interaction
 */
public class TileCauldronInventory extends CauldronItemInventory {
  private final CauldronTileEntity tile;

  @Nullable
  private ICauldronContents newContents = null;
  private int newLevel = -1;
  public TileCauldronInventory(CauldronTileEntity tile) {
    this.tile = tile;
  }

  @Override
  public boolean isSimple() {
    return false;
  }

  @Override
  public void playSound(SoundEvent sound) {
    World world = tile.getWorld();
    if (world != null) {
      world.playSound(null, tile.getPos(), sound, SoundCategory.BLOCKS, 1.0f, 1.0f);
    }
  }

  /* Item handling */

  /**
   * Sets the context for item stack handling
   * @param stack       Stack to match for recipes
   * @param itemSetter  Logic to update the stack in the context. If null, will assume {@link #getStack()} will be used for updates
   * @param itemAdder   Logic to add a new stack to the context
   */
  public void setItemContext(ItemStack stack, @Nullable Consumer<ItemStack> itemSetter, Consumer<ItemStack> itemAdder) {
    this.stack = stack;
    this.itemSetter = itemSetter == null ? EMPTY_CONSUMER : itemSetter;
    this.itemAdder = itemAdder;
    this.newLevel = -1;
  }

  /**
   * Clears any context specific data from the wrapper
   */
  public void clearContext() {
    this.stack = ItemStack.EMPTY;
    this.itemSetter = EMPTY_CONSUMER;
    this.itemAdder = EMPTY_CONSUMER;
    this.newLevel = -1;
  }


  /* Level */

  @Override
  public int getLevel() {
    if (newLevel != -1) {
      return newLevel;
    }
    return tile.getLevel();
  }

  @Override
  public void setLevel(int level) {
    // set variable so we can just run updates at the end
    newLevel = MathHelper.clamp(level, 0, ICauldronRecipe.MAX);
  }


  /* Contents */

  @Override
  public ICauldronContents getContents() {
    if (newContents != null) {
      return newContents;
    }
    return tile.getContents();
  }

  @Override
  public void setContents(ICauldronContents contents) {
    this.newContents = contents;
  }

  @Override
  public CauldronTemperature getTemperature() {
    return tile.getTemperature();
  }
}
