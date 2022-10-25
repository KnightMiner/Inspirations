package knightminer.inspirations.tweaks.recipe;

import knightminer.inspirations.common.IHidable;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtils;
import net.minecraftforge.common.brewing.IBrewingRecipe;

import java.util.function.Supplier;

/**
 * A brewing recipe type which can be applied to all three potion item types.
 */
public class NormalBrewingRecipe implements IHidable, IBrewingRecipe {
  private final Supplier<Boolean> enabled;
  private final Potion start;
  private final Ingredient catalyst;
  private final Potion output;

  public NormalBrewingRecipe(Potion start, Ingredient catalyst, Potion output, Supplier<Boolean> enabledFunc) {
    this.start = start;
    this.catalyst = catalyst;
    this.output = output;
    this.enabled = enabledFunc;
  }

  @Override
  public boolean isEnabled() {
    return enabled.get();
  }

  public Ingredient getCatalyst() {
    return catalyst;
  }

  public Potion getStart() {
    return start;
  }

  public Potion getOutput() {
    return output;
  }

  @Override
  public boolean isInput(ItemStack input) {
    Item item = input.getItem();
    if (item == Items.POTION || item == Items.SPLASH_POTION || item == Items.LINGERING_POTION) {
      return PotionUtils.getPotion(input) == start;
    }
    return false;
  }

  @Override
  public boolean isIngredient(ItemStack ingredient) {
    return catalyst.test(ingredient);
  }

  @Override
  public ItemStack getOutput(ItemStack input, ItemStack ingredient) {
    if (!isEnabled()) {
      return ItemStack.EMPTY;
    }
    if (!catalyst.test(ingredient)) {
      return ItemStack.EMPTY;
    }
    Item item = input.getItem();
    if (isInput(input)) {
      return PotionUtils.setPotion(new ItemStack(item), output);
    }
    return ItemStack.EMPTY;
  }
}
