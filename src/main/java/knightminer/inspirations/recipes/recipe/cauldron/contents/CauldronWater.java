package knightminer.inspirations.recipes.recipe.cauldron.contents;

import knightminer.inspirations.library.recipe.cauldron.CauldronContentTypes;
import knightminer.inspirations.library.recipe.cauldron.contents.ICauldronFluid;
import knightminer.inspirations.library.recipe.cauldron.contents.ICauldronPotion;
import knightminer.inspirations.library.recipe.cauldron.contenttype.CauldronContentType;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.potion.Potion;
import net.minecraft.potion.Potions;

/**
 * Class for the base water content type
 */
public class CauldronWater implements ICauldronFluid, ICauldronPotion {
  @Override
  public CauldronContentType<?> getType() {
    return CauldronContentTypes.WATER;
  }

  @Override
  public int getTintColor() {
    return -1;
  }

  @Override
  public Fluid getFluid() {
    return Fluids.WATER;
  }

  @Override
  public Potion getPotion() {
    return Potions.WATER;
  }
}
