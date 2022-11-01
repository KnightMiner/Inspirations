package knightminer.inspirations.utility.datagen;

import knightminer.inspirations.building.InspirationsBuilding;
import knightminer.inspirations.common.data.ConfigEnabledCondition;
import knightminer.inspirations.common.datagen.IInspirationsRecipeBuilder;
import knightminer.inspirations.library.InspirationsTags;
import knightminer.inspirations.shared.InspirationsShared;
import knightminer.inspirations.utility.InspirationsUtility;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.crafting.conditions.IConditionBuilder;

import java.util.function.Consumer;

public class UtilityRecipeProvider extends RecipeProvider implements IConditionBuilder, IInspirationsRecipeBuilder {
  public UtilityRecipeProvider(DataGenerator gen) {
    super(gen);
  }

  @Override
  public String getName() {
    return "Inspirations Recipes - Utility";
  }

  @Override
  protected void buildCraftingRecipes(Consumer<FinishedRecipe> consumer) {
    // collector
    ShapedRecipeBuilder.shaped(InspirationsUtility.collector)
                       .unlockedBy("has_rod", has(Items.FISHING_ROD))
                       .define('C', Tags.Items.COBBLESTONE)
                       .define('R', Tags.Items.DUSTS_REDSTONE)
                       .define('F', Items.FISHING_ROD)
                       .pattern("CCC")
                       .pattern("CFC")
                       .pattern("CRC")
                       .save(withCondition(consumer, ConfigEnabledCondition.COLLECTOR), prefix(InspirationsUtility.collector, "utility/"));

    // pipe
    ShapedRecipeBuilder.shaped(InspirationsUtility.pipe, 4)
                       .unlockedBy("has_hopper", has(Items.HOPPER))
                       .define('I', Tags.Items.INGOTS_IRON)
                       .define('P', ItemTags.PLANKS)
                       .pattern("IPI")
                       .save(withCondition(consumer, ConfigEnabledCondition.PIPE), prefix(InspirationsUtility.pipe, "utility/"));

    // redstone book
    ShapelessRecipeBuilder.shapeless(InspirationsBuilding.redstoneBook)
                          .unlockedBy("has_bookshelf", has(InspirationsTags.Items.BOOKSHELVES))
                          .requires(Tags.Items.LEATHER)
                          .requires(Items.PAPER).requires(Items.PAPER)
                          .requires(Tags.Items.DUSTS_REDSTONE)
                          .save(withCondition(consumer, ConfigEnabledCondition.BOOKSHELF, ConfigEnabledCondition.REDSTONE_BOOK), prefix(InspirationsBuilding.redstoneBook, "utility/"));

    // torch lever
    ShapedRecipeBuilder.shaped(InspirationsUtility.torchLeverItem)
            .unlockedBy("has_torch", has(Items.TORCH))
            .unlockedBy("has_lever", has(Items.LEVER))
            .define('S', Tags.Items.COBBLESTONE)
            .define('T', Items.TORCH)
            .pattern("T")
            .pattern("S")
            .save(withCondition(consumer, ConfigEnabledCondition.TORCH_LEVER), prefix(InspirationsUtility.torchLeverItem, "utility/"));

    // soul torch lever
    ShapedRecipeBuilder.shaped(InspirationsUtility.soulLeverItem)
            .unlockedBy("has_soul_torch", has(Items.SOUL_TORCH))
            .unlockedBy("has_lever", has(Items.LEVER))
            .define('S', Tags.Items.COBBLESTONE)
            .define('T', Items.SOUL_TORCH)
            .pattern("T")
            .pattern("S")
            .save(withCondition(consumer, ConfigEnabledCondition.TORCH_LEVER), prefix(InspirationsUtility.soulLeverItem, "utility/"));

    // carpeted trapdoor.
    Consumer<FinishedRecipe> trapdoorConfig = withCondition(consumer, ConfigEnabledCondition.CARPETED_TRAPDOOR);
    String carpetedGroup = modPrefix("carpeted_trapdoor");
    InspirationsUtility.carpetedTrapdoors.forEach((color, trapdoor) ->
                                                      ShapedRecipeBuilder.shaped(trapdoor)
                                                                         .unlockedBy("has_carpet", has(InspirationsTags.Items.CARPETS))
                                                                         .group(carpetedGroup)
                                                                         .define('C', InspirationsShared.VANILLA_CARPETS.get(color))
                                                                         .define('T', ItemTags.WOODEN_TRAPDOORS)
                                                                         .pattern("C")
                                                                         .pattern("T")
                                                                         .save(trapdoorConfig, modResource("utility/carpeted_trapdoor/" + color.getSerializedName()))
                                                 );
  }
}
