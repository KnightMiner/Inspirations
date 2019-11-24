package knightminer.inspirations.building.tileentity;

import knightminer.inspirations.building.InspirationsBuilding;
import knightminer.inspirations.library.client.ClientUtil;
import knightminer.inspirations.library.util.TextureBlockUtil;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.data.ModelDataMap;
import net.minecraftforge.client.model.data.ModelProperty;

import javax.annotation.Nonnull;

public class EnlightenedBushTileEntity extends TileEntity {

	public static final ModelProperty<String> TEXTURE = TextureBlockUtil.TEXTURE_PROP;

	public EnlightenedBushTileEntity() {
		super(InspirationsBuilding.tileEnlightenedBush);
	}


	/*
	 * Rendering
	 */
	@Nonnull
	@Override
	public IModelData getModelData() {
		// texture not loaded
		ModelDataMap.Builder data = new ModelDataMap.Builder();
		String texture = ClientUtil.getTexturePath(this);
		if(!texture.isEmpty()) {
			data = data.withInitial(TEXTURE, texture);
		}
		return data.build();
	}

	/*
	 * Networking
	 */
	@Nonnull
	@Override
	public CompoundNBT getUpdateTag() {
		// new tag instead of super since default implementation calls the super of writeToNBT
		return write(new CompoundNBT());
	}

	@Override
	public SUpdateTileEntityPacket getUpdatePacket() {
		// note that this sends all of the tile data. you should change this if you use additional tile data
		CompoundNBT tag = getTileData().copy();
		write(tag);
		return new SUpdateTileEntityPacket(this.getPos(), 0, tag);
	}

	@Override
	public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
		CompoundNBT tag = pkt.getNbtCompound();
		TextureBlockUtil.updateTextureBlock(this, tag);
		read(tag);
	}
}
