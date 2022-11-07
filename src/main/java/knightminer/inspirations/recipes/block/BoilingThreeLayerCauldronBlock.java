package knightminer.inspirations.recipes.block;

import knightminer.inspirations.library.MiscUtil;
import knightminer.inspirations.recipes.InspirationsRecipes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome.Precipitation;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Map;
import java.util.Random;
import java.util.function.Predicate;

import static knightminer.inspirations.recipes.block.BoilingFourLayerCauldronBlock.DAMAGE_BOIL;
import static knightminer.inspirations.recipes.block.BoilingFourLayerCauldronBlock.isBoiling;

/** Cauldron block that has four layers and emits boiling particles when over fire */
public class BoilingThreeLayerCauldronBlock extends LayeredCauldronBlock {
	public BoilingThreeLayerCauldronBlock(Properties props, Predicate<Precipitation> fillPredicate, Map<Item,CauldronInteraction> interactions) {
		super(props, fillPredicate, interactions);
	}

	@Override
	public void entityInside(BlockState state, Level world, BlockPos pos, Entity entity) {
		if (!world.isClientSide && isEntityInsideContent(state, pos, entity)) {
			if (isBoiling(world, pos)) {
				entity.hurt(DAMAGE_BOIL, 2.0F);
			}
			if (entity.isOnFire()) {
				entity.clearFire();
				if (entity.mayInteract(world, pos)) {
					this.handleEntityOnFireInside(state, world, pos);
				}
			}
		}
	}

	@Override
	public void animateTick(BlockState state, Level level, BlockPos pos, Random rand) {
		if (isBoiling(level, pos)) {
			MiscUtil.addParticles(InspirationsRecipes.boilingParticle, level, pos, 2, getContentHeight(state), rand);
		}
	}
}
