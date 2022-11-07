package knightminer.inspirations.cauldrons.item;

import knightminer.inspirations.cauldrons.InspirationsCaudrons;
import knightminer.inspirations.library.MiscUtil;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class MixedDyedBottleItem extends Item {
  private static final String TAG_COLOR = "color";
  public MixedDyedBottleItem(Properties props) {
    super(props);
  }

  @Override
  public void fillItemCategory(CreativeModeTab group, NonNullList<ItemStack> items) {
    // hide from creative as means nothing without NBT, and the simple ones do the NBT
  }

  /**
   * Get a dye bottle with the specified color.
   * @param color Armor-style dye color
   * @return A single bottle.
   */
  public static ItemStack bottleFromDye(int color) {
    DyeColor dyeColor = MiscUtil.getDyeForColor(color);
    if (dyeColor != null) {
      return new ItemStack(InspirationsCaudrons.simpleDyedWaterBottle.get(dyeColor));
    }

    return MiscUtil.setColor(new ItemStack(InspirationsCaudrons.mixedDyedWaterBottle), color);
  }

  /**
   * Return the color for this bottle, if it has one.
   * @param bottle A stack holding a dye bottle
   * @return The armor-style color, or -1 if not a bottle.
   */
  public static int dyeFromBottle(ItemStack bottle) {
    Item item = bottle.getItem();
    if (item instanceof SimpleDyedBottleItem bottleItem) {
      return MiscUtil.getColor(bottleItem.getDyeColor());
    } else if (item == InspirationsCaudrons.mixedDyedWaterBottle) {
      CompoundTag tags = bottle.getTag();

      if (tags != null) {
        CompoundTag display = tags.getCompound("display");
        if (display.contains(TAG_COLOR, Tag.TAG_INT)) {
          return display.getInt(TAG_COLOR);
        }
      }
    }

    return -1;
  }
}
