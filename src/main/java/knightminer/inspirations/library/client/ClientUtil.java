package knightminer.inspirations.library.client;

import com.mojang.blaze3d.platform.GlStateManager;
import knightminer.inspirations.Inspirations;
import knightminer.inspirations.common.Config;
import knightminer.inspirations.library.InspirationsRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.MissingTextureSprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockDisplayReader;
import net.minecraftforge.client.model.data.EmptyModelData;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nullable;
import java.awt.Color;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@SuppressWarnings("WeakerAccess")
public final class ClientUtil {
  private static final String TAG_TEXTURE_PATH = "texture_path";
  private static final Minecraft mc = Minecraft.getInstance();

  private ClientUtil() { }

  private static final Map<Item,Integer> COLOR_CACHE = new HashMap<>();

  /**
   * Gets the color for an Item
   * @param item The item to check
   * @return Color for the stack
   */
  public static int getItemColor(Item item) {
    return COLOR_CACHE.computeIfAbsent(item, ClientUtil::getItemColorRaw);
  }

  /**
   * Gets the color for an item stack, used internally by colorCache. Licensed under http://www.apache.org/licenses/LICENSE-2.0
   * @param key Item meta cache combination
   * @return Color for the item meta combination
   * @author InsomniaKitten
   */
  private static Integer getItemColorRaw(Item key) {
    IBakedModel model = mc.getItemRenderer().getItemModelWithOverrides(new ItemStack(key), null, null);
    if (model == mc.getModelManager().getMissingModel()) {
      return -1;
    }
    TextureAtlasSprite sprite = model.getParticleTexture(EmptyModelData.INSTANCE);
    if (sprite == null) {
      return -1;
    }
    float r = 0, g = 0, b = 0, count = 0;
    float[] hsb = new float[3];
    try {
      for (int x = 0; x < sprite.getWidth(); x++) {
        for (int y = 0; y < sprite.getHeight(); y++) {
          int argb = sprite.getPixelRGBA(0, x, y);
          // integer is in format of 0xAABBGGRR
          int cr = argb & 0xFF;
          int cg = argb >> 8 & 0xFF;
          int cb = argb >> 16 & 0xFF;
          int ca = argb >> 24 & 0xFF;
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
    } catch (Exception e) {
      // there is a random bug where models do not properly load, leading to a null frame data
      // so just catch that and treat it as another error state
      InspirationsRegistry.log.error("Caught exception reading sprite for " + key.getRegistryName(), e);
      return -1;
    }
    if (count > 0) {
      r /= count;
      g /= count;
      b /= count;
    }
    return 0xFF000000 | (int)r << 16 | (int)g << 8 | (int)b;
  }

  /**
   * Called on resource reload to clear any resource based cache
   */
  public static void clearCache() {
    COLOR_CACHE.clear();
    unsafe.clear();
  }

  /**
   * Gets the sprite for the given texture location, or Missing Texture if no sprite is found
   */
  public static TextureAtlasSprite getSprite(@Nullable ResourceLocation location) {
    AtlasTexture atlas = mc.getModelManager().getAtlasTexture(PlayerContainer.LOCATION_BLOCKS_TEXTURE);
    if (location == null) {
      return atlas.getSprite(MissingTextureSprite.getLocation());
    }
    return atlas.getSprite(location);
  }

  /**
   * Used to render fluid sprites in the JEI interface
   * @param sprite Sprite to render
   * @param x      Sprite X position
   * @param y      Sprite Y position
   * @param size   Sprite size in pixels
   * @param filled Amount of sprite filled in pixels
   */
  public static void renderFilledSprite(TextureAtlasSprite sprite, final int x, final int y, final int size, final int filled) {
    float uMin = sprite.getMinU();
    float uMax = sprite.getMaxU();
    float vMin = sprite.getMinV();
    float vMax = sprite.getMaxV();
    uMax = uMax - (16 - size) / 16.0f * (uMax - uMin);
    vMax = vMax - (16 - filled) / 16.0f * (vMax - vMin);

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
   * Any items which have blockColors methods that throw an exception
   */
  private static Set<Item> unsafe = new HashSet<>();

  /**
   * Gets the block colors for a block from an itemstack, logging an exception if it fails. Use this to get block colors when the implementation is unknown
   * @param stack Stack to use
   * @param world World
   * @param pos   Pos
   * @param index Tint index
   * @return color, or -1 for undefined
   */
  public static int getStackBlockColorsSafe(ItemStack stack, @Nullable IBlockDisplayReader world, @Nullable BlockPos pos, int index) {
    if (stack.isEmpty()) {
      return -1;
    }

    // do not try if it failed before
    Item item = stack.getItem();
    if (!unsafe.contains(item)) {
      try {
        return getStackBlockColors(stack, world, pos, index);
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
   * @param stack Stack to check
   * @param world World
   * @param pos   Pos
   * @param index Tint index
   * @return color, or -1 for undefined
   */
  public static int getStackBlockColors(ItemStack stack, @Nullable IBlockDisplayReader world, @Nullable BlockPos pos, int index) {
    if (stack.isEmpty() || !(stack.getItem() instanceof BlockItem)) {
      return -1;
    }
    BlockItem item = (BlockItem)stack.getItem();
    BlockState state = item.getBlock().getDefaultState();
    return mc.getBlockColors().getColor(state, world, pos, index);
  }

  /**
   * Renders a colored sprite to display in JEI as cauldron contents
   * @param x        Sprite X position
   * @param y        Sprite Y position
   * @param location Sprite resource location
   * @param color    Sprite color
   * @param level    Cauldron level
   */
  @Deprecated
  public static void renderJEICauldronFluid(int x, int y, ResourceLocation location, float[] color, int level) {
    GlStateManager.enableBlend();
    //mc.gameRenderer.enableLightmap();
    GlStateManager.color4f(color[0], color[1], color[2], 1);
    // 0 means JEI ingredient list
    TextureAtlasSprite sprite = ClientUtil.getSprite(location);
    if (level == 0) {
      ClientUtil.renderFilledSprite(sprite, x, y, 16, 16);
    } else {
      int height = ((10 * level) / Config.getCauldronMax());
      ClientUtil.renderFilledSprite(sprite, x, y, 10, height);
    }
    GlStateManager.color4f(1, 1, 1, 1);
    GlStateManager.disableBlend();
  }

  private static final Map<String,String> NORMALIZED_NAMES = new HashMap<>();

  /**
   * Normalizes a name by replacing underscores with spaces and capitalizing first letters
   * @param name Name to normalize
   * @return Normalized name
   */
  public static String normalizeName(String name) {
    return NORMALIZED_NAMES.computeIfAbsent(name, (s) -> WordUtils.capitalizeFully(name.replace('_', ' ')));
  }
}
