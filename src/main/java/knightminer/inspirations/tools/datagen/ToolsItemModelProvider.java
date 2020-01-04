package knightminer.inspirations.tools.datagen;

import knightminer.inspirations.common.datagen.InspirationsItemModelProvider;
import knightminer.inspirations.tools.InspirationsTools;
import knightminer.inspirations.tools.client.BarometerPropertyGetter;
import knightminer.inspirations.tools.client.PhotometerPropertyGetter;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.client.model.generators.ExistingFileHelper;
import net.minecraftforge.client.model.generators.ModelBuilder;

import javax.annotation.Nonnull;

public class ToolsItemModelProvider extends InspirationsItemModelProvider {
	public ToolsItemModelProvider(DataGenerator gen, ExistingFileHelper existingFileHelper) {
		super(gen, existingFileHelper);
	}

	@Nonnull
	@Override
	public String getName() {
		return "Inspirations - Tools Items Models";
	}

	@Override
	protected void registerModels() {
		createSelfItem(InspirationsTools.lock);
		createSelfItem(InspirationsTools.key);
		createSelfItem(InspirationsTools.redstoneCharger);
		createSelfItem(InspirationsTools.redstoneArrow);

		makePropertyItem(InspirationsTools.barometer, BarometerPropertyGetter.ID, 49,
				(x) -> x == 0 ? 0.0f: (x-0.5f)/48
		).transforms()
				.transform(ModelBuilder.Perspective.FIRSTPERSON_LEFT)
					.rotation(14.74f, -24.25f, 0)
					.translation(1, 4, 2)
					.scale(0.65f)
				.end()
				.transform(ModelBuilder.Perspective.FIRSTPERSON_RIGHT)
					.rotation(14.74f, 155.75f, 0)
					.translation(1, 4, 2)
					.scale(0.65f)
				.end()
				.transform(ModelBuilder.Perspective.THIRDPERSON_LEFT)
					.translation(0, 0, -0.25f).scale(0.5f).end()
				.transform(ModelBuilder.Perspective.THIRDPERSON_RIGHT)
					.translation(0, 0, -0.25f).scale(0.5f).end()
		.end();

		makePropertyItemRanged(InspirationsTools.photometer, PhotometerPropertyGetter.ID, 16).transforms()
				.transform(ModelBuilder.Perspective.GROUND)
					.translation(0, 2, 0).scale(0.5f).end()
				.transform(ModelBuilder.Perspective.HEAD)
					.translation(0, 12, 0).end()
				.transform(ModelBuilder.Perspective.THIRDPERSON_LEFT)
					.rotation(20, 0, 0)
					.translation(-0.25f, 2.75f, 1)
					.scale(0.55f)
				.end()
				.transform(ModelBuilder.Perspective.THIRDPERSON_RIGHT)
					.rotation(20, 0, 0)
					.translation(0.25f, 2.75f, 1)
					.scale(0.55f)
				.end()
				.transform(ModelBuilder.Perspective.FIRSTPERSON_LEFT)
					.rotation(8, -11, 0)
					.translation(2.5f, 2.75f, 0)
					.scale(0.68f)
				.end()
				.transform(ModelBuilder.Perspective.FIRSTPERSON_RIGHT)
					.rotation(8, -11, 0)
					.translation(2.5f, 2.75f, 0)
					.scale(0.68f)
				.end()
		.end();
	}
}
