package knightminer.inspirations.library.client;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import knightminer.inspirations.Inspirations;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.resource.IResourceType;
import net.minecraftforge.resource.VanillaResourceType;
import slimeknights.mantle.client.IEarlySelectiveReloadListener;

import javax.annotation.Nullable;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Class that will load a list of textures from a JSON file
 */
public class CustomTextureLoader implements IEarlySelectiveReloadListener {
  /** Map of resource name to texture name */
  private final Map<ResourceLocation, ResourceLocation> textures = new HashMap<>();
  /** JSON file containing the textures list */
  private final ResourceLocation file;

  /**
   * Creates a new texture loader instance
   * @param file  File to load textures from
   */
  public CustomTextureLoader(ResourceLocation file) {
    this.file = new ResourceLocation(file.getNamespace(), "models/" + file.getPath() + ".json");
    FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onTextureStitch);
  }

  @Override
  public void onResourceManagerReload(IResourceManager manager, Predicate<IResourceType> type) {
    // model type as the TESR is linked to the blockstate models
    if (type.test(VanillaResourceType.MODELS)) {
      // first, get a list of all json files
      List<JsonObject> jsonFiles;
      try {
        // get all files
        jsonFiles = manager.getAllResources(file).stream()
                           .map(CustomTextureLoader::getJson)
                           .filter(Objects::nonNull)
                           .collect(Collectors.toList());
      } catch(IOException e) {
        jsonFiles = Collections.emptyList();
        Inspirations.log.error("Failed to load model settings file", e);
      }

      // first object is bottom most pack, so upper resource packs will replace it
      for (JsonObject json : jsonFiles) {
        // right now just do simply key value pairs
        for (Entry<String, JsonElement> entry : json.entrySet()) {
          // get a valid name
          String key = entry.getKey();
          ResourceLocation name = ResourceLocation.tryCreate(key);
          if (name == null) {
            Inspirations.log.error("Skipping invalid key " + key + " as it is not a valid resource location");
            continue;
          } else if (!ModList.get().isLoaded(name.getNamespace())) {
            Inspirations.log.debug("Skipping loading texture " + key + " as the dependent mod is not loaded");
            continue;
          }

          // get a valid element
          JsonElement element = entry.getValue();
          if (!element.isJsonPrimitive()) {
            Inspirations.log.error("Skipping key " + key + " as the value is not a string");
            continue;
          }
          ResourceLocation texture = ResourceLocation.tryCreate(element.getAsString());
          if (texture == null) {
            Inspirations.log.error("Skipping key " + key + " as the texture " + element.getAsString() + " is an invalid texture path");
          } else {
            textures.put(name, texture);
          }
        }
      }
    }
  }

  /**
   * Called during the texture stitch event to add all relevant textures to the map
   * @param event  Texture stitch event
   */
  private void onTextureStitch(TextureStitchEvent.Pre event) {
    if (PlayerContainer.LOCATION_BLOCKS_TEXTURE.equals(event.getMap().getTextureLocation())) {
      textures.values().forEach(event::addSprite);
    }
  }


  /* Helpers */

  /**
   * Converts the resource into a JSON file
   * @param resource  Resource to read. Closed when done
   * @return  JSON object, or null if failed to parse
   */
  @Nullable
  private static JsonObject getJson(IResource resource) {
    // this code is heavily based on ResourcePack::getResourceMetadata
    try {
      Throwable thrown = null;
      BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8));
      try {
        return JSONUtils.fromJson(reader);
      } catch (Throwable e) {
        // store this exception in case we throw again
        thrown = e;
        throw e;
      } finally {
        try {
          reader.close();
        } catch (Throwable e) {
          // if we already threw, suppress this exception
          if (thrown != null) {
            thrown.addSuppressed(e);
          } else {
            throw e;
          }
        }
      }
    } catch (JsonParseException | IOException e) {
      Inspirations.log.error("Failed to load texture JSON " + resource.getLocation(), e);
      return null;
    }
  }


  /* Public methods */

  /**
   * Gets a texture from the pack
   * @param location  Texture name
   * @return  Texture
   */
  public ResourceLocation getTexture(ResourceLocation location) {
    // fallback to the passed texture, used for fluid textures which return directly instead of going though this listener
    return textures.getOrDefault(location, location);
  }
}
