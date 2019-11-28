package knightminer.inspirations.shared.client;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import knightminer.inspirations.Inspirations;
import knightminer.inspirations.library.util.TextureBlockUtil;
import knightminer.inspirations.shared.SharedClientProxy;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.BlockModel;
import net.minecraft.client.renderer.model.BlockPart;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.model.ModelRotation;
import net.minecraft.client.renderer.model.Variant;
import net.minecraft.client.renderer.model.VariantList;
import net.minecraft.client.renderer.model.WeightedBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.model.BakedModelWrapper;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.data.ModelProperty;
import slimeknights.mantle.client.ModelHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;

public class TextureModel extends BakedModelWrapper<IBakedModel> {
	private final Map<String, IBakedModel> cache = new HashMap<>();
	private IModel unbakedModel;
	private final VertexFormat format;
	private final String textureKey;
	private boolean item;
	private Map<ResourceLocation,IModel> unbakedChildren;

	ModelProperty<String> TEXTURE = TextureBlockUtil.TEXTURE_PROP;

	/**
	 * Creates a new instance of the unbakedModel
	 * @param originalModel  Original baked unbakedModel
	 * @param unbakedModel   Unbaked, retexturable model
	 * @param format         Model format, either BLOCK or ITEM
	 * @param textureKey     Name of the primary key to retexture
	 * @param item           If true, add logic to retexture the item model too
	 */
	public TextureModel(IBakedModel originalModel, IModel unbakedModel, VertexFormat format, String textureKey, boolean item) {
		super(originalModel);
		this.unbakedModel = unbakedModel;
		this.format = format;
		this.textureKey = textureKey;
		this.item = item;
	}

	/**
	 * Fetches all children models required by this unbakedModel. Done because it is bad practice and prone to threading errors if we fetch them at runtime
	 * @param loader  Forge Model Loader
	 */
	public void fetchChildren(ModelLoader loader) {
		// only variant lists have children that we need to fetch at this time
		if (unbakedModel instanceof VariantList) {
			VariantList list = (VariantList)unbakedModel;
			// nothing to do if empty
			if (list.getVariantList().isEmpty()) {
				unbakedChildren = ImmutableMap.of();
				return;
			}

			// make an immutable map of all children
			ImmutableMap.Builder<ResourceLocation, IModel> builder = new ImmutableMap.Builder<>();
			// skip loading the same unbakedModel multiple times, it will fail again
			Set<ResourceLocation> loaded = new HashSet<>();
			// needed for getTextures, though we will just discard it when done
			Set<String> missingTextures = new HashSet<>();
			for(Variant variant : list.getVariantList()) {
				ResourceLocation location = variant.getModelLocation();
				if (loaded.contains(location)) {
					continue;
				}
				loaded.add(location);
				try {
					// using getUnbakedModel as it fetches models with the parent already loaded
					// plus, already needed to be able to retexture BlockModel for the sake of item models
					builder.put(location, loader.getUnbakedModel(location));
				} catch (Exception e) {
					Inspirations.log.error("Error loading unbaked model for " + location, e);
				}
			}
			unbakedChildren = builder.build();
		}
	}

