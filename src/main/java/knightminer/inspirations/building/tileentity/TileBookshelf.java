package knightminer.inspirations.building.tileentity;

import javax.annotation.Nonnull;

import knightminer.inspirations.building.InspirationsBuilding;
import knightminer.inspirations.building.block.BlockBookshelf;
import knightminer.inspirations.building.client.GuiBookshelf;
import knightminer.inspirations.building.inventory.ContainerBookshelf;
import knightminer.inspirations.common.network.InspirationsNetwork;
import knightminer.inspirations.common.network.InventorySlotSyncPacket;
import knightminer.inspirations.library.InspirationsRegistry;
import knightminer.inspirations.library.util.RecipeUtil;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.ItemHandlerHelper;
import slimeknights.mantle.client.ModelHelper;
import slimeknights.mantle.common.IInventoryGui;
import slimeknights.mantle.tileentity.TileInventory;

public class TileBookshelf extends TileInventory implements IInventoryGui {

	public static final String TAG_TEXTURE_PATH = "texture_path";

	public TileBookshelf() {
		super("gui.inspirations.bookshelf.name", 14, 1);
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack itemstack) {
		ItemStack oldStack = this.getStackInSlot(slot);

		// we sync slot changes to all clients around
		if(getWorld() != null && getWorld() instanceof WorldServer && !getWorld().isRemote && !ItemStack.areItemStacksEqual(itemstack, getStackInSlot(slot))) {
			InspirationsNetwork.sendToClients((WorldServer) getWorld(), this.pos, new InventorySlotSyncPacket(itemstack, slot, pos));
		}
		super.setInventorySlotContents(slot, itemstack);

		if(getWorld() != null) {
			// update for rendering
			if(getWorld().isRemote) {
				Minecraft.getMinecraft().renderGlobal.notifyBlockUpdate(null, pos, null, null, 0);
			}

			// if we have redstone books and either the old stack or the new one is a book, update
			if(InspirationsBuilding.redstoneBook != null
					&& (InspirationsBuilding.redstoneBook.isItemEqual(oldStack) ^ InspirationsBuilding.redstoneBook.isItemEqual(itemstack))) {

				world.notifyNeighborsOfStateChange(pos, this.getBlockType(), false);
				IBlockState state = world.getBlockState(pos);
				if(state.getBlock() == this.getBlockType()) {
					world.notifyNeighborsOfStateChange(pos.offset(state.getValue(BlockBookshelf.FACING).getOpposite()), this.getBlockType(), false);
				}
			}
		}
	}

	/*
	 * Book logic
	 */

	public boolean interact(EntityPlayer player, EnumHand hand, EnumFacing facing, float clickX, float clickY, float clickZ) {
		int bookClicked = bookClicked(facing, clickX, clickY, clickZ);
		if(bookClicked == -1) {
			return false;
		}

		// if it contains a book, take the book out
		if(isStackInSlot(bookClicked)) {
			ItemHandlerHelper.giveItemToPlayer(player, getStackInSlot(bookClicked), player.inventory.currentItem);
			setInventorySlotContents(bookClicked, ItemStack.EMPTY);
		} else {
			// otherwise try putting a book in
			ItemStack stack = player.getHeldItemMainhand();
			if(!InspirationsRegistry.isBook(stack)) {
				return true;
			}

			ItemStack book = player.inventory.decrStackSize(player.inventory.currentItem, stackSizeLimit);
			setInventorySlotContents(bookClicked, book);
		}
		return true;
	}


