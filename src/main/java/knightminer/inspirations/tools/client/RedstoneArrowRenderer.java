package knightminer.inspirations.tools.client;

import knightminer.inspirations.Inspirations;
import knightminer.inspirations.tools.entity.RedstoneArrow;
import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class RedstoneArrowRenderer extends ArrowRenderer<RedstoneArrow> {
  private static final ResourceLocation CHARGED_ARROW = Inspirations.getResource("textures/entity/arrow/charged.png");

  public RedstoneArrowRenderer(EntityRendererProvider.Context context) {
    super(context);
  }

  @Override
  public ResourceLocation getTextureLocation(RedstoneArrow entity) {
    return CHARGED_ARROW;
  }
}
