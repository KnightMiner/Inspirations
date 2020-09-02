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
   * @param retextured  Names of fluid textures to retexture
   */
  protected CauldronModel(SimpleBlockModel model, Set<String> retextured) {
    this.model = model;
    this.retextured = retextured;
  }

  @Override
  public Collection<RenderMaterial> getTextures(IModelConfiguration owner, Function<ResourceLocation,IUnbakedModel> modelGetter, Set<Pair<String,String>> missingTextureErrors) {
    return model.getTextures(owner, modelGetter, missingTextureErrors);
  }

  @Override
  public IBakedModel bake(IModelConfiguration owner, ModelBakery bakery, Function<RenderMaterial,TextureAtlasSprite> spriteGetter, IModelTransform modelTransform, ItemOverrideList overrides, ResourceLocation modelLocation) {
    List<BlockPart> elements = new ArrayList<>();
    for (BlockPart part : model.getElements()) {
      boolean updated = false;
      Map<Direction, BlockPartFace> newFaces = new EnumMap<>(Direction.class);
      for (Entry<Direction,BlockPartFace> entry : part.mapFaces.entrySet()) {
        BlockPartFace face = entry.getValue();
        if (face.tintIndex != 1 && retextured.contains(face.texture.substring(1))) {
          updated = true;
          newFaces.put(entry.getKey(), new BlockPartFace(face.cullFace, 1, face.texture, face.blockFaceUV));
        } else {
          newFaces.put(entry.getKey(), face);
        }
      }
      if (updated) {
        elements.add(new BlockPart(part.positionFrom, part.positionTo, newFaces, part.partRotation, part.shade));
      } else {
        elements.add(part);
      }
    }

    IBakedModel baked = SimpleBlockModel.bakeModel(owner, elements, modelTransform, overrides, spriteGetter, modelLocation);
    return new BakedModel(baked, owner, elements, modelTransform, RetexturedModel.getAllRetextured(owner, model, retextured));
  }

  /** Baked model, to swap out textures dynamically */
  private static class BakedModel extends DynamicBakedWrapper<IBakedModel> {
    private final Map<ResourceLocation,IBakedModel> fluidCache = new HashMap<>();
    // data needed to rebake
    private final IModelConfiguration owner;
    private final List<BlockPart> elements;
    private final IModelTransform transform;
    private final Set<String> retextured;
    protected BakedModel(IBakedModel originalModel, IModelConfiguration owner, List<BlockPart> elements, IModelTransform transform, Set<String> fluidNames) {
      super(originalModel);
      this.owner = owner;
      this.elements = elements;
      this.transform = transform;
      this.retextured = fluidNames;

      // for each part face using the fluid texture, set the tint index to 1. Saves having to recreate models
      // the vanilla model does this using tint index 0, but that is problematic as that also tints the particle texture
      // plus, ensures resource pack support if a resource pack does weird cauldron stuff

    }

    /**
     * Bakes the baked model for the given fluid
     * @param fluid  Cauldron content name
     * @return  Baked model
     */
    private IBakedModel getFluidModel(ResourceLocation fluid) {
      return SimpleBlockModel.bakeDynamic(new RetexturedConfiguration(owner, retextured, fluid), elements, transform);
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction direction, Random random, IModelData data) {
      ResourceLocation texture = data.getData(CauldronTileEntity.TEXTURE);
      if (texture == null) {
        return originalModel.getQuads(state, direction, random, data);
      }
      // serverside uses texture "name" rather than path, use the sprite getter to translate
      return fluidCache.computeIfAbsent(RecipesClientEvents.cauldronTextures.getTexture(texture), this::getFluidModel).getQuads(state, direction, random, data);
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
      Set<String> retextured = RetexturedModel.Loader.getRetextured(json);
      return new CauldronModel(model, retextured);
    }
  }
}
