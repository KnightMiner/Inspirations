package knightminer.inspirations.utility.block;

import knightminer.inspirations.shared.InspirationsShared;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.PressurePlateBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;

import net.minecraft.block.PressurePlateBlock.Sensitivity;

public class CarpetedPressurePlateBlock extends PressurePlateBlock {
  private static final VoxelShape PRESSED_AABB = VoxelShapes.or(
      Block.box(0, 0, 0, 16, 1, 16),
      Block.box(1, 1, 1, 15, 1.25, 15));
  private static final VoxelShape UNPRESSED_AABB = VoxelShapes.or(
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
  public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
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
  public IItemProvider getCarpet() {
    return InspirationsShared.VANILLA_CARPETS.get(color);
  }

  @Override
  public ItemStack getPickBlock(BlockState state, RayTraceResult target, IBlockReader world, BlockPos pos, PlayerEntity player) {
    return new ItemStack(getCarpet());
  }
}
