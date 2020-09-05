package knightminer.inspirations.library.recipe.cauldron.special;

import com.google.gson.JsonObject;
import knightminer.inspirations.library.Util;
import knightminer.inspirations.library.recipe.RecipeSerializer;
import knightminer.inspirations.library.recipe.RecipeSerializers;
import knightminer.inspirations.library.recipe.cauldron.CauldronContentTypes;
import knightminer.inspirations.library.recipe.cauldron.inventory.ICauldronInventory;
import knightminer.inspirations.library.recipe.cauldron.inventory.IModifyableCauldronInventory;
import knightminer.inspirations.library.recipe.cauldron.recipe.ICauldronRecipe;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;
import net.minecraftforge.common.crafting.CraftingHelper;
import slimeknights.mantle.recipe.RecipeHelper;
import slimeknights.mantle.util.JsonHelper;

import javax.annotation.Nullable;

/**
 * Recipe to fill a potion bottle using a cauldron
 */
public class FillPotionCauldronRecipe implements ICauldronRecipe {
  private final ResourceLocation id;
  private final Ingredient bottle;
  private final int amount;
  private final Item potionItem;

  /**
   * Creates a new potion fill recipe
   * @param id          Recipe ID
   * @param bottle      Potion bottle
   * @param amount      Number of the bottle needed and count of the output
   * @param potionItem  Item to output
   */
  public FillPotionCauldronRecipe(ResourceLocation id, Ingredient bottle, int amount, Item potionItem) {
    this.id = id;
    this.potionItem = potionItem;
    this.amount = amount;
    this.bottle = bottle;
  }

  @Override
  public boolean matches(ICauldronInventory inv, World world) {
    // must have at least one level, contain any potion, and be using the correct item
    ItemStack stack = inv.getStack();
    return inv.getLevel() > 0 && inv.getContents().contains(CauldronContentTypes.POTION) && stack.getCount() >= amount && bottle.test(stack);
  }

  @Override
  public void handleRecipe(IModifyableCauldronInventory inv) {
    inv.getContents().get(CauldronContentTypes.POTION).ifPresent(potion -> {
      // give player potion, removing a bottle
      inv.shrinkStack(amount);
      inv.setOrGiveStack(PotionUtils.addPotionToItemStack(new ItemStack(potionItem, amount), potion));

      // update level
      inv.addLevel(-1);

      // play sound
      inv.playSound(SoundEvents.ITEM_BOTTLE_FILL);
    });
  }

  @Override
  public ResourceLocation getId() {
    return id;
  }

  @Override
  public IRecipeSerializer<?> getSerializer() {
    return RecipeSerializers.CAULDRON_FILL_POTION;
  }

  public static class Serializer extends RecipeSerializer<FillPotionCauldronRecipe> {
    @Override
    public FillPotionCauldronRecipe read(ResourceLocation id, JsonObject json) {
      Ingredient bottle = CraftingHelper.getIngredient(JsonHelper.getElement(json, "bottle"));
      int amount = JSONUtils.getInt(json, "amount", 1);
      Item potion = Util.deserializeItem(json, "potion");
      return new FillPotionCauldronRecipe(id, bottle, amount, potion);
    }

    @Nullable
    @Override
    public FillPotionCauldronRecipe read(ResourceLocation id, PacketBuffer buffer) {
      Ingredient bottle = Ingredient.read(buffer);
      int amount = buffer.readVarInt();
      Item potion = RecipeHelper.readItem(buffer);
      return new FillPotionCauldronRecipe(id, bottle, amount, potion);
    }

    @Override
    public void write(PacketBuffer buffer, FillPotionCauldronRecipe recipe) {
      recipe.bottle.write(buffer);
      buffer.writeVarInt(recipe.amount);
      RecipeHelper.writeItem(buffer, recipe.potionItem);
    }
  }
}
