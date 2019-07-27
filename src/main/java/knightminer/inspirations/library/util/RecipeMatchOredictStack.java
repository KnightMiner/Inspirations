package knightminer.inspirations.library.util;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import slimeknights.mantle.util.RecipeMatch;

import java.util.List;
import java.util.stream.Collectors;

/** Normal oredict assumes we want stacksize of 1 separated over slots, we want specific size */
public class RecipeMatchOredictStack extends RecipeMatch.Oredict {

  private final List<ItemStack> oredictEntry;
  public RecipeMatchOredictStack(String oredictEntry, int amountNeeded) {
    this(oredictEntry, amountNeeded, 1);
  }

  public RecipeMatchOredictStack(String oredictEntry, int amountNeeded, int amountMatched) {
    super(oredictEntry, amountNeeded, amountMatched);
    this.oredictEntry = OreDictionary.getOres(oredictEntry);
  }

  @Override
  public List<ItemStack> getInputs() {
    // transforms the stack list into a list with counts
    return oredictEntry.stream().map((s) -> {
      s = s.copy();
      s.setCount(amountNeeded);
      return s;
    }).collect(Collectors.toList());
  }
}
