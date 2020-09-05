package knightminer.inspirations.plugins.jei.cauldron;

import com.mojang.blaze3d.matrix.MatrixStack;
import knightminer.inspirations.Inspirations;
import knightminer.inspirations.library.recipe.cauldron.contents.ICauldronContents;
import knightminer.inspirations.library.recipe.cauldron.recipe.ICauldronRecipeDisplay;
import knightminer.inspirations.library.recipe.cauldron.util.TemperaturePredicate;
import knightminer.inspirations.plugins.jei.JEIPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.drawable.IDrawableAnimated.StartDirection;
import mezz.jei.api.gui.ingredient.IGuiFluidStackGroup;
import mezz.jei.api.gui.ingredient.IGuiIngredientGroup;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.ForgeI18n;

import java.awt.Color;
import java.util.Collections;
import java.util.List;

/**
 * Implementation to show cauldron recipes in JEI
 * TODO: time string
 * TODO: freezing and boiling
 * TODO: time arrow
 */
public class CauldronCategory implements IRecipeCategory<ICauldronRecipeDisplay> {
  /** Unique ID for this category */
  public static final ResourceLocation ID = Inspirations.getResource("cauldron");
  private static final ResourceLocation BACKGROUND_LOC = Inspirations.getResource("textures/gui/jei/cauldron.png");
  /** Base translation key for all JEI string */
  public static final String TRANSLATION_KEY = Util.makeTranslationKey("jei", ID);
  private static final String KEY_TIME = TRANSLATION_KEY + ".time";
  private static final String KEY_BOILING = TRANSLATION_KEY + ".boiling";
  private static final String KEY_WARM = TRANSLATION_KEY + ".warm";
  private static final String KEY_FREEZING = TRANSLATION_KEY + ".freezing";
  private static final String KEY_COOL = TRANSLATION_KEY + ".cool";
  private static final String KEY_NORMAL = TRANSLATION_KEY + ".normal";

  private static final int SIZE = 16;

  private final String title = ForgeI18n.getPattern(TRANSLATION_KEY);
  private final IDrawable background;
  private final IDrawable icon;
  private final IDrawable boiling, warm, cool, freezing;
  private final IDrawableAnimated arrow;
  public CauldronCategory(IGuiHelper helper) {
    this.background = helper.drawableBuilder(BACKGROUND_LOC, 0, 0, 86, 51).addPadding(0, 0, 0, 8).build();
    this.icon = helper.createDrawableIngredient(new ItemStack(Blocks.CAULDRON));
    // temperature icons
    this.boiling = helper.createDrawable(BACKGROUND_LOC, 86, 0, 14, 14);
    this.cool = helper.createDrawable(BACKGROUND_LOC, 100, 0, 14, 14);
    this.freezing = helper.createDrawable(BACKGROUND_LOC, 86, 14, 8, 16);
    this.warm = helper.createDrawable(BACKGROUND_LOC, 94, 14, 8, 16);
    this.arrow = helper.drawableBuilder(BACKGROUND_LOC, 86, 30, 24, 17).buildAnimated(200, StartDirection.LEFT, false);
  }

  /* Basic */

  @Override
  public ResourceLocation getUid() {
    return ID;
  }

  @Override
  public String getTitle() {
    return title;
  }

  @Override
  public IDrawable getBackground() {
    return background;
  }

  @Override
  public IDrawable getIcon() {
    return icon;
  }

  @Override
  public Class<? extends ICauldronRecipeDisplay> getRecipeClass() {
    return ICauldronRecipeDisplay.class;
  }


  /* Recipe behavior */

  @Override
  public void setIngredients(ICauldronRecipeDisplay recipe, IIngredients ingredients) {
    // items
    ingredients.setInputLists(VanillaTypes.ITEM, Collections.singletonList(recipe.getItemInputs()));
    ItemStack outputItem = recipe.getItemOutput();
    if (!outputItem.isEmpty()) {
      ingredients.setOutput(VanillaTypes.ITEM, outputItem);
    }

    // contents - set fluid if available
    if (recipe.getLevelInput() > 0) {
      List<FluidStack> fluids = recipe.getFluidInputs();
      if (fluids.isEmpty()) {
        ingredients.setInputLists(JEIPlugin.CAULDRON_CONTENTS, Collections.singletonList(recipe.getContentInputs()));
      } else {
        ingredients.setInputLists(VanillaTypes.FLUID, Collections.singletonList(fluids));
      }
    }
    if (recipe.getLevelOutput() > 0) {
      FluidStack outputFluid = recipe.getFluidOutput();
      if (outputFluid.isEmpty()) {
        ingredients.setOutput(JEIPlugin.CAULDRON_CONTENTS, recipe.getContentOutput());
      } else {
        ingredients.setOutput(VanillaTypes.FLUID, outputFluid);
      }
    }
  }

