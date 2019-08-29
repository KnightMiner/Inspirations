package knightminer.inspirations.common.recipe;

import com.google.gson.JsonObject;
import knightminer.inspirations.Inspirations;
import knightminer.inspirations.library.Util;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.IConditionSerializer;

public class PulseLoaded implements ICondition {
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
	public ResourceLocation getID() {
		return ID;
	}

	public static class Serializer implements IConditionSerializer<PulseLoaded> {
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
	}
}
