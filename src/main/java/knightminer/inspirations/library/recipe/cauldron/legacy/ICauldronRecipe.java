package knightminer.inspirations.library.recipe.cauldron.legacy;

import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.Potion;
import net.minecraft.potion.Potions;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.Objects;

/**
 * Base interface for all cauldron recipes. Contains all methods required to determine new state, itemstack, and level based on the recipe
 * <p>
 * Parameters are considered stateless and generally should not modify the input stack except in the case of transformInput()
 * @deprecated Cauldron will be getting major changes, should avoid using any of the API until complete
 */
@Deprecated
public interface ICauldronRecipe {

  /**
   * Checks if the recipe matches the given input
   * @param stack   Input stack, should not be modified
   * @param boiling Whether the cauldron is above fire
   * @param level   Input level
   * @param state   Input cauldron state
   * @return True if the recipe matches
   */
  boolean matches(ItemStack stack, boolean boiling, int level, CauldronState state);

  /**
   * Gets the result stack for this recipe
   * @param stack   Input stack, should not be modified
   * @param boiling Whether the cauldron is above fire
   * @param level   Input level
   * @param state   Input cauldron state
   * @return ItemStack result
   */
  default ItemStack getResult(ItemStack stack, boolean boiling, int level, CauldronState state) {
    return ItemStack.EMPTY;
  }

  /**
   * Transforms the input itemstack for the recipe. Default implementation shrinks the input stack by 1
   * @param stack   Input stack to transform, may be modified
   * @param boiling Whether the cauldron is above fire
   * @param level   Input level
   * @param state   Input cauldron state
   * @return Resulting stack, does not need to be the input stack
   */
  default ItemStack transformInput(ItemStack stack, boolean boiling, int level, CauldronState state) {
    stack.shrink(1);
    return stack;
  }

  /**
   * Gets the new cauldron level as a result of this recipe
   * @param level Starting level
   * @return New level
   */
  default int getLevel(int level) {
    return level;
  }

  /**
   * Gets the resulting cauldron state for this recipe
   * @param stack   Input stack, should not be modified
   * @param boiling Whether the cauldron is above fire
   * @param level   Input level
   * @param state   Input cauldron state
   * @return New cauldron state
   */
  default CauldronState getState(ItemStack stack, boolean boiling, int level, CauldronState state) {
    return state;
  }

  /**
   * Gets the sound to play when performing this recipe
   * @param stack   Input stack, should not be modified
   * @param boiling Whether the cauldron is above fire
   * @param level   Input level
   * @param state   Input cauldron state
   * @return Sound event
   */
  @Nullable
  default SoundEvent getSound(ItemStack stack, boolean boiling, int level, CauldronState state) {
    return SoundEvents.ENTITY_FISHING_BOBBER_SPLASH;
  }

  /**
   * Volume to use when playing the sound
   * @return Sound volume
   */
  default float getVolume(SoundEvent sound) {
    return sound == SoundEvents.ENTITY_FISHING_BOBBER_SPLASH ? 0.3f : 1.0f;
  }

  /**
   * Returns the container item for this recipe. May be ItemStack.EMPTY if the container is handled elsewhere
   * @param stack Input stack, should not be modified
   * @return Container stack or empty for no container
   */
  default ItemStack getContainer(ItemStack stack) {
    return stack.getItem().getContainerItem(stack).copy();
  }


  /**
   * Current cauldron state
   */
  class CauldronState {
    private int color;
    private Potion potion;
    private FluidStack fluid;

    /**
     * Special constant for default cauldron state. Use this instead of setting the state to null
     */
    public static final CauldronState WATER = new CauldronState();

    /**
     * Creates a new water cauldron state
     */
    private CauldronState() {
      this.color = -1;
      this.potion = Potions.EMPTY;
      this.fluid = FluidStack.EMPTY;
    }


    /* Constructors */

    /**
     * Gets a new cauldron color state
     * @param color Color input
     */
    public static CauldronState dye(int color) {
      if (color == -1) {
        return WATER;
      }

      CauldronState state = new CauldronState();
      state.color = color;
      return state;
    }

    /**
     * Gets a potion cauldron state
     * @param potion Potion input
     */
    public static CauldronState potion(Potion potion) {
      if (potion == Potions.WATER) {
        return WATER;
      }

      CauldronState state = new CauldronState();
      state.potion = potion;
      return state;
    }

    /**
     * Gets a fluid cauldron state
     * @param fluid Fluid input
     */
    public static CauldronState fluid(@Nullable Fluid fluid) {
      if (fluid == null) {
        return WATER;
      }

      CauldronState state = new CauldronState();
      state.fluid = new FluidStack(fluid, FluidAttributes.BUCKET_VOLUME);
      return state;
    }


