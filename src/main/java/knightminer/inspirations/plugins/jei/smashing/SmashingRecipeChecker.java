package knightminer.inspirations.plugins.jei.smashing;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import knightminer.inspirations.library.InspirationsRegistry;
import knightminer.inspirations.library.Util;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

public class SmashingRecipeChecker {
	public static List<SmashingRecipeWrapper> getRecipes() {
		List<SmashingRecipeWrapper> recipes = new ArrayList<>();
		// block state to block state
		for(Map.Entry<IBlockState, IBlockState> entry : InspirationsRegistry.getAllAnvilStateSmashing()) {
			ItemStack input = Util.getStackFromState(entry.getKey());
			ItemStack output = Util.getStackFromState(entry.getValue());
			if(!input.isEmpty() && !output.isEmpty()) {
				recipes.add(new SmashingRecipeWrapper(input, output));
			}
		}
		// block to block state
		for(Map.Entry<Block, IBlockState> entry : InspirationsRegistry.getAllAnvilBlockSmashing()) {
			ItemStack output = Util.getStackFromState(entry.getValue());
			Block block = entry.getKey();
			if(block != null && block != Blocks.AIR && !output.isEmpty()) {
				recipes.add(new SmashingRecipeWrapper(entry.getKey(), output));
			}
		}

		return recipes;
	}
}
