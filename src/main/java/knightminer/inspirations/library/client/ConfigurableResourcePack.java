package knightminer.inspirations.library.client;

import knightminer.inspirations.Inspirations;
import net.minecraft.SharedConstants;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.AbstractPackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.repository.RepositorySource;
import net.minecraft.server.packs.resources.IoSupplier;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import slimeknights.mantle.data.loadable.Loadables;

import javax.annotation.Nullable;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

/**
 * Resource pack that overrides resources based on config
 */
public class ConfigurableResourcePack extends AbstractPackResources implements RepositorySource {
  /** Class within the mod jar to serve as a root for getting resources */
  private final Class<?> resourceLoader;
  /** Namespaced pack name, used to pass to resource pack loaders and for the translation key */
  private final String packId;
  /** Display name of the pack */
  private final Component displayName;
  /** Pack description */
  private final Component description;
  /** Prefix for where to find pack resources */
  private final String pathPrefix;
  /** Set of namespaces relevant to this pack */
  private final Set<String> namespaces;

  /** Map of replaced resource name to condition for replacing */
  private final Map<String,Replacement> replacements = new HashMap<>();

  /**
   * Creates a new pack instance
   * @param resourceLoader  Class context for resource loading
   * @param packId          ID for the pack
   * @param displayName     Display name of the pack for UIs
   * @param namespaces      List of namespaces that have resources replaced
   */
  public ConfigurableResourcePack(Class<?> resourceLoader, ResourceLocation packId, Component displayName, Component description, Set<String> namespaces) {
    this(resourceLoader, packId.toString(), String.format(Locale.ROOT, "/%s/%s/%s/", PackType.CLIENT_RESOURCES.getDirectory(), packId.getNamespace(), packId.getPath()), displayName, description, namespaces);
  }

  /**
   * Internal method for constructing
   * @param resourceLoader  Class context for resource loading
   * @param packId          Pack ID resource location
   * @param pathPrefix      Path resource prefix
   * @param namespaces      List of namepsaces that have resources replaced
   */
  private ConfigurableResourcePack(Class<?> resourceLoader, String packId, String pathPrefix, Component displayName, Component description, Set<String> namespaces) {
    super(packId, true);
    this.resourceLoader = resourceLoader;
    this.packId = packId;
    this.displayName = displayName;
    this.description = description;
    this.pathPrefix = pathPrefix;
    this.namespaces = namespaces;
  }

  @Nullable
  @Override
  public IoSupplier<InputStream> getRootResource(String... elements) {
    return this.getResource(String.join("/", elements));
  }

  @Nullable
  @Override
  public IoSupplier<InputStream> getResource(PackType packType, ResourceLocation location) {
    return this.getResource(String.format(Locale.ROOT, "%s/%s/%s", packType.getDirectory(), location.getNamespace(), location.getPath()));
  }

  @Override
  public Set<String> getNamespaces(PackType type) {
    return type == PackType.CLIENT_RESOURCES ? namespaces : Collections.emptySet();
  }

  /**
   * Gets the resource in the pack for the given name
   * @param name  Default resource path
   * @return  Resource from the path, or null if missing
   */
  private InputStream getPackResource(String name) throws IOException {
    InputStream stream = resourceLoader.getResourceAsStream(pathPrefix + name);
    if (stream != null) {
      return stream;
    }
    throw new FileNotFoundException("Failed to open resource at " + pathPrefix + name);
  }

  /** Common code for getting an IO supplier */
  private IoSupplier<InputStream> getIoSupplier(String name) {
    return () -> getPackResource(name);
  }

  /** Gets a resource supplier for the given name */
  @Nullable
  private IoSupplier<InputStream> getResource(String name) {
    // pack.mcmeta and pack.png are requested without prefix, and required directly
    if (name.equals("pack.mcmeta") || name.equals("pack.png")) {
      return getIoSupplier(name);
    }
    // if it's a replacement, treat as such
    Replacement replacement = replacements.get(name);
    if (replacement != null && replacement.isEnabled()) {
      return getIoSupplier(replacement.name());
    } else if (replacement != null && !replacement.isEnabled()) {
      Inspirations.log.warn("Replacement {} for {} not enabled", replacement.name, name);
    }
    // not a replacement or replacement is disabled, return nothing
    return null;
  }

