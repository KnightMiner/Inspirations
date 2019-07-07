package knightminer.inspirations.tools.item;

import knightminer.inspirations.common.Config;
import knightminer.inspirations.library.Util;
import knightminer.inspirations.library.util.TagUtil;
import knightminer.inspirations.tools.client.WaypointCompassGetter;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBeacon;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class ItemWaypointCompass extends Item {

  public static final String TAG_POS = "pos";
  public static final String TAG_DIMENSION = "dimension";
  public static final String TAG_CHECK_BEACON = "check_beacon";

  public ItemWaypointCompass() {
    this.setHasSubtypes(true);
    this.setCreativeTab(CreativeTabs.TOOLS);
    this.addPropertyOverride(new ResourceLocation("angle"), new WaypointCompassGetter());
  }

  @Override
  public void onUpdate(ItemStack stack, World world, Entity entityIn, int itemSlot, boolean isSelected) {
    if (world.isRemote || !stack.hasTagCompound() || world.getTotalWorldTime() % 160 != 20) {
      return;
    }

    // TODO: handle other dimensions
    DimensionType type = getDimension(stack);
    if (type == world.provider.getDimensionType()) {
      BlockPos pos = getPos(stack, null, null);
      if (pos != null && world.isBlockLoaded(pos, false)) {
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileEntityBeacon) {
          // if the beacon beam is just missing, wait another cycle before clearing, this sometimes happens on world load
          NBTTagCompound tags = stack.getTagCompound();
          if (((TileEntityBeacon)te).isComplete) {
            tags.removeTag(TAG_CHECK_BEACON);
          } else if (tags.getBoolean(TAG_CHECK_BEACON)) {
            stack.setTagCompound(null);
          } else {
            stack.getTagCompound().setBoolean(TAG_CHECK_BEACON, true);
          }
        } else {
          // if the beacon is missing, clear immediately
          stack.setTagCompound(null);
        }
      }
    }
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flag) {
    DimensionType type = getDimension(stack);
    if (type != null) {
      String dimension = type.getName();
      String key = "dimension." + dimension + ".name";
      if (Util.canTranslate(key)) {
        dimension = Util.translate(key);
      }
      String dimensionTooltip = dimension;
      if (Config.waypointCompassAdvTooltip && flag == ITooltipFlag.TooltipFlags.ADVANCED) {
        BlockPos pos = getPos(stack, null, null);
        if (pos != null) {
          dimensionTooltip = Util.translateFormatted(getUnlocalizedName() + ".pos.tooltip", dimension, pos.getX(), pos.getZ());
        }
      }
      tooltip.add(dimensionTooltip);
    } else {
      tooltip.add(TextFormatting.ITALIC + Util.translate(getUnlocalizedName() + ".blank.tooltip"));
    }
  }


  /* Utilities */

  /**
   * Gets the dimension from a given waypoint compass
   * @param stack  Compass stack
   * @return  Dimension type compass points to
   */
  public static DimensionType getDimension(ItemStack stack) {
    if (!stack.hasTagCompound()) {
      return null;
    }
    String dimension = stack.getTagCompound().getString(ItemWaypointCompass.TAG_DIMENSION);
    if (StringUtils.isNullOrEmpty(dimension)) {
      return null;
    }
    try {
      return DimensionType.byName(dimension);
    } catch (IllegalArgumentException e) {
      return null;
    }
  }

  /**
   * Gets the position from the given compass
   * @param stack        Compass stack
   * @param compassType  Dimension type on the compass
   * @param worldType    Dimension type in the player's world
   * @return  Position for the compass, adjusted for the overworld/nether difference
   */
  public static BlockPos getPos(ItemStack stack, @Nullable DimensionType compassType, @Nullable DimensionType worldType) {
    if (!stack.hasTagCompound()) {
      return null;
    }
    BlockPos pos = TagUtil.readPos(stack.getTagCompound().getCompoundTag(ItemWaypointCompass.TAG_POS));
    if (pos == null) {
      return null;
    }

    // if the types differ, we may have special logic
    if (compassType != worldType) {
      if (!Config.waypointCompassCrossDimension) {
        return null;
      }

      // from nether coords
      if (compassType == DimensionType.NETHER) {
        return new BlockPos(pos.getX() * 8, pos.getY(), pos.getZ() * 8);
      }
      // to nether coords
      if (worldType == DimensionType.NETHER) {
        return new BlockPos(Math.round(pos.getX() / 8f), pos.getY(), Math.round(pos.getZ() / 8f));
      }
    }
    return pos;
  }

  /**
   * Sets the NBT for a compass based on the given world and block position
   * @param stack  Stack to modify
   * @param world  World
   * @param pos    Block pos
   */
  public static void setNBT(@Nonnull ItemStack stack, @Nullable World world, @Nullable BlockPos pos) {
    if (world == null || pos == null) {
      stack.setTagCompound(null);
      return;
    }
    setNBT(stack, world.provider.getDimensionType(), pos);
  }

  /**
   * Copies the waypoint from one compass to another
   * @param stack     Stack to modify
   * @param waypoint  Stack to copy from
   */
  public static void copyNBT(@Nonnull ItemStack stack, @Nonnull ItemStack waypoint) {
    setNBT(stack, getDimension(waypoint), getPos(waypoint, null, null));
    if(waypoint.hasDisplayName()) {
      stack.setStackDisplayName(waypoint.getDisplayName());
    }
  }

  private static void setNBT(@Nonnull ItemStack stack, DimensionType type, BlockPos pos) {
    if (type == null || pos == null) {
      stack.setTagCompound(null);
      return;
    }
    NBTTagCompound tags = TagUtil.getTagSafe(stack);
    tags.setString(ItemWaypointCompass.TAG_DIMENSION, type.getName());;
    tags.setTag(ItemWaypointCompass.TAG_POS, TagUtil.writePos(pos));
    stack.setTagCompound(tags);
  }

  /**
   * Gets the color of the needle to complement the compass color
   * @param color  Compass color
   * @return  Needle color int
   */
  public static int getNeedleColor(EnumDyeColor color) {
    switch(color) {
      case WHITE:      return 0xFFC100;
      case SILVER:     return EnumDyeColor.WHITE.colorValue;
      case GRAY:       return EnumDyeColor.SILVER.colorValue;
      case BLACK:      return EnumDyeColor.RED.colorValue;
      case RED:        return EnumDyeColor.ORANGE.colorValue;
      case ORANGE:     return EnumDyeColor.YELLOW.colorValue;
      case YELLOW:     return 0xDBA213;
      case LIME:       return EnumDyeColor.BROWN.colorValue;
      case GREEN:      return EnumDyeColor.LIME.colorValue;
      case CYAN:       return EnumDyeColor.LIGHT_BLUE.colorValue;
      case LIGHT_BLUE: return 0x77A9FF;
      case BLUE:       return 0x7E54FF;
      case PURPLE:     return EnumDyeColor.MAGENTA.colorValue;
      case MAGENTA:    return EnumDyeColor.PINK.colorValue;
      case PINK:       return 0xF2BFCE;
      case BROWN:      return 0xA59072;
    }
    return -1;
  }

  /**
   * Checks if the given stack is a valid base as a waypoint compass
   * @param stack  Stack to check
   * @return  True if it can be used as a waypoint compass, false otherwise
   */
  public static boolean isWaypointCompass(ItemStack stack) {
    Item item = stack.getItem();
    return item == InspirationsTools.waypointCompass || (!Config.craftWaypointCompass && item == Items.COMPASS);
  }
}
