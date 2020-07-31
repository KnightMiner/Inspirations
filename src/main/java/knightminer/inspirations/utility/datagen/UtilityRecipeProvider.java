package knightminer.inspirations.utility.datagen;

import knightminer.inspirations.building.InspirationsBuilding;
import knightminer.inspirations.common.data.ConfigEnabledCondition;
import knightminer.inspirations.common.datagen.IRecipeBuilderUtils;
import knightminer.inspirations.library.InspirationsTags;
import knightminer.inspirations.shared.InspirationsShared;
import knightminer.inspirations.utility.InspirationsUtility;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.RecipeProvider;
import net.minecraft.data.ShapedRecipeBuilder;
import net.minecraft.data.ShapelessRecipeBuilder;
import net.minecraft.item.Items;
import net.minecraft.tags.ItemTags;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.IConditionBuilder;

import java.util.function.Consumer;

public class UtilityRecipeProvider extends RecipeProvider implements IConditionBuilder, IRecipeBuilderUtils {
  private Consumer<IFinishedRecipe> consumer;

  public UtilityRecipeProvider(DataGenerator gen) {
    super(gen);
  }

  @Override
  public String getName() {
    return "Inspirations Recipes - Utility";
  }

  @Override
  public ICondition baseCondition() {
    return ConfigEnabledCondition.MODULE_UTILITY;
  }

  @Override
  public Consumer<IFinishedRecipe> getConsumer() {
    return consumer;
  }

  @Override
  protected void registerRecipes(Consumer<IFinishedRecipe> consumer) {
    this.consumer = consumer;

    // collector
    ShapedRecipeBuilder.shapedRecipe(InspirationsUtility.collector)
                       .addCriterion("has_rod", hasItem(Items.FISHING_ROD))
                       .key('C', Tags.Items.COBBLESTONE)
                       .key('R', Tags.Items.DUSTS_REDSTONE)
                       .key('F', Items.FISHING_ROD)
                       .patternLine("CCC")
                       .patternLine("CFC")
                       .patternLine("CRC")
                       .build(withCondition(ConfigEnabledCondition.COLLECTOR), prefix(InspirationsUtility.collector, "utility/"));

    // pipe
    ShapedRecipeBuilder.shapedRecipe(InspirationsUtility.pipe, 4)
                       .addCriterion("has_hopper", hasItem(Items.HOPPER))
                       .key('I', Tags.Items.INGOTS_IRON)
                       .key('P', ItemTags.PLANKS)
                       .patternLine("IPI")
                       .build(withCondition(ConfigEnabledCondition.PIPE), prefix(InspirationsUtility.pipe, "utility/"));

    // redstone book
    ShapelessRecipeBuilder.shapelessRecipe(InspirationsBuilding.redstoneBook)
                          .addCriterion("has_bookshelf", hasItem(InspirationsTags.Items.BOOKSHELVES))
                          .addIngredient(Tags.Items.LEATHER)
                          .addIngredient(Items.PAPER).addIngredient(Items.PAPER)
                          .addIngredient(Tags.Items.DUSTS_REDSTONE)
                          .build(
                              withCondition(ConfigEnabledCondition.MODULE_BUILDING, ConfigEnabledCondition.REDSTONE_BOOK),
                              prefix(InspirationsBuilding.redstoneBook, "utility/")
                                );

    // torch lever
    ShapedRecipeBuilder.shapedRecipe(InspirationsUtility.torchLeverItem)
                       .addCriterion("has_torch", hasItem(Items.TORCH))
                       .addCriterion("has_lever", hasItem(Items.LEVER))
                       .key('S', Tags.Items.COBBLESTONE)
                       .key('T', Items.TORCH)
                       .patternLine("T")
                       .patternLine("S")
                       .build(withCondition(ConfigEnabledCondition.TORCH_LEVER), prefix(InspirationsUtility.torchLeverItem, "utility/"));

    // carpeted trapdoor.
    Consumer<IFinishedRecipe> trapdoorConfig = withCondition(ConfigEnabledCondition.CARPETED_TRAPDOOR);
    String carpetedGroup = resourceName("carpeted_trapdoor");
    InspirationsUtility.carpetedTrapdoors.forEach((color, trapdoor) ->
                                                      ShapedRecipeBuilder.shapedRecipe(trapdoor)
                                                                         .addCriterion("has_carpet", hasItem(InspirationsTags.Items.CARPETS))
                                                                         .setGroup(carpetedGroup)
                                                                         .key('C', InspirationsShared.VANILLA_CARPETS.get(color))
                                                                         .key('T', ItemTags.WOODEN_TRAPDOORS)
                                                                         .patternLine("C")
                                                                         .patternLine("T")
                                                                         .build(trapdoorConfig, resource("utility/carpeted_trapdoor/" + color.getString()))
                                                 );
  }
}
