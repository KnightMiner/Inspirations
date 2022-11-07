package knightminer.inspirations.cauldrons.block.entity;

import knightminer.inspirations.cauldrons.InspirationsCaudrons;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.SuspiciousStewItem;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ForgeHooks;
import slimeknights.mantle.block.entity.MantleBlockEntity;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

import static net.minecraft.world.item.SuspiciousStewItem.EFFECT_DURATION_TAG;
import static net.minecraft.world.item.SuspiciousStewItem.EFFECT_ID_TAG;

/** Block entity for cauldron suspicious stew contents */
public class SuspiciousStewCauldronBlockEntity extends MantleBlockEntity {
	/** Effect name */
	private static final String EFFECT_NAME = "forge:effect_id";

	private ListTag effects = new ListTag();
	public SuspiciousStewCauldronBlockEntity(BlockPos pos, BlockState state) {
		super(InspirationsCaudrons.suspiciousStewCauldronEntity, pos, state);
	}

	/** Gets the stew contents */
	public ListTag getEffects() {
		return effects;
	}

	/** Sets the effects in the cauldron */
	public void setEffects(ListTag effects) {
		this.effects = effects.copy();
		this.setChangedFast();
	}

	/** Updates the effects in this cauldron */
	public void addEffect(MobEffect effect, int duration) {
		CompoundTag effectTag = new CompoundTag();
		effectTag.putByte(EFFECT_ID_TAG, (byte)MobEffect.getId(effect));
		ForgeHooks.saveMobEffect(effectTag, "forge:effect_id", effect);
		effectTag.putInt(EFFECT_DURATION_TAG, duration);
		effects.add(effectTag);
		this.setChangedFast();
	}

	/** Gets the effect stored in the tag */
	@Nullable
	private static MobEffect getEffect(CompoundTag tag) {
		MobEffect effect = MobEffect.byId(tag.getByte(EFFECT_ID_TAG));
		return ForgeHooks.loadMobEffect(tag, EFFECT_NAME, effect);
	}

	/** Scale the effect down */
	private static void scaleList(Map<MobEffect,CompoundTag> existingEffects, ListTag list, int size) {
		for (int i = 0; i < list.size(); i++) {
			CompoundTag effectTag = list.getCompound(i);
			MobEffect effect = getEffect(effectTag);
			if (effect != null) {
				int duration = effectTag.getInt(EFFECT_DURATION_TAG) * size;
				// if the effect already existed, merge it in
				CompoundTag existing = existingEffects.get(effect);
				if (existing != null) {
					existing.putInt(EFFECT_DURATION_TAG, duration + existing.getInt(EFFECT_DURATION_TAG));
				} else {
					effectTag = effectTag.copy();
					effectTag.putInt(EFFECT_DURATION_TAG, duration);
					existingEffects.put(effect, effectTag);
				}
			}
		}
	}

	/** Merges the list of effects into the internal list */
	public void mergeEffects(int existingSize, ListTag mergeEffects, int mergeSize) {
		// start by boosting all existing durations
		Map<MobEffect,CompoundTag> existingEffects = new HashMap<>();
		scaleList(existingEffects, effects, existingSize);
		scaleList(existingEffects, mergeEffects, mergeSize);

		effects = new ListTag();
		int totalSize = mergeSize + existingSize;
		for (CompoundTag effect : existingEffects.values()) {
			effect.putInt(EFFECT_DURATION_TAG, effect.getInt(EFFECT_DURATION_TAG) / totalSize);
			effects.add(effect);
		}
		this.setChangedFast();
	}

	@Override
	public void load(CompoundTag nbt) {
		super.load(nbt);
		this.effects = nbt.getList(SuspiciousStewItem.EFFECTS_TAG, Tag.TAG_COMPOUND).copy();
	}

	@Override
	public void saveAdditional(CompoundTag nbt) {
		super.saveAdditional(nbt);
		nbt.put(SuspiciousStewItem.EFFECTS_TAG, this.effects.copy());
	}
}
