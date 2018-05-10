package knightminer.inspirations.recipes.tileentity;

import javax.annotation.Nonnull;

import knightminer.inspirations.common.Config;
import knightminer.inspirations.library.InspirationsRegistry;
import knightminer.inspirations.library.recipe.cauldron.ICauldronRecipe;
import knightminer.inspirations.library.recipe.cauldron.ICauldronRecipe.CauldronState;
import knightminer.inspirations.recipes.block.BlockEnhancedCauldron;
import knightminer.inspirations.recipes.block.BlockEnhancedCauldron.CauldronContents;
import net.minecraft.block.BlockCauldron;
import net.minecraft.block.BlockFire;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.potion.PotionUtils;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.fluids.Fluid;
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
		if(state.getFluid() != null) {
			return CauldronContents.FLUID;
		}
		if(state.getColor() > -1) {
			return CauldronContents.DYE;
		}
		if(state.getPotion() != null) {
			return CauldronContents.POTION;
		}

		return CauldronContents.FLUID;
	}

	/**
	 * Checks if this TE currently has water in it
	 * @return
	 */
	public boolean isWater() {
		return state.isWater();
	}

	/**
	 * Returns the current color for tinting
	 * @return  block colors color
	 */
	public int getColor() {
		switch(getContentType()) {
			case DYE:
				return state.getColor();
			case POTION:
				return PotionUtils.getPotionColor(state.getPotion());
		}

		Fluid fluid = state.getFluid();
		if(fluid != null) {
			return state.getFluid().getColor();
		}

		return -1;
	}

	/**
	 * Method to run cauldron interaction code. Used for both TileCauldron and simple cauldron
	 * @return  True if successful, false for pass
	 */
	public static boolean interact(World world, BlockPos pos, IBlockState blockState, EntityPlayer player, EnumHand hand) {
		// ensure we have a stack
		ItemStack stack = player.getHeldItem(hand);
		if(stack.isEmpty()) {
			return false;
		}

		// grab the TE if extended
		TileCauldron cauldron = null;
		CauldronState state = CauldronState.WATER;
		if(Config.enableExtendedCauldron) {
			TileEntity te = world.getTileEntity(pos);
			if(te instanceof TileCauldron) {
				cauldron = (TileCauldron) te;
				state = cauldron.state;
			}
		}

		// other properties
		boolean boiling = world.getBlockState(pos.down()).getBlock() instanceof BlockFire;
		int level = blockState.getValue(BlockCauldron.LEVEL);

		// grab recipe
		ICauldronRecipe recipe = InspirationsRegistry.getCauldronResult(stack, boiling, level, state);
		if(recipe != null) {
			// update properties based on the recipe
			if(!world.isRemote) {
				// grab state first since we may need to back out
				CauldronState newState = recipe.getState(stack, boiling, level, state);

				// if its not a TE, stop right here and disallow any recipes which do not return water
				if(cauldron == null && !CauldronState.WATER.matches(newState)) {
					return true;
				}

				// play sound
				SoundEvent sound = recipe.getSound(stack, boiling, level, state);
				if(sound != null) {
					world.playSound((EntityPlayer)null, pos, sound, SoundCategory.BLOCKS, recipe.getVolume(sound), 1.0F);
				}

				// update level
				int newLevel = MathHelper.clamp(recipe.getLevel(level), 0, 3);
				if(newLevel != level) {
					Blocks.CAULDRON.setWaterLevel(world, pos, blockState, newLevel);
					if(newLevel == 0) {
						newState = CauldronState.WATER;
					}
				}

				// update the state
				if(cauldron != null && !state.matches(newState)) {
					cauldron.state = newState;
					world.notifyBlockUpdate(pos, blockState, blockState, 2);
				}

				// result
				ItemStack result = recipe.getResult(stack, boiling, level, state);
				// update held item
				if(!player.capabilities.isCreativeMode) {
					player.setHeldItem(hand, recipe.transformInput(stack, boiling, level, state));
				}
				// and give the new item to the player
				if(!result.isEmpty()) {
					ItemHandlerHelper.giveItemToPlayer(player, result, player.inventory.currentItem);
				}
			}

			return true;
		}

		// if we have water, allow default actions to run, otherwise block
		if(state.isWater()) {
			// though skip default interactions for water bottles and water buckets if not pure water
			Item item = stack.getItem();
			return state != CauldronState.WATER && (item == Items.POTIONITEM || item == Items.WATER_BUCKET);
		}

		return true;
	}

	public IBlockState writeExtendedBlockState(IExtendedBlockState state) {
		// just pull the texture right from the fluid
		if(this.state != CauldronState.WATER && getContentType() == CauldronContents.FLUID) {
			Fluid fluid = this.state.getFluid();
			if(fluid != null) {
				state = state.withProperty(BlockEnhancedCauldron.TEXTURE, fluid.getStill().toString());
			}
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
