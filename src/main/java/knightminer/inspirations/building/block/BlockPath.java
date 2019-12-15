package knightminer.inspirations.building.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import slimeknights.mantle.block.EnumBlock;
import slimeknights.mantle.client.CreativeTab;

import java.util.Locale;

public class BlockPath extends EnumBlock<BlockPath.PathType> {

	public static final PropertyEnum<PathType> TYPE = PropertyEnum.create("type", PathType.class);
	public BlockPath() {
		super(Material.ROCK, TYPE, PathType.class);

		this.setCreativeTab(CreativeTab.DECORATIONS);
		this.setHardness(1.5f);
		this.setResistance(10f);
		this.setHarvestLevel("pickaxe", 0);
	}

	/* Block Shape */

	protected static final AxisAlignedBB BOUNDS = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.0625D, 1.0D);

	@Deprecated
	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return BOUNDS;
	}

	@Deprecated
	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Deprecated
	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}

	@Deprecated
	@Override
	public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
		return face == EnumFacing.DOWN ? BlockFaceShape.SOLID : BlockFaceShape.UNDEFINED;
	}


	/* Solid surface below */

	@Override
	public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
		return super.canPlaceBlockAt(worldIn, pos) && this.canBlockStay(worldIn, pos);
	}

	private boolean canBlockStay(World world, BlockPos pos) {
		BlockPos down = pos.down();
		return world.getBlockState(down).getBlockFaceShape(world, down, EnumFacing.UP) == BlockFaceShape.SOLID;
	}

	@Deprecated
	@Override
	public void neighborChanged(IBlockState state, World world, BlockPos pos, Block blockIn, BlockPos fromPos) {
		if (!this.canBlockStay(world, pos)) {
			this.dropBlockAsItem(world, pos, state, 0);
			world.setBlockToAir(pos);
		}
	}

	public static enum PathType implements IStringSerializable, EnumBlock.IEnumMeta {
		ROCKS,
		ROUND,
		TILES,
		BRICKS;

		private int meta;
		PathType() {
			this.meta = ordinal();
		}

		@Override
		public int getMeta() {
			return meta;
		}

		@Override
		public String getName() {
			return this.name().toLowerCase(Locale.US);
		}
	}
}
