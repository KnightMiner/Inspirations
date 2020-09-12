package knightminer.inspirations.library.recipe.cauldron.recipe;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import knightminer.inspirations.library.recipe.cauldron.CauldronContentTypes;
import knightminer.inspirations.library.recipe.cauldron.contents.ICauldronContents;
import knightminer.inspirations.library.recipe.cauldron.ingredient.ICauldronIngredient;
import knightminer.inspirations.library.recipe.cauldron.inventory.ICauldronState;
import knightminer.inspirations.library.recipe.cauldron.util.DisplayCauldronRecipe;
import knightminer.inspirations.library.recipe.cauldron.util.LevelPredicate;
import knightminer.inspirations.library.recipe.cauldron.util.TemperaturePredicate;
import net.minecraft.util.JSONUtils;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Shared base logic between {@link CauldronRecipe} and {@link CauldronTransform}
 */
public abstract class AbstractCauldronRecipe implements ICauldronRecipeDisplay {
  protected final ICauldronIngredient ingredient;
  protected final LevelPredicate level;
  protected final TemperaturePredicate temperature;
  @Nullable
  protected final ICauldronContents outputContents;

  // values for JEI display
  private ICauldronContents displayContents;
  private FluidStack displayFluid;
  private List<FluidStack> fluidInputs;

  /**
   * Creates a new recipe
   * @param ingredient   Cauldron content ingredient
   * @param level        Input level
   * @param temperature  Predicate for required cauldron temperature
   * @param output       Output stack, use empty for no output
   */
  public AbstractCauldronRecipe(ICauldronIngredient ingredient, LevelPredicate level, TemperaturePredicate temperature, @Nullable ICauldronContents output) {
    this.ingredient = ingredient;
    this.level = level;
    this.temperature = temperature;
    this.outputContents = output;
  }

  /**
   * Checks if the given cauldron state matches these inputs
   * @param state  State to check
   * @return  True if it matches the inputs
   */
  protected boolean matches(ICauldronState state) {
    int currentLevel = state.getLevel();
    return temperature.test(state.getTemperature())
           && level.test(currentLevel)
           && (currentLevel == 0 || ingredient.test(state.getContents()));
  }

  @Override
  public List<ICauldronContents> getContentInputs() {
    return ingredient.getMatchingContents();
  }

  @Override
  public List<FluidStack> getFluidInputs() {
    if (fluidInputs == null) {
      // map fluids to fluid stacks, skip any non-fluids
      List<ICauldronContents> inputs = getContentInputs();
      fluidInputs = inputs.stream()
                          .flatMap(contents -> DisplayCauldronRecipe.getFluid(contents).map(Stream::of).orElseGet(Stream::empty))
                          .collect(Collectors.toList());
      // ensure size is the same, potions are cross registered
      if (fluidInputs.size() != inputs.size()) {
        fluidInputs = Collections.emptyList();
      }
    }
    return fluidInputs;
  }

  @Override
  public TemperaturePredicate getTemperature() {
    return temperature;
  }

  @Override
  public ICauldronContents getContentOutput() {
    // if output is null, display first input
    if (outputContents == null) {
      if (displayContents == null) {
        List<ICauldronContents> inputs = getContentInputs();
        displayContents = inputs.isEmpty() ? CauldronContentTypes.DEFAULT.get() : inputs.get(0);
      }
      return displayContents;
    }
    return outputContents;
  }

  @Override
  public FluidStack getFluidOutput() {
    if (displayFluid == null) {
      displayFluid = DisplayCauldronRecipe.getFluid(getContentOutput()).orElse(FluidStack.EMPTY);
    }
    return displayFluid;
  }

  /**
   * Gets the boiling predicate for the given JSON
   * @param json  Parent json object
   * @param key   Key in json
   * @return  Boiling predicate
   * @throws JsonSyntaxException  If the value is invalid
   */
  public static TemperaturePredicate getBoiling(JsonObject json, String key) {
    String name = JSONUtils.getString(json, key, "any");
    TemperaturePredicate boiling = TemperaturePredicate.byName(name);
    if (boiling == null) {
      throw new JsonSyntaxException("Invalid boiling predicate '" + name + "'");
    }
    return boiling;
  }
}
