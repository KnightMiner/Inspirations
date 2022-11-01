package knightminer.inspirations.common.datagen;

import knightminer.inspirations.Inspirations;
import knightminer.inspirations.building.InspirationsBuilding;
import knightminer.inspirations.building.block.RopeBlock;
import knightminer.inspirations.common.Config;
import knightminer.inspirations.recipes.InspirationsRecipes;
import knightminer.inspirations.tools.InspirationsTools;
import knightminer.inspirations.tweaks.InspirationsTweaks;
import knightminer.inspirations.utility.InspirationsUtility;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.data.loot.BlockLoot;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootTableReference;
import net.minecraft.world.level.storage.loot.functions.CopyNameFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraftforge.registries.ForgeRegistries;
import slimeknights.mantle.loot.function.RetexturedLootFunction;

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.stream.Collectors;

public class InspirationsBlockLootTable extends BlockLoot {
  @Nonnull
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
    this.addRecipes();
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
                                       .add(LootItem.lootTableItem(InspirationsBuilding.glassDoor))
                                       .when(
                                           LootItemBlockStatePropertyCondition.hasBlockStateProperties(InspirationsBuilding.glassDoor)
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

  private void addRecipes() {
    this.dropOther(InspirationsRecipes.beetrootSoupCauldron, Blocks.CAULDRON);
    this.dropOther(InspirationsRecipes.mushroomStewCauldron, Blocks.CAULDRON);
    this.dropOther(InspirationsRecipes.potatoSoupCauldron, Blocks.CAULDRON);
    this.dropOther(InspirationsRecipes.rabbitStewCauldron, Blocks.CAULDRON);
    this.dropOther(InspirationsRecipes.honeyCauldron, Blocks.CAULDRON);
    this.dropOther(InspirationsRecipes.milkCauldron, Blocks.CAULDRON);
    this.dropOther(InspirationsRecipes.dyeCauldron, Blocks.CAULDRON);
    this.dropOther(InspirationsRecipes.potionCauldron, Blocks.CAULDRON);
    this.dropOther(InspirationsRecipes.suspiciousStewCauldron, Blocks.CAULDRON);
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
                                                                                                                                                    .add(LootItem.lootTableItem(plate.getCarpet()))))
                                                                                                  .withPool(applyExplosionCondition(plate, LootPool.lootPool()
                                                                                                                                                    .add(LootItem.lootTableItem(Items.STONE_PRESSURE_PLATE)))))
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
                                                                      .add(LootItem.lootTableItem(block))
                                                      ))
                    // And, if rungs are present the items for those.
                    .withPool(applyExplosionDecay(block, LootPool.lootPool()
                                                                   .add(LootItem.lootTableItem(rope.getRungsItem())
                                                                                          .apply(SetItemCountFunction.setCount(ConstantValue.exactly(RopeBlock.RUNG_ITEM_COUNT)))
                                                                            )
                                                                   .when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(rope)
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
                                         .add(LootItem.lootTableItem(bush)
                                                                .apply(RetexturedLootFunction::new))
                                         .when(HAS_SHEARS_OR_SILK_TOUCH)
                                );
  }

  private LootTable.Builder droppingWithNameAndTexture(Block block) {
    return LootTable.lootTable()
                    .withPool(applyExplosionCondition(block, LootPool.lootPool()
                                                                      .add(LootItem.lootTableItem(block)
                                                                                             .apply(CopyNameFunction.copyName(CopyNameFunction.NameSource.BLOCK_ENTITY))
                                                                                             .apply(RetexturedLootFunction::new)
                                                                               )));
  }

  private void registerRedirect(Block block, Block originalBlock) {
    this.add(block, LootTable.lootTable()
                                           .withPool(LootPool.lootPool().add(LootTableReference.lootTableReference(originalBlock.getLootTable()))
                                                       ));
  }
}
