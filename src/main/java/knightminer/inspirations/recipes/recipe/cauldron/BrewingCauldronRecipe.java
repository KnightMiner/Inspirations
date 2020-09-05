package knightminer.inspirations.recipes.recipe.cauldron;

import knightminer.inspirations.library.recipe.RecipeSerializers;
import knightminer.inspirations.library.recipe.cauldron.CauldronContentTypes;
import knightminer.inspirations.library.recipe.cauldron.inventory.ICauldronInventory;
import knightminer.inspirations.library.recipe.cauldron.inventory.IModifyableCauldronInventory;
import knightminer.inspirations.library.recipe.cauldron.recipe.ICauldronRecipe;
import knightminer.inspirations.library.recipe.cauldron.util.CauldronTemperature;
import knightminer.inspirations.library.recipe.cauldron.util.DisplayCauldronRecipe;
import knightminer.inspirations.library.util.ReflectionUtil;
import knightminer.inspirations.tweaks.recipe.NormalBrewingRecipe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionBrewing;
import net.minecraft.potion.PotionUtils;
import net.minecraft.potion.Potions;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;
import net.minecraftforge.common.brewing.BrewingRecipe;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.common.brewing.IBrewingRecipe;
import net.minecraftforge.common.brewing.VanillaBrewingRecipe;
import slimeknights.mantle.recipe.IMultiRecipe;

import java.util.Arrays;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Shared logic for both potion brewing recipe types
 */
public abstract class BrewingCauldronRecipe implements ICauldronRecipe, IMultiRecipe<DisplayCauldronRecipe> {
  private final ResourceLocation id;
  private List<DisplayCauldronRecipe> displayRecipes;
  protected BrewingCauldronRecipe(ResourceLocation id) {
    this.id = id;
  }

  @Override
  public boolean matches(ICauldronInventory inv, World worldIn) {
    // must have liquid and not be boiling
    if (inv.getLevel() == 0 || inv.getTemperature() != CauldronTemperature.BOILING) {
      return false;
    }
    // must have an item, and if the cauldron has 2 levels we must have two of it
    ItemStack stack = inv.getStack();
    if (stack.isEmpty()) {
      return false;
    }
    // find a matching predicate and we are good
    return inv.getContents()
              .get(CauldronContentTypes.POTION)
              .filter(value -> getResult(value, stack) != Potions.EMPTY)
              .isPresent();
  }

  @Override
  public void handleRecipe(IModifyableCauldronInventory inventory) {
    inventory.getContents().get(CauldronContentTypes.POTION).ifPresent(potion -> {
      // find a mix
      ItemStack stack = inventory.getStack();
      Potion output = getResult(potion, stack);
      if (output != Potions.EMPTY) {
        // change the potion
        inventory.setContents(CauldronContentTypes.POTION.of(output));

        // shrink the stack in hand and return the container
        ItemStack container = stack.getContainerItem().copy();
        inventory.shrinkStack(1);
        inventory.setOrGiveStack(container);

        // play sound
        inventory.playSound(SoundEvents.BLOCK_BREWING_STAND_BREW);
      }
    });
  }

  /**
   * Gets the resulting potion for this combination, or empty if none
   * @param potion  Potion input
   * @param stack   Stack input
   * @return  Resulting potion, or empty if not recipe
   */
  protected abstract Potion getResult(Potion potion, ItemStack stack);

  @Override
  public ResourceLocation getId() {
    return id;
  }

  /**
   * Makes a display brewing recipe from the given inputs
   * @param input    Potion input
   * @param reagent  Potion reagent
   * @param output   Potion output
   * @return  Recipe instance
   */
  protected static DisplayCauldronRecipe makeRecipe(Potion input, Ingredient reagent, Potion output) {
    return DisplayCauldronRecipe.builder(3)
                         .setItemInputs(Arrays.asList(reagent.getMatchingStacks()))
                         .setContentInputs(CauldronContentTypes.POTION.of(input))
                         .setContentOutput(CauldronContentTypes.POTION.of(output))
                         .build();
  }

  /**
   * Gets a list of recipes for display. Does not need to be cached
   * @return  List of display recipes
   */
  protected abstract List<DisplayCauldronRecipe> getDisplayRecipes();

  @Override
  public List<DisplayCauldronRecipe> getRecipes() {
    if (displayRecipes == null) {
      displayRecipes = getDisplayRecipes();
    }
    return displayRecipes;
  }

  /** Brewing cauldron recipe for the vanilla {@link PotionBrewing} */
  public static class Vanilla extends BrewingCauldronRecipe {
    /** Last successful match among potion mixes */
    private Object lastMix;

    /**
     * Creates a new recipe instance
     * @param id  Recipe ID
     */
    public Vanilla(ResourceLocation id) {
      super(id);
    }

    /**
     * Checks if the given mix predicate matches
     * @param mix  Mix predicate to test
     * @param potion        Potion to test against the predicate
     * @param stack         Reagent stack to test
     * @return  True if it matches
     */
    private static Potion tryMix(Object mix, Potion potion, ItemStack stack) {
      if (ReflectionUtil.getMixPredicateInput(mix) == potion) {
        Ingredient ingredient = ReflectionUtil.getMixPredicateReagent(mix);
        if (ingredient != null && ingredient.test(stack)) {
          Potion output = ReflectionUtil.getMixPredicateOutput(mix);
          if (output != null) {
            return output;
          }
        }
      }
      return Potions.EMPTY;
    }

