package knightminer.inspirations.library.client;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.math.NumberUtils;
import org.lwjgl.opengl.GL11;

import knightminer.inspirations.library.ItemMetaKey;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class ClientUtil {
	private static final Minecraft mc = Minecraft.getMinecraft();
	private ClientUtil() {}

	private static Map<ItemMetaKey, Integer> colorCache = new HashMap<>();

	/**
	 * Gets the color for an ItemStack based on its item and metadata
	 * @param stack  Input stack
	 * @return  Color for the stack
	 */
	public static int getStackColor(ItemStack stack) {
		return colorCache.computeIfAbsent(new ItemMetaKey(stack), ClientUtil::getStackColor);
	}

	/**
	 * Gets the color for an item stack, used internally by colorCache. Licensed under http://www.apache.org/licenses/LICENSE-2.0
	 * @param key Item meta cache combination
	 * @return  Color for the item meta combination
	 * @author InsomniaKitten
	 */
	private static Integer getStackColor(ItemMetaKey key) {
		IBakedModel model = mc.getRenderItem().getItemModelWithOverrides(key.makeItemStack(), null, null);
		TextureAtlasSprite sprite = model.getParticleTexture();
		int[] pixels = sprite.getFrameTextureData(0)[0];
		float r = 0, g = 0, b = 0, count = 0;
		float[] hsb = new float[3];
		for (int argb : pixels) {
			int ca = argb >> 24 & 0xFF;
			int cr = argb >> 16 & 0xFF;
			int cg = argb >> 8 & 0xFF;
			int cb = argb & 0xFF;
			if (ca > 0x7F && NumberUtils.max(cr, cg, cb) > 0x1F) {
				Color.RGBtoHSB(ca, cr, cg, hsb);
				float weight = hsb[1];
				r += cr * weight;
				g += cg * weight;
				b += cb * weight;
				count += weight;
			}
		}
		if (count > 0) {
			r /= count;
			g /= count;
			b /= count;
		}
		return 0xFF000000 | (int) r << 16 | (int) g << 8 | (int) b;
	}

	/**
	 * Called on resource reload to clear any resource based cache
	 * @param manager
	 */
	public static void onResourceReload(IResourceManager manager) {
		colorCache.clear();
	}

	/**
	 * Gets the sprite for the given texture location, or null if no sprite is found
	 * @param location
	 * @return
	 */
	public static TextureAtlasSprite getSprite(ResourceLocation location) {
		TextureMap textureMapBlocks = mc.getTextureMapBlocks();
		TextureAtlasSprite sprite = null;
		if(location != null) {
			sprite = textureMapBlocks.getTextureExtry(location.toString());
		}
		if (sprite == null) {
			sprite = textureMapBlocks.getMissingSprite();
		}
		return sprite;
	}

	public static void renderFilledSprite(TextureAtlasSprite sprite, final int x, final int y, final int size, final int filled) {
		double uMin = sprite.getMinU();
		double uMax = sprite.getMaxU();
		double vMin = sprite.getMinV();
		double vMax = sprite.getMaxV();
		uMax = uMax - (16 - size) / 16.0 * (uMax - uMin);
		vMax = vMax - (16 - filled) / 16.0 * (vMax - vMin);

		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();
		bufferBuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		bufferBuilder.pos(x, y + size, 100).tex(uMin, vMax).endVertex();
		bufferBuilder.pos(x + size, y + size, 100).tex(uMax, vMax).endVertex();
		bufferBuilder.pos(x + size, y + size - filled, 100).tex(uMax, vMin).endVertex();
		bufferBuilder.pos(x, y + size - filled, 100).tex(uMin, vMin).endVertex();
		tessellator.draw();
	}
}