  @Override
  public void setRecipe(IRecipeLayout layout, ICauldronRecipeDisplay recipe, IIngredients ingredients) {
    // get ingredient types
    IGuiItemStackGroup items = layout.getItemStacks();
    IGuiFluidStackGroup fluids = layout.getFluidStacks();
    IGuiIngredientGroup<ICauldronContents> contents = layout.getIngredientsGroup(JEIPlugin.CAULDRON_CONTENTS);
    // add callbacks and ingredients
    //fluids.addTooltipCallback(FluidTooltipHandler.getCallback(input, output));

    // items
    items.init(0, true, 9, 1);
    if (!recipe.getItemOutput().isEmpty()) {
      items.init(1, false, 67, 1);
    }

    // contents inputs
    int input = recipe.getLevelInput();
    if (input > 0) {
      if (recipe.getFluidInputs().isEmpty()) {
        contents.init(0, true, CauldronRenderer.contentLevel(input), 10, 18, SIZE, 14, 3, 2);
      } else {
        fluids.init(0, true, CauldronRenderer.fluidLevel(input), 10, 18, SIZE, 14, 3, 2);
      }
    }

    // contents outputs
    int output = recipe.getLevelOutput();
    if (output > 0) {
      if (recipe.getFluidOutput().isEmpty()) {
        contents.init(1, false, CauldronRenderer.contentLevel(output), 68, 18, SIZE, 14, 3, 2);
      } else {
        fluids.init(1, false, CauldronRenderer.fluidLevel(output), 68, 18, SIZE, 14, 3, 2);
      }
    }

    items.set(ingredients);
    fluids.set(ingredients);
    contents.set(ingredients);
  }


  /* Boiling, freezing, and time */

  @Override
  public void draw(ICauldronRecipeDisplay recipe, MatrixStack matrices, double mouseX, double mouseY) {
    int time = recipe.getTime();
    if (time > 0) {
      String timeString = I18n.format(KEY_TIME, time / 20);
      FontRenderer fontRenderer = Minecraft.getInstance().fontRenderer;
      int x = 46 - fontRenderer.getStringWidth(timeString) / 2;
      fontRenderer.drawString(matrices, timeString, x, 4, Color.GRAY.getRGB());
      // draw animated arrow icon
      arrow.draw(matrices, 35, 17);
    }

    // draw temperature icons
    switch(recipe.getTemperature()) {
      case BOILING:
        boiling.draw(matrices, 11, 35);
        break;
      case WARM:
        warm.draw(matrices, 2, 18);
        warm.draw(matrices, 26, 18);
        break;
      case FREEZING:
        freezing.draw(matrices, 2, 18);
        freezing.draw(matrices, 26, 18);
        break;
      case COOL:
        cool.draw(matrices, 11, 35);
        break;
      case NORMAL:
        warm.draw(matrices, 2, 18);
        warm.draw(matrices, 26, 18);
        cool.draw(matrices, 11, 35);
        break;
    }
  }

  @Override
  public List<ITextComponent> getTooltipStrings(ICauldronRecipeDisplay recipe, double mouseX, double mouseY) {
    String tooltip = null;
    TemperaturePredicate temperature = recipe.getTemperature();
    // boiling fire
    if (mouseX >= 11 && mouseX < 25 && mouseY >= 35 && mouseY < 49) {
      if (temperature == TemperaturePredicate.BOILING) {
        tooltip = KEY_BOILING;
      } else if (temperature == TemperaturePredicate.COOL) {
        tooltip = KEY_COOL;
      } else if (temperature == TemperaturePredicate.NORMAL) {
        tooltip = KEY_NORMAL;
      }
    }
    // freezing ice
    if (tooltip == null) {
      if ((mouseX >= 2 && mouseX < 10 && mouseY >= 18 && mouseY < 34) || (mouseX >= 26 && mouseX < 34 && mouseY >= 18 && mouseY < 34)) {
        if (temperature == TemperaturePredicate.FREEZING) {
          tooltip = KEY_FREEZING;
        } else if (temperature == TemperaturePredicate.WARM) {
          tooltip = KEY_WARM;
        } else if (temperature == TemperaturePredicate.NORMAL) {
          tooltip = KEY_NORMAL;
        }
      }
    }

    // return tooltip if we found one
    if (tooltip != null) {
      return Collections.singletonList(new TranslationTextComponent(tooltip));
    }
    return Collections.emptyList();
  }
}
