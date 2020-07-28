package knightminer.inspirations.plugins.jei;

import knightminer.inspirations.Inspirations;
import knightminer.inspirations.building.InspirationsBuilding;
import knightminer.inspirations.common.IHidable;
import knightminer.inspirations.library.Util;
import knightminer.inspirations.library.recipe.TextureRecipe;
import knightminer.inspirations.library.util.TextureBlockUtil;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.ingredient.ICraftingGridHelper;
import mezz.jei.api.helpers.IModIdHelper;
import mezz.jei.api.ingredients.subtypes.ISubtypeInterpreter;
import mezz.jei.api.recipe.IRecipeManager;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.ISubtypeRegistration;
import mezz.jei.api.registration.IVanillaCategoryExtensionRegistration;
import mezz.jei.api.runtime.IIngredientManager;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

@JeiPlugin
public class JEIPlugin implements IModPlugin {
	public static IRecipeManager recipeManager;
	public static ICraftingGridHelper vanillaCraftingHelper;
	public static IModIdHelper modIdHelper;
	public static IIngredientManager ingedientManager;

	// Store which items can be hidden, and their current state.
	// This lets us reduce the work JEI tries to do.
	private static List<HideState> hideableItems = new ArrayList<>();

	static class HideState {
		ItemStack stack;
		boolean visible;

		HideState(ItemStack item) {
			stack = item;
			visible = true;
		}
	}

	@Override
	public ResourceLocation getPluginUid() {
		return Util.getResource("jei");
	}

	@Override
	public void registerItemSubtypes(ISubtypeRegistration registry) {
		ISubtypeInterpreter texture = TextureBlockUtil::getTextureBlockName;

		// building
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
		ingedientManager = jeiRuntime.getIngredientManager();

		hideableItems.clear();
		for(ItemStack item: ingedientManager.getAllIngredients(VanillaTypes.ITEM)) {
			if (item.getItem() instanceof IHidable) {
				hideableItems.add(new HideState(item));
			}
		}
		Inspirations.updateJEI = JEIPlugin::updateHiddenItems;
	}

	// Go through and hide/unhide Inspirations items whenever the config reloads.
	private static void updateHiddenItems() {
		// Only try to alter the state of items that have actually changed.
		List<ItemStack> hidden = new ArrayList<>();
		List<ItemStack> visible = new ArrayList<>();
		for(HideState state: hideableItems) {
			boolean enabled = ((IHidable)state.stack.getItem()).isEnabled();
			if (enabled != state.visible) {
				if (enabled) {
					visible.add(state.stack);
				} else {
					hidden.add(state.stack);
				}
				state.visible = enabled;
			}
		}
		if (hidden.size() > 0) {
			ingedientManager.removeIngredientsAtRuntime(VanillaTypes.ITEM, hidden);
		}
		if (visible.size() > 0) {
			ingedientManager.addIngredientsAtRuntime(VanillaTypes.ITEM, visible);
		}
	}
}
