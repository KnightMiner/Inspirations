package knightminer.inspirations.common.datagen;

import knightminer.inspirations.Inspirations;
import net.minecraft.data.DataGenerator;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.generators.ExistingFileHelper;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;

import java.util.function.Function;

public abstract class InspirationsItemModelProvider extends ItemModelProvider {
	public static String GENERATED = "item/generated";

	public InspirationsItemModelProvider(DataGenerator generator, ExistingFileHelper existingFileHelper) {
		super(generator, Inspirations.modID, existingFileHelper);
	}

	protected ItemModelBuilder createSelfItem(Item item) {
		String name = item.getRegistryName().getPath();
		return withExistingParent(name, GENERATED).texture("layer0", "item/" + name);
	}

	protected ItemModelBuilder makePropertyItemRanged(Item item, ResourceLocation propID, int splits) {
		return makePropertyItem(item, propID, splits, f -> (float)f);
	}

	protected ItemModelBuilder makePropertyItemNormalized(Item item, ResourceLocation propID, int splits) {
		return makePropertyItem(item, propID, splits, (x) -> (float)x / splits);
	}

	protected ItemModelBuilder makePropertyItem(Item item, ResourceLocation propID, int count, Function<Integer, Float> valueFunc) {
		String name = item.getRegistryName().getPath();

		ItemModelBuilder builder = withExistingParent(name, GENERATED);
		builder.texture("layer0", modLoc(String.format("item/%s/%02d", name, 0)));

		for(int i = 1; i < count; i++) {
			builder.override().predicate(propID, valueFunc.apply(i))
					.model(withExistingParent(String.format("item/%s/%02d", name, i), builder.getLocation())
							.texture("layer0", String.format("item/%s/%02d", name, i)));
		}
		return builder;
	}
}