    @Override
    protected Potion getResult(Potion potion, ItemStack stack) {
      // try last mix first
      if (lastMix != null) {
        Potion output = tryMix(lastMix, potion, stack);
        if (output != Potions.EMPTY) {
          return output;
        }
      }

      // try to find a new predicate among the list
      for (Object mix : PotionBrewing.POTION_TYPE_CONVERSIONS) {
        Potion output = tryMix(mix, potion, stack);
        if (output != Potions.EMPTY) {
          lastMix = mix;
          return output;
        }
      }
      return Potions.EMPTY;
    }

    @Override
    protected List<DisplayCauldronRecipe> getDisplayRecipes() {
      // map all potion type conversions to a recipe if possible
      return ((List<?>)PotionBrewing.POTION_TYPE_CONVERSIONS).stream().flatMap(mix -> {
        Potion input = ReflectionUtil.getMixPredicateInput(mix);
        Potion output = ReflectionUtil.getMixPredicateOutput(mix);
        Ingredient reagent = ReflectionUtil.getMixPredicateReagent(mix);
        // ensure the reflection worked and the recipe is valid
        if (input != null && output != null && reagent != null && !reagent.hasNoMatchingItems()) {
          return Stream.of(makeRecipe(input, reagent, output));
        }
        return Stream.empty();
      }).collect(Collectors.toList());
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
      return RecipeSerializers.CAULDRON_POTION_BREWING;
    }
  }

  /** Potion brewing logic using {@link BrewingRecipeRegistry} */
  public static class Forge extends BrewingCauldronRecipe {
    /** Function to make a stack from a potion */
    private static final Function<Potion, ItemStack> POTION_ITEM_MAPPER = potion -> PotionUtils.addPotionToItemStack(new ItemStack(Items.POTION), potion);
    /** Cached map of items for each potion */
    private final Map<Potion,ItemStack> potionItemLookup = new IdentityHashMap<>();

    /** Last successful match among potion mixes */
    private IBrewingRecipe lastRecipe;

    /**
     * Creates a new recipe instance
     * @param id  Recipe ID
     */
    public Forge(ResourceLocation id) {
      super(id);
    }

    /**
     * Trys a potion brewing recipe to get the potion output
     * @param recipe  Recipe to try
     * @param potion  Potion stack input
     * @param stack   Item stack input
     * @return  Potion result, or empty if no match
     */
    private static Potion tryRecipe(IBrewingRecipe recipe, ItemStack potion, ItemStack stack) {
      ItemStack outputStack = recipe.getOutput(potion, stack);
      if (!outputStack.isEmpty()) {
        return PotionUtils.getPotionFromItem(outputStack);
      }
      return Potions.EMPTY;
    }

    @Override
    protected Potion getResult(Potion potion, ItemStack stack) {
      // first, make a stack from the potion
      ItemStack input = potionItemLookup.computeIfAbsent(potion, POTION_ITEM_MAPPER);
      if (lastRecipe != null) {
        Potion output = tryRecipe(lastRecipe, input, stack);
        if (output != Potions.EMPTY) {
          return output;
        }
      }
      // try each brewing recipe in the registry
      for (IBrewingRecipe recipe : BrewingRecipeRegistry.getRecipes()) {
        // skip vanilla recipe, we handle that separately for efficiency
        if (recipe instanceof VanillaBrewingRecipe) {
          continue;
        }
        Potion output = tryRecipe(recipe, input, stack);
        if (output != Potions.EMPTY) {
          lastRecipe = recipe;
          return output;
        }
      }
      return Potions.EMPTY;
    }

    @Override
    protected List<DisplayCauldronRecipe> getDisplayRecipes() {
      List<IBrewingRecipe> recipes = BrewingRecipeRegistry.getRecipes();
      return Stream.concat(
          // try to handle the Forge one
          recipes.stream()
                 .filter(r -> r instanceof BrewingRecipe)
                 .map(r -> (BrewingRecipe) r)
                 .filter(r -> r.getOutput().getItem() == Items.POTION)
                 .flatMap(recipe -> {
                   // output must be a potion
                   Potion output = PotionUtils.getPotionFromItem(recipe.getOutput());
                   if (output != Potions.EMPTY) {
                     // get all potion inputs and return recipes
                     return Arrays.stream(recipe.getInput().getMatchingStacks())
                                  .filter(s -> s.getItem() == Items.POTION)
                                  .map(PotionUtils::getPotionFromItem)
                                  .filter(pot -> pot != Potions.EMPTY)
                                  .map(input -> makeRecipe(input, recipe.getIngredient(), output));
                   }
                   return Stream.empty();
                 }),
          // special case our own brewing class, the Forge one really sucks
          recipes.stream().filter(r -> r instanceof NormalBrewingRecipe)
                 .map(r -> (NormalBrewingRecipe) r)
                 .filter(NormalBrewingRecipe::isEnabled)
                 .map(r -> makeRecipe(r.getStart(), r.getCatalyst(), r.getOutput()))
      ).collect(Collectors.toList());
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
      return RecipeSerializers.CAULDRON_FORGE_BREWING;
    }
  }
}
