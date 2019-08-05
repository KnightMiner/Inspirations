package knightminer.inspirations.tweaks;

import knightminer.inspirations.common.Config;
import knightminer.inspirations.common.IHidable;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtils;
import net.minecraftforge.common.brewing.BrewingRecipe;

import javax.annotation.Nonnull;
import java.util.function.Supplier;

public class HidableBrewingRecipe extends BrewingRecipe implements IHidable {
    private final Supplier<Boolean> enabled;

	public HidableBrewingRecipe(Potion start, Ingredient reagent, Potion end) {
		this(start, reagent, end, Config.brewMissingPotions::get);
    }

	public HidableBrewingRecipe(Potion start, Ingredient reagent, Potion end, Supplier<Boolean> enableFunc) {
        this(
            Ingredient.fromStacks(PotionUtils.addPotionToItemStack(new ItemStack(Items.POTION), start)),
            reagent,
            PotionUtils.addPotionToItemStack(new ItemStack(Items.POTION), end),
            enableFunc
        );
    }

    public HidableBrewingRecipe(Ingredient input, Ingredient ingredient, ItemStack output, Supplier<Boolean> enableFunc) {
        super(input, ingredient, output);
        enabled = enableFunc;
    }

    @Override
    public boolean isEnabled() {
        return enabled.get();
    }

    @Override
    public boolean isInput(@Nonnull ItemStack ingredient) {
        return isEnabled() && super.isInput(ingredient);
    }

    @Override
    public boolean isIngredient(@Nonnull ItemStack ingredient) {
        return isEnabled() && super.isIngredient(ingredient);
    }
}
