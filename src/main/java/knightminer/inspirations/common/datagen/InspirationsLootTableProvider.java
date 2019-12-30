package knightminer.inspirations.common.datagen;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import knightminer.inspirations.Inspirations;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.LootTableProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootParameterSet;
import net.minecraft.world.storage.loot.LootParameterSets;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraft.world.storage.loot.LootTableManager;
import net.minecraft.world.storage.loot.ValidationResults;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class InspirationsLootTableProvider extends LootTableProvider {
	public InspirationsLootTableProvider(DataGenerator gen) {
		super(gen);
	}

	@Nonnull
	@Override
	public String getName() {
		return "Inspirations Loot Tables";
	}

	@Nonnull
	@Override
	protected List<Pair<Supplier<Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>>, LootParameterSet>> getTables() {
		return ImmutableList.of(
				Pair.of(InspirationsBlockLootTable::new, LootParameterSets.BLOCK)
		);
	}

	// Override to skip validating that vanilla's tables are present.
	@Override
	protected void validate(Map<ResourceLocation, LootTable> map, ValidationResults validationresults) {
		map.forEach((loc, table) -> LootTableManager.func_215302_a(validationresults, loc, table, map::get));
		// Remove vanilla's tables, which we also loaded so we can redirect stuff to them.
		// This ensures the remaining generator logic doesn't write those to files.
		map.keySet().removeIf((loc) -> !loc.getNamespace().equals(Inspirations.modID));
	}
}
