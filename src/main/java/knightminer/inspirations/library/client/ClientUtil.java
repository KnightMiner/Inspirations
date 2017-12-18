package knightminer.inspirations.library.client;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.math.NumberUtils;

import knightminer.inspirations.library.ItemMetaKey;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.ItemStack;
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

	public static int combineColors(int color1, int color2, int scale) {
		if(scale == 0) {
			return color1;
		}
		int a = color1 >> 24 & 0xFF;
		int r = color1 >> 16 & 0xFF;
		int g = color1 >> 8 & 0xFF;
		int b = color1 & 0xFF;
		int a2 = color2 >> 24 & 0xFF;
		int r2 = color2 >> 16 & 0xFF;
		int g2 = color2 >> 8 & 0xFF;
		int b2 = color2 & 0xFF;

		for(int i = 0; i < scale; i++) {
			a = (int) Math.sqrt(a * a2);
			r = (int) Math.sqrt(r * r2);
			g = (int) Math.sqrt(g * g2);
			b = (int) Math.sqrt(b * b2);
		}
		return a << 24 | r << 16 | g << 8 | b;
	}
}
