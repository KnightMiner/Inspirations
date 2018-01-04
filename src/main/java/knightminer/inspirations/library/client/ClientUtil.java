package knightminer.inspirations.library.client;

import java.awt.Color;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;

import org.apache.commons.lang3.math.NumberUtils;
import org.lwjgl.opengl.GL11;

import knightminer.inspirations.Inspirations;
import knightminer.inspirations.library.ItemMetaKey;
import knightminer.inspirations.library.util.TextureBlockUtil;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import slimeknights.mantle.client.ModelHelper;

@SideOnly(Side.CLIENT)
public final class ClientUtil {
	public static final String TAG_TEXTURE_PATH = "texture_path";
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

	/**
	 * Gets the cached texture from the TileEntity, or stores it from the texture stack if none is cached
	 * @param te  Tile Entity
	 * @return  String of texture path, or empty string if none found
	 */
	public static String getTexturePath(TileEntity te) {
		String texture = te.getTileData().getString(TAG_TEXTURE_PATH);
		if(texture.isEmpty()) {
			// load it from saved block
			ItemStack stack = new ItemStack(te.getTileData().getCompoundTag(TextureBlockUtil.TAG_TEXTURE));
			if(!stack.isEmpty()) {
				Block block = Block.getBlockFromItem(stack.getItem());
				texture = ModelHelper.getTextureFromBlock(block, stack.getItemDamage()).getIconName();
				te.getTileData().setString(TAG_TEXTURE_PATH, texture);
			}
		}
		return texture;
	}

	/**
	 * Writes the default extended blockstate for a texture block
	 * @param world  World
	 * @param pos    Pos
	 * @param state  State
	 * @return  The extended block state
	 */
	public static IBlockState writeTextureBlockState(IBlockAccess world, BlockPos pos, IBlockState state) {
		TileEntity te = world.getTileEntity(pos);
		if(te != null) {
			String texture = getTexturePath(te);
			if(!texture.isEmpty()) {
				state = ((IExtendedBlockState)state).withProperty(TextureBlockUtil.TEXTURE_PROP, texture);
			}
		}
		return state;
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
	public static int getStackBlockColorsSafe(ItemStack stack, @Nullable IBlockAccess world, @Nullable BlockPos pos, int index) {
		if(stack.isEmpty()) {
			return -1;
		}

		// do not try if it failed before
		Item item = stack.getItem();
		if(!ClientUtil.unsafe.contains(item)) {
			try {
				return ClientUtil.getStackBlockColors(stack, world, pos, index);
			} catch (Exception e) {
				// catch and log possible exceptions. Most likely exception is ClassCastException if they do not perform safety checks
				Inspirations.log.error(String.format("Caught exception getting block colors for %s", item.getRegistryName()), e);
				ClientUtil.unsafe.add(item);
			}
		}

		// fallback to item colors
		return mc.getItemColors().colorMultiplier(stack, index);
	}

	/**
	 * Gets the block colors from an item stack
	 * @param stack  Stack to check
	 * @param world  World
	 * @param pos    Pos
	 * @param index  Tint index
	 * @return  color, or -1 for undefined
	 */
	@SuppressWarnings("deprecation")
	public static int getStackBlockColors(ItemStack stack, @Nullable IBlockAccess world, @Nullable BlockPos pos, int index) {
		if(stack.isEmpty() || !(stack.getItem() instanceof ItemBlock)) {
			return -1;
		}
		ItemBlock item = (ItemBlock) stack.getItem();
		IBlockState iblockstate = item.getBlock().getStateFromMeta(item.getDamage(stack));
		return mc.getBlockColors().colorMultiplier(iblockstate, world, pos, index);
	}
}
