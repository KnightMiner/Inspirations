package knightminer.inspirations.recipes.recipe.cauldron;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import knightminer.inspirations.library.MiscUtil;
import knightminer.inspirations.library.recipe.DynamicFinishedRecipe;
import knightminer.inspirations.library.recipe.RecipeSerializers;
import knightminer.inspirations.library.recipe.cauldron.CauldronContentTypes;
import knightminer.inspirations.library.recipe.cauldron.contents.ICauldronContents;
import knightminer.inspirations.library.recipe.cauldron.inventory.ICauldronInventory;
import knightminer.inspirations.library.recipe.cauldron.inventory.IModifyableCauldronInventory;
import knightminer.inspirations.library.recipe.cauldron.recipe.ICauldronRecipe;
import knightminer.inspirations.library.recipe.cauldron.recipe.ICauldronRecipeDisplay;
import knightminer.inspirations.library.recipe.cauldron.util.DisplayCauldronRecipe;
import knightminer.inspirations.library.recipe.cauldron.util.TemperaturePredicate;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.mantle.recipe.helper.AbstractRecipeSerializer;
import slimeknights.mantle.util.RegistryHelper;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Recipe that takes a solid dye and mixes it with liquid in the cauldron
 */
public class DyeCauldronWaterRecipe implements ICauldronRecipe, ICauldronRecipeDisplay {
  private final ResourceLocation id;
  private final DyeColor dye;
  private List<ItemStack> inputs;
  public DyeCauldronWaterRecipe(ResourceLocation id, DyeColor dye) {
    this.id = id;
    this.dye = dye;
  }

  @Override
  public boolean matches(ICauldronInventory inv, Level worldIn) {
    // must have at least 1 level, and must match dye tag
    // nothing with a container, that behaves differently
    ItemStack stack = inv.getStack();
    if (inv.getLevel() == 0 || !stack.is(dye.getTag()) || !stack.getContainerItem().isEmpty()) {
      return false;
    }

    // can dye water and other dyes
    // cannot dye if already that color
    ICauldronContents contents = inv.getContents();
    return contents.contains(CauldronContentTypes.FLUID, Fluids.WATER)
           || contents.get(CauldronContentTypes.COLOR)
                      .filter(color -> color != MiscUtil.getColor(dye))
                      .isPresent();
  }

  @Override
  public void handleRecipe(IModifyableCauldronInventory inv) {
    ICauldronContents contents = inv.getContents();
    // if water, set the color directly
    if (contents.contains(CauldronContentTypes.FLUID, Fluids.WATER)) {
      // update dye stack
      inv.shrinkStack(1);

      // update contents
      inv.setContents(CauldronContentTypes.DYE.of(dye));

      // play sound
      inv.playSound(SoundEvents.FISHING_BOBBER_SPLASH);
    } else {
      contents.get(CauldronContentTypes.COLOR).ifPresent(color -> {
        // update dye stack
        inv.shrinkStack(1);

        // update contents
        inv.setContents(CauldronContentTypes.COLOR.of(addColors(MiscUtil.getColor(dye), 1, color, 1)));

        // play sound
        inv.playSound(SoundEvents.GENERIC_SPLASH);
      });
    }
  }

  /**
   * Adds two colors
   * @param newColor    New color added
   * @param newLevels   Amount of new color added
   * @param original    Color in cauldron
   * @param origLevels  Number of levels in the cauldron
   * @return  Added colors
   */
  public static int addColors(int newColor, int newLevels, int original, int origLevels) {
    // keep original components as we average towards them
    int nr = getRed(newColor);
    int ng = getGreen(newColor);
    int nb = getBlue(newColor);
    // sum color components, add in 4 copies as a bottle is 4 levels
    // add in one copy per level of the original color
    int r = (nr * newLevels) + (getRed(original)   * origLevels);
    int g = (ng * newLevels) + (getGreen(original) * origLevels);
    int b = (nb * newLevels) + (getBlue(original)  * origLevels);
    // base acts as preference
    // divide per component, tending towards base
    int c = origLevels + newLevels;
    return divide(r, nr, c) << 16 | divide(g, ng, c) << 8 | divide(b, nb, c);
  }

