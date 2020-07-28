package knightminer.inspirations.building.datagen;

import knightminer.inspirations.building.InspirationsBuilding;
import knightminer.inspirations.common.data.ConfigEnabledCondition;
import knightminer.inspirations.common.datagen.CondRecipe;
import knightminer.inspirations.library.InspirationsTags;
import knightminer.inspirations.library.Util;
import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.RecipeProvider;
import net.minecraft.data.ShapedRecipeBuilder;
import net.minecraft.item.DyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.IItemProvider;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.IConditionBuilder;

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.function.Consumer;

public class BuildingRecipeProvider extends RecipeProvider implements IConditionBuilder {
	private final ICondition BUILDING = ConfigEnabledCondition.MODULE_BUILDING;

	// Prevent needing to pass this into every method.
	private Consumer<IFinishedRecipe> consumer = null;

	public BuildingRecipeProvider(DataGenerator gen) {
		super(gen);
	}

	@Nonnull
	@Override
	public String getName() {
		return "Inspirations Recipes - Building";
	}

	@Override
	protected void registerRecipes(@Nonnull Consumer<IFinishedRecipe> consumer) {
		this.consumer = consumer;
		// First several one-off recipes.
		CondRecipe.shaped(InspirationsBuilding.glassDoor)
				.addCondition(BUILDING)
				.addCondition(ConfigEnabledCondition.GLASS_DOOR)
				.addCriterion("has_glass", hasItem(Items.GLASS_PANE))
				.key('G', Items.GLASS_PANE)
				.patternLine("GG")
				.patternLine("GG")
				.patternLine("GG")
				.build(consumer);

		CondRecipe.shaped(InspirationsBuilding.glassTrapdoor, 2)
				.addCondition(BUILDING)
				.addCondition(ConfigEnabledCondition.GLASS_DOOR)
				.addCriterion("has_glass", hasItem(Items.GLASS_PANE))
				.key('G', Items.GLASS_PANE)
				.patternLine("GGG")
				.patternLine("GGG")
				.build(consumer);

		buildingRope();
		buildingPath();
		buildingBookshelf();
		buildingMulch(InspirationsBuilding.blackMulch, Tags.Items.DYES_BLACK);
		buildingMulch(InspirationsBuilding.blueMulch, Tags.Items.DYES_BLUE);
		buildingMulch(InspirationsBuilding.brownMulch, Tags.Items.DYES_BROWN);
		buildingMulch(InspirationsBuilding.redMulch, Tags.Items.DYES_RED);
		buildingColoredBooks();

		buildingFlowerDye(InspirationsBuilding.flower_rose, Items.RED_DYE);
		buildingFlowerDye(InspirationsBuilding.flower_paeonia, Items.PINK_DYE);
		buildingFlowerDye(InspirationsBuilding.flower_syringa, Items.MAGENTA_DYE);
		buildingFlowerDye(InspirationsBuilding.flower_cyan, Items.CYAN_DYE);

		CondRecipe.shapeless(InspirationsBuilding.flower_cyan)
				.addCondition(BUILDING)
				.addCondition(ConfigEnabledCondition.FLOWERS)
				.addCondition(not(ConfigEnabledCondition.CAULDRON_DYEING))
				.addCriterion("has_dye", hasItem(Tags.Items.DYES_CYAN))
				.addCriterion("has_flower", hasItem(InspirationsBuilding.flower_rose))
				.addIngredient(Tags.Items.DYES_CYAN)
				.addIngredient(InspirationsBuilding.flower_rose)
				.build(consumer, "flower/cyan_flower");

		buildingEnlightnedBush(InspirationsBuilding.whiteEnlightenedBush, DyeColor.WHITE);
		buildingEnlightnedBush(InspirationsBuilding.redEnlightenedBush, DyeColor.RED);
		buildingEnlightnedBush(InspirationsBuilding.greenEnlightenedBush, DyeColor.GREEN);
		buildingEnlightnedBush(InspirationsBuilding.blueEnlightenedBush, DyeColor.BLUE);
	}

