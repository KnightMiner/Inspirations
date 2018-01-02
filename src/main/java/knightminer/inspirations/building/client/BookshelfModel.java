package knightminer.inspirations.building.client;

import java.util.List;

import com.google.common.collect.ImmutableMap;

import knightminer.inspirations.building.block.BlockBookshelf;
import knightminer.inspirations.shared.client.TextureModel;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.common.property.IExtendedBlockState;

public class BookshelfModel extends TextureModel {

	public BookshelfModel(IBakedModel standard, IModel model, VertexFormat format) {
		super(standard, model, format, "texture", true);
	}

	@Override
	public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, long rand) {
		IBakedModel bakedModel = this.originalModel;
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
}
