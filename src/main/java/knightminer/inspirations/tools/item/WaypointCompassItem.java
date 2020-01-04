package knightminer.inspirations.tools.item;

import knightminer.inspirations.Inspirations;
import knightminer.inspirations.common.Config;
import knightminer.inspirations.common.item.HidableItem;
import knightminer.inspirations.library.Util;
import knightminer.inspirations.library.client.ClientUtil;
import knightminer.inspirations.library.util.TagUtil;
import knightminer.inspirations.tools.client.WaypointCompassPropertyGetter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.item.DyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.BeaconTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Supplier;

public class WaypointCompassItem extends HidableItem {

  public static final String TAG_POS = "pos";
  public static final String TAG_DIMENSION = "dimension";
  public static final String TAG_CHECK_BEACON = "check_beacon";

  private final int bodyColor;
  private final int needleColor;

  public WaypointCompassItem(int bodyColor, int needleColor) {
    this(bodyColor, needleColor, Config::dyeWaypointCompass);
  }

  public WaypointCompassItem(int bodyColor, int needleColor, Supplier<Boolean> enableFunc) {
    super(new Item.Properties().group(ItemGroup.TOOLS), enableFunc);

    this.bodyColor = bodyColor;
    this.needleColor = needleColor;

    this.addPropertyOverride(WaypointCompassPropertyGetter.ID, new WaypointCompassPropertyGetter());
  }

  public int getColor(ItemStack stack, int tintIndex) {
    switch(tintIndex) {
      case 0:
        return bodyColor;
      case 1:
        return needleColor;
      default:
        return -1;
    }
  }

  /**
   * Make all compasses use the same name.
   */
  @Nonnull
  @Override
  public String getTranslationKey() {
    return "item.inspirations.waypoint_compass";
  }

  @Override
  public void inventoryTick(ItemStack stack, World world, Entity entity, int itemSlot, boolean isSelected) {
    if (world.isRemote || !stack.hasTag() || world.getGameTime() % 160 != 20) {
      return;
    }

    DimensionType dimension = getDimensionType(stack);
    if (dimension != null) {
      if (dimension == world.getDimension().getType()) {
        checkPos(stack, world, getPos(stack));
        // only update other dimensions half as often
      } else if (Config.waypointCompassCrossDimension.get() && world.getGameTime() % 320 == 20) {
        World other = DimensionManager.getWorld(world.getServer(), dimension, false, false);
        // TODO: clear NBT if null?
        if (other != null) {
          checkPos(stack, other, getPos(stack, dimension, other.getDimension().getType()));
        }
      }
    }
  }

  /**
   * Determine if the examined beacon has at least one level.
   * @param te Potential beacon tile
   * @return If the beacon is constructed and can see the sky
   */
  public static boolean beaconIsComplete(TileEntity te) {
    if (!(te instanceof BeaconTileEntity)) {
      return false;
    }
    BeaconTileEntity beacon = (BeaconTileEntity)te;
    return beacon.getLevels() > 0 && beacon.beamSegments.size() != 0;
  }

  /** Checks a position in the world to see if the compass is valid */
  private void checkPos(ItemStack stack, World world, BlockPos pos) {
    if (pos != null && world.isBlockLoaded(pos)) {
      TileEntity te = world.getTileEntity(pos);
      if (te instanceof BeaconTileEntity) {
        // if the beacon beam is just missing, wait another cycle before clearing, this sometimes happens on world load
        CompoundNBT tags = stack.getOrCreateTag();
        if (beaconIsComplete(te)) {
          tags.remove(TAG_CHECK_BEACON);
        } else if (tags.getBoolean(TAG_CHECK_BEACON)) {
          clearNBT(stack);
        } else {
          stack.getOrCreateTag().putBoolean(TAG_CHECK_BEACON, true);
        }
      } else {
        // if the beacon is missing, clear immediately
        clearNBT(stack);
      }
    }
  }

