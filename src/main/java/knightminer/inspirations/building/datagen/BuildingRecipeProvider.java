package knightminer.inspirations.building.datagen;

import knightminer.inspirations.building.InspirationsBuilding;
import knightminer.inspirations.building.block.type.FlowerType;
import knightminer.inspirations.building.block.type.MulchType;
import knightminer.inspirations.building.block.type.PathType;
import knightminer.inspirations.building.block.type.ShelfType;
import knightminer.inspirations.common.data.ConfigEnabledCondition;
import knightminer.inspirations.common.datagen.IRecipeBuilderUtils;
import knightminer.inspirations.library.InspirationsTags;
import knightminer.inspirations.library.recipe.crafting.TextureRecipeBuilder;
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

import java.util.Locale;
import java.util.Objects;
import java.util.function.Consumer;

public class BuildingRecipeProvider extends RecipeProvider implements IConditionBuilder, IRecipeBuilderUtils {
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
  protected void registerRecipes(Consumer<IFinishedRecipe> consumer) {
    // set consumer for the util
    this.consumer = consumer;

    // glass doors
    Consumer<IFinishedRecipe> glassDoorCondition = withCondition(ConfigEnabledCondition.GLASS_DOOR);
    ShapedRecipeBuilder.shapedRecipe(InspirationsBuilding.glassDoor)
                       .addCriterion("has_glass", hasItem(Items.GLASS_PANE))
                       .key('G', Items.GLASS_PANE)
                       .patternLine("GG")
                       .patternLine("GG")
                       .patternLine("GG")
                       .build(glassDoorCondition, prefix(InspirationsBuilding.glassDoor, "building/"));
    ShapedRecipeBuilder.shapedRecipe(InspirationsBuilding.glassTrapdoor, 2)
                       .addCriterion("has_glass", hasItem(Items.GLASS_PANE))
                       .key('G', Items.GLASS_PANE)
                       .patternLine("GGG")
                       .patternLine("GGG")
                       .build(glassDoorCondition, prefix(InspirationsBuilding.glassTrapdoor, "building/"));

    // rope
    Consumer<IFinishedRecipe> ropeCondition = withCondition(ConfigEnabledCondition.ROPE);
    ShapedRecipeBuilder.shapedRecipe(InspirationsBuilding.rope, 3)
              .addCriterion("has_string", hasItem(Tags.Items.STRING))
              .key('S', Items.STRING)
              .patternLine("SS")
              .patternLine("SS")
              .patternLine("SS")
              .build(ropeCondition, prefix(InspirationsBuilding.rope, "building/"));
    ShapedRecipeBuilder.shapedRecipe(InspirationsBuilding.vine, 3)
              .addCriterion("has_vines", hasItem(Items.VINE))
              .key('V', Items.VINE)
              .patternLine("V")
              .patternLine("V")
              .patternLine("V")
              .build(ropeCondition, prefix(InspirationsBuilding.vine, "building/"));
    ShapedRecipeBuilder.shapedRecipe(InspirationsBuilding.chain, 6)
              .addCriterion("has_bars", hasItem(Items.IRON_BARS))
              .key('B', Items.IRON_BARS)
              .patternLine("B")
              .patternLine("B")
              .patternLine("B")
              .build(ropeCondition, prefix(InspirationsBuilding.chain, "building/"));

    // path
    Consumer<IFinishedRecipe> pathCondition = withCondition(ConfigEnabledCondition.PATH);
    addPath(pathCondition, PathType.BRICK, Ingredient.fromItems(Items.BRICKS), hasItem(Items.BRICKS));
    addPath(pathCondition, PathType.ROCK, Ingredient.fromTag(Tags.Items.COBBLESTONE), hasItem(Tags.Items.COBBLESTONE));
    addPath(pathCondition, PathType.ROUND, Ingredient.fromItems(Items.STONE), hasItem(Items.STONE));
    addPath(pathCondition, PathType.TILE, Ingredient.fromItems(Items.STONE_BRICKS), hasItem(Items.STONE_BRICKS));

    // mulch
    Consumer<IFinishedRecipe> mulchCondition = withCondition(ConfigEnabledCondition.MULCH);
    // make plain in stonecutter
    IItemProvider plainMulch = InspirationsBuilding.mulch.get(MulchType.PLAIN);
    SingleItemRecipeBuilder.stonecuttingRecipe(Ingredient.fromTag(ItemTags.PLANKS), plainMulch)
                           .addCriterion("hasPlanks", hasItem(ItemTags.PLANKS))
                           .build(mulchCondition, resource("building/mulch/" + MulchType.PLAIN.getString()));
    // dye for other colors
    InspirationsBuilding.mulch.forEach((type, mulch) -> {
      DyeColor dye = type.getDye();
      if (dye != null) {
        ShapelessRecipeBuilder.shapelessRecipe(mulch)
                              .addCriterion("has_mulch", hasItem(plainMulch))
                              .addIngredient(plainMulch)
                              .addIngredient(dye.getTag())
                              .build(mulchCondition, resource("building/mulch/" + type.getString()));
      }
    });

    // colored books
    Consumer<IFinishedRecipe> bookConditions = withCondition(ConfigEnabledCondition.COLORED_BOOKS);
    String bookGroup = resourceName("colored_book");
    InspirationsBuilding.coloredBooks.forEach((color, book) -> {
      ShapelessRecipeBuilder.shapelessRecipe(book)
                            .addCriterion("has_bookshelf", hasItem(InspirationsTags.Items.BOOKSHELVES))
                            .setGroup(bookGroup)
                            .addIngredient(Items.BOOK)
                            .addIngredient(color.getTag())
                            .build(bookConditions, resource("building/books/" + color.getString()));
    });

    // flowers
    // add dye crafting recipes
    Consumer<IFinishedRecipe> flowerConditions = withCondition(ConfigEnabledCondition.FLOWERS);
    InspirationsBuilding.flower.forEach((type, flower) -> {
      Item dye = type.getDye();
      ShapelessRecipeBuilder.shapelessRecipe(dye)
                            .addCriterion("has_flower", hasItem(flower))
                            .setGroup(Objects.requireNonNull(dye).toString())
                            .addIngredient(flower)
                            .build(flowerConditions, resource("building/flower/" + Objects.requireNonNull(dye.getRegistryName()).getPath()));
    });
    // add temporary cyan dye crafting recipe
    Consumer<IFinishedRecipe> cyanFlowerConditions = withCondition(ConfigEnabledCondition.FLOWERS, not(ConfigEnabledCondition.CAULDRON_DYEING));
    IItemProvider rose = InspirationsBuilding.flower.get(FlowerType.ROSE);
    ShapelessRecipeBuilder.shapelessRecipe(InspirationsBuilding.flower.get(FlowerType.CYAN))
                          .addCriterion("has_dye", hasItem(Tags.Items.DYES_CYAN))
                          .addCriterion("has_flower", hasItem(rose))
                          .addIngredient(Tags.Items.DYES_CYAN)
                          .addIngredient(rose)
                          .build(cyanFlowerConditions, resource("building/flower/cyan_flower"));

    // bookshelves
    Consumer<IFinishedRecipe> bookshelfConditions = withCondition(ConfigEnabledCondition.BOOKSHELF);
    String shelfGroup = resourceName("bookshelf");
    InspirationsBuilding.bookshelf.forEach((type, shelf) -> {
      String[] variants = getShelfVariants(type);
      for (String variant : variants) {
        ShapedRecipeBuilder builder =
            ShapedRecipeBuilder.shapedRecipe(shelf, 2)
                               .setGroup(shelfGroup)
                               .addCriterion("has_slabs", hasItem(ItemTags.WOODEN_SLABS))
                               .addCriterion("has_book", hasItem(Items.BOOK))
                               .key('S', ItemTags.WOODEN_SLABS)
                               .patternLine("SSS")
                               .patternLine(variant)
                               .patternLine("SSS");
        // add extra items for the variant
        switch (type) {
          case ANCIENT:
            builder.key('P', Items.PAPER);
            break;
          case TOMES:
            builder.key('B', Items.BOOK);
            break;
          case RAINBOW:
            builder.key('R', Tags.Items.DYES_RED)
                   .key('G', Tags.Items.DYES_GREEN)
                   .key('B', Tags.Items.DYES_BLUE);
        }
        // add texture and build
        String suffix = variants.length > 1 ? "_" + variant.toLowerCase(Locale.US) : "";
        TextureRecipeBuilder.fromShaped(builder)
                            .setSource(ItemTags.WOODEN_SLABS)
                            .build(bookshelfConditions, resource("building/bookshelves/" + type.getString() + suffix));
      }
    });

    // enlightened bushes
    Consumer<IFinishedRecipe> bushConditions = withCondition(ConfigEnabledCondition.ENLIGHTENED_BUSH);
    String bushGroup = resourceName("enlightened_bush");
    InspirationsBuilding.enlightenedBush.forEach((type, bush) -> {
      ShapedRecipeBuilder builder =
          ShapedRecipeBuilder.shapedRecipe(bush)
                             .setGroup(bushGroup)
                             .addCriterion("has_leaves", hasItem(ItemTags.LEAVES))
                             .addCriterion("has_glowstone", hasItem(Tags.Items.DUSTS_GLOWSTONE))
                             .key('L', ItemTags.LEAVES)
                             .key('G', Tags.Items.DUSTS_GLOWSTONE);
      // white uses no dye
      DyeColor dye = type.getDye();
      if (dye != null) {
        // First line - dye above the middle.
        builder = builder.key('D', dye.getTag()).patternLine(" D ");
      }
      builder.patternLine("GLG");
      // add texture and build
      TextureRecipeBuilder.fromShaped(builder)
                          .setSource(ItemTags.LEAVES)
                          .setMatchFirst()
                          .build(bushConditions, resource("building/enlightened_bush/" + type.getString()));
    });
  }

  /**
   * Adds recipes for a path type.
   * @param consumer     Recipe consumer
   * @param type         Path type
   * @param ingredient   Ingredient for crafting
   * @param criteria     Criteria instance
   */
  private void addPath(Consumer<IFinishedRecipe> consumer, PathType type, Ingredient ingredient, ICriterionInstance criteria) {
    // crafting
    IItemProvider path = InspirationsBuilding.path.get(type);
    // skip round path crafting, conflict with pressure plates
    if (type != PathType.ROUND) {
      ShapedRecipeBuilder.shapedRecipe(path, 6)
                         .addCriterion("has_item", criteria)
                         .key('C', ingredient)
                         .patternLine("CC")
                         .build(consumer, resource("building/path/" + type.getString() + "_crafting"));
    }
    // stonecutting
    SingleItemRecipeBuilder.stonecuttingRecipe(ingredient, path, 6)
                           .addCriterion("has_stone", criteria)
                           .build(consumer, resource("building/path/" + type.getString() + "_cutting"));
  }

  /**
   * Gets recipe variants for a shelf type
   * @param type  Shelf type
   * @return  Array of recipe variants
   */
  private static String[] getShelfVariants(ShelfType type) {
    switch (type) {
      case NORMAL: default:
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
