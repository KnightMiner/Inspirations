package knightminer.inspirations.library.recipe.cauldron;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import io.netty.handler.codec.DecoderException;
import knightminer.inspirations.Inspirations;
import knightminer.inspirations.library.recipe.cauldron.contents.CauldronContentType;
import knightminer.inspirations.library.recipe.cauldron.contents.ICauldronContents;
import knightminer.inspirations.recipes.recipe.cauldron.contents.ColorContentType;
import knightminer.inspirations.recipes.recipe.cauldron.contents.CustomContentType;
import knightminer.inspirations.recipes.recipe.cauldron.contents.DyeContentType;
import knightminer.inspirations.recipes.recipe.cauldron.contents.FluidContentType;
import knightminer.inspirations.recipes.recipe.cauldron.contents.PotionContentType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.util.Lazy;

import javax.annotation.Nullable;
import java.util.function.BiFunction;

/**
 * Registry that helps with registering, serializing, and deserializing cauldron properties
 * @deprecated Replaced by each cauldron being its own block
 */
@Deprecated
public class CauldronContentTypes {
  private static final ResourceLocation UNREGISTERED = Inspirations.getResource("null");
  public static final String KEY_TYPE = "type";
  private static final BiMap<ResourceLocation,CauldronContentType<?>> TYPES = HashBiMap.create();

  /* Public constants */

  /** Contains a specific fluid */
  public static final CauldronContentType<Fluid> FLUID = register("fluid", new FluidContentType());

  /** Contains an arbitrary color */
  public static final CauldronContentType<Integer> COLOR = register("color", new ColorContentType());

  /** Contains a specific color */
  public static final CauldronContentType<DyeColor> DYE = register("dye", new DyeContentType());

  /** Contains a specific potion */
  public static final CauldronContentType<Potion> POTION = register("potion", new PotionContentType(Inspirations.getResource("potion"), false));
  /** Potion that is currently in progress brewing */
  public static final CauldronContentType<Potion> UNFERMENTED_POTION = register("unfermented_potion", new PotionContentType(Inspirations.getResource("unfermented_potion"), true));

  /** Contains a specific fluid */
  public static final CauldronContentType<ResourceLocation> CUSTOM = register("custom", new CustomContentType());

  /** Default cauldron content type, return when reading if type invalid */
  public static final Lazy<ICauldronContents> DEFAULT = Lazy.of(() -> FLUID.of(Fluids.WATER));

  /**
   * Registers a new content type
   * @param name  Name
   * @param type  Type to register
   */
  public static void register(ResourceLocation name, CauldronContentType<?> type) {
    if (UNREGISTERED.equals(name) || TYPES.containsKey(name)) {
      throw new IllegalArgumentException("Duplicate cauldron content type '" + name + "'");
    }
    TYPES.put(name, type);
  }

  /**
   * Gets the content type at the given name
   * @param name  Content type name
   * @return  Content type
   */
  @Nullable
  public static CauldronContentType<?> get(ResourceLocation name) {
    return TYPES.get(name);
  }

  /**
   * Helper to register for Inspirations
   * @param name  Name to register
   * @param type  Type to register
   * @param <T>   Type class
   * @return  Registered type
   */
  private static <T extends CauldronContentType<?>> T register(String name, T type) {
    register(Inspirations.getResource(name), type);
    return type;
  }

  /**
   * Gets the name for a content type
   * @param type  Type to get
   * @return  Type registry name
   */
  public static ResourceLocation getName(CauldronContentType<?> type) {
    ResourceLocation name = TYPES.inverse().get(type);
    if (name == null) {
      return UNREGISTERED;
    }
    return name;
  }

  /**
   * Simple helper function to make the generics work out
   * @param type    Content type
   * @param data    Data being read
   * @param parser  Parses data into contents
   */
  private static <T, D> ICauldronContents read(CauldronContentType<T> type, D data, BiFunction<CauldronContentType<T>,D,T> parser) {
    T value = parser.apply(type, data);
    if (value == null) {
      return DEFAULT.get();
    }
    return type.of(value);
  }

  /**
   * Reads the cauldron contents from JSON
   * @param json  JSON to read
   * @return  Cauldron contents
   * @throws JsonSyntaxException  If the type is missing or the data invalid
   */
  public static ICauldronContents read(JsonObject json) {
    ResourceLocation location = new ResourceLocation(GsonHelper.getAsString(json, KEY_TYPE));
    CauldronContentType<?> type = get(location);
    if (type != null) {
      return read(type, json, CauldronContentType::read);
    }
    throw new JsonSyntaxException("Invalid cauldron content type '" + location + "'");
  }

  /**
   * Reads the given contents from NBT
   * @param nbt  NBT contents
   * @return  Cauldron contents
   */
  public static ICauldronContents read(CompoundTag nbt) {
    if (nbt.contains(KEY_TYPE, Tag.TAG_STRING)) {
      ResourceLocation location = new ResourceLocation(nbt.getString(KEY_TYPE));
      CauldronContentType<?> type = get(location);
      if (type != null) {
        return read(type, nbt, CauldronContentType::read);
      }
    }
    return CauldronContentTypes.DEFAULT.get();
  }

  /**
   * Reads the given contents from NBT
   * @param buffer Buffer instance
   * @return  Cauldron contents
   * @throws  DecoderException  if the type is missing or the data invalids
   */
  public static ICauldronContents read(FriendlyByteBuf buffer) {
    ResourceLocation name = buffer.readResourceLocation();
    CauldronContentType<?> type = get(name);
    if (type == null) {
      throw new DecoderException("Invalid type name '" + name + "'");
    }
    return read(type, buffer, CauldronContentType::read);
  }
}
