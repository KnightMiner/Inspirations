package knightminer.inspirations.recipes.recipe.cauldron;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import knightminer.inspirations.library.recipe.DynamicFinishedRecipe;
import knightminer.inspirations.library.recipe.RecipeSerializer;
import knightminer.inspirations.library.recipe.RecipeSerializers;
import knightminer.inspirations.library.recipe.cauldron.CauldronContentTypes;
import knightminer.inspirations.library.recipe.cauldron.contents.ICauldronContents;
import knightminer.inspirations.library.recipe.cauldron.inventory.ICauldronInventory;
import knightminer.inspirations.library.recipe.cauldron.inventory.IModifyableCauldronInventory;
import knightminer.inspirations.library.recipe.cauldron.recipe.ICauldronRecipe;
import knightminer.inspirations.library.recipe.cauldron.recipe.ICauldronRecipeDisplay;
import knightminer.inspirations.library.recipe.cauldron.util.DisplayCauldronRecipe;
import knightminer.inspirations.library.recipe.cauldron.util.TemperaturePredicate;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.DyeColor;
import net.minecraft.item.DyeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;

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
  public boolean matches(ICauldronInventory inv, World worldIn) {
    // must have at least 1 level, and must match dye tag
    // nothing with a container, that behaves differently
    ItemStack stack = inv.getStack();
    if (inv.getLevel() == 0 || !dye.getTag().contains(stack.getItem()) || !stack.getContainerItem().isEmpty()) {
      return false;
    }

    // can dye water and other dyes
    // cannot dye if already that color
    ICauldronContents contents = inv.getContents();
    return contents.contains(CauldronContentTypes.FLUID, Fluids.WATER)
           || contents.get(CauldronContentTypes.COLOR)
                      .filter(color -> color != dye.getColorValue())
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
      inv.playSound(SoundEvents.ENTITY_FISHING_BOBBER_SPLASH);
    } else {
      contents.get(CauldronContentTypes.COLOR).ifPresent(color -> {
        // update dye stack
        inv.shrinkStack(1);

        // update contents
        inv.setContents(CauldronContentTypes.COLOR.of(addColors(dye.getColorValue(), 1, color, 1)));

        // play sound
        inv.playSound(SoundEvents.ENTITY_GENERIC_SPLASH);
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
  public IRecipeSerializer<?> getSerializer() {
    return RecipeSerializers.CAULDRON_DYE_WATER;
  }


  /* Display */

  @Override
  public List<ItemStack> getItemInputs() {
    if (inputs == null) {
      inputs = dye.getTag()
                  .getAllElements()
                  .stream()
                  .map(ItemStack::new)
                  .filter(stack -> !stack.hasContainerItem())
                  .collect(Collectors.toList());
      // empty on a server when this is called typically
      if (inputs.isEmpty()) {
        inputs = Collections.singletonList(new ItemStack(DyeItem.getItem(dye)));
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
  public static class Serializer extends RecipeSerializer<DyeCauldronWaterRecipe> {
    @SuppressWarnings("ConstantConditions")
    @Override
    public DyeCauldronWaterRecipe read(ResourceLocation id, JsonObject json) {
      String name = JSONUtils.getString(json, "dye");
      DyeColor dye = DyeColor.byTranslationKey(name, null);
      if (dye == null) {
        throw new JsonSyntaxException("Invalid color " + name);
      }
      return new DyeCauldronWaterRecipe(id, dye);
    }

    @Nullable
    @Override
    public DyeCauldronWaterRecipe read(ResourceLocation id, PacketBuffer buffer) {
      return new DyeCauldronWaterRecipe(id, buffer.readEnumValue(DyeColor.class));
    }

    @Override
    public void write(PacketBuffer buffer, DyeCauldronWaterRecipe recipe) {
      buffer.writeEnumValue(recipe.dye);
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
    public void serialize(JsonObject json) {
      json.addProperty("dye", dye.getString());
    }
  }
}
