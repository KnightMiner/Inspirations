package knightminer.inspirations.recipes.block;

import knightminer.inspirations.Inspirations;
import knightminer.inspirations.library.InspirationsTags;
import knightminer.inspirations.library.MiscUtil;
import knightminer.inspirations.recipes.InspirationsRecipes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import java.util.Map;
import java.util.Random;

/** Cauldron block that has four layers and emits boiling particles when over fire */
public class BoilingFourLayerCauldronBlock extends FourLayerCauldronBlock {
	public static final DamageSource DAMAGE_BOIL = new DamageSource(Inspirations.prefix("boiling")).bypassArmor();

	public BoilingFourLayerCauldronBlock(Properties props, Map<Item,CauldronInteraction> interactions) {
		super(props, interactions);
	}

	@Override
	public void entityInside(BlockState state, Level world, BlockPos pos, Entity entity) {
		if (!world.isClientSide && isEntityInsideContent(state, pos, entity) && isBoiling(world, pos)) {
			entity.hurt(DAMAGE_BOIL, 2.0F);
		}
	}

	@Override
	public void animateTick(BlockState state, Level level, BlockPos pos, Random rand) {
		if (isBoiling(level, pos)) {
			MiscUtil.addParticles(InspirationsRecipes.boilingParticle, level, pos, 2, getContentHeight(state), rand);
		}
	}

	/** Checks if this cauldron is boiling */
	public static boolean isBoiling(Level level, BlockPos pos) {
		BlockState state = level.getBlockState(pos.below());
		if (state.is(InspirationsTags.Blocks.CAULDRON_FIRE)) {
			// if it has a lit property, use that (campfires, furnaces). Otherwise just needs to be in the tag
			return !state.hasProperty(BlockStateProperties.LIT) || state.getValue(BlockStateProperties.LIT);
		}
		return false;
	}
}
