package knightminer.inspirations.plugins.jei;

import knightminer.inspirations.Inspirations;
import knightminer.inspirations.building.InspirationsBuilding;
import knightminer.inspirations.common.Config;
import knightminer.inspirations.common.IHidable;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.ingredients.subtypes.IIngredientSubtypeInterpreter;
import mezz.jei.api.registration.ISubtypeRegistration;
import mezz.jei.api.runtime.IIngredientManager;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import slimeknights.mantle.item.RetexturedBlockItem;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@JeiPlugin
public class JEIPlugin implements IModPlugin {
  private static IIngredientManager ingedientManager;

  // Store which items can be hidden, and their current state.
  // This lets us reduce the work JEI tries to do.
  private static final List<HideState> HIDABLE_ITEMS = new ArrayList<>();

  @Override
  public ResourceLocation getPluginUid() {
    return Inspirations.getResource("jei");
  }

  @Override
  public void registerItemSubtypes(ISubtypeRegistration registry) {
    IIngredientSubtypeInterpreter<ItemStack> texture = (ingredient, content) -> RetexturedBlockItem.getTextureName(ingredient);
    Consumer<ItemLike> setTextureSubtype = item -> registry.registerSubtypeInterpreter(VanillaTypes.ITEM_STACK, item.asItem(), texture);

    // building
    InspirationsBuilding.shelf.values().forEach(setTextureSubtype);
    InspirationsBuilding.enlightenedBush.values().forEach(setTextureSubtype);
  }

  @Override
  public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
    ingedientManager = jeiRuntime.getIngredientManager();
    HIDABLE_ITEMS.clear();
    for (ItemStack item : ingedientManager.getAllIngredients(VanillaTypes.ITEM_STACK)) {
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
      ingedientManager.removeIngredientsAtRuntime(VanillaTypes.ITEM_STACK, hidden);
    }
    if (visible.size() > 0) {
      ingedientManager.addIngredientsAtRuntime(VanillaTypes.ITEM_STACK, visible);
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
