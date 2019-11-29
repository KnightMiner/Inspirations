package knightminer.inspirations.plugins.jei;

import knightminer.inspirations.building.InspirationsBuilding;
import knightminer.inspirations.common.PulseBase;
import knightminer.inspirations.library.Util;
import knightminer.inspirations.library.recipe.TextureRecipe;
import knightminer.inspirations.library.util.TextureBlockUtil;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.gui.ingredient.ICraftingGridHelper;
import mezz.jei.api.helpers.IModIdHelper;
import mezz.jei.api.ingredients.subtypes.ISubtypeInterpreter;
import mezz.jei.api.recipe.IRecipeManager;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.ISubtypeRegistration;
import mezz.jei.api.registration.IVanillaCategoryExtensionRegistration;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.util.ResourceLocation;

@JeiPlugin
public class JEIPlugin implements IModPlugin {
	public static IRecipeManager recipeManager;
	public static ICraftingGridHelper vanillaCraftingHelper;
	public static IModIdHelper modIdHelper;

	@Override
	public ResourceLocation getPluginUid() {
		return Util.getResource("jei");
	}

	@Override
	public void registerItemSubtypes(ISubtypeRegistration registry) {
		ISubtypeInterpreter texture = TextureBlockUtil::getTextureBlockName;

		// building
		if(PulseBase.isBuildingLoaded()) {
			// bookshelves
			registry.registerSubtypeInterpreter(InspirationsBuilding.shelf_normal.asItem(), texture);
			registry.registerSubtypeInterpreter(InspirationsBuilding.shelf_ancient.asItem(), texture);
			registry.registerSubtypeInterpreter(InspirationsBuilding.shelf_rainbow.asItem(), texture);
			registry.registerSubtypeInterpreter(InspirationsBuilding.shelf_tomes.asItem(), texture);

			// enlightened bushes
			registry.registerSubtypeInterpreter(InspirationsBuilding.whiteEnlightenedBush.asItem(), texture);
			registry.registerSubtypeInterpreter(InspirationsBuilding.redEnlightenedBush.asItem(), texture);
			registry.registerSubtypeInterpreter(InspirationsBuilding.greenEnlightenedBush.asItem(), texture);
			registry.registerSubtypeInterpreter(InspirationsBuilding.blueEnlightenedBush.asItem(), texture);
		}
	}

	@Override
	public void registerRecipes(IRecipeRegistration registry) {
		vanillaCraftingHelper = registry.getJeiHelpers().getGuiHelper().createCraftingGridHelper(1);
		modIdHelper = registry.getJeiHelpers().getModIdHelper();
	}


	@Override
	public void registerVanillaCategoryExtensions(IVanillaCategoryExtensionRegistration registry) {
		registry.getCraftingCategory().addCategoryExtension(TextureRecipe.class, TextureRecipeExtension::new);
	}

	@Override
	public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
		recipeManager = jeiRuntime.getRecipeManager();
	}
}
