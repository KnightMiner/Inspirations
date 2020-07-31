package knightminer.inspirations.common.datagen;

import knightminer.inspirations.Inspirations;
import knightminer.inspirations.building.InspirationsBuilding;
import knightminer.inspirations.building.block.RopeBlock;
import knightminer.inspirations.common.Config;
import knightminer.inspirations.common.data.FillTexturedBlockLootFunction;
import knightminer.inspirations.tools.InspirationsTools;
import knightminer.inspirations.tweaks.InspirationsTweaks;
import knightminer.inspirations.utility.InspirationsUtility;
import net.minecraft.advancements.criterion.StatePropertiesPredicate;
import net.minecraft.block.Block;
import net.minecraft.block.DoorBlock;
import net.minecraft.data.loot.BlockLootTables;
import net.minecraft.item.DyeColor;
import net.minecraft.item.Items;
import net.minecraft.loot.ConstantRange;
import net.minecraft.loot.ItemLootEntry;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.TableLootEntry;
import net.minecraft.loot.conditions.BlockStateProperty;
import net.minecraft.loot.functions.CopyName;
import net.minecraft.loot.functions.SetCount;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Objects;
import java.util.stream.Collectors;

// TODO: javadocs
public class InspirationsBlockLootTable extends BlockLootTables {

  @Override
  protected Iterable<Block> getKnownBlocks() {
    // We only care about our blocks.
    return ForgeRegistries.BLOCKS.getValues().stream()
                                 .filter((block) -> {
                                   String ns = Objects.requireNonNull(block.getRegistryName()).getNamespace();
                                   return ns.equals(Inspirations.modID) || ns.equals("minecraft");
                                 })
                                 .collect(Collectors.toList());
  }

  @Override
  protected void addTables() {
    super.addTables();

    this.addBuilding();
    this.addTools();
    this.addTweaks();
    this.addRecipes();
    this.addUtility();
  }

  private void addBuilding() {
    // enum
    InspirationsBuilding.bookshelf.values().forEach(block -> this.registerLootTable(block, this::droppingWithNameAndTexture));
    InspirationsBuilding.enlightenedBush.values().forEach(block -> this.registerLootTable(block, this::enlightenedBush));
    InspirationsBuilding.mulch.values().forEach(this::registerDropSelfLootTable);
    InspirationsBuilding.path.values().forEach(this::registerDropSelfLootTable);

    // glass doors
    this.registerSilkTouch(InspirationsBuilding.glassTrapdoor);
    // For glass doors, they need to only drop from one of the blocks so it doesn't dupe.
    this.registerLootTable(InspirationsBuilding.glassDoor, LootTable.builder()
                                                                    .addLootPool(LootPool.builder().addEntry(ItemLootEntry.builder(InspirationsBuilding.glassDoor))
                                                                                         .acceptCondition(BlockStateProperty.builder(InspirationsBuilding.glassDoor)
                                                                                                                            .fromProperties(StatePropertiesPredicate.Builder.newBuilder()
                                                                                                                                                                            .withProp(DoorBlock.HALF, DoubleBlockHalf.LOWER)
                                                                                                                                           )
                                                                                                         )
                                                                                         .acceptCondition(SILK_TOUCH)
                                                                                )
                          );

    // flowers
    InspirationsBuilding.flower.values().forEach(this::registerDropSelfLootTable);
    InspirationsBuilding.flowerPot.values().forEach(this::registerFlowerPot);

    // ropes
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
      for (DyeColor color : DyeColor.values()) {
        this.registerRedirect(
            InspirationsTweaks.fitCarpets.get(color),
            InspirationsTweaks.flatCarpets.get(color)
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
    InspirationsUtility.carpetedTrapdoors.values().forEach(this::registerDropSelfLootTable);
    InspirationsUtility.carpetedPressurePlates.forEach((color, plate) ->
                                                           this.registerLootTable(plate, LootTable.builder()
                                                                                                  .addLootPool(withSurvivesExplosion(plate, LootPool.builder()
                                                                                                                                                    .addEntry(ItemLootEntry.builder(plate.getCarpet()))))
                                                                                                  .addLootPool(withSurvivesExplosion(plate, LootPool.builder()
                                                                                                                                                    .addEntry(ItemLootEntry.builder(Items.STONE_PRESSURE_PLATE)))))
                                                      );
    this.registerDropSelfLootTable(InspirationsUtility.pipe);
    this.registerDropSelfLootTable(InspirationsUtility.collector);
    this.registerDropping(InspirationsUtility.torchLeverFloor, InspirationsUtility.torchLeverItem);
    this.registerDropping(InspirationsUtility.torchLeverWall, InspirationsUtility.torchLeverItem);
  }

  private LootTable.Builder rope(Block block) {
    RopeBlock rope = (RopeBlock)block;
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
                                                                                                      .fromProperties(StatePropertiesPredicate.Builder.newBuilder()
                                                                                                                                                      .withProp(RopeBlock.RUNGS, RopeBlock.Rungs.NONE))
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
                                           .addLootPool(LootPool.builder().addEntry(TableLootEntry.builder(originalBlock.getLootTable()))
                                                       ));
  }
}
