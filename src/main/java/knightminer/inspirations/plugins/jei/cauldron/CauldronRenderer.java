package knightminer.inspirations.plugins.jei.cauldron;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import knightminer.inspirations.library.recipe.cauldron.contents.ICauldronContents;
import knightminer.inspirations.recipes.RecipesClientEvents;
import mezz.jei.api.ingredients.IIngredientRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

import static knightminer.inspirations.library.recipe.cauldron.recipe.ICauldronRecipe.HALF;
import static knightminer.inspirations.library.recipe.cauldron.recipe.ICauldronRecipe.MAX;
import static knightminer.inspirations.library.recipe.cauldron.recipe.ICauldronRecipe.QUARTER;
import static knightminer.inspirations.library.recipe.cauldron.recipe.ICauldronRecipe.SIXTH;
import static knightminer.inspirations.library.recipe.cauldron.recipe.ICauldronRecipe.THIRD;

/** Renderer for the cauldron contents */
public class CauldronRenderer {
  // translations
  private static final String LEVEL = CauldronCategory.TRANSLATION_KEY + ".level";
  private static final String LEVEL_HALF = LEVEL + ".half";
  private static final String LEVEL_THIRD = LEVEL + ".third";
  private static final String LEVEL_QUARTER = LEVEL + ".quarter";
  private static final String LEVEL_SIXTH = LEVEL + ".sixth";
  private static final String LEVEL_EMPTY = LEVEL + ".empty";
  private static final String LEVEL_FULL = LEVEL + ".full";
  private static final ITextComponent[] AMOUNT_TEXTS = new ITextComponent[MAX + 1];
  /** Size of the cauldron in pixels */
  public static final int CAULDRON_SIZE = 12;

  private CauldronRenderer() {}

  /**
   * Rendering to use in the ingredient list
   */
  public static final Contents LIST = new CauldronRenderer.Contents(16, MAX, true);

  /**
   * Rendering to use in recipes
   */
  private static final Contents[] CONTENT_LEVEL = new Contents[MAX + 1];
  private static final Fluids[] FLUID_LEVEL = new Fluids[MAX + 1];

  /**
   * Gets the content renderer for the given amount
   * @param amount Amount to render
   * @return Renderer
   */
  public static Contents contentLevel(int amount) {
    if (amount < 0) amount = 0;
    else if (amount > MAX) amount = MAX;
    if (CONTENT_LEVEL[amount] == null) {
      CONTENT_LEVEL[amount] = new Contents(CAULDRON_SIZE, amount, false);
    }
    return CONTENT_LEVEL[amount];
  }

  /**
   * Gets the fluid renderer for the given amount
   * @param amount Amount to render
   * @return Renderer
   */
  public static Fluids fluidLevel(int amount) {
    if (amount < 0) amount = 0;
    else if (amount > MAX) amount = MAX;
    if (FLUID_LEVEL[amount] == null) {
      FLUID_LEVEL[amount] = new Fluids(amount);
    }
    return FLUID_LEVEL[amount];
  }

  public static ITextComponent getAmountText(int amount) {
    if (amount < 0) amount = 0;
    if (amount > MAX) amount = MAX;
    if (AMOUNT_TEXTS[amount] == null) {
      IFormattableTextComponent amountText;
      // 0 is empty, display quarter and third as cleaner fractions
      if (amount == 0) {
        amountText = new TranslationTextComponent(LEVEL_EMPTY);
      } else if (amount == MAX) {
        amountText = new TranslationTextComponent(LEVEL_FULL);
      } else if (amount % HALF == 0) {
        amountText = new TranslationTextComponent(LEVEL_HALF);
      } else if (amount % THIRD == 0) {
        amountText = new TranslationTextComponent(LEVEL_THIRD, amount / THIRD);
      } else if (amount % QUARTER == 0) {
        amountText = new TranslationTextComponent(LEVEL_QUARTER, amount / QUARTER);
      } else if (amount % SIXTH == 0) {
        amountText = new TranslationTextComponent(LEVEL_SIXTH, amount / SIXTH);
      } else {
        // default to x/12 for odd cases
        amountText = new TranslationTextComponent(LEVEL, amount);
      }
      AMOUNT_TEXTS[amount] = amountText.withStyle(TextFormatting.GRAY);
    }
    return AMOUNT_TEXTS[amount];
  }

