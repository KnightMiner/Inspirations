package knightminer.inspirations.library.recipe.cauldron;

import javax.annotation.Nonnull;

import knightminer.inspirations.library.InspirationsRegistry;
import net.minecraft.init.PotionTypes;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionType;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

/**
 * Base interface for all cauldron recipes. Contains all methods required to determine new state, itemstack, and level based on the recipe
 *
 * Parameters are considered stateless and generally should not modify the input stack except in the case of transformInput()
 */
public interface ICauldronRecipe {

	/**
	 * Checks if the recipe matches the given input
	 * @param stack    Input stack, should not be modified
	 * @param boiling  Whether the cauldron is above fire
	 * @param level    Input level
	 * @param state    Input cauldron state
	 * @return True if the recipe matches
	 */
	boolean matches(ItemStack stack, boolean boiling, int level, CauldronState state);

	/**
	 * Gets the result stack for this recipe
	 * @param stack    Input stack, should not be modified
	 * @param boiling  Whether the cauldron is above fire
	 * @param level    Input level
	 * @param state    Input cauldron state
	 * @return ItemStack result
	 */
	default ItemStack getResult(ItemStack stack, boolean boiling, int level, CauldronState state) {
		return ItemStack.EMPTY;
	}

	/**
	 * Transforms the input itemstack for the recipe. Default implementation shrinks the input stack by 1
	 * @param stack    Input stack to transform, may be modified
	 * @param boiling  Whether the cauldron is above fire
	 * @param level    Input level
	 * @param state    Input cauldron state
	 * @return  Resulting stack, does not need to be the input stack
	 */
	default ItemStack transformInput(ItemStack stack, boolean boiling, int level, CauldronState state) {
		stack.shrink(1);
		return stack;
	}

	/**
	 * Gets the new cauldron level as a result of this recipe
	 * @param level  Starting level
	 * @return  New level
	 */
	default int getLevel(int level) {
		return level;
	}

	/**
	 * Gets the resulting cauldron state for this recipe
	 * @param stack    Input stack, should not be modified
	 * @param boiling  Whether the cauldron is above fire
	 * @param level    Input level
	 * @param state    Input cauldron state
	 * @return New cauldron state
	 */
	default CauldronState getState(ItemStack stack, boolean boiling, int level, CauldronState state) {
		return state;
	}

	/**
	 * Gets the sound to play when performing this recipe
	 * @param stack    Input stack, should not be modified
	 * @param boiling  Whether the cauldron is above fire
	 * @param level    Input level
	 * @param state    Input cauldron state
	 * @return  Sound event
	 */
	default SoundEvent getSound(ItemStack stack, boolean boiling, int level, CauldronState state) {
		return SoundEvents.ENTITY_BOBBER_SPLASH;
	}

	/**
	 * Volume to use when playing the sound
	 * @return  Sound volume
	 */
	default float getVolume(SoundEvent sound) {
		return sound == SoundEvents.ENTITY_BOBBER_SPLASH ? 0.3f : 1.0f;
	}

	/**
	 * Returns the container item for this recipe. May be ItemStack.EMPTY if the container is handled elsewhere
	 * @param stack  Input stack, should not be modified
	 * @return  Container stack or empty for no container
	 */
	default ItemStack getContainer(ItemStack stack) {
		return stack.getItem().getContainerItem(stack).copy();
	}


	/**
	 * Current cauldron state
	 */
	public class CauldronState {
		private int color;
		private PotionType potion;
		private FluidStack fluid;

		/** Special constant for default cauldron state. Use this instead of setting the state to null */
		public static final CauldronState WATER = new CauldronState();

		/**
		 * Creates a new water cauldron state
		 */
		private CauldronState() {
			this.color = -1;
			this.potion = null;
			this.fluid = null;
		}


		/* Constructors */

		/**
		 * Gets a new cauldron color state
		 * @param color  Color input
		 */
		public static CauldronState dye(int color) {
			if(color == -1) {
				return WATER;
			}

			CauldronState state = new CauldronState();
			state.color = color;
			return state;
		}

		/**
		 * Gets a potion cauldron state
		 * @param potion  Potion input
		 */
		public static CauldronState potion(PotionType potion) {
			if(potion == PotionTypes.WATER) {
				return WATER;
			}

			CauldronState state = new CauldronState();
			state.potion = potion;
			return state;
		}

