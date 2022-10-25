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

import net.minecraft.data.TagsProvider.Builder;

public class InspirationsBlockTagsProvider extends BlockTagsProvider {
  public InspirationsBlockTagsProvider(DataGenerator gen, ExistingFileHelper existing) {
    super(gen, Inspirations.modID, existing);
  }

  @Override
  public String getName() {
    return "Inspirations Block Tags";
  }

  @Override
  protected void addTags() {
    registerInspTags();
    registerVanillaTags();
  }

  private void registerInspTags() {

    // building
    this.tag(InspirationsTags.Blocks.MULCH).add(toArray(InspirationsBuilding.mulch));
    this.tag(InspirationsTags.Blocks.SMALL_FLOWERS).add(toArray(InspirationsBuilding.flower));
    this.tag(InspirationsTags.Blocks.FLOWER_POTS).add(toArray(InspirationsBuilding.flowerPot));
    this.tag(InspirationsTags.Blocks.ENLIGHTENED_BUSHES).add(toArray(InspirationsBuilding.enlightenedBush));
    this.tag(InspirationsTags.Blocks.SHELVES).add(toArray(InspirationsBuilding.shelf));
    this.tag(BlockTags.CLIMBABLE).add(InspirationsBuilding.rope, InspirationsBuilding.vine);
    this.tag(BlockTags.DOORS).add(InspirationsBuilding.glassDoor);
    this.tag(BlockTags.TRAPDOORS).add(InspirationsBuilding.glassTrapdoor);

    this.tag(BlockTags.WOODEN_DOORS).add(InspirationsBuilding.glassDoor);

    // utility
    this.tag(InspirationsTags.Blocks.CARPETED_TRAPDOORS).add(toArray(InspirationsUtility.carpetedTrapdoors));
    this.tag(InspirationsTags.Blocks.CARPETED_PRESSURE_PLATES).add(toArray(InspirationsUtility.carpetedPressurePlates));

    // recipes
    this.tag(InspirationsTags.Blocks.CAULDRON_FIRE).add(Blocks.FIRE, Blocks.SOUL_FIRE).addTag(BlockTags.CAMPFIRES);
    this.tag(InspirationsTags.Blocks.CAULDRON_ICE).add(Blocks.PACKED_ICE, Blocks.BLUE_ICE);
  }

  private void registerVanillaTags() {
    this.tag(BlockTags.LEAVES).addTag(InspirationsTags.Blocks.ENLIGHTENED_BUSHES);
    //this.getBuilder(BlockTags.DIRT_LIKE).add(InspirationsTags.Blocks.MULCH);
    this.tag(BlockTags.BAMBOO_PLANTABLE_ON).addTag(InspirationsTags.Blocks.MULCH);
    this.tag(BlockTags.WOODEN_TRAPDOORS).addTag(InspirationsTags.Blocks.CARPETED_TRAPDOORS);
    this.tag(BlockTags.FLOWER_POTS).addTag(InspirationsTags.Blocks.FLOWER_POTS);
    Builder<Block> carpets = this.tag(BlockTags.CARPETS);
    InspirationsTweaks.fitCarpets.forEach(block -> carpets.add(block));
    this.tag(BlockTags.PIGLIN_REPELLENTS).add(InspirationsUtility.soulLeverWall, InspirationsUtility.soulLeverFloor);
    this.tag(BlockTags.WALL_POST_OVERRIDE).add(
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
