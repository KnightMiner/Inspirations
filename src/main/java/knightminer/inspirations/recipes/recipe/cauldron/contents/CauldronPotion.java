package knightminer.inspirations.recipes.recipe.cauldron.contents;

import knightminer.inspirations.Inspirations;
import knightminer.inspirations.library.recipe.cauldron.CauldronContentTypes;
import knightminer.inspirations.library.recipe.cauldron.contents.ICauldronPotion;
import knightminer.inspirations.library.recipe.cauldron.contenttype.CauldronContentType;
import net.minecraft.potion.Potion;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IRegistryDelegate;

public class CauldronPotion implements ICauldronPotion {
  public static final ResourceLocation TEXTURE = Inspirations.getResource("block/fluid_potion");

  private final IRegistryDelegate<Potion> potion;
  public CauldronPotion(Potion potion) {
    this.potion = potion.delegate;
  }

  @Override
  public CauldronContentType<?> getType() {
    return CauldronContentTypes.POTION;
  }

  @Override
  public Potion getPotion() {
    return potion.get();
  }

  @Override
  public ResourceLocation getTextureName() {
    return TEXTURE;
  }
}
