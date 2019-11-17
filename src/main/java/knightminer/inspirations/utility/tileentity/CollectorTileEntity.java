package knightminer.inspirations.utility.tileentity;

import knightminer.inspirations.utility.InspirationsUtility;
import knightminer.inspirations.utility.inventory.CollectorContainer;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import slimeknights.mantle.tileentity.InventoryTileEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class CollectorTileEntity extends InventoryTileEntity {
	public static ITextComponent TITLE = new TranslationTextComponent("gui.inspirations.collector");

	public CollectorTileEntity() {
		super(InspirationsUtility.tileCollector, TITLE, 9);
	}

	public void collect(Direction facing) {
		BlockPos offset = pos.offset(facing);
		TileEntity te = world.getTileEntity(offset);
		// if we have a TE and its an item handler, try extracting from that
		if(te != null) {
			te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, facing.getOpposite()).ifPresent((neighbor) -> {
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
			});
		}

		// collect items from world
		AxisAlignedBB aabb = new AxisAlignedBB(offset.getX(), offset.getY(), offset.getZ(), offset.getX()+1, offset.getY()+1, offset.getZ()+1);
		boolean collected = false;
		for(ItemEntity entity : world.getEntitiesWithinAABB(ItemEntity.class, aabb)) {
			ItemStack insert = entity.getItem();
			// no need to simulate, if successful we have to modify the stack regardless
			ItemStack remainder = ItemHandlerHelper.insertItemStacked(itemHandler, insert, false);
			// if the stack changed, we were successful
			if(remainder.getCount() < insert.getCount()) {
				collected = true;
				// empty means item is gone
				if(remainder.isEmpty()) {
					entity.remove();
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

	@Nullable
	@Override
	public Container createMenu(int winId, @Nonnull PlayerInventory playerInv, @Nonnull PlayerEntity player) {
		return new CollectorContainer(winId, playerInv, this);
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
}
