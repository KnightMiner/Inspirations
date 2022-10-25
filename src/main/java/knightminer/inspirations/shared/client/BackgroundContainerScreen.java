package knightminer.inspirations.shared.client;

import com.mojang.blaze3d.vertex.PoseStack;
import knightminer.inspirations.Inspirations;
import knightminer.inspirations.library.client.ClientUtil;
import net.minecraft.client.gui.screens.MenuScreens.ScreenConstructor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;

/**
 * Generic container screen that simply draws the given background
 * @param <T> Container type
 */
@SuppressWarnings("WeakerAccess")
public class BackgroundContainerScreen<T extends AbstractContainerMenu> extends AbstractContainerScreen<T> {

  /**
   * Background drawn for this screen
   */
  protected final ResourceLocation background;

  /**
   * Creates a new screen instance
   * @param container  Container class
   * @param inventory  Player inventory
   * @param name       Container name
   * @param background Container background
   */
  public BackgroundContainerScreen(T container, Inventory inventory, Component name, int height, ResourceLocation background) {
    super(container, inventory, name);
    this.background = background;
    this.imageHeight = height;
    this.inventoryLabelY = this.imageHeight - 94;
  }

  @Override
  protected void init() {
    super.init();
    this.titleLabelX = (this.imageWidth - this.font.width(this.title)) / 2;
  }

  @Override
  public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
    this.renderBackground(matrixStack);
    super.render(matrixStack, mouseX, mouseY, partialTicks);
    this.renderTooltip(matrixStack, mouseX, mouseY);
  }

  @Override
  protected void renderBg(PoseStack matrixStack, float partialTicks, int mouseX, int mouseY) {
    ClientUtil.setup(this.background);
    this.blit(matrixStack, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
  }

  public static class Factory<T extends AbstractContainerMenu> implements ScreenConstructor<T,BackgroundContainerScreen<T>> {
    private final ResourceLocation background;
    private final int height;

    /**
     * Creates a factory from the given background location
     * @param height     Screen height
     * @param background Background location
     */
    public Factory(int height, ResourceLocation background) {
      this.height = height;
      this.background = background;
    }

    /**
     * Creates a factory from the container name
     * @param height Screen height
     * @param name   Name of this container
     */
    public Factory(int height, String name) {
      this(height, Inspirations.getResource(String.format("textures/gui/%s.png", name)));
    }

    @Override
    public BackgroundContainerScreen<T> create(T container, Inventory inventory, Component name) {
      return new BackgroundContainerScreen<>(container, inventory, name, height, background);
    }
  }
}
