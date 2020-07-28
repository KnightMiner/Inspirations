package knightminer.inspirations.common.datagen;

import knightminer.inspirations.Inspirations;
import knightminer.inspirations.building.InspirationsBuilding;
import knightminer.inspirations.library.InspirationsTags;
import knightminer.inspirations.tweaks.InspirationsTweaks;
import knightminer.inspirations.utility.InspirationsUtility;
import net.minecraft.block.Blocks;
import net.minecraft.data.BlockTagsProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.tags.BlockTags;

import javax.annotation.Nonnull;

public class InspirationsBlockTagsProvider extends BlockTagsProvider {
	public InspirationsBlockTagsProvider(DataGenerator gen) {
		super(gen);
		modId = Inspirations.modID;
	}

	@Nonnull
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
		this.getOrCreateBuilder(InspirationsTags.Blocks.MULCH)
				.add(InspirationsBuilding.plainMulch)
				.add(InspirationsBuilding.blackMulch)
				.add(InspirationsBuilding.blueMulch)
				.add(InspirationsBuilding.brownMulch)
				.add(InspirationsBuilding.redMulch);

		this.getOrCreateBuilder(InspirationsTags.Blocks.SMALL_FLOWERS)
				.add(InspirationsBuilding.flower_rose)
				.add(InspirationsBuilding.flower_cyan)
				.add(InspirationsBuilding.flower_paeonia)
				.add(InspirationsBuilding.flower_syringa);

		this.getOrCreateBuilder(InspirationsTags.Blocks.FLOWER_POTS)
				.add(InspirationsBuilding.potted_rose)
				.add(InspirationsBuilding.potted_cyan)
				.add(InspirationsBuilding.potted_paeonia)
				.add(InspirationsBuilding.potted_syringa);

		this.getOrCreateBuilder(InspirationsTags.Blocks.ENLIGHTENED_BUSHES)
				.add(InspirationsBuilding.whiteEnlightenedBush)
				.add(InspirationsBuilding.blueEnlightenedBush)
				.add(InspirationsBuilding.greenEnlightenedBush)
				.add(InspirationsBuilding.redEnlightenedBush);

		this.getOrCreateBuilder(InspirationsTags.Blocks.CARPETED_TRAPDOORS)
				.add(InspirationsUtility.carpetedTrapdoors);
		this.getOrCreateBuilder(InspirationsTags.Blocks.CARPETED_PRESSURE_PLATES)
				.add(InspirationsUtility.carpetedPressurePlates);

		this.getOrCreateBuilder(InspirationsTags.Blocks.BOOKSHELVES)
				.add(InspirationsBuilding.shelf_normal)
				.add(InspirationsBuilding.shelf_ancient)
				.add(InspirationsBuilding.shelf_rainbow)
				.add(InspirationsBuilding.shelf_tomes);

		this.getOrCreateBuilder(InspirationsTags.Blocks.CAULDRON_FIRE)
				.add(Blocks.FIRE);
	}

	private void registerVanillaTags() {
		this.getOrCreateBuilder(BlockTags.LEAVES).addTag(InspirationsTags.Blocks.ENLIGHTENED_BUSHES);
		//this.getBuilder(BlockTags.DIRT_LIKE).add(InspirationsTags.Blocks.MULCH);
		this.getOrCreateBuilder(BlockTags.BAMBOO_PLANTABLE_ON).addTag(InspirationsTags.Blocks.MULCH);
		this.getOrCreateBuilder(BlockTags.WOODEN_TRAPDOORS).addTag(InspirationsTags.Blocks.CARPETED_TRAPDOORS);
		this.getOrCreateBuilder(BlockTags.FLOWER_POTS).addTag(InspirationsTags.Blocks.FLOWER_POTS);

		this.getOrCreateBuilder(BlockTags.CARPETS).add(InspirationsTweaks.fitCarpets);
	}
}