	@Nonnull
	@Override
	public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @Nonnull Random rand, @Nonnull IModelData extraData) {
		IBakedModel bakedModel = this.originalModel;
		String texture = extraData.getData(TEXTURE);
		if(texture != null) {
			bakedModel = getCachedTextureModel(texture);
		}
		return bakedModel.getQuads(state, side, rand, extraData);
	}

	/* Retexturing logic */

	/**
	 * Retextures the unbakedModel based on the given texture map
	 * @param textures  Map of textures to modify
	 * @return  Retextured and baked unbakedModel
	 */
	protected IBakedModel getTexturedModel(ImmutableMap<String, String> textures) {
		if (textures.isEmpty()) {
			return originalModel;
		}

		// variant list needs to be retextured while it is baked, so special case that
		if (unbakedModel instanceof VariantList) {
			return retextureVariantList((VariantList)unbakedModel, textures);
		}
		return retexture(unbakedModel, textures).bake(
				SharedClientProxy.modelLoader,
				(Function<ResourceLocation, TextureAtlasSprite>) Minecraft.getInstance().getTextureMap()::getSprite,
				ModelRotation.X0_Y0, this.format);
	}

	/**
	 * Caching version of getTexturedModel. Requires only a single texture, from textureKey, to be used
	 * @param texture  New texture path
	 * @return  Baked unbakedModel, pulling from the cache if present
	 */
	protected IBakedModel getCachedTextureModel(String texture) {
		return cache.computeIfAbsent(texture, (tex) -> getTexturedModel(ImmutableMap.of(textureKey, tex)));
	}

	/**
	 * Retexures a unbakedModel, calling the override for BlockModel if needed
	 * @param model     Model to retexture
	 * @param textures  New textures
	 * @return  Retextured unbakedModel
	 */
	private IModel retexture(IModel model, ImmutableMap<String,String> textures) {
		if (model instanceof BlockModel) {
			return retextureBlockModel((BlockModel)model, textures);
		}
		return model.retexture(textures);
	}

	/**
	 * Private copy of ModelLoader.VanillaModelWrapper::retexture. Needed since BlockModel is not retexturable, but is used by item models
	 * @param model     Model to retexture
	 * @param textures  New textures
	 * @return  Retextured unbakedModel
	 */
	private BlockModel retextureBlockModel(BlockModel model, ImmutableMap<String,String> textures) {
		List<BlockPart> elements = Lists.newArrayList(); //We have to duplicate this so we can edit it below.
		for (BlockPart part : model.getElements()) {
			elements.add(new BlockPart(part.positionFrom, part.positionTo, Maps.newHashMap(part.mapFaces), part.partRotation, part.shade));
		}

		BlockModel newModel = new BlockModel(model.getParentLocation(), elements,
																				 Maps.newHashMap(model.textures), model.isAmbientOcclusion(), model.isGui3d(),
																				 model.getAllTransforms(), Lists.newArrayList(model.getOverrides()));
		newModel.name = model.name;
		newModel.parent = model.parent;

		Set<String> removed = Sets.newHashSet();
		for (Map.Entry<String, String> e : textures.entrySet()) {
			if ("".equals(e.getValue())) {
				removed.add(e.getKey());
				newModel.textures.remove(e.getKey());
			}
			else {
				newModel.textures.put(e.getKey(), e.getValue());
			}
		}

		// Map the unbakedModel's texture references as if it was the parent of a unbakedModel with the retexture map as its textures.
		Map<String, String> remapped = Maps.newHashMap();
		for (Map.Entry<String, String> e : newModel.textures.entrySet()) {
			if (e.getValue().startsWith("#")) {
				String key = e.getValue().substring(1);
				if (newModel.textures.containsKey(key)) {
					remapped.put(e.getKey(), newModel.textures.get(key));
				}
			}
		}

		newModel.textures.putAll(remapped);

		//Remove any faces that use a null texture, this is for performance reasons, also allows some cool layering stuff.
		for (BlockPart part : newModel.getElements()) {
			part.mapFaces.entrySet().removeIf(entry -> removed.contains(entry.getValue().texture));
		}

		return newModel;
	}

	/**
	 * Special logic to retexture instances of VariantList, as they need to be retextured while baking
	 * @param list      VariantList instance
	 * @param textures  Textures to use in retexturing
	 * @return
	 */
	private IBakedModel retextureVariantList(VariantList list, ImmutableMap<String, String> textures) {
		// nothing to do if no variants
		if (list.getVariantList().isEmpty()) {
			return originalModel;
		} else {
			// this logic is based off VariantList::bake, difference is each child unbakedModel is retextured
			WeightedBakedModel.Builder builder = new WeightedBakedModel.Builder();
			for(Variant variant : list.getVariantList()) {
				IModel model = unbakedChildren.get(variant.getModelLocation());
				if (model == null) {
					continue;
				}
				IBakedModel ibakedmodel = retexture(model, textures).bake(
						SharedClientProxy.modelLoader,
						(Function<ResourceLocation, TextureAtlasSprite>) Minecraft.getInstance().getTextureMap()::getSprite,
						variant,
						this.format
																																);
				builder.add(ibakedmodel, variant.getWeight());
			}

			// might be null if all children are missing
			IBakedModel baked = builder.build();
			return baked != null ? baked : originalModel;
		}
	}

	/* Item model logic */

	@Nonnull
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
		public IBakedModel getModelWithOverrides(@Nonnull IBakedModel originalModel, @Nonnull ItemStack stack, @Nullable World world, @Nullable LivingEntity entity) {
			if(originalModel instanceof TextureModel) {
				// read out the data on the itemstack
				Block block = TextureBlockUtil.getTextureBlock(stack);
				if(block != Blocks.AIR) {
					ResourceLocation texture = ModelHelper.getTextureFromBlockstate(block.getDefaultState()).getName();
					TextureModel textureModel = (TextureModel) originalModel;
					return textureModel.getCachedTextureModel(texture.toString());
				}
			}

			return originalModel;
		}
	}
}
