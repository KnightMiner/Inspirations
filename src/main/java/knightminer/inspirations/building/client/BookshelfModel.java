package knightminer.inspirations.building.client;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.ImmutableMap;
import knightminer.inspirations.Inspirations;
import knightminer.inspirations.building.tileentity.BookshelfTileEntity;
import knightminer.inspirations.shared.client.TextureModel;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.data.IModelData;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ExecutionException;

// FIXME: use model loader
public class BookshelfModel extends TextureModel {

  /**
   * Book model cache, for internal use only
   */
  public static final Cache<BookshelfCacheKey,IBakedModel> BOOK_CACHE = CacheBuilder.newBuilder().maximumSize(30).build();

  public BookshelfModel(ResourceLocation location, ModelBakery loader, IBakedModel standard) {
    super(location, loader, standard, "texture", false);
  }

  @Override
  public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, Random rand, IModelData extraData) {
    if (state == null) {
      return super.getQuads(null, side, rand, extraData);
    }
    IBakedModel bakedModel = this.originalModel;
    String texture = extraData.getData(BookshelfTileEntity.TEXTURE);
    Integer booksValue = extraData.getData(BookshelfTileEntity.BOOKS);
    int books = booksValue != null ? booksValue : 0;
    try {
      // grab the model from cache if present
      bakedModel = BOOK_CACHE.get(new BookshelfCacheKey(state, texture, books), () -> {
        // have books
        ImmutableMap.Builder<String,String> builder = ImmutableMap.builder();
        if (texture != null) {
          builder.put("texture", texture);
        }
        for (int i = 0; i < 14; i++) {
          // if there is no book in the slot, remove the texture so the quad is removed
          if ((books & (1 << i)) == 0) {
            builder.put("#book" + i, "");
            builder.put("#bookLabel" + i, "");
          }
        }
        return getTexturedModel(builder.build());
      });
    } catch (ExecutionException e) {
      Inspirations.log.error(e);
    }
    return bakedModel.getQuads(state, side, rand, extraData);
  }

  private static class BookshelfCacheKey {
    private BlockState state;
    @Nullable
    private String texture;
    private int books;

    BookshelfCacheKey(BlockState state, @Nullable String texture, int books) {
      this.state = state;
      this.texture = texture;
      this.books = books;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }

      BookshelfCacheKey that = (BookshelfCacheKey)o;
      return this.books == that.books && this.state == that.state && Objects.equals(this.texture, that.texture);
    }

    @Override
    public int hashCode() {
      return 31 * (31 * state.hashCode() + (texture == null ? 0 : texture.hashCode())) + books;
    }
  }
}
