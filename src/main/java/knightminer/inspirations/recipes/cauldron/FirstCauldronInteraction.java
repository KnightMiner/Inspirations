package knightminer.inspirations.recipes.cauldron;

import net.minecraft.core.BlockPos;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

/** Merges multiple interactions into one, using the first that matches */
public record FirstCauldronInteraction(CauldronInteraction... interactions) implements CauldronInteraction {
	@Override
	public InteractionResult interact(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, ItemStack stack) {
		for (CauldronInteraction interaction : interactions) {
			InteractionResult result = interaction.interact(state, level, pos, player, hand, stack);
			if (result.consumesAction()) {
				return result;
			}
		}
		return InteractionResult.PASS;
	}
}
