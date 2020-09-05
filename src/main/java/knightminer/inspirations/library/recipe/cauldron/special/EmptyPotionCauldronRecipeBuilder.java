package knightminer.inspirations.library.recipe.cauldron.special;

import com.google.gson.JsonObject;
import knightminer.inspirations.library.recipe.RecipeSerializers;
import net.minecraft.advancements.Advancement;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import slimeknights.mantle.recipe.data.AbstractRecipeBuilder;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Builder for a recipe that fills a bottle with a potion
 */
public class EmptyPotionCauldronRecipeBuilder extends AbstractRecipeBuilder<EmptyPotionCauldronRecipeBuilder> {
  private final Item bottle;
  private final Item potionItem;

  private EmptyPotionCauldronRecipeBuilder(Item potionItem, Item bottle) {
    this.potionItem = potionItem;
    this.bottle = bottle;
  }

  /**
   * Creates a new builder instance
   * @param potionItem  Potion item for input
   * @param bottle      Bottle output
   * @return  Builder instance
   */
  public static EmptyPotionCauldronRecipeBuilder empty(IItemProvider potionItem, IItemProvider bottle) {
    return new EmptyPotionCauldronRecipeBuilder(potionItem.asItem(), bottle.asItem());
  }

  @Override
  public void build(Consumer<IFinishedRecipe> consumer) {
    build(consumer, Objects.requireNonNull(bottle.getRegistryName()));
  }

  @Override
  public void build(Consumer<IFinishedRecipe> consumer, ResourceLocation id) {
    ResourceLocation advancementID = this.buildAdvancement(id, "cauldron");
    consumer.accept(new Result(id, potionItem, bottle, advancementBuilder, advancementID));
  }

  /** Recipe result to put in the consumer */
  private static class Result implements IFinishedRecipe {
    private final ResourceLocation id;
    private final Item potionItem;
    private final Item bottle;
    private final Advancement.Builder advancementBuilder;
    private final ResourceLocation advancementId;
    private Result(ResourceLocation id, Item potionItem, Item bottle, Advancement.Builder advancementBuilder, ResourceLocation advancementId) {
      this.id = id;
      this.potionItem = potionItem;
      this.bottle = bottle;
      this.advancementBuilder = advancementBuilder;
      this.advancementId = advancementId;
    }

    @Override
    public void serialize(JsonObject json) {
      json.addProperty("potion", Objects.requireNonNull(potionItem.getRegistryName()).toString());
      json.addProperty("bottle", Objects.requireNonNull(bottle.getRegistryName()).toString());
    }

    @Override
    public ResourceLocation getID() {
      return id;
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
      return RecipeSerializers.CAULDRON_EMPTY_POTION;
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
