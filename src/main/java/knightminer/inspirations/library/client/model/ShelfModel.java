package knightminer.inspirations.library.client.model;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import knightminer.inspirations.Inspirations;
import knightminer.inspirations.building.block.entity.ShelfBlockEntity;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockElement;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.client.model.geometry.IGeometryBakingContext;
import net.minecraftforge.client.model.geometry.IGeometryLoader;
import net.minecraftforge.client.model.geometry.IUnbakedGeometry;
import slimeknights.mantle.client.model.RetexturedModel;
import slimeknights.mantle.client.model.RetexturedModel.RetexturedContext;
import slimeknights.mantle.client.model.util.DynamicBakedWrapper;
import slimeknights.mantle.client.model.util.ModelHelper;
import slimeknights.mantle.client.model.util.SimpleBlockModel;
import slimeknights.mantle.util.RetexturedHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;

/**
 * Model that retextures a shelf while also adding in a list of books based on slot contents
 */
public class ShelfModel implements IUnbakedGeometry<ShelfModel> {
  /** Loader instance to register */
  public static final IGeometryLoader<ShelfModel> LOADER = ShelfModel::readModel;
  private final SimpleBlockModel model;
  private final Set<String> retextured;
  private final List<List<BlockElement>> books;

  protected ShelfModel(SimpleBlockModel model, Set<String> retextured, List<List<BlockElement>> books) {
    this.model = model;
    this.retextured = retextured;
    this.books = books;
  }

  @Override
  public void resolveParents(Function<ResourceLocation, UnbakedModel> modelGetter, IGeometryBakingContext context) {
    model.resolveParents(modelGetter, context);
  }

  @Override
  public BakedModel bake(IGeometryBakingContext owner, ModelBaker bakery, Function<Material,TextureAtlasSprite> spriteGetter, ModelState transforms, ItemOverrides overrides, ResourceLocation location) {
    Shelf model = new Shelf(owner, this.model, transforms, this.books);
    BakedModel baked = model.bake(spriteGetter, location);
    return new Baked(baked, model, RetexturedModel.getAllRetextured(owner, this.model, retextured));
  }

  /** Model loader logic */
  public static ShelfModel readModel(JsonObject json, JsonDeserializationContext context) {
    // basic model
    SimpleBlockModel model = SimpleBlockModel.deserialize(json, context);
    Set<String> retextured = RetexturedModel.getRetexturedNames(json);

    // books
    JsonArray bookArray = GsonHelper.getAsJsonArray(json, "books");
    if (bookArray.isEmpty()) {
      throw new JsonSyntaxException("Must have at least one book element");
    }
    ImmutableList.Builder<List<BlockElement>> builder = ImmutableList.builder();
    for (int i = 0; i < bookArray.size(); i++) {
      builder.add(SimpleBlockModel.getModelElements(context, bookArray.get(i), "books[" + i + "]"));
    }
    // final model
    return new ShelfModel(model, retextured, builder.build());
  }

  /**
   * Base shelf wrapper, contains logic to get books, but no books
   */
  private static class Shelf {
    /* Properties for baking */
    private final IGeometryBakingContext owner;
    private final SimpleBlockModel model;
    private final ModelState transform;
    /* Model books */
    private final List<List<BlockElement>> books;
    /* Cached baked model */
    private BakedModel baked;

    private Shelf(IGeometryBakingContext owner, SimpleBlockModel model, ModelState transform, List<List<BlockElement>> books) {
      this.owner = owner;
      this.model = model;
      this.transform = transform;
      this.books = books;
    }

    /**
     * Bakes and caches the model using the given properties
     * @param spriteGetter  Sprite getter function
     * @param location      Bake location
     * @return  Baked model
     */
    public BakedModel bake(Function<Material,TextureAtlasSprite> spriteGetter, ResourceLocation location) {
      List<BlockElement> elements = Lists.newArrayList(model.getElements());
      books.forEach(elements::addAll);
      baked = SimpleBlockModel.bakeModel(owner, elements, spriteGetter, transform, ItemOverrides.EMPTY, location);
      return baked;
    }

    /**
     * Gets the baked model, dynamically baking the unbaked one if missing
     * @return  Baked model
     */
    public BakedModel getBaked() {
      if (baked == null) {
        List<BlockElement> elements = Lists.newArrayList(model.getElements());
        books.forEach(elements::addAll);
        baked = model.bakeWithElements(owner, elements, transform);
      }
      return baked;
    }

    /**
     * Gets a bookshelf with the given texture
     * @param retextured  List of names to retexture
     * @param texture     Texture name
     * @return  Bookshelf textured with the given texture
     */
    public Shelf withTexture(Set<String> retextured, ResourceLocation texture) {
      return new Shelf(new RetexturedContext(owner, retextured, texture), model, transform, books);
    }

