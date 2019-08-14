package knightminer.inspirations.library.client;

import com.mojang.blaze3d.platform.GlStateManager;
import knightminer.inspirations.Inspirations;
import knightminer.inspirations.library.InspirationsRegistry;
import knightminer.inspirations.library.util.TextureBlockUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.MissingTextureSprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.Item;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IFutureReloadListener;
import net.minecraft.resources.IResourceManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IEnviromentBlockReader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.data.EmptyModelData;
import org.apache.commons.lang3.math.NumberUtils;
import org.lwjgl.opengl.GL11;
import slimeknights.mantle.client.ModelHelper;

import javax.annotation.Nullable;
import java.awt.Color;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@OnlyIn(Dist.CLIENT)
public final class ClientUtil {
	public static final String TAG_TEXTURE_PATH = "texture_path";
	private static final Minecraft mc = Minecraft.getInstance();
	private ClientUtil() {}

	private static Map<Item, Integer> colorCache = new HashMap<>();

	/**
	 * Gets the color for an ItemStack
	 * @param stack  Input stack
	 * @return  Color for the stack
	 */
	public static int getStackColor(ItemStack stack) {
		return colorCache.computeIfAbsent(stack.getItem(), ClientUtil::getStackColor);
	}

	/**
	 * Gets the color for an item stack, used internally by colorCache. Licensed under http://www.apache.org/licenses/LICENSE-2.0
	 * @param key Item meta cache combination
	 * @return  Color for the item meta combination
	 * @author InsomniaKitten
	 */
	private static Integer getStackColor(Item key) {
		IBakedModel model = mc.getItemRenderer().getItemModelWithOverrides(new ItemStack(key), null, null);
		if(model == null) {
			return -1;
		}
		TextureAtlasSprite sprite = model.getParticleTexture(EmptyModelData.INSTANCE);
		if(sprite == null) {
			return -1;
		}
		float r = 0, g = 0, b = 0, count = 0;
		float[] hsb = new float[3];
		for (int x = 0; x < sprite.getWidth(); x++) {
			for (int y = 0; y < sprite.getHeight(); y++) {
				int argb = sprite.getPixelRGBA(0, x, y);
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
	 */

	public static void clearCache() {
		colorCache.clear();
		unsafe.clear();
	}


	/**
	 * Gets the sprite for the given texture location, or null if no sprite is found
	 * @param location
	 * @return
	 */
	public static TextureAtlasSprite getSprite(ResourceLocation location) {
		AtlasTexture textureMapBlocks = mc.getTextureMap();
		TextureAtlasSprite sprite = null;
		if(location != null) {
			sprite = textureMapBlocks.getSprite(location);
		}
		if (sprite == null) {
			sprite = textureMapBlocks.getSprite(MissingTextureSprite.getLocation());
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

	/**
	 * Gets the cached texture from the TileEntity, or stores it from the texture stack if none is cached
	 * @param te  Tile Entity
	 * @return  String of texture path, or empty string if none found
	 */
	public static String getTexturePath(TileEntity te) {
		String texture = te.getTileData().getString(TAG_TEXTURE_PATH);
		if(texture.isEmpty()) {
			// load it from saved block
			ItemStack stack = ItemStack.read(te.getTileData().getCompound(TextureBlockUtil.TAG_TEXTURE));
			if(!stack.isEmpty()) {
				Block block = Block.getBlockFromItem(stack.getItem());
				texture = ModelHelper.getTextureFromBlockstate(block.getDefaultState()).getName().toString();
				te.getTileData().putString(TAG_TEXTURE_PATH, texture);
			}
		}
		return texture;
	}

	/** Any items which have blockColors methods that throw an exception */
	private static Set<Item> unsafe = new HashSet<>();

	/**
	 * Gets the block colors for a block from an itemstack, logging an exception if it fails. Use this to get block colors when the implementation is unknown
	 * @param stack  Stack to use
	 * @param world  World
	 * @param pos    Pos
	 * @param index  Tint index
	 * @return  color, or -1 for undefined
	 */
	public static int getStackBlockColorsSafe(ItemStack stack, @Nullable IEnviromentBlockReader world, @Nullable BlockPos pos, int index) {
		if(stack.isEmpty()) {
			return -1;
		}

		// do not try if it failed before
		Item item = stack.getItem();
		if(!unsafe.contains(item)) {
			try {
				return ClientUtil.getStackBlockColors(stack, world, pos, index);
			} catch (Exception e) {
				// catch and log possible exceptions. Most likely exception is ClassCastException if they do not perform safety checks
				Inspirations.log.error(String.format("Caught exception getting block colors for %s", item.getRegistryName()), e);
				unsafe.add(item);
			}
		}

		// fallback to item colors
		return mc.getItemColors().getColor(stack, index);
	}

	/**
	 * Gets the block colors from an item stack
	 * @param stack  Stack to check
	 * @param world  World
	 * @param pos    Pos
	 * @param index  Tint index
	 * @return  color, or -1 for undefined
	 */
	public static int getStackBlockColors(ItemStack stack, @Nullable IEnviromentBlockReader world, @Nullable BlockPos pos, int index) {
		if(stack.isEmpty() || !(stack.getItem() instanceof BlockItem)) {
			return -1;
		}
		BlockItem item = (BlockItem) stack.getItem();
		BlockState state = item.getBlock().getDefaultState();
		return mc.getBlockColors().getColor(state, world, pos, index);
	}

	/**
	 * Renders a colored sprite to display in JEI as cauldron contents
	 * @param x         Sprite X position
	 * @param y         Sprite Y position
	 * @param location  Sprite resource location
	 * @param color     Sprite color
	 * @param level     Cauldron level
	 */
	public static void renderJEICauldronFluid(int x, int y, ResourceLocation location, float[] color, int level) {
		GlStateManager.enableBlend();
		mc.gameRenderer.enableLightmap();
		GlStateManager.color3f(color[0], color[1], color[2]);
		// 0 means JEI ingredient list
		TextureAtlasSprite sprite = ClientUtil.getSprite(location);
		if(level == 0) {
			ClientUtil.renderFilledSprite(sprite, x, y, 16, 16);
		} else {
			int height = ((10 * level) / InspirationsRegistry.getCauldronMax());
			ClientUtil.renderFilledSprite(sprite, x, y, 10, height);
		}
		GlStateManager.color3f(1, 1, 1);
		GlStateManager.disableBlend();
	}
}
