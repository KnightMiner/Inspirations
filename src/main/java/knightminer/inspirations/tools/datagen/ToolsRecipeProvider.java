package knightminer.inspirations.tools.datagen;

import knightminer.inspirations.common.data.ConfigEnabledCondition;
import knightminer.inspirations.common.datagen.IInspirationsRecipeBuilder;
import knightminer.inspirations.common.datagen.NBTIngredient;
import knightminer.inspirations.tools.InspirationsTools;
import net.minecraft.advancements.critereon.EnchantmentPredicate;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.advancements.critereon.NbtPredicate;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.IConditionBuilder;

import java.util.Collections;
import java.util.function.Consumer;

public class ToolsRecipeProvider extends RecipeProvider implements IConditionBuilder, IInspirationsRecipeBuilder {
  private Consumer<FinishedRecipe> consumer;

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
  public Consumer<FinishedRecipe> getConsumer() {
    return consumer;
  }

  @Override
  protected void buildCraftingRecipes(Consumer<FinishedRecipe> consumer) {
    // set for the util
    this.consumer = consumer;

    // photometer
    ShapedRecipeBuilder.shaped(InspirationsTools.photometer)
                       .unlockedBy("has_glowstone", has(Tags.Items.DUSTS_GLOWSTONE))
                       .unlockedBy("has_redstone", has(Tags.Items.DUSTS_REDSTONE))
                       .define('B', Items.GLASS_BOTTLE)
                       .define('R', Tags.Items.DUSTS_REDSTONE)
                       .define('G', Tags.Items.DUSTS_GLOWSTONE)
                       .define('I', Tags.Items.INGOTS_IRON)
                       .pattern("RBG")
                       .pattern(" I ")
                       .save(withCondition(ConfigEnabledCondition.PHOTOMETER), prefix(InspirationsTools.photometer, "tools/"));

    // barometer
    // not using the builder because it lacks potion
    ItemPredicate hasWaterBottle = new ItemPredicate(
        null,  // Tag
        Collections.singleton(Items.POTION),
        MinMaxBounds.Ints.ANY,
        MinMaxBounds.Ints.ANY,
        EnchantmentPredicate.NONE,
        EnchantmentPredicate.NONE,
        Potions.WATER,
        NbtPredicate.ANY
    );
    ShapedRecipeBuilder.shaped(InspirationsTools.barometer)
                       .unlockedBy("has_bottle", inventoryTrigger(hasWaterBottle))
                       .define('W', new NBTIngredient(PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.WATER)))
                       .define('B', Items.GLASS_BOTTLE)
                       .define('R', Tags.Items.DUSTS_REDSTONE)
                       .pattern(" W")
                       .pattern("BR")
                       .save(withCondition(ConfigEnabledCondition.BAROMETER), prefix(InspirationsTools.barometer, "tools/"));

    // lock and key
    Consumer<FinishedRecipe> lockCondition = withCondition(ConfigEnabledCondition.LOCK);
    ShapedRecipeBuilder.shaped(InspirationsTools.lock)
                       .unlockedBy("has_iron", has(Tags.Items.INGOTS_IRON))
                       .define('I', Tags.Items.INGOTS_IRON)
                       .define('N', Tags.Items.NUGGETS_IRON)
                       .pattern("I")
                       .pattern("N")
                       .save(lockCondition, prefix(InspirationsTools.lock, "tools/"));
    ShapedRecipeBuilder.shaped(InspirationsTools.key)
                       .unlockedBy("has_iron", has(Tags.Items.INGOTS_IRON))
                       .define('I', Tags.Items.INGOTS_IRON)
                       .define('N', Tags.Items.NUGGETS_IRON)
                       .pattern("IN")
                       .save(lockCondition, prefix(InspirationsTools.key, "tools/"));

    // north compass
    ShapedRecipeBuilder.shaped(InspirationsTools.northCompass)
                       .unlockedBy("has_iron", has(Tags.Items.INGOTS_IRON))
                       .define('I', Tags.Items.INGOTS_IRON)
                       .define('N', Tags.Items.NUGGETS_IRON)
                       .pattern(" I ")
                       .pattern("INI")
                       .pattern(" I ")
                       .save(withCondition(ConfigEnabledCondition.NORTH_COMPASS), prefix(InspirationsTools.northCompass, "tools/"));

    // waypoint compasses
    // white
    ShapedRecipeBuilder.shaped(InspirationsTools.dimensionCompass)
                       .unlockedBy("has_ender", has(Tags.Items.ENDER_PEARLS))
                       .define('G', Tags.Items.INGOTS_GOLD)
                       .define('E', Tags.Items.ENDER_PEARLS)
                       .pattern(" G ")
                       .pattern("GEG")
                       .pattern(" G ")
                       .save(withCondition(ConfigEnabledCondition.DIMENSION_COMPASS), resource("tools/dimension_compass"));

    // redstone arrow
    ShapedRecipeBuilder.shaped(InspirationsTools.redstoneArrow, 8)
                       .unlockedBy("has_redstone", has(Tags.Items.DUSTS_REDSTONE))
                       .define('R', Tags.Items.DUSTS_REDSTONE)
                       .define('S', Tags.Items.RODS_WOODEN)
                       .define('F', Tags.Items.FEATHERS)
                       .pattern("R")
                       .pattern("S")
                       .pattern("F")
                       .save(withCondition(ConfigEnabledCondition.CHARGED_ARROW), prefix(InspirationsTools.redstoneArrow, "tools/"));

    // redstone charger
    ShapelessRecipeBuilder.shapeless(InspirationsTools.redstoneCharger)
                          .unlockedBy("has_redstone", has(Tags.Items.DUSTS_REDSTONE))
                          .unlockedBy("has_gold", has(Tags.Items.INGOTS_GOLD))
                          .requires(Tags.Items.DUSTS_REDSTONE)
                          .requires(Tags.Items.INGOTS_GOLD)
                          .save(withCondition(ConfigEnabledCondition.REDSTONE_CHARGER), prefix(InspirationsTools.redstoneCharger, "tools/"));

  }
}
