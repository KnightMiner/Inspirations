package knightminer.inspirations.tools.datagen;

import knightminer.inspirations.common.data.ConfigEnabledCondition;
import knightminer.inspirations.common.data.ConsumerWrapperBuilder;
import knightminer.inspirations.common.datagen.IRecipeBuilderUtils;
import knightminer.inspirations.common.datagen.NBTIngredient;
import knightminer.inspirations.library.InspirationsTags;
import knightminer.inspirations.library.recipe.RecipeSerializers;
import knightminer.inspirations.tools.InspirationsTools;
import net.minecraft.advancements.criterion.EnchantmentPredicate;
import net.minecraft.advancements.criterion.ItemPredicate;
import net.minecraft.advancements.criterion.MinMaxBounds;
import net.minecraft.advancements.criterion.NBTPredicate;
import net.minecraft.data.CustomRecipeBuilder;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.RecipeProvider;
import net.minecraft.data.ShapedRecipeBuilder;
import net.minecraft.data.ShapelessRecipeBuilder;
import net.minecraft.item.DyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.PotionUtils;
import net.minecraft.potion.Potions;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.IConditionBuilder;

import java.util.function.Consumer;

public class ToolsRecipeProvider extends RecipeProvider implements IConditionBuilder, IRecipeBuilderUtils {
  private Consumer<IFinishedRecipe> consumer;

  public ToolsRecipeProvider(DataGenerator gen) {
    super(gen);
  }

  @Override
  public String getName() {
    return "Inspirations Recipes - Tools";
  }

  @Override
  public ICondition baseCondition() {
    return ConfigEnabledCondition.MODULE_TOOLS;
  }

  @Override
  public Consumer<IFinishedRecipe> getConsumer() {
    return consumer;
  }

