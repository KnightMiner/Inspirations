package knightminer.inspirations.library.client;

import knightminer.inspirations.Inspirations;
import net.minecraft.block.Block;
import net.minecraft.resources.IPackFinder;
import net.minecraft.resources.ResourcePack;
import net.minecraft.resources.ResourcePackFileNotFoundException;
import net.minecraft.resources.ResourcePackInfo;
import net.minecraft.resources.ResourcePackInfo.IFactory;
import net.minecraft.resources.ResourcePackType;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Resource pack that overrides resources based on config
 */
public class ConfigurableResourcePack extends ResourcePack implements IPackFinder {
  /** Class within the mod jar to serve as a root for getting resources */
  private final Class<?> resourceLoader;
  /** Namespaced pack name, used to pass to resource pack loaders and for the translation key */
  private final String packId;
  /** Display name of the pack */
  private final String displayName;
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
  public ConfigurableResourcePack(Class<?> resourceLoader, ResourceLocation packId, String displayName, Set<String> namespaces) {
    this(resourceLoader, packId.toString(), String.format("/%s/%s/%s/", ResourcePackType.CLIENT_RESOURCES.getDirectoryName(), packId.getNamespace(), packId.getPath()), displayName, namespaces);
  }

  /**
   * Internal method for constructing
   * @param resourceLoader  Class context for resource loading
   * @param packId          Pack ID resource location
   * @param pathPrefix      Path resource prefix
   * @param namespaces      List of namepsaces that have resources replaced
   */
  private ConfigurableResourcePack(Class<?> resourceLoader, String packId, String pathPrefix, String displayName, Set<String> namespaces) {
    super(new File(pathPrefix));
    this.resourceLoader = resourceLoader;
    this.packId = packId;
    this.displayName = displayName;
    this.pathPrefix = pathPrefix;
    this.namespaces = namespaces;
  }

  @Override
  public String getName() {
    return displayName;
  }

  @Override
  public Set<String> getResourceNamespaces(ResourcePackType type) {
    return type == ResourcePackType.CLIENT_RESOURCES ? namespaces : Collections.emptySet();
  }

  /**
   * Gets the resource in the pack for the given name
   * @param name  Default resource path
   * @return  Resource from the path, or null if missing
   */
  private InputStream getPackResource(String name) {
    return resourceLoader.getResourceAsStream(pathPrefix + name);
  }

  @Override
  protected InputStream getInputStream(String name) throws IOException {
    // pack.mcmeta and pack.png are requested without prefix, and requird directly
    if (name.equals("pack.mcmeta") || name.equals("pack.png")) {
      return getPackResource(name);
    }

    // if its a replacement, treat as such
    Replacement replacement = replacements.get(name);
    if (replacement != null && replacement.isEnabled()) {
      return getPackResource(replacement.getName());
    }

    // not a replacement or replacement is disabled, error
    throw new ResourcePackFileNotFoundException(this.file, name);
  }

  @Override
  protected boolean resourceExists(String name) {
    Replacement replacement = replacements.get(name);
    return replacement != null && replacement.isEnabled();
  }

  @Override
  public Collection<ResourceLocation> getAllResourceLocations(ResourcePackType type, String domain, String path, int maxDepth, Predicate<String> filter) {
    // this method appears to only be called for fonts and GUIs, so just return an empty list as neither is used here
    return Collections.emptyList();
  }

  @Override
  public void close() {}

  @Override
  public void findPacks(Consumer<ResourcePackInfo> consumer, IFactory factory) {
    // add a new always enabled pack. Config is how you disable the replacements
    consumer.accept(ResourcePackInfo.createResourcePack(
        packId, true, () -> this, factory, ResourcePackInfo.Priority.TOP,
        name -> new TranslationTextComponent("pack.nameAndSource", name, Inspirations.modID)));
  }

  /* Replacement additions */

  /**
   * Generic method to add a replacement
   * @param condition     Condition for replacement
   * @param originalPath  Original resource path
   * @param resource      Path to the replacement resource relative to the pack root
   */
  public void addReplacement(BooleanSupplier condition, String originalPath, String resource) {
    if (replacements.containsKey(originalPath)) {
      throw new IllegalArgumentException("Duplicate replacement '" + originalPath + "' for configurable pack " + packId);
    }
    this.replacements.put(originalPath, new Replacement(condition, resource));
  }

  /**
   * Makes a path for the given resource
   * @param id         Resource ID
   * @param folder     Resource folder
   * @return  Full resource path
   */
  private static String makePath(ResourceLocation id, String folder, String extension) {
    return String.format("%s/%s/%s/%s.%s", ResourcePackType.CLIENT_RESOURCES.getDirectoryName(), id.getNamespace(), folder, id.getPath(), extension);
  }

  /**
   * Adds a replacement for a blockstate JSON
   * @param condition  Condition for replacement
   * @param block      Block to replace the model
   * @param resource   Name of blockstate replacement
   */
  public void addBlockstateReplacement(BooleanSupplier condition, Block block, String resource) {
    addReplacement(condition, makePath(Objects.requireNonNull(block.getRegistryName()), "blockstates", "json"), "blockstates/" + resource + ".json");
  }

  /**
   * Adds a replacement for a item model replacement
   * @param condition  Condition for replacement
   * @param item       Item to replace the model
   * @param resource   New name supplier
   */
  public void addItemModelReplacement(BooleanSupplier condition, IItemProvider item, String resource) {
    addReplacement(condition, makePath(Objects.requireNonNull(item.asItem().getRegistryName()), "models/item", "json"), "item_models/" + resource + ".json");
  }

  /**
   * Data class holding a single replacement pair
   */
  private static class Replacement {
    private final BooleanSupplier condition;
    private final String name;

    /**
     * Creates a new replacement
     * @param condition  Condition for the replacement
     * @param name       New file name, relative to pack root
     */
    public Replacement(BooleanSupplier condition, String name) {
      this.name = name;
      this.condition = condition;
    }

    public String getName() {
      return name;
    }

    /**
     * If true, this replacement is enabled
     * @return  True if enabled
     */
    public boolean isEnabled() {
      return condition.getAsBoolean();
    }
  }
}
