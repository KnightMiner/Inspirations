package knightminer.inspirations.tweaks.datagen;

import knightminer.inspirations.common.data.ConfigEnabledCondition;
import knightminer.inspirations.common.datagen.IInspirationsRecipeBuilder;
import knightminer.inspirations.utility.InspirationsUtility;
import net.minecraft.advancements.critereon.EnchantmentPredicate;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.advancements.critereon.NbtPredicate;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.IConditionBuilder;

import java.util.Collections;
import java.util.function.Consumer;

public class TweaksRecipeProvider extends RecipeProvider implements IConditionBuilder, IInspirationsRecipeBuilder {
  private Consumer<FinishedRecipe> consumer;

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
  public Consumer<FinishedRecipe> getConsumer() {
    return consumer;
  }

  @Override
  protected void buildCraftingRecipes(Consumer<FinishedRecipe> consumer) {
    this.consumer = consumer;

    // Since these are mass-production recipes, show them after the user makes
    // several of the original.

    // stackable collector
    ShapedRecipeBuilder.shaped(InspirationsUtility.collector)
                       // builder is missing count, so use ItemPredicate constructor
                       .unlockedBy("many_collectors", inventoryTrigger(new ItemPredicate(
                           null, // tag
                           Collections.singleton(InspirationsUtility.collector.asItem()),
                           MinMaxBounds.Ints.atLeast(5),
                           MinMaxBounds.Ints.ANY,
                           EnchantmentPredicate.NONE,
                           EnchantmentPredicate.NONE,
                           null, // potion
                           NbtPredicate.ANY
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
                           Collections.singleton(Items.DISPENSER),
                           MinMaxBounds.Ints.atLeast(5),
                           MinMaxBounds.Ints.ANY,
                           EnchantmentPredicate.NONE,
                           EnchantmentPredicate.NONE,
                           null, // potion
                           NbtPredicate.ANY
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
