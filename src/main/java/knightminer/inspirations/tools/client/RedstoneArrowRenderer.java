package knightminer.inspirations.tools.client;

import knightminer.inspirations.Inspirations;
import knightminer.inspirations.tools.entity.RedstoneArrow;
import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;

public class RedstoneArrowRenderer extends ArrowRenderer<RedstoneArrow> {
  private static final ResourceLocation CHARGED_ARROW = Inspirations.getResource("textures/entity/arrow/charged.png");

  public RedstoneArrowRenderer(EntityRendererManager renderManager) {
    super(renderManager);
  }

  @Override
  public ResourceLocation getTextureLocation(RedstoneArrow entity) {
    return CHARGED_ARROW;
  }
}
