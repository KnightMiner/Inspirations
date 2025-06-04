package knightminer.inspirations.common.datagen;

import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class InspirationsLootTableProvider extends LootTableProvider {
  public InspirationsLootTableProvider(PackOutput packOutput) {
    super(packOutput, Set.of(), List.of(
      new SubProviderEntry(InspirationsBlockLootTable::new, LootContextParamSets.BLOCK)
    ));
  }

  @Override
  protected void validate(Map<ResourceLocation, LootTable> map, ValidationContext validationcontext) {
    // Do not validate against all registered loot tables
  }
}
