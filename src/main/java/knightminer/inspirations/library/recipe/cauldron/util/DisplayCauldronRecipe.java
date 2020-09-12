package knightminer.inspirations.library.recipe.cauldron.util;

import knightminer.inspirations.Inspirations;
import knightminer.inspirations.library.recipe.cauldron.CauldronContentTypes;
import knightminer.inspirations.library.recipe.cauldron.contents.ICauldronContents;
import knightminer.inspirations.library.recipe.cauldron.recipe.ICauldronRecipe;
import knightminer.inspirations.library.recipe.cauldron.recipe.ICauldronRecipeDisplay;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.mantle.recipe.ICustomOutputRecipe;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Helper class to build a JEI display recipe for use in {@link slimeknights.mantle.recipe.IMultiRecipe}
 * TODO: make mantle IMultiRecipe not require IRecipe
 */
public class DisplayCauldronRecipe implements ICauldronRecipeDisplay, ICustomOutputRecipe<IInventory> {
  private static final ResourceLocation ID = Inspirations.getResource("dynamic_display");
  /** Lazy getter for water */
  public static final Lazy<List<ICauldronContents>> WATER_CONTENTS = Lazy.of(() -> Collections.singletonList(CauldronContentTypes.DEFAULT.get()));
  /** Lazy getter for water as a fluid stack */
  public static final Lazy<List<FluidStack>> WATER_FLUID = Lazy.of(() -> Collections.singletonList(new FluidStack(Fluids.WATER, FluidAttributes.BUCKET_VOLUME)));
  // inputs
  private final List<ItemStack> itemInputs;
  private final List<ICauldronContents> contentInputs;
  private final List<FluidStack> fluidInputs;
  private final int levelInput;
  // outputs
  private final ItemStack itemOutput;
  private final ICauldronContents contentOutputs;
  private final FluidStack fluidOutput;
  private final int levelOutput;
  // misc
  private final TemperaturePredicate temperature;
  private final int time;

  /** Constructor, use the builder to create */
  private DisplayCauldronRecipe(List<ItemStack> itemInputs, List<ICauldronContents> contentInputs, List<FluidStack> fluidInputs, int levelInput,
                                ItemStack itemOutput, ICauldronContents contentOutputs, FluidStack fluidOutput, int levelOutput, TemperaturePredicate temperature, int time) {
    this.fluidInputs = fluidInputs;
    this.levelInput = levelInput;
    this.itemInputs = itemInputs;
    this.contentInputs = contentInputs;
    this.fluidOutput = fluidOutput;
    this.levelOutput = levelOutput;
    this.itemOutput = itemOutput;
    this.contentOutputs = contentOutputs;
    this.temperature = temperature;
    this.time = time;
  }

  /**
   * Creates a new builder using the given levels
   * @param levelInput   Input level
   * @param levelOutput  Output level
   * @return  Builder instance
   */
  public static Builder builder(int levelInput, int levelOutput) {
    return new Builder(levelInput, levelOutput);
  }

  /**
   * Creates a new builder for an unchanging level
   * @param level  Level
   * @return  Builder instance
   */
  public static Builder builder(int level) {
    return builder(level, level);
  }

  /**
   * Gets an optional fluidstack from the given contents
   * @param contents  Contents instance
   * @return  Fluid stack if its a fluid contents
   */
  public static Optional<FluidStack> getFluid(ICauldronContents contents) {
    return contents.get(CauldronContentTypes.FLUID).map(fluid -> new FluidStack(fluid, FluidAttributes.BUCKET_VOLUME));
  }

  /* Input */

  @Override
  public List<ItemStack> getItemInputs() {
    return itemInputs;
  }

  @Override
  public List<ICauldronContents> getContentInputs() {
    return contentInputs;
  }

  @Override
  public List<FluidStack> getFluidInputs() {
    return fluidInputs;
  }

  @Override
  public int getLevelInput() {
    return levelInput;
  }

  /* Outputs */

  @Override
  public ItemStack getItemOutput() {
    return itemOutput;
  }

  @Override
  public ICauldronContents getContentOutput() {
    return contentOutputs;
  }

  @Override
  public FluidStack getFluidOutput() {
    return fluidOutput;
  }

  @Override
  public int getLevelOutput() {
    return levelOutput;
  }

  /* Misc */

  @Override
  public TemperaturePredicate getTemperature() {
    return temperature;
  }

  @Override
  public int getTime() {
    return time;
  }

  /* IRecipe methods */

  /** @deprecated unsupported method */
  @Deprecated
  @Override
  public boolean matches(IInventory inv, World worldIn) {
    return false;
  }

  /** @deprecated unsupported method */
  @Deprecated
  @Override
  public ResourceLocation getId() {
    return ID;
  }

