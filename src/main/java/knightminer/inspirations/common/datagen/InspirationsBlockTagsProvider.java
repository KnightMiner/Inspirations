package knightminer.inspirations.common.datagen;

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
		this.getBuilder(InspirationsTags.Blocks.MULCH)
				.add(InspirationsBuilding.plainMulch)
				.add(InspirationsBuilding.blackMulch)
				.add(InspirationsBuilding.blueMulch)
				.add(InspirationsBuilding.brownMulch)
				.add(InspirationsBuilding.redMulch);

		this.getBuilder(InspirationsTags.Blocks.SMALL_FLOWERS)
				.add(InspirationsBuilding.flower_rose)
				.add(InspirationsBuilding.flower_cyan)
				.add(InspirationsBuilding.flower_paeonia)
				.add(InspirationsBuilding.flower_syringa);

		this.getBuilder(InspirationsTags.Blocks.FLOWER_POTS)
				.add(InspirationsBuilding.potted_rose)
				.add(InspirationsBuilding.potted_cyan)
				.add(InspirationsBuilding.potted_paeonia)
				.add(InspirationsBuilding.potted_syringa);

		this.getBuilder(InspirationsTags.Blocks.ENLIGHTENED_BUSHES)
				.add(InspirationsBuilding.whiteEnlightenedBush)
				.add(InspirationsBuilding.blueEnlightenedBush)
				.add(InspirationsBuilding.greenEnlightenedBush)
				.add(InspirationsBuilding.redEnlightenedBush);

		this.getBuilder(InspirationsTags.Blocks.CARPETED_TRAPDOORS)
				.add(InspirationsUtility.carpetedTrapdoors);
		this.getBuilder(InspirationsTags.Blocks.CARPETED_PRESSURE_PLATES)
				.add(InspirationsUtility.carpetedPressurePlates);

		this.getBuilder(InspirationsTags.Blocks.BOOKSHELVES)
				.add(InspirationsBuilding.shelf_normal)
				.add(InspirationsBuilding.shelf_ancient)
				.add(InspirationsBuilding.shelf_rainbow)
				.add(InspirationsBuilding.shelf_tomes);

		this.getBuilder(InspirationsTags.Blocks.CAULDRON_FIRE)
				.add(Blocks.FIRE);
	}

	private void registerVanillaTags() {
		this.getBuilder(BlockTags.LEAVES).add(InspirationsTags.Blocks.ENLIGHTENED_BUSHES);
		//this.getBuilder(BlockTags.DIRT_LIKE).add(InspirationsTags.Blocks.MULCH);
		this.getBuilder(BlockTags.BAMBOO_PLANTABLE_ON).add(InspirationsTags.Blocks.MULCH);
		this.getBuilder(BlockTags.WOODEN_TRAPDOORS).add(InspirationsTags.Blocks.CARPETED_TRAPDOORS);
		this.getBuilder(BlockTags.FLOWER_POTS).add(InspirationsTags.Blocks.FLOWER_POTS);

		this.getBuilder(BlockTags.CARPETS)
				.add(InspirationsTweaks.fitCarpets)
				.add(InspirationsTags.Blocks.CARPETED_TRAPDOORS);

	}
}
