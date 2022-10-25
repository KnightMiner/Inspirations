package knightminer.inspirations.building.block;

import knightminer.inspirations.building.tileentity.EnlightenedBushTileEntity;
import knightminer.inspirations.common.Config;
import knightminer.inspirations.common.IHidable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import slimeknights.mantle.block.RetexturedBlock;

import javax.annotation.Nullable;

public class EnlightenedBushBlock extends RetexturedBlock implements IHidable {
  private final int color;

  public EnlightenedBushBlock(int color) {
    super(Block.Properties.of(Material.LEAVES)
                          .lightLevel((state) -> 15)
                          .strength(0.2F)
                          .sound(SoundType.GRASS)
                          .noOcclusion()
         );
    this.color = color;
  }

  /**
   * Gets the color to tint this bush
   * @return  Bush tint color
   */
  public int getColor() {
    return color;
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
  public VoxelShape getBlockSupportShape(BlockState p_230335_1_, IBlockReader p_230335_2_, BlockPos p_230335_3_) {
    return VoxelShapes.empty();
  }
}
