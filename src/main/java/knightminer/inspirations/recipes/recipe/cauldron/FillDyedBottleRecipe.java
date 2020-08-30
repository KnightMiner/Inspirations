package knightminer.inspirations.recipes.recipe.cauldron;

import knightminer.inspirations.library.recipe.cauldron.CauldronContentTypes;
import knightminer.inspirations.library.recipe.cauldron.inventory.ICauldronInventory;
import knightminer.inspirations.library.recipe.cauldron.inventory.IModifyableCauldronInventory;
import knightminer.inspirations.library.recipe.cauldron.recipe.ICauldronRecipe;
import knightminer.inspirations.recipes.InspirationsRecipes;
import knightminer.inspirations.recipes.item.MixedDyedBottleItem;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

/**
 * Recipe to fill a dyed bottle from cauldron water
 */
public class FillDyedBottleRecipe implements ICauldronRecipe {
  private final ResourceLocation id;
  public FillDyedBottleRecipe(ResourceLocation id) {
    this.id = id;
  }

  @Override
  public boolean matches(ICauldronInventory inv, World worldIn) {
    return inv.getLevel() > 0 && inv.getStack().getItem() == Items.GLASS_BOTTLE && inv.getContents().contains(CauldronContentTypes.COLOR);
  }

  @Override
  public void handleRecipe(IModifyableCauldronInventory inventory) {
    inventory.getContents().get(CauldronContentTypes.COLOR).ifPresent(color -> {
      inventory.shrinkStack(1);
      inventory.setOrGiveStack(MixedDyedBottleItem.bottleFromDye(color));
      inventory.addLevel(-1);
    });
  }

  /**
   * @deprecated Use {@link #getCraftingResult(IInventory)}
   */
  @Deprecated
  @Override
  public ItemStack getRecipeOutput() {
    return new ItemStack(InspirationsRecipes.mixedDyedWaterBottle);
  }

  @Override
  public ItemStack getCraftingResult(ICauldronInventory inv) {
    return inv.getContents()
              .get(CauldronContentTypes.COLOR)
              .map(MixedDyedBottleItem::bottleFromDye)
              .orElseGet(() -> new ItemStack(Items.GLASS_BOTTLE));
  }

  @Override
  public ResourceLocation getId() {
    return id;
  }

  @Override
  public IRecipeSerializer<?> getSerializer() {
    return InspirationsRecipes.fillDyedBottleSerializer;
  }
}
