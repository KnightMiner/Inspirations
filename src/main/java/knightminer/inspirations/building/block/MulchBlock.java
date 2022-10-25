package knightminer.inspirations.building.block;

import knightminer.inspirations.common.Config;
import knightminer.inspirations.common.IHidable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FallingBlock;
import net.minecraft.block.SaplingBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.PlantType;
import net.minecraftforge.common.ToolType;

import net.minecraft.block.AbstractBlock.Properties;

public class MulchBlock extends FallingBlock implements IHidable {

  private static final VoxelShape SHAPE = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 15.0D, 16.0D);

  public MulchBlock(MaterialColor color) {
    super(Properties.of(Material.WOOD, color)
                    .harvestTool(ToolType.SHOVEL)
                    .sound(SoundType.WET_GRASS)
                    .strength(0.6F)
         );
  }

  @Override
  public boolean isEnabled() {
    return Config.enableMulch.get();
  }

  @Override
  public void fillItemCategory(ItemGroup group, NonNullList<ItemStack> items) {
    if (shouldAddtoItemGroup(group)) {
      super.fillItemCategory(group, items);
    }
  }

  @SuppressWarnings("deprecation")
  @Deprecated
  @Override
  public VoxelShape getCollisionShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
    return SHAPE;
  }

  /*
   * Plants
   */
  @Override
  public boolean canSustainPlant(BlockState state, IBlockReader world, BlockPos pos, Direction direction, IPlantable plantable) {
    // we are fine with most plants, but saplings are a bit much
    // this is mostly cop out since I have no way of stopping sapling growth
    return plantable.getPlantType(world, pos.relative(direction)) == PlantType.PLAINS && !(plantable instanceof SaplingBlock);
  }
}
