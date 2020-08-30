package knightminer.inspirations.library.recipe.cauldron.recipe;

import com.google.gson.JsonObject;
import knightminer.inspirations.library.Util;
import knightminer.inspirations.library.recipe.RecipeSerializer;
import knightminer.inspirations.library.recipe.cauldron.CauldronContentTypes;
import knightminer.inspirations.library.recipe.cauldron.inventory.ICauldronInventory;
import knightminer.inspirations.library.recipe.cauldron.inventory.IModifyableCauldronInventory;
import knightminer.inspirations.recipes.InspirationsRecipes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtils;
import net.minecraft.potion.Potions;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import slimeknights.mantle.recipe.RecipeHelper;

import javax.annotation.Nullable;

public class EmptyPotionCauldronRecipe implements ICauldronRecipe {
  private final ResourceLocation id;
  private final Item potionItem;
  private final Item bottle;

  public EmptyPotionCauldronRecipe(ResourceLocation id, Item potion, Item bottle) {
    this.id = id;
    this.potionItem = potion;
    this.bottle = bottle;
  }

  @Override
  public boolean matches(ICauldronInventory inv, World worldIn) {
    // not too full
    if (inv.getLevel() == MAX) {
      return false;
    }
    // item must be a potion
    ItemStack stack = inv.getStack();
    if (stack.getItem() != potionItem) {
      return false;
    }
    Potion potion = PotionUtils.getPotionFromItem(stack);
    if (potion == Potions.EMPTY) {
      return false;
    }

    // contents must be empty or match the given potion
    return inv.getLevel() == 0 || inv.getContents()
                                     .get(CauldronContentTypes.POTION)
                                     .map(potion::equals)
                                     .orElse(false);
  }

  @Override
  public void handleRecipe(IModifyableCauldronInventory inv) {
    Potion potion = PotionUtils.getPotionFromItem(inv.getStack());
    if (potion != Potions.EMPTY) {
      // add levels
      inv.addLevel(1);
      // update contents
      inv.setContents(CauldronContentTypes.POTION.of(potion));

      // shrink stack
      inv.shrinkStack(1);
      inv.setOrGiveStack(new ItemStack(bottle));
    }
  }

  @Override
  public ResourceLocation getId() {
    return id;
  }

  @Override
  public ItemStack getRecipeOutput() {
    return new ItemStack(bottle);
  }

  @Override
  public IRecipeSerializer<?> getSerializer() {
    return InspirationsRecipes.emptyPotionSerializer;
  }

  public static class Serializer extends RecipeSerializer<EmptyPotionCauldronRecipe> {
    @Override
    public EmptyPotionCauldronRecipe read(ResourceLocation id, JsonObject json) {
      Item potion = Util.deserializeItem(json, "potion");
      Item bottle = Util.deserializeItem(json, "bottle");
      return new EmptyPotionCauldronRecipe(id, potion, bottle);
    }

    @Nullable
    @Override
    public EmptyPotionCauldronRecipe read(ResourceLocation id, PacketBuffer buffer) {
      Item potion = RecipeHelper.readItem(buffer);
      Item bottle = RecipeHelper.readItem(buffer);
      return new EmptyPotionCauldronRecipe(id, potion, bottle);
    }

    @Override
    public void write(PacketBuffer buffer, EmptyPotionCauldronRecipe recipe) {
      RecipeHelper.writeItem(buffer, recipe.potionItem);
      RecipeHelper.writeItem(buffer, recipe.bottle);
    }
  }
}
