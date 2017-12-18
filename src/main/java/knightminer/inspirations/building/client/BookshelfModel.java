package knightminer.inspirations.building.client;

import java.util.List;

import javax.annotation.Nonnull;
import javax.vecmath.Matrix4f;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;

import knightminer.inspirations.building.block.BlockBookshelf;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.common.property.IExtendedBlockState;

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
				if(!extendedState.getValue(BlockBookshelf.BOOKS[i])) {
					builder.put("#book" + i, "");
					builder.put("#bookLabel" + i, "");
				}
			}
			IModel retextured = model.retexture(builder.build());
			bakedModel = retextured.bake(retextured.getDefaultState(), format, textureGetter);
		}
		return bakedModel.getQuads(state, side, rand);
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
		return standard.getOverrides();
	}

	@Override
	public Pair<? extends IBakedModel, Matrix4f> handlePerspective(ItemCameraTransforms.TransformType cameraTransformType) {
		Pair<? extends IBakedModel, Matrix4f> pair = standard.handlePerspective(cameraTransformType);
		return Pair.of(this, pair.getRight());
	}

}
