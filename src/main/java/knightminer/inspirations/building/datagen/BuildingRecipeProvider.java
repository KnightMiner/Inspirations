package knightminer.inspirations.building.datagen;

import knightminer.inspirations.building.InspirationsBuilding;
import knightminer.inspirations.building.block.type.FlowerType;
import knightminer.inspirations.building.block.type.MulchType;
import knightminer.inspirations.building.block.type.PathType;
import knightminer.inspirations.building.block.type.ShelfType;
import knightminer.inspirations.common.data.ConfigEnabledCondition;
import knightminer.inspirations.common.datagen.IInspirationsRecipeBuilder;
import knightminer.inspirations.library.InspirationsTags;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.data.recipes.SingleItemRecipeBuilder;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.IConditionBuilder;
import slimeknights.mantle.recipe.crafting.ShapedRetexturedRecipeBuilder;

import java.util.Locale;
import java.util.Objects;
import java.util.function.Consumer;

public class BuildingRecipeProvider extends RecipeProvider implements IConditionBuilder, IInspirationsRecipeBuilder {
  private Consumer<FinishedRecipe> consumer;

  public BuildingRecipeProvider(DataGenerator gen) {
    super(gen);
  }

  @Override
  public String getName() {
    return "Inspirations Recipes - Building";
  }

  @Override
  public Consumer<FinishedRecipe> getConsumer() {
    return consumer;
  }

  @Override
  public ICondition baseCondition() {
    return ConfigEnabledCondition.MODULE_BUILDING;
  }