  @Override
  public void addInformation(ItemStack stack, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag flag) {
    if (!Inspirations.configLoaded) {
      // MC reads all the tooltips very early for the Creative search,
      // which causes a crash here from reading uninitalised configs.
      // So just skip.
      return;
    }
    DimensionType type = getDimensionType(stack);
    if (type != null) {
      ResourceLocation dimension = DimensionType.getKey(type);
      if (dimension == null) {
        dimension = new ResourceLocation("null_dimension");
      }
      TranslationTextComponent prettyDim = new TranslationTextComponent(
              "dimension." + dimension.getNamespace() + "." + dimension.getPath().replace('/', '.')
      );

      ITextComponent dimensionTooltip;
      if (!prettyDim.getUnformattedComponentText().equals(prettyDim.getKey())) {
        dimensionTooltip = prettyDim;
      } else {
        // Fallback if we don't have a translation for the name.
        dimensionTooltip = new StringTextComponent(ClientUtil.normalizeName(dimension.getPath()));
      }

      if (flag == ITooltipFlag.TooltipFlags.ADVANCED) {
        dimensionTooltip.appendSibling(new StringTextComponent(String.format(" (%d)", getDimension(stack))));
        if (Config.waypointCompassAdvTooltip.get() && !Minecraft.getInstance().isReducedDebug()) {
          BlockPos pos = getPos(stack);
          if (pos != null) {
            dimensionTooltip = new TranslationTextComponent(getTranslationKey() + ".pos.tooltip", prettyDim, pos.getX(), pos.getZ());
          }
        }
      }
      tooltip.add(dimensionTooltip);
    } else if (Config.craftWaypointCompass()) {
      tooltip.add(new TranslationTextComponent(getTranslationKey() + ".blank.tooltip")
              .setStyle(new Style().setItalic(true))
      );
    } else {
      tooltip.add(new TranslationTextComponent(getTranslationKey() + ".vanilla.tooltip",
              new TranslationTextComponent(Items.COMPASS.getTranslationKey())
              .setStyle(new Style().setItalic(true))
      ));
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
    if (!stack.hasTag()) {
      return null;
    }
    CompoundNBT tags = stack.getOrCreateTag();
    return tags.contains(TAG_DIMENSION, Constants.NBT.TAG_ANY_NUMERIC) ? tags.getInt(TAG_DIMENSION) : null;
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
        return DimensionType.getById(dimension);
      } catch (IllegalArgumentException e) {
        return null;
      }
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
    if (!stack.hasTag()) {
      return null;
    }
    return TagUtil.readPos(stack.getOrCreateTag().getCompound(WaypointCompassItem.TAG_POS));
  }

  /**
   * Gets the position from the given compass, taking dimension differences into account
   * @param stack             Compass stack
   * @param compassDimension  Dimension on the compass
   * @param worldDimension    Dimension in the player's world
   * @return  Position for the compass, adjusted for the overworld/nether difference
   */
  @Nullable
  public static BlockPos getPos(ItemStack stack, DimensionType compassDimension, DimensionType worldDimension) {
    BlockPos pos = getPos(stack);
    if (pos == null) {
      return null;
    }

    // if the types differ, we may have special logic
    if (compassDimension != worldDimension) {
      if (!Config.waypointCompassCrossDimension.get()) {
        return null;
      }

      // from nether coords
      if (compassDimension == DimensionType.THE_NETHER) {
        return new BlockPos(pos.getX() * 8, pos.getY(), pos.getZ() * 8);
      }
      // to nether coords
      if (worldDimension == DimensionType.THE_NETHER) {
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
    setNBT(stack, world.getDimension().getType(), pos);
  }

  /** Removes compass related NBT, but keeps the display name */
  private static void clearNBT(ItemStack stack) {
    if (stack.hasDisplayName()) {
      ITextComponent name = stack.getDisplayName();
      stack.setTag(null);
      stack.setDisplayName(name);
    } else {
      stack.setTag(null);
    }
  }

  /**
   * Copies the waypoint from one compass to another
   * @param stack     Stack to modify
   * @param waypoint  Stack to copy from
   */
  public static void copyNBT(@Nonnull ItemStack stack, @Nonnull ItemStack waypoint) {
    if (!waypoint.hasTag()) {
      return;
    }
    setNBT(stack, getDimensionType(waypoint), getPos(waypoint));
  }

  private static void setNBT(@Nonnull ItemStack stack, DimensionType dimension, BlockPos pos) {
    if (pos == null) {
      stack.setTag(null);
      return;
    }
    CompoundNBT tags = TagUtil.getTagSafe(stack);
    tags.putInt(WaypointCompassItem.TAG_DIMENSION, dimension.getId());
    tags.put(WaypointCompassItem.TAG_POS, TagUtil.writePos(pos));
    stack.setTag(tags);
  }

  /**
   * Gets the color of the needle to complement the compass color
   * @param color  Compass color
   * @return  Needle color int
   */
  public static int getNeedleColor(DyeColor color) {
    switch(color) {
      case WHITE:      return 0xFFC100;
      case LIGHT_GRAY: return DyeColor.WHITE.colorValue;
      case GRAY:       return DyeColor.LIGHT_GRAY.colorValue;
      case BLACK:      return DyeColor.RED.colorValue;
      case RED:        return DyeColor.ORANGE.colorValue;
      case ORANGE:     return DyeColor.YELLOW.colorValue;
      case YELLOW:     return 0xDBA213;
      case LIME:       return DyeColor.BROWN.colorValue;
      case GREEN:      return DyeColor.LIME.colorValue;
      case CYAN:       return DyeColor.LIGHT_BLUE.colorValue;
      case LIGHT_BLUE: return 0x77A9FF;
      case BLUE:       return 0x7E54FF;
      case PURPLE:     return DyeColor.MAGENTA.colorValue;
      case MAGENTA:    return DyeColor.PINK.colorValue;
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
    return item instanceof WaypointCompassItem || (!Config.craftWaypointCompass() && item == Items.COMPASS);
  }
}
