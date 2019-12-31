package knightminer.inspirations.common.datagen;

import knightminer.inspirations.Inspirations;
import knightminer.inspirations.building.InspirationsBuilding;
import knightminer.inspirations.building.block.RopeBlock;
import knightminer.inspirations.common.Config;
import knightminer.inspirations.common.data.FillTexturedBlockLootFunction;
import knightminer.inspirations.recipes.InspirationsRecipes;
import knightminer.inspirations.tools.InspirationsTools;
import knightminer.inspirations.tweaks.InspirationsTweaks;
import knightminer.inspirations.utility.InspirationsUtility;
import net.minecraft.block.Block;
import net.minecraft.data.loot.BlockLootTables;
import net.minecraft.item.DyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.world.storage.loot.ConstantRange;
import net.minecraft.world.storage.loot.ItemLootEntry;
import net.minecraft.world.storage.loot.LootPool;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraft.world.storage.loot.TableLootEntry;
import net.minecraft.world.storage.loot.conditions.BlockStateProperty;
import net.minecraft.world.storage.loot.functions.CopyName;
import net.minecraft.world.storage.loot.functions.SetCount;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import java.util.stream.Collectors;

public class InspirationsBlockLootTable extends BlockLootTables {

	// We only care about our blocks.
	@Nonnull
	@Override
	protected Iterable<Block> getKnownBlocks() {
		return ForgeRegistries.BLOCKS.getValues().stream()
				.filter((block) -> {
					String ns = block.getRegistryName().getNamespace();
					return ns.equals(Inspirations.modID) || ns.equals("minecraft");
				})
				.collect(Collectors.toList());
	}

	@Override
	protected void addTables() {
		super.addTables();

		addIf(InspirationsBuilding.pulseID, this::addBuilding);
		addIf(InspirationsTools.pulseID, this::addTools);
		addIf(InspirationsTweaks.pulseID, this::addTweaks);
		addIf(InspirationsRecipes.pulseID, this::addRecipes);
		addIf(InspirationsUtility.pulseID, this::addUtility);
	}

	private void addIf(String pulseID, Runnable registrar) {
		if (Inspirations.pulseManager.isPulseLoaded(pulseID)) {
			registrar.run();
		}
	}

	private void addBuilding() {
		this.registerLootTable(InspirationsBuilding.shelf_normal, this::droppingWithNameAndTexture);
		this.registerLootTable(InspirationsBuilding.shelf_rainbow, this::droppingWithNameAndTexture);
		this.registerLootTable(InspirationsBuilding.shelf_ancient, this::droppingWithNameAndTexture);
		this.registerLootTable(InspirationsBuilding.shelf_tomes, this::droppingWithNameAndTexture);

		this.registerLootTable(InspirationsBuilding.whiteEnlightenedBush, this::enlightenedBush);
		this.registerLootTable(InspirationsBuilding.redEnlightenedBush, this::enlightenedBush);
		this.registerLootTable(InspirationsBuilding.greenEnlightenedBush, this::enlightenedBush);
		this.registerLootTable(InspirationsBuilding.blueEnlightenedBush, this::enlightenedBush);

		this.registerDropSelfLootTable(InspirationsBuilding.plainMulch);
		this.registerDropSelfLootTable(InspirationsBuilding.blackMulch);
		this.registerDropSelfLootTable(InspirationsBuilding.blueMulch);
		this.registerDropSelfLootTable(InspirationsBuilding.brownMulch);
		this.registerDropSelfLootTable(InspirationsBuilding.redMulch);

		this.registerSilkTouch(InspirationsBuilding.glassDoor);
		this.registerSilkTouch(InspirationsBuilding.glassTrapdoor);

		this.registerDropSelfLootTable(InspirationsBuilding.flower_rose);
		this.registerDropSelfLootTable(InspirationsBuilding.flower_cyan);
		this.registerDropSelfLootTable(InspirationsBuilding.flower_paeonia);
		this.registerDropSelfLootTable(InspirationsBuilding.flower_syringa);

		this.registerFlowerPot(InspirationsBuilding.potted_rose);
		this.registerFlowerPot(InspirationsBuilding.potted_cyan);
		this.registerFlowerPot(InspirationsBuilding.potted_paeonia);
		this.registerFlowerPot(InspirationsBuilding.potted_syringa);

		this.registerDropSelfLootTable(InspirationsBuilding.path_brick);
		this.registerDropSelfLootTable(InspirationsBuilding.path_rock);
		this.registerDropSelfLootTable(InspirationsBuilding.path_round);
		this.registerDropSelfLootTable(InspirationsBuilding.path_tile);

		this.registerLootTable(InspirationsBuilding.rope, this::rope);
		this.registerLootTable(InspirationsBuilding.chain, this::rope);
		this.registerLootTable(InspirationsBuilding.vine, this::rope);
	}

