package knightminer.inspirations.shared.client;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import knightminer.inspirations.Inspirations;
import knightminer.inspirations.library.client.ClientUtil;
import knightminer.inspirations.library.util.TextureBlockUtil;
import knightminer.inspirations.shared.SharedClientEvents;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.BlockModel;
import net.minecraft.client.renderer.model.BlockPart;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.IUnbakedModel;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.client.renderer.model.ModelRotation;
import net.minecraft.client.renderer.model.RenderMaterial;
import net.minecraft.client.renderer.model.Variant;
import net.minecraft.client.renderer.model.VariantList;
import net.minecraft.client.renderer.model.WeightedBakedModel;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.BakedModelWrapper;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.data.ModelProperty;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

// TODO: update to use a model loader
@SuppressWarnings("WeakerAccess")
public class TextureModel extends BakedModelWrapper<IBakedModel> {
  private final Map<String,IBakedModel> cache = new HashMap<>();
  private final ResourceLocation location;
  private final ModelBakery loader;
  private final IUnbakedModel unbakedModel;
  private final String textureKey;
  private boolean item;
  private Map<ResourceLocation,IUnbakedModel> unbakedChildren = Collections.emptyMap();
  private final ModelProperty<String> TEXTURE = TextureBlockUtil.TEXTURE_PROP;

  /**
   * Creates a new instance of the unbakedModel
   * @param location      Location of the model being replaced
   * @param loader        Model loader instance to rebake models
   * @param originalModel Original baked unbakedModel
   * @param textureKey    Name of the primary key to retexture
   * @param item          If true, add logic to retexture the item model too
   */
  public TextureModel(ResourceLocation location, ModelBakery loader, IBakedModel originalModel, String textureKey, boolean item) {
    super(originalModel);
    this.location = location;
    this.loader = loader;
    this.unbakedModel = loader.getUnbakedModel(location);
    this.textureKey = textureKey;
    this.item = item;
    this.fetchDependents();
  }

  /**
   * Ensures parents and children of this model are properly fetched
   */
  private void fetchDependents() {
    // needed for getTextures, though we just discard it these are printed elsewhere in loading
    Set<Pair<String,String>> missingTextures = Sets.newLinkedHashSet();

    // load the parent of the main model
    unbakedModel.getTextures(loader::getUnbakedModel, missingTextures);

    // variant lists have children that we need to fetch at this time
    if (unbakedModel instanceof VariantList) {
      VariantList list = (VariantList)unbakedModel;
      // nothing to do if empty
      if (list.getVariantList().isEmpty()) {
        unbakedChildren = ImmutableMap.of();
        return;
      }

      // make an immutable map of all children
      ImmutableMap.Builder<ResourceLocation,IUnbakedModel> builder = new ImmutableMap.Builder<>();
      // skip loading the same unbakedModel multiple times, it will fail again
      Set<ResourceLocation> loaded = new HashSet<>();
      for (Variant variant : list.getVariantList()) {
        ResourceLocation variantLocation = variant.getModelLocation();
        if (loaded.contains(variantLocation)) {
          continue;
        }
        loaded.add(variantLocation);
        try {
          // using getUnbakedModel as its much simplier, and typically the parent is already loaded
          IUnbakedModel model = loader.getUnbakedModel(variantLocation);
          // run getTextures to ensure the parent is loaded
          model.getTextures(loader::getUnbakedModel, missingTextures);
          // store it to fetch when texturing
          builder.put(variantLocation, model);
        } catch (Exception e) {
          Inspirations.log.error("Error loading unbaked model for " + variantLocation, e);
        }
      }
      unbakedChildren = builder.build();
    }
  }

