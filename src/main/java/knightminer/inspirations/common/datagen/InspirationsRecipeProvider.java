package knightminer.inspirations.common.datagen;

import com.google.common.collect.ImmutableList;
import knightminer.inspirations.Inspirations;
import knightminer.inspirations.building.InspirationsBuilding;
import knightminer.inspirations.building.block.FlowerBlock;
import knightminer.inspirations.common.Config;
import knightminer.inspirations.common.data.ConfigEnabledCondition;
import knightminer.inspirations.common.data.PulseLoadedCondition;
import knightminer.inspirations.library.Util;
import knightminer.inspirations.tools.InspirationsTools;
import knightminer.inspirations.tweaks.InspirationsTweaks;
import knightminer.inspirations.utility.InspirationsUtility;
import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.RecipeProvider;
import net.minecraft.item.DyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.IItemProvider;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.IConditionBuilder;
import net.minecraftforge.common.crafting.conditions.NotCondition;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.function.Consumer;

public class InspirationsRecipeProvider extends RecipeProvider implements IConditionBuilder {
	ICondition BUILDING = new PulseLoadedCondition(InspirationsBuilding.pulseID);
	ICondition TWEAKS = new PulseLoadedCondition(InspirationsTweaks.pulseID);
	ICondition UTILITY = new PulseLoadedCondition(InspirationsUtility.pulseID);
	ICondition TOOLS = new PulseLoadedCondition(InspirationsTools.pulseID);

	// Prevent needing to pass this into every method.
	private Consumer<IFinishedRecipe> consumer = null;

	// Dyes in ordinal order.
	public static List<Tag<Item>> TAGS_DYE = ImmutableList.of(
			Tags.Items.DYES_ORANGE,
			Tags.Items.DYES_MAGENTA,
			Tags.Items.DYES_LIGHT_BLUE,
			Tags.Items.DYES_YELLOW,
			Tags.Items.DYES_LIME,
			Tags.Items.DYES_PINK,
			Tags.Items.DYES_GRAY,
			Tags.Items.DYES_LIGHT_GRAY,
			Tags.Items.DYES_CYAN,
			Tags.Items.DYES_PURPLE,
			Tags.Items.DYES_BLUE,
			Tags.Items.DYES_BROWN,
			Tags.Items.DYES_GREEN,
			Tags.Items.DYES_RED,
			Tags.Items.DYES_BLACK
	);

	public InspirationsRecipeProvider(DataGenerator gen) {
		super(gen);
	}

	@Override
	protected void registerRecipes(@Nonnull Consumer<IFinishedRecipe> consumer) {
		this.consumer = consumer;
		if (Inspirations.pulseManager.isPulseLoaded(InspirationsBuilding.pulseID)) {
			addBuilding();
		}

		this.consumer = null;
	}

	private void addBuilding() {
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

		buildingFlowerDye(InspirationsBuilding.flower_rose, Items.RED_DYE);
		buildingFlowerDye(InspirationsBuilding.flower_paeonia, Items.PINK_DYE);
		buildingFlowerDye(InspirationsBuilding.flower_syringa, Items.MAGENTA_DYE);
		buildingFlowerDye(InspirationsBuilding.flower_cyan, Items.CYAN_DYE);
		buildingCyanFlower();
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

	private void buildingMulch(Block block, Tag<Item> dye) {
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
		for (String dyeRow: new String[]{"RGB", "RBG", "GRB", "GBR", "BRG", "BGR"}) {
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

	// Register a flower -> one dye recipe.
	private void buildingFlowerDye(IItemProvider flower, Item dye) {
		CondRecipe.shapeless(dye)
				.addCondition(BUILDING)
				.addCondition(ConfigEnabledCondition.FLOWERS)
				.setGroup(dye.getRegistryName().toString())
				.addIngredient(flower)
				.build(consumer, "flower/" + dye.getRegistryName().getPath());
	}

	private void buildingCyanFlower() {
		CondRecipe.shapeless(InspirationsBuilding.flower_cyan)
				.addCondition(BUILDING)
				.addCondition(ConfigEnabledCondition.FLOWERS)
				.addCondition(not(ConfigEnabledCondition.CAULDRON_DYEING))
				.addIngredient(Tags.Items.DYES_CYAN)
				.addIngredient(InspirationsBuilding.flower_rose)
				.build(consumer, "flower/cyan_flower");
	}
}
