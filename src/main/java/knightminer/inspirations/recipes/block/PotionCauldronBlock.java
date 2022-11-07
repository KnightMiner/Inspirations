package knightminer.inspirations.recipes.block;

import knightminer.inspirations.recipes.InspirationsRecipes;
import knightminer.inspirations.recipes.block.entity.PotionCauldronBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.List;

import static knightminer.inspirations.recipes.block.BoilingFourLayerCauldronBlock.DAMAGE_BOIL;
import static knightminer.inspirations.recipes.block.BoilingFourLayerCauldronBlock.isBoiling;

/** Cauldron that contains a data instance for the potion */
public class PotionCauldronBlock extends BoilingThreeLayerCauldronBlock implements EntityBlock {
	public PotionCauldronBlock(Properties props) {
		super(props, precipitation -> false, InspirationsRecipes.POTION_CAULDRON_INTERACTIONS);
	}

	@Override
	public void entityInside(BlockState state, Level world, BlockPos pos, Entity entity) {
		if (!world.isClientSide && isEntityInsideContent(state, pos, entity)) {
			if (isBoiling(world, pos)) {
				entity.hurt(DAMAGE_BOIL, 2.0F);
			}
			if (entity instanceof LivingEntity living) {
				PotionCauldronBlockEntity be = InspirationsRecipes.potionCauldronEntity.getBlockEntity(world, pos);
				if (be != null) {
					List<MobEffectInstance> effects = be.getPotion().getEffects();
					if (effects.stream().anyMatch(effect -> !living.hasEffect(effect.getEffect()))) {
						for (MobEffectInstance effect : effects) {
							if (effect.getEffect().isInstantenous()) {
								effect.getEffect().applyInstantenousEffect(null, null, living, effect.getAmplifier(), 1.0D);
							} else {
								living.addEffect(new MobEffectInstance(effect));
							}
						}
						lowerFillLevel(state, world, pos);
						world.playSound(null, pos, SoundEvents.GENERIC_SPLASH, SoundSource.BLOCKS, 1.0f, 1.0f);
					}
				}
			}
		}
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new PotionCauldronBlockEntity(pos, state);
	}

	@Override
	public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
		if (!state.is(newState.getBlock())) {
			// if broken, potion!
			if (!isMoving && !newState.is(BlockTags.CAULDRONS)) {
				PotionCauldronBlockEntity be = InspirationsRecipes.potionCauldronEntity.getBlockEntity(level, pos);
				if (be != null) {
					Potion potion = be.getPotion();
					int waterLevel = state.getValue(LEVEL);
					AreaEffectCloud cloud = new AreaEffectCloud(level, pos.getX() + 0.5F, pos.getY() + 0.5F, pos.getZ() + 0.5F);
					cloud.setRadius(0.5F * waterLevel + 0.5F);
					cloud.setDuration(20 * (waterLevel + 1));
					cloud.setRadiusOnUse(-0.5F);
					cloud.setWaitTime(10);
					cloud.setRadiusPerTick(-cloud.getRadius() / cloud.getDuration());
					cloud.setPotion(potion);
					for (MobEffectInstance effect : potion.getEffects()) {
						cloud.addEffect(new MobEffectInstance(effect));
					}
					level.addFreshEntity(cloud);
					level.playSound(null, pos, SoundEvents.SPLASH_POTION_BREAK, SoundSource.BLOCKS, 1.0f, 1.0f);
				}
			}
			level.removeBlockEntity(pos);
		}
	}
}
