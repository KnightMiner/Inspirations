package knightminer.inspirations.building.block.type;

import net.minecraft.block.Block;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;

import java.util.Locale;

/**
 * Variants for each of the mulch types
 */
public enum PathType implements IStringSerializable {
  // There's multiple variants for these, just use a square
  ROCK(MaterialColor.STONE, Block.box(.5, 0, .5, 15.5, 1, 15.5)),
  ROUND(MaterialColor.STONE, VoxelShapes.or(
      Block.box(1, 0, 5, 15, 1, 11),
      Block.box(5, 0, 1, 11, 1, 15),
      Block.box(2, 0, 3, 14, 1, 13),
      Block.box(3, 0, 2, 13, 1, 14)).optimize()),
  TILE(MaterialColor.STONE, VoxelShapes.or(
      Block.box(1, 0, 1, 7, 1, 7),
      Block.box(9, 0, 1, 15, 1, 7),
      Block.box(9, 0, 9, 15, 1, 15),
      Block.box(1, 0, 9, 7, 1, 15))),
  BRICK(MaterialColor.COLOR_RED, VoxelShapes.or(
      Block.box(0, 0, 0, 3, 1, 3),
      Block.box(4, 0, 0, 7, 1, 7),
      Block.box(0, 0, 4, 3, 1, 11),
      Block.box(12, 0, 8, 15, 1, 15),
      Block.box(8, 0, 0, 11, 1, 3),
      Block.box(8, 0, 12, 11, 1, 16),
      Block.box(12, 0, 0, 16, 1, 3),
      Block.box(8, 0, 4, 15, 1, 7),
      Block.box(4, 0, 8, 11, 1, 11),
      Block.box(0, 0, 12, 7, 1, 15)));

  private final String name = name().toLowerCase(Locale.ROOT);
  private final MaterialColor color;
  private final VoxelShape shape;

  PathType(MaterialColor color, VoxelShape shape) {
    this.color = color;
    this.shape = shape;
  }

  /**
   * Gets the material color for this mulch type
   * @return Material color
   */
  public MaterialColor getColor() {
    return this.color;
  }

  /**
   * Gets the shape for this path type
   * @return Path shape
   */
  public VoxelShape getShape() {
    return this.shape;
  }

  @Override
  public String getSerializedName() {
    return name;
  }

  @Override
  public String toString() {
    return name;
  }
}
