package knightminer.inspirations.common.datagen;

import knightminer.inspirations.Inspirations;
import knightminer.inspirations.building.InspirationsBuilding;
import knightminer.inspirations.building.block.RopeBlock;
import knightminer.inspirations.common.Config;
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
import slimeknights.mantle.loot.RetexturedLootFunction;

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
    this.addUtility();
  }

  private void addBuilding() {
    // enum
    InspirationsBuilding.shelf.values().forEach(block -> this.add(block, this::droppingWithNameAndTexture));
    InspirationsBuilding.enlightenedBush.values().forEach(block -> this.add(block, this::enlightenedBush));
    InspirationsBuilding.mulch.values().forEach(this::dropSelf);
    InspirationsBuilding.path.values().forEach(this::dropSelf);

    // glass doors
    this.dropWhenSilkTouch(InspirationsBuilding.glassTrapdoor);
    // For glass doors, they need to only drop from one of the blocks so it doesn't dupe.
    this.add(InspirationsBuilding.glassDoor,
                           LootTable.lootTable().withPool(
                               LootPool.lootPool()
                                       .add(ItemLootEntry.lootTableItem(InspirationsBuilding.glassDoor))
                                       .when(
                                           BlockStateProperty.hasBlockStateProperties(InspirationsBuilding.glassDoor)
                                                             .setProperties(
                                                                 StatePropertiesPredicate.Builder.properties()
                                                                                                 .hasProperty(DoorBlock.HALF, DoubleBlockHalf.LOWER)))
                                       .when(HAS_SILK_TOUCH)));

    // flowers
    InspirationsBuilding.flower.values().forEach(this::dropSelf);
    InspirationsBuilding.flowerPot.values().forEach(this::dropPottedContents);

    // ropes
    this.add(InspirationsBuilding.rope, this::rope);
    this.add(InspirationsBuilding.vine, this::rope);
  }

  private void addTools() {
    this.add(InspirationsTools.redstoneCharge, noDrop());
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
    this.add(InspirationsTweaks.wetHopper, createNameableBlockEntityTable(InspirationsTweaks.dryHopper));
    this.dropOther(InspirationsTweaks.sugarCane, InspirationsTweaks.sugarCaneSeeds);
    this.dropOther(InspirationsTweaks.cactus, InspirationsTweaks.cactusSeeds);
  }

  private void addUtility() {
    InspirationsUtility.carpetedTrapdoors.values().forEach(this::dropSelf);
    InspirationsUtility.carpetedPressurePlates.forEach((color, plate) ->
                                                           this.add(plate, LootTable.lootTable()
                                                                                                  .withPool(applyExplosionCondition(plate, LootPool.lootPool()
                                                                                                                                                    .add(ItemLootEntry.lootTableItem(plate.getCarpet()))))
                                                                                                  .withPool(applyExplosionCondition(plate, LootPool.lootPool()
                                                                                                                                                    .add(ItemLootEntry.lootTableItem(Items.STONE_PRESSURE_PLATE)))))
                                                      );
    this.dropSelf(InspirationsUtility.pipe);
    this.dropSelf(InspirationsUtility.collector);
    // Wall blocks redirect to the floor table.
    this.dropOther(InspirationsUtility.torchLeverFloor, InspirationsUtility.torchLeverItem);
    this.dropOther(InspirationsUtility.soulLeverFloor, InspirationsUtility.soulLeverItem);
  }

  private LootTable.Builder rope(Block block) {
    RopeBlock rope = (RopeBlock)block;
    return LootTable.lootTable()
                    // The rope block itself
                    .withPool(applyExplosionCondition(block, LootPool.lootPool()
                                                                      .add(ItemLootEntry.lootTableItem(block))
                                                      ))
                    // And, if rungs are present the items for those.
                    .withPool(applyExplosionDecay(block, LootPool.lootPool()
                                                                   .add(ItemLootEntry.lootTableItem(rope.getRungsItem())
                                                                                          .apply(SetCount.setCount(ConstantRange.exactly(RopeBlock.RUNG_ITEM_COUNT)))
                                                                            )
                                                                   .when(BlockStateProperty.hasBlockStateProperties(rope)
                                                                                                      .setProperties(StatePropertiesPredicate.Builder.properties()
                                                                                                                                                      .hasProperty(RopeBlock.RUNGS, RopeBlock.Rungs.NONE))
                                                                                                      .invert()
                                                                                   )
                                                   ));
  }

  private LootTable.Builder enlightenedBush(Block bush) {
    return LootTable.lootTable()
                    // No explosion check, since that's not going to pass a
                    // tool check.
                    .withPool(LootPool.lootPool()
                                         .add(ItemLootEntry.lootTableItem(bush)
                                                                .apply(RetexturedLootFunction::new))
                                         .when(HAS_SHEARS_OR_SILK_TOUCH)
                                );
  }

  private LootTable.Builder droppingWithNameAndTexture(Block block) {
    return LootTable.lootTable()
                    .withPool(applyExplosionCondition(block, LootPool.lootPool()
                                                                      .add(ItemLootEntry.lootTableItem(block)
                                                                                             .apply(CopyName.copyName(CopyName.Source.BLOCK_ENTITY))
                                                                                             .apply(RetexturedLootFunction::new)
                                                                               )));
  }

  private void registerRedirect(Block block, Block originalBlock) {
    this.add(block, LootTable.lootTable()
                                           .withPool(LootPool.lootPool().add(TableLootEntry.lootTableReference(originalBlock.getLootTable()))
                                                       ));
  }
}
