package knightminer.inspirations.library.client.model;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import knightminer.inspirations.recipes.RecipesClientEvents;
import knightminer.inspirations.recipes.tileentity.CauldronTileEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockElement;
import net.minecraft.client.renderer.block.model.BlockElementFace;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.core.Direction;
import net.minecraft.util.GsonHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import com.mojang.math.Vector3f;
import net.minecraftforge.client.model.IModelConfiguration;
import net.minecraftforge.client.model.IModelLoader;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.geometry.IModelGeometry;
import slimeknights.mantle.client.model.RetexturedModel;
import slimeknights.mantle.client.model.RetexturedModel.RetexturedConfiguration;
import slimeknights.mantle.client.model.util.DynamicBakedWrapper;
import slimeknights.mantle.client.model.util.SimpleBlockModel;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;

/**
 * Model to replace cauldron water texture with the relevant fluid texture
 */
public class CauldronModel implements IModelGeometry<CauldronModel> {
  public static final Loader LOADER = new Loader();
  private final SimpleBlockModel model;
  private final Set<String> retextured;
  private final float liquidOffset;

  /**
   * Creates a new model instance
   * @param model        Model instance
   * @param retextured   Names of fluid textures to retexture. If empty, assumes no fluid exists
   * @param liquidOffset Amount to offset the liquid portion in the texture
   */
  protected CauldronModel(SimpleBlockModel model, Set<String> retextured, float liquidOffset) {
    this.model = model;
    this.retextured = retextured;
    this.liquidOffset = liquidOffset;
  }

  @Override
  public Collection<Material> getTextures(IModelConfiguration owner, Function<ResourceLocation,UnbakedModel> modelGetter, Set<Pair<String,String>> missingTextureErrors) {
    Collection<Material> textures = model.getTextures(owner, modelGetter, missingTextureErrors);
    // get special frost texture
    if (owner.isTexturePresent("frost")) {
      textures.add(owner.resolveTexture("frost"));
    } else {
      missingTextureErrors.add(Pair.of("frost", owner.getModelName()));
    }
    return textures;
  }

  @Override
  public BakedModel bake(IModelConfiguration owner, ModelBakery bakery, Function<Material,TextureAtlasSprite> spriteGetter, ModelState modelTransform, ItemOverrides overrides, ResourceLocation modelLocation) {
    // fetch textures before rebaking
    Set<String> retextured = this.retextured.isEmpty() ? Collections.emptySet() : RetexturedModel.getAllRetextured(owner, model, this.retextured);
    // making two models, normal and frosted
    List<BlockElement> warmElements = new ArrayList<>();
    List<BlockElement> frostElements = new ArrayList<>();
    List<BlockElement> liquidElements = new ArrayList<>();
    for (BlockElement part : model.getElements()) {
      boolean updated = false;
      Map<Direction, BlockElementFace> warmFaces = new EnumMap<>(Direction.class);
      Map<Direction, BlockElementFace> frostFaces = new EnumMap<>(Direction.class);
      Map<Direction, BlockElementFace> liquidFaces = new EnumMap<>(Direction.class);
      for (Entry<Direction,BlockElementFace> entry : part.faces.entrySet()) {
        BlockElementFace face = entry.getValue();
        // if the texture is liquid, update the tint index and insert into the liquid list
        if (retextured.contains(face.texture.substring(1))) {
          liquidFaces.put(entry.getKey(), new BlockElementFace(face.cullForDirection, 1, face.texture, face.uv));
        } else {
          // otherwise use original face and make a copy for frost
          warmFaces.put(entry.getKey(), face);
          frostFaces.put(entry.getKey(), new BlockElementFace(face.cullForDirection, -1, "frost", face.uv));
        }
      }
      // if we had a liquid face, make a new part for the warm elements and add a liquid element
      BlockElement newPart = part;
      if (!liquidFaces.isEmpty()) {
        newPart = new BlockElement(part.from, part.to, warmFaces, part.rotation, part.shade);
        Vector3f newTo = part.to;
        if (liquidOffset != 0) {
          newTo = part.to.copy();
          newTo.add(0, liquidOffset, 0);
        }
        liquidElements.add(new BlockElement(part.from, newTo, liquidFaces, part.rotation, part.shade));
      }
      // frosted has all elements of normal, plus an overlay when relevant
      warmElements.add(newPart);
      frostElements.add(newPart);
      // add frost element if anything is frosted
      if (!frostFaces.isEmpty()) {
        frostElements.add(new BlockElement(part.from, part.to, frostFaces, part.rotation, part.shade));
      }
    }

    // make a list of parts with warm and liquid for the base model
    List<BlockElement> firstBake = warmElements;
    if (liquidElements.isEmpty()) {
      liquidElements = Collections.emptyList();
    } else {
      firstBake = new ArrayList<>(warmElements);
      firstBake.addAll(liquidElements);
    }

    // if nothing retextured, bake frosted and return simple baked model
    BakedModel baked = SimpleBlockModel.bakeModel(owner, firstBake, modelTransform, overrides, spriteGetter, modelLocation);
    if (retextured.isEmpty()) {
      BakedModel frosted = SimpleBlockModel.bakeModel(owner, frostElements, modelTransform, overrides, spriteGetter, modelLocation);
      return new FrostedBakedModel(baked, frosted);
    }

    // full dynamic baked model
    return new TexturedBakedModel(baked, owner, warmElements, frostElements, liquidElements, modelTransform, retextured);
  }