  @Override
  protected void registerRecipes(Consumer<IFinishedRecipe> consumer) {
    // set for the util
    this.consumer = consumer;

    // photometer
    ShapedRecipeBuilder.shapedRecipe(InspirationsTools.photometer)
                       .addCriterion("has_glowstone", hasItem(Tags.Items.DUSTS_GLOWSTONE))
                       .addCriterion("has_redstone", hasItem(Tags.Items.DUSTS_REDSTONE))
                       .key('B', Items.GLASS_BOTTLE)
                       .key('R', Tags.Items.DUSTS_REDSTONE)
                       .key('G', Tags.Items.DUSTS_GLOWSTONE)
                       .key('I', Tags.Items.INGOTS_IRON)
                       .patternLine("RBG")
                       .patternLine(" I ")
                       .build(withCondition(ConfigEnabledCondition.PHOTOMETER), prefix(InspirationsTools.photometer, "tools/"));

    // barometer
    // not using the builder because it lacks potion
    ItemPredicate hasWaterBottle = new ItemPredicate(
        null,  // Tag
        Items.POTION,
        MinMaxBounds.IntBound.UNBOUNDED,
        MinMaxBounds.IntBound.UNBOUNDED,
        EnchantmentPredicate.enchantments,
        EnchantmentPredicate.enchantments,
        Potions.WATER,
        NBTPredicate.ANY
    );
    ShapedRecipeBuilder.shapedRecipe(InspirationsTools.barometer)
                       .addCriterion("has_bottle", hasItem(hasWaterBottle))
                       .key('W', new NBTIngredient(PotionUtils.addPotionToItemStack(new ItemStack(Items.POTION), Potions.WATER)))
                       .key('B', Items.GLASS_BOTTLE)
                       .key('R', Tags.Items.DUSTS_REDSTONE)
                       .patternLine(" W")
                       .patternLine("BR")
                       .build(withCondition(ConfigEnabledCondition.BAROMETER), prefix(InspirationsTools.barometer, "tools/"));

    // lock and key
    Consumer<IFinishedRecipe> lockCondition = withCondition(ConfigEnabledCondition.LOCK);
    ShapedRecipeBuilder.shapedRecipe(InspirationsTools.lock)
                       .addCriterion("has_iron", hasItem(Tags.Items.INGOTS_IRON))
                       .key('I', Tags.Items.INGOTS_IRON)
                       .key('N', Tags.Items.NUGGETS_IRON)
                       .patternLine("I")
                       .patternLine("N")
                       .build(lockCondition, prefix(InspirationsTools.lock, "tools/"));
    ShapedRecipeBuilder.shapedRecipe(InspirationsTools.key)
                       .addCriterion("has_iron", hasItem(Tags.Items.INGOTS_IRON))
                       .key('I', Tags.Items.INGOTS_IRON)
                       .key('N', Tags.Items.NUGGETS_IRON)
                       .patternLine("IN")
                       .build(lockCondition, prefix(InspirationsTools.key, "tools/"));

    // north compass
    ShapedRecipeBuilder.shapedRecipe(InspirationsTools.northCompass)
                       .addCriterion("has_iron", hasItem(Tags.Items.INGOTS_IRON))
                       .key('I', Tags.Items.INGOTS_IRON)
                       .key('N', Tags.Items.NUGGETS_IRON)
                       .patternLine(" I ")
                       .patternLine("INI")
                       .patternLine(" I ")
                       .build(withCondition(ConfigEnabledCondition.NORTH_COMPASS), prefix(InspirationsTools.northCompass, "tools/"));

    // waypoint compasses
    // white
    Item undyedCompass = InspirationsTools.waypointCompasses[DyeColor.WHITE.getId()];
    ShapedRecipeBuilder.shapedRecipe(undyedCompass)
                       .addCriterion("has_blaze", hasItem(Tags.Items.RODS_BLAZE))
                       .key('I', Tags.Items.INGOTS_IRON)
                       .key('B', Tags.Items.RODS_BLAZE)
                       .patternLine(" I ")
                       .patternLine("IBI")
                       .patternLine(" I ")
                       .build(withCondition(ConfigEnabledCondition.CRAFT_WAYPOINT), resource("tools/waypoint_compass/base"));
    // coloring
    CustomRecipeBuilder.customRecipe(RecipeSerializers.COPY_WAYPOINT_COMPASS)
                       .build(withCondition(ConfigEnabledCondition.COPY_WAYPOINT), resourceName("tools/waypoint_compass/copy"));

    Consumer<IFinishedRecipe> dyeWaypoint =
        ConsumerWrapperBuilder.wrap(RecipeSerializers.DYE_WAYPOINT_COMPASS)
                              .addCondition(ConfigEnabledCondition.MODULE_TOOLS)
                              .addCondition(ConfigEnabledCondition.DYE_WAYPOINT)
                              .build(consumer);
    String dyeGroup = resourceName("dye_waypoint_compass");
    for (DyeColor color : DyeColor.values()) {
      ShapelessRecipeBuilder.shapelessRecipe(InspirationsTools.waypointCompasses[color.getId()])
                            .addCriterion("has_compass", hasItem(InspirationsTags.Items.WAYPOINT_COMPASSES))
                            .setGroup(dyeGroup)
                            .addIngredient(InspirationsTags.Items.WAYPOINT_COMPASSES)
                            .addIngredient(color.getTag())
                            .build(dyeWaypoint, resource("tools/waypoint_compass/" + color.getString()));
    }

    // redstone arrow
    ShapedRecipeBuilder.shapedRecipe(InspirationsTools.redstoneArrow, 8)
                       .addCriterion("has_redstone", hasItem(Tags.Items.DUSTS_REDSTONE))
                       .key('R', Tags.Items.DUSTS_REDSTONE)
                       .key('S', Tags.Items.RODS_WOODEN)
                       .key('F', Tags.Items.FEATHERS)
                       .patternLine("R")
                       .patternLine("S")
                       .patternLine("F")
                       .build(withCondition(ConfigEnabledCondition.CHARGED_ARROW), prefix(InspirationsTools.redstoneArrow, "tools/"));

    // redstone charger
    ShapelessRecipeBuilder.shapelessRecipe(InspirationsTools.redstoneCharger)
                          .addCriterion("has_redstone", hasItem(Tags.Items.DUSTS_REDSTONE))
                          .addCriterion("has_gold", hasItem(Tags.Items.INGOTS_GOLD))
                          .addIngredient(Tags.Items.DUSTS_REDSTONE)
                          .addIngredient(Tags.Items.INGOTS_GOLD)
                          .build(withCondition(ConfigEnabledCondition.REDSTONE_CHARGER), prefix(InspirationsTools.redstoneCharger, "tools/"));

  }
}
