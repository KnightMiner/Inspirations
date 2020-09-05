package knightminer.inspirations.library.recipe.cauldron.special;

import com.google.gson.JsonObject;
import knightminer.inspirations.recipes.InspirationsRecipes;
import net.minecraft.advancements.Advancement;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import slimeknights.mantle.recipe.data.AbstractRecipeBuilder;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Builder for a recipe that fills a bottle with a potion
 */
public class FillPotionCauldronRecipeBuilder extends AbstractRecipeBuilder<FillPotionCauldronRecipeBuilder> {
  private final Ingredient bottle;
  private final int amount;
  private final Item potionItem;

  private FillPotionCauldronRecipeBuilder(Ingredient bottle, int amount, Item potionItem) {
    this.bottle = bottle;
    this.amount = amount;
    this.potionItem = potionItem;
  }

  /**
   * Creates a new builder with a specific amount
   * @param bottle      Bottle ingredient
   * @param amount      Amount needed and crafted
   * @param potionItem  Potion item for output
   * @return  Builder instance
   */
  public static FillPotionCauldronRecipeBuilder fill(Ingredient bottle, int amount, IItemProvider potionItem) {
    return new FillPotionCauldronRecipeBuilder(bottle, amount, potionItem.asItem());
  }

  /**
   * Creates a new builder with an amount of 1
   * @param bottle      Bottle ingredient
   * @param potionItem  Potion item for output
   * @return  Builder instance
   */
  public static FillPotionCauldronRecipeBuilder fill(Ingredient bottle, IItemProvider potionItem) {
    return fill(bottle, 1, potionItem);
  }

  @Override
  public void build(Consumer<IFinishedRecipe> consumer) {
    build(consumer, Objects.requireNonNull(potionItem.getRegistryName()));
  }

  @Override
  public void build(Consumer<IFinishedRecipe> consumer, ResourceLocation id) {
    if (amount < 1) {
      throw new IllegalStateException("Amount must be at least 1");
    }
    ResourceLocation advancementID = this.buildAdvancement(id, "cauldron");
    consumer.accept(new Result(id, bottle, amount, potionItem, advancementBuilder, advancementID));
  }

  private static class Result implements IFinishedRecipe {
    private final ResourceLocation id;
    private final Ingredient bottle;
    private final int amount;
    private final Item potionItem;
    private final Advancement.Builder advancementBuilder;
    private final ResourceLocation advancementId;
    private Result(ResourceLocation id, Ingredient bottle, int amount, Item potionItem, Advancement.Builder advancementBuilder, ResourceLocation advancementId) {
      this.id = id;
      this.bottle = bottle;
      this.amount = amount;
      this.potionItem = potionItem;
      this.advancementBuilder = advancementBuilder;
      this.advancementId = advancementId;
    }

    @Override
    public void serialize(JsonObject json) {
      json.add("bottle", bottle.serialize());
      if (amount != 1) {
        json.addProperty("amount", amount);
      }
      json.addProperty("potion", Objects.requireNonNull(potionItem.getRegistryName()).toString());
    }

    @Override
    public ResourceLocation getID() {
      return id;
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
      return InspirationsRecipes.fillPotionSerializer;
    }

    @Nullable
    @Override
    public ResourceLocation getAdvancementID() {
      return advancementId;
    }

    @Nullable
    @Override
    public JsonObject getAdvancementJson() {
      return advancementBuilder.serialize();
    }
  }
}
