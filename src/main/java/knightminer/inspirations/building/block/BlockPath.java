package knightminer.inspirations.building.block;

import knightminer.inspirations.common.Config;
import knightminer.inspirations.common.block.HidableBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;
import slimeknights.mantle.client.CreativeTab;

public class BlockPath extends HidableBlock {

	public BlockPath(MaterialColor mapColor) {
		super(Block.Properties.create(Material.ROCK, mapColor)
			.hardnessAndResistance(1.5F, 10F)
			.harvestTool(ToolType.PICKAXE).harvestLevel(0),
			Config.enablePath::get
		);

	}

	/* Block Shape */

	protected static final VoxelShape BOUNDS = Block.makeCuboidShape(0, 0, 0, 16, 1, 16);

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		return BOUNDS;
	}

	/* Solid surface below */

	@Override
	public boolean isValidPosition(BlockState state, IWorldReader world, BlockPos pos) {
		return super.isValidPosition(state, world, pos) && this.canBlockStay(world, pos);
	}

	private boolean canBlockStay(IWorldReader world, BlockPos pos) {
		BlockPos down = pos.down();
		return Block.hasSolidSide(world.getBlockState(down), world, pos, Direction.UP);
	}

	@Override
	public void neighborChanged(BlockState state, World world, BlockPos pos, Block blockIn, BlockPos fromPos, boolean p_220069_6_) {
		if (!this.canBlockStay(world, pos)) {
			world.destroyBlock(pos, true);
		}
	}

}