    /* Getters */

    /**
     * Check if the state is treated as water. Use if the exact fluid/potion water is not required
     * @return true if the state is treated as water
     */
    public boolean isWater() {
      return this == WATER || (!fluid.isEmpty() && fluid.getFluid().isIn(FluidTags.WATER));
    }

    /**
     * Gets the color for this state
     * @return color for the state, or -1 if the type is not a dye
     */
    public int getColor() {
      return color;
    }

    /**
     * Gets the potion for this state
     * @return potion for this state, or null if it is not a potion
     */
    public Potion getPotion() {
      if (this == WATER) {
        return Potions.WATER;
      }
      return potion;
    }

    /**
     * Gets the fluid for this state
     * @return fluid for this state, or EMPTY if it is not a fluid
     */
    public Fluid getFluid() {
      if (this == WATER) {
        return Fluids.WATER;
      }
      return fluid.isEmpty() ? Fluids.EMPTY : fluid.getFluid();
    }

    /**
     * Gets the fluid stack for this state
     * @return fluid stack for this state, or EMPTY if it is not a fluid
     */
    public FluidStack getFluidStack() {
      if (this == WATER) {
        return new FluidStack(Fluids.WATER, FluidAttributes.BUCKET_VOLUME);
      }
      return fluid.isEmpty() ? FluidStack.EMPTY : fluid.copy();
    }

    /**
     * Checks if two cauldron states match
     * @param state State to compare
     * @return True if the states match, that is they are the same type (dye, potion, or fluid) and have the same contents
     */
    public boolean matches(@Nullable CauldronState state) {
      return state != null && (this == state
                               || (state.color == this.color
                                   && state.potion == this.potion
                                   && state.getFluid() == this.getFluid()));
    }

    /**
     * Checks if a fluid is valid for a cauldron state
     * @param fluid Fluid to check
     * @return True if the fluid is valid, that is its non null, bucket volume, and no NBT
     */
    public static boolean fluidValid(FluidStack fluid) {
      return fluid.getAmount() == FluidAttributes.BUCKET_VOLUME && !fluid.hasTag();
    }

    /* NBT */
    private static final String TAG_WATER = "water";
    private static final String TAG_COLOR = "color";
    private static final String TAG_POTION = "potion";
    private static final String TAG_FLUID = "fluid";

    /**
     * Creates a new cauldron state from NBT
     * @param tags NBT tags
     */
    public static CauldronState fromNBT(CompoundNBT tags) {
      // quick boolean for water
      if (tags.getBoolean(TAG_WATER)) {
        return WATER;
      }

      // the rest will set properties
      CauldronState state = new CauldronState();
      if (tags.contains(TAG_COLOR)) {
        state.color = tags.getInt(TAG_COLOR);
      }
      if (tags.contains(TAG_POTION)) {
        state.potion = Potion.getPotionTypeForName(tags.getString(TAG_POTION));
      }
      if (tags.contains(TAG_FLUID)) {
        Fluid fluid = ForgeRegistries.FLUIDS.getValue(new ResourceLocation(tags.getString(TAG_FLUID)));
        if (fluid != null && fluid != Fluids.EMPTY) {
          state.fluid = new FluidStack(fluid, FluidAttributes.BUCKET_VOLUME);
        }
      }

      return state;
    }

    /**
     * Writes this state to NBT
     * @return CompoundNBT of the state
     */
    public CompoundNBT writeToNBT() {
      CompoundNBT tags = new CompoundNBT();
      // if water, just set a boolean so we have something
      if (this == WATER) {
        tags.putBoolean(TAG_WATER, true);
        return tags;
      }

      // otherwise set each property we have
      if (color > -1) {
        tags.putInt(TAG_COLOR, color);
      }
      if (potion != Potions.EMPTY) {
        tags.putString(TAG_POTION, Objects.requireNonNull(potion.getRegistryName()).toString());
      }
      if (!fluid.isEmpty()) {
        tags.putString(TAG_FLUID, Objects.requireNonNull(fluid.getFluid().getRegistryName()).toString());
      }

      return tags;
    }

    @Override
    public boolean equals(Object other) {
      if (this == other) {
        return true;
      }
      if (other == null || this.getClass() != other.getClass()) {
        return false;
      }
      return this.matches((CauldronState)other);
    }

    @Override
    public int hashCode() {
      int hashCode = 0;
      if (color > -1) {
        hashCode = color;
      }
      if (potion != Potions.EMPTY) {
        hashCode = hashCode * 31 + potion.hashCode();
      }
      if (!fluid.isEmpty()) {
        hashCode = hashCode * 31 + fluid.getFluid().hashCode();
      }
      return hashCode;
    }
  }
}