  @Override
  public void listResources(PackType packType, String namespace, String folder, ResourceOutput output) {
    String root = packType.getDirectory() + '/' + namespace + '/';
    String prefix = root + folder + '/';
    for (Entry<String,Replacement> entry : replacements.entrySet()) {
      Replacement replacement = entry.getValue();
      if (replacement.isEnabled()) {
        String path = entry.getKey();
        if (path.startsWith(prefix)) {
          ResourceLocation location = ResourceLocation.tryBuild(namespace, path.substring(root.length()));
          if (location != null) {
            output.accept(location, getIoSupplier(replacement.name));
          }
        }
      }
    }
  }

  @Override
  public void close() {}

  @Override
  public void loadPacks(Consumer<Pack> consumer) {
    // add a new always enabled pack. Config is how you disable the replacements
    consumer.accept(Pack.create(packId, displayName, true, id -> this, new Pack.Info(
      description,
      SharedConstants.getCurrentVersion().getPackVersion(PackType.SERVER_DATA),
      SharedConstants.getCurrentVersion().getPackVersion(PackType.CLIENT_RESOURCES),
      FeatureFlags.REGISTRY.allFlags(),
      false
    ), PackType.CLIENT_RESOURCES, Pack.Position.TOP, false, PackSource.BUILT_IN));
  }


  /* Replacement additions */

  /**
   * Generic method to add a replacement
   * @param condition     Condition for replacement
   * @param originalPath  Original resource path
   * @param resource      Path to the replacement resource relative to the pack root
   */
  public void addReplacement(BooleanSupplier condition, String originalPath, String resource) {
    Replacement original = this.replacements.putIfAbsent(originalPath, new Replacement(condition, resource));
    if (original != null) {
      Inspirations.log.warn("Duplicate replacement '{}' for configurable pack {}.", originalPath, packId);
    }
  }

  /**
   * Makes a path for the given resource
   * @param id         Resource ID
   * @param folder     Resource folder
   * @return  Full resource path
   */
  private static String makePath(ResourceLocation id, String folder, String extension) {
    return String.format("%s/%s/%s/%s.%s", PackType.CLIENT_RESOURCES.getDirectory(), id.getNamespace(), folder, id.getPath(), extension);
  }

  /**
   * Adds a replacement for a blockstate JSON
   * @param condition  Condition for replacement
   * @param block      Block to replace the model
   * @param resource   Name of blockstate replacement
   */
  public void addBlockstateReplacement(BooleanSupplier condition, Block block, String resource) {
    addReplacement(condition, makePath(Loadables.BLOCK.getKey(block), "blockstates", "json"), "blockstates/" + resource + ".json");
  }

  /**
   * Adds a replacement for a blockstate JSON
   * @param condition  Condition for replacement
   * @param block      Block to replace the model
   * @param resource   Name of blockstate replacement
   */
  public void addBlockstateReplacement(BooleanValue condition, Block block, String resource) {
    addBlockstateReplacement(condition::get, block, resource);
  }

  /**
   * Adds a replacement for a item model replacement
   * @param condition  Condition for replacement
   * @param item       Item to replace the model
   * @param resource   New name supplier
   */
  public void addItemModelReplacement(BooleanSupplier condition, ItemLike item, String resource) {
    addReplacement(condition, makePath(Loadables.ITEM.getKey(item.asItem()), "models/item", "json"), "item_models/" + resource + ".json");
  }

  /**
   * Adds a replacement for a item model replacement
   * @param condition  Condition for replacement
   * @param item       Item to replace the model
   * @param resource   New name supplier
   */
  public void addItemModelReplacement(BooleanValue condition, ItemLike item, String resource) {
    addItemModelReplacement(condition::get, item, resource);
  }

  /**
   * Data class holding a single replacement pair
   * @param condition Condition for the replacement
   * @param name      New file name, relative to pack root
   */
  private record Replacement(BooleanSupplier condition, String name) {
    /** {@return true if enabled} */
    public boolean isEnabled() {
      return condition.getAsBoolean();
    }
  }
}
