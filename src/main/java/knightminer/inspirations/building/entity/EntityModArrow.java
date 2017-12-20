package knightminer.inspirations.building.entity;

import static knightminer.inspirations.building.InspirationsBuilding.redstoneCharge;

import knightminer.inspirations.building.InspirationsBuilding;
import knightminer.inspirations.building.block.BlockRedstoneCharge;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class EntityModArrow extends EntityArrow {

	public EntityModArrow(World world) {
		super(world);
	}

	public EntityModArrow(World world, double x, double y, double z) {
		super(world, x, y, z);
		this.setDamage(0.25);
	}

	public EntityModArrow(World world, EntityLivingBase shooter) {
		super(world, shooter);
		this.setDamage(0.25);
	}

	@Override
	protected ItemStack getArrowStack() {
		return new ItemStack(InspirationsBuilding.arrow);
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
}