	private static int bookClicked(EnumFacing facing, float clickX, float clickY, float clickZ) {
		// if we did not click between the shelves, ignore
		if(clickY < 0.0625 || clickY > 0.9375) {
			return -1;
		}
		int shelf = 0;
		// if we clicked below the middle shelf, add 7 to the book
		if(clickY <= 0.4375) {
			shelf = 7;
			// if we clicked below the top shelf but not quite in the middle shelf, no book
		} else if(clickY < 0.5625) {
			return -1;
		}

		int offX = facing.getFrontOffsetX();
		int offZ = facing.getFrontOffsetZ();
		double x1 = offX == -1 ? 0.625 : 0.0625;
		double z1 = offZ == -1 ? 0.625 : 0.0625;
		double x2 = offX ==  1 ? 0.375 : 0.9375;
		double z2 = offZ ==  1 ? 0.375 : 0.9375;
		// ensure we clicked within a shelf, not outside one
		if(clickX < x1 || clickX > x2 || clickZ < z1 || clickZ > z2) {
			return -1;
		}

		// okay, so now we know we clicked in the book area, so just take the position clicked to determine where
		EnumFacing dir = facing.rotateYCCW();
		// subtract one pixel and multiply by our direction
		double clicked = (dir.getFrontOffsetX() * clickX) + (dir.getFrontOffsetZ() * clickZ) - 0.0625;
		// if negative, just add one to wrap back around
		if(clicked < 0) {
			clicked = 1 + clicked;
		}

		// multiply by 8 to account for extra 2 pixels
		return shelf + Math.min((int)(clicked * 8), 7);
	}


	/*
	 * GUI
	 */

	@Override
	public ContainerBookshelf createContainer(InventoryPlayer inventoryplayer, World world, BlockPos pos) {
		return new ContainerBookshelf(inventoryplayer, this);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public GuiContainer createGui(InventoryPlayer inventoryplayer, World world, BlockPos pos) {
		return new GuiBookshelf(createContainer(inventoryplayer, world, pos));
	}


	/*
	 * Redstone logic
	 */

	public int getPower() {
		if(InspirationsBuilding.redstoneBook == null) {
			return 0;
		}
		for(int i = 0; i < 14; i++) {
			if(InspirationsBuilding.redstoneBook.isItemEqual(getStackInSlot(i))) {
				// we do plus two so a book in slot 13 (last one) gives 15
				return i + 2;
			}
		}
		return 0;
	}


	/*
	 * Rendering
	 */

	public IBlockState writeExtendedBlockState(IExtendedBlockState state) {
		for(int i = 0; i < 14; i++) {
			state = state.withProperty(BlockBookshelf.BOOKS[i], isStackInSlot(i));
		}

		// texture not loaded
		String texture = getTileData().getString(TAG_TEXTURE_PATH);
		if(texture.isEmpty()) {
			// load it from saved block
			ItemStack stack = new ItemStack(getTileData().getCompoundTag(RecipeUtil.TAG_TEXTURE));
			if(!stack.isEmpty()) {
				Block block = Block.getBlockFromItem(stack.getItem());
				texture = ModelHelper.getTextureFromBlock(block, stack.getItemDamage()).getIconName();
				getTileData().setString(TAG_TEXTURE_PATH, texture);
			}
		}
		if(!texture.isEmpty()) {
			state = state.withProperty(BlockBookshelf.TEXTURE, texture);
		}

		return state;
	}

	public void updateTextureBlock(NBTTagCompound tag) {
		getTileData().setTag(RecipeUtil.TAG_TEXTURE, tag);
	}

	public NBTTagCompound getTextureBlock() {
		return getTileData().getCompoundTag(RecipeUtil.TAG_TEXTURE);
	}


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
		NBTBase texture = tag.getTag(RecipeUtil.TAG_TEXTURE);
		if(texture != null) {
			getTileData().setTag(RecipeUtil.TAG_TEXTURE, texture);
		}
		readFromNBT(tag);
	}

	/* NBT */
	@Override
	public void readFromNBT(NBTTagCompound tags) {
		super.readFromNBT(tags);

		// pull the old texture string into the proper location if found
		NBTTagCompound forgeData = tags.getCompoundTag("ForgeData");
		if(forgeData.hasKey(RecipeUtil.TAG_TEXTURE, 8)) {
			forgeData.setString(TAG_TEXTURE_PATH, forgeData.getString(RecipeUtil.TAG_TEXTURE));
			forgeData.removeTag(RecipeUtil.TAG_TEXTURE);
		}
	}

}
