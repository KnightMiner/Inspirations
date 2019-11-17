package knightminer.inspirations.utility.tileentity;

import knightminer.inspirations.common.Config;
import knightminer.inspirations.utility.InspirationsUtility;
import knightminer.inspirations.utility.block.PipeBlock;
import knightminer.inspirations.utility.inventory.PipeContainer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.HopperTileEntity;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import slimeknights.mantle.tileentity.InventoryTileEntity;

import javax.annotation.Nonnull;

public class PipeTileEntity extends InventoryTileEntity implements ITickableTileEntity {
	public static ITextComponent TITLE = new TranslationTextComponent("gui.inspirations.pipe");
	private short cooldown = 0;

	public PipeTileEntity() {
		super(InspirationsUtility.tilePipe, TITLE, 1);
	}

	@Override
	public void tick() {
		if(world == null || world.isRemote) {
			return;
		}

		// do not function if facing up when disallowed
		Direction facing = this.getFacing();
		if(!Config.pipeUpwards.get() && facing == Direction.UP) {
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
			te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, facing.getOpposite()).ifPresent((neighbor) -> {
				ItemStack copy = stack.copy();
				copy.setCount(1);
				// if we successfully place it in, shrink it here
				if(ItemHandlerHelper.insertItemStacked(neighbor, copy, false).isEmpty()) {
					if(te instanceof HopperTileEntity) {
						((HopperTileEntity)te).setTransferCooldown(7);
					}

					// remove the stack if empty
					stack.shrink(1);
					if(stack.isEmpty()) {
						this.setInventorySlotContents(0, ItemStack.EMPTY);
					}
					cooldown = 8;

					this.markDirty();
				}
			});
		}
	}

	@Override
	public void setInventorySlotContents(int slot, @Nonnull ItemStack itemstack) {
		super.setInventorySlotContents(slot, itemstack);
		cooldown = 7; // set the cooldown to prevent instant retransfer
	}

	private Direction getFacing() {
		return this.getBlockState().get(PipeBlock.FACING);
	}


	/* GUI */

	@Nonnull
	@Override
	public Container createMenu(int winId, @Nonnull PlayerInventory inv, @Nonnull PlayerEntity entity) {
		return new PipeContainer(winId, inv, this);
	}


	/* Networking */

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
		read(tag);
	}


	/* NBT */

	private static final String TAG_COOLDOWN = "cooldown";

	@Override
	public void read(CompoundNBT tags) {
		super.read(tags);
		this.cooldown = tags.getShort(TAG_COOLDOWN);
	}

	@Nonnull
	@Override
	public CompoundNBT write(CompoundNBT tags) {
		super.write(tags);
		tags.putShort(TAG_COOLDOWN, this.cooldown);

		return tags;
	}
}
