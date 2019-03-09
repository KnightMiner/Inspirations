package knightminer.inspirations.building.tileentity;

import javax.annotation.Nonnull;

import knightminer.inspirations.building.InspirationsBuilding;
import knightminer.inspirations.building.block.BlockBookshelf;
import knightminer.inspirations.building.client.GuiBookshelf;
import knightminer.inspirations.building.inventory.ContainerBookshelf;
import knightminer.inspirations.common.network.InspirationsNetwork;
import knightminer.inspirations.common.network.InventorySlotSyncPacket;
import knightminer.inspirations.library.InspirationsRegistry;
import knightminer.inspirations.library.client.ClientUtil;
import knightminer.inspirations.library.util.TextureBlockUtil;
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
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.ItemHandlerHelper;
import slimeknights.mantle.common.IInventoryGui;
import slimeknights.mantle.tileentity.TileInventory;

public class TileBookshelf extends TileInventory implements IInventoryGui {

	/** Cached enchantment bonus, so we are not constantly digging the inventory */
	private float enchantBonus;

	public TileBookshelf() {
		super("gui.inspirations.bookshelf.name", 14, 1);
		enchantBonus = Float.NaN;
	}

	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate) {
		return oldState.getBlock() != newSate.getBlock();
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

		// clear bonus to recalculate it
		enchantBonus = Float.NaN;
	}

	/*
	 * Book logic
	 */

	public boolean interact(EntityPlayer player, EnumHand hand, int bookClicked) {
		// if it contains a book, take the book out
		if(isStackInSlot(bookClicked)) {
			if (!world.isRemote) {
				ItemHandlerHelper.giveItemToPlayer(player, getStackInSlot(bookClicked), player.inventory.currentItem);
				setInventorySlotContents(bookClicked, ItemStack.EMPTY);
			}
			return true;
		}

		// try adding book
		ItemStack stack = player.getHeldItem(hand);
		if(InspirationsRegistry.isBook(stack)) {
			if (!world.isRemote) {
				setInventorySlotContents(bookClicked, stack.splitStack(1));
			}
			return true;
		}

		return false;
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
	 * Extra logic
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

	public float getEnchantPower() {
		// if we have a cached value, use that
		if(!Float.isNaN(enchantBonus)) {
			return enchantBonus;
		}
		// simple sum of all books with the power of a full shelf
		float books = 0;
		for(int i = 0; i < this.getSizeInventory(); i++) {
			if(isStackInSlot(i)) {
				float power = InspirationsRegistry.getBookEnchantingPower(getStackInSlot(i));
				if (power >= 0) {
					books += power;
				}
			}
		}

		// divide by 14 since that is the number of books in a shelf
		enchantBonus = books / 14;
		return enchantBonus;
	}

	/*
	 * Rendering
	 */

	public IBlockState writeExtendedBlockState(IExtendedBlockState state) {
		for(int i = 0; i < 14; i++) {
			state = state.withProperty(BlockBookshelf.BOOKS[i], isStackInSlot(i));
		}

		// texture not loaded
		String texture = ClientUtil.getTexturePath(this);
		if(!texture.isEmpty()) {
			state = state.withProperty(BlockBookshelf.TEXTURE, texture);
		}

		return state;
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
		NBTBase texture = tag.getTag(TextureBlockUtil.TAG_TEXTURE);
		if(texture != null) {
			getTileData().setTag(TextureBlockUtil.TAG_TEXTURE, texture);
		}
		readFromNBT(tag);
	}

	/* NBT */
	@Override
	public void readFromNBT(NBTTagCompound tags) {
		super.readFromNBT(tags);

		// pull the old texture string into the proper location if found
		NBTTagCompound forgeData = tags.getCompoundTag("ForgeData");
		if(forgeData.hasKey(TextureBlockUtil.TAG_TEXTURE, 8)) {
			forgeData.setString("texture_path", forgeData.getString(TextureBlockUtil.TAG_TEXTURE));
			forgeData.removeTag(TextureBlockUtil.TAG_TEXTURE);
		}
	}
}
