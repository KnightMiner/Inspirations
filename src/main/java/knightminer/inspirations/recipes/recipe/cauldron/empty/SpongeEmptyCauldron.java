package knightminer.inspirations.recipes.recipe.cauldron.empty;

import knightminer.inspirations.common.Config;
import knightminer.inspirations.library.recipe.cauldron.FluidCauldronRecipe;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potions;
import slimeknights.mantle.util.RecipeMatch;

@Deprecated
public class SpongeEmptyCauldron extends FluidCauldronRecipe {

	public static final SpongeEmptyCauldron INSTANCE = new SpongeEmptyCauldron();

	private SpongeEmptyCauldron() {
		super(RecipeMatch.of(new ItemStack(Blocks.SPONGE)),
					Fluids.EMPTY,
					new ItemStack(Blocks.WET_SPONGE),
					null, Config.canSpongeEmptyFullOnly() ? 3 : 1);
	}

	@Override
	protected boolean matches(CauldronState state) {
		// matches water, dye, or potions
		return state.isWater() || state.getColor() > -1 || state.getPotion() != Potions.EMPTY;
	}

	@Override
	public int getLevel(int level) {
		return 0;
	}

	@Override
	public ItemStack getContainer(ItemStack stack) {
		return ItemStack.EMPTY;
	}
}
