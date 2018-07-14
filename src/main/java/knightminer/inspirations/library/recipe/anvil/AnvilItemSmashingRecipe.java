package knightminer.inspirations.library.recipe.anvil;

import java.util.List;
import java.util.Optional;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import slimeknights.mantle.util.ItemStackList;
import slimeknights.mantle.util.RecipeMatch;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class AnvilItemSmashingRecipe implements ISimpleAnvilRecipe {
	private RecipeMatch input;
	private ItemStackList result;
	private IBlockState state;
	private Integer fallHeight;

	/**
	 * Recipe for item smashing
	 * @param input matcher for the inputs
	 * @param result list of results
	 * @param fallHeight required minimum fall height, can be null if there is no requirement
	 * @param state required input block state, can be null if there is no requirement
	 */
	public AnvilItemSmashingRecipe(RecipeMatch input, ItemStackList result, @Nullable Integer fallHeight,
			@Nullable IBlockState state) {
		this.input = input;
		this.result = result;
		this.state = state;
		this.fallHeight = fallHeight;
	}

	/**
	 * Recipe for item smashing
	 * @param input matcher for the inputs
	 * @param result list of results
	 * @param state required input block state, can be null if there is no requirement
	 */
	public AnvilItemSmashingRecipe(RecipeMatch input, ItemStackList result, @Nullable IBlockState state) {
		this(input, result, null, state);
	}

	/**
	 * Recipe for item smashing
	 * @param input matcher for the inputs
	 * @param result list of results
	 * @param fallHeight required minimum fall height, can be null if there is no requirement
	 */
	public AnvilItemSmashingRecipe(RecipeMatch input, ItemStackList result, @Nullable Integer fallHeight) {
		this(input, result, fallHeight, null);
	}

	/**
	 * Recipe for item smashing
	 * @param input matcher for the inputs
	 * @param result list of results
	 */
	public AnvilItemSmashingRecipe(RecipeMatch input, ItemStackList result) {
		this(input, result, null, null);
	}

	@Override
	public boolean matches(NonNullList<ItemStack> stack, int height, IBlockState state) {
		// match the conditions if they are set
		if(!fallHeightMatches(height) || !stateMatches(state)) {
			return false;
		}

		return this.input.matches(stack).isPresent();
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
	public List<ItemStack> getResult() {
		return result;
	}

	@Override
	public Optional<Integer> getFallHeight() {
		return Optional.ofNullable(fallHeight);
	}

	@Override
	public Optional<IBlockState> getState() {
		return Optional.ofNullable(this.state);
	}

	@Override
	public NonNullList<ItemStack> getOutputs(NonNullList<ItemStack> stack, int fallHeight, IBlockState state) {
		// assume this recipe matches, otherwise this method shouldn't have been called
		RecipeMatch.Match match = input.matches(stack).get();

		// remove the matching stacks
		RecipeMatch.removeMatch(stack, match);

		// return the results
		return result.deepCopy(true);
	}

	@Override
	public String toString() {
		return String.format("AnvilItemSmashingRecipe: %s from %s", result.toString(), input.getInputs());
	}
}
