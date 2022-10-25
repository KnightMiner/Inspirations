package knightminer.inspirations.building.datagen;

import knightminer.inspirations.building.InspirationsBuilding;
import knightminer.inspirations.building.block.type.FlowerType;
import knightminer.inspirations.building.block.type.MulchType;
import knightminer.inspirations.building.block.type.PathType;
import knightminer.inspirations.building.block.type.ShelfType;
import knightminer.inspirations.common.data.ConfigEnabledCondition;
import knightminer.inspirations.common.datagen.IInspirationsRecipeBuilder;
import knightminer.inspirations.library.InspirationsTags;
import net.minecraft.advancements.ICriterionInstance;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.RecipeProvider;
import net.minecraft.data.ShapedRecipeBuilder;
import net.minecraft.data.ShapelessRecipeBuilder;
import net.minecraft.data.SingleItemRecipeBuilder;
import net.minecraft.item.DyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.IItemProvider;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.IConditionBuilder;
import slimeknights.mantle.recipe.crafting.ShapedRetexturedRecipeBuilder;

import java.util.Locale;
import java.util.Objects;
import java.util.function.Consumer;

public class BuildingRecipeProvider extends RecipeProvider implements IConditionBuilder, IInspirationsRecipeBuilder {
  private Consumer<IFinishedRecipe> consumer;

  public BuildingRecipeProvider(DataGenerator gen) {
    super(gen);
  }

  @Override
  public String getName() {
    return "Inspirations Recipes - Building";
  }

  @Override
  public Consumer<IFinishedRecipe> getConsumer() {
    return consumer;
  }

  @Override
  public ICondition baseCondition() {
    return ConfigEnabledCondition.MODULE_BUILDING;
  }

  @Override
  protected void buildShapelessRecipes(Consumer<IFinishedRecipe> consumer) {
    // set consumer for the util
    this.consumer = consumer;

    // glass doors
    Consumer<IFinishedRecipe> glassDoorCondition = withCondition(ConfigEnabledCondition.GLASS_DOOR);
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
    Consumer<IFinishedRecipe> ropeCondition = withCondition(ConfigEnabledCondition.ROPE);
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
    Consumer<IFinishedRecipe> pathCondition = withCondition(ConfigEnabledCondition.PATH);
    addPath(pathCondition, PathType.BRICK, Ingredient.of(Items.BRICKS), has(Items.BRICKS));
    addPath(pathCondition, PathType.ROCK, Ingredient.of(Tags.Items.COBBLESTONE), has(Tags.Items.COBBLESTONE));
    addPath(pathCondition, PathType.ROUND, Ingredient.of(Items.STONE), has(Items.STONE));
    addPath(pathCondition, PathType.TILE, Ingredient.of(Items.STONE_BRICKS), has(Items.STONE_BRICKS));

    // mulch
    Consumer<IFinishedRecipe> mulchCondition = withCondition(ConfigEnabledCondition.MULCH);
    // make plain in stonecutter
    IItemProvider plainMulch = InspirationsBuilding.mulch.get(MulchType.PLAIN);
    SingleItemRecipeBuilder.stonecutting(Ingredient.of(ItemTags.PLANKS), plainMulch)
                           .unlocks("hasPlanks", has(ItemTags.PLANKS))
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
    Consumer<IFinishedRecipe> bookConditions = withCondition(ConfigEnabledCondition.COLORED_BOOKS);
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
    Consumer<IFinishedRecipe> flowerConditions = withCondition(ConfigEnabledCondition.FLOWERS);
    InspirationsBuilding.flower.forEach((type, flower) -> {
      Item dye = type.getDye();
      ShapelessRecipeBuilder.shapeless(dye)
                            .unlockedBy("has_flower", has(flower))
                            .group(Objects.requireNonNull(dye).toString())
                            .requires(flower)
                            .save(flowerConditions, resource("building/flower/" + Objects.requireNonNull(dye.getRegistryName()).getPath()));
    });
    // add temporary cyan dye crafting recipe
    Consumer<IFinishedRecipe> cyanFlowerConditions = withCondition(ConfigEnabledCondition.FLOWERS, not(ConfigEnabledCondition.CAULDRON_DYEING));
    IItemProvider rose = InspirationsBuilding.flower.get(FlowerType.ROSE);
    ShapelessRecipeBuilder.shapeless(InspirationsBuilding.flower.get(FlowerType.CYAN))
                          .unlockedBy("has_dye", has(Tags.Items.DYES_CYAN))
                          .unlockedBy("has_flower", has(rose))
                          .requires(Tags.Items.DYES_CYAN)
                          .requires(rose)
                          .save(cyanFlowerConditions, resource("building/flower/cyan_flower"));

    // bookshelves
    Consumer<IFinishedRecipe> bookshelfConditions = withCondition(ConfigEnabledCondition.BOOKSHELF);
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
          case ANCIENT:
            builder.define('P', Items.PAPER);
            break;
          case TOMES:
            builder.define('B', Items.BOOK);
            break;
          case RAINBOW:
            builder.define('R', Tags.Items.DYES_RED)
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
    Consumer<IFinishedRecipe> bushConditions = withCondition(ConfigEnabledCondition.ENLIGHTENED_BUSH);
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
  private void addPath(Consumer<IFinishedRecipe> consumer, PathType type, Ingredient ingredient, ICriterionInstance criteria) {
    // crafting
    IItemProvider path = InspirationsBuilding.path.get(type);
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
                           .unlocks("has_stone", criteria)
                           .save(consumer, resource("building/path/" + type.getSerializedName() + "_cutting"));
  }

  /**
   * Gets recipe variants for a shelf type
   * @param type Shelf type
   * @return Array of recipe variants
   */
  private static String[] getShelfVariants(ShelfType type) {
    switch (type) {
      case NORMAL:
      default:
        return new String[]{" S "};
      case ANCIENT:
        return new String[]{"PPP"};
      case TOMES:
        return new String[]{" B "};
      case RAINBOW:
        return new String[]{"RGB", "RBG", "GRB"};
    }
  }
}
