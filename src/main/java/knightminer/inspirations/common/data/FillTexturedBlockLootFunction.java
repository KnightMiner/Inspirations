package knightminer.inspirations.common.data;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import knightminer.inspirations.library.util.TextureBlockUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootFunction;
import net.minecraft.world.storage.loot.LootParameter;
import net.minecraft.world.storage.loot.LootParameters;
import net.minecraft.world.storage.loot.conditions.ILootCondition;

import javax.annotation.Nonnull;
import java.util.Set;

/**
 * Applies the data for a texturable block to the dropped item.
 * No configuration is necessary.
 */
public class FillTexturedBlockLootFunction extends LootFunction {
	public FillTexturedBlockLootFunction(ILootCondition[] conditions) {
		super(conditions);
	}

	@Nonnull
	@Override
	protected ItemStack doApply(@Nonnull ItemStack stack, @Nonnull LootContext context) {
		TileEntity te = context.get(LootParameters.BLOCK_ENTITY);
		if (te != null) {
			CompoundNBT nbt = TextureBlockUtil.getTextureBlock(te);
			stack.getOrCreateTag().put(TextureBlockUtil.TAG_TEXTURE, nbt.copy());
		}
		return stack;
	}

	@Nonnull
	@Override
	public Set<LootParameter<?>> getRequiredParameters() {
		return ImmutableSet.of(LootParameters.BLOCK_ENTITY);
	}

	public static class Serializer extends LootFunction.Serializer<FillTexturedBlockLootFunction> {
		public Serializer(ResourceLocation location) {
			super(location, FillTexturedBlockLootFunction.class);
		}

		@Nonnull
		@Override
		public FillTexturedBlockLootFunction deserialize(@Nonnull JsonObject json, @Nonnull JsonDeserializationContext ctx, @Nonnull ILootCondition[] conditions) {
			return new FillTexturedBlockLootFunction(conditions);
		}
	}
}
