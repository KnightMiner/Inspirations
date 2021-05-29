package knightminer.inspirations.common.datagen;

import knightminer.inspirations.Inspirations;
import knightminer.inspirations.building.InspirationsBuilding;
import knightminer.inspirations.library.InspirationsTags;
import knightminer.inspirations.tweaks.InspirationsTweaks;
import knightminer.inspirations.utility.InspirationsUtility;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.data.BlockTagsProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.tags.BlockTags;
import net.minecraftforge.common.data.ExistingFileHelper;
import slimeknights.mantle.registration.object.EnumObject;

public class InspirationsBlockTagsProvider extends BlockTagsProvider {
  public InspirationsBlockTagsProvider(DataGenerator gen, ExistingFileHelper existing) {
    super(gen, Inspirations.modID, existing);
  }

  @Override
  public String getName() {
    return "Inspirations Block Tags";
  }

  @Override
  protected void registerTags() {
    registerInspTags();
    registerVanillaTags();
  }

  private void registerInspTags() {

    // building
    this.getOrCreateBuilder(InspirationsTags.Blocks.MULCH).add(toArray(InspirationsBuilding.mulch));
    this.getOrCreateBuilder(InspirationsTags.Blocks.SMALL_FLOWERS).add(toArray(InspirationsBuilding.flower));
    this.getOrCreateBuilder(InspirationsTags.Blocks.FLOWER_POTS).add(toArray(InspirationsBuilding.flowerPot));
    this.getOrCreateBuilder(InspirationsTags.Blocks.ENLIGHTENED_BUSHES).add(toArray(InspirationsBuilding.enlightenedBush));
    this.getOrCreateBuilder(InspirationsTags.Blocks.SHELVES).add(toArray(InspirationsBuilding.shelf));
    this.getOrCreateBuilder(BlockTags.CLIMBABLE).add(InspirationsBuilding.rope, InspirationsBuilding.vine);
    this.getOrCreateBuilder(BlockTags.DOORS).add(InspirationsBuilding.glassDoor);
    this.getOrCreateBuilder(BlockTags.TRAPDOORS).add(InspirationsBuilding.glassTrapdoor);

    // utility
    this.getOrCreateBuilder(InspirationsTags.Blocks.CARPETED_TRAPDOORS).add(toArray(InspirationsUtility.carpetedTrapdoors));
    this.getOrCreateBuilder(InspirationsTags.Blocks.CARPETED_PRESSURE_PLATES).add(toArray(InspirationsUtility.carpetedPressurePlates));

    // recipes
    this.getOrCreateBuilder(InspirationsTags.Blocks.CAULDRON_FIRE).add(Blocks.FIRE, Blocks.SOUL_FIRE).addTag(BlockTags.CAMPFIRES);
    this.getOrCreateBuilder(InspirationsTags.Blocks.CAULDRON_ICE).add(Blocks.PACKED_ICE, Blocks.BLUE_ICE);
  }

  private void registerVanillaTags() {
    this.getOrCreateBuilder(BlockTags.LEAVES).addTag(InspirationsTags.Blocks.ENLIGHTENED_BUSHES);
    //this.getBuilder(BlockTags.DIRT_LIKE).add(InspirationsTags.Blocks.MULCH);
    this.getOrCreateBuilder(BlockTags.BAMBOO_PLANTABLE_ON).addTag(InspirationsTags.Blocks.MULCH);
    this.getOrCreateBuilder(BlockTags.WOODEN_TRAPDOORS).addTag(InspirationsTags.Blocks.CARPETED_TRAPDOORS);
    this.getOrCreateBuilder(BlockTags.FLOWER_POTS).addTag(InspirationsTags.Blocks.FLOWER_POTS);
    Builder<Block> carpets = this.getOrCreateBuilder(BlockTags.CARPETS);
    InspirationsTweaks.fitCarpets.forEach(carpets::addItemEntry);
    this.getOrCreateBuilder(BlockTags.PIGLIN_REPELLENTS).add(InspirationsUtility.soulLeverWall, InspirationsUtility.soulLeverFloor);
    this.getOrCreateBuilder(BlockTags.WALL_POST_OVERRIDE).add(
            InspirationsUtility.torchLeverFloor, InspirationsUtility.torchLeverWall,
            InspirationsUtility.soulLeverFloor, InspirationsUtility.soulLeverWall
    );
  }

  /**
   * Converts an enum object into an array of values
   * @param object Enum object
   * @return Array of enum object values
   */
  private static Block[] toArray(EnumObject<?,? extends Block> object) {
    return object.values().toArray(new Block[0]);
  }
}
