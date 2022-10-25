package knightminer.inspirations.building.tileentity;

import knightminer.inspirations.building.InspirationsBuilding;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import slimeknights.mantle.block.entity.RetexturedBlockEntity;

/**
 * Simply a wrapper around the base one to add in the custom type
 */
public class EnlightenedBushTileEntity extends RetexturedBlockEntity {
  public EnlightenedBushTileEntity(BlockPos pos, BlockState state) {
    super(InspirationsBuilding.enlightenedBushTileEntity, pos, state);
  }
}
