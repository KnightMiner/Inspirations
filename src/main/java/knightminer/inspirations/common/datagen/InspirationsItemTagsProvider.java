package knightminer.inspirations.common.datagen;

import knightminer.inspirations.Inspirations;
import knightminer.inspirations.building.InspirationsBuilding;
import knightminer.inspirations.library.InspirationsTags;
import knightminer.inspirations.recipes.InspirationsRecipes;
import knightminer.inspirations.tools.InspirationsTools;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.ItemTagsProvider;
import net.minecraft.item.DyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.Tags;

import javax.annotation.Nonnull;

public class InspirationsItemTagsProvider extends ItemTagsProvider {
	public InspirationsItemTagsProvider(DataGenerator gen) {
		super(gen);
	}

	@Nonnull
	@Override
	public String getName() {
		return "Inspirations Item Tags";
	}

	@Override
	protected void registerTags() {
		registerInspTags();
		registerForgeTags();
		registerVanillaTags();
	}

	private void registerInspTags() {
		this.copy(InspirationsTags.Blocks.MULCH, InspirationsTags.Items.MULCH);
		this.copy(InspirationsTags.Blocks.SMALL_FLOWERS, InspirationsTags.Items.SMALL_FLOWERS);
		this.copy(InspirationsTags.Blocks.CARPETED_TRAPDOORS, InspirationsTags.Items.CARPETED_TRAPDOORS);
		this.copy(InspirationsTags.Blocks.BOOKSHELVES, InspirationsTags.Items.BOOKSHELVES);
		this.copy(InspirationsTags.Blocks.ENLIGHTENED_BUSHES, InspirationsTags.Items.ENLIGHTENED_BUSHES);

		this.getBuilder(InspirationsTags.Items.BOOKS)
				.add(InspirationsBuilding.coloredBooks)
				.add(InspirationsBuilding.redstoneBook)
				.add(Items.BOOK,Items.WRITABLE_BOOK, Items.WRITTEN_BOOK)
				.add(Items.ENCHANTED_BOOK, Items.KNOWLEDGE_BOOK)
				;

		// Copy the item form for us to use.
		this.copy(BlockTags.CARPETS, InspirationsTags.Items.CARPETS);

		this.getBuilder(InspirationsTags.Items.WAYPOINT_COMPASSES).add(InspirationsTools.waypointCompasses);

	}

	private void registerForgeTags() {
		this.getBuilder(Tags.Items.BOOKSHELVES).add(InspirationsTags.Items.BOOKSHELVES);

		// TODO: Once recipe pulse is reenabled.
		if (Inspirations.pulseManager.isPulseLoaded(InspirationsRecipes.pulseID)) {
		for(DyeColor color : DyeColor.values()) {
			Tag<Item> tag = ItemTags.getCollection()
					.getOrCreate(new ResourceLocation("forge", "dyes/" + color.getName()));
			this.getBuilder(tag).add(InspirationsRecipes.simpleDyedWaterBottle.get(color));
		}
		this.getBuilder(Tags.Items.DYES).add((Item[]) InspirationsRecipes.simpleDyedWaterBottle.values().toArray());
		}
	}

	private void registerVanillaTags() {
		this.getBuilder(ItemTags.ARROWS).add(InspirationsTools.redstoneArrow);
		this.copy(BlockTags.LEAVES, ItemTags.LEAVES);
		this.copy(BlockTags.WOODEN_TRAPDOORS, ItemTags.WOODEN_TRAPDOORS);
	}
}