    /**
     * Gets a baked shelf using the given books list
     * @param modelBooks  Books to get
     * @return Shelf with the requested books
     */
    public BakedModel bakeWithBooks(int modelBooks) {
      List<BlockElement> elements = Lists.newArrayList(model.getElements());
      for (int i = 0; i < books.size(); i++) {
        int flag = 1 << i;
        if ((modelBooks & flag) == flag) {
          elements.addAll(books.get(i));
        }
      }
      return model.bakeWithElements(owner, elements, transform);
    }
  }

  /**
   * Baked shelf model instance
   */
  private static class Baked extends DynamicBakedWrapper<BakedModel> {
    /** Cache of texture to shelf model, used for items and to make crafting the shelf with books faster */
    private final Map<ResourceLocation,Shelf> texturedCache = new HashMap<>();
    /** Cache of shelf with books and texture, limited size */
    private final Cache<BookshelfCacheKey,BakedModel> bookshelfCache = CacheBuilder.newBuilder().maximumSize(30).build();

    /** Unbaked model */
    private final Shelf model;
    /** List to retexture */
    private final Set<String> retextured;
    /** Overrides instance */
    private final ItemOverrides overrides = new RetexturedOverride();

    /**
     * Gets a baked model with the given properties
     * @param baked       Default model
     * @param model       Shelf model for baking new shelves
     * @param retextured  List of textures for retexturing
     */
    protected Baked(BakedModel baked, Shelf model, Set<String> retextured) {
      super(baked);
      this.model = model;
      this.retextured = retextured;
    }

    /**
     * Gets the textured shelf for the given path
     * @param texture  Texture for shelf
     * @return  Textured shelf
     */
    private Shelf getTexturedShelf(@Nullable ResourceLocation texture) {
      if (texture == null) {
        return model;
      }
      return texturedCache.computeIfAbsent(texture, location -> model.withTexture(retextured, location));
    }

    /**
     * Gets the textured shelf for the given block
     * @param texture  Texture for shelf
     * @return  Textured shelf
     */
    private Shelf getTexturedShelf(Block texture) {
      return getTexturedShelf(ModelHelper.getParticleTexture(texture));
    }

    @Override
    public TextureAtlasSprite getParticleIcon(ModelData data) {
      // particle must be retextured, and must have a block
      if (retextured.contains("particle")) {
        Block block = data.get(RetexturedHelper.BLOCK_PROPERTY);
        if (block != null && block != Blocks.AIR) {
          return getTexturedShelf(block).getBaked().getParticleIcon(data);
        }
      }
      return originalModel.getParticleIcon(data);
    }

    @Nonnull
    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction direction, RandomSource random, ModelData data, @Nullable RenderType renderType) {
      if (data == ModelData.EMPTY) {
        return originalModel.getQuads(state, direction, random, data, renderType);
      }

      // if block is unset, default to null (no texture)
      Block block = data.get(RetexturedHelper.BLOCK_PROPERTY);
      ResourceLocation texture = null;
      if (block != null && block != Blocks.AIR) {
        texture = ModelHelper.getParticleTexture(block);
      }

      // if books unset, default to 0 (no books)
      Integer books = data.get(ShelfBlockEntity.BOOKS);
      if (books == null) {
        books = 0;
      }

      // fetch combo from cache, compute if needed
      BakedModel finalModel;
      try {
        BookshelfCacheKey key = new BookshelfCacheKey(texture, books);
        finalModel = bookshelfCache.get(key, () -> getTexturedShelf(key.texture).bakeWithBooks(key.books));
      } catch (ExecutionException e) {
        Inspirations.log.error(e);
        finalModel = originalModel;
      }
      return finalModel.getQuads(state, direction, random, data, renderType);
    }

    @Override
    public ItemOverrides getOverrides() {
      return overrides;
    }

    /** Override list to swap the texture in from NBT */
    private class RetexturedOverride extends ItemOverrides {
      @Nullable
      @Override
      public BakedModel resolve(BakedModel originalModel, ItemStack stack, @Nullable ClientLevel world, @Nullable LivingEntity entity, int seed) {
        if (stack.isEmpty() || !stack.hasTag()) {
          return originalModel;
        }
        // get the block first, ensuring its valid
        Block block = RetexturedHelper.getTexture(stack);
        if (block == Blocks.AIR) {
          return originalModel;
        }
        // if valid, use the block
        return getTexturedShelf(block).getBaked();
      }
    }
  }

  /** Key for bookshelf model cache, there is one cache per shelf model orientation */
  private static class BookshelfCacheKey {
    @Nullable
    protected ResourceLocation texture;
    protected int books;

    BookshelfCacheKey(@Nullable ResourceLocation texture, int books) {
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
      return this.books == that.books && Objects.equals(this.texture, that.texture);
    }

    @Override
    public int hashCode() {
      return (texture == null ? 0 : 31 * texture.hashCode()) + books;
    }
  }
}
