package knightminer.inspirations.library.recipe.cauldron.special;

import com.google.gson.JsonObject;
import knightminer.inspirations.library.Util;
import knightminer.inspirations.library.recipe.DynamicFinishedRecipe;
import knightminer.inspirations.library.recipe.RecipeSerializer;
import knightminer.inspirations.library.recipe.RecipeSerializers;
import knightminer.inspirations.library.recipe.cauldron.CauldronContentTypes;
import knightminer.inspirations.library.recipe.cauldron.inventory.ICauldronInventory;
import knightminer.inspirations.library.recipe.cauldron.inventory.IModifyableCauldronInventory;
import knightminer.inspirations.library.recipe.cauldron.recipe.ICauldronRecipe;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;
import slimeknights.mantle.recipe.RecipeHelper;
import slimeknights.mantle.recipe.SizedIngredient;

import javax.annotation.Nullable;
import java.util.Objects;

/**
 * Recipe to fill a potion bottle using a cauldron
 */
public class FillPotionCauldronRecipe implements ICauldronRecipe {
  private final ResourceLocation id;
  private final SizedIngredient bottle;
  private final Item potionItem;

  /**
   * Creates a new potion fill recipe
   * @param id          Recipe ID
   * @param bottle      Potion bottle
   * @param potionItem  Item to output
   */
  public FillPotionCauldronRecipe(ResourceLocation id, SizedIngredient bottle, Item potionItem) {
    this.id = id;
    this.potionItem = potionItem;
    this.bottle = bottle;
  }

  @Override
  public boolean matches(ICauldronInventory inv, World world) {
    // must have at least one level, contain any potion, and be using the correct item
    return inv.getLevel() >= THIRD && inv.getContents().contains(CauldronContentTypes.POTION) && bottle.test(inv.getStack());
  }

  @Override
  public void handleRecipe(IModifyableCauldronInventory inv) {
    inv.getContents().get(CauldronContentTypes.POTION).ifPresent(potion -> {
      // give player potion, removing a bottle
      int amount = bottle.getAmountNeeded();
      inv.shrinkStack(amount);
      inv.setOrGiveStack(PotionUtils.setPotion(new ItemStack(potionItem, amount), potion));

      // update level
      inv.addLevel(-THIRD);

      // play sound
      inv.playSound(SoundEvents.BOTTLE_FILL);
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
    public FillPotionCauldronRecipe fromJson(ResourceLocation id, JsonObject json) {
      SizedIngredient bottle = SizedIngredient.deserialize(JSONUtils.getAsJsonObject(json, "bottle"));
      Item potion = Util.deserializeItem(json, "potion");
      return new FillPotionCauldronRecipe(id, bottle, potion);
    }

    @Nullable
    @Override
    public FillPotionCauldronRecipe fromNetwork(ResourceLocation id, PacketBuffer buffer) {
      SizedIngredient bottle = SizedIngredient.read(buffer);
      Item potion = RecipeHelper.readItem(buffer);
      return new FillPotionCauldronRecipe(id, bottle, potion);
    }

    @Override
    public void toNetwork(PacketBuffer buffer, FillPotionCauldronRecipe recipe) {
      recipe.bottle.write(buffer);
      RecipeHelper.writeItem(buffer, recipe.potionItem);
    }
  }

  /** Finished recipe for datagen */
  public static class FinishedRecipe extends DynamicFinishedRecipe {
    private final SizedIngredient bottle;
    private final Item potionItem;
    public FinishedRecipe(ResourceLocation id, SizedIngredient bottle, Item potionItem) {
      super(id, RecipeSerializers.CAULDRON_FILL_POTION);
      this.bottle = bottle;
      this.potionItem = potionItem;
    }

    @Override
    public void serializeRecipeData(JsonObject json) {
      json.add("bottle", bottle.serialize());
      json.addProperty("potion", Objects.requireNonNull(potionItem.getRegistryName()).toString());
    }
  }
}