	private void addTools() {
		// func_218482 = droppingNothing()
		this.registerLootTable(InspirationsTools.redstoneCharge, func_218482_a());
	}

	private void addTweaks() {
		if (Config.enableFittedCarpets.get()) {
			for(DyeColor color : DyeColor.values()) {
				this.registerRedirect(
						InspirationsTweaks.fitCarpets[color.getId()],
						InspirationsTweaks.flatCarpets[color.getId()]
				);
			}
		}
		this.registerLootTable(InspirationsTweaks.wetHopper, droppingWithName(InspirationsTweaks.dryHopper));
		this.registerDropping(InspirationsTweaks.sugarCane, InspirationsTweaks.sugarCaneSeeds);
		this.registerDropping(InspirationsTweaks.cactus, InspirationsTweaks.cactusSeeds);
	}

	private void addRecipes() {

	}

	private void addUtility() {
		for(DyeColor color : DyeColor.values()) {
			this.registerDropSelfLootTable(InspirationsUtility.carpetedTrapdoors[color.getId()]);

			Block trapdoor = InspirationsUtility.carpetedPressurePlates[color.getId()];
			// We don't use these values.
			Item carpet = trapdoor.getPickBlock(null, null, null, null, null).getItem();
			this.registerLootTable(trapdoor, LootTable.builder()
				.addLootPool(withSurvivesExplosion(trapdoor, LootPool.builder()
						.addEntry(ItemLootEntry.builder(carpet))
				))
				.addLootPool(withSurvivesExplosion(carpet, LootPool.builder()
						.addEntry(ItemLootEntry.builder(Items.STONE_PRESSURE_PLATE))
				))
			);
		}

		this.registerDropSelfLootTable(InspirationsUtility.pipe);
		this.registerDropSelfLootTable(InspirationsUtility.collector);
		this.registerDropping(InspirationsUtility.torchLeverFloor, InspirationsUtility.torchLeverItem);
		this.registerDropping(InspirationsUtility.torchLeverWall, InspirationsUtility.torchLeverItem);
	}

	private LootTable.Builder rope(Block block) {
		RopeBlock rope = (RopeBlock) block;
		return LootTable.builder()
				// The rope block itself
				.addLootPool(withSurvivesExplosion(block, LootPool.builder()
						.addEntry(ItemLootEntry.builder(block))
				))
				// And, if rungs are present the items for those.
				.addLootPool(withExplosionDecay(block, LootPool.builder()
						.addEntry(ItemLootEntry.builder(rope.getRungsItem())
								.acceptFunction(SetCount.builder(ConstantRange.of(RopeBlock.RUNG_ITEM_COUNT)))
						)
						.acceptCondition(BlockStateProperty.builder(rope)
								.with(RopeBlock.RUNGS, RopeBlock.Rungs.NONE)
								.inverted()
						)
				));
	}

	private LootTable.Builder enlightenedBush(Block bush) {
		return LootTable.builder()
				// No explosion check, since that's not going to pass a
				// tool check.
				.addLootPool(LootPool.builder()
						.addEntry(ItemLootEntry.builder(bush)
								.acceptFunction(FillTexturedBlockLootFunction::new))
						.acceptCondition(SILK_TOUCH_OR_SHEARS)
				);
	}

	private LootTable.Builder droppingWithNameAndTexture(Block block) {
		return LootTable.builder()
				.addLootPool(withSurvivesExplosion(block, LootPool.builder()
						.addEntry(ItemLootEntry.builder(block)
								.acceptFunction(CopyName.builder(CopyName.Source.BLOCK_ENTITY))
								.acceptFunction(FillTexturedBlockLootFunction::new)
						)));
	}

	private void registerRedirect(Block block, Block originalBlock) {
		this.registerLootTable(block, LootTable.builder()
				.addLootPool(LootPool.builder()
						.addEntry(TableLootEntry.builder(originalBlock.getLootTable()))
				));
	}
}
