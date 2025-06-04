package knightminer.inspirations.cauldrons.data;

import com.google.gson.JsonObject;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.PackOutput;
import net.minecraft.data.PackOutput.Target;
import net.minecraft.world.item.BucketItem;
import slimeknights.mantle.data.GenericDataProvider;
import slimeknights.mantle.data.loadable.Loadables;

import java.util.concurrent.CompletableFuture;

/** Quick and dirty data provider to generate fluid bucket models */
public class FluidBucketModelProvider extends GenericDataProvider {
  private final String modId;
  public FluidBucketModelProvider(PackOutput packOutput, String modId) {
    super(packOutput, Target.RESOURCE_PACK, "models/item");
    this.modId = modId;
  }

  /** Makes the JSON for a given bucket */
  private static JsonObject makeJson(BucketItem bucket) {
    JsonObject json = new JsonObject();
    json.addProperty("parent", "forge:item/bucket_drip");
    // using our own model as the forge one expects us to use item colors to handle tints, when we could just bake it in
    json.addProperty("loader", "forge:fluid_container");
    json.addProperty("flip_gas", bucket.getFluid().getFluidType().isLighterThanAir());
    json.addProperty("fluid", Loadables.FLUID.getString(bucket.getFluid()));
    return json;
  }

  @Override
  public CompletableFuture<?> run(CachedOutput cache) {
    return allOf(BuiltInRegistries.ITEM.entrySet().stream()
      .filter(entry -> entry.getKey().location().getNamespace().equals(modId) && entry.getValue() instanceof BucketItem)
      .map(entry -> saveJson(cache, entry.getKey().location(), makeJson((BucketItem)entry.getValue()))));
  }

  @Override
  public String getName() {
    return modId + " Fluid Bucket Model Provider";
  }
}
