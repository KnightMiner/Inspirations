package knightminer.inspirations.building.block.type;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.util.StringRepresentable;
import net.minecraftforge.registries.IRegistryDelegate;

import java.util.Locale;

/**
 * Variants for each of the mulch types
 */
public enum FlowerType implements StringRepresentable {
  CYAN(Items.CYAN_DYE),
  SYRINGA(Items.MAGENTA_DYE),
  PAEONIA(Items.PINK_DYE),
  ROSE(Items.RED_DYE);

  private final String name = name().toLowerCase(Locale.ROOT);
  private final IRegistryDelegate<Item> dye;

  FlowerType(Item dye) {
    this.dye = dye.delegate;
  }

  /**
   * Gets the dye for this color
   * @return Dye color
   */
  public Item getDye() {
    return dye.get();
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