	private void buildingEnlightnedBush(Block bush, DyeColor dye) {
		ShapedRecipeBuilder builder = CondRecipe.shaped(bush)
				.textureSource(ItemTags.LEAVES)
				.textureMatchFirst()
				.addCondition(BUILDING)
				.addCondition(ConfigEnabledCondition.ENLIGHTENED_BUSH)
				.setGroup(Util.resource("enlightened_bush"))
				.addCriterion("has_leaves", hasItem(ItemTags.LEAVES))
				.addCriterion("has_glowstone", hasItem(Tags.Items.DUSTS_GLOWSTONE))
				.key('L', ItemTags.LEAVES)
				.key('G', Tags.Items.DUSTS_GLOWSTONE);

		if (dye != DyeColor.WHITE) {
			// First line - dye above the middle.
			builder = builder.key('D', Util.getDyeTag(dye)).patternLine(" D ");
		}
		builder.patternLine("GLG").build(consumer);
	}

	private void buildingRope() {
		CondRecipe.shaped(InspirationsBuilding.rope, 3)
				.addCondition(BUILDING)
				.addCondition(ConfigEnabledCondition.ROPE)
				.addCriterion("has_string", hasItem(Tags.Items.STRING))
				.key('S', Items.STRING)
				.patternLine("SS")
				.patternLine("SS")
				.patternLine("SS")
				.build(consumer);

		CondRecipe.shaped(InspirationsBuilding.vine, 3)
				.addCondition(BUILDING)
				.addCondition(ConfigEnabledCondition.ROPE)
				.addCriterion("has_vines", hasItem(Items.VINE))
				.key('V', Items.VINE)
				.patternLine("V")
				.patternLine("V")
				.patternLine("V")
				.build(consumer);

		CondRecipe.shaped(InspirationsBuilding.chain, 6)
				.addCondition(BUILDING)
				.addCondition(ConfigEnabledCondition.ROPE)
				.addCriterion("has_bars", hasItem(Items.IRON_BARS))
				.key('B', Items.IRON_BARS)
				.patternLine("B")
				.patternLine("B")
				.patternLine("B")
				.build(consumer);
	}

	private void buildingPath() {
		CondRecipe.shaped(InspirationsBuilding.path_brick, 6)
				.addCondition(BUILDING)
				.addCondition(ConfigEnabledCondition.PATH)
				.addCriterion("has_brick", hasItem(Items.BRICKS))
				.key('B', Items.BRICKS)
				.patternLine("BB")
				.build(consumer);

		CondRecipe.shaped(InspirationsBuilding.path_rock, 6)
				.addCondition(BUILDING)
				.addCondition(ConfigEnabledCondition.PATH)
				.addCriterion("has_cobble", hasItem(Tags.Items.COBBLESTONE))
				.key('C', Tags.Items.COBBLESTONE)
				.patternLine("CC")
				.build(consumer);

		CondRecipe.shaped(InspirationsBuilding.path_round, 6)
				.addCondition(BUILDING)
				.addCondition(ConfigEnabledCondition.PATH)
				.addCriterion("has_stone", hasItem(Tags.Items.STONE))
				.key('S', Tags.Items.STONE)
				.patternLine(" S ")
				.patternLine("S S")
				.patternLine(" S ")
				.build(consumer);

		CondRecipe.shaped(InspirationsBuilding.path_tile, 6)
				.addCondition(BUILDING)
				.addCondition(ConfigEnabledCondition.PATH)
				.addCriterion("has_stone", hasItem(Items.STONE_BRICKS))
				.key('C', Items.STONE_BRICKS)
				.patternLine("CC")
				.build(consumer);
	}

	private void buildingMulch(Block block, ITag<Item> dye) {
		CondRecipe.shapeless(block)
				.addCondition(BUILDING)
				.addCondition(ConfigEnabledCondition.MULCH)
				.addCriterion("has_mulch", hasItem(InspirationsBuilding.plainMulch))
				.addIngredient(InspirationsBuilding.plainMulch)
				.addIngredient(dye)
				.build(consumer);
	}