  /**
   * Shared cauldron render code
   * @param matrices Matrix stack instance
   * @param x        X position
   * @param y        Y position
   * @param width    Width
   * @param height   Height
   * @param amount   Fluid amount
   * @param texture  Texture to render
   * @param color    Texture color
   */
  private static void render(MatrixStack matrices, int x, int y, int width, int height, int amount, ResourceLocation texture, int color) {
    if (amount == 0) {
      return;
    }

    // set up renderer
    RenderSystem.enableBlend();
    RenderSystem.enableAlphaTest();
    Minecraft minecraft = Minecraft.getInstance();
    TextureAtlasSprite sprite = minecraft.getModelManager().getAtlas(PlayerContainer.BLOCK_ATLAS).getSprite(texture);
    minecraft.getTextureManager().bind(PlayerContainer.BLOCK_ATLAS);

    // draw
    int scaled = amount * height / MAX;
    Matrix4f matrix = matrices.last().pose();
    setGLColorFromInt(color);
    drawSprite(matrix, x, (y + height - scaled), width, scaled, sprite);

    // reset render system
    RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
    RenderSystem.disableAlphaTest();
    RenderSystem.disableBlend();
  }

  /**
   * Sets the color based on the given int
   * @param color Int color
   */
  @SuppressWarnings("deprecation")
  private static void setGLColorFromInt(int color) {
    float red = (color >> 16 & 255) / 255f;
    float green = (color >> 8 & 255) / 255f;
    float blue = (color & 255) / 255f;
    int alphaI = (color >> 24 & 255);
    float alpha = alphaI == 0 ? 1 : alphaI / 255f;
    RenderSystem.color4f(red, green, blue, alpha);
  }

  /**
   * Draws the actual sprite
   * @param matrix Matrix
   * @param x1     Sprite X start
   * @param y1     Sprite Y start
   * @param width  Sprite width
   * @param height Sprite height
   * @param sprite Sprite to draw
   */
  private static void drawSprite(Matrix4f matrix, float x1, float y1, float width, float height, TextureAtlasSprite sprite) {
    // calculate missing numbers
    final float z = 100.0f;
    float u1 = sprite.getU0();
    float u2 = sprite.getU((16 * width) / sprite.getWidth());
    float v1 = sprite.getV0();
    float v2 = sprite.getV((16 * height) / sprite.getHeight());
    float x2 = x1 + width;
    float y2 = y1 + height;

    // start drawing
    Tessellator tessellator = Tessellator.getInstance();
    BufferBuilder builder = tessellator.getBuilder();
    builder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
    builder.vertex(matrix, x1, y2, z).uv(u1, v2).endVertex();
    builder.vertex(matrix, x2, y2, z).uv(u2, v2).endVertex();
    builder.vertex(matrix, x2, y1, z).uv(u2, v1).endVertex();
    builder.vertex(matrix, x1, y1, z).uv(u1, v1).endVertex();
    tessellator.end();
  }

  /**
   * Cauldron contents implementation
   */
  public static class Contents implements IIngredientRenderer<ICauldronContents> {
    // constants
    private final int size;
    private final int amount;
    private final boolean isList;

    /**
     * Creates a new renderer instance
     * @param size  Renderer size in pixels
     * @param amount Amount to render
     * @param isList If true, is the ingredient list
     */
    private Contents(int size, int amount, boolean isList) {
      this.size = size;
      this.amount = amount;
      this.isList = isList;
    }

    @Override
    public void render(MatrixStack matrices, int x, int y, @Nullable ICauldronContents contents) {
      if (contents == null) {
        return;
      }

      CauldronRenderer.render(matrices, x, y, size, size, amount, RecipesClientEvents.cauldronTextures.getTexture(contents.getTextureName()), contents.getTintColor());
    }

    @Override
    public List<ITextComponent> getTooltip(ICauldronContents contents, ITooltipFlag flag) {
      List<ITextComponent> list = new ArrayList<>();
      if (!isList && amount == 0) {
        list.add(getAmountText(0));
      } else {
        list.add(contents.getDisplayName());
        contents.addInformation(list, flag);
        if (!isList) {
          list.add(getAmountText(amount));
        }
      }

      return list;
    }
  }

  /** Fluid renderer override for ingredient type matching */
  public static class Fluids implements IIngredientRenderer<FluidStack> {
    private final int amount;

    /**
     * Creates a new renderer instance
     * @param amount Amount to render
     */
    private Fluids(int amount) {
      this.amount = amount;
    }

    @Override
    public void render(MatrixStack matrices, int x, int y, @Nullable FluidStack fluid) {
      if (fluid == null || fluid.isEmpty()) {
        return;
      }

      FluidAttributes attributes = fluid.getFluid().getAttributes();
      CauldronRenderer.render(matrices, x, y, CAULDRON_SIZE, CAULDRON_SIZE, amount, attributes.getStillTexture(fluid), attributes.getColor(fluid));
    }

    @Override
    public List<ITextComponent> getTooltip(FluidStack fluidStack, ITooltipFlag iTooltipFlag) {
      List<ITextComponent> list = new ArrayList<>();
      if (amount == 0) {
        list.add(getAmountText(0));
      } else {
        list.add(fluidStack.getDisplayName());
        list.add(getAmountText(amount));
      }

      return list;
    }
  }
}
