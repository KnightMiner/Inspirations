package knightminer.inspirations.library.recipe.cauldron.ingredient;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import io.netty.handler.codec.DecoderException;
import knightminer.inspirations.library.recipe.cauldron.CauldronContentTypes;
import knightminer.inspirations.library.recipe.cauldron.contents.ICauldronFluid;
import knightminer.inspirations.library.recipe.cauldron.contenttype.MapContentType;
import knightminer.inspirations.recipes.InspirationsRecipes;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ITag;
import net.minecraft.tags.TagCollectionManager;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import slimeknights.mantle.util.JsonHelper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

/**
 * Cauldron ingredient type for fluid contents, mostly an extension of {@link ContentMatchIngredient}, but also includes tags
 */
public class FluidCauldronIngredient extends ContentMatchIngredient<ICauldronFluid,Fluid> {
  private final ITag<Fluid> tag;
  private FluidCauldronIngredient(MapContentType<ICauldronFluid,Fluid> type, ITag<Fluid> tag) {
    super(type);
    this.tag = tag;
  }

  /**
   * Creates an ingredient matching a single fluid
   * @param fluid  Fluid to match
   * @return  Ingredient
   */
  public static ContentMatchIngredient<ICauldronFluid, Fluid> of(Fluid fluid) {
    return of(CauldronContentTypes.FLUID, fluid);
  }

  /**
   * Creates an ingredient matching a set of fluids
   * @param fluids  Fluids to match
   * @return  Ingredient
   */
  public static ContentMatchIngredient<ICauldronFluid, Fluid> of(Collection<Fluid> fluids) {
    return of(CauldronContentTypes.FLUID, ImmutableSet.copyOf(fluids));
  }

  /**
   * Creates an ingredient matching a fluid tag
   * @param tag  Fluids to match
   * @return  Ingredient
   */
  public static FluidCauldronIngredient of(ITag<Fluid> tag) {
    return new FluidCauldronIngredient(CauldronContentTypes.FLUID, tag);
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
  public ICauldronIngredientSerializer<?> getSerializer() {
    return InspirationsRecipes.fluidIngredient;
  }

  public static class Serializer implements ICauldronIngredientSerializer<ContentMatchIngredient<ICauldronFluid, Fluid>> {
    /**
     * Gets a fluid by name
     * @param name       Name of the fluid
     * @param exception  Excecption to throw if no fluid
     * @return  Fluid
     */
    private static Fluid getFluid(String name, Function<String, RuntimeException> exception) {
      Fluid fluid = ForgeRegistries.FLUIDS.getValue(new ResourceLocation(name));
      if (fluid != null && fluid != Fluids.EMPTY) {
        return fluid;
      }
      throw exception.apply("Unknown fluid '" + name + "'");
    }

    @Override
    public ContentMatchIngredient<ICauldronFluid, Fluid> read(JsonObject json) {
      // single fluid or array
      if (json.has("name")) {
        // can be string or array
        JsonElement element = JsonHelper.getElement(json, "name");

        // single name
        if (element.isJsonPrimitive()) {
          return of(getFluid(json.getAsString(), JsonSyntaxException::new));
        }

        // array of names
        if (element.isJsonArray()) {
          return of(JsonHelper.parseList(element.getAsJsonArray(), "names", JSONUtils::getString, name -> getFluid(name, JsonSyntaxException::new)));
        }

        // error
        throw new JsonSyntaxException("Invalid 'name', must be a single value or an array");
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

    @Override
    public void write(ContentMatchIngredient<ICauldronFluid, Fluid> ingredient, JsonObject json) {
      ingredient.write(json);
    }

    @Override
    public ContentMatchIngredient<ICauldronFluid, Fluid> read(PacketBuffer buffer) {
      // read the number told
      int size = buffer.readVarInt();
      List<Fluid> fluids = new ArrayList<>(size);
      for (int i = 0; i < size; i++) {
        fluids.add(getFluid(buffer.readString(Short.MAX_VALUE), DecoderException::new));
      }

      // simplify if just one fluid
      if (fluids.size() == 1) {
        return of(fluids.get(0));
      }
      return of(fluids);
    }

    @Override
    public void write(ContentMatchIngredient<ICauldronFluid, Fluid> ingredient, PacketBuffer buffer) {
      ingredient.write(buffer);
    }
  }
}
