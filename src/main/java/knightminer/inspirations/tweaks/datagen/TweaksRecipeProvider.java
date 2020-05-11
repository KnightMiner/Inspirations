package knightminer.inspirations.tweaks.datagen;

import knightminer.inspirations.common.data.ConfigEnabledCondition;
import knightminer.inspirations.common.data.PulseLoadedCondition;
import knightminer.inspirations.common.datagen.CondRecipe;
import knightminer.inspirations.tweaks.InspirationsTweaks;
import knightminer.inspirations.utility.InspirationsUtility;
import net.minecraft.advancements.criterion.EnchantmentPredicate;
import net.minecraft.advancements.criterion.ItemPredicate;
import net.minecraft.advancements.criterion.MinMaxBounds;
import net.minecraft.advancements.criterion.NBTPredicate;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.RecipeProvider;
import net.minecraft.item.Items;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.IConditionBuilder;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

public class TweaksRecipeProvider extends RecipeProvider implements IConditionBuilder {
	ICondition TWEAKS = new PulseLoadedCondition(InspirationsTweaks.pulseID);
	ICondition UTILITY = new PulseLoadedCondition(InspirationsUtility.pulseID);

	public TweaksRecipeProvider(DataGenerator gen) {
		super(gen);
	}

	@Nonnull
	@Override
	public String getName() {
		return "Inspirations Recipes - Tweaks";
	}
	@Override
	protected void registerRecipes(@Nonnull Consumer<IFinishedRecipe> consumer) {
		// Since these are mass-production recipes, show them after the user makes
		// several of the original.
		CondRecipe.shaped(InspirationsUtility.collector)
				.addCondition(UTILITY)
				.addCondition(ConfigEnabledCondition.COLLECTOR)
				.addCondition(TWEAKS)
				.addCondition(ConfigEnabledCondition.UNSTACKABLE_ALTS)
				// builder is missing count, so use ItemPredicate constructor
				.addCriterion("many_collectors", hasItem(new ItemPredicate(
						null, // tag
						InspirationsUtility.collector.asItem(),
						MinMaxBounds.IntBound.atLeast(5),
						MinMaxBounds.IntBound.UNBOUNDED,
						EnchantmentPredicate.field_226534_b_,
						EnchantmentPredicate.field_226534_b_,
						null, // potion
						NBTPredicate.ANY
				)))
				.key('T', Tags.Items.RODS_WOODEN)
				.key('S', Tags.Items.STRING)
				.key('D', Items.DROPPER)
				.patternLine("  T")
				.patternLine(" TS")
				.patternLine("TDS")
				.build(consumer, "collector_stackable");

		CondRecipe.shaped(Items.DISPENSER)
				.addCondition(UTILITY)
				.addCondition(ConfigEnabledCondition.COLLECTOR)
				.addCondition(TWEAKS)
				.addCondition(ConfigEnabledCondition.UNSTACKABLE_ALTS)
				// builder is missing count, so use ItemPredicate constructor
				.addCriterion("many_collectors", hasItem(new ItemPredicate(
						null, // tag
						Items.DISPENSER,
						MinMaxBounds.IntBound.atLeast(5),
						MinMaxBounds.IntBound.UNBOUNDED,
						EnchantmentPredicate.field_226534_b_,
						EnchantmentPredicate.field_226534_b_,
						null, // potion
						NBTPredicate.ANY
				)))
				.key('T', Tags.Items.RODS_WOODEN)
				.key('S', Tags.Items.STRING)
				.key('D', Items.DROPPER)
				.patternLine(" DS")
				.patternLine("TDS")
				.patternLine(" DS")
				.build(consumer, "dispenser_stackable");
	}
}
