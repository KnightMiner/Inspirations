package knightminer.inspirations.common.data;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import knightminer.inspirations.library.util.TextureBlockUtil;
import knightminer.inspirations.shared.InspirationsShared;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootFunction;
import net.minecraft.loot.LootFunctionType;
import net.minecraft.loot.LootParameter;
import net.minecraft.loot.LootParameters;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.tileentity.TileEntity;

import java.util.Set;

/**
 * Applies the data for a texturable block to the dropped item.
 * No configuration is necessary.
 */
@SuppressWarnings("WeakerAccess")
public class FillTexturedBlockLootFunction extends LootFunction {
	/**
	 * Creates a new instance from the given conditions
	 * @param conditions  Conditions list
	 */
	public FillTexturedBlockLootFunction(ILootCondition[] conditions) {
		super(conditions);
	}

	/**
	 * Creates a new instance with no conditions
	 */
	public FillTexturedBlockLootFunction() {
		super(new ILootCondition[0]);
	}

	@Override
	protected ItemStack doApply(ItemStack stack, LootContext context) {
		TileEntity te = context.get(LootParameters.BLOCK_ENTITY);
		if (te != null) {
			TextureBlockUtil.setStackTexture(stack, TextureBlockUtil.getTextureBlockName(te));
		}
		return stack;
	}

	@Override
	public Set<LootParameter<?>> getRequiredParameters() {
		return ImmutableSet.of(LootParameters.BLOCK_ENTITY);
	}

	@Override
	public LootFunctionType func_230425_b_() {
		return InspirationsShared.textureFunction;
	}

	public static class Serializer extends LootFunction.Serializer<FillTexturedBlockLootFunction> {
		@Override
		public FillTexturedBlockLootFunction deserialize(JsonObject json, JsonDeserializationContext ctx, ILootCondition[] conditions) {
			return new FillTexturedBlockLootFunction(conditions);
		}
	}
}
