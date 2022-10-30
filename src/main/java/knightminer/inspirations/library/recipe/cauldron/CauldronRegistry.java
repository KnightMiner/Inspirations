package knightminer.inspirations.library.recipe.cauldron;

import net.minecraft.core.BlockPos;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import slimeknights.mantle.util.RegistryHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/** Class to register tag based cauldron interactions, this may be temporary until I decide if JSON recipes can make a comeback reasonably */
public class CauldronRegistry {
	private static final List<CauldronOverride> CAULDRON_OVERRIDES = new ArrayList<>();

	/** Matches all cauldrons */
	public static final Predicate<Block> ALL_CAULDRONS = block -> true;

	/**
	 * Registers a cauldron override
	 * @param blockPredicate  List of cauldrons to match, should be a subset of {@link net.minecraft.tags.BlockTags#CAULDRONS}
	 * @param itemPredicate   Items to match for the recipes, typically a tag
	 * @param interaction     Interaction to run if matches
	 */
	public static void register(Predicate<Block> blockPredicate, Predicate<Item> itemPredicate, CauldronInteraction interaction) {
		CAULDRON_OVERRIDES.add(new CauldronOverride(blockPredicate, itemPredicate, interaction));
	}

	/** Tries all overrides, returns the first one that both matches and consumes acton */
	public static InteractionResult attemptOverride(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, ItemStack stack) {
		Block block = state.getBlock();
		Item item = stack.getItem();
		for (CauldronOverride override : CAULDRON_OVERRIDES) {
			if (override.matches(block, item)) {
				InteractionResult result = override.interaction().interact(state, level, pos, player, hand, stack);
				if (result.consumesAction()) {
					return result;
				}
			}
		}
		return InteractionResult.PASS;
	}

	/** Creates an entry for a fluid tag */
	public static Predicate<Item> fluidTag(TagKey<Fluid> fluid) {
		return item -> item instanceof BucketItem bucket && bucket.getFluid().is(fluid);
	}

	/** Creates an entry for a item tag */
	public static Predicate<Item> itemTag(TagKey<Item> tag) {
		return item -> RegistryHelper.contains(tag, item);
	}

	/** Creates an entry for a block tag */
	public static Predicate<Block> exactBlock(Block check) {
		return block -> block == check;
	}

	private record CauldronOverride(Predicate<Block> blockPredicate, Predicate<Item> itemPredicate, CauldronInteraction interaction) {
		/** Checks if this predicate matches the given context */
		public boolean matches(Block block, Item item) {
			return blockPredicate.test(block) && itemPredicate.test(item);
		}
	}
}
