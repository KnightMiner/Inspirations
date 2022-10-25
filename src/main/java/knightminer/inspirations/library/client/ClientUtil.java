package knightminer.inspirations.library.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import knightminer.inspirations.Inspirations;
import knightminer.inspirations.library.InspirationsRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.EmptyModelData;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.text.WordUtils;

import javax.annotation.Nullable;
import java.awt.Color;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@SuppressWarnings("WeakerAccess")
public final class ClientUtil {
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
    BakedModel model = mc.getItemRenderer().getModel(new ItemStack(key), null, null, 0);
    if (model == mc.getModelManager().getMissingModel()) {
      return -1;
    }
    TextureAtlasSprite sprite = model.getParticleIcon(EmptyModelData.INSTANCE);
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
   * Gets the sprite for the given texture location, or Missing Texture if no sprite is found
   */
  public static TextureAtlasSprite getSprite(@Nullable ResourceLocation location) {
    TextureAtlas atlas = mc.getModelManager().getAtlas(InventoryMenu.BLOCK_ATLAS);
    if (location == null) {
      return atlas.getSprite(MissingTextureAtlasSprite.getLocation());
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
    float uMin = sprite.getU0();
    float uMax = sprite.getU1();
    float vMin = sprite.getV0();
    float vMax = sprite.getV1();
    uMax = uMax - (16 - size) / 16.0f * (uMax - uMin);
    vMax = vMax - (16 - filled) / 16.0f * (vMax - vMin);

    Tesselator tessellator = Tesselator.getInstance();
    BufferBuilder bufferBuilder = tessellator.getBuilder();

    bufferBuilder.begin(Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
    bufferBuilder.vertex(x, y + size, 100).uv(uMin, vMax).endVertex();
    bufferBuilder.vertex(x + size, y + size, 100).uv(uMax, vMax).endVertex();
    bufferBuilder.vertex(x + size, y + size - filled, 100).uv(uMax, vMin).endVertex();
    bufferBuilder.vertex(x, y + size - filled, 100).uv(uMin, vMin).endVertex();
    tessellator.end();
  }

  /**
   * Any items which have blockColors methods that throw an exception
   */
  private static final Set<Item> UNSAFE_COLORS = new HashSet<>();

  /**
   * Gets the block colors for a block from an itemstack, logging an exception if it fails. Use this to get block colors when the implementation is unknown
   * @param stack Stack to use
   * @param world World
   * @param pos   Pos
   * @param index Tint index
   * @return color, or -1 for undefined
   */
  public static int getStackBlockColorsSafe(ItemStack stack, @Nullable BlockAndTintGetter world, @Nullable BlockPos pos, int index) {
    if (stack.isEmpty()) {
      return -1;
    }

    // do not try if it failed before
    Item item = stack.getItem();
    if (!UNSAFE_COLORS.contains(item)) {
      try {
        return getStackBlockColors(stack, world, pos, index);
      } catch (Exception e) {
        // catch and log possible exceptions. Most likely exception is ClassCastException if they do not perform safety checks
        Inspirations.log.error(String.format("Caught exception getting block colors for %s", item.getRegistryName()), e);
        UNSAFE_COLORS.add(item);
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
  public static int getStackBlockColors(ItemStack stack, @Nullable BlockAndTintGetter world, @Nullable BlockPos pos, int index) {
    if (stack.isEmpty() || !(stack.getItem() instanceof BlockItem item)) {
      return -1;
    }
    BlockState state = item.getBlock().defaultBlockState();
    return mc.getBlockColors().getColor(state, world, pos, index);
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

  /** Reload listener for client utils */
  public static final ResourceManagerReloadListener RELOAD_LISTENER = manager -> COLOR_CACHE.clear();


  /* GUI helpers */

  /**
   * Binds a texture for rendering
   * @param texture  Texture
   */
  public static void bindTexture(ResourceLocation texture) {
    RenderSystem.setShader(GameRenderer::getPositionTexShader);
    RenderSystem.setShaderTexture(0, texture);
  }

  /**
   * Sets up the shader for rendering
   * @param texture  Texture
   * @param red      Red tint
   * @param green    Green tint
   * @param blue     Blue tint
   * @param alpha    Alpha tint
   */
  public static void setup(ResourceLocation texture, float red, float green, float blue, float alpha) {
    bindTexture(texture);
    RenderSystem.setShaderColor(red, green, blue, alpha);
  }

  /**
   * Sets up the shader for rendering
   * @param texture  Texture
   */
  public static void setup(ResourceLocation texture) {
    setup(texture, 1.0f, 1.0f, 1.0f, 1.0f);
  }
}
