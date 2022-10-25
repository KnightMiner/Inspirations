package knightminer.inspirations.utility.block;

import knightminer.inspirations.shared.InspirationsShared;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.PressurePlateBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class CarpetedPressurePlateBlock extends PressurePlateBlock {
  private static final VoxelShape PRESSED_AABB = Shapes.or(
      Block.box(0, 0, 0, 16, 1, 16),
      Block.box(1, 1, 1, 15, 1.25, 15));
  private static final VoxelShape UNPRESSED_AABB = Shapes.or(
      Block.box(0, 0, 0, 16, 1, 16),
      Block.box(1, 1, 1, 15, 1.5, 15));

  private final DyeColor color;
  private final String transKey;

  public CarpetedPressurePlateBlock(DyeColor color) {
    super(Sensitivity.MOBS, Block.Properties.of(Material.CLOTH_DECORATION, color)
                                            .strength(0.5F)
                                            .sound(SoundType.WOOL)
         );
    this.color = color;
    this.transKey = String.format("block.minecraft.%s_carpet", color.getName());
  }

  @Override
  public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
    boolean flag = this.getSignalForState(state) > 0;
    return flag ? PRESSED_AABB : UNPRESSED_AABB;
  }

  @Override
  public String getDescriptionId() {
    // Use the name of the carpet on top for the translation key.
    // This should never be seen normally, but other mods might display it
    // so ensure it's a valid value.
    return transKey;
  }

  /**
   * Gets the carpet block that cooresponds to this block
   * @return Carpet block
   */
  public ItemLike getCarpet() {
    return InspirationsShared.VANILLA_CARPETS.get(color);
  }

  @Override
  public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter level, BlockPos pos, Player player) {
    return new ItemStack(getCarpet());
  }
}
