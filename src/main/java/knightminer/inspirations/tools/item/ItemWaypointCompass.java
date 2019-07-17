package knightminer.inspirations.tools.item;

import knightminer.inspirations.common.Config;
import knightminer.inspirations.library.Util;
import knightminer.inspirations.library.client.ClientUtil;
import knightminer.inspirations.library.util.TagUtil;
import knightminer.inspirations.tools.InspirationsTools;
import knightminer.inspirations.tools.client.WaypointCompassGetter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBeacon;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Locale;

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

    Integer dimension = getDimension(stack);
    if (dimension != null) {
      if (dimension == world.provider.getDimension()) {
        checkPos(stack, world, getPos(stack));
        // only update other dimensions half as often
      } else if (Config.waypointCompassCrossDimension && world.getTotalWorldTime() % 320 == 20) {
        World other = DimensionManager.getWorld(dimension);
        // TODO: clear NBT if null?
        if (other != null) {
          checkPos(stack, other, getPos(stack, dimension, other.provider.getDimension()));
        }
      }
    }
  }

  /** Checks a position in the world to see if the compass is valid */
  private void checkPos(ItemStack stack, World world, BlockPos pos) {
    if (pos != null && world.isBlockLoaded(pos, false)) {
      TileEntity te = world.getTileEntity(pos);
      if (te instanceof TileEntityBeacon) {
        // if the beacon beam is just missing, wait another cycle before clearing, this sometimes happens on world load
        NBTTagCompound tags = stack.getTagCompound();
        if (((TileEntityBeacon)te).isComplete) {
          tags.removeTag(TAG_CHECK_BEACON);
        } else if (tags.getBoolean(TAG_CHECK_BEACON)) {
          clearNBT(stack);
        } else {
          stack.getTagCompound().setBoolean(TAG_CHECK_BEACON, true);
        }
      } else {
        // if the beacon is missing, clear immediately
        clearNBT(stack);
      }
    }
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flag) {
    DimensionType type = getDimensionType(stack);
    if (type != null) {
      String dimension = type.getName().toLowerCase(Locale.US);
      String key = "dimension." + dimension.replace(' ', '_') + ".name";
      if (Util.canTranslate(key)) {
        dimension = Util.translate(key);
      } else {
        dimension = ClientUtil.normalizeName(dimension);
      }
      if (flag == ITooltipFlag.TooltipFlags.ADVANCED) {
        dimension += String.format(" (%d)", getDimension(stack));
        if (Config.waypointCompassAdvTooltip && !Minecraft.getMinecraft().isReducedDebug()) {
          BlockPos pos = getPos(stack);
          if (pos != null) {
            dimension = Util.translateFormatted(getUnlocalizedName() + ".pos.tooltip", dimension, pos.getX(), pos.getZ());
          }
        }
      }
      tooltip.add(dimension);
    } else if (Config.craftWaypointCompass) {
      tooltip.add(TextFormatting.ITALIC + Util.translate(getUnlocalizedName() + ".blank.tooltip"));
    } else {
      tooltip.add(TextFormatting.ITALIC + Util.translateFormatted(getUnlocalizedName() + ".vanilla.tooltip", Util.translate(Items.COMPASS.getUnlocalizedName() + ".name")));
    }
  }


  /* Utilities */

  /**
   * Gets the dimension ID from a stack, or null if it has none
   * @param stack  Stack containing dimension
   * @return  Dimension ID, or null if not present+
   */
  @Nullable
  public static Integer getDimension(ItemStack stack) {
    if (!stack.hasTagCompound()) {
      return null;
    }
    NBTTagCompound tags = stack.getTagCompound();
    return tags.hasKey(TAG_DIMENSION, 99) ? tags.getInteger(TAG_DIMENSION) : null;
  }

  /**
   * Gets the dimension from a given waypoint compass
   * @param stack  Compass stack
   * @return  Dimension type compass points to
   */
  @Nullable
  public static DimensionType getDimensionType(ItemStack stack) {
    Integer dimension = getDimension(stack);
    if (dimension != null) {
      try {
        return DimensionManager.getProviderType(dimension);
      } catch (IllegalArgumentException e) {}
    }
    return null;
  }

  /**
   * Gets the position from the given compass, ignoring dimension differences
   * @param stack  Compass stack
   * @return  Position from the compass
   */
  @Nullable
  public static BlockPos getPos(ItemStack stack) {
    if (!stack.hasTagCompound()) {
      return null;
    }
    return TagUtil.readPos(stack.getTagCompound().getCompoundTag(ItemWaypointCompass.TAG_POS));
  }

  /**
   * Gets the position from the given compass, taking dimension differences into account
   * @param stack             Compass stack
   * @param compassDimension  Dimension on the compass
   * @param worldDimension    Dimension in the player's world
   * @return  Position for the compass, adjusted for the overworld/nether difference
   */
  @Nullable
  public static BlockPos getPos(ItemStack stack, int compassDimension, int worldDimension) {
    BlockPos pos = getPos(stack);
    if (pos == null) {
      return null;
    }

    // if the types differ, we may have special logic
    if (compassDimension != worldDimension) {
      if (!Config.waypointCompassCrossDimension) {
        return null;
      }

      // from nether coords
      if (compassDimension == DimensionType.NETHER.getId()) {
        return new BlockPos(pos.getX() * 8, pos.getY(), pos.getZ() * 8);
      }
      // to nether coords
      if (worldDimension == DimensionType.NETHER.getId()) {
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
      clearNBT(stack);
      return;
    }
    setNBT(stack, world.provider.getDimension(), pos);
  }

  /** Removes compass related NBT, but keeps the display name */
  private static void clearNBT(ItemStack stack) {
    if (stack.hasDisplayName()) {
      String name = stack.getDisplayName();
      stack.setTagCompound(null);
      stack.setStackDisplayName(name);
    } else {
      stack.setTagCompound(null);
    }
  }

  /**
   * Copies the waypoint from one compass to another
   * @param stack     Stack to modify
   * @param waypoint  Stack to copy from
   */
  public static void copyNBT(@Nonnull ItemStack stack, @Nonnull ItemStack waypoint) {
    if (!waypoint.hasTagCompound()) {
      return;
    }
    setNBT(stack, getDimension(waypoint), getPos(waypoint));
  }

  private static void setNBT(@Nonnull ItemStack stack, int dimension, BlockPos pos) {
    if (pos == null) {
      stack.setTagCompound(null);
      return;
    }
    NBTTagCompound tags = TagUtil.getTagSafe(stack);
    tags.setInteger(ItemWaypointCompass.TAG_DIMENSION, dimension);
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
