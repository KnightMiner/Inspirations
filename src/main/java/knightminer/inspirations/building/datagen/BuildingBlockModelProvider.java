package knightminer.inspirations.building.datagen;

import knightminer.inspirations.Inspirations;
import net.minecraft.data.DataGenerator;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.generators.BlockModelBuilder;
import net.minecraftforge.client.model.generators.BlockModelProvider;
import net.minecraftforge.client.model.generators.ExistingFileHelper;
import net.minecraftforge.client.model.generators.ModelFile;

import javax.annotation.Nonnull;

/** Generates the models for the Building module */
public class BuildingBlockModelProvider extends BlockModelProvider {
	public BuildingBlockModelProvider(DataGenerator generator, ExistingFileHelper existingFileHelper) {
		super(generator, Inspirations.modID, existingFileHelper);
	}

	@Nonnull
	@Override
	public String getName() {
		return "Inspirations - Building Block Models";
	}

	@Override
	protected void registerModels() {
		makeMulch("plain");
		makeMulch("brown");
		makeMulch("red");
		makeMulch("black");
		makeMulch("blue");

		makeFlower("cyan");
		makeFlower("paeonia");
		makeFlower("rose");
		makeFlower("syringa");

		makeGlassTrapdoor("bottom", "top", "open");

		BlockModelBuilder bush = getBuilder("block/enlightened_bush/template")
				.parent(getExistingFile(mcLoc("block/block")))
				.texture("particle", "#leaves");
		// The lights have alpha, so they override some pixels and leave others untouched.
		bush.element().allFaces((dir, face) -> face.texture("#leaves").cullface(dir).tintindex(0));
		bush.element().allFaces((dir, face) -> face.texture("#lights").cullface(dir));

		makeEnlightenedBush(bush, "white");
		makeEnlightenedBush(bush,"green");
		makeEnlightenedBush(bush,"red");
		makeEnlightenedBush(bush,"blue");
	}

	private void makeMulch(String color) {
		getBuilder("block/mulch/" + color)
				.parent(getExistingFile(mcLoc("block/cube_all")))
				.texture("all", modLoc("block/mulch_" + color));
	}

	private void makeFlower(String type) {
		ResourceLocation tex = modLoc("block/flower_" + type);
		getBuilder("block/flower/" + type)
				.parent(getExistingFile(mcLoc("block/cross")))
				.texture("cross", tex);
		getBuilder("block/potted/" + type)
				.parent(getExistingFile(mcLoc("block/flower_pot_cross")))
				.texture("plant", tex);
	}

	private void makeGlassTrapdoor(String... variants) {
		for(String variant: variants) {
			getBuilder("block/trapdoor/glass_trapdoor_" + variant)
					.parent(getExistingFile(mcLoc("block/template_orientable_trapdoor_" + variant)))
					.texture("texture", modLoc("block/glass_trapdoor"));
		}
	}

	private void makeEnlightenedBush(ModelFile template, String color) {
		getBuilder("block/enlightened_bush/" + color)
				.parent(template)
				.texture("leaves", mcLoc("block/oak_leaves"))
				.texture("lights", modLoc("block/lights_" + color));
	}
}
