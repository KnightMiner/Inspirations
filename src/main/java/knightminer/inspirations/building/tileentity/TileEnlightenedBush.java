package knightminer.inspirations.building.tileentity;

import javax.annotation.Nonnull;

import knightminer.inspirations.library.util.TextureBlockUtil;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;

public class TileEnlightenedBush extends TileEntity {
	/*
	 * Networking
	 */
	@Nonnull
	@Override
	public NBTTagCompound getUpdateTag() {
		// new tag instead of super since default implementation calls the super of writeToNBT
		return writeToNBT(new NBTTagCompound());
	}

	@Override
	public SPacketUpdateTileEntity getUpdatePacket() {
		// note that this sends all of the tile data. you should change this if you use additional tile data
		NBTTagCompound tag = getTileData().copy();
		writeToNBT(tag);
		return new SPacketUpdateTileEntity(this.getPos(), this.getBlockMetadata(), tag);
	}

	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
		NBTTagCompound tag = pkt.getNbtCompound();
		NBTBase texture = tag.getTag(TextureBlockUtil.TAG_TEXTURE);
		if(texture != null) {
			getTileData().setTag(TextureBlockUtil.TAG_TEXTURE, texture);
		}
		readFromNBT(tag);
	}
}