  /** Full baked model, does frost and fluid textures */
  private static class TexturedBakedModel extends DynamicBakedWrapper<BakedModel> {
    private final Map<TextureOffsetPair,BakedModel> warmCache = new HashMap<>();
    private final Map<TextureOffsetPair,BakedModel> frostedCache = new HashMap<>();
    // data needed to rebake
    private final IModelConfiguration owner;
    private final ModelState transform;
    private final Set<String> retextured;
    private final List<BlockElement> liquidElements;
    /** Function to bake a warm model for the given texture */
    private final Function<TextureOffsetPair, BakedModel> warmBakery;
    /** Function to bake a frosted model for the given texture */
    private final Function<TextureOffsetPair, BakedModel> frostedBakery;
    protected TexturedBakedModel(BakedModel originalModel, IModelConfiguration owner, List<BlockElement> warmElements, List<BlockElement> frostElements, List<BlockElement> liquidElements, ModelState transform, Set<String> fluidNames) {
      super(originalModel);
      this.owner = owner;
      this.transform = transform;
      this.retextured = fluidNames;
      this.liquidElements = liquidElements;
      this.warmBakery = name -> getFluidModel(name, warmElements);
      this.frostedBakery = name -> getFluidModel(name, frostElements);
    }

    /**
     * Bakes the baked model for the given fluid
     * @param pair  Object containing texture and offset amount
     * @return  Baked model
     */
    private BakedModel getFluidModel(TextureOffsetPair pair, List<BlockElement> baseElements) {
      // if we have liquid elements, add them
      List<BlockElement> elements = new ArrayList<>(baseElements);
      // if no offset, copy in liquid list exactly
      if (pair.offset == 0) {
        elements.addAll(liquidElements);
      } else {
        // offset each element. Note -3 is moved up slightly to prevent z-fighting, its only used when the cauldron is level 1
        float offset = Mth.clamp(pair.offset, -2.95f, 3f);
        liquidElements.stream().map(part -> {
          Vector3f newTo = part.to.copy();
          newTo.add(0, offset, 0);
          return new BlockElement(part.from, newTo, part.faces, part.rotation, part.shade);
        }).forEach(elements::add);
      }
      // bake the new model
      return SimpleBlockModel.bakeDynamic(new RetexturedConfiguration(owner, retextured, pair.location), elements, transform);
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction direction, Random random, IModelData data) {
      if (data == EmptyModelData.INSTANCE) {
        return originalModel.getQuads(state, direction, random, data);
      }

      // get texture name, if missing use missing
      // also use missing if no retextured, that just makes the cache smaller for empty cauldron
      ResourceLocation texture = data.getData(CauldronTileEntity.TEXTURE);
      if (texture == null) {
        texture = MissingTextureAtlasSprite.getLocation();
      } else {
        // serverside uses texture "name" rather than path, use the sprite getter to translate
        texture = RecipesClientEvents.cauldronTextures.getTexture(texture);
      }

      // fetch liquid offset amount
      Integer offset = data.getData(CauldronTileEntity.OFFSET);
      TextureOffsetPair pair = new TextureOffsetPair(texture, offset == null ? 0 : offset);
      // determine model variant
      BakedModel baked = (data.getData(CauldronTileEntity.FROSTED) == Boolean.TRUE)
                          ? frostedCache.computeIfAbsent(pair, frostedBakery)
                          : warmCache.computeIfAbsent(pair, warmBakery);
      // return quads
      return baked.getQuads(state, direction, random, data);
    }
  }

  /** Simplier baked model for when textures are not needed */
  private static class FrostedBakedModel extends DynamicBakedWrapper<BakedModel> {
    private final BakedModel frosted;
    private FrostedBakedModel(BakedModel originalModel, BakedModel frosted) {
      super(originalModel);
      this.frosted = frosted;
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction direction, Random random, IModelData data) {
      return (data.getData(CauldronTileEntity.FROSTED) == Boolean.TRUE ? frosted : originalModel).getQuads(state, direction, random, data);
    }
  }

  /** Data class containing a resource location and an integer. Hashable */
  private static class TextureOffsetPair {
    private final ResourceLocation location;
    private final int offset;
    private TextureOffsetPair(ResourceLocation location, int offset) {
      this.location = location;
      this.offset = offset;
    }

    @Override
    public int hashCode() {
      return location.hashCode() + 31 * offset;
    }

    @Override
    public boolean equals(Object other) {
      if (this == other) {
        return true;
      }
      if (!(other instanceof TextureOffsetPair)) {
        return false;
      }
      TextureOffsetPair pair = (TextureOffsetPair) other;
      return offset == pair.offset && location.equals(pair.location);
    }
  }

  /** Loader class */
  private static class Loader implements IModelLoader<CauldronModel> {
    private Loader() {}

    @Override
    public void onResourceManagerReload(ResourceManager resourceManager) {}

    @Override
    public CauldronModel read(JsonDeserializationContext context, JsonObject json) {
      SimpleBlockModel model = SimpleBlockModel.deserialize(context, json);
      Set<String> retextured = json.has("retextured") ? RetexturedModel.Loader.getRetextured(json) : Collections.emptySet();
      float offset = GsonHelper.getAsFloat(json, "liquid_offset", 0);
      return new CauldronModel(model, retextured, offset);
    }
  }
}
