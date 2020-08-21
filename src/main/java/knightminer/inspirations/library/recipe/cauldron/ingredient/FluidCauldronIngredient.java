package knightminer.inspirations.library.recipe.cauldron.ingredient;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import knightminer.inspirations.library.recipe.cauldron.CauldronContentTypes;
import knightminer.inspirations.library.recipe.cauldron.CauldronIngredients;
import knightminer.inspirations.library.recipe.cauldron.contents.ICauldronFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ITag;
import net.minecraft.tags.TagCollectionManager;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * Cauldron ingredient type for fluid contents, mostly an extension of {@link ContentMatchIngredient}, but also includes tags
 */
public class FluidCauldronIngredient extends ContentMatchIngredient<ICauldronFluid,Fluid> {
  private final ITag<Fluid> tag;
  private FluidCauldronIngredient(ITag<Fluid> tag) {
    super(CauldronIngredients.FLUID, CauldronContentTypes.FLUID);
    this.tag = tag;
  }

  /**
   * Creates an ingredient matching a single fluid
   * @param fluid  Fluid to match
   * @return  Ingredient
   */
  public static ContentMatchIngredient<ICauldronFluid, Fluid> of(Fluid fluid) {
    return of(CauldronIngredients.FLUID, fluid);
  }

  /**
   * Creates an ingredient matching a set of fluids
   * @param fluids  Fluids to match
   * @return  Ingredient
   */
  public static ContentMatchIngredient<ICauldronFluid, Fluid> of(Collection<Fluid> fluids) {
    return of(CauldronIngredients.FLUID, ImmutableSet.copyOf(fluids));
  }

  /**
   * Creates an ingredient matching a fluid tag
   * @param tag  Fluids to match
   * @return  Ingredient
   */
  public static FluidCauldronIngredient of(ITag<Fluid> tag) {
    return new FluidCauldronIngredient(tag);
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

  public static class Serializer extends ContentMatchIngredient.Serializer<ICauldronFluid,Fluid> {
    /**
     * Creates a new serializer instance
     */
    public Serializer() {
      super(CauldronContentTypes.FLUID);
    }

    @Override
    public ContentMatchIngredient<ICauldronFluid, Fluid> read(JsonObject json) {
      // single fluid or array
      if (json.has("name")) {
        return super.read(json);
      }

      // tag
      if (json.has("tag")) {
        ResourceLocation tagName = new ResourceLocation(JSONUtils.getString(json, "tag"));
        ITag<Fluid> tag = FluidTags.getCollection().get(tagName);
        if (tag == null) {
          throw new JsonSyntaxException("Unknown fluid tag '" + tagName + "'");
        }
        return of(tag);
      }

      throw new JsonSyntaxException("Invalid cauldron fluid ingredient, must have 'name' or 'tag");
    }
  }
}
