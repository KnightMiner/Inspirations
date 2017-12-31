package knightminer.inspirations.recipes.tileentity;

import javax.annotation.Nonnull;

import knightminer.inspirations.common.Config;
import knightminer.inspirations.library.InspirationsRegistry;
import knightminer.inspirations.library.recipe.cauldron.ICauldronRecipe;
import knightminer.inspirations.library.recipe.cauldron.ICauldronRecipe.CauldronContents;
import knightminer.inspirations.library.recipe.cauldron.ICauldronRecipe.CauldronState;
import knightminer.inspirations.recipes.block.BlockEnhancedCauldron;
import net.minecraft.block.BlockCauldron;
import net.minecraft.block.BlockFire;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.potion.PotionUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.items.ItemHandlerHelper;

public class TileCauldron extends TileEntity {
	private CauldronState state;

	public TileCauldron() {
		this.state = CauldronState.WATER;
	}

	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate) {
		return oldState.getBlock() != newSate.getBlock();
	}

	@Nonnull
	public CauldronContents getContentType() {
		return state.getType();
	}

	public int getColor() {
		switch(state.getType()) {
			case DYE:
				return state.getColor();
			case POTION:
				return PotionUtils.getPotionColor(state.getPotion());
			case FLUID:
				return state.getFluid().getColor(world, pos);
		}

		return -1;
	}

	public boolean interact(IBlockState blockState, EntityPlayer player, EnumHand hand) {
		ItemStack stack = player.getHeldItem(hand);
		if(stack.isEmpty()) {
			return false;
		}

		boolean boiling = world.getBlockState(pos.down()).getBlock() instanceof BlockFire;
		int level = blockState.getValue(BlockCauldron.LEVEL);
		ICauldronRecipe recipe = InspirationsRegistry.getCauldronResult(stack, boiling, level, state);
		CauldronState state = this.state;
		if(recipe != null) {
			if(!world.isRemote) {
				// sound
				SoundEvent sound = recipe.getSound(stack, boiling, level, state);
				if(sound != null) {
					world.playSound((EntityPlayer)null, pos, sound, SoundCategory.BLOCKS, recipe.getVolume(sound), 1.0F);
				}

				// state
				CauldronState newState = recipe.getState(stack, boiling, level, state);
				if(!isValid(newState)) {
					return false;
				}

				if(!state.matches(newState)) {
					this.state = newState;
					world.notifyBlockUpdate(pos, blockState, blockState, 2);
				}
				// level
				setLevel(blockState, recipe.getLevel(level));

				// result
				ItemStack result = recipe.getResult(stack, boiling, level, state);
				// update held item
				player.setHeldItem(hand, recipe.transformInput(stack, boiling, level, state));
				// and give the new item to the player
				if(!result.isEmpty()) {
					ItemHandlerHelper.giveItemToPlayer(player, result, player.inventory.currentItem);
				}
			}

			return true;
		}

		return false;
	}

	private boolean isValid(CauldronState state) {
		switch(state.getType()) {
			case POTION:
				return Config.enableCauldronBrewing;
			case DYE:
				return Config.enableCauldronDyeing;
		}
		return true;
	}

	private void setLevel(IBlockState state, int level) {
		level = MathHelper.clamp(level, 0, 3);
		if(state.getValue(BlockCauldron.LEVEL) == level) {
			return;
		}
		if(level == 0) {
			this.state = CauldronState.WATER;
		}

		Blocks.CAULDRON.setWaterLevel(world, pos, state, level);
	}

	public IBlockState writeExtendedBlockState(IExtendedBlockState state) {
		// just pull the texture right from the fluid
		if(getContentType() == CauldronContents.FLUID) {
			state = state.withProperty(BlockEnhancedCauldron.TEXTURE, this.state.getFluid().getStill().toString());
		}

		return state;
	}


	/* NBT */
	public static final String TAG_STATE = "state";

	@Nonnull
	@Override
	public NBTTagCompound getUpdateTag() {
		// new tag instead of super since default implementation calls the super of writeToNBT
		return writeToNBT(new NBTTagCompound());
	}

	@Nonnull
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tags) {
		tags = super.writeToNBT(tags);

		tags.setTag(TAG_STATE, state.writeToNBT());

		return tags;
	}

	@Override
	public void readFromNBT(NBTTagCompound tags) {
		super.readFromNBT(tags);

		this.state = CauldronState.fromNBT(tags.getCompoundTag(TAG_STATE));
	}

	@Override
	public SPacketUpdateTileEntity getUpdatePacket() {
		return new SPacketUpdateTileEntity(this.getPos(), this.getBlockMetadata(), state.writeToNBT());
	}

	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
		CauldronState newState = CauldronState.fromNBT(pkt.getNbtCompound());
		if(!this.state.matches(newState)) {
			this.state = newState;
			if(world.isRemote) {
				Minecraft.getMinecraft().renderGlobal.notifyBlockUpdate(null, pos, null, null, 0);
			}
		}
	}
}
