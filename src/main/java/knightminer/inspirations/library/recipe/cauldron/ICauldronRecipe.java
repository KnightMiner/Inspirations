package knightminer.inspirations.library.recipe.cauldron;

import java.util.Locale;

import javax.annotation.Nonnull;

import net.minecraft.init.PotionTypes;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionType;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

public interface ICauldronRecipe {

	/**
	 * Checks if the recipe matches the given input
	 * @param stack    Input stack
	 * @param boiling  Whether the cauldron is above fire
	 * @param level    Input level
	 * @param state    Input cauldron state
	 * @return True if the recipe matches
	 */
	boolean matches(ItemStack stack, boolean boiling, int level, CauldronState state);

	/**
	 * Gets the result stack for this recipe
	 * @param stack    Input stack
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
	 * @param stack    Input stack to transform
	 * @param boiling  Whether the cauldron is above fire
	 * @param level    Input level
	 * @param state    Input cauldron state
	 * @return
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
	 * @param stack    Input stack
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
	 * Current contents type for a cauldron state
	 */
	public enum CauldronContents implements IStringSerializable {
		WATER,
		DYE,
		POTION,
		FLUID;

		private int meta;
		CauldronContents() {
			this.meta = ordinal();
		}

		@Override
		public String getName() {
			return name().toLowerCase(Locale.US);
		}

		public int getMeta() {
			return meta;
		}

		public static CauldronContents fromMeta(int meta) {
			if(meta > values().length) {
				meta = 0;
			}

			return values()[meta];
		}
	}

	/**
	 * Current cauldron state
	 */
	public class CauldronState {
		private final CauldronContents type;
		private int color;
		private PotionType potion;
		private Fluid fluid;

		public static final CauldronState WATER = new CauldronState(CauldronContents.WATER);

		/**
		 * Creates a new water cauldron state
		 */
		private CauldronState(CauldronContents type) {
			this.type = type;
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

			CauldronState state = new CauldronState(CauldronContents.DYE);
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

			CauldronState state = new CauldronState(CauldronContents.POTION);
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

			CauldronState state = new CauldronState(CauldronContents.FLUID);
			state.fluid = fluid;
			return state;
		}

		/**
		 * Creates a new cauldron state from NBT
		 * @param tags  NBT tags
		 */
		public static CauldronState fromNBT(NBTTagCompound tags) {
			CauldronContents type = ICauldronRecipe.CauldronContents.fromMeta(tags.getInteger(TAG_TYPE));
			if(type == CauldronContents.WATER) {
				return WATER;
			}

			CauldronState state = new CauldronState(type);
			switch(type) {
				case DYE:
					state.color = tags.getInteger(TAG_COLOR);
					break;
				case POTION:
					if(tags.hasKey(TAG_POTION)) {
						state.potion = PotionType.getPotionTypeForName(tags.getString(TAG_POTION));
					}
					break;
				case FLUID:
					if(tags.hasKey(TAG_FLUID)) {
						state.fluid = FluidRegistry.getFluid(tags.getString(TAG_FLUID));
					}
					break;
			}

			return state;
		}


		/* Getters */

		/**
		 * Getst the type of this state
		 * @return  contents type
		 */
		public CauldronContents getType() {
			return type;
		}

		/**
		 * Gets the color for this state
		 * @return  color for the state, or -1 if the type is not DYE
		 */
		public int getColor() {
			return color;
		}

		/**
		 * Gets the potion for this state
		 * @return  potion for this state
		 */
		public PotionType getPotion() {
			if(this == WATER) {
				return PotionTypes.WATER;
			}
			return potion;
		}

		/**
		 * Gets the fluid for this state
		 * @return  fluid for this state
		 */
		public Fluid getFluid() {
			if(this == WATER) {
				return FluidRegistry.WATER;
			}
			return fluid;
		}

		public boolean matches(CauldronState state) {
			if(this == state) {
				return true;
			}
			return state.type == this.type
					&& state.color == this.color
					&& state.potion == this.potion
					&& state.fluid == this.fluid;
		}

		/* NBT */
		public static final String TAG_TYPE = "type";
		public static final String TAG_COLOR = "color";
		public static final String TAG_POTION = "potion";
		public static final String TAG_FLUID = "fluid";

		/**
		 * Writes this state to NBT
		 * @return  NBTTagCompound of the state
		 */
		@Nonnull
		public NBTTagCompound writeToNBT() {
			NBTTagCompound tags = new NBTTagCompound();
			tags.setInteger(TAG_TYPE, type.getMeta());
			switch(type) {
				case DYE:
					tags.setInteger(TAG_COLOR, color);
					break;
				case POTION:
					if(potion != null) {
						tags.setString(TAG_POTION, potion.getRegistryName().toString());
					}
					break;
				case FLUID:
					if(fluid != null) {
						tags.setString(TAG_FLUID, fluid.getName());
					}
					break;
			}

			return tags;
		}
	}
}
