package knightminer.inspirations.plugins.jei.cauldron;

import com.google.common.collect.ImmutableList;

import knightminer.inspirations.library.InspirationsRegistry;
import knightminer.inspirations.library.Util;
import knightminer.inspirations.library.recipe.cauldron.ISimpleCauldronRecipe;
import knightminer.inspirations.plugins.jei.JEIPlugin;
import knightminer.inspirations.plugins.jei.cauldron.ingredient.DyeIngredient;
import knightminer.inspirations.plugins.jei.cauldron.ingredient.PotionIngredient;
import knightminer.inspirations.recipes.block.BlockEnhancedCauldron.CauldronContents;
import net.minecraft.client.Minecraft;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionType;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import java.util.List;

import mezz.jei.api.ingredients.IIngredients;

public class CauldronRecipeWrapper implements ICauldronRecipeWrapper {

	private static final FluidStack WATER_STACK = new FluidStack(FluidRegistry.WATER, 1);

	protected final List<List<ItemStack>> input;
	protected final FluidStack inputFluid;
	protected final EnumDyeColor inputColor;
	protected final PotionType inputPotion;
	protected final int inputLevel;
	protected final boolean boiling;

	protected final List<ItemStack> output;
	protected final FluidStack outputFluid;
	protected final EnumDyeColor outputColor;
	protected final PotionType outputPotion;
	protected final int outputLevel;

	public CauldronRecipeWrapper(ISimpleCauldronRecipe recipe) {
		this.input = ImmutableList.of(recipe.getInput());
		this.inputLevel = MathHelper.clamp(recipe.getInputLevel(), 0, InspirationsRegistry.getCauldronMax());
		this.boiling = recipe.isBoiling();

		this.output = ImmutableList.of(recipe.getResult());
		this.outputLevel = MathHelper.clamp(recipe.getLevel(inputLevel), 0, InspirationsRegistry.getCauldronMax());

		// input state
		if(inputLevel == 0) {
			this.inputFluid = null;
			this.inputColor = null;
			this.inputPotion = null;
		} else {
			Object inputState = recipe.getInputState();
			this.inputFluid = (inputState instanceof Fluid) ? new FluidStack((Fluid) inputState, inputLevel) : null;
			this.inputColor = (inputState instanceof EnumDyeColor) ? (EnumDyeColor) inputState : null;
			this.inputPotion = (inputState instanceof PotionType) ? (PotionType) inputState : null;
		}

		// output state
		if(outputLevel == 0) {
			this.outputFluid = null;
			this.outputColor = null;
			this.outputPotion = null;
		} else {
			Object outputState = recipe.getState();
			this.outputFluid = (outputState instanceof Fluid) ? new FluidStack((Fluid) outputState, outputLevel) : null;
			this.outputColor = (outputState instanceof EnumDyeColor) ? (EnumDyeColor) outputState : null;
			this.outputPotion = (outputState instanceof PotionType) ? (PotionType) outputState : null;
		}
	}

	public CauldronRecipeWrapper(ItemStack input, ItemStack output) {
		this(ImmutableList.of(input), output);
	}

	public CauldronRecipeWrapper(List<ItemStack> input, ItemStack output) {
		this.input = ImmutableList.of(input);
		this.output = ImmutableList.of(output);

		this.inputLevel = 1;
		this.outputLevel = 0;
		this.boiling = false;

		this.inputFluid = WATER_STACK;
		this.outputFluid = null;
		this.inputColor = this.outputColor = null;
		this.inputPotion = this.outputPotion = null;
	}

	/* Getters used in the category */

	@Override
	public CauldronContents getInputType() {
		return getType(inputLevel, inputFluid, inputColor, inputPotion);
	}

	@Override
	public CauldronContents getOutputType() {
		return getType(outputLevel, outputFluid, outputColor, outputPotion);
	}

	@Override
	public int getInputLevel() {
		return inputLevel;
	}

	@Override
	public int getOutputLevel() {
		return outputLevel;
	}


	/* JEI methods */

	@Override
	public void getIngredients(IIngredients ingredients) {
		ingredients.setInputLists(ItemStack.class, input);
		if(inputFluid != null) {
			ingredients.setInput(FluidStack.class, inputFluid);
		}
		if(inputColor != null) {
			ingredients.setInput(DyeIngredient.class, new DyeIngredient(inputColor));
		}
		if(inputPotion != null) {
			ingredients.setInput(PotionIngredient.class, new PotionIngredient(inputPotion));
		}

		ingredients.setOutputs(ItemStack.class, output);
		if(outputFluid != null) {
			ingredients.setOutput(FluidStack.class, outputFluid);
		}
		if(outputColor != null) {
			ingredients.setOutput(DyeIngredient.class, new DyeIngredient(outputColor));
		}
		if(outputPotion != null) {
			ingredients.setOutput(PotionIngredient.class, new PotionIngredient(outputPotion));
		}
	}

	@Override
	public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
		if(boiling) {
			JEIPlugin.cauldron.fire.draw(minecraft, 45, 36);
		}
	}

	@Override
	public List<String> getTooltipStrings(int mouseX, int mouseY) {
		List<String> tooltip = ICauldronRecipeWrapper.super.getTooltipStrings(mouseX, mouseY);

		if(boiling && mouseX > 45 && mouseX <= 58 && mouseY > 36 && mouseY <= 48) {
			tooltip.add(Util.translate("gui.jei.cauldron.boiling"));
		}

		return tooltip;
	}


	/* Helpers */

	/**
	 * Quickly gets the type based on the given properties
	 */
	private static CauldronContents getType(int level, FluidStack fluid, EnumDyeColor color, PotionType potion) {
		if(level == 0) {
			return null;
		}
		if(fluid != null) {
			return CauldronContents.FLUID;
		}
		if(color != null) {
			return CauldronContents.DYE;
		}
		if(potion != null) {
			return CauldronContents.POTION;
		}

		return null;
	}
}
