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
import net.minecraft.util.ResourceLocation;
import slimeknights.mantle.registration.object.EnumObject;

import javax.annotation.Nullable;
import java.nio.file.Path;

public class InspirationsBlockTagsProvider extends BlockTagsProvider {
  public InspirationsBlockTagsProvider(DataGenerator gen) {
    super(gen);
    modId = Inspirations.modID;
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
    this.getOrCreateBuilder(InspirationsTags.Blocks.BOOKSHELVES).add(toArray(InspirationsBuilding.bookshelf));
    this.getOrCreateBuilder(BlockTags.CLIMBABLE).add(InspirationsBuilding.rope, InspirationsBuilding.vine);

    // utility
    this.getOrCreateBuilder(InspirationsTags.Blocks.CARPETED_TRAPDOORS).add(toArray(InspirationsUtility.carpetedTrapdoors));
    this.getOrCreateBuilder(InspirationsTags.Blocks.CARPETED_PRESSURE_PLATES).add(toArray(InspirationsUtility.carpetedPressurePlates));

    // recipes
    this.getOrCreateBuilder(BlockTags.CAMPFIRES); // dummy builder so I can use campfires
    this.getOrCreateBuilder(InspirationsTags.Blocks.CAULDRON_FIRE).add(Blocks.FIRE, Blocks.SOUL_FIRE).addTag(BlockTags.CAMPFIRES);
    this.getOrCreateBuilder(InspirationsTags.Blocks.CAULDRON_ICE).add(Blocks.PACKED_ICE, Blocks.BLUE_ICE);
  }

  private void registerVanillaTags() {
    this.getOrCreateBuilder(BlockTags.LEAVES).addTag(InspirationsTags.Blocks.ENLIGHTENED_BUSHES);
    //this.getBuilder(BlockTags.DIRT_LIKE).add(InspirationsTags.Blocks.MULCH);
    this.getOrCreateBuilder(BlockTags.BAMBOO_PLANTABLE_ON).addTag(InspirationsTags.Blocks.MULCH);
    this.getOrCreateBuilder(BlockTags.WOODEN_TRAPDOORS).addTag(InspirationsTags.Blocks.CARPETED_TRAPDOORS);
    this.getOrCreateBuilder(BlockTags.FLOWER_POTS).addTag(InspirationsTags.Blocks.FLOWER_POTS);
    this.getOrCreateBuilder(BlockTags.CARPETS).add(toArray(InspirationsTweaks.fitCarpets));
  }

  @SuppressWarnings("NullableProblems") // forge overwrites the method behavior to be nullable
  @Override
  @Nullable
  protected Path makePath(ResourceLocation id) {
    // ignore campfires, I do not change that tag. Extend this if need more tags in the future before 1.16.2
    if (BlockTags.CAMPFIRES.getName().equals(id)) {
      return null;
    }
    return super.makePath(id);
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
