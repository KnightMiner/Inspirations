package knightminer.inspirations.plugins.jei;

import knightminer.inspirations.Inspirations;
import knightminer.inspirations.building.InspirationsBuilding;
import knightminer.inspirations.common.Config;
import knightminer.inspirations.common.IHidable;
import knightminer.inspirations.library.recipe.RecipeTypes;
import knightminer.inspirations.library.recipe.cauldron.CauldronContentTypes;
import knightminer.inspirations.library.recipe.cauldron.contents.ICauldronContents;
import knightminer.inspirations.library.recipe.cauldron.recipe.ICauldronRecipeDisplay;
import knightminer.inspirations.plugins.jei.cauldron.CauldronCategory;
import knightminer.inspirations.plugins.jei.cauldron.CauldronContentHelper;
import knightminer.inspirations.plugins.jei.cauldron.CauldronRenderer;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.ingredients.subtypes.IIngredientSubtypeInterpreter;
import mezz.jei.api.registration.IModIngredientRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.ISubtypeRegistration;
import mezz.jei.api.runtime.IIngredientManager;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.registries.ForgeRegistries;
import slimeknights.mantle.item.RetexturedBlockItem;
import slimeknights.mantle.recipe.IMultiRecipe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@JeiPlugin
public class JEIPlugin implements IModPlugin {
  private static IIngredientManager ingedientManager;

  /** Cauldron contents ingredient type */
  public static final IIngredientType<ICauldronContents> CAULDRON_CONTENTS = () -> ICauldronContents.class;

  // Store which items can be hidden, and their current state.
  // This lets us reduce the work JEI tries to do.
  private static final List<HideState> HIDABLE_ITEMS = new ArrayList<>();

  // list of recipes, stored between register ingredients and register recipes
  private static List<ICauldronRecipeDisplay> recipes;

  @Override
  public ResourceLocation getPluginUid() {
    return Inspirations.getResource("jei");
  }

  /**
   * Gets a list of all cauldron recipes for display in JEI
   * @return  List of cauldron recipes
   */
  private static List<ICauldronRecipeDisplay> getCauldronRecipes() {
    assert Minecraft.getInstance().level != null;
    RecipeManager manager = Minecraft.getInstance().level.getRecipeManager();
    boolean isExtended = Config.extendedCauldron.get();
    Stream<? extends Recipe<?>> allRecipes = manager.byType(RecipeTypes.CAULDRON).values().stream();
    // combine in transform recipes if extended
    if (isExtended) {
      allRecipes = Stream.concat(allRecipes, manager.byType(RecipeTypes.CAULDRON_TRANSFORM).values().stream());
    }
    // sort recipes, and filter
    Stream<ICauldronRecipeDisplay> recipes = allRecipes
        .sorted((r1, r2) -> {
          boolean m1 = r1 instanceof IMultiRecipe;
          boolean m2 = r2 instanceof IMultiRecipe;
          if (m1 && !m2) return 1;
          return !m1 && m2 ? -1 : r1.getId().compareTo(r2.getId());
        })
        .flatMap((recipe) -> recipe instanceof IMultiRecipe ? ((IMultiRecipe<?>)recipe).getRecipes().stream() : Stream.of(recipe))
        .filter(recipe -> recipe instanceof ICauldronRecipeDisplay)
        .map(recipe -> (ICauldronRecipeDisplay) recipe)
        .filter(ICauldronRecipeDisplay::isSimple);
    // if simple, remove anything with an invalid input or output
    if (!isExtended) {
      recipes = recipes.filter(recipe -> recipe.getContentOutput().isSimple() && recipe.getContentInputs().stream().allMatch(ICauldronContents::isSimple));
    }
    // return collected recipes
    return recipes.collect(Collectors.toList());
  }

  @Override
  public void registerIngredients(IModIngredientRegistration registration) {
    if (Config.cauldronRecipes.getAsBoolean()) {
      // need the ingredient regardless, but will be empty if simple
      List<ICauldronContents> contents;
      if (Config.extendedCauldron.get()) {
        contents = new ArrayList<>();
        // first, add potions
        contents.addAll(ForgeRegistries.POTIONS.getValues().stream().map(CauldronContentTypes.POTION::of).toList());
        // next, dyes
        contents.addAll(Arrays.stream(DyeColor.values())
                              .map(CauldronContentTypes.DYE::of).toList());
        // finally custom, do this by scanning all recipe outputs
        recipes = getCauldronRecipes();
        contents.addAll(recipes.stream()
                               .map(ICauldronRecipeDisplay::getContentOutput)
                               .filter(c -> c.contains(CauldronContentTypes.CUSTOM))
                               .distinct().toList());
        // filter out any types cross registered as fluids or another type
        contents = contents.stream().filter(content -> !content.contains(CauldronContentTypes.FLUID)).distinct().collect(Collectors.toList());
      } else {
        contents = Collections.emptyList();
      }
      // register the ingredient
      registration.register(CAULDRON_CONTENTS, contents, CauldronContentHelper.INSTANCE, CauldronRenderer.LIST);
    }
  }

  @Override
  public void registerCategories(IRecipeCategoryRegistration registration) {
    if (Config.cauldronRecipes.getAsBoolean()) {
      registration.addRecipeCategories(new CauldronCategory(registration.getJeiHelpers().getGuiHelper()));
    }
  }

  @Override
  public void registerRecipes(IRecipeRegistration registration) {
    if (Config.cauldronRecipes.getAsBoolean()) {
      if (recipes == null) {
        recipes = getCauldronRecipes();
      }
      registration.addRecipes(recipes, CauldronCategory.ID);
      recipes = null;
    }
  }

  @Override
  public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
    if (Config.cauldronRecipes.getAsBoolean()) {
      registration.addRecipeCatalyst(new ItemStack(Blocks.CAULDRON), CauldronCategory.ID);
    }
  }

  @Override
  public void registerItemSubtypes(ISubtypeRegistration registry) {
    IIngredientSubtypeInterpreter<ItemStack> texture = (ingredient, content) -> RetexturedBlockItem.getTextureName(ingredient);
    Consumer<ItemLike> setTextureSubtype = item -> registry.registerSubtypeInterpreter(item.asItem(), texture);

    // building
    InspirationsBuilding.shelf.values().forEach(setTextureSubtype);
    InspirationsBuilding.enlightenedBush.values().forEach(setTextureSubtype);
  }

  @Override
  public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
    ingedientManager = jeiRuntime.getIngredientManager();
    HIDABLE_ITEMS.clear();
    for (ItemStack item : ingedientManager.getAllIngredients(VanillaTypes.ITEM)) {
      if (item.getItem() instanceof IHidable) {
        HIDABLE_ITEMS.add(new HideState(item));
      }
    }
    Config.setJEIUpdateRunnable(JEIPlugin::updateHiddenItems);
  }

  // Go through and hide/unhide Inspirations items whenever the config reloads.
  private static void updateHiddenItems() {
    // Only try to alter the state of items that have actually changed.
    List<ItemStack> hidden = new ArrayList<>();
    List<ItemStack> visible = new ArrayList<>();
    for (HideState state : HIDABLE_ITEMS) {
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


  /** Data object for state of a hidable object */
  private static class HideState {
    private final ItemStack stack;
    private boolean visible;
    private HideState(ItemStack item) {
      stack = item;
      visible = true;
    }
  }
}
