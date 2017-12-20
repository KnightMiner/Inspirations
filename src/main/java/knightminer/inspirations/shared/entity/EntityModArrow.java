package knightminer.inspirations.shared.entity;

import static knightminer.inspirations.utility.InspirationsUtility.redstoneCharge;

import knightminer.inspirations.shared.InspirationsShared;
import knightminer.inspirations.utility.block.BlockRedstoneCharge;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class EntityModArrow extends EntityArrow {

	private int meta;
	public EntityModArrow(World world) {
		super(world);
	}

	public EntityModArrow(World world, double x, double y, double z, int meta) {
		super(world, x, y, z);
		init(meta);
	}

	public EntityModArrow(World world, EntityLivingBase shooter, int meta) {
		super(world, shooter);
		init(meta);
	}

	private void init(int meta) {
		this.meta = meta;
		this.setDamage(0.25);
	}

	@Override
	protected ItemStack getArrowStack() {
		return new ItemStack(InspirationsShared.arrow, 1, meta);
	}

	/**
	 * Called when the arrow hits a block or an entity
	 */
	@Override
	protected void onHit(RayTraceResult raytrace) {
		if(raytrace.typeOfHit == RayTraceResult.Type.BLOCK) {
			// get to the block the arrow is on
			BlockPos pos = raytrace.getBlockPos().offset(raytrace.sideHit);

			// if there is a block there, try the block next to that
			if(!redstoneCharge.canPlaceBlockAt(world, pos)) {
				pos = pos.offset(raytrace.sideHit);
				if(!redstoneCharge.canPlaceBlockAt(world, pos)) {
					super.onHit(raytrace);
					return;
				}
			}

			world.playSound(null, pos, SoundEvents.ITEM_FLINTANDSTEEL_USE, SoundCategory.BLOCKS, 1.0F, world.rand.nextFloat() * 0.4F + 0.8F);
			IBlockState state = redstoneCharge.getDefaultState().withProperty(BlockRedstoneCharge.FACING, raytrace.sideHit.getOpposite());
			world.setBlockState(pos, state, 11);

			this.setDead();
			return;
		}

		super.onHit(raytrace);
	}


	/* NBT */
	public static final String TAG_META = "meta";
	@Override
	public void writeEntityToNBT(NBTTagCompound compound) {
		super.writeEntityToNBT(compound);
		compound.setInteger(TAG_META, meta);
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound compound) {
		super.readEntityFromNBT(compound);
		this.meta = compound.getInteger(TAG_META);
	}
}
