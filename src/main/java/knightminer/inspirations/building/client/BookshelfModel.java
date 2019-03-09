package knightminer.inspirations.building.client;

import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.annotation.Nullable;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.ImmutableMap;

import knightminer.inspirations.Inspirations;
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

	/** Book model cache, for internal use only */
	public static final Cache<BookshelfCacheKey, IBakedModel> BOOK_CACHE = CacheBuilder.newBuilder().maximumSize(30).build();
	public BookshelfModel(IBakedModel standard, IModel model, VertexFormat format) {
		super(standard, model, format, "texture", true);
	}

	@Override
	public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, long rand) {
		IBakedModel bakedModel = this.originalModel;
		if(state instanceof IExtendedBlockState) {
			IExtendedBlockState extendedState = (IExtendedBlockState) state;
			String texture = extendedState.getValue(BlockBookshelf.TEXTURE);
			Integer booksValue = extendedState.getValue(BlockBookshelf.BOOKS);
			int books = booksValue != null ? booksValue : 0;
			try {
				// grab the model from cache if present
				bakedModel = BOOK_CACHE.get(new BookshelfCacheKey(extendedState.getClean(), texture, books), () -> {
					// have books
					ImmutableMap.Builder<String, String> builder = ImmutableMap.builder();
					if(texture != null) {
						builder.put("texture", texture);
					}
					for(int i = 0; i < 14; i++) {
						// if there is no book in the slot, remove the texture so the quad is removed
						if((books & (1 << i)) == 0) {
							builder.put("#book" + i, "");
							builder.put("#bookLabel" + i, "");
						}
					}
					return getTexturedModel(builder.build());
				});
			} catch(ExecutionException e) {
				Inspirations.log.error(e);
			}
		}
		return bakedModel.getQuads(state, side, rand);
	}

	private static class BookshelfCacheKey {
		private IBlockState state;
		@Nullable
		private String texture;
		private int books;
		public BookshelfCacheKey(IBlockState state, @Nullable String texture, int books) {
			this.state = state;
			this.texture = texture;
			this.books = books;
		}

		@Override
		public boolean equals(Object o) {
			if(this == o) {
				return true;
			}
			if(o == null || getClass() != o.getClass()) {
				return false;
			}

			BookshelfCacheKey that = (BookshelfCacheKey) o;
			return this.books == that.books && this.state == that.state
					&& (this.texture == that.texture || this.texture != null && this.texture.equals(that.texture));
		}

		@Override
		public int hashCode() {
			return 31 * (31 * state.hashCode() + (texture == null ? 0 : texture.hashCode())) + books;
		}
	}
}
