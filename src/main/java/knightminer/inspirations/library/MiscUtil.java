package knightminer.inspirations.library;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeableLeatherItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings("deprecation")
public class MiscUtil {
  private static final String TAG_DISPLAY = "display";
  private static final String TAG_COLOR = "color";

  /**
   * Compute a voxelshape, rotated by the provided yaw.
   */
  public static VoxelShape makeRotatedShape(Direction side, int x1, int y1, int z1, int x2, int y2, int z2) {
    float yaw = -(float)Math.PI / 2F * side.get2DDataValue();
    Vec3 min = new Vec3(x1 - 8, y1 - 8, z1 - 8).yRot(yaw);
    Vec3 max = new Vec3(x2 - 8, y2 - 8, z2 - 8).yRot(yaw);
    return Shapes.box(
        0.5 + min.x / 16.0, 0.5 + min.y / 16.0, 0.5 + min.z / 16.0,
        0.5 + max.x / 16.0, 0.5 + max.y / 16.0, 0.5 + max.z / 16.0
                             );
  }

  // An item with Silk Touch, to make blocks drop their silk touch items if they have any.
  // Using a Stick makes sure it won't be damaged.
  private static final ItemStack silkTouchItem = new ItemStack(Items.STICK);
  static {
    silkTouchItem.enchant(Enchantments.SILK_TOUCH, 1);
  }

  /**
   * Gets an item stack from a block state. Uses Silk Touch drops
   * @param state Input state
   * @return ItemStack for the state, or ItemStack.EMPTY if a valid item cannot be found
   */
  public static ItemStack getStackFromState(ServerLevel world, @Nullable BlockState state) {
    if (state == null) {
      return ItemStack.EMPTY;
    }
    Block block = state.getBlock();

    // skip air
    if (block == Blocks.AIR) {
      return ItemStack.EMPTY;
    }

    // Fill a fake context in to get Silk Touch drops.
    // From LootParameterSets.Block,
    // BLOCK_STATE, POSITION and TOOL is required and
    // THIS_ENTITY, BLOCK_ENTITY and EXPLOSION_RADIUS are optional.
    // BLOCK_STATE is provided by getDrops().
    List<ItemStack> drops = state.getDrops(new LootContext.Builder(world)
                                               .withParameter(LootContextParams.ORIGIN, new Vec3(0.5, 64, 0.5))
                                               .withParameter(LootContextParams.TOOL, silkTouchItem)
                                          );
    if (drops.size() > 0) {
      return drops.get(0);
    }

    // if it fails, do a fallback of item.getItemFromBlock
    InspirationsRegistry.log.error("Failed to get silk touch drop for {}, using fallback", state);

    // fallback, use item dropped
    Item item = Item.byBlock(block);
    if (item == Items.AIR) {
      return ItemStack.EMPTY;
    }
    return new ItemStack(item);
  }

  /**
   * Creates a NonNullList from the specified elements, using the class as the type
   * @param elements Elements for the list
   * @return New NonNullList
   */
  @SafeVarargs
  @Deprecated
  public static <E> NonNullList<E> createNonNullList(E... elements) {
    NonNullList<E> list = NonNullList.create();
    list.addAll(Arrays.asList(elements));
    return list;
  }

  /**
   * Combines two colors
   * @param color1 First color
   * @param color2 Second color
   * @param scale  Determines how many times color2 is applied
   * @return Combined color
   */
  public static int combineColors(int color1, int color2, int scale) {
    if (scale == 0) {
      return color1;
    }
    int a = color1 >> 24 & 0xFF;
    int r = color1 >> 16 & 0xFF;
    int g = color1 >> 8 & 0xFF;
    int b = color1 & 0xFF;
    int a2 = color2 >> 24 & 0xFF;
    int r2 = color2 >> 16 & 0xFF;
    int g2 = color2 >> 8 & 0xFF;
    int b2 = color2 & 0xFF;

    for (int i = 0; i < scale; i++) {
      a = (int)Math.sqrt(a * a2);
      r = (int)Math.sqrt(r * r2);
      g = (int)Math.sqrt(g * g2);
      b = (int)Math.sqrt(b * b2);
    }
    return a << 24 | r << 16 | g << 8 | b;
  }

