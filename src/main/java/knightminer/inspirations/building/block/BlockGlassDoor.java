package knightminer.inspirations.building.block;

import java.util.Random;

import knightminer.inspirations.building.InspirationsBuilding;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockGlassDoor extends BlockDoor {

	public BlockGlassDoor() {
		super(Material.GLASS);
		this.setHardness(0.3F);
		this.setSoundType(SoundType.GLASS);
		this.setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
		this.disableStats();
	}

	@Override
	public void onBlockHarvested(World worldIn, BlockPos pos, IBlockState state, EntityPlayer player) {
		// honestly have no idea what this code was doing, but it broke silk touching the door
	}

	/**
	 * Get the Item that this Block should drop when harvested.
	 */
	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune) {
		return InspirationsBuilding.glassDoorItem;
	}

	/**
	 * Determines if the player can harvest this block, obtaining it's drops when the block is destroyed.
	 *
	 * @param player The player damaging the block
	 * @param pos The block's current position
	 * @return True to spawn the drops
	 */
	@Override
	public boolean canHarvestBlock(IBlockAccess world, BlockPos pos, EntityPlayer player) {
		return true;
	}

	@Override
	protected ItemStack getSilkTouchDrop(IBlockState state) {
		return new ItemStack(InspirationsBuilding.glassDoorItem);
	}

	/**
	 * Returns the quantity of items to drop on block destruction.
	 */
	@Override
	public int quantityDropped(Random random) {
		return 0;
	}

	@Override
	protected boolean canSilkHarvest() {
		return true;
	}

}
