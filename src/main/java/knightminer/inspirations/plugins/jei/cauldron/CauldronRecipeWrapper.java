package knightminer.inspirations.plugins.jei.cauldron;

import com.google.common.collect.ImmutableList;

import knightminer.inspirations.Inspirations;
import knightminer.inspirations.library.Util;
import knightminer.inspirations.library.recipe.cauldron.ISimpleCauldronRecipe;
import knightminer.inspirations.plugins.jei.JEIPlugin;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionType;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.List;

import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeWrapper;

public class CauldronRecipeWrapper implements IRecipeWrapper {

	private static final FluidStack WATER_INPUT = getFluid(FluidRegistry.WATER, 3);
	private static final FluidStack WATER_OUTPUT = getFluid(FluidRegistry.WATER, 2);

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
		this.inputLevel = recipe.getInputLevel();
		this.boiling = recipe.isBoiling();

		this.output = ImmutableList.of(recipe.getResult());
		this.outputLevel = recipe.getLevel(inputLevel);

		// input state
		Object inputState = recipe.getInputState();
		this.inputFluid = (inputState instanceof Fluid) ? getFluid((Fluid) inputState, inputLevel) : null;
		this.inputColor = (inputState instanceof EnumDyeColor) ? (EnumDyeColor) inputState : null;
		this.inputPotion = (inputState instanceof PotionType) ? (PotionType) inputState : null;

		// output state
		Object outputState = recipe.getState();
		this.outputFluid = (outputState instanceof Fluid) ? getFluid((Fluid) outputState, outputLevel) : null;
		this.outputColor = (outputState instanceof EnumDyeColor) ? (EnumDyeColor) outputState : null;
		this.outputPotion = (outputState instanceof PotionType) ? (PotionType) outputState : null;
	}

	public CauldronRecipeWrapper(ItemStack input, ItemStack output) {
		this(ImmutableList.of(input), output);
	}

	public CauldronRecipeWrapper(List<ItemStack> input, ItemStack output) {
		this.input = ImmutableList.of(input);
		this.output = ImmutableList.of(output);

		this.inputLevel = 3;
		this.outputLevel = 2;
		this.boiling = false;

		this.inputFluid = WATER_INPUT;
		this.outputFluid = WATER_OUTPUT;
		this.inputColor = this.outputColor = null;
		this.inputPotion = this.outputPotion = null;
	}

	public boolean hasInputFluid() {
		return this.inputFluid != null;
	}

	public boolean hasOutputFluid() {
		return this.outputFluid != null;
	}

	@Override
	public void getIngredients(IIngredients ingredients) {
		ingredients.setInputLists(ItemStack.class, input);
		ingredients.setOutputs(ItemStack.class, output);

		ingredients.setInput(FluidStack.class, inputFluid);
		ingredients.setOutput(FluidStack.class, outputFluid);
	}

	@Override
	public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
		if(boiling) {
			JEIPlugin.cauldron.fire.draw(minecraft, 45, 42);
		}
		drawIngredient(minecraft, inputColor, inputPotion, inputLevel, 47, 25);
		drawIngredient(minecraft, outputColor, outputPotion, outputLevel, 101, 25);
	}

	@Override
	public List<String> getTooltipStrings(int mouseX, int mouseY) {
		List<String> tooltip = new ArrayList<>();
		addStringTooltip(tooltip, inputColor, inputPotion, inputLevel, 47, 25, mouseX, mouseY);
		addStringTooltip(tooltip, outputColor, outputPotion, outputLevel, 101, 25, mouseX, mouseY);
		if(boiling && mouseX > 45 && mouseX <= 58 && mouseY > 36 && mouseY <= 48) {
			tooltip.add(Util.translate("gui.jei.cauldron.boiling"));
		}

		return tooltip;
	}


	/* Helpers */

	private static FluidStack getFluid(Fluid fluid, int level) {
		if(level == 0) {
			return null;
		}

		return new FluidStack(fluid, padLevel(level));
	}

	private static int padLevel(int level) {
		return level * 333 + (level > 1 ? 1 : 0);
	}

	private static void drawIngredient(Minecraft minecraft, EnumDyeColor color, PotionType potion, int level, int x, int y) {
		if(color == null && potion == null) {
			return;
		}

		float[] colors;
		if(color != null) {
			colors = color.getColorComponentValues();
		} else {
			colors = Util.getColorComponents(PotionUtils.getPotionColor(potion));
		}
		GlStateManager.color(colors[0], colors[1], colors[2]);

		getFluidIcon(color != null, level - 1).draw(minecraft, x, y + (3 * (3 - level)));
		GlStateManager.color(1f, 1f, 1f);
	}

	private static IDrawable getFluidIcon(boolean dye, int index) {
		return dye ? JEIPlugin.cauldron.dye[index] : JEIPlugin.cauldron.potion[index];
	}

	private static void addStringTooltip(List<String> tooltips, EnumDyeColor color, PotionType potion, int level, int x, int y, int mouseX, int mouseY) {
		if(mouseX >= x && mouseX < x + 16 && mouseY >= y && mouseY < y + 16) {
			if(level == 0) {
				tooltips.add(Util.translateFormatted("gui.jei.cauldron.level.empty"));
				return;
			} else if(color != null) {
				tooltips.add(Util.translateFormatted("gui.jei.cauldron.color", Util.translate("item.fireworksCharge.%s", color.getUnlocalizedName())));
			} else if(potion != null) {
				tooltips.add(Util.translate(potion.getNamePrefixed("potion.effect.")));
				Util.addPotionTooltip(potion, tooltips);
			} else {
				return;
			}
			tooltips.add(TextFormatting.GRAY + Util.translateFormatted("gui.jei.cauldron.level.filled", padLevel(level)));
			tooltips.add(TextFormatting.BLUE + "" + TextFormatting.ITALIC + Inspirations.modName);
		}
	}
}
