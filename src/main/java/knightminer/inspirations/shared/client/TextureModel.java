package knightminer.inspirations.shared.client;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.collect.ImmutableMap;
import knightminer.inspirations.library.util.TagUtil;
import knightminer.inspirations.library.util.TextureBlockUtil;
import knightminer.inspirations.shared.SharedClientProxy;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.model.ModelRotation;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.model.BakedModelWrapper;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.data.ModelProperty;
import slimeknights.mantle.client.ModelHelper;

public class TextureModel extends BakedModelWrapper<IBakedModel> {
	private final Map<String, IBakedModel> cache = new HashMap<>();
	private IModel model;
	private final VertexFormat format;
	private final String textureKey;
	private boolean item;

	ModelProperty<String> TEXTURE = new ModelProperty<>();

	public TextureModel(IBakedModel originalModel, IModel model, VertexFormat format, String textureKey, boolean item) {
		super(originalModel);
		this.model = model;
		this.format = format;
		this.textureKey = textureKey;
		this.item = item;
	}

	@Nonnull
	@Override
	public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @Nonnull Random rand, @Nonnull IModelData extraData) {
		IBakedModel bakedModel = this.originalModel;
		String texture = extraData.getData(TEXTURE);
		if(texture != null) {
			bakedModel = getCachedTextureModel(texture);
		}
		return bakedModel.getQuads(state, side, rand);
	}

	protected IBakedModel getTexturedModel(ImmutableMap<String, String> textures) {
		return model.retexture(textures).bake(
				SharedClientProxy.modelLoader,
				(x) -> Minecraft.getInstance().getTextureMap().getSprite((ResourceLocation) x),
				ModelRotation.X0_Y0,
				this.format
		);
	}

	protected IBakedModel getCachedTextureModel(String texture) {
		return cache.computeIfAbsent(texture, (tex) -> getTexturedModel(ImmutableMap.of(textureKey, tex)));
	}

	@Override
	public ItemOverrideList getOverrides() {
		return item ? ItemTextureOverride.INSTANCE : super.getOverrides();
	}

	private static class ItemTextureOverride extends ItemOverrideList {

		static ItemTextureOverride INSTANCE = new ItemTextureOverride();

		private ItemTextureOverride() {
			super();
		}

		@Nullable
		@Override
		public IBakedModel getModelWithOverrides(@Nonnull IBakedModel originalModel, @Nonnull ItemStack stack, @Nullable World world, @Nullable LivingEntity entity) {
			if(originalModel instanceof TextureModel) {
				// read out the data on the itemstack
				ItemStack blockStack = ItemStack.read(TagUtil.getTagSafe(stack).getCompound(TextureBlockUtil.TAG_TEXTURE));
				if(!blockStack.isEmpty()) {
					// get model from data
					Item item = blockStack.getItem();
					Block block = Block.getBlockFromItem(item);
					ResourceLocation texture = ModelHelper.getTextureFromBlockstate(block.getDefaultState()).getName();
					TextureModel textureModel = (TextureModel) originalModel;
					return textureModel.getCachedTextureModel(texture.toString());
				}
			}

			return originalModel;
		}
	}
}
