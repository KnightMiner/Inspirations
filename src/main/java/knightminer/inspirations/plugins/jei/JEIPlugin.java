package knightminer.inspirations.plugins.jei;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.PotionTypes;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionType;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.annotation.Nonnull;

import knightminer.inspirations.building.InspirationsBuilding;
import knightminer.inspirations.common.Config;
import knightminer.inspirations.common.PulseBase;
import knightminer.inspirations.library.recipe.TextureRecipe;
import knightminer.inspirations.plugins.jei.cauldron.CauldronRecipeCategory;
import knightminer.inspirations.plugins.jei.cauldron.CauldronRecipeChecker;
import knightminer.inspirations.plugins.jei.cauldron.ingredient.DyeIngredient;
import knightminer.inspirations.plugins.jei.cauldron.ingredient.DyeIngredientHelper;
import knightminer.inspirations.plugins.jei.cauldron.ingredient.DyeIngredientRenderer;
import knightminer.inspirations.plugins.jei.cauldron.ingredient.PotionIngredient;
import knightminer.inspirations.plugins.jei.cauldron.ingredient.PotionIngredientHelper;
import knightminer.inspirations.plugins.jei.cauldron.ingredient.PotionIngredientRenderer;
import knightminer.inspirations.plugins.jei.smashing.SmashingRecipeCategory;
import knightminer.inspirations.plugins.jei.smashing.SmashingRecipeChecker;
import knightminer.inspirations.plugins.jei.texture.TextureRecipeHandler;
import knightminer.inspirations.plugins.jei.texture.TextureSubtypeInterpreter;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.IJeiRuntime;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.IRecipeRegistry;
import mezz.jei.api.ISubtypeRegistry;
import mezz.jei.api.gui.ICraftingGridHelper;
import mezz.jei.api.ingredients.IModIngredientRegistration;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import mezz.jei.api.recipe.VanillaRecipeCategoryUid;

@mezz.jei.api.JEIPlugin
public class JEIPlugin implements IModPlugin {
	public static IJeiHelpers jeiHelpers;
	// crafting grid slots, integer constants from the default crafting grid implementation
	private static final int craftOutputSlot = 0;
	private static final int craftInputSlot1 = 1;

	public static ICraftingGridHelper craftingGridHelper;
	public static IRecipeRegistry recipeRegistry;
	public static CauldronRecipeCategory cauldron;

	@Override
	public void registerItemSubtypes(ISubtypeRegistry registry) {
		TextureSubtypeInterpreter texture = new TextureSubtypeInterpreter();

		// tools
		if(PulseBase.isBuildingLoaded()) {
			// bookshelves
			if(InspirationsBuilding.bookshelf != null) {
				registry.registerSubtypeInterpreter(Item.getItemFromBlock(InspirationsBuilding.bookshelf), texture);
			}

			// enlightened bush
			if(InspirationsBuilding.enlightenedBush != null) {
				registry.registerSubtypeInterpreter(Item.getItemFromBlock(InspirationsBuilding.enlightenedBush), texture);
			}
		}
	}

	@Override
	public void registerCategories(IRecipeCategoryRegistration registry) {
		final IGuiHelper guiHelper = registry.getJeiHelpers().getGuiHelper();

		if(PulseBase.isRecipesLoaded()) {
			// Anvil
			if(Config.enableAnvilSmashing) {
				registry.addRecipeCategories(new SmashingRecipeCategory(guiHelper));
			}
			// cauldron
			if(Config.enableCauldronRecipes) {
				registry.addRecipeCategories(cauldron = new CauldronRecipeCategory(guiHelper));
			}
		}
	}

	@Override
	public void register(@Nonnull IModRegistry registry) {
		jeiHelpers = registry.getJeiHelpers();
		IGuiHelper guiHelper = jeiHelpers.getGuiHelper();

		// crafting helper used by the shaped table wrapper
		craftingGridHelper = guiHelper.createCraftingGridHelper(craftInputSlot1, craftOutputSlot);
		registry.handleRecipes(TextureRecipe.class, new TextureRecipeHandler(), VanillaRecipeCategoryUid.CRAFTING);

		// tweaks
		if(PulseBase.isRecipesLoaded()) {
			if(Config.enableAnvilSmashing) {
				registry.addRecipes(SmashingRecipeChecker.getRecipes(), SmashingRecipeCategory.CATEGORY);
				registry.addRecipeCatalyst(new ItemStack(Blocks.ANVIL), SmashingRecipeCategory.CATEGORY);
			}
			if(Config.enableCauldronRecipes) {
				registry.addRecipes(CauldronRecipeChecker.getRecipes(), CauldronRecipeCategory.CATEGORY);
				registry.addRecipeCatalyst(new ItemStack(Items.CAULDRON), CauldronRecipeCategory.CATEGORY);
			}
		}
	}

	@Override
	public void registerIngredients(IModIngredientRegistration registry) {
		if(PulseBase.isRecipesLoaded()) {
			if(Config.enableCauldronDyeing) {
				registry.register(DyeIngredient.class, Arrays.stream(EnumDyeColor.values()).map(DyeIngredient::new).collect(Collectors.toList()), DyeIngredientHelper.INSTANCE, DyeIngredientRenderer.INVENTORY);
			}
			if(Config.enableCauldronBrewing) {
				registry.register(PotionIngredient.class, StreamSupport.stream(PotionType.REGISTRY.spliterator(), false)
						.filter(type->type != PotionTypes.EMPTY && type != PotionTypes.WATER).map(PotionIngredient::new)
						.collect(Collectors.toList()), PotionIngredientHelper.INSTANCE, PotionIngredientRenderer.INVENTORY);
			}
		}
	}

	@Override
	public void onRuntimeAvailable(@Nonnull IJeiRuntime jeiRuntime) {
		recipeRegistry = jeiRuntime.getRecipeRegistry();
	}
}
