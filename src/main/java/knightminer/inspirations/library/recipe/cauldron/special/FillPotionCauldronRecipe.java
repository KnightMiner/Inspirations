package knightminer.inspirations.library.recipe.cauldron.special;

import com.google.gson.JsonObject;
import knightminer.inspirations.library.MiscUtil;
import knightminer.inspirations.library.recipe.DynamicFinishedRecipe;
import knightminer.inspirations.library.recipe.RecipeSerializers;
import knightminer.inspirations.library.recipe.cauldron.CauldronContentTypes;
import knightminer.inspirations.library.recipe.cauldron.inventory.ICauldronInventory;
import knightminer.inspirations.library.recipe.cauldron.inventory.IModifyableCauldronInventory;
import knightminer.inspirations.library.recipe.cauldron.recipe.ICauldronRecipe;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import slimeknights.mantle.recipe.helper.AbstractRecipeSerializer;
import slimeknights.mantle.recipe.helper.RecipeHelper;
import slimeknights.mantle.recipe.ingredient.SizedIngredient;

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
  public boolean matches(ICauldronInventory inv, Level world) {
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
  public RecipeSerializer<?> getSerializer() {
    return RecipeSerializers.CAULDRON_FILL_POTION;
  }

  public static class Serializer extends AbstractRecipeSerializer<FillPotionCauldronRecipe> {
    @Override
    public FillPotionCauldronRecipe fromJson(ResourceLocation id, JsonObject json) {
      SizedIngredient bottle = SizedIngredient.deserialize(GsonHelper.getAsJsonObject(json, "bottle"));
      Item potion = MiscUtil.deserializeItem(json, "potion");
      return new FillPotionCauldronRecipe(id, bottle, potion);
    }

    @Nullable
    @Override
    public FillPotionCauldronRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buffer) {
      SizedIngredient bottle = SizedIngredient.read(buffer);
      Item potion = RecipeHelper.readItem(buffer);
      return new FillPotionCauldronRecipe(id, bottle, potion);
    }

    @Override
    public void toNetwork(FriendlyByteBuf buffer, FillPotionCauldronRecipe recipe) {
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
