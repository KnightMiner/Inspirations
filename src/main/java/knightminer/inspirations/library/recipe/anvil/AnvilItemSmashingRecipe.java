package knightminer.inspirations.library.recipe.anvil;

import knightminer.inspirations.library.Util;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import slimeknights.mantle.util.RecipeMatch;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
public class AnvilItemSmashingRecipe implements ISimpleAnvilRecipe {
	protected RecipeMatch input;
	private ItemStack result;
	@Nullable
	protected IBlockState state;
	@Nullable
	private Integer fallHeight;

	public AnvilItemSmashingRecipe(RecipeMatch input, ItemStack result, @Nullable IBlockState state,
			@Nullable Integer fallHeight) {
		this.input = input;
		this.result = result;
		this.state = state;
		this.fallHeight = fallHeight;
	}

	public AnvilItemSmashingRecipe(RecipeMatch input, ItemStack result, @Nullable IBlockState state) {
		this(input, result, state, null);
	}

	public AnvilItemSmashingRecipe(RecipeMatch input, ItemStack result, @Nullable Integer fallHeight) {
		this(input, result, null, fallHeight);
	}

	public AnvilItemSmashingRecipe(RecipeMatch input, ItemStack result) {
		this(input, result, null, null);
	}

	@Override
	public boolean matches(ItemStack stack, int height, IBlockState state) {
		// match the conditions if they are set
		if(!fallHeightMatches(height) || !stateMatches(state)) {
			return false;
		}

		return this.input.matches(Util.createNonNullList(stack)).isPresent();
	}

	/**
	 * Match the actual block state to the required one.
	 * @param state input block state
	 * @return true if there is either no requirement for a specific block state or it matches the actual one
	 */
	private boolean stateMatches(IBlockState state) {
		return this.state == null || state == this.state;
	}

	/**
	 * Match the actual fall height to the required one.
	 * @param height the actual fall height of the anvil
	 * @return true if there is either no fall height requirement or the actual height is greater or equal to it
	 */
	private boolean fallHeightMatches(int height) {
		return this.fallHeight == null || height >= this.fallHeight;
	}

	@Override
	public List<ItemStack> getInput() {
		return input.getInputs();
	}

	@Override
	public ItemStack getResult() {
		return result;
	}

	@Override
	@Nullable
	public Integer getFallHeight() {
		return fallHeight;
	}

	@Override
	public NonNullList<ItemStack> transformInput(ItemStack stack, int fallHeight, IBlockState state) {
		// assume this recipe matches, otherwise this method shouldn't have been called
		RecipeMatch.Match match = input.matches(Util.createNonNullList(stack)).get();

		// calculate the number of times this recipe can be applied to the input item stack
		int matchCount = stack.getCount() / match.amount;

		// calculate the remainder of the input item stack that doesn't fit with the recipe
		int remainder = stack.getCount() % match.amount;

		// modify the input stack
		stack.setCount(remainder);

		// transform the output stack
		int totalCount = result.getCount() * matchCount;
		return getItemStacks(result, totalCount);
	}

	/**
	 * Creates item stacks from the template that sum up to the given total count and respect the max stack size for
	 * the item.
	 * @param template   the template item stack, is read only
	 * @param totalCount the sum of all item stacks
	 * @return a list of non empty item stacks whose counts sum up to the given total
	 */
	public static NonNullList<ItemStack> getItemStacks(@Nonnull ItemStack template, int totalCount) {
		NonNullList<ItemStack> outList = NonNullList.create();

		// respect max stack size of the template
		int maxStackSize = template.getMaxStackSize();

		int remainingStackSize = totalCount;
		while(remainingStackSize > 0) {
			ItemStack out = template.copy();
			int count = Math.min(maxStackSize, remainingStackSize);
			out.setCount(count);
			outList.add(out);
			remainingStackSize -= count;
		}
		return outList;
	}

	@Override
	public String toString() {
		return String.format("AnvilItemSmashingRecipe: %s from %s", result.toString(), input.getInputs());
	}
}
