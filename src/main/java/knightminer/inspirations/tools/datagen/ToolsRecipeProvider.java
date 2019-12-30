package knightminer.inspirations.tools.datagen;

import knightminer.inspirations.common.Config;
import knightminer.inspirations.common.data.ConfigEnabledCondition;
import knightminer.inspirations.common.data.PulseLoadedCondition;
import knightminer.inspirations.common.datagen.CondRecipe;
import knightminer.inspirations.library.InspirationsTags;
import knightminer.inspirations.library.Util;
import knightminer.inspirations.tools.InspirationsTools;
import knightminer.inspirations.tools.recipe.CopyWaypointCompassRecipe;
import knightminer.inspirations.tools.recipe.DyeWaypointCompassRecipe;
import net.minecraft.advancements.criterion.EnchantmentPredicate;
import net.minecraft.advancements.criterion.ItemPredicate;
import net.minecraft.advancements.criterion.MinMaxBounds;
import net.minecraft.advancements.criterion.NBTPredicate;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.RecipeProvider;
import net.minecraft.item.DyeColor;
import net.minecraft.item.DyeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.PotionUtils;
import net.minecraft.potion.Potions;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.crafting.IngredientNBT;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.IConditionBuilder;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

public class ToolsRecipeProvider extends RecipeProvider implements IConditionBuilder {
	ICondition TOOLS = new PulseLoadedCondition(InspirationsTools.pulseID);

	public ToolsRecipeProvider(DataGenerator gen) {
		super(gen);
	}

	@Nonnull
	@Override
	public String getName() {
		return "Inspirations Recipes - Tools";
	}

	@Override
	protected void registerRecipes(@Nonnull Consumer<IFinishedRecipe> consumer) {
		CondRecipe.shaped(InspirationsTools.photometer)
				.addCondition(TOOLS)
				.addCondition(ConfigEnabledCondition.PHOTOMETER)
				.addCriterion("has_glowstone", hasItem(Tags.Items.DUSTS_GLOWSTONE))
				.addCriterion("has_redstone", hasItem(Tags.Items.DUSTS_REDSTONE))
				.key('B', Items.GLASS_BOTTLE)
				.key('R', Tags.Items.DUSTS_REDSTONE)
				.key('G', Tags.Items.DUSTS_GLOWSTONE)
				.key('I', Tags.Items.INGOTS_IRON)
				.patternLine("RGB")
				.patternLine(" I ")
				.build(consumer);

		ItemPredicate hasWater = new ItemPredicate(
				null,  // Tag
				Items.POTION,
				MinMaxBounds.IntBound.UNBOUNDED,
				MinMaxBounds.IntBound.UNBOUNDED,
				new EnchantmentPredicate[0],
				Potions.WATER,
				NBTPredicate.ANY
		);

		CondRecipe.shaped(InspirationsTools.barometer)
				.addCondition(TOOLS)
				.addCondition(ConfigEnabledCondition.BAROMETER)
				.addCriterion("has_bottle", hasItem(hasWater))
				.key('W', IngredientNBT.fromStacks(PotionUtils.addPotionToItemStack(
						new ItemStack(Items.POTION),
						Potions.WATER
				)))
				.key('B', Items.GLASS_BOTTLE)
				.key('R', Tags.Items.DUSTS_REDSTONE)
				.patternLine(" W")
				.patternLine("BR")
				.build(consumer);

		CondRecipe.shaped(InspirationsTools.lock)
				.addCondition(TOOLS)
				.addCondition(ConfigEnabledCondition.LOCK)
				.addCriterion("has_iron", hasItem(Tags.Items.INGOTS_IRON))
				.key('I', Tags.Items.INGOTS_IRON)
				.key('N', Tags.Items.NUGGETS_IRON)
				.patternLine("I")
				.patternLine("N")
				.build(consumer);

		CondRecipe.shaped(InspirationsTools.key)
				.addCondition(TOOLS)
				.addCondition(ConfigEnabledCondition.LOCK)
				.canMirror()
				.addCriterion("has_iron", hasItem(Tags.Items.INGOTS_IRON))
				.key('I', Tags.Items.INGOTS_IRON)
				.key('N', Tags.Items.NUGGETS_IRON)
				.patternLine("IN")
				.build(consumer);

		CondRecipe.shaped(InspirationsTools.northCompass)
				.addCondition(TOOLS)
				.addCondition(ConfigEnabledCondition.NORTH_COMPASS)
				.canMirror()
				.addCriterion("has_iron", hasItem(Tags.Items.INGOTS_IRON))
				.key('I', Tags.Items.INGOTS_IRON)
				.key('N', Tags.Items.NUGGETS_IRON)
				.patternLine(" I ")
				.patternLine("INI")
				.patternLine(" I ")
				.build(consumer);

		Item undyedCompass = InspirationsTools.waypointCompasses[DyeColor.WHITE.getId()];
		CondRecipe.shaped(undyedCompass)
				.addCondition(TOOLS)
				.addCondition(ConfigEnabledCondition.CRAFT_WAYPOINT)
				.addCriterion("has_blaze", hasItem(Tags.Items.RODS_BLAZE))
				.key('I', Tags.Items.INGOTS_IRON)
				.key('B', Tags.Items.RODS_BLAZE)
				.patternLine(" I ")
				.patternLine("IBI")
				.patternLine(" I ")
				.build(consumer);

		CondRecipe.custom(CopyWaypointCompassRecipe.SERIALIZER)
				.addCondition(TOOLS)
				.addCondition(ConfigEnabledCondition.COPY_WAYPOINT)
				.build(consumer);

		for (DyeColor color: DyeColor.values()) {
			CondRecipe.shapeless(InspirationsTools.waypointCompasses[color.getId()])
					.custom(DyeWaypointCompassRecipe.SERIALIZER)
					.addCondition(TOOLS)
					.addCondition(ConfigEnabledCondition.DYE_WAYPOINT)
					.addCriterion("has_compass", hasItem(InspirationsTags.Items.WAYPOINT_COMPASSES))
					.setGroup(Util.resource("dye_waypoint_compass"))
					.addIngredient(InspirationsTags.Items.WAYPOINT_COMPASSES)
					.addIngredient(Util.getDyeTag(color))
					.build(consumer, "waypoint_compass/" + (color == DyeColor.WHITE ? "undye" : color.getName()));
		}

		CondRecipe.shaped(InspirationsTools.redstoneArrow, 8)
				.addCondition(TOOLS)
				.addCondition(ConfigEnabledCondition.CHARGED_ARROW)
				.addCriterion("has_redstone", hasItem(Tags.Items.DUSTS_REDSTONE))
				.key('R', Tags.Items.DUSTS_REDSTONE)
				.key('S', Tags.Items.RODS_WOODEN)
				.key('F', Tags.Items.FEATHERS)
				.patternLine("R")
				.patternLine("S")
				.patternLine("F")
				.build(consumer);

		CondRecipe.shapeless(InspirationsTools.redstoneCharger)
				.addCondition(TOOLS)
				.addCondition(ConfigEnabledCondition.REDSTONE_CHARGER)
				.addCriterion("has_redstone", hasItem(Tags.Items.DUSTS_REDSTONE))
				.addCriterion("has_gold", hasItem(Tags.Items.INGOTS_GOLD))
				.addIngredient(Tags.Items.DUSTS_REDSTONE)
				.addIngredient(Tags.Items.INGOTS_GOLD)
				.build(consumer);

	}
}
