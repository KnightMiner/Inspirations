package knightminer.inspirations.library.recipe.cauldron.util;

/**
 * Enum containing valid cauldron temperatures
 * @deprecated No idea if temperature will still be possible, perhaps dedicated cauldrons?
 */
@Deprecated
public enum CauldronTemperature {
  /** Cauldron is at room temperature */
  NORMAL,
  /** Cauldron is heated by a fire */
  BOILING,
  /** Cauldron is in a cold biome or sorrunded by ice */
  FREEZING;
}
