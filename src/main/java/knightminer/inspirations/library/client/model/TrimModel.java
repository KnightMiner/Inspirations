package knightminer.inspirations.library.client.model;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.renderer.model.BlockFaceUV;
import net.minecraft.client.renderer.model.BlockPart;
import net.minecraft.client.renderer.model.BlockPartFace;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.IModelTransform;
import net.minecraft.client.renderer.model.IUnbakedModel;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.client.renderer.model.RenderMaterial;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.Direction;
import net.minecraft.util.Direction.Axis;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.client.model.IModelConfiguration;
import net.minecraftforge.client.model.IModelLoader;
import net.minecraftforge.client.model.geometry.IModelGeometry;
import slimeknights.mantle.client.model.util.SimpleBlockModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Function;

/**
 * Model that trims the specified number of pixels off the top of all elements. Designed for use with a parent model in resource packs.
 */
public class TrimModel implements IModelGeometry<TrimModel> {
  /** Loader instance */
  public static final Loader LOADER = new Loader();

  private final SimpleBlockModel model;
  private final float trim;

  /**
   * Creates a new model instance
   * @param model  Base model
   * @param trim   Number of pixels to trim off the top of all elements
   */
  @SuppressWarnings("WeakerAccess")
  public TrimModel(SimpleBlockModel model, float trim) {
    this.model = model;
    this.trim = trim;
  }

  @Override
  public Collection<RenderMaterial> getTextures(IModelConfiguration owner, Function<ResourceLocation,IUnbakedModel> modelGetter, Set<Pair<String,String>> missingTextureErrors) {
    return model.getTextures(owner, modelGetter, missingTextureErrors);
  }

  @Override
  public IBakedModel bake(IModelConfiguration owner, ModelBakery bakery, Function<RenderMaterial,TextureAtlasSprite> spriteGetter, IModelTransform transform, ItemOverrideList overrides, ResourceLocation location) {
    // first, determine the highest pixel for each xz location, this is needed as there may be multiple elements in a column
    List<BlockPart> originalElements = model.getElements();
    // map of XZ to highest height
    Map<Pair<Float,Float>, Float> highest = new HashMap<>();
    // map of XZ to a face, to use if no top face
    Map<Pair<Float,Float>, BlockPartFace> topFaces = new HashMap<>();
    for (BlockPart part : originalElements){
      // xz position
      Pair<Float,Float> xz = Pair.of(part.from.x(), part.from.z());
      float height = part.to.y();
      // if we found an element at this location, keep the largest
      BlockPartFace face = part.faces.get(Direction.UP);
      if (highest.containsKey(xz)) {
        // replace if the highest
        boolean isHighest = height > highest.get(xz);
        if (isHighest) {
          highest.put(xz, height);
        }
        // replace top face if we have one and are higher or its missing
        if (face != null && isHighest || !topFaces.containsKey(xz)) {
          topFaces.put(xz, face);
        }
      } else {
        // add top face if present
        highest.put(xz, height);
        if (face != null) {
          topFaces.put(xz, face);
        }
      }
    }

    // iterate all elements, trimming to the highest height
    List<BlockPart> elements = new ArrayList<>();
    for (BlockPart part : originalElements) {
      // determine how tall this element can be
      Pair<Float, Float> xz = Pair.of(part.from.x(), part.from.z());
      float newHeight = highest.get(xz) - trim;
      if (newHeight == 0) {
        newHeight = 0.05f;
      }
      // if the max height is taller than us, no work to do
      float oldHeight = part.to.y();
      if (newHeight > oldHeight) {
        elements.add(part);
      } else {
        // update the part
        Vector3f to = new Vector3f(part.to.x(), newHeight, part.to.z());
        float trimAmount = oldHeight - newHeight;

        // if the element now has a height of less than 0, remove it
        if (to.y() >= part.from.y()) {
          // if the element has a height of exactly 0, remove side faces
          boolean zeroHeight = to.y() == part.from.y();
          // trim UVs on each face
          Map<Direction,BlockPartFace> faces = new EnumMap<>(Direction.class);
          for (Entry<Direction, BlockPartFace> entry : part.faces.entrySet()) {
            Direction side = entry.getKey();
            boolean isY = side.getAxis() == Axis.Y;
            if (!zeroHeight || isY) {
              // only trim UV for non-y axis sides
              if (isY) {
                faces.put(side, entry.getValue());
              } else {
                faces.put(side, trimUV(entry.getValue(), trimAmount));
              }
            }
          }

          // add a top face if missing
          if (!faces.containsKey(Direction.UP)) {
            BlockPartFace topFace = topFaces.get(xz);
            if (topFace != null) {
              faces.put(Direction.UP, topFace);
            }
          }

          // add the updated element
          elements.add(new BlockPart(part.from, to, faces, part.rotation, part.shade));
        }
      }
    }
    // bake the final model
    return SimpleBlockModel.bakeModel(owner, elements, transform, overrides, spriteGetter, location);
  }

  /**
   * Trims the given face UV by the model amount
   * @param face  Face to trim
   * @return  New face with trimmed UV, or original face if auto UV is used
   */
  private static BlockPartFace trimUV(BlockPartFace face, float amount) {
    // if no UV is set, we can return the original face, auto UV will handle it
    BlockFaceUV uv = face.uv;
    if (uv.uvs == null) {
      return face;
    }
    // trim UVs based on rotation, have to add to smaller numbers, subtract from larger
    float[] uvs = Arrays.copyOf(uv.uvs, 4);
    switch(uv.rotation) {
      case 0:
        trim(uvs, amount, 1, 3);
        break;
      case 180:
        trim(uvs, amount, 3, 1);
        break;
      case 90:
        trim(uvs, amount, 0, 2);
        break;
      case 270:
        trim(uvs, amount, 2, 0);
        break;
    }
    return new BlockPartFace(face.cullForDirection, face.tintIndex, face.texture, new BlockFaceUV(uvs, uv.rotation));
  }

  /**
   * Trims the UVs for the given coord
   * @param uvs      UVs
   * @param index    Index to trim
   * @param compare  Index to compare to determine whether trim is adding or subtracting
   */
  private static void trim(float[] uvs, float amount, int index, int compare) {
    // subtract if the larger one, add if the smaller one
    if (uvs[index] > uvs[compare]) {
      uvs[index] -= amount;
    } else {
      uvs[index] += amount;
    }
  }

  /** Loader logic */
  private static class Loader implements IModelLoader<TrimModel> {
    @Override
    public void onResourceManagerReload(IResourceManager resourceManager) {}

    @Override
    public TrimModel read(JsonDeserializationContext context, JsonObject json) {
      SimpleBlockModel model = SimpleBlockModel.deserialize(context, json);
      float trim = JSONUtils.getAsFloat(json, "trim");
      if (trim <= 0) {
        throw new JsonSyntaxException("trim must be greater than 0");
      }
      return new TrimModel(model, trim);
    }
  }
}
