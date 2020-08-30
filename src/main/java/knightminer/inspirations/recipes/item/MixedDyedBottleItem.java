package knightminer.inspirations.recipes.item;

import knightminer.inspirations.library.Util;
import knightminer.inspirations.recipes.InspirationsRecipes;
import net.minecraft.item.DyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.util.Constants;

public class MixedDyedBottleItem extends Item {
  private static final String TAG_COLOR = "color";
  public MixedDyedBottleItem(Properties props) {
    super(props);
  }

  @Override
  public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
    // hide from creative as means nothing without NBT, and the simple ones do the NBT
  }

  /**
   * Get a dye bottle with the specified color.
   * @param color Armor-style dye color
   * @return A single bottle.
   */
  public static ItemStack bottleFromDye(int color) {
    DyeColor dyeColor = Util.getDyeForColor(color);
    if (dyeColor != null) {
      return new ItemStack(InspirationsRecipes.simpleDyedWaterBottle.get(dyeColor));
    }

    ItemStack result = new ItemStack(InspirationsRecipes.mixedDyedWaterBottle);
    result.getOrCreateChildTag("display").putInt(TAG_COLOR, color);
    return result;
  }

  /**
   * Return the color for this bottle, if it has one.
   * @param bottle A stack holding a dye bottle
   * @return The armor-style color, or -1 if not a bottle.
   */
  public static int dyeFromBottle(ItemStack bottle) {
    Item item = bottle.getItem();
    if (item instanceof SimpleDyedBottleItem) {
      return ((SimpleDyedBottleItem)item).getColor().getColorValue();
    } else if (item == InspirationsRecipes.mixedDyedWaterBottle) {
      CompoundNBT tags = bottle.getTag();

      if (tags != null) {
        CompoundNBT display = tags.getCompound("display");
        if (display.contains(TAG_COLOR, Constants.NBT.TAG_INT)) {
          return display.getInt(TAG_COLOR);
        }
      }
    }

    return -1;
  }
}
