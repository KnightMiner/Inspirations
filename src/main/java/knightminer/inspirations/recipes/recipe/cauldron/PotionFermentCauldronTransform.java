package knightminer.inspirations.recipes.recipe.cauldron;

import com.google.gson.JsonObject;
import knightminer.inspirations.library.recipe.DynamicFinishedRecipe;
import knightminer.inspirations.library.recipe.RecipeSerializers;
import knightminer.inspirations.library.recipe.cauldron.CauldronContentTypes;
import knightminer.inspirations.library.recipe.cauldron.contents.ICauldronContents;
import knightminer.inspirations.library.recipe.cauldron.inventory.ICauldronState;
import knightminer.inspirations.library.recipe.cauldron.recipe.ICauldronTransform;
import knightminer.inspirations.library.recipe.cauldron.util.CauldronTemperature;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import slimeknights.mantle.recipe.helper.AbstractRecipeSerializer;

import javax.annotation.Nullable;

/**
 * Recipe to transform a potion from the "brewing" form to the usable potion
 */
public class PotionFermentCauldronTransform implements ICauldronTransform {
  private final ResourceLocation id;
  private final int time;

  public PotionFermentCauldronTransform(ResourceLocation id, int time) {
    this.id = id;
    this.time = time;
  }

  @Override
  public boolean matches(ICauldronState inv, Level worldIn) {
    return inv.getLevel() > 0 && inv.getTemperature() == CauldronTemperature.BOILING && inv.getContents().contains(CauldronContentTypes.UNFERMENTED_POTION);
  }

  @Override
  public int getTime() {
    return time;
  }

  @Override
  public ICauldronContents getContentOutput(ICauldronState inv) {
    return inv.getContents()
              .get(CauldronContentTypes.UNFERMENTED_POTION)
              .map(CauldronContentTypes.POTION::of)
              .orElseGet(CauldronContentTypes.DEFAULT);
  }

  @Override
  public ResourceLocation getId() {
    return id;
  }

  @Override
  public RecipeSerializer<?> getSerializer() {
    return RecipeSerializers.CAULDRON_POTION_FERMENT;
  }

  @Override
  public SoundEvent getSound() {
    return SoundEvents.BREWING_STAND_BREW;
  }

  /** Serializer for a potion transform */
  public static class Serializer extends AbstractRecipeSerializer<PotionFermentCauldronTransform> {
    @Override
    public PotionFermentCauldronTransform fromJson(ResourceLocation id, JsonObject json) {
      int time = GsonHelper.getAsInt(json, "time");
      return new PotionFermentCauldronTransform(id, time);
    }

    @Nullable
    @Override
    public PotionFermentCauldronTransform fromNetwork(ResourceLocation id, FriendlyByteBuf buffer) {
      return new PotionFermentCauldronTransform(id, buffer.readVarInt());
    }

    @Override
    public void toNetwork(FriendlyByteBuf buffer, PotionFermentCauldronTransform recipe) {
      buffer.writeVarInt(recipe.time);
    }
  }

  /** Finished recipe for datagen */
  public static class FinishedRecipe extends DynamicFinishedRecipe {
    private final int time;
    public FinishedRecipe(ResourceLocation id, int time) {
      super(id, RecipeSerializers.CAULDRON_POTION_FERMENT);
      this.time = time;
    }

    @Override
    public void serializeRecipeData(JsonObject json) {
      json.addProperty("time", time);
    }
  }
}
