package knightminer.inspirations.common.data;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import knightminer.inspirations.Inspirations;
import knightminer.inspirations.library.Util;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.conditions.ILootCondition;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.IConditionSerializer;

import javax.annotation.Nonnull;

// Reuse code for both a recipe and loot table condition.
public class PulseLoaded implements ICondition, ILootCondition {
	public static final ResourceLocation ID = Util.getResource("pulse_loaded");
	private final String pulseId;

	public PulseLoaded(String pulseId) {
		this.pulseId = pulseId;
	}

	@Override
	public boolean test() {
		return Inspirations.pulseManager.isPulseLoaded(pulseId);
	}

	@Override
	public boolean test(LootContext lootContext) {
		return Inspirations.pulseManager.isPulseLoaded(pulseId);
	}

	@Override
	public ResourceLocation getID() {
		return ID;
	}

	public static class Serializer extends AbstractSerializer<PulseLoaded> implements IConditionSerializer<PulseLoaded> {
		public Serializer() {
			super(ID, PulseLoaded.class);
		}

		@Override
		public void write(JsonObject json, PulseLoaded cond) {
			json.addProperty("pulse", cond.pulseId);
		}

		@Override
		public PulseLoaded read(JsonObject json) {
			String pulse = JSONUtils.getString(json, "pulse");
			return new PulseLoaded(pulse);
		}

		@Override
		public ResourceLocation getID() {
			return ID;
		}

		@Override
		public void serialize(@Nonnull JsonObject json, @Nonnull PulseLoaded cond, @Nonnull JsonSerializationContext ctx) {
			write(json, cond);
		}

		@Nonnull
		@Override
		public PulseLoaded deserialize(@Nonnull JsonObject json, @Nonnull JsonDeserializationContext ctx) {
			return read(json);
		}
	}
}
