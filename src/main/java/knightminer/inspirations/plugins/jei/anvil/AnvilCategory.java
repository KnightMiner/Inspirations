package knightminer.inspirations.plugins.jei.anvil;

import com.mojang.blaze3d.matrix.MatrixStack;
import knightminer.inspirations.Inspirations;
import knightminer.inspirations.library.recipe.BlockIngredient;
import knightminer.inspirations.library.recipe.anvil.AnvilRecipe;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.ForgeI18n;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class AnvilCategory implements IRecipeCategory<AnvilRecipe> {
  /** Unique ID for this category */
  public static final ResourceLocation ID = Inspirations.getResource("anvil_smashing");
  private static final ResourceLocation BACKGROUND_LOC = Inspirations.getResource("textures/gui/jei/anvil_smashing.png");
  /** Base translation key for all JEI string */
  public static final String TRANSLATION_KEY = Util.makeTranslationKey("jei", ID);
  private static final String KEY_DESTROYS = TRANSLATION_KEY + ".destroys";

  private final String title = ForgeI18n.getPattern(TRANSLATION_KEY);

  private final IDrawable background;
  private final IDrawable icon;
  private final IDrawable destroyIcon;

  public AnvilCategory(IGuiHelper helper) {
    background = helper.drawableBuilder(BACKGROUND_LOC, 0, 0, 182, 38).addPadding(0, 4, 0, 0).build();
    // Chipped to imply it having dropped.
    icon = helper.createDrawableIngredient(new ItemStack(Blocks.CHIPPED_ANVIL));
    destroyIcon = helper.createDrawableIngredient(new ItemStack(Blocks.BARRIER));
  }

  private static final int BLOCK_INP_X = 56;
  private static final int BLOCK_OUT_X = 110;
  private static final int BLOCK_Y = 20; // Both in/out the same.

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
  public void setIngredients(AnvilRecipe recipe, IIngredients ingredients) {
    List<ItemStack> inputBlocks = new ArrayList<>();
    List<ItemStack> outputBlocks;

    List<List<ItemStack>> inputItems = new ArrayList<>();
    List<List<ItemStack>> outputItems = new ArrayList<>();
    for(Ingredient ingredient:recipe.getIngredients()) {
      if (ingredient instanceof BlockIngredient) {
        BlockIngredient ing = (BlockIngredient) ingredient;
        ing.getMatchingBlocks().stream()
                .map(Block::asItem)
                .distinct()
                .filter((it) -> it != Items.AIR)
                .map(ItemStack::new)
                .forEach(inputBlocks::add);
      } else {
        inputItems.add(Arrays.asList(ingredient.getMatchingStacks()));
      }
    }
    Item outputBlock = recipe.getTransformResult().asItem();
    if (outputBlock != Items.AIR) {
      outputBlocks = Collections.singletonList(new ItemStack(outputBlock));
    } else {
      outputBlocks = Collections.emptyList();
    }
    inputItems.add(0, inputBlocks);
    outputItems.add(0, outputBlocks);
    ingredients.setInputLists(VanillaTypes.ITEM, inputItems);
    ingredients.setOutputLists(VanillaTypes.ITEM, outputItems);
  }

  @Override
  public void setRecipe(IRecipeLayout layout, AnvilRecipe recipe, IIngredients ingredients) {
    IGuiItemStackGroup items = layout.getItemStacks();
    List<List<ItemStack>> inputs = ingredients.getInputs(VanillaTypes.ITEM);
    List<List<ItemStack>> outputs = ingredients.getOutputs(VanillaTypes.ITEM);

    if (inputs.size() == 0 || outputs.size() == 0) {
      throw new IllegalArgumentException("Must have input or output block list.");
    }
    items.init(0, true, BLOCK_INP_X, BLOCK_Y);
    items.init(1, false, BLOCK_OUT_X, BLOCK_Y);
    items.set(0, inputs.get(0));
    items.set(1, outputs.get(0));
  }

  @Override
  public void draw(AnvilRecipe recipe, MatrixStack matrices, double mouseX, double mouseY) {
    if (recipe.getConversion() == AnvilRecipe.ConvertType.SMASH) {
      destroyIcon.draw(matrices, BLOCK_OUT_X + 1, BLOCK_Y + 1);
    }
  }

  @Override
  public List<ITextComponent> getTooltipStrings(AnvilRecipe recipe, double mouseX, double mouseY) {
    if (recipe.getConversion() == AnvilRecipe.ConvertType.SMASH) {
      if(mouseX > BLOCK_OUT_X && mouseX < BLOCK_OUT_X + 18 && mouseY > BLOCK_Y && mouseY < BLOCK_Y + 18) {
        return Collections.singletonList(new TranslationTextComponent(KEY_DESTROYS));
      }
    }
    return IRecipeCategory.super.getTooltipStrings(recipe, mouseX, mouseY);
  }

  @Override
  public Class<? extends AnvilRecipe> getRecipeClass() {
    return AnvilRecipe.class;
  }

}
