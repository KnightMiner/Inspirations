package knightminer.inspirations.recipes.recipe.cauldron;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import knightminer.inspirations.library.Util;
import knightminer.inspirations.library.recipe.RecipeSerializer;
import knightminer.inspirations.library.recipe.cauldron.CauldronContentTypes;
import knightminer.inspirations.library.recipe.cauldron.contents.EmptyCauldronContents;
import knightminer.inspirations.library.recipe.cauldron.contents.ICauldronContents;
import knightminer.inspirations.library.recipe.cauldron.inventory.ICauldronInventory;
import knightminer.inspirations.library.recipe.cauldron.inventory.IModifyableCauldronInventory;
import knightminer.inspirations.library.recipe.cauldron.recipe.ICauldronRecipe;
import knightminer.inspirations.recipes.InspirationsRecipes;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import slimeknights.mantle.util.JsonHelper;

import javax.annotation.Nullable;
import java.util.Arrays;

/**
 * Cauldron recipe that takes a liquid container filled with dye and increases the cauldron liquid
 */
public class MixCauldronDyeRecipe implements ICauldronRecipe {
  private final ResourceLocation id;
  private final Ingredient ingredient;
  @Nullable
  private final Integer color;

  public MixCauldronDyeRecipe(ResourceLocation id, Ingredient ingredient, @Nullable Integer color) {
    this.id = id;
    this.ingredient = ingredient;
    this.color = color;
  }

  @Override
  public boolean matches(ICauldronInventory inv, World worldIn) {
    // must have space
    int level = inv.getLevel();
    if (level == MAX) {
      return false;
    }
    // must have color, must match ingredient
    ICauldronContents contents = inv.getContents();
    return (level == 0 || contents.contains(CauldronContentTypes.FLUID, Fluids.WATER) || contents.contains(CauldronContentTypes.COLOR))
           && ingredient.test(inv.getStack());
  }


  @Override
  public void handleRecipe(IModifyableCauldronInventory inv) {
    // can set the color to null to read color from NBT
    int newColor;
    if (this.color == null) {
      newColor = Util.getColor(inv.getStack());
    } else {
      newColor = this.color;
    }

    // if water, set the color directly
    ICauldronContents contents = inv.getContents();
    ItemStack container = inv.getStack().getContainerItem();
    if (contents == EmptyCauldronContents.INSTANCE || contents.contains(CauldronContentTypes.FLUID, Fluids.WATER)) {
      // update dye stack and return container
      inv.shrinkStack(1);
      inv.giveStack(container.copy());

      // mix in a neutral grey for the water, its not a free dye dupe
      int[] existing = new int[inv.getLevel()];
      Arrays.fill(existing, 0x808080);

      // update contents
      inv.addLevel(1);
      inv.setContents(CauldronContentTypes.COLOR.of(DyeCauldronWaterRecipe.addColors(newColor, existing)));
    } else {
      contents.get(CauldronContentTypes.COLOR).ifPresent(color -> {
        // update dye stack and return container
        inv.shrinkStack(1);
        inv.giveStack(container.copy());

        // add one copy of the existing color for each level
        int[] existing = new int[inv.getLevel()];
        Arrays.fill(existing, color);

        // set contents
        inv.addLevel(1);
        inv.setContents(CauldronContentTypes.COLOR.of(DyeCauldronWaterRecipe.addColors(newColor, existing)));
      });
    }
  }

  @Override
  public ItemStack getRecipeOutput() {
    return ItemStack.EMPTY;
  }

  @Override
  public ResourceLocation getId() {
    return id;
  }

  @Override
  public IRecipeSerializer<?> getSerializer() {
    return InspirationsRecipes.mixCauldronDyeSerializer;
  }

  public static class Serializer extends RecipeSerializer<MixCauldronDyeRecipe> {
    @SuppressWarnings("ConstantConditions")
    @Override
    public MixCauldronDyeRecipe read(ResourceLocation id, JsonObject json) {
      Ingredient ingredient = Ingredient.deserialize(JsonHelper.getElement(json, "ingredient"));

      // if color is defined, parse it
      Integer color = null;
      if (json.has("color")) {
        // try dye color name first
        String colorText = JSONUtils.getString(json, "color");
        DyeColor dye = DyeColor.byTranslationKey(colorText, null);
        if (dye != null) {
          color = dye.getColorValue();
        } else {
          // hexadecimal color next
          try {
            color = Integer.parseInt(JSONUtils.getString(json, "color"), 16);
          } catch (NumberFormatException e) {
            throw new JsonSyntaxException("Invalid color string '" + colorText + "'");
          }
        }
      }

      return new MixCauldronDyeRecipe(id, ingredient, color);
    }

    @Nullable
    @Override
    public MixCauldronDyeRecipe read(ResourceLocation id, PacketBuffer buffer) {
      Ingredient ingredient = Ingredient.read(buffer);
      Integer color = null;
      if (buffer.readBoolean()) {
        color = buffer.readInt();
      }
      return new MixCauldronDyeRecipe(id, ingredient, color);
    }

    @Override
    public void write(PacketBuffer buffer, MixCauldronDyeRecipe recipe) {
      recipe.ingredient.write(buffer);
      if (recipe.color == null) {
        buffer.writeBoolean(false);
      } else {
        buffer.writeBoolean(true);
        buffer.writeInt(recipe.color);
      }
    }
  }

  /**
   * Finished recipe class for datagen
   */
  public static class FinishedRecipe implements IFinishedRecipe {
    private final ResourceLocation id;
    private final Ingredient ingredient;
    @Nullable
    private final Integer color;

    /**
     * Creates a new recipe instance using the specified color
     * @param id          Recipe ID
     * @param ingredient  Recipe ingredient
     * @param color       Recipe color
     */
    public FinishedRecipe(ResourceLocation id, Ingredient ingredient, Integer color) {
      this.id = id;
      this.ingredient = ingredient;
      this.color = color;
    }

    /**
     * Creates a new recipe instance using NBT dye color
     * @param id          Recipe ID
     * @param ingredient  Recipe ingredient
     */
    public FinishedRecipe(ResourceLocation id, Ingredient ingredient) {
      this.id = id;
      this.ingredient = ingredient;
      this.color = null;
    }

    @Override
    public void serialize(JsonObject json) {
      json.add("ingredient", ingredient.serialize());
      if (color != null) {
        json.addProperty("color", Integer.toHexString(color));
      }
    }

    @Override
    public ResourceLocation getID() {
      return id;
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
      return InspirationsRecipes.mixCauldronDyeSerializer;
    }

    @Nullable
    @Override
    public JsonObject getAdvancementJson() {
      return null;
    }

    @Nullable
    @Override
    public ResourceLocation getAdvancementID() {
      return null;
    }
  }
}