  @Override
  protected void buildCraftingRecipes(Consumer<FinishedRecipe> consumer) {
    // set consumer for the util
    this.consumer = consumer;

    // glass doors
    Consumer<FinishedRecipe> glassDoorCondition = withCondition(ConfigEnabledCondition.GLASS_DOOR);
    ShapedRecipeBuilder.shaped(InspirationsBuilding.glassDoor)
                       .unlockedBy("has_glass", has(Items.GLASS_PANE))
                       .define('G', Items.GLASS_PANE)
                       .pattern("GG")
                       .pattern("GG")
                       .pattern("GG")
                       .save(glassDoorCondition, prefix(InspirationsBuilding.glassDoor, "building/"));
    ShapedRecipeBuilder.shaped(InspirationsBuilding.glassTrapdoor, 2)
                       .unlockedBy("has_glass", has(Items.GLASS_PANE))
                       .define('G', Items.GLASS_PANE)
                       .pattern("GGG")
                       .pattern("GGG")
                       .save(glassDoorCondition, prefix(InspirationsBuilding.glassTrapdoor, "building/"));

    // rope
    Consumer<FinishedRecipe> ropeCondition = withCondition(ConfigEnabledCondition.ROPE);
    ShapedRecipeBuilder.shaped(InspirationsBuilding.rope, 3)
                       .unlockedBy("has_string", has(Tags.Items.STRING))
                       .define('S', Items.STRING)
                       .pattern("SS")
                       .pattern("SS")
                       .pattern("SS")
                       .save(ropeCondition, prefix(InspirationsBuilding.rope, "building/"));
    ShapedRecipeBuilder.shaped(InspirationsBuilding.vine, 3)
                       .unlockedBy("has_vines", has(Items.VINE))
                       .define('V', Items.VINE)
                       .pattern("V")
                       .pattern("V")
                       .pattern("V")
                       .save(ropeCondition, prefix(InspirationsBuilding.vine, "building/"));

    // path
    Consumer<FinishedRecipe> pathCondition = withCondition(ConfigEnabledCondition.PATH);
    addPath(pathCondition, PathType.BRICK, Ingredient.of(Items.BRICKS), has(Items.BRICKS));
    addPath(pathCondition, PathType.ROCK, Ingredient.of(Tags.Items.COBBLESTONE), has(Tags.Items.COBBLESTONE));
    addPath(pathCondition, PathType.ROUND, Ingredient.of(Items.STONE), has(Items.STONE));
    addPath(pathCondition, PathType.TILE, Ingredient.of(Items.STONE_BRICKS), has(Items.STONE_BRICKS));

    // mulch
    Consumer<FinishedRecipe> mulchCondition = withCondition(ConfigEnabledCondition.MULCH);
    // make plain in stonecutter
    ItemLike plainMulch = InspirationsBuilding.mulch.get(MulchType.PLAIN);
    SingleItemRecipeBuilder.stonecutting(Ingredient.of(ItemTags.PLANKS), plainMulch)
                           .unlockedBy("hasPlanks", has(ItemTags.PLANKS))
                           .save(mulchCondition, resource("building/mulch/" + MulchType.PLAIN.getSerializedName()));
    // dye for other colors
    InspirationsBuilding.mulch.forEach((type, mulch) -> {
      DyeColor dye = type.getDye();
      if (dye != null) {
        ShapelessRecipeBuilder.shapeless(mulch)
                              .unlockedBy("has_mulch", has(plainMulch))
                              .requires(plainMulch)
                              .requires(dye.getTag())
                              .save(mulchCondition, resource("building/mulch/" + type.getSerializedName()));
      }
    });

    // colored books
    Consumer<FinishedRecipe> bookConditions = withCondition(ConfigEnabledCondition.COLORED_BOOKS);
    String bookGroup = resourceName("colored_book");
    InspirationsBuilding.coloredBooks.forEach((color, book) ->
      ShapelessRecipeBuilder.shapeless(book)
                            .unlockedBy("has_bookshelf", has(InspirationsTags.Items.BOOKSHELVES))
                            .group(bookGroup)
                            .requires(Items.BOOK)
                            .requires(color.getTag())
                            .save(bookConditions, resource("building/books/" + color.getSerializedName()))
    );

    // flowers
    // add dye crafting recipes
    Consumer<FinishedRecipe> flowerConditions = withCondition(ConfigEnabledCondition.FLOWERS);
    InspirationsBuilding.flower.forEach((type, flower) -> {
      Item dye = type.getDye();
      ShapelessRecipeBuilder.shapeless(dye)
                            .unlockedBy("has_flower", has(flower))
                            .group(Objects.requireNonNull(dye).toString())
                            .requires(flower)
                            .save(flowerConditions, resource("building/flower/" + Objects.requireNonNull(dye.getRegistryName()).getPath()));
    });
    // add temporary cyan dye crafting recipe
    Consumer<FinishedRecipe> cyanFlowerConditions = withCondition(ConfigEnabledCondition.FLOWERS, not(ConfigEnabledCondition.CAULDRON_DYEING));
    ItemLike rose = InspirationsBuilding.flower.get(FlowerType.ROSE);
    ShapelessRecipeBuilder.shapeless(InspirationsBuilding.flower.get(FlowerType.CYAN))
                          .unlockedBy("has_dye", has(Tags.Items.DYES_CYAN))
                          .unlockedBy("has_flower", has(rose))
                          .requires(Tags.Items.DYES_CYAN)
                          .requires(rose)
                          .save(cyanFlowerConditions, resource("building/flower/cyan_flower"));

    // bookshelves
    Consumer<FinishedRecipe> bookshelfConditions = withCondition(ConfigEnabledCondition.BOOKSHELF);
    String shelfGroup = resourceName("bookshelf");
    InspirationsBuilding.shelf.forEach((type, shelf) -> {
      String[] variants = getShelfVariants(type);
      for (String variant : variants) {
        ShapedRecipeBuilder builder =
            ShapedRecipeBuilder.shaped(shelf, 2)
                               .group(shelfGroup)
                               .unlockedBy("has_slabs", has(ItemTags.WOODEN_SLABS))
                               .unlockedBy("has_book", has(Items.BOOK))
                               .define('S', ItemTags.WOODEN_SLABS)
                               .pattern("SSS")
                               .pattern(variant)
                               .pattern("SSS");
        // add extra items for the variant
        switch (type) {
          case ANCIENT -> builder.define('P', Items.PAPER);
          case TOMES -> builder.define('B', Items.BOOK);
          case RAINBOW -> builder.define('R', Tags.Items.DYES_RED)
                                 .define('G', Tags.Items.DYES_GREEN)
                                 .define('B', Tags.Items.DYES_BLUE);
        }
        // add texture and build
        String suffix = variants.length > 1 ? "_" + variant.toLowerCase(Locale.US) : "";
        ShapedRetexturedRecipeBuilder.fromShaped(builder)
                                     .setSource(ItemTags.WOODEN_SLABS)
                                     .setMatchAll()
                                     .build(bookshelfConditions, resource("building/bookshelves/" + type.getSerializedName() + suffix));
      }
    });

    // enlightened bushes
    Consumer<FinishedRecipe> bushConditions = withCondition(ConfigEnabledCondition.ENLIGHTENED_BUSH);
    String bushGroup = resourceName("enlightened_bush");
    InspirationsBuilding.enlightenedBush.forEach((type, bush) -> {
      ShapedRecipeBuilder builder =
          ShapedRecipeBuilder.shaped(bush)
                             .group(bushGroup)
                             .unlockedBy("has_leaves", has(ItemTags.LEAVES))
                             .unlockedBy("has_glowstone", has(Tags.Items.DUSTS_GLOWSTONE))
                             .define('L', ItemTags.LEAVES)
                             .define('G', Tags.Items.DUSTS_GLOWSTONE);
      // white uses no dye
      DyeColor dye = type.getDye();
      if (dye != null) {
        // First line - dye above the middle.
        builder = builder.define('D', dye.getTag()).pattern(" D ");
      }
      builder.pattern("GLG");
      // add texture and build
      ShapedRetexturedRecipeBuilder.fromShaped(builder)
                                   .setSource(ItemTags.LEAVES)
                                   .build(bushConditions, resource("building/enlightened_bush/" + type.getSerializedName()));
    });
  }

  /**
   * Adds recipes for a path type.
   * @param consumer   Recipe consumer
   * @param type       Path type
   * @param ingredient Ingredient for crafting
   * @param criteria   Criteria instance
   */
  private void addPath(Consumer<FinishedRecipe> consumer, PathType type, Ingredient ingredient, CriterionTriggerInstance criteria) {
    // crafting
    ItemLike path = InspirationsBuilding.path.get(type);
    // skip round path crafting, conflict with pressure plates
    if (type != PathType.ROUND) {
      ShapedRecipeBuilder.shaped(path, 6)
                         .unlockedBy("has_item", criteria)
                         .define('C', ingredient)
                         .pattern("CC")
                         .save(consumer, resource("building/path/" + type.getSerializedName() + "_crafting"));
    }
    // stonecutting
    SingleItemRecipeBuilder.stonecutting(ingredient, path, 6)
                           .unlockedBy("has_stone", criteria)
                           .save(consumer, resource("building/path/" + type.getSerializedName() + "_cutting"));
  }

  /**
   * Gets recipe variants for a shelf type
   * @param type Shelf type
   * @return Array of recipe variants
   */
  private static String[] getShelfVariants(ShelfType type) {
    return switch (type) {
      default -> new String[]{" S "};
      case ANCIENT -> new String[]{"PPP"};
      case TOMES -> new String[]{" B "};
      case RAINBOW -> new String[]{"RGB", "RBG", "GRB"};
    };
  }
}
