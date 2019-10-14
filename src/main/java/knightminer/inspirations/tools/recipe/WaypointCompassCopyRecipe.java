package knightminer.inspirations.tools.recipe;

import knightminer.inspirations.common.Config;
import knightminer.inspirations.tools.InspirationsTools;
import knightminer.inspirations.tools.item.ItemWaypointCompass;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.SpecialRecipe;
import net.minecraft.item.crafting.SpecialRecipeSerializer;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.function.Function;

public class WaypointCompassCopyRecipe extends SpecialRecipe {
  public WaypointCompassCopyRecipe(ResourceLocation idIn) {
    super(idIn);
  }

  @Override
  public boolean matches(@Nonnull CraftingInventory inv, @Nonnull World world) {
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
      if (!ItemWaypointCompass.isWaypointCompass(stack)) {
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
        if (!blank.isEmpty() && blank.getItem() != stack.getItem()) {
          return false;
        }
        blank = stack;
      }
    }
    return foundWaypoint && !blank.isEmpty();
  }

  @Nonnull
  @Override
  public ItemStack getRecipeOutput() {
    return ItemStack.EMPTY;
  }

  @Nonnull
  @Override
  public ItemStack getCraftingResult(CraftingInventory inv) {
    ItemStack waypoint = ItemStack.EMPTY;
    ItemStack result = ItemStack.EMPTY;
    int count = 0;

    // already validated that these are unique in matches
    for (int i = 0; i < inv.getSizeInventory(); i++) {
      ItemStack stack = inv.getStackInSlot(i);
      if (!ItemWaypointCompass.isWaypointCompass(stack)) {
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
      if (result.getItem() instanceof ItemWaypointCompass) {
        result = result.copy();
      } else {
        result = new ItemStack(InspirationsTools.waypointCompasses[DyeColor.WHITE.getId()]);
      }
      result.setCount(count);
      ItemWaypointCompass.copyNBT(result, waypoint);
      return result;
    }
    return ItemStack.EMPTY;
  }

  @Nonnull
  @Override
  public NonNullList<ItemStack> getRemainingItems(CraftingInventory inv) {
    NonNullList<ItemStack> items = NonNullList.withSize(inv.getSizeInventory(), ItemStack.EMPTY);
    for (int i = 0; i < inv.getSizeInventory(); i++) {
      ItemStack stack = inv.getStackInSlot(i);
      if (stack.getItem() instanceof ItemWaypointCompass && ItemWaypointCompass.getDimension(stack) != null) {
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

  @Nonnull
  @Override
  public IRecipeSerializer<?> getSerializer() {
    return SERIALIZER;
  }

  public static Serializer SERIALIZER = new Serializer(WaypointCompassCopyRecipe::new);

  public static class Serializer extends SpecialRecipeSerializer<WaypointCompassCopyRecipe> {
    public Serializer(Function<ResourceLocation, WaypointCompassCopyRecipe> factory) {
      super(factory);
    }
  }
}
