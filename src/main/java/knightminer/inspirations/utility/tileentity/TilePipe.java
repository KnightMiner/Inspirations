package knightminer.inspirations.utility.tileentity;

import javax.annotation.Nonnull;

import knightminer.inspirations.common.Config;
import knightminer.inspirations.utility.client.GuiPipe;
import knightminer.inspirations.utility.inventory.ContainerPipe;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import slimeknights.mantle.common.IInventoryGui;
import slimeknights.mantle.tileentity.TileInventory;

public class TilePipe extends TileInventory implements IInventoryGui, ITickable  {

	private short cooldown = 0;
	public TilePipe() {
		super("gui.inspirations.pipe.name", 1);
	}

	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate) {
		// triggering does not mean you go and delete the TE
		return oldState.getBlock() != newSate.getBlock();
	}

	@Override
	public void update() {
		if(world == null || world.isRemote) {
			return;
		}

		// do not function if facing up when disallowed
		EnumFacing facing = this.getFacing();
		if(!Config.pipeUpwards && facing == EnumFacing.UP) {
			return;
		}

		cooldown--;
		if(cooldown > 0) {
			return;
		}
		cooldown = 0;
		// got just 1 stack, makes life easy
		ItemStack stack = this.getStackInSlot(0);
		if(stack.isEmpty()) {
			return;
		}

		// if we have a TE and its an item handler, try inserting into that
		TileEntity te = world.getTileEntity(pos.offset(facing));
		if(te != null) {
			IItemHandler neighbor = te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, facing.getOpposite());
			if(neighbor != null) {
				ItemStack copy = stack.copy();
				copy.setCount(1);
				// if we successfully place it in, shrink it here
				if(ItemHandlerHelper.insertItemStacked(neighbor, copy, false).isEmpty()) {
					if(te instanceof TileEntityHopper) {
						((TileEntityHopper)te).setTransferCooldown(7);
					}

					// remove the stack if empty
					stack.shrink(1);
					if(stack.isEmpty()) {
						this.setInventorySlotContents(0, ItemStack.EMPTY);
					}
					cooldown = 8;

					this.markDirty();
				}
			}
		}
	}

	@Override
	public void setInventorySlotContents(int slot, @Nonnull ItemStack itemstack) {
		super.setInventorySlotContents(slot, itemstack);
		cooldown = 7; // set the cooldown to prevent instant retransfer
	}

	private EnumFacing getFacing() {
		return EnumFacing.getFront(this.getBlockMetadata() & 7);
	}


	/* GUI */

	@Override
	public ContainerPipe createContainer(InventoryPlayer inventoryplayer, World world, BlockPos pos) {
		return new ContainerPipe(inventoryplayer, this);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public GuiContainer createGui(InventoryPlayer inventoryplayer, World world, BlockPos pos) {
		return new GuiPipe(createContainer(inventoryplayer, world, pos));
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


	/* NBT */

	private static final String TAG_COOLDOWN = "cooldown";

	@Override
	public void readFromNBT(NBTTagCompound tags) {
		super.readFromNBT(tags);
		this.cooldown = tags.getShort(TAG_COOLDOWN);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tags) {
		super.writeToNBT(tags);
		tags.setShort(TAG_COOLDOWN, this.cooldown);

		return tags;
	}
}
