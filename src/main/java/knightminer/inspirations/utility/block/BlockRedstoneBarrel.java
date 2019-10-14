package knightminer.inspirations.utility.block;

import knightminer.inspirations.common.Config;
import knightminer.inspirations.common.block.HidableBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nonnull;

public class BlockRedstoneBarrel extends HidableBlock {
	public static final IntegerProperty LEVEL = IntegerProperty.create("level", 0, 15);
	public BlockRedstoneBarrel() {
		super(Block.Properties.create(Material.ROCK)
				.hardnessAndResistance(1.5F, 10.0F)
				.sound(SoundType.STONE),
				Config.enableRedstoneBarrel::get
		);
		setDefaultState(getStateContainer().getBaseState().with(LEVEL, 0));
	}


	/* Blockstate */

	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(LEVEL);
	}

	// Collision shape.
	private static final VoxelShape SHAPE_FRAME = VoxelShapes.or(
			// The four sides of the barrel.
			Block.makeCuboidShape(1, 0, 1, 15, 16, 2),
			Block.makeCuboidShape(1, 0, 1, 2, 16, 15),
			Block.makeCuboidShape(1, 0, 14, 15, 16, 15),
			Block.makeCuboidShape(14, 0, 1, 15, 16, 15)
	);
	private static VoxelShape[] SHAPES = new VoxelShape[16];
	// For each level, the middle section.
	static {
		for (int i = 0; i <= 15; i++) {
			// The empty barrel is 1 thick.
			SHAPES[i] = VoxelShapes.or(SHAPE_FRAME, Block.makeCuboidShape(2, 0, 2, 14, (i==0) ? 1 : i+0.5, 14));
		}
	}

	@Nonnull
	@Override
	public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
		return SHAPES[state.get(LEVEL)];
	}


	/* Redstone */
	@Override
	public boolean hasComparatorInputOverride(BlockState state) {
		return true;
	}

	@Override
	public int getComparatorInputOverride(BlockState blockState, World worldIn, BlockPos pos) {
		return blockState.get(LEVEL);
	}

	@Override
	public boolean onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
		int level = state.get(LEVEL);
		ItemStack stack = player.getHeldItem(hand);

		// holding redstone: fill
		if(stack.getItem() == Items.REDSTONE) {
			if(level < 15) {
				if(!world.isRemote) {
					if(!player.isCreative()) {
						stack.shrink(1);
					}
					setLevel(world, pos, state, level + 1);
				}
				return true;
			}

			return true;

			// not holding redstone: extract
		} else if(level > 0) {
			if(!world.isRemote) {
				ItemHandlerHelper.giveItemToPlayer(player, new ItemStack(Items.REDSTONE));
				setLevel(world, pos, state, level - 1);
			}
			return true;
		}
		return false;
	}

	public void setLevel(World world, BlockPos pos, BlockState state, int level) {
		world.setBlockState(pos, state.with(LEVEL, MathHelper.clamp(level, 0, 15)), 2);
		world.updateComparatorOutputLevel(pos, this);
	}
}
