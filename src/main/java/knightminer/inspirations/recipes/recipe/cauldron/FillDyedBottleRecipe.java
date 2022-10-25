package knightminer.inspirations.recipes.recipe.cauldron;

import knightminer.inspirations.library.recipe.RecipeSerializers;
import knightminer.inspirations.library.recipe.cauldron.CauldronContentTypes;
import knightminer.inspirations.library.recipe.cauldron.inventory.ICauldronInventory;
import knightminer.inspirations.library.recipe.cauldron.inventory.IModifyableCauldronInventory;
import knightminer.inspirations.library.recipe.cauldron.recipe.ICauldronRecipe;
import knightminer.inspirations.library.recipe.cauldron.util.DisplayCauldronRecipe;
import knightminer.inspirations.recipes.InspirationsRecipes;
import knightminer.inspirations.recipes.item.MixedDyedBottleItem;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import slimeknights.mantle.recipe.IMultiRecipe;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Recipe to fill a dyed bottle from cauldron water
 */
public class FillDyedBottleRecipe implements ICauldronRecipe, IMultiRecipe<DisplayCauldronRecipe> {
  private final ResourceLocation id;
  private List<DisplayCauldronRecipe> displayRecipes;
  public FillDyedBottleRecipe(ResourceLocation id) {
    this.id = id;
  }

  @Override
  public boolean matches(ICauldronInventory inv, Level worldIn) {
    return inv.getLevel() >= THIRD && inv.getStack().getItem() == Items.GLASS_BOTTLE && inv.getContents().contains(CauldronContentTypes.COLOR);
  }

  @Override
  public void handleRecipe(IModifyableCauldronInventory inventory) {
    inventory.getContents().get(CauldronContentTypes.COLOR).ifPresent(color -> {
      inventory.shrinkStack(1);
      inventory.setOrGiveStack(MixedDyedBottleItem.bottleFromDye(color));
      inventory.addLevel(-THIRD);

      // play sound
      inventory.playSound(SoundEvents.BOTTLE_FILL);
    });
  }

  @Override
  public ResourceLocation getId() {
    return id;
  }

  @Override
  public RecipeSerializer<?> getSerializer() {
    return RecipeSerializers.CAULDRON_FILL_DYED_BOTTLE;
  }

  @Override
  public List<DisplayCauldronRecipe> getRecipes() {
    if (displayRecipes == null) {
      // recipe is glass bottle + dye = dyed bottle
      List<ItemStack> glassBottle = Collections.singletonList(new ItemStack(Items.GLASS_BOTTLE));
      displayRecipes = Arrays.stream(DyeColor.values())
                             .map(color -> DisplayCauldronRecipe.builder(THIRD, 0)
                                                                .setItemInputs(glassBottle)
                                                                .setContentInputs(CauldronContentTypes.DYE.of(color))
                                                                .setItemOutput(InspirationsRecipes.simpleDyedWaterBottle.get(color))
                                                                .build())
                             .collect(Collectors.toList());
    }
    return displayRecipes;
  }
}
