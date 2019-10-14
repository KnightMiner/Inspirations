package knightminer.inspirations.plugins.jei;

import knightminer.inspirations.library.Util;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.recipe.IRecipeManager;
import mezz.jei.api.registration.IModIngredientRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
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
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.gui.ingredient.ICraftingGridHelper;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.ISubtypeRegistration;
import net.minecraft.util.ResourceLocation;

@mezz.jei.api.JeiPlugin
public class JEIPlugin implements IModPlugin {
	public static IJeiHelpers jeiHelpers;
	// crafting grid slots, integer constants from the default crafting grid implementation
	private static final int craftOutputSlot = 0;
	private static final int craftInputSlot1 = 1;

	public static ICraftingGridHelper craftingGridHelper;
	public static IRecipeManager recipeRegistry;
	public static CauldronRecipeCategory cauldron;

	@Nonnull
	@Override
	public ResourceLocation getPluginUid() {
		return Util.getResource("jeiPlugin");
	}

	@Override
	public void registerItemSubtypes(ISubtypeRegistration registry) {
		TextureSubtypeInterpreter texture = new TextureSubtypeInterpreter();

		// tools
		if(PulseBase.isBuildingLoaded()) {
			// bookshelves
			registry.registerSubtypeInterpreter(Item.getItemFromBlock(InspirationsBuilding.shelf_normal), texture);
			registry.registerSubtypeInterpreter(Item.getItemFromBlock(InspirationsBuilding.shelf_ancient), texture);
			registry.registerSubtypeInterpreter(Item.getItemFromBlock(InspirationsBuilding.shelf_rainbow), texture);
			registry.registerSubtypeInterpreter(Item.getItemFromBlock(InspirationsBuilding.shelf_tomes), texture);

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
			registry.addRecipeCategories(new SmashingRecipeCategory(guiHelper));
			// cauldron
			registry.addRecipeCategories(cauldron = new CauldronRecipeCategory(guiHelper));
		}
	}

	@Override
	public void registerRecipes(IRecipeRegistration registry) {
		jeiHelpers = registry.getJeiHelpers();
		IGuiHelper guiHelper = jeiHelpers.getGuiHelper();

		// crafting helper used by the shaped table wrapper
		craftingGridHelper = guiHelper.createCraftingGridHelper(craftInputSlot1, craftOutputSlot);
		registry.handleRecipes(TextureRecipe.class, new TextureRecipeHandler(), VanillaRecipeCategoryUid.CRAFTING);

		// tweaks
		if(PulseBase.isRecipesLoaded()) {
			if(Config.enableAnvilSmashing.get()) {
				registry.addRecipes(SmashingRecipeChecker.getRecipes(), SmashingRecipeCategory.CATEGORY);
				registry.addRecipeCatalyst(new ItemStack(Blocks.ANVIL), SmashingRecipeCategory.CATEGORY);
			}
			if(Config.enableCauldronRecipes()) {
				registry.addRecipes(CauldronRecipeChecker.getRecipes(), CauldronRecipeCategory.CATEGORY);
				registry.addRecipeCatalyst(new ItemStack(Items.CAULDRON), CauldronRecipeCategory.CATEGORY);
			}
		}
	}

	@Override
	public void registerIngredients(IModIngredientRegistration registration) {
		if(PulseBase.isRecipesLoaded()) {
			// dye ingredients
			registration.register(() -> DyeIngredient.class, DyeIngredientHelper.ALL_DYES, DyeIngredientHelper.INSTANCE, DyeIngredientRenderer.INVENTORY);
			// potion ingredients
			registration.register(() -> PotionIngredient.class, PotionIngredientHelper.ALL_POTIONS, PotionIngredientHelper.INSTANCE, PotionIngredientRenderer.INVENTORY);
		}
	}

	@Override
	public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
		recipeRegistry = jeiRuntime.getRecipeManager();
	}
}
