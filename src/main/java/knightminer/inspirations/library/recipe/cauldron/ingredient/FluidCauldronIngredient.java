package knightminer.inspirations.library.recipe.cauldron.ingredient;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import knightminer.inspirations.library.recipe.cauldron.CauldronContentTypes;
import knightminer.inspirations.library.recipe.cauldron.CauldronIngredients;
import knightminer.inspirations.library.recipe.cauldron.contents.ICauldronContents;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;
import slimeknights.mantle.util.JsonHelper;
import slimeknights.mantle.util.RegistryHelper;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Cauldron ingredient type for fluid contents, mostly an extension of {@link ContentMatchIngredient}, but also includes tags
 * @deprecated Block predicates are sufficient
 */
@Deprecated
public class FluidCauldronIngredient extends ContentMatchIngredient<Fluid> {
  private final TagKey<Fluid> tag;
  private List<ICauldronContents> displayValues;
  private FluidCauldronIngredient(TagKey<Fluid> tag) {
    super(CauldronIngredients.FLUID);
    this.tag = tag;
  }

  @Override
  protected boolean testValue(Fluid value) {
    return value.is(tag);
  }

  @Override
  protected void write(JsonObject json) {
    json.addProperty("tag", tag.location().toString());
  }

  @Override
  protected void write(FriendlyByteBuf buffer) {
    List<Fluid> fluids = RegistryHelper.getTagValueStream(Registry.FLUID, tag).toList();
    buffer.writeVarInt(fluids.size());
    for (Fluid fluid : fluids) {
      buffer.writeResourceLocation(Objects.requireNonNull(fluid.getRegistryName()));
    }
  }

  @Override
  public List<ICauldronContents> getMatchingContents() {
    if (displayValues == null) {
      displayValues = RegistryHelper.getTagValueStream(Registry.FLUID, tag)
                                    .map(CauldronContentTypes.FLUID::of)
                                    .collect(Collectors.toList());
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
    public ContentMatchIngredient<Fluid> of(TagKey<Fluid> tag) {
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
        ResourceLocation tagName = JsonHelper.getResourceLocation(json, "tag");
        TagKey<Fluid> tag = TagKey.create(Registry.FLUID_REGISTRY, tagName);
        return of(tag);
      }

      throw new JsonSyntaxException("Invalid cauldron fluid ingredient, must have 'name' or 'tag");
    }
  }
}
