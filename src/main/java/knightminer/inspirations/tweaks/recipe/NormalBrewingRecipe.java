package knightminer.inspirations.tweaks.recipe;

import knightminer.inspirations.common.IHidable;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.brewing.IBrewingRecipe;

import java.util.function.BooleanSupplier;

/**
 * A brewing recipe type which can be applied to all three potion item types.
 */
public class NormalBrewingRecipe implements IHidable, IBrewingRecipe {
  private final BooleanSupplier enabled;
  private final Potion start;
  private final Ingredient catalyst;
  private final Potion output;

  public NormalBrewingRecipe(Potion start, Ingredient catalyst, Potion output, BooleanSupplier enabledFunc) {
    this.start = start;
    this.catalyst = catalyst;
    this.output = output;
    this.enabled = enabledFunc;
  }

  @Override
  public boolean isEnabled() {
    return enabled.getAsBoolean();
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
