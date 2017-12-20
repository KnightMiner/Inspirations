package knightminer.inspirations.building.client;

import knightminer.inspirations.building.entity.EntityModArrow;
import knightminer.inspirations.library.Util;
import net.minecraft.client.renderer.entity.RenderArrow;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;

public class RenderModArrow extends RenderArrow<EntityModArrow> {

	public static final ResourceLocation CHARGED_ARROW = Util.getResource("textures/entity/arrow/charged.png");
	public RenderModArrow(RenderManager renderManager) {
		super(renderManager);
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityModArrow entity) {
		return CHARGED_ARROW;
	}

}
