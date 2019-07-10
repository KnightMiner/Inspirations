package knightminer.inspirations.plugins.tan;

import com.google.common.collect.ImmutableList;
import knightminer.inspirations.library.recipe.cauldron.ISimpleCauldronRecipe;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;

import java.util.List;

public class FillCanteenRecipe implements ISimpleCauldronRecipe {

	private Item canteen;
	private int meta, current, type;
	private CauldronState fluid;
	// JEI data
	private List<ItemStack> input;
	private ItemStack result;

	public FillCanteenRecipe(Item canteen, int current, int type, Fluid fluid) {
		this.canteen = canteen;
		// canteen level
		this.current = current;
		// meta offset for canteen
		this.type = type;
		// detemine the metadata for the input, easier than calculating it two places
		this.meta = current == 0 ? 0 : (3 - current) * 4 + type;
		this.fluid = CauldronState.fluid(fluid);

		// JEI data
		this.input = ImmutableList.of(new ItemStack(canteen, 1, meta));
		this.result = new ItemStack(canteen, 1, type);
	}

	@Override
	public boolean matches(ItemStack stack, boolean boiling, int level, CauldronState state) {
		return level >= 1 && fluid.matches(state)
				&& stack.getItem() == canteen && stack.getMetadata() == meta;
	}

	@Override
	public ItemStack getResult(ItemStack stack, boolean boiling, int level, CauldronState state) {
		if(level + current >= 3) {
			return new ItemStack(canteen, 1, type);
		}
		return new ItemStack(canteen, 1, ((3 - (level + current)) * 4) + type);
	}

	@Override
	public int getInputLevel() {
		return 3 - current;
	}

	@Override
	public int getLevel(int level) {
		int total = level + current;
		return total <= 3 ? 0 : total - 3;
	}

	/* JEI */
	@Override
	public List<ItemStack> getInput() {
		return input;
	}

	@Override
	public ItemStack getResult() {
		return result;
	}

	@Override
	public Object getInputState() {
		return fluid.getFluid();
	}
}