	private void buildingBookshelf() {
		String group = Util.resource("bookshelf");

		CondRecipe.shaped(InspirationsBuilding.shelf_normal, 2)
				.textureSource(ItemTags.WOODEN_SLABS)
				.addCondition(BUILDING)
				.addCondition(ConfigEnabledCondition.BOOKSHELF)
				.setGroup(group)
				.addCriterion("has_slabs", hasItem(ItemTags.WOODEN_SLABS))
				.addCriterion("has_book", hasItem(Items.BOOK))
				.key('S', ItemTags.WOODEN_SLABS)
				.patternLine("SSS")
				.patternLine(" S ")
				.patternLine("SSS")
				.build(consumer);

		CondRecipe.shaped(InspirationsBuilding.shelf_ancient, 2)
				.textureSource(ItemTags.WOODEN_SLABS)
				.addCondition(BUILDING)
				.addCondition(ConfigEnabledCondition.BOOKSHELF)
				.setGroup(group)
				.addCriterion("has_slabs", hasItem(ItemTags.WOODEN_SLABS))
				.addCriterion("has_book", hasItem(Items.BOOK))
				.key('S', ItemTags.WOODEN_SLABS)
				.key('P', Items.PAPER)
				.patternLine("SSS")
				.patternLine("PPP")
				.patternLine("SSS")
				.build(consumer);

		CondRecipe.shaped(InspirationsBuilding.shelf_tomes, 2)
				.textureSource(ItemTags.WOODEN_SLABS)
				.addCondition(BUILDING)
				.addCondition(ConfigEnabledCondition.BOOKSHELF)
				.setGroup(group)
				.addCriterion("has_slabs", hasItem(ItemTags.WOODEN_SLABS))
				.addCriterion("has_book", hasItem(Items.BOOK))
				.key('S', ItemTags.WOODEN_SLABS)
				.key('B', Items.BOOK)
				.patternLine("SSS")
				.patternLine(" B ")
				.patternLine("SSS")
				.build(consumer);

		// Allow any order for these.
		for (String dyeRow: new String[]{"RGB", "RBG", "GRB"}) {
			CondRecipe.shaped(InspirationsBuilding.shelf_rainbow, 2)
					.textureSource(ItemTags.WOODEN_SLABS)
					.addCondition(BUILDING)
					.addCondition(ConfigEnabledCondition.BOOKSHELF)
					.setGroup(group)
					.addCriterion("has_slabs", hasItem(ItemTags.WOODEN_SLABS))
					.addCriterion("has_book", hasItem(Items.BOOK))
					.key('S', ItemTags.WOODEN_SLABS)
					.key('R', Tags.Items.DYES_RED)
					.key('G', Tags.Items.DYES_GREEN)
					.key('B', Tags.Items.DYES_BLUE)
					.patternLine("SSS")
					.patternLine(dyeRow)
					.patternLine("SSS")
					.build(consumer, "rainbow_bookshelf_" + dyeRow.toLowerCase());
		}
	}

	private void buildingColoredBooks() {
		String group = Util.resource("colored_book");

		for(DyeColor color: DyeColor.values()) {
			CondRecipe.shapeless(InspirationsBuilding.coloredBooks[color.getId()])
					.addCondition(BUILDING)
					.addCondition(ConfigEnabledCondition.COLORED_BOOKS)
					.addCriterion("has_bookshelf", hasItem(InspirationsTags.Items.BOOKSHELVES))
					.setGroup(group)
					.addIngredient(Items.BOOK)
					.addIngredient(Util.getDyeTag(color))
					.build(consumer);
		}
	}

	// Register a flower -> one dye recipe.
	private void buildingFlowerDye(IItemProvider flower, Item dye) {
		CondRecipe.shapeless(dye)
				.addCondition(BUILDING)
				.addCondition(ConfigEnabledCondition.FLOWERS)
				.addCriterion("has_flower", hasItem(flower))
				.setGroup(Objects.requireNonNull(dye.getRegistryName()).toString())
				.addIngredient(flower)
				.build(consumer, "flower/" + dye.getRegistryName().getPath());
	}
}
