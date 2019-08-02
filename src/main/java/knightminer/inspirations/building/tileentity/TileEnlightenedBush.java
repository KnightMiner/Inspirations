package knightminer.inspirations.building.tileentity;

import javax.annotation.Nonnull;

import knightminer.inspirations.library.util.TextureBlockUtil;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;

public class TileEnlightenedBush extends TileEntity {

	public TileEnlightenedBush(TileEntityType<?> tileEntityTypeIn) {
		super(tileEntityTypeIn);
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
		INBT texture = tag.get(TextureBlockUtil.TAG_TEXTURE);
		if(texture != null) {
			getTileData().put(TextureBlockUtil.TAG_TEXTURE, texture);
		}
		read(tag);
	}
}
