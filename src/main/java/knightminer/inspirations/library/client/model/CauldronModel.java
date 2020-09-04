package knightminer.inspirations.library.client.model;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import knightminer.inspirations.recipes.RecipesClientEvents;
import knightminer.inspirations.recipes.tileentity.CauldronTileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.BakedQuad;
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
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModelConfiguration;
import net.minecraftforge.client.model.IModelLoader;
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

  /**
   * Creates a new model instance
   * @param model       Model instance
   * @param retextured  Names of fluid textures to retexture. If empty, assumes no fluid exists
   */
  protected CauldronModel(SimpleBlockModel model, Set<String> retextured) {
    this.model = model;
    this.retextured = retextured;
  }

  @Override
  public Collection<RenderMaterial> getTextures(IModelConfiguration owner, Function<ResourceLocation,IUnbakedModel> modelGetter, Set<Pair<String,String>> missingTextureErrors) {
    Collection<RenderMaterial> textures = model.getTextures(owner, modelGetter, missingTextureErrors);
    // get special frost texture
    if (owner.isTexturePresent("frost")) {
      textures.add(owner.resolveTexture("frost"));
    } else {
      missingTextureErrors.add(Pair.of("frost", owner.getModelName()));
    }
    return textures;
  }

  @Override
  public IBakedModel bake(IModelConfiguration owner, ModelBakery bakery, Function<RenderMaterial,TextureAtlasSprite> spriteGetter, IModelTransform modelTransform, ItemOverrideList overrides, ResourceLocation modelLocation) {
    // fetch textures before rebaking
    Set<String> retextured = this.retextured.isEmpty() ? Collections.emptySet() : RetexturedModel.getAllRetextured(owner, model, this.retextured);
    // making two models, normal and frosted
    List<BlockPart> warmElements = new ArrayList<>();
    List<BlockPart> frostElements = new ArrayList<>();
    for (BlockPart part : model.getElements()) {
      boolean updated = false;
      Map<Direction, BlockPartFace> newFaces = new EnumMap<>(Direction.class);
      Map<Direction, BlockPartFace> frostFaces = new EnumMap<>(Direction.class);
      for (Entry<Direction,BlockPartFace> entry : part.mapFaces.entrySet()) {
        BlockPartFace face = entry.getValue();
        // if the texture is liquid, update the tint index
        if (face.tintIndex != 1 && retextured.contains(face.texture.substring(1))) {
          updated = true;
          newFaces.put(entry.getKey(), new BlockPartFace(face.cullFace, 1, face.texture, face.blockFaceUV));
        } else {
          // otherwise use original face and make a copy for frost
          newFaces.put(entry.getKey(), face);
          frostFaces.put(entry.getKey(), new BlockPartFace(face.cullFace, -1, "frost", face.blockFaceUV));
        }
      }
      // frosted has all elements of normal, plus an overlay when relevant
      BlockPart newPart = updated ? new BlockPart(part.positionFrom, part.positionTo, newFaces, part.partRotation, part.shade) : part;
      warmElements.add(newPart);
      frostElements.add(newPart);
      // add frost element if anything happened
      if (!frostFaces.isEmpty()) {
        frostElements.add(new BlockPart(part.positionFrom, part.positionTo, frostFaces, part.partRotation, part.shade));
      }
    }

    // if nothing retextured, bake frosted and return simple baked model
    IBakedModel baked = SimpleBlockModel.bakeModel(owner, warmElements, modelTransform, overrides, spriteGetter, modelLocation);
    if (retextured.isEmpty()) {
      IBakedModel frosted = SimpleBlockModel.bakeModel(owner, frostElements, modelTransform, overrides, spriteGetter, modelLocation);
      return new FrostedBakedModel(baked, frosted);
    }

    // full dynamic baked model
    return new TexturedBakedModel(baked, owner, warmElements, frostElements, modelTransform, retextured);
  }

  /** Full baked model, does frost and fluid textures */
  private static class TexturedBakedModel extends DynamicBakedWrapper<IBakedModel> {
    private final Map<ResourceLocation,IBakedModel> warmCache = new HashMap<>();
    private final Map<ResourceLocation,IBakedModel> frostedCache = new HashMap<>();
    // data needed to rebake
    private final IModelConfiguration owner;
    private final IModelTransform transform;
    private final Set<String> retextured;
    /** Function to bake a warm model for the given texture */
    private final Function<ResourceLocation, IBakedModel> warmBakery;
    /** Function to bake a frosted model for the given texture */
    private final Function<ResourceLocation, IBakedModel> frostedBakery;
    protected TexturedBakedModel(IBakedModel originalModel, IModelConfiguration owner, List<BlockPart> warmElements, List<BlockPart> frostElements, IModelTransform transform, Set<String> fluidNames) {
      super(originalModel);
      this.owner = owner;
      this.transform = transform;
      this.retextured = fluidNames;
      this.warmBakery = name -> getFluidModel(name, warmElements);
      this.frostedBakery = name -> getFluidModel(name, frostElements);
    }

    /**
     * Bakes the baked model for the given fluid
     * @param fluid  Cauldron content name
     * @return  Baked model
     */
    private IBakedModel getFluidModel(ResourceLocation fluid, List<BlockPart> elements) {
      return SimpleBlockModel.bakeDynamic(new RetexturedConfiguration(owner, retextured, fluid), elements, transform);
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction direction, Random random, IModelData data) {
      ResourceLocation textureName = data.getData(CauldronTileEntity.TEXTURE);
      if (textureName == null) {
        return originalModel.getQuads(state, direction, random, data);
      }

      // serverside uses texture "name" rather than path, use the sprite getter to translate
      ResourceLocation texture = RecipesClientEvents.cauldronTextures.getTexture(textureName);
      // determine model variant
      IBakedModel baked = (data.getData(CauldronTileEntity.FROSTED) == Boolean.TRUE)
                          ? frostedCache.computeIfAbsent(texture, frostedBakery)
                          : warmCache.computeIfAbsent(texture, warmBakery);
      // return quads
      return baked.getQuads(state, direction, random, data);
    }
  }

  /** Simplier baked model for when textures are not needed */
  private static class FrostedBakedModel extends DynamicBakedWrapper<IBakedModel> {
    private final IBakedModel frosted;
    private FrostedBakedModel(IBakedModel originalModel, IBakedModel frosted) {
      super(originalModel);
      this.frosted = frosted;
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction direction, Random random, IModelData data) {
      return (data.getData(CauldronTileEntity.FROSTED) == Boolean.TRUE ? frosted : originalModel).getQuads(state, direction, random, data);
    }
  }

  /** Loader class */
  private static class Loader implements IModelLoader<CauldronModel> {
    private Loader() {}

    @Override
    public void onResourceManagerReload(IResourceManager resourceManager) {}

    @Override
    public CauldronModel read(JsonDeserializationContext context, JsonObject json) {
      SimpleBlockModel model = SimpleBlockModel.deserialize(context, json);
      Set<String> retextured = json.has("retextured") ? RetexturedModel.Loader.getRetextured(json) : Collections.emptySet();
      return new CauldronModel(model, retextured);
    }
  }
}
