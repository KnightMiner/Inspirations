package knightminer.inspirations.utility.datagen;

import knightminer.inspirations.building.InspirationsBuilding;
import knightminer.inspirations.common.data.ConfigEnabledCondition;
import knightminer.inspirations.common.data.PulseLoadedCondition;
import knightminer.inspirations.common.datagen.CondRecipe;
import knightminer.inspirations.library.InspirationsTags;
import knightminer.inspirations.library.Util;
import knightminer.inspirations.utility.InspirationsUtility;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.CarpetBlock;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.RecipeProvider;
import net.minecraft.item.DyeColor;
import net.minecraft.item.Items;
import net.minecraft.tags.ItemTags;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.IConditionBuilder;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

public class UtilityRecipeProvider extends RecipeProvider implements IConditionBuilder {
	ICondition BUILDING = new PulseLoadedCondition(InspirationsBuilding.pulseID);
	ICondition UTILITY = new PulseLoadedCondition(InspirationsUtility.pulseID);

	public UtilityRecipeProvider(DataGenerator gen) {
		super(gen);
	}

	@Nonnull
	@Override
	public String getName() {
		return "Inspirations Recipes - Utility";
	}

	@Override
	protected void registerRecipes(@Nonnull Consumer<IFinishedRecipe> consumer) {
		CondRecipe.shaped(InspirationsUtility.collector)
				.addCondition(UTILITY)
				.addCondition(ConfigEnabledCondition.COLLECTOR)
				.addCriterion("has_rod", hasItem(Items.FISHING_ROD))
				.key('C', Tags.Items.COBBLESTONE)
				.key('R', Tags.Items.DUSTS_REDSTONE)
				.key('F', Items.FISHING_ROD)
				.patternLine("CCC")
				.patternLine("CFC")
				.patternLine("CRC")
				.build(consumer);

		CondRecipe.shaped(InspirationsUtility.pipeItem, 4)
				.addCondition(UTILITY)
				.addCondition(ConfigEnabledCondition.PIPE)
				.addCriterion("has_hopper", hasItem(Items.HOPPER))
				.key('I', Tags.Items.INGOTS_IRON)
				.key('P', ItemTags.PLANKS)
				.patternLine("IPI")
				.build(consumer);

		CondRecipe.shapeless(InspirationsBuilding.redstoneBook)
				.addCondition(UTILITY)
				.addCondition(BUILDING)
				.addCondition(ConfigEnabledCondition.REDSTONE_BOOK)
				.addCriterion("has_bookshelf", hasItem(InspirationsTags.Items.BOOKSHELVES))
				.addIngredient(Tags.Items.LEATHER)
				.addIngredient(Items.PAPER).addIngredient(Items.PAPER)
				.addIngredient(Tags.Items.DUSTS_REDSTONE)
				.build(consumer);

		CondRecipe.shaped(InspirationsUtility.torchLeverItem)
				.addCondition(UTILITY)
				.addCondition(ConfigEnabledCondition.TORCH_LEVER)
				.addCriterion("has_lever", hasItem(Items.LEVER))
				.addCriterion("has_torch", hasItem(Items.TORCH))
				.key('S', Tags.Items.COBBLESTONE)
				.key('T', Items.TORCH)
				.patternLine("T")
				.patternLine("S")
				.build(consumer);

		// All the trapdoors.
		carpetedTrapdoor(consumer, Blocks.WHITE_CARPET);
		carpetedTrapdoor(consumer, Blocks.ORANGE_CARPET);
		carpetedTrapdoor(consumer, Blocks.MAGENTA_CARPET);
		carpetedTrapdoor(consumer, Blocks.LIGHT_BLUE_CARPET);
		carpetedTrapdoor(consumer, Blocks.YELLOW_CARPET);
		carpetedTrapdoor(consumer, Blocks.LIME_CARPET);
		carpetedTrapdoor(consumer, Blocks.PINK_CARPET);
		carpetedTrapdoor(consumer, Blocks.GRAY_CARPET);
		carpetedTrapdoor(consumer, Blocks.LIGHT_GRAY_CARPET);
		carpetedTrapdoor(consumer, Blocks.CYAN_CARPET);
		carpetedTrapdoor(consumer, Blocks.PURPLE_CARPET);
		carpetedTrapdoor(consumer, Blocks.BLUE_CARPET);
		carpetedTrapdoor(consumer, Blocks.BROWN_CARPET);
		carpetedTrapdoor(consumer, Blocks.GREEN_CARPET);
		carpetedTrapdoor(consumer, Blocks.RED_CARPET);
		carpetedTrapdoor(consumer, Blocks.BLACK_CARPET);
	}

	private void carpetedTrapdoor(@Nonnull Consumer<IFinishedRecipe> consumer, Block carpet) {
		DyeColor color = ((CarpetBlock)carpet).getColor();
		CondRecipe.shaped(InspirationsUtility.carpetedTrapdoors[color.getId()])
				.addCondition(UTILITY)
				.addCondition(ConfigEnabledCondition.CARPETED_TRAPDOOR)
				.addCriterion("has_carpet", hasItem(InspirationsTags.Items.CARPETS))
				.setGroup(Util.resource("carpeted_trapdoor"))
				.key('C', carpet)
				.key('T', ItemTags.WOODEN_TRAPDOORS)
				.patternLine("C")
				.patternLine("T")
				.build(consumer);
	}
}
