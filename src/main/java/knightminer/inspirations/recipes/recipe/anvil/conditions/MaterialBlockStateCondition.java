package knightminer.inspirations.recipes.recipe.anvil.conditions;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import knightminer.inspirations.library.Util;
import knightminer.inspirations.recipes.recipe.anvil.IBlockStateCondition;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Condition that is true if the material of the block matches.
 */
public class MaterialBlockStateCondition implements IBlockStateCondition {
  private final MaterialWrapper[] materialExpected;

  public MaterialBlockStateCondition(@Nonnull MaterialWrapper materialExpected) {
    this(new MaterialWrapper[]{materialExpected});
  }

  public MaterialBlockStateCondition(@Nonnull MaterialWrapper[] materialExpected) {
    this.materialExpected = materialExpected;
  }

  @Override
  public boolean matches(World world, BlockPos pos, @Nonnull IBlockState state) {
    Material material = state.getMaterial();
    return Arrays.stream(this.materialExpected).anyMatch(material::equals);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    MaterialBlockStateCondition that = (MaterialBlockStateCondition) o;
    return Arrays.equals(materialExpected, that.materialExpected);
  }

  @Override
  public int hashCode() {
    return Arrays.hashCode(materialExpected);
  }

  @Override
  public String getTooltip() {
    String materialList = Arrays.stream(materialExpected)
        .map(MaterialWrapper::getName)
        .collect(Collectors.joining(", "));
    return Util.translateFormatted("gui.jei.anvil_smashing.condition.material", materialList);
  }

  public static MaterialBlockStateCondition fromConfig(@Nonnull String config) {
    String[] materials = config.split(",");
    MaterialBlockStateCondition.MaterialWrapper[] materialWrappers = Arrays.stream(materials)
            .map(MaterialBlockStateCondition.MaterialWrapper::fromName)
            .toArray(MaterialBlockStateCondition.MaterialWrapper[]::new);
    return new MaterialBlockStateCondition(materialWrappers);
  }

  public static class MaterialWrapper {
    private static final String defaultTranslationKeyPrefix = "gui.jei.anvil_smashing.materials.";
    private Material material;
    private String key;

    public MaterialWrapper(Material material, String key) {
      this.material = material;
      this.key = key;
    }

    public Material getMaterial() {
      return material;
    }

    public String getName() {
      return Util.translate(key);
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      MaterialWrapper that = (MaterialWrapper) o;
      return Objects.equals(getMaterial(), that.getMaterial()) &&
          Objects.equals(getName(), that.getName());
    }

    @Override
    public int hashCode() {
      return Objects.hash(getMaterial(), getName());
    }


    private static BiMap<String, Material> vanillaMaterialKeyMap;

    static {
      vanillaMaterialKeyMap = HashBiMap.create();

      vanillaMaterialKeyMap.put("air", Material.AIR);
      vanillaMaterialKeyMap.put("grass", Material.GRASS);
      vanillaMaterialKeyMap.put("ground", Material.GROUND);
      vanillaMaterialKeyMap.put("wood", Material.WOOD);
      vanillaMaterialKeyMap.put("rock", Material.ROCK);
      vanillaMaterialKeyMap.put("iron", Material.IRON);
      vanillaMaterialKeyMap.put("anvil", Material.ANVIL);
      vanillaMaterialKeyMap.put("water", Material.WATER);
      vanillaMaterialKeyMap.put("lava", Material.LAVA);
      vanillaMaterialKeyMap.put("leaves", Material.LEAVES);
      vanillaMaterialKeyMap.put("plants", Material.PLANTS);
      vanillaMaterialKeyMap.put("vine", Material.VINE);
      vanillaMaterialKeyMap.put("sponge", Material.SPONGE);
      vanillaMaterialKeyMap.put("cloth", Material.CLOTH);
      vanillaMaterialKeyMap.put("fire", Material.FIRE);
      vanillaMaterialKeyMap.put("sand", Material.SAND);
      vanillaMaterialKeyMap.put("circuits", Material.CIRCUITS);
      vanillaMaterialKeyMap.put("carpet", Material.CARPET);
      vanillaMaterialKeyMap.put("glass", Material.GLASS);
      vanillaMaterialKeyMap.put("redstone_light", Material.REDSTONE_LIGHT);
      vanillaMaterialKeyMap.put("tnt", Material.TNT);
      vanillaMaterialKeyMap.put("coral", Material.CORAL);
      vanillaMaterialKeyMap.put("ice", Material.ICE);
      vanillaMaterialKeyMap.put("packed_ice", Material.PACKED_ICE);
      vanillaMaterialKeyMap.put("snow", Material.SNOW);
      vanillaMaterialKeyMap.put("crafted_snow", Material.CRAFTED_SNOW);
      vanillaMaterialKeyMap.put("cactus", Material.CACTUS);
      vanillaMaterialKeyMap.put("clay", Material.CLAY);
      vanillaMaterialKeyMap.put("gourd", Material.GOURD);
      vanillaMaterialKeyMap.put("dragon_egg", Material.DRAGON_EGG);
      vanillaMaterialKeyMap.put("portal", Material.PORTAL);
      vanillaMaterialKeyMap.put("cake", Material.CAKE);
      vanillaMaterialKeyMap.put("web", Material.WEB);
      vanillaMaterialKeyMap.put("piston", Material.PISTON);
      vanillaMaterialKeyMap.put("barrier", Material.BARRIER);
      vanillaMaterialKeyMap.put("structure_void", Material.STRUCTURE_VOID);
    }

    public static MaterialWrapper fromName(String name) {
      String key = name.toLowerCase();
      Material material = vanillaMaterialKeyMap.get(key);
      if (material == null) {
        return null;
      }

      return new MaterialWrapper(material, defaultTranslationKeyPrefix + key);
    }

    public static MaterialWrapper fromMaterial(Material material) {
      String key = vanillaMaterialKeyMap.inverse().get(material);
      if (key == null) {
        key = "unknown";
      }

      return new MaterialWrapper(material, defaultTranslationKeyPrefix + key);
    }
  }
}
