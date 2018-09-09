package knightminer.inspirations.recipes.recipe;

import knightminer.inspirations.common.Config;
import knightminer.inspirations.library.recipe.cauldron.CauldronFluidRecipe;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import slimeknights.mantle.util.RecipeMatch;

public class SpongeEmptyCauldron extends CauldronFluidRecipe {

	public static final SpongeEmptyCauldron INSTANCE = new SpongeEmptyCauldron();

	private SpongeEmptyCauldron() {
		super(RecipeMatch.of(new ItemStack(Blocks.SPONGE, 1, 0)),
				null,
				new ItemStack(Blocks.SPONGE, 1, 1),
				null, Config.spongeCauldronFull ? 3 : 1
				);
	}

	@Override
	protected boolean matches(CauldronState state) {
		// matches water, dye, or potions
		return state.isWater() || state.getColor() > -1 || state.getPotion() != null;
	}

	@Override
	public int getLevel(int level) {
		return 0;
	}
}
