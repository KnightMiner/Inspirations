package knightminer.inspirations.library.recipe.anvil;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import slimeknights.mantle.util.RecipeMatch;

/**
 * Composite recipe match of multiple individual matches. This assumes the matches are mutual exclusive, otherwise
 * this won't work!
 */
public class CompositeRecipeMatch extends RecipeMatch {

	private final List<? extends RecipeMatch> recipeMatches;
	private final List<ItemStack> inputs;

	private CompositeRecipeMatch(List<? extends RecipeMatch> matches, int amountMatched, int amountNeeded) {
		super(amountMatched, amountNeeded);

		this.recipeMatches = matches;
		this.inputs = matches.stream().flatMap(match -> match.getInputs().stream()).collect(Collectors.toList());
	}

	@Override
	public List<ItemStack> getInputs() {
		return ImmutableList.copyOf(this.inputs);
	}

	@Override
	public Optional<Match> matches(NonNullList<ItemStack> stacks) {
		List<ItemStack> matchedStacks = Lists.newArrayList();

		// must match everything
		if(!this.recipeMatches.stream().map(match -> match.matches(stacks)).allMatch(match -> {
			if(match.isPresent()) {
				matchedStacks.addAll(match.get().stacks);
				return true;
			}
			return false;
		})) {
			return Optional.empty();
		}

		return Optional.of(new Match(matchedStacks, amountMatched));
	}

	/**
	 * Create a new composite recipe match. If there is only one input the original object will be used instead of
	 * the composite wrapper
	 * @param matches the recipe matches to compose
	 * @return the recipe match object composing all the matches or the single match if there is exactly one
	 */
	public static RecipeMatch of(RecipeMatch... matches) {
		return ofMatches(Arrays.asList(matches));
	}

	/**
	 * Create a new composite recipe match. If there is only one input the original object will be used instead of
	 * the composite wrapper
	 * @param matches  the recipe matches to compose
	 * @return the recipe match object composing all the matches or the single match if there is exactly one
	 */
	public static RecipeMatch ofMatches(List<? extends RecipeMatch> matches) {
		// If there is only one input use that instead of a complex wrapper around it
		if(matches.size() == 1) {
			return matches.get(0);
		}
		return new CompositeRecipeMatch(matches, 1, 1);
	}
}
