package knightminer.inspirations.library.recipe.cauldron.ingredient;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import knightminer.inspirations.library.recipe.cauldron.CauldronContentTypes;
import knightminer.inspirations.library.recipe.cauldron.CauldronIngredients;
import knightminer.inspirations.library.recipe.cauldron.contents.ICauldronContents;
import net.minecraft.fluid.Fluid;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tags.ITag;
import net.minecraft.tags.TagCollectionManager;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Cauldron ingredient type for fluid contents, mostly an extension of {@link ContentMatchIngredient}, but also includes tags
 */
public class FluidCauldronIngredient extends ContentMatchIngredient<Fluid> {
  private final ITag<Fluid> tag;
  private List<ICauldronContents> displayValues;
  private FluidCauldronIngredient(ITag<Fluid> tag) {
    super(CauldronIngredients.FLUID);
    this.tag = tag;
  }

  @Override
  protected boolean testValue(Fluid value) {
    return tag.contains(value);
  }

  @Override
  protected void write(JsonObject json) {
    json.addProperty("tag", TagCollectionManager.func_232928_e_().func_232926_c_().func_232975_b_(this.tag).toString());
  }

  @Override
  protected void write(PacketBuffer buffer) {
    List<Fluid> elements = tag.getAllElements();
    buffer.writeVarInt(elements.size());
    for (Fluid fluid : elements) {
      buffer.writeResourceLocation(Objects.requireNonNull(fluid.getRegistryName()));
    }
  }

  @Override
  public List<ICauldronContents> getMatchingContents() {
    if (displayValues == null) {
      displayValues = tag.getAllElements().stream().map(CauldronContentTypes.FLUID::of).collect(Collectors.toList());
    }
    return displayValues;
  }

  /** Specific fluid serializer class */
  public static class Serializer extends ContentMatchIngredient.Serializer<Fluid> {
    /**
     * Creates a new serializer instance
     */
    public Serializer() {
      super(CauldronContentTypes.FLUID);
    }

    /**
     * Creates a new ingredient from the given tag
     * @param tag  Tag instance
     * @return  Ingredient instance
     */
    public ContentMatchIngredient<Fluid> of(ITag<Fluid> tag) {
      return new FluidCauldronIngredient(tag);
    }

    @Override
    public ContentMatchIngredient<Fluid> read(JsonObject json) {
      // single fluid or array
      if (json.has("name")) {
        return super.read(json);
      }

      // tag
      if (json.has("tag")) {
        ResourceLocation tagName = new ResourceLocation(JSONUtils.getString(json, "tag"));
        ITag<Fluid> tag = TagCollectionManager.func_232928_e_().func_232926_c_().get(tagName);
        if (tag == null) {
          throw new JsonSyntaxException("Unknown fluid tag '" + tagName + "'");
        }
        return of(tag);
      }

      throw new JsonSyntaxException("Invalid cauldron fluid ingredient, must have 'name' or 'tag");
    }
  }
}