  /** List of dye color values */
  private static final int[] DYE_COLORS;
  static {
    DYE_COLORS = new int[16];
    for (DyeColor color : DyeColor.values()) {
      DYE_COLORS[color.getId()] = getColorInteger(color.getTextureDiffuseColors());
    }
  }

  /** Gets the color for the given dye */
  public static int getColor(DyeColor color) {
    return DYE_COLORS[color.getId()];
  }

  /**
   * Merge three float color components between 0 and 1 into a hex color integer
   * @param component float color component array, must be length 3
   * @return Color integer value
   */
  public static int getColorInteger(float[] component) {
    return ((int)(component[0] * 255) & 0xFF) << 16
           | ((int)(component[1] * 255) & 0xFF) << 8
           | ((int)(component[2] * 255) & 0xFF);
  }

  /**
   * Gets the dye color for the given color int
   * @param color Dye color input
   * @return EnumDyeColor matching, or null for no match
   */
  @Nullable
  public static DyeColor getDyeForColor(int color) {
    for (DyeColor dyeColor : DyeColor.values()) {
      if (getColor(dyeColor) == color) {
        return dyeColor;
      }
    }
    return null;
  }

  /**
   * Gets the color from the given stack
   * @param stack  Stack
   * @return  Stack color
   */
  public static int getColor(ItemStack stack) {
    Item item = stack.getItem();
    // use the interface if present
    if (item instanceof DyeableLeatherItem) {
      return ((DyeableLeatherItem) item).getColor(stack);
    }

    // default to NBT
    CompoundTag tags = stack.getTag();
    if (tags != null) {
      CompoundTag display = tags.getCompound(TAG_DISPLAY);
      if (display.contains(TAG_COLOR, Tag.TAG_INT)) {
        return display.getInt(TAG_COLOR);
      }
    }
    return -1;
  }

  /**
   * Checks if the stack currently has color
   * @param stack  Stack to check
   * @return  True if it has color
   */
  public static boolean hasColor(ItemStack stack) {
    Item item = stack.getItem();
    // use the interface if present
    if (item instanceof DyeableLeatherItem) {
      return ((DyeableLeatherItem) item).hasCustomColor(stack);
    }
    CompoundTag tags = stack.getTagElement(TAG_COLOR);
    return tags != null && tags.contains(TAG_COLOR, Tag.TAG_ANY_NUMERIC);
  }

  /**
   * Sets the color on the given stack
   * @param stack  Stack
   * @param color  Color to set
   * @return  Stack with color
   */
  public static ItemStack setColor(ItemStack stack, int color) {
    Item item = stack.getItem();
    // use the interface if present
    if (item instanceof DyeableLeatherItem) {
      ((DyeableLeatherItem) item).setColor(stack, color);
    } else {
      stack.getOrCreateTagElement(TAG_DISPLAY).putInt(TAG_COLOR, color);
    }
    return stack;
  }

  /**
   * Clears the color on a stack
   * @param stack  Stack instance
   * @return  Stack without color
   */
  public static ItemStack clearColor(ItemStack stack) {
    Item item = stack.getItem();
    // use the interface if present
    if (item instanceof DyeableLeatherItem) {
      ((DyeableLeatherItem) item).clearColor(stack);
    } else {
      CompoundTag displayTag = stack.getTagElement(TAG_DISPLAY);
      if (displayTag != null && displayTag.contains(TAG_COLOR)) {
        displayTag.remove(TAG_COLOR);
      }
    }
    return stack;
  }

  /**
   * Reads an iem from a string
   * @param parent  Parent object
   * @param key     JSON key of object
   * @return  Item
   * @throws JsonSyntaxException  If item is invalid or missing
   */
  public static Item deserializeItem(JsonObject parent, String key) {
    String name = GsonHelper.getAsString(parent, key);
    Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(name));
    if (item == null) {
      throw new JsonSyntaxException("Invalid " + key + ": Unknown item " + name + "'");
    }
    return item;
  }

  /**
   * Notifies render global that TE data changed requiring a block update.
   * Used since refreshModelData alone does not seem sufficient.
   * @param te  TE to update
   */
  public static void notifyClientUpdate(BlockEntity te) {
    Level world = te.getLevel();
    if (world != null && world.isClientSide) {
      te.requestModelDataUpdate();
      BlockState state = te.getBlockState();
      world.sendBlockUpdated(te.getBlockPos(), state, state, Block.UPDATE_NONE | Block.UPDATE_SUPPRESS_DROPS);
    }
  }
}
