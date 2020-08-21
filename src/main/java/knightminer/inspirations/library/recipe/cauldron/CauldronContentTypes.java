package knightminer.inspirations.library.recipe.cauldron;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import io.netty.handler.codec.DecoderException;
import knightminer.inspirations.Inspirations;
import knightminer.inspirations.library.recipe.cauldron.contents.EmptyCauldronContents;
import knightminer.inspirations.library.recipe.cauldron.contents.ICauldronColor;
import knightminer.inspirations.library.recipe.cauldron.contents.ICauldronContents;
import knightminer.inspirations.library.recipe.cauldron.contents.ICauldronDye;
import knightminer.inspirations.library.recipe.cauldron.contents.ICauldronFluid;
import knightminer.inspirations.library.recipe.cauldron.contents.ICauldronPotion;
import knightminer.inspirations.library.recipe.cauldron.contenttype.CauldronContentType;
import knightminer.inspirations.library.recipe.cauldron.contenttype.MapContentType;
import knightminer.inspirations.library.recipe.cauldron.contenttype.NamedContentType;
import knightminer.inspirations.library.recipe.cauldron.contenttype.RegistryContentType;
import knightminer.inspirations.library.recipe.cauldron.contenttype.SingletonContentType;
import knightminer.inspirations.recipes.recipe.cauldron.contents.CauldronDye;
import knightminer.inspirations.recipes.recipe.cauldron.contents.CauldronFluid;
import knightminer.inspirations.recipes.recipe.cauldron.contents.CauldronPotion;
import knightminer.inspirations.recipes.recipe.cauldron.contents.CauldronWater;
import knightminer.inspirations.recipes.recipe.cauldron.contents.ColorContentType;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.DyeColor;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.potion.Potion;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.util.BiConsumer;

import javax.annotation.Nullable;

/**
 * Registry that helps with registering, serializing, and deserializing cauldron properties
 */
public class CauldronContentTypes {
  private static final ResourceLocation UNREGISTERED = Inspirations.getResource("null");
  private static final String KEY_TYPE = "type";
  private static final BiMap<ResourceLocation,CauldronContentType<?>> TYPES = HashBiMap.create();

  /* Public constants */

  /** Generic water type */
  public static final SingletonContentType<EmptyCauldronContents> EMPTY = register("empty", new SingletonContentType<>(EmptyCauldronContents.class, EmptyCauldronContents.INSTANCE));

  /** Generic water type */
  public static final SingletonContentType<CauldronWater> WATER = register("water", new SingletonContentType<>(CauldronWater.class, new CauldronWater()));

  /** Contains an arbitrary color */
  public static final MapContentType<ICauldronColor, Integer> COLOR = register("color", new ColorContentType());

  /** Contains a specific color */
  @SuppressWarnings("ConstantConditions")
  public static final MapContentType<ICauldronDye, DyeColor> DYE = register("dye", new NamedContentType<>(ICauldronDye.class, CauldronDye::new, name -> DyeColor.byTranslationKey(name, null), ICauldronDye::getDye));

  /** Contains a specific color */
  public static final MapContentType<ICauldronPotion, Potion> POTION = register("potion", new RegistryContentType<>(ICauldronPotion.class, CauldronPotion::new, ForgeRegistries.POTION_TYPES, ICauldronPotion::getPotion));

  /** Contains a specific fluid */
  public static final MapContentType<ICauldronFluid, Fluid> FLUID = register("fluid", new RegistryContentType<>(ICauldronFluid.class, CauldronFluid::new, ForgeRegistries.FLUIDS, ICauldronFluid::getFluid));

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
   * Handles the generics for an unknown type generics
   * @param type      Type to check
   * @param contents  Contents
   * @param consumer  Logic to run
   * @param <T>  Type class
   */
  private static <T extends ICauldronContents> void write(CauldronContentType<T> type, ICauldronContents contents, BiConsumer<CauldronContentType<T>, T> consumer) {
    type.get(contents).ifPresent(cont -> consumer.accept(type, cont));
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
   * Converts the given contents to JSON
   * @param contents  Contents
   * @return  JSON
   */
  public static JsonObject toJson(ICauldronContents contents) {
    JsonObject json = new JsonObject();
    CauldronContentType<?> type = contents.getType();
    json.addProperty(KEY_TYPE, getName(type).toString());
    write(type, contents, (t,c) -> t.write(c, json));
    return json;
  }

  /**
   * Reads the cauldron contents from JSON
   * @param json  JSON to read
   * @return  Cauldron contents
   */
  public static ICauldronContents read(JsonObject json) {
    ResourceLocation location = new ResourceLocation(JSONUtils.getString(json, KEY_TYPE));
    CauldronContentType<?> type = get(location);
    if (type != null) {
      return type.read(json);
    }
    throw new JsonSyntaxException("Invalid cauldron content type '" + location + "'");
  }

  /**
   * Writes the given contents to NBT
   * @param contents  Contents to write
   * @return  Contents written to NBT
   */
  public static CompoundNBT toNbt(ICauldronContents contents) {
    CompoundNBT nbt = new CompoundNBT();
    CauldronContentType<?> type = contents.getType();
    nbt.putString(KEY_TYPE, getName(type).toString());
    write(type, contents, (t,c) -> t.write(c, nbt));
    return nbt;
  }

  /**
   * Reads the given contents from NBT
   * @param nbt  NBT contents
   * @return  Cauldron contents
   */
  public static ICauldronContents read(CompoundNBT nbt) {
    if (nbt.contains(KEY_TYPE, NBT.TAG_STRING)) {
      ResourceLocation location = new ResourceLocation(nbt.getString());
      CauldronContentType<?> type = get(location);
      if (type != null) {
        ICauldronContents contents = type.read(nbt);
        if (contents != null) {
          return contents;
        }
      }
    }
    return CauldronContentTypes.WATER.get();
  }

  /**
   * Writes the given contents to NBT
   * @param contents  Contents to write
   * @param buffer    Buffer instance
   */
  public static void write(ICauldronContents contents, PacketBuffer buffer) {
    CauldronContentType<?> type = contents.getType();
    buffer.writeResourceLocation(getName(type));
    write(type, contents, (t,c) -> t.write(c, buffer));
  }

  /**
   * Reads the given contents from NBT
   * @param buffer Buffer instance
   * @return  Cauldron contents
   */
  public static ICauldronContents read(PacketBuffer buffer) {
    ResourceLocation name = buffer.readResourceLocation();
    CauldronContentType<?> type = get(name);
    if (type == null) {
      throw new DecoderException("Invalid type name '" + name + "'");
    }
    return type.read(buffer);
  }
}
