package knightminer.inspirations.utility.tileentity;

import javax.annotation.Nonnull;

import knightminer.inspirations.utility.client.GuiCollector;
import knightminer.inspirations.utility.inventory.ContainerCollector;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import slimeknights.mantle.common.IInventoryGui;
import slimeknights.mantle.tileentity.TileInventory;

public class TileCollector extends TileInventory implements IInventoryGui {
	public TileCollector() {
		super("gui.inspirations.collector.name", 9);
	}

	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate) {
		// triggering does not mean you go and delete the TE
		return oldState.getBlock() != newSate.getBlock();
	}

	public void collect(EnumFacing facing) {
		BlockPos offset = pos.offset(facing);
		TileEntity te = world.getTileEntity(offset);
		// if we have a TE and its an item handler, try extracting from that
		if(te != null) {
			IItemHandler neighbor = te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, facing.getOpposite());
			if(neighbor != null) {
				// basically, we iterate every slot, trying to remove a single item
				for(int i = 0; i < neighbor.getSlots(); i++) {
					ItemStack simulated = neighbor.extractItem(i, 1, true);
					// as soon as we find one we can extract, we try inserting it
					if(!simulated.isEmpty()) {
						// if it successfully inserts, extract it from the original inventory
						if(ItemHandlerHelper.insertItemStacked(itemHandler, simulated, false).isEmpty()) {
							neighbor.extractItem(i, 1, false);
							break;
						}
					}
				}
				return;
			}
		}

		// collect items from world
		AxisAlignedBB aabb = new AxisAlignedBB(offset.getX(), offset.getY(), offset.getZ(), offset.getX()+1, offset.getY()+1, offset.getZ()+1);
		boolean collected = false;
		for(EntityItem entity : world.getEntitiesWithinAABB(EntityItem.class, aabb)) {
			ItemStack insert = entity.getItem();
			// no need to simulate, if successful we have to modify the stack regardless
			ItemStack remainder = ItemHandlerHelper.insertItemStacked(itemHandler, insert, false);
			// if the stack changed, we were successful
			if(remainder.getCount() < insert.getCount()) {
				collected = true;
				// empty means item is gone
				if(remainder.isEmpty()) {
					entity.setDead();
				} else {
					entity.setItem(remainder);
				}
			}
		}
		// play sound. Plays dispenser dispense if success and dispenser fail if not
		world.playEvent(collected ? 1000 : 1001, pos, 0);
	}

	@Override
	public boolean isItemValidForSlot(int slot, @Nonnull ItemStack itemstack) {
		// mantle checks stack size which breaks some things when using stacks bigger than 1
		return slot < getSizeInventory();
	}

	/*
	 * GUI
	 */

	@Override
	public ContainerCollector createContainer(InventoryPlayer inventoryplayer, World world, BlockPos pos) {
		return new ContainerCollector(inventoryplayer, this);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public GuiContainer createGui(InventoryPlayer inventoryplayer, World world, BlockPos pos) {
		return new GuiCollector(createContainer(inventoryplayer, world, pos));
	}


	/* Networking */
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
		readFromNBT(tag);
	}
}
