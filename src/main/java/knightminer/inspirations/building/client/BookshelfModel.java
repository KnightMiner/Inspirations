package knightminer.inspirations.building.client;

import java.util.List;

import javax.annotation.Nonnull;
import javax.vecmath.Matrix4f;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import knightminer.inspirations.building.block.BlockBookshelf;
import knightminer.inspirations.library.util.RecipeUtil;
import knightminer.inspirations.library.util.TagUtil;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.common.property.IExtendedBlockState;
import slimeknights.mantle.client.ModelHelper;

public class BookshelfModel implements IBakedModel {

	public static final Function<ResourceLocation, TextureAtlasSprite> textureGetter = location -> {
		assert location != null;
		return Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(location.toString());
	};
	private IBakedModel standard;
	private IModel model;
	private final VertexFormat format;
	public BookshelfModel(IBakedModel standard, IModel model, VertexFormat format) {
		this.standard = standard;
		this.model = model;
		this.format = format;
	}

	@Override
	public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, long rand) {
		IBakedModel bakedModel = standard;
		if(state instanceof IExtendedBlockState) {
			IExtendedBlockState extendedState = (IExtendedBlockState) state;
			ImmutableMap.Builder<String, String> builder = ImmutableMap.builder();

			for(int i = 0; i < 14; i++) {
				// if there is no book in the slot, remove the texture so the quad is removed
				if(extendedState.getValue(BlockBookshelf.BOOKS[i]) != Boolean.TRUE) {
					builder.put("#book" + i, "");
					builder.put("#bookLabel" + i, "");
				}
			}

			String texture = extendedState.getValue(BlockBookshelf.TEXTURE);
			if(texture != null) {
				builder.put("texture", texture);
			}

			bakedModel = getTexturedModel(builder.build());
		}
		return bakedModel.getQuads(state, side, rand);
	}

	private IBakedModel getTexturedModel(ImmutableMap<String, String> textures) {
		IModel retextured = model.retexture(textures);
		return retextured.bake(retextured.getDefaultState(), format, textureGetter);
	}

	@Override
	public boolean isAmbientOcclusion() {
		return standard.isAmbientOcclusion();
	}

	@Override
	public boolean isGui3d() {
		return standard.isGui3d();
	}

	@Override
	public boolean isBuiltInRenderer() {
		return standard.isBuiltInRenderer();
	}

	@Nonnull
	@Override
	public TextureAtlasSprite getParticleTexture() {
		return standard.getParticleTexture();
	}

	@Nonnull
	@Override
	@Deprecated
	public ItemCameraTransforms getItemCameraTransforms() {
		return standard.getItemCameraTransforms();
	}

	@Nonnull
	@Override
	public ItemOverrideList getOverrides() {
		return BookshelfTextureOverride.INSTANCE;
	}

	@Override
	public Pair<? extends IBakedModel, Matrix4f> handlePerspective(ItemCameraTransforms.TransformType cameraTransformType) {
		Pair<? extends IBakedModel, Matrix4f> pair = standard.handlePerspective(cameraTransformType);
		return Pair.of(this, pair.getRight());
	}


	private static class BookshelfTextureOverride extends ItemOverrideList {

		static BookshelfTextureOverride INSTANCE = new BookshelfTextureOverride();

		private BookshelfTextureOverride() {
			super(ImmutableList.of());
		}

		@Nonnull
		@Override
		public IBakedModel handleItemState(@Nonnull IBakedModel originalModel, ItemStack stack, World world, EntityLivingBase entity) {
			if(originalModel instanceof BookshelfModel) {
				// read out the data on the itemstack
				ItemStack blockStack = new ItemStack(TagUtil.getTagSafe(stack).getCompoundTag(RecipeUtil.TAG_TEXTURE));
				if(!blockStack.isEmpty()) {
					// get model from data
					Block block = Block.getBlockFromItem(blockStack.getItem());
					String texture = ModelHelper.getTextureFromBlock(block, blockStack.getItemDamage()).getIconName();
					return ((BookshelfModel) originalModel).getTexturedModel(ImmutableMap.of("texture", texture));
				}
			}

			return originalModel;
		}
	}
}
