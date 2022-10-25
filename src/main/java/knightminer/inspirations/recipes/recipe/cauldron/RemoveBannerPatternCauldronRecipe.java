package knightminer.inspirations.recipes.recipe.cauldron;

import knightminer.inspirations.library.recipe.RecipeSerializers;
import knightminer.inspirations.library.recipe.cauldron.CauldronContentTypes;
import knightminer.inspirations.library.recipe.cauldron.inventory.ICauldronInventory;
import knightminer.inspirations.library.recipe.cauldron.inventory.IModifyableCauldronInventory;
import knightminer.inspirations.library.recipe.cauldron.recipe.ICauldronRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BannerBlockEntity;
import net.minecraft.world.level.material.Fluids;

/**
 * Cauldron recipe to remove banner patterns
 */
public class RemoveBannerPatternCauldronRecipe implements ICauldronRecipe {
  private final ResourceLocation id;
  public RemoveBannerPatternCauldronRecipe(ResourceLocation id) {
    this.id = id;
  }

  @Override
  public boolean matches(ICauldronInventory inv, Level worldIn) {
    ItemStack stack = inv.getStack();
    // must be at least one level of water, be a banner, and have patterns
    return inv.getLevel() >= THIRD && inv.getContents().contains(CauldronContentTypes.FLUID, Fluids.WATER)
           && stack.is(ItemTags.BANNERS)
           && BannerBlockEntity.getPatternCount(stack) > 0;
  }

  @Override
  public void handleRecipe(IModifyableCauldronInventory inv) {
    // remove patterns
    ItemStack stack = inv.splitStack(1);
    BannerBlockEntity.removeLastPattern(stack);
    inv.setOrGiveStack(stack);
    // use one level of water
    inv.addLevel(-THIRD);

    // play sound
    inv.playSound(SoundEvents.FISHING_BOBBER_SPLASH);
  }

  @Override
  public ResourceLocation getId() {
    return id;
  }

  @Override
  public RecipeSerializer<?> getSerializer() {
    return RecipeSerializers.CAULDRON_REMOVE_BANNER_PATTERN;
  }
}
