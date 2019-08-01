package knightminer.inspirations.utility.dispenser;

import knightminer.inspirations.library.InspirationsRegistry;
import net.minecraft.block.DispenserBlock;
import net.minecraft.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.dispenser.IDispenseItemBehavior;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.DispenserTileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidActionResult;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;

import javax.annotation.Nonnull;

public class DispenseFluidTank extends DefaultDispenseItemBehavior {
	private static final DefaultDispenseItemBehavior DEFAULT = new DefaultDispenseItemBehavior();
	private IDispenseItemBehavior fallback;
	public DispenseFluidTank(IDispenseItemBehavior fallback) {
		this.fallback = fallback;
	}

	@Nonnull
	@Override
	protected ItemStack dispenseStack(IBlockSource source, ItemStack stack){
		if(!stack.getItem().isIn(InspirationsRegistry.TAG_DISP_FLUID_TANKS)) {
			return fallback.dispense(source, stack);
		}

		Direction side = source.getBlockState().get(DispenserBlock.FACING);
		BlockPos pos = source.getBlockPos().offset(side);
		World world = source.getWorld();
		return FluidUtil.getFluidHandler(world, pos, side.getOpposite()).map((handler) -> {
			FluidActionResult result;
			LazyOptional<FluidStack> optFluid = FluidUtil.getFluidContained(stack);
			if(optFluid.isPresent()) {
				result = FluidUtil.tryEmptyContainer(stack, handler, Integer.MAX_VALUE, null, true);
			} else {
				result = FluidUtil.tryFillContainer(stack, handler, Integer.MAX_VALUE, null, true);
			}

			if(result.isSuccess()) {
				ItemStack resultStack = result.getResult();
				// play sound
				SoundEvent sound = optFluid.map(
					(fluid) -> fluid.getFluid().getEmptySound(fluid)
				).orElseGet(() -> {
					FluidStack resultFluid = FluidUtil.getFluidContained(resultStack).orElseThrow(AssertionError::new);
					return resultFluid.getFluid().getFillSound(resultFluid);
				});

				world.playSound(null, pos, sound, SoundCategory.BLOCKS, 1.0F, 1.0F);

				if(stack.getCount() == 1) {
					return resultStack;
				}

				if(!resultStack.isEmpty() && ((DispenserTileEntity)source.getBlockTileEntity()).addItemStack(resultStack) < 0) {
					DEFAULT.dispense(source, resultStack);
				}

				ItemStack shrink = stack.copy();
				shrink.shrink(1);
				return shrink;
			}
			// TODO: fallback?
			return DEFAULT.dispense(source, stack);
		}).orElseGet(() -> fallback.dispense(source, stack));
	}
}
