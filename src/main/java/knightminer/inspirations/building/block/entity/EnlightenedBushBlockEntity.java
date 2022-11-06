package knightminer.inspirations.building.block.entity;

import knightminer.inspirations.building.InspirationsBuilding;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import slimeknights.mantle.block.entity.DefaultRetexturedBlockEntity;

/**
 * Simply a wrapper around the base one to add in the custom type
 */
public class EnlightenedBushBlockEntity extends DefaultRetexturedBlockEntity {
  public EnlightenedBushBlockEntity(BlockPos pos, BlockState state) {
    super(InspirationsBuilding.enlightenedBushTileEntity, pos, state);
  }
}
