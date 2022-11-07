package knightminer.inspirations.cauldrons.interaction.potion;

import knightminer.inspirations.library.MiscUtil;
import knightminer.inspirations.cauldrons.InspirationsCaudrons;
import knightminer.inspirations.cauldrons.block.entity.PotionCauldronBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.alchemy.PotionBrewing.Mix;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.common.brewing.IBrewingRecipe;
import net.minecraftforge.common.brewing.VanillaBrewingRecipe;

import javax.annotation.Nullable;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.function.Function;

import static knightminer.inspirations.cauldrons.block.BoilingFourLayerCauldronBlock.isBoiling;
import static net.minecraft.world.level.block.LayeredCauldronBlock.LEVEL;

/** Cauldron interaction for brewing in a cauldron */
public class BrewingCauldronInteraction implements CauldronInteraction {
	/** Fixed type of potion, if null fetches it from the block entity */
	@Nullable
	private final Potion fixedInput;
	public BrewingCauldronInteraction(@Nullable Potion fixedInput) {
		this.fixedInput = fixedInput;
	}

	@Override
	public InteractionResult interact(BlockState oldState, Level level, BlockPos pos, Player player, InteractionHand hand, ItemStack stack) {
		// cauldron needs to be above fire
		if (isBoiling(level, pos)) {
			// if given a fixed input, use that
			PotionCauldronBlockEntity cauldron = null;
			Potion oldPotion = this.fixedInput;
			if (oldPotion == null) {
				cauldron = InspirationsCaudrons.potionCauldronEntity.getBlockEntity(level, pos);
				if (cauldron == null) {
					return InteractionResult.PASS;
				}
				oldPotion = cauldron.getPotion();
			}

			// try both forge and vanilla for result
			Potion newPotion = getVanillaResult(oldPotion, stack);
			if (newPotion == Potions.EMPTY) {
				newPotion = getForgeResult(oldPotion, stack);
			}
			// found either?
			if (newPotion != Potions.EMPTY) {
				if (!level.isClientSide) {
					// if we have a fixed input, update the cauldron then fetch the BE
					if (fixedInput != null) {
						// update the block, if already a potion cauldron it will remain one, but it may be water before
						level.setBlockAndUpdate(pos, InspirationsCaudrons.potionCauldron.defaultBlockState().setValue(LEVEL, oldState.getValue(LEVEL)));
						cauldron = InspirationsCaudrons.potionCauldronEntity.getBlockEntity(level, pos);
						if (cauldron == null) {
							return InteractionResult.CONSUME;
						}
					}

					// update potion
					cauldron.setPotion(newPotion);

					// consume items
					ItemStack container = stack.getContainerItem();
					MiscUtil.shrinkHeldItem(player, hand, stack, 1);
					if (!container.isEmpty()) {
						MiscUtil.givePlayerItem(player, container.copy());
					}

					player.awardStat(Stats.USE_CAULDRON);
					level.playSound(null, pos, SoundEvents.BREWING_STAND_BREW, SoundSource.BLOCKS, 1.0F, 1.0F);
				}
				return InteractionResult.sidedSuccess(level.isClientSide);
			}
		}
		return InteractionResult.PASS;
	}


	/* Vanilla recipe logic */

	/** Last successful match among potion mixes */
	private static Mix<Potion> lastMix;

	/**
	 * Checks if the given mix predicate matches
	 * @param mix  Mix predicate to test
	 * @param potion        Potion to test against the predicate
	 * @param stack         Reagent stack to test
	 * @return  True if it matches
	 */
	private static Potion tryMix(Mix<Potion> mix, Potion potion, ItemStack stack) {
		if (mix.from.get() == potion && mix.ingredient.test(stack)) {
			Potion output = mix.to.get();
			if (output != null) {
				return output;
			}
		}
		return Potions.EMPTY;
	}

	/** Gets the result of a vanilla potion recipe */
	private static Potion getVanillaResult(Potion potion, ItemStack stack) {
		// try last mix first
		if (lastMix != null) {
			Potion output = tryMix(lastMix, potion, stack);
			if (output != Potions.EMPTY) {
				return output;
			}
		}

		// try to find a new predicate among the list
		for (Mix<Potion> mix : PotionBrewing.POTION_MIXES) {
			Potion output = tryMix(mix, potion, stack);
			if (output != Potions.EMPTY) {
				lastMix = mix;
				return output;
			}
		}
		return Potions.EMPTY;
	}


	/* Forge recipe lookup */

	/** Function to make a stack from a potion */
	private static final Function<Potion, ItemStack> POTION_ITEM_MAPPER = potion -> PotionUtils.setPotion(new ItemStack(Items.POTION), potion);
	/** Cached map of items for each potion */
	private static final Map<Potion,ItemStack> POTION_ITEM_LOOKUP = new IdentityHashMap<>();

	/** Last successful match among potion mixes */
	private static IBrewingRecipe lastRecipe;

	/**
	 * Trys a potion brewing recipe to get the potion output
	 * @param recipe  Recipe to try
	 * @param potion  Potion stack input
	 * @param stack   Item stack input
	 * @return  Potion result, or empty if no match
	 */
	private static Potion tryForgeRecipe(IBrewingRecipe recipe, ItemStack potion, ItemStack stack) {
		ItemStack outputStack = recipe.getOutput(potion, stack);
		if (!outputStack.isEmpty()) {
			return PotionUtils.getPotion(outputStack);
		}
		return Potions.EMPTY;
	}

	/** Gets the result of a forge recipe, or empty if no match */
	private static Potion getForgeResult(Potion potion, ItemStack stack) {
		// first, make a stack from the potion
		ItemStack input = POTION_ITEM_LOOKUP.computeIfAbsent(potion, POTION_ITEM_MAPPER);
		if (lastRecipe != null) {
			Potion output = tryForgeRecipe(lastRecipe, input, stack);
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
			Potion output = tryForgeRecipe(recipe, input, stack);
			if (output != Potions.EMPTY) {
				lastRecipe = recipe;
				return output;
			}
		}
		return Potions.EMPTY;
	}
}
