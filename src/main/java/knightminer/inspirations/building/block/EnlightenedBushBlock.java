package knightminer.inspirations.building.block;

import knightminer.inspirations.building.block.entity.EnlightenedBushBlockEntity;
import knightminer.inspirations.common.Config;
import knightminer.inspirations.common.IHidable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
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
  public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
    return new EnlightenedBushBlockEntity(pos, state);
  }

  @Override
  public boolean isEnabled() {
    return Config.enableEnlightenedBush.getAsBoolean();
  }

  /*
   * Properties
   */

  @SuppressWarnings("deprecation")
  @Deprecated
  @Override
  public VoxelShape getBlockSupportShape(BlockState p_230335_1_, BlockGetter p_230335_2_, BlockPos p_230335_3_) {
    return Shapes.empty();
  }
}
