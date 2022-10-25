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
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import slimeknights.mantle.recipe.helper.AbstractRecipeSerializer;
import slimeknights.mantle.recipe.helper.RecipeHelper;

import javax.annotation.Nullable;
import java.util.Objects;

/**
 * Recipe that drains a potion into the cauldron
 */
public class EmptyPotionCauldronRecipe implements ICauldronRecipe {
  private final ResourceLocation id;
  private final Item potionItem;
  private final Item bottle;

  /**
   * Creates a new instance
   * @param id      Recipe ID
   * @param potion  Potion item to drain
   * @param bottle  Potion bottle
   */
  public EmptyPotionCauldronRecipe(ResourceLocation id, Item potion, Item bottle) {
    this.id = id;
    this.potionItem = potion;
    this.bottle = bottle;
  }

  @Override
  public boolean matches(ICauldronInventory inv, Level worldIn) {
    // not too full
    if (inv.getLevel() == MAX) {
      return false;
    }
    // item must be a potion
    ItemStack stack = inv.getStack();
    if (stack.getItem() != potionItem) {
      return false;
    }
    Potion potion = PotionUtils.getPotion(stack);
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
    Potion potion = PotionUtils.getPotion(inv.getStack());
    if (potion != Potions.EMPTY) {
      // add levels
      inv.addLevel(THIRD);
      // update contents
      inv.setContents(CauldronContentTypes.POTION.of(potion));

      // shrink stack
      inv.shrinkStack(1);
      inv.setOrGiveStack(new ItemStack(bottle));

      // play sound
      inv.playSound(SoundEvents.BOTTLE_EMPTY);
    }
  }

  @Override
  public ResourceLocation getId() {
    return id;
  }

  @Override
  public RecipeSerializer<?> getSerializer() {
    return RecipeSerializers.CAULDRON_EMPTY_POTION;
  }

  public static class Serializer extends AbstractRecipeSerializer<EmptyPotionCauldronRecipe> {
    @Override
    public EmptyPotionCauldronRecipe fromJson(ResourceLocation id, JsonObject json) {
      Item potion = MiscUtil.deserializeItem(json, "potion");
      Item bottle = MiscUtil.deserializeItem(json, "bottle");
      return new EmptyPotionCauldronRecipe(id, potion, bottle);
    }

    @Nullable
    @Override
    public EmptyPotionCauldronRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buffer) {
      Item potion = RecipeHelper.readItem(buffer);
      Item bottle = RecipeHelper.readItem(buffer);
      return new EmptyPotionCauldronRecipe(id, potion, bottle);
    }

    @Override
    public void toNetwork(FriendlyByteBuf buffer, EmptyPotionCauldronRecipe recipe) {
      RecipeHelper.writeItem(buffer, recipe.potionItem);
      RecipeHelper.writeItem(buffer, recipe.bottle);
    }
  }

  /** Finished recipe for datagen */
  public static class FinishedRecipe extends DynamicFinishedRecipe {
    private final Item potionItem;
    private final Item bottle;
    public FinishedRecipe(ResourceLocation id, Item potionItem, Item bottle) {
      super(id, RecipeSerializers.CAULDRON_EMPTY_POTION);
      this.potionItem = potionItem;
      this.bottle = bottle;
    }

    @Override
    public void serializeRecipeData(JsonObject json) {
      json.addProperty("potion", Objects.requireNonNull(potionItem.getRegistryName()).toString());
      json.addProperty("bottle", Objects.requireNonNull(bottle.getRegistryName()).toString());
    }
  }
}
