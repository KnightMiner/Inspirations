package knightminer.inspirations.tools.entity;

import static knightminer.inspirations.tools.InspirationsTools.redstoneCharge;

import knightminer.inspirations.tools.InspirationsTools;
import knightminer.inspirations.tools.block.BlockRedstoneCharge;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.item.DirectionalPlaceContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nonnull;

public class RedstoneArrow extends AbstractArrowEntity {
	public RedstoneArrow(EntityType<RedstoneArrow> entType, World world) {
		super(entType, world);
	}

	public RedstoneArrow(World world, double x, double y, double z) {
		super(InspirationsTools.entRSArrow, x, y, z, world);
		init();
	}

	public RedstoneArrow(World world, LivingEntity shooter) {
		super(InspirationsTools.entRSArrow, shooter, world);
		init();
	}

	private void init() {
		this.setDamage(0.25);
	}

	private static TranslationTextComponent NAME = new TranslationTextComponent("item.inspirations.charged_arrow");

	@Nonnull
	@Override
	public ITextComponent getName() {
		if (this.hasCustomName()) {
			return super.getName();
		} else {
			return NAME;
		}
	}

	@Nonnull
	@Override
	protected ItemStack getArrowStack() {
		return new ItemStack(InspirationsTools.redstoneArrow, 1);
	}

	/**
	 * Called when the arrow hits a block or an entity
	 */
	@Override
	protected void onHit(RayTraceResult raytrace) {
		if(raytrace.getType() == RayTraceResult.Type.BLOCK && raytrace instanceof BlockRayTraceResult) {
			// get to the block the arrow is on
			Direction sideHit = ((BlockRayTraceResult)raytrace).getFace();
			BlockPos pos = ((BlockRayTraceResult)raytrace).getPos().offset(sideHit);

			// if there is a block there, try the block next to that
			if(!world.getBlockState(pos).isReplaceable(new DirectionalPlaceContext(world, pos, sideHit, ItemStack.EMPTY, sideHit))) {
				pos = pos.offset(sideHit);
				if(!world.getBlockState(pos).isReplaceable(new DirectionalPlaceContext(world, pos, sideHit, ItemStack.EMPTY, sideHit))) {
					super.onHit(raytrace);
					return;
				}
			}

			world.playSound(null, pos, SoundEvents.ITEM_FLINTANDSTEEL_USE, SoundCategory.BLOCKS, 1.0F, world.rand.nextFloat() * 0.4F + 0.8F);
			BlockState state = redstoneCharge.getDefaultState().with(BlockRedstoneCharge.FACING, sideHit.getOpposite());
			world.setBlockState(pos, state, Constants.BlockFlags.DEFAULT_AND_RERENDER);
			redstoneCharge.onBlockPlacedBy(world, pos, state, null, ItemStack.EMPTY);


			this.remove();
			return;
		}

		super.onHit(raytrace);
	}
}
