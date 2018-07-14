package knightminer.inspirations.library.recipe.anvil;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
	public List<List<ItemStack>> getInput() {
		// if the recipe match is a composite all the composed ones must be checked
		Stream<? extends RecipeMatch> matches = (this.input instanceof CompositeRecipeMatch) ?
				((CompositeRecipeMatch) this.input).getRecipeMatches().stream() :
				Stream.of(this.input);
		return matches.map(match -> {
			// ore dict will return a list of stacks of size 1, so transform those stacks into stacks with the
			// actual required size so JEI can show them correctly
			if(match instanceof RecipeMatch.Oredict) {
				return match.getInputs().stream().map(itemStack -> {
					ItemStack copy = itemStack.copy();
					copy.setCount(match.amountNeeded);
					return copy;
				}).collect(Collectors.toList());
			}
			else {
				// no special handling required
				return match.getInputs();
			}
		}).collect(Collectors.toList());
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
		// collect all results
		ItemStackList output = ItemStackList.create();

		// since the recipe match does only apply once the easiest way is to loop until there is no more match found
		Stream.generate(() -> input.matches(stack).map(match -> {
			// remove the matching stacks
			RecipeMatch.removeMatch(stack, match);

			// return the results
			output.addAll(result.deepCopy(true));
			return false;
		}).orElse(true)).findAny();
		return output;
	}

	@Override
	public String toString() {
		return String.format("AnvilItemSmashingRecipe: %s from %s", result.toString(), input.getInputs());
	}
}
