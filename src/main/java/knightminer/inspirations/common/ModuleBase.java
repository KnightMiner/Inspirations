package knightminer.inspirations.common;

import knightminer.inspirations.Inspirations;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootTableReference;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraftforge.event.LootTableLoadEvent;

public class ModuleBase {
  /* Loaded */
  @Deprecated
  public static boolean isBuildingLoaded() {
    return Config.buildingModule.get();
  }

  /**
   * Registers an object with a vanilla registry
   * @param registry Registry
   * @param name     Registry name
   * @param value    Value to registry
   * @param <T>      Registry type
   * @param <V>      Value type
   * @return Value registered
   */
  protected static <T, V extends T> V register(Registry<T> registry, String name, V value) {
    return Registry.register(registry, Inspirations.getResource(name), value);
  }

  /**
   * Adds entries from a loot table in the inspirations directory to a vanilla loot table
   * @param event LootTableLoadEvent
   * @param name  Name of vanilla table and the inspirations table
   */
  protected static void addToVanillaLoot(LootTableLoadEvent event, String name) {
    if (!event.getName().getNamespace().equals("minecraft") || !event.getName().getPath().equals(name)) {
      return;
    }
    ResourceLocation base = new ResourceLocation(name);
    LootTable table = event.getTable();
    if (table != LootTable.EMPTY) {
      ResourceLocation location = Inspirations.getResource(base.getPath());
      table.addPool(new LootPool.Builder()
                        .name(location.toString())
                        .setRolls(ConstantValue.exactly(1))
                        .add(LootTableReference.lootTableReference(location))
                        .build()
                   );
    }
  }
}
