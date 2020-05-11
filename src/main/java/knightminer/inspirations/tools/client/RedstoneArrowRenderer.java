package knightminer.inspirations.tools.client;

import knightminer.inspirations.library.Util;
import knightminer.inspirations.tools.entity.RedstoneArrow;
import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;

public class RedstoneArrowRenderer extends ArrowRenderer<RedstoneArrow> {

	public static final ResourceLocation CHARGED_ARROW = Util.getResource("textures/entity/arrow/charged.png");
	public RedstoneArrowRenderer(EntityRendererManager renderManager) {
		super(renderManager);
	}

	@Override
	public ResourceLocation getEntityTexture(@Nonnull RedstoneArrow entity) {
		return CHARGED_ARROW;
	}

}