  /** @deprecated unsupported method */
  @Deprecated
  @Override
  public IRecipeSerializer<?> getSerializer() {
    throw new UnsupportedOperationException();
  }

  /** @deprecated unsupported method */
  @Deprecated
  @Override
  public IRecipeType<?> getType() {
    throw new UnsupportedOperationException();
  }

  /** Builder class */
  public static class Builder {
    private final int levelInput;
    private final int levelOutput;
    private List<ItemStack> itemInputs = Collections.emptyList();
    private List<ICauldronContents> contentInputs = Collections.emptyList();
    private ItemStack itemOutput = ItemStack.EMPTY;
    private ICauldronContents contentOutputs;
    private TemperaturePredicate temperature = TemperaturePredicate.ANY;
    private int time = -1;
    private Builder(int levelInput, int levelOutput) {
      this.levelInput = MathHelper.clamp(levelInput, 0, ICauldronRecipe.MAX);
      this.levelOutput = MathHelper.clamp(levelOutput, 0, ICauldronRecipe.MAX);
    }

    /* Input */

    /**
     * Sets item inputs
     * @param input  Input list
     * @return  Builder instance
     */
    public Builder setItemInputs(List<ItemStack> input) {
      this.itemInputs = input;
      return this;
    }

    /**
     * Sets item inputs
     * @param input  Input stack
     * @return  Builder instance
     */
    public Builder setItemInputs(ItemStack input) {
      return setItemInputs(Collections.singletonList(input));
    }

    /**
     * Sets item inputs
     * @param input  Input item
     * @return  Builder instance
     */
    public Builder setItemInputs(IItemProvider input) {
      return setItemInputs(new ItemStack(input));
    }

    /**
     * Sets the cauldron content inputs
     * @param input  Content list
     * @return  Builder instance
     */
    public Builder setContentInputs(List<ICauldronContents> input) {
      if (levelInput != 0) {
        this.contentInputs = input;
      }
      return this;
    }

    /**
     * Sets the cauldron content inputs
     * @param input  Content input
     * @return  Builder instance
     */
    public Builder setContentInputs(ICauldronContents input) {
      return setContentInputs(Collections.singletonList(input));
    }

    /* Output */

    /**
     * Sets the output item
     * @param output  Output stack
     * @return  Builder instance
     */
    public Builder setItemOutput(ItemStack output) {
      this.itemOutput = output;
      return this;
    }

    /**
     * Sets the output item
     * @param output  Output item
     * @return  Builder instance
     */
    public Builder setItemOutput(IItemProvider output) {
      return setItemOutput(new ItemStack(output));
    }

    /**
     * Sets the content output
     * @param output  Content output
     * @return  Builder instance
     */
    public Builder setContentOutput(ICauldronContents output) {
      if (levelOutput != 0) {
        this.contentOutputs = output;
      }
      return this;
    }

    /* Misc */

    /**
     * Sets the recipe temperature
     * @param temperature  Temperature predicate
     * @return Builder instance
     */
    public Builder setTemperature(TemperaturePredicate temperature) {
      this.temperature = temperature;
      return this;
    }

    /**
     * Sets the recipe time
     * @param time  Recipe time in ticks
     * @return  Builder instance
     */
    public Builder setTime(int time) {
      this.time = time;
      return this;
    }

    /**
     * Builds the final display recipe
     * @return  Display recipe
     */
    public DisplayCauldronRecipe build() {
      // if we have an input, get fluid inputs and validate
      List<FluidStack> fluidInputs = Collections.emptyList();
      if (levelInput != 0) {
        if (contentInputs.isEmpty()) {
          throw new IllegalStateException("Invalid recipe, must have at least one input for level greater than 0");
        }
        fluidInputs = contentInputs.stream()
                                   .flatMap(contents -> getFluid(contents).map(Stream::of).orElseGet(Stream::empty))
                                   .collect(Collectors.toList());
        // size change means not all were fluids
        if (fluidInputs.size() != contentInputs.size()) {
          fluidInputs = Collections.emptyList();
        }
      }
      // if we have an output, get fluid output and validate
      FluidStack fluidOutput = FluidStack.EMPTY;
      if (levelOutput != 0) {
        if (contentOutputs == null) {
          throw new IllegalStateException("Invalid recipe, must have at least one output for level greater than 0");
        }
        fluidOutput = getFluid(contentOutputs).orElse(FluidStack.EMPTY);
      } else {
        contentOutputs = CauldronContentTypes.DEFAULT.get();
      }
      // return recipe
      return new DisplayCauldronRecipe(itemInputs, contentInputs, fluidInputs, levelInput, itemOutput, contentOutputs, fluidOutput, levelOutput, temperature, time);
    }
  }
}