  @Override
  public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, Random rand, IModelData extraData) {
    IBakedModel bakedModel = this.originalModel;
    String texture = extraData.getData(TEXTURE);
    if (texture != null) {
      bakedModel = getCachedTextureModel(texture);
    }
    return bakedModel.getQuads(state, side, rand, extraData);
  }

  /* Retexturing logic */

  /**
   * Retextures the unbakedModel based on the given texture map
   * @param textures Map of textures to modify
   * @return Retextured and baked unbakedModel
   */
  protected IBakedModel getTexturedModel(ImmutableMap<String,String> textures) {
    if (textures.isEmpty()) {
      return originalModel;
    }

    // variant list needs to be retextured while it is baked, so special case that
    if (unbakedModel instanceof VariantList) {
      return retextureVariantList((VariantList)unbakedModel, textures);
    }
    // retexture the model
    IBakedModel textured = retexture(unbakedModel, textures).bakeModel(
        SharedClientEvents.modelLoader,
        loader.getSpriteMap()::getSprite,
        ModelRotation.X0_Y0,
        this.location
                                                                      );
    return textured == null ? originalModel : textured;
  }

  /**
   * Caching version of getTexturedModel. Requires only a single texture, from textureKey, to be used
   * @param texture New texture path
   * @return Baked unbakedModel, pulling from the cache if present
   */
  protected IBakedModel getCachedTextureModel(String texture) {
    return cache.computeIfAbsent(texture, (tex) -> getTexturedModel(ImmutableMap.of(textureKey, tex)));
  }

  /**
   * Retexures a unbakedModel, calling the override for BlockModel if needed
   * @param model    Model to retexture
   * @param textures New textures
   * @return Retextured unbakedModel
   */
  private IUnbakedModel retexture(IUnbakedModel model, ImmutableMap<String,String> textures) {
    if (model instanceof BlockModel) {
      return retextureBlockModel((BlockModel)model, textures);
    }
    Inspirations.log.error("Failed to retexture model of class {}", model.getClass());
    return model;
  }

  /**
   * Private copy of ModelLoader.VanillaModelWrapper::retexture. Needed since BlockModel is not retexturable, but is used by item models
   * @param model    Model to retexture
   * @param textures New textures
   * @return Retextured unbakedModel
   */
  private BlockModel retextureBlockModel(BlockModel model, ImmutableMap<String,String> textures) {
    List<BlockPart> elements = Lists.newArrayList(); //We have to duplicate this so we can edit it below.
    for (BlockPart part : model.getElements()) {
      elements.add(new BlockPart(part.positionFrom, part.positionTo, Maps.newHashMap(part.mapFaces), part.partRotation, part.shade));
    }

    BlockModel newModel = new BlockModel(model.getParentLocation(), elements,
                                         Maps.newHashMap(model.textures), model.isAmbientOcclusion(), model.func_230176_c_(),
                                         model.getAllTransforms(), Lists.newArrayList(model.getOverrides()));
    newModel.name = model.name;
    newModel.parent = model.parent;

    Set<String> removed = Sets.newHashSet();
    for (Map.Entry<String,String> e : textures.entrySet()) {
      if ("".equals(e.getValue())) {
        removed.add(e.getKey());
        newModel.textures.remove(e.getKey());
      } else {
        newModel.textures.put(e.getKey(), Either.left(ModelLoaderRegistry.blockMaterial(e.getValue())));
      }
    }

    // Map the unbakedModel's texture references as if it was the parent of a unbakedModel with the retexture map as its textures.
    Map<String,Either<RenderMaterial,String>> remapped = Maps.newHashMap();
    for (Map.Entry<String,Either<RenderMaterial,String>> e : newModel.textures.entrySet()) {
      Either<RenderMaterial,String> either = e.getValue();
      either.ifRight((path) -> {
        if (path.startsWith("#")) {
          String key = path.substring(1);
          if (newModel.textures.containsKey(key)) {
            remapped.put(e.getKey(), newModel.textures.get(key));
          }
        }
      });
    }
    newModel.textures.putAll(remapped);

    // Remove any faces that use a null texture, this is for performance reasons, also allows some cool layering stuff.
    for (BlockPart part : newModel.getElements()) {
      part.mapFaces.entrySet().removeIf(entry -> removed.contains(entry.getValue().texture));
    }

    return newModel;
  }

  /**
   * Special logic to retexture instances of VariantList, as they need to be retextured while baking
   * @param list     VariantList instance
   * @param textures Textures to use in retexturing
   * @return Retextured variant list model
   */
  private IBakedModel retextureVariantList(VariantList list, ImmutableMap<String,String> textures) {
    // nothing to do if no variants
    if (list.getVariantList().isEmpty()) {
      return originalModel;
    } else {
      // this logic is based off VariantList::bake, difference is each child unbakedModel is retextured
      WeightedBakedModel.Builder builder = new WeightedBakedModel.Builder();
      for (Variant variant : list.getVariantList()) {
        IUnbakedModel model = unbakedChildren.get(variant.getModelLocation());
        if (model == null) {
          continue;
        }
        IBakedModel ibakedmodel = retexture(model, textures).bakeModel(
            SharedClientEvents.modelLoader,
            loader.getSpriteMap()::getSprite,
            variant,
            this.location
                                                                      );
        builder.add(ibakedmodel, variant.getWeight());
      }

      // might be null if all children are missing
      IBakedModel baked = builder.build();
      return baked != null ? baked : originalModel;
    }
  }

  /* Item model logic */

  @Override
  public ItemOverrideList getOverrides() {
    return item ? ItemTextureOverride.INSTANCE : super.getOverrides();
  }

  private static class ItemTextureOverride extends ItemOverrideList {
    private static final ItemTextureOverride INSTANCE = new ItemTextureOverride();

    private ItemTextureOverride() {
      super();
    }

    @Nullable
    @Override
    public IBakedModel func_239290_a_(IBakedModel originalModel, ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity entity) {
      if (originalModel instanceof TextureModel) {
        // read out the data on the itemstack
        Block block = TextureBlockUtil.getTextureBlock(stack);
        if (block != Blocks.AIR) {
          ResourceLocation texture = ClientUtil.getTextureFromBlock(block);
          TextureModel textureModel = (TextureModel)originalModel;
          return textureModel.getCachedTextureModel(texture.toString());
        }
      }

      return originalModel;
    }
  }
}