		/**
		 * Gets a fluid cauldron state
		 * @param fluid  Fluid input
		 */
		public static CauldronState fluid(Fluid fluid) {
			if(fluid == FluidRegistry.WATER) {
				return WATER;
			}

			CauldronState state = new CauldronState();
			state.fluid = new FluidStack(fluid, Fluid.BUCKET_VOLUME);
			return state;
		}


		/* Getters */

		/**
		 * Check if the state is treated as water. Use if the exact fluid/potion water is not required
		 * @return true if the state is treated as water
		 */
		public boolean isWater() {
			return this == WATER || (fluid != null && InspirationsRegistry.isCauldronWater(fluid.getFluid()));
		}

		/**
		 * Gets the color for this state
		 * @return  color for the state, or -1 if the type is not a dye
		 */
		public int getColor() {
			return color;
		}

		/**
		 * Gets the potion for this state
		 * @return  potion for this state, or null if it is not a potion
		 */
		public PotionType getPotion() {
			if(this == WATER) {
				return PotionTypes.WATER;
			}
			return potion;
		}

		/**
		 * Gets the fluid for this state
		 * @return  fluid for this state, or null if it is not a fluid
		 */
		public Fluid getFluid() {
			if(this == WATER) {
				return FluidRegistry.WATER;
			}
			return fluid == null ? null : fluid.getFluid();
		}

		/**
		 * Gets the fluid stack for this state
		 * @return  fluid stack for this state, or null if it is not a fluid
		 */
		public FluidStack getFluidStack() {
			if(this == WATER) {
				return new FluidStack(FluidRegistry.WATER, Fluid.BUCKET_VOLUME);
			}
			return fluid == null ? null : fluid.copy();
		}

		/**
		 * Checks if two cauldron states match
		 * @param state  State to compare
		 * @return  True if the states match, that is they are the same type (dye, potion, or fluid) and have the same contents
		 */
		public boolean matches(CauldronState state) {
			return this == state
					|| (state.color == this.color
					&& state.potion == this.potion
					&& state.getFluid() == this.getFluid());
		}

		/**
		 * Checks if a fluid is valid for a cauldron state
		 * @param fluid  Fluid to check
		 * @return  True if the fluid is valid, that is its non null, bucket volume, and no NBT
		 */
		public static boolean fluidValid(FluidStack fluid) {
			return fluid != null && fluid.amount == Fluid.BUCKET_VOLUME && fluid.tag == null;
		}

		/* NBT */
		public static final String TAG_WATER = "water";
		public static final String TAG_COLOR = "color";
		public static final String TAG_POTION = "potion";
		public static final String TAG_FLUID = "fluid";

		/**
		 * Creates a new cauldron state from NBT
		 * @param tags  NBT tags
		 */
		public static CauldronState fromNBT(NBTTagCompound tags) {
			// quick boolean for water
			if(tags.getBoolean(TAG_WATER)) {
				return WATER;
			}

			// the rest will set properties
			CauldronState state = new CauldronState();
			if(tags.hasKey(TAG_COLOR)) {
				state.color = tags.getInteger(TAG_COLOR);
			}
			if(tags.hasKey(TAG_POTION)) {
				state.potion = PotionType.getPotionTypeForName(tags.getString(TAG_POTION));
			}
			if(tags.hasKey(TAG_FLUID)) {
				Fluid fluid = FluidRegistry.getFluid(tags.getString(TAG_FLUID));
				if(fluid != null) {
					state.fluid = new FluidStack(fluid, Fluid.BUCKET_VOLUME);
				}
			}

			return state;
		}

		/**
		 * Writes this state to NBT
		 * @return  NBTTagCompound of the state
		 */
		@Nonnull
		public NBTTagCompound writeToNBT() {
			NBTTagCompound tags = new NBTTagCompound();
			// if water, just set a boolean so we have something
			if(this == WATER) {
				tags.setBoolean(TAG_WATER, true);
				return tags;
			}

			// otherwise set each property we have
			if(color > -1) {
				tags.setInteger(TAG_COLOR, color);
			}
			if(potion != null) {
				tags.setString(TAG_POTION, potion.getRegistryName().toString());
			}
			if(fluid != null) {
				tags.setString(TAG_FLUID, fluid.getFluid().getName());
			}

			return tags;
		}
	}
}
