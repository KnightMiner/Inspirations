package knightminer.inspirations.shared.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import knightminer.inspirations.Inspirations;
import net.minecraft.client.gui.ScreenManager.IScreenFactory;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

/**
 * Generic container screen that simply draws the given background
 * @param <T>  Container type
 */
@SuppressWarnings("WeakerAccess")
public class BackgroundContainerScreen<T extends Container> extends ContainerScreen<T> {

  /** Background drawn for this screen */
  protected final ResourceLocation background;

  /**
   * Creates a new screen instance
   * @param container    Container class
   * @param inventory    Player inventory
   * @param name         Container name
   * @param background   Container background
   */
  public BackgroundContainerScreen(T container, PlayerInventory inventory, ITextComponent name, int height, ResourceLocation background) {
    super(container, inventory, name);
    this.background = background;
    this.ySize = height;
    this.playerInventoryTitleY = this.ySize - 94;
  }

  @Override
  protected void init() {
    super.init();
    this.titleX = (this.xSize - this.font.func_238414_a_(this.title)) / 2;
  }

  @Override
  public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
    this.renderBackground(matrixStack);
    super.render(matrixStack, mouseX, mouseY, partialTicks);
    this.func_230459_a_(matrixStack, mouseX, mouseY);
  }

  @Override
  protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY) {
    RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
    assert this.minecraft != null;
    this.minecraft.getTextureManager().bindTexture(this.background);
    this.blit(matrixStack, this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
  }

  public static class Factory<T extends Container> implements IScreenFactory<T,BackgroundContainerScreen<T>> {
    private final ResourceLocation background;
    private final int height;

    /**
     * Creates a factory from the given background location
     * @param height      Screen height
     * @param background  Background location
     */
    public Factory(int height, ResourceLocation background) {
      this.height = height;
      this.background = background;
    }

    /**
     * Creates a factory from the container name
     * @param height      Screen height
     * @param name  Name of this container
     */
    public Factory(int height, String name) {
      this(height, Inspirations.getResource(String.format("textures/gui/%s.png", name)));
    }

    @Override
    public BackgroundContainerScreen<T> create(T container, PlayerInventory inventory, ITextComponent name) {
      return new BackgroundContainerScreen<>(container, inventory, name, height, background);
    }
  }
}
