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
  protected void buildShapelessRecipes(Consumer<IFinishedRecipe> consumer) {
    this.consumer = consumer;

    // Since these are mass-production recipes, show them after the user makes
    // several of the original.

    // stackable collector
    ShapedRecipeBuilder.shaped(InspirationsUtility.collector)
                       // builder is missing count, so use ItemPredicate constructor
                       .unlockedBy("many_collectors", inventoryTrigger(new ItemPredicate(
                           null, // tag
                           InspirationsUtility.collector.asItem(),
                           MinMaxBounds.IntBound.atLeast(5),
                           MinMaxBounds.IntBound.ANY,
                           EnchantmentPredicate.NONE,
                           EnchantmentPredicate.NONE,
                           null, // potion
                           NBTPredicate.ANY
                       )))
                       .define('T', Tags.Items.RODS_WOODEN)
                       .define('S', Tags.Items.STRING)
                       .define('D', Items.DROPPER)
                       .pattern("  T")
                       .pattern(" TS")
                       .pattern("TDS")
                       .save(
                           withCondition(ConfigEnabledCondition.MODULE_UTILITY, ConfigEnabledCondition.COLLECTOR, ConfigEnabledCondition.UNSTACKABLE_ALTS),
                           resource("tweaks/collector_stackable")
                             );

    // stackable dispenser
    ShapedRecipeBuilder.shaped(Items.DISPENSER)
                       // builder is missing count, so use ItemPredicate constructor
                       .unlockedBy("many_collectors", inventoryTrigger(new ItemPredicate(
                           null, // tag
                           Items.DISPENSER,
                           MinMaxBounds.IntBound.atLeast(5),
                           MinMaxBounds.IntBound.ANY,
                           EnchantmentPredicate.NONE,
                           EnchantmentPredicate.NONE,
                           null, // potion
                           NBTPredicate.ANY
                       )))
                       .define('T', Tags.Items.RODS_WOODEN)
                       .define('S', Tags.Items.STRING)
                       .define('D', Items.DROPPER)
                       .pattern(" TS")
                       .pattern("TDS")
                       .pattern(" TS")
                       .save(withCondition(ConfigEnabledCondition.UNSTACKABLE_ALTS), resource("tweaks/dispenser_stackable"));
  }
}