  /**
   * Divides a sum of colors, favoring pref if the remainder is non-zero
   * @param sum      Color sum
   * @param pref     Preferred component
   * @param divisor  Number to divide by
   * @return Divided sum
   */
  private static int divide(int sum, int pref, int divisor) {
    int color = sum / divisor;
    // if there was a remainder, favor the original color
    if (sum % divisor != 0 && pref > color) {
      color++;
    }
    return color;
  }

  /**
   * Gets the red value of a color
   * @param color  Color
   * @return Red value
   */
  private static int getRed(int color) {
    return (color & 0xFF0000) >> 16;
  }

  /**
   * Gets the green value of a color
   * @param color  Color
   * @return Green value
   */
  private static int getGreen(int color) {
    return (color & 0xFF00) >> 8;
  }

  /**
   * Gets the blue value of a color
   * @param color  Color
   * @return Blue value
   */
  private static int getBlue(int color) {
    return (color & 0xFF);
  }

  @Override
  public ResourceLocation getId() {
    return id;
  }

  @Override
  public RecipeSerializer<?> getSerializer() {
    return RecipeSerializers.CAULDRON_DYE_WATER;
  }


  /* Display */

  @Override
  public List<ItemStack> getItemInputs() {
    if (inputs == null) {
      inputs = RegistryHelper.getTagValueStream(Registry.ITEM, dye.getTag())
                             .map(ItemStack::new)
                             .filter(stack -> !stack.hasContainerItem())
                             .collect(Collectors.toList());
      // empty on a server when this is called typically
      if (inputs.isEmpty()) {
        inputs = Collections.singletonList(new ItemStack(DyeItem.byColor(dye)));
      }
    }
    return inputs;
  }


  @Override
  public List<ICauldronContents> getContentInputs() {
    return DisplayCauldronRecipe.WATER_CONTENTS.get();
  }

  @Override
  public List<FluidStack> getFluidInputs() {
    return DisplayCauldronRecipe.WATER_FLUID.get();
  }

  @Override
  public ICauldronContents getContentOutput() {
    return CauldronContentTypes.DYE.of(dye);
  }

  @Override
  public int getLevelInput() {
    return THIRD;
  }

  @Override
  public int getLevelOutput() {
    return THIRD;
  }

  @Override
  public TemperaturePredicate getTemperature() {
    return TemperaturePredicate.ANY;
  }

  @Override
  public ItemStack getItemOutput() {
    return ItemStack.EMPTY;
  }

  /* Serializer for the recipe */
  public static class Serializer extends AbstractRecipeSerializer<DyeCauldronWaterRecipe> {
    @SuppressWarnings("ConstantConditions")
    @Override
    public DyeCauldronWaterRecipe fromJson(ResourceLocation id, JsonObject json) {
      String name = GsonHelper.getAsString(json, "dye");
      DyeColor dye = DyeColor.byName(name, null);
      if (dye == null) {
        throw new JsonSyntaxException("Invalid color " + name);
      }
      return new DyeCauldronWaterRecipe(id, dye);
    }

    @Nullable
    @Override
    public DyeCauldronWaterRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buffer) {
      return new DyeCauldronWaterRecipe(id, buffer.readEnum(DyeColor.class));
    }

    @Override
    public void toNetwork(FriendlyByteBuf buffer, DyeCauldronWaterRecipe recipe) {
      buffer.writeEnum(recipe.dye);
    }
  }

  /**
   * Finished recipe class for datagen
   */
  public static class FinishedRecipe extends DynamicFinishedRecipe {
    private final DyeColor dye;
    public FinishedRecipe(ResourceLocation id, DyeColor dye) {
      super(id, RecipeSerializers.CAULDRON_DYE_WATER);
      this.dye = dye;
    }

    @Override
    public void serializeRecipeData(JsonObject json) {
      json.addProperty("dye", dye.getSerializedName());
    }
  }
}
