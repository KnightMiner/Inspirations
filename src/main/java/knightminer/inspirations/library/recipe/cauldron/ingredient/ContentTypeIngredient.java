package knightminer.inspirations.library.recipe.cauldron.ingredient;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import io.netty.handler.codec.DecoderException;
import knightminer.inspirations.library.recipe.cauldron.CauldronContentTypes;
import knightminer.inspirations.library.recipe.cauldron.CauldronIngredients;
import knightminer.inspirations.library.recipe.cauldron.contents.ICauldronContents;
import knightminer.inspirations.library.recipe.cauldron.contenttype.CauldronContentType;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;

import java.util.IdentityHashMap;
import java.util.Map;

/**
 * Ingredient that checks if the cauldron contains the right content type
 */
public class ContentTypeIngredient implements ICauldronIngredient {
  private static final Map<CauldronContentType<?>, ContentTypeIngredient> MAP = new IdentityHashMap<>();
  private final CauldronContentType<?> type;

  private ContentTypeIngredient(CauldronContentType<?> type) {
    this.type = type;
  }

  /**
   * Gets an ingredient for the content type, or makes a new one if missing
   * @param type  Type to fetch
   * @return  Ingredient instance
   */
  public static ContentTypeIngredient of(CauldronContentType<?> type) {
    return MAP.computeIfAbsent(type, ContentTypeIngredient::new);
  }

  @Override
  public boolean test(ICauldronContents contents) {
    return contents.is(type);
  }

  @Override
  public ICauldronIngredientSerializer<?> getSerializer() {
    return CauldronIngredients.TYPE;
  }

  public static class Serializer implements ICauldronIngredientSerializer<ContentTypeIngredient> {
    @Override
    public ContentTypeIngredient read(JsonObject json) {
      ResourceLocation name = new ResourceLocation(JSONUtils.getString(json, "name"));
      CauldronContentType<?> type = CauldronContentTypes.get(name);
      if (type == null) {
        throw new JsonSyntaxException("Invalid cauldron content type '" + name + "'");
      }
      return of(type);
    }

    @Override
    public void write(ContentTypeIngredient ingredient, JsonObject json) {
      json.addProperty("name", CauldronContentTypes.getName(ingredient.type).toString());
    }

    @Override
    public ContentTypeIngredient read(PacketBuffer buffer) {
      ResourceLocation name = buffer.readResourceLocation();
      CauldronContentType<?> type = CauldronContentTypes.get(name);
      if (type == null) {
        throw new DecoderException("Invalid cauldron content type '" + name + "'");
      }
      return of(type);
    }

    @Override
    public void write(ContentTypeIngredient ingredient, PacketBuffer buffer) {
      buffer.writeResourceLocation(CauldronContentTypes.getName(ingredient.type));
    }
  }
}
