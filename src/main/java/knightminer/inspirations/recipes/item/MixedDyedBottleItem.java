package knightminer.inspirations.recipes.item;

import knightminer.inspirations.common.Config;
import knightminer.inspirations.common.item.HidableItem;
import knightminer.inspirations.library.Util;
import knightminer.inspirations.recipes.InspirationsRecipes;
import net.minecraft.item.DyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.Constants;

public class MixedDyedBottleItem extends HidableItem {
  private static final String TAG_COLOR = "color";
  public MixedDyedBottleItem(Properties props) {
    super(props, Config::enableCauldronDyeing);
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
    CompoundNBT tags = new CompoundNBT();
    CompoundNBT display = new CompoundNBT();
    display.putInt(TAG_COLOR, color);
    tags.put("display", display);
    result.setTag(tags);
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
      return ((SimpleDyedBottleItem)item).getColor().colorValue;
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
