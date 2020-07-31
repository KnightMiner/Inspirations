package knightminer.inspirations.building.block;

import knightminer.inspirations.building.tileentity.EnlightenedBushTileEntity;
import knightminer.inspirations.common.Config;
import knightminer.inspirations.common.IHidable;
import knightminer.inspirations.library.util.TextureBlockUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class EnlightenedBushBlock extends Block implements IHidable {
  private final int color;

  public EnlightenedBushBlock(int color) {
    super(Block.Properties.create(Material.LEAVES)
                          .setLightLevel((state) -> 15)
                          .hardnessAndResistance(0.2F)
                          .sound(SoundType.PLANT)
                          .notSolid()
         );
    this.color = color;
  }

  public int getColor() {
    return color;
  }

  @Override
  public boolean hasTileEntity(BlockState state) {
    return true;
  }

  @Nullable
  @Override
  public TileEntity createTileEntity(BlockState state, IBlockReader world) {
    return new EnlightenedBushTileEntity();
  }

  @Override
  public boolean isEnabled() {
    return Config.enableEnlightenedBush.get();
  }

  /*
   * Properties
   */

  @SuppressWarnings("deprecation")
  @Deprecated
  @Override
  public VoxelShape getCollisionShape(BlockState p_230335_1_, IBlockReader p_230335_2_, BlockPos p_230335_3_) {
    return VoxelShapes.empty();
  }


  /*
   * Texturing
   */

  @Override
  public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
    super.onBlockPlacedBy(world, pos, state, placer, stack);
    TextureBlockUtil.updateTextureBlock(world, pos, stack);
  }

  @Override
  public ItemStack getPickBlock(BlockState state, RayTraceResult target, IBlockReader world, BlockPos pos, PlayerEntity player) {
    return TextureBlockUtil.getPickBlock(world, pos, state);
  }
}
