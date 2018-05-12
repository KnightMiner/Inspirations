package knightminer.inspirations.plugins.jei.cauldron;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;

import knightminer.inspirations.plugins.jei.cauldron.ingredient.DyeIngredient;
import knightminer.inspirations.plugins.jei.cauldron.ingredient.DyeIngredientHelper;
import knightminer.inspirations.recipes.InspirationsRecipes;
import knightminer.inspirations.recipes.block.BlockEnhancedCauldron.CauldronContents;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;

public class DyeFillWrapper implements ICauldronRecipeWrapper {

	private static List<List<DyeIngredient>> dyes = ImmutableList.of(DyeIngredientHelper.ALL_DYES);
	private static List<List<ItemStack>> filled = ImmutableList.of(
			Arrays.stream(EnumDyeColor.values())
			.map(color -> new ItemStack(InspirationsRecipes.dyedWaterBottle, 1, color.getDyeDamage()))
			.collect(Collectors.toList()));

	private static ItemStack bottle = new ItemStack(Items.GLASS_BOTTLE);
	private boolean fill;

	public DyeFillWrapper(boolean fill) {
		this.fill = fill;
	}

	@Override
	public void getIngredients(IIngredients ingredients) {
		if(fill) {
			ingredients.setInput(ItemStack.class, bottle);
			ingredients.setOutputLists(ItemStack.class, filled);
			ingredients.setInputLists(DyeIngredient.class, dyes);
		} else {
			ingredients.setOutput(ItemStack.class, bottle);
			ingredients.setInputLists(ItemStack.class, filled);
			ingredients.setOutputLists(DyeIngredient.class, dyes);
		}
	}

	@Override
	public CauldronContents getInputType() {
		return CauldronContents.DYE;
	}

	@Override
	public CauldronContents getOutputType() {
		return CauldronContents.DYE;
	}

	@Override
	public int getInputLevel() {
		return fill ? 1 : 0;
	}

	@Override
	public int getOutputLevel() {
		return fill ? 0 : 1;
	}
}
