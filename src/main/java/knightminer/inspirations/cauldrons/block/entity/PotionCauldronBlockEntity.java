package knightminer.inspirations.cauldrons.block.entity;

import knightminer.inspirations.cauldrons.InspirationsCaudrons;
import knightminer.inspirations.common.network.CauldronPotionUpdatePacket;
import knightminer.inspirations.common.network.InspirationsNetwork;
import knightminer.inspirations.library.MiscUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;
import slimeknights.mantle.block.entity.MantleBlockEntity;

import java.util.Objects;

/** Block entity for a cauldron that also holds a potion */
public class PotionCauldronBlockEntity extends MantleBlockEntity {
	private static final String TAG_POTION = "potion";
	private Potion potion = Potions.EMPTY;
	public PotionCauldronBlockEntity(BlockPos pos, BlockState state) {
		super(InspirationsCaudrons.potionCauldronEntity, pos, state);
	}

	/** Gets the potion */
	public Potion getPotion() {
		return potion;
	}

	/**
	 * Updates the potion, sending the proper packet on the server
	 * @param potion  New potion
	 * @return  True if the potion changed
	 */
	public boolean setPotion(Potion potion) {
		if (potion != this.potion) {
			this.potion = potion;
			if (level != null && !level.isClientSide) {
				InspirationsNetwork.sendToClients(level, worldPosition, new CauldronPotionUpdatePacket(worldPosition, potion));
			}
			this.setChangedFast();
			return true;
		}
		return false;
	}

	@Override
	protected boolean shouldSyncOnUpdate() {
		return true;
	}

	@Override
	protected void saveSynced(CompoundTag nbt) {
		super.saveSynced(nbt);

		nbt.putString(TAG_POTION, Objects.requireNonNull(potion.getRegistryName()).toString());
	}

	/** Parses a potion from the key */
	private static Potion parsePotion(String key) {
		ResourceLocation id = ResourceLocation.tryParse(key);
		if (id != null) {
			return Objects.requireNonNullElse(ForgeRegistries.POTIONS.getValue(id), Potions.EMPTY);
		}
		return Potions.EMPTY;
	}

	@Override
	public void load(CompoundTag nbt) {
		super.load(nbt);

		potion = parsePotion(nbt.getString(TAG_POTION));
		if (level != null && level.isClientSide) {
			MiscUtil.notifyClientUpdate(this);
		}
	}
}
