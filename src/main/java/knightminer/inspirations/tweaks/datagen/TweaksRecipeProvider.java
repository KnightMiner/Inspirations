package knightminer.inspirations.tweaks.datagen;

import knightminer.inspirations.common.data.ConfigEnabledCondition;
import knightminer.inspirations.common.datagen.IInspirationsRecipeBuilder;
import knightminer.inspirations.utility.InspirationsUtility;
import net.minecraft.advancements.criterion.EnchantmentPredicate;
import net.minecraft.advancements.criterion.ItemPredicate;
import net.minecraft.advancements.criterion.MinMaxBounds;
import net.minecraft.advancements.criterion.NBTPredicate;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.RecipeProvider;
import net.minecraft.data.ShapedRecipeBuilder;
import net.minecraft.item.Items;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.IConditionBuilder;

import java.util.function.Consumer;

public class TweaksRecipeProvider extends RecipeProvider implements IConditionBuilder, IInspirationsRecipeBuilder {
  private Consumer<IFinishedRecipe> consumer;

  public TweaksRecipeProvider(DataGenerator gen) {
    super(gen);
  }

  @Override
  public String getName() {
    return "Inspirations Recipes - Tweaks";
  }

  @Override
  public ICondition baseCondition() {
    return ConfigEnabledCondition.MODULE_TWEAKS;
  }

  @Override
  public Consumer<IFinishedRecipe> getConsumer() {
    return consumer;
  }

  @Override
  protected void registerRecipes(Consumer<IFinishedRecipe> consumer) {
    this.consumer = consumer;

    // Since these are mass-production recipes, show them after the user makes
    // several of the original.

    // stackable collector
    ShapedRecipeBuilder.shapedRecipe(InspirationsUtility.collector)
                       // builder is missing count, so use ItemPredicate constructor
                       .addCriterion("many_collectors", hasItem(new ItemPredicate(
                           null, // tag
                           InspirationsUtility.collector.asItem(),
                           MinMaxBounds.IntBound.atLeast(5),
                           MinMaxBounds.IntBound.UNBOUNDED,
                           EnchantmentPredicate.enchantments,
                           EnchantmentPredicate.enchantments,
                           null, // potion
                           NBTPredicate.ANY
                       )))
                       .key('T', Tags.Items.RODS_WOODEN)
                       .key('S', Tags.Items.STRING)
                       .key('D', Items.DROPPER)
                       .patternLine("  T")
                       .patternLine(" TS")
                       .patternLine("TDS")
                       .build(
                           withCondition(ConfigEnabledCondition.MODULE_UTILITY, ConfigEnabledCondition.COLLECTOR, ConfigEnabledCondition.UNSTACKABLE_ALTS),
                           resource("tweaks/collector_stackable")
                             );

    // stackable dispenser
    ShapedRecipeBuilder.shapedRecipe(Items.DISPENSER)
                       // builder is missing count, so use ItemPredicate constructor
                       .addCriterion("many_collectors", hasItem(new ItemPredicate(
                           null, // tag
                           Items.DISPENSER,
                           MinMaxBounds.IntBound.atLeast(5),
                           MinMaxBounds.IntBound.UNBOUNDED,
                           EnchantmentPredicate.enchantments,
                           EnchantmentPredicate.enchantments,
                           null, // potion
                           NBTPredicate.ANY
                       )))
                       .key('T', Tags.Items.RODS_WOODEN)
                       .key('S', Tags.Items.STRING)
                       .key('D', Items.DROPPER)
                       .patternLine(" TS")
                       .patternLine("TDS")
                       .patternLine(" TS")
                       .build(withCondition(ConfigEnabledCondition.UNSTACKABLE_ALTS), resource("tweaks/dispenser_stackable"));
  }
}
