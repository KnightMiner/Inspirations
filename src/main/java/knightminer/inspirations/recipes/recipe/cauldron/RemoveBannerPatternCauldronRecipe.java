package knightminer.inspirations.recipes.recipe.cauldron;

import knightminer.inspirations.library.recipe.RecipeSerializers;
import knightminer.inspirations.library.recipe.cauldron.CauldronContentTypes;
import knightminer.inspirations.library.recipe.cauldron.inventory.ICauldronInventory;
import knightminer.inspirations.library.recipe.cauldron.inventory.IModifyableCauldronInventory;
import knightminer.inspirations.library.recipe.cauldron.recipe.ICauldronRecipe;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.tags.ItemTags;
import net.minecraft.tileentity.BannerTileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;

/**
 * Cauldron recipe to remove banner patterns
 */
public class RemoveBannerPatternCauldronRecipe implements ICauldronRecipe {
  private final ResourceLocation id;
  public RemoveBannerPatternCauldronRecipe(ResourceLocation id) {
    this.id = id;
  }

  @Override
  public boolean matches(ICauldronInventory inv, World worldIn) {
    ItemStack stack = inv.getStack();
    // must be at least one level of water, be a banner, and have patterns
    return inv.getLevel() >= THIRD && inv.getContents().contains(CauldronContentTypes.FLUID, Fluids.WATER)
           && ItemTags.BANNERS.contains(stack.getItem())
           && BannerTileEntity.getPatterns(stack) > 0;
  }

  @Override
  public void handleRecipe(IModifyableCauldronInventory inv) {
    // remove patterns
    ItemStack stack = inv.getStack().split(1);
    BannerTileEntity.removeBannerData(stack);
    inv.setOrGiveStack(stack);
    // use one level of water
    inv.addLevel(-THIRD);

    // play sound
    inv.playSound(SoundEvents.ENTITY_FISHING_BOBBER_SPLASH);
  }

  @Override
  public ResourceLocation getId() {
    return id;
  }

  @Override
  public IRecipeSerializer<?> getSerializer() {
    return RecipeSerializers.CAULDRON_REMOVE_BANNER_PATTERN;
  }
}
