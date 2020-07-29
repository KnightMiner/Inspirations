package knightminer.inspirations.tools.recipe;

import knightminer.inspirations.common.Config;
import knightminer.inspirations.library.recipe.RecipeSerializers;
import knightminer.inspirations.tools.InspirationsTools;
import knightminer.inspirations.tools.item.WaypointCompassItem;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.SpecialRecipe;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class CopyWaypointCompassRecipe extends SpecialRecipe {
  public CopyWaypointCompassRecipe(ResourceLocation idIn) {
    super(idIn);
  }

  @Override
  public boolean matches(CraftingInventory inv, World world) {
    if (!Config.enableWaypointCompass.get()) {
      return false;
    }
    boolean foundWaypoint = false;
    ItemStack blank = ItemStack.EMPTY;
    for (int i = 0; i < inv.getSizeInventory(); i++) {
      // must be a compass
      ItemStack stack = inv.getStackInSlot(i);
      if (stack.isEmpty()) {
        continue;
      }
      if (!WaypointCompassItem.isWaypointCompass(stack)) {
        return false;
      }

      // if it has a NBT dimension, that is our waypoint
      if (WaypointCompassItem.getDimension(stack) != null) {
        // at most one waypoint, so we know what to copy
        if (foundWaypoint) {
          return false;
        }
        foundWaypoint = true;
      } else {
        // can have multiple blanks if they all match
        if (!blank.isEmpty() && blank.getItem() != stack.getItem()) {
          return false;
        }
        blank = stack;
      }
    }
    return foundWaypoint && !blank.isEmpty();
  }

  @Override
  public ItemStack getRecipeOutput() {
    return ItemStack.EMPTY;
  }

  @Override
  public ItemStack getCraftingResult(CraftingInventory inv) {
    ItemStack waypoint = ItemStack.EMPTY;
    ItemStack result = ItemStack.EMPTY;
    int count = 0;

    // already validated that these are unique in matches
    for (int i = 0; i < inv.getSizeInventory(); i++) {
      ItemStack stack = inv.getStackInSlot(i);
      if (!WaypointCompassItem.isWaypointCompass(stack)) {
        continue;
      }

      // if it has a NBT dimension, that is our waypoint
      if (WaypointCompassItem.getDimension(stack) != null) {
        waypoint = stack;
      } else {
        result = stack;
        count++;
      }
    }

    if (!result.isEmpty() && !waypoint.isEmpty()) {
      if (result.getItem() instanceof WaypointCompassItem) {
        result = result.copy();
      } else {
        result = new ItemStack(InspirationsTools.waypointCompasses[DyeColor.WHITE.getId()]);
      }
      result.setCount(count);
      WaypointCompassItem.copyNBT(result, waypoint);
      return result;
    }
    return ItemStack.EMPTY;
  }

  @Override
  public NonNullList<ItemStack> getRemainingItems(CraftingInventory inv) {
    NonNullList<ItemStack> items = NonNullList.withSize(inv.getSizeInventory(), ItemStack.EMPTY);
    for (int i = 0; i < inv.getSizeInventory(); i++) {
      ItemStack stack = inv.getStackInSlot(i);
      if (stack.getItem() instanceof WaypointCompassItem && WaypointCompassItem.getDimension(stack) != null) {
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
  public String getGroup() {
    return "inspirations:waypoint_compass_copy";
  }

  @Override
  public IRecipeSerializer<?> getSerializer() {
    return RecipeSerializers.COPY_WAYPOINT_COMPASS;
  }
}
