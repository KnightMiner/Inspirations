package knightminer.inspirations.plugins.jei;

import knightminer.inspirations.Inspirations;
import knightminer.inspirations.building.InspirationsBuilding;
import knightminer.inspirations.common.IHidable;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.ingredients.subtypes.ISubtypeInterpreter;
import mezz.jei.api.registration.ISubtypeRegistration;
import mezz.jei.api.runtime.IIngredientManager;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import slimeknights.mantle.item.RetexturedBlockItem;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@JeiPlugin
public class JEIPlugin implements IModPlugin {
  private static IIngredientManager ingedientManager;

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
    return Inspirations.getResource("jei");
  }

  @Override
  public void registerItemSubtypes(ISubtypeRegistration registry) {
    ISubtypeInterpreter texture = RetexturedBlockItem::getTextureName;
    Consumer<IItemProvider> setTextureSubtype = item -> registry.registerSubtypeInterpreter(item.asItem(), texture);

    // building
    InspirationsBuilding.bookshelf.values().forEach(setTextureSubtype);
    InspirationsBuilding.enlightenedBush.values().forEach(setTextureSubtype);
  }

  @Override
  public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
    ingedientManager = jeiRuntime.getIngredientManager();

    hideableItems.clear();
    for (ItemStack item : ingedientManager.getAllIngredients(VanillaTypes.ITEM)) {
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
    for (HideState state : hideableItems) {
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
