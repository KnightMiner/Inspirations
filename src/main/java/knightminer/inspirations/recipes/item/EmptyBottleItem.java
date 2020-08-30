package knightminer.inspirations.recipes.item;

import knightminer.inspirations.common.Config;
import knightminer.inspirations.common.IHidable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.GlassBottleItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionUtils;
import net.minecraft.potion.Potions;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

import java.util.function.Supplier;

/**
 * Item representing an empty potion bottle. Can be filled with water from a source block
 */
public class EmptyBottleItem extends GlassBottleItem implements IHidable {
  private final Supplier<Item> filled;

  /**
   * Creates a new bottle instance
   * @param properties  Item properties 
   * @param filled      Supplier returning the filled potion item
   */
  public EmptyBottleItem(Properties properties, Supplier<Item> filled) {
    super(properties);
    this.filled = filled;
  }

  @Override
  public boolean isEnabled() {
    return Config.enableCauldronPotions.getAsBoolean();
  }

  @Override
  public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
    if (shouldAddtoItemGroup(group)) {
      super.fillItemGroup(group, items);
    }
  }

  @Override
  public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
    ItemStack stack = player.getHeldItem(hand);

    // must hit a block
    BlockRayTraceResult trace = rayTrace(world, player, RayTraceContext.FluidMode.SOURCE_ONLY);
    if (trace.getType() == RayTraceResult.Type.BLOCK) {
      BlockPos pos = trace.getPos();
      if (!world.isBlockModifiable(player, pos)) {
        return ActionResult.resultPass(stack);
      }

      // if the block contains water, give filled stack
      if (world.getFluidState(pos).isTagged(FluidTags.WATER)) {
        world.playSound(player, player.getPosX(), player.getPosY(), player.getPosZ(), SoundEvents.ITEM_BOTTLE_FILL, SoundCategory.NEUTRAL, 1.0F, 1.0F);
        return ActionResult.func_233538_a_(this.turnBottleIntoItem(stack, player, PotionUtils.addPotionToItemStack(new ItemStack(filled.get()), Potions.WATER)), world.isRemote());
      }
    }

    return ActionResult.resultPass(stack);
  }
}
