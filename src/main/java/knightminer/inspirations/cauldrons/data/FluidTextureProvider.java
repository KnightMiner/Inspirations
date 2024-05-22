package knightminer.inspirations.cauldrons.data;

import knightminer.inspirations.Inspirations;
import knightminer.inspirations.cauldrons.InspirationsCaudrons;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fluids.FluidType;
import slimeknights.mantle.fluid.texture.AbstractFluidTextureProvider;
import slimeknights.mantle.fluid.texture.FluidTexture;

public class FluidTextureProvider extends AbstractFluidTextureProvider {
  private static final ResourceLocation COLORLESS = Inspirations.getResource("fluid/colorless_");
  private static final ResourceLocation TRANSPARENT = Inspirations.getResource("fluid/transparent_");
  public FluidTextureProvider(DataGenerator generator) {
    super(generator, Inspirations.modID);
  }

  @Override
  public void addTextures() {
    colorless(InspirationsCaudrons.potatoSoupType, 0xFFF2DA9F);
    colorless(InspirationsCaudrons.rabbitStewType, 0xFF984A2C);
    colorless(InspirationsCaudrons.beetrootSoupType, 0xFF84160D);
    colorless(InspirationsCaudrons.mushroomStewType, 0xFFCD8C6F);
    texture(InspirationsCaudrons.honeyType).textures(TRANSPARENT, false, false).color(0xFFFF9116);
  }

  private FluidTexture.Builder colorless(FluidType type, int color) {
    return texture(type).textures(COLORLESS, false, false).color(color);
  }

  @Override
  public String getName() {
    return "Inspirations Fluid Textures";
  }
}
