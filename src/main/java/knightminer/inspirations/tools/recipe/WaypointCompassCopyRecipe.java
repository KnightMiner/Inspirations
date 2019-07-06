package knightminer.inspirations.tools.recipe;

import knightminer.inspirations.tools.InspirationsTools;
import knightminer.inspirations.tools.item.ItemWaypointCompass;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.registries.IForgeRegistryEntry;

public class WaypointCompassCopyRecipe extends IForgeRegistryEntry.Impl<IRecipe> implements IRecipe {
  private ItemStack OUTPUT = new ItemStack(InspirationsTools.waypointCompass);

  @Override
  public boolean matches(InventoryCrafting inv, World world) {
    boolean foundWaypoint = false;
    ItemStack blank = ItemStack.EMPTY;
    for (int i = 0; i < inv.getSizeInventory(); i++) {
      // must be a compass
      ItemStack stack = inv.getStackInSlot(i);
      if (stack.isEmpty()) {
        continue;
      }
      if (stack.getItem() != InspirationsTools.waypointCompass) {
        return false;
      }

      // if it has a NBT dimension, that is our waypoint
      if (ItemWaypointCompass.getDimension(stack) != null) {
        // at most one waypoint, so we know what to copy
        if (foundWaypoint) {
          return false;
        }
        foundWaypoint = true;
      } else {
        // can have multiple blanks if they all match
        if (!blank.isEmpty() && blank.getMetadata() != stack.getMetadata()) {
          return false;
        }
        blank = stack;
      }
    }
    return foundWaypoint && !blank.isEmpty();
  }

  @Override
  public ItemStack getRecipeOutput() {
    return OUTPUT;
  }

  @Override
  public ItemStack getCraftingResult(InventoryCrafting inv) {
    ItemStack waypoint = ItemStack.EMPTY;
    ItemStack result = ItemStack.EMPTY;
    int count = 0;

    // already validated that these are unique in matches
    for (int i = 0; i < inv.getSizeInventory(); i++) {
      ItemStack stack = inv.getStackInSlot(i);
      if (stack.getItem() != InspirationsTools.waypointCompass) {
        continue;
      }

      // if it has a NBT dimension, that is our waypoint
      if (ItemWaypointCompass.getDimension(stack) != null) {
        waypoint = stack;
      } else {
        result = stack;
        count++;
      }
    }

    if (!result.isEmpty() && !waypoint.isEmpty()) {
      result = result.copy();
      result.setCount(count);
      ItemWaypointCompass.copyNBT(result, waypoint);
      return result;
    }
    return ItemStack.EMPTY;
  }

  @Override
  public NonNullList<ItemStack> getRemainingItems(InventoryCrafting inv) {
    NonNullList<ItemStack> items = NonNullList.withSize(inv.getSizeInventory(), ItemStack.EMPTY);
    for (int i = 0; i < inv.getSizeInventory(); i++) {
      ItemStack stack = inv.getStackInSlot(i);
      if (stack.getItem() == InspirationsTools.waypointCompass && ItemWaypointCompass.getDimension(stack) != null) {
        stack = stack.copy();
        stack.setCount(1);
        items.set(i, stack);
        break;
      }
    }
    return items;
  }

  @Override
  public boolean canFit(int width, int height) {
    return width * height >= 2;
  }

  @Override
  public boolean isDynamic() {
    return true;
  }
}
