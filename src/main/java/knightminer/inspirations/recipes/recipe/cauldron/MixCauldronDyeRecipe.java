package knightminer.inspirations.recipes.recipe.cauldron;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import knightminer.inspirations.library.Util;
import knightminer.inspirations.library.recipe.DynamicFinishedRecipe;
import knightminer.inspirations.library.recipe.RecipeSerializer;
import knightminer.inspirations.library.recipe.RecipeSerializers;
import knightminer.inspirations.library.recipe.cauldron.CauldronContentTypes;
import knightminer.inspirations.library.recipe.cauldron.contents.ICauldronContents;
import knightminer.inspirations.library.recipe.cauldron.inventory.ICauldronInventory;
import knightminer.inspirations.library.recipe.cauldron.inventory.IModifyableCauldronInventory;
import knightminer.inspirations.library.recipe.cauldron.recipe.ICauldronRecipe;
import knightminer.inspirations.library.recipe.cauldron.recipe.ICauldronRecipeDisplay;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;
import slimeknights.mantle.util.JsonHelper;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Cauldron recipe that takes a liquid container filled with dye and increases the cauldron liquid
 */
public class MixCauldronDyeRecipe implements ICauldronRecipe, ICauldronRecipeDisplay {
  private final ResourceLocation id;
  private final Ingredient ingredient;
  @Nullable
  private final Integer color;

  // display
  private List<ItemStack> inputDisplay;
  private ItemStack outputDisplay;
  private ICauldronContents outputContents;

  /**
   * Creates a new recipe instance
   * @param id          Recipe ID
   * @param ingredient  Input ingredient
   * @param color       Ingredient color
   */
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
    if (inv.getLevel() == 0 || contents.contains(CauldronContentTypes.FLUID, Fluids.WATER)) {
      // update dye stack and return container
      inv.shrinkStack(1);
      inv.giveStack(container.copy());

      // update contents
      inv.addLevel(THIRD);

      // mix in a neutral grey for the water, its not a free dye dupe
      inv.setContents(CauldronContentTypes.COLOR.of(DyeCauldronWaterRecipe.addColors(newColor, THIRD, 0x808080, inv.getLevel())));

      // play sound
      inv.playSound(SoundEvents.ITEM_BOTTLE_EMPTY);
    } else {
      contents.get(CauldronContentTypes.COLOR).ifPresent(color -> {
        // update dye stack and return container
        inv.shrinkStack(1);
        inv.giveStack(container.copy());

        // set contents
        inv.addLevel(THIRD);

        // mix color with amount existing
        inv.setContents(CauldronContentTypes.COLOR.of(DyeCauldronWaterRecipe.addColors(newColor, THIRD, color, inv.getLevel())));

        // play sound
        inv.playSound(SoundEvents.ITEM_BOTTLE_EMPTY);
      });
    }
  }

  @Override
  public ResourceLocation getId() {
    return id;
  }

  @Override
  public IRecipeSerializer<?> getSerializer() {
    return RecipeSerializers.CAULDRON_MIX_DYE;
  }

  /* Display */

  @Override
  public List<ItemStack> getItemInputs() {
    if (inputDisplay == null) {
      inputDisplay = Arrays.asList(ingredient.getMatchingStacks());
    }
    return inputDisplay;
  }

  @Override
  public List<ICauldronContents> getContentInputs() {
    return Collections.emptyList();
  }

  @Override
  public int getLevelInput() {
    return 0;
  }

  @Override
  public int getLevelOutput() {
    return THIRD;
  }

  @Override
  public ItemStack getItemOutput() {
    if (outputDisplay == null) {
      ItemStack[] items = ingredient.getMatchingStacks();
      if (items.length != 0) {
        outputDisplay = items[0].getContainerItem();
      } else {
        outputDisplay = ItemStack.EMPTY;
      }
    }
    return outputDisplay;
  }

  @Override
  public ICauldronContents getContentOutput() {
    if (outputContents == null) {
      assert color != null;
      outputContents = CauldronContentTypes.COLOR.of(color);
    }
    return outputContents;
  }

  @Override
  public boolean isSimple() {
    return color != null;
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
  public static class FinishedRecipe extends DynamicFinishedRecipe {
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
      super(id, RecipeSerializers.CAULDRON_MIX_DYE);
      this.ingredient = ingredient;
      this.color = color;
    }

    /**
     * Creates a new recipe instance using NBT dye color
     * @param id          Recipe ID
     * @param ingredient  Recipe ingredient
     */
    public FinishedRecipe(ResourceLocation id, Ingredient ingredient) {
      super(id, RecipeSerializers.CAULDRON_MIX_DYE);
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
  }
}
