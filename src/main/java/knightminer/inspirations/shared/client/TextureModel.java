package knightminer.inspirations.shared.client;

import java.util.List;

import javax.annotation.Nonnull;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import knightminer.inspirations.library.util.TagUtil;
import knightminer.inspirations.library.util.TextureBlockUtil;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.client.model.BakedModelWrapper;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.property.IExtendedBlockState;
import slimeknights.mantle.client.ModelHelper;

public class TextureModel extends BakedModelWrapper<IBakedModel> {

	private IModel model;
	private final VertexFormat format;
	private final String textureKey;
	private boolean item;
	public TextureModel(IBakedModel originalModel, IModel model, VertexFormat format, String textureKey, boolean item) {
		super(originalModel);
		this.model = model;
		this.format = format;
		this.textureKey = textureKey;
		this.item = item;
	}

	@Override
	public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, long rand) {
		IBakedModel bakedModel = this.originalModel;
		if(state instanceof IExtendedBlockState) {
			IExtendedBlockState extendedState = (IExtendedBlockState) state;

			String texture = extendedState.getValue(TextureBlockUtil.TEXTURE_PROP);
			if(texture != null) {
				bakedModel = getTexturedModel(ImmutableMap.of(textureKey, texture));
			}
		}
		return bakedModel.getQuads(state, side, rand);
	}

	protected IBakedModel getTexturedModel(ImmutableMap<String, String> textures) {
		IModel retextured = model.retexture(textures);
		return retextured.bake(retextured.getDefaultState(), format, ModelLoader.defaultTextureGetter());
	}

	@Nonnull
	@Override
	public ItemOverrideList getOverrides() {
		return item ? ItemTextureOverride.INSTANCE : super.getOverrides();
	}

	private static class ItemTextureOverride extends ItemOverrideList {

		static ItemTextureOverride INSTANCE = new ItemTextureOverride();

		private ItemTextureOverride() {
			super(ImmutableList.of());
		}

		@Nonnull
		@Override
		public IBakedModel handleItemState(@Nonnull IBakedModel originalModel, ItemStack stack, World world, EntityLivingBase entity) {
			if(originalModel instanceof TextureModel) {
				// read out the data on the itemstack
				ItemStack blockStack = new ItemStack(TagUtil.getTagSafe(stack).getCompoundTag(TextureBlockUtil.TAG_TEXTURE));
				if(!blockStack.isEmpty()) {
					// get model from data
					Item item = blockStack.getItem();
					Block block = Block.getBlockFromItem(item);
					String texture = ModelHelper.getTextureFromBlock(block, item.getMetadata(blockStack)).getIconName();
					TextureModel textureModel = (TextureModel) originalModel;
					return textureModel.getTexturedModel(ImmutableMap.of(textureModel.textureKey, texture));
				}
			}

			return originalModel;
		}
	}
}
