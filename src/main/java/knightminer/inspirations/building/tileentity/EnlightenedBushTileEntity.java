package knightminer.inspirations.building.tileentity;

import knightminer.inspirations.building.InspirationsBuilding;
import slimeknights.mantle.tileentity.RetexturedTileEntity;

/**
 * Simply a wrapper around the base one to add in the custom type
 */
public class EnlightenedBushTileEntity extends RetexturedTileEntity {
  public EnlightenedBushTileEntity() {
    super(InspirationsBuilding.enlightenedBushTileEntity);
  }
}
