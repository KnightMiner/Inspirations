package knightminer.inspirations.tweaks.block;

import knightminer.inspirations.tweaks.InspirationsTweaks;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.EnumPlantType;

public class BlockCactusCrop extends BlockBlockCrop {

	private static final AxisAlignedBB[] BOUNDS = {
			new AxisAlignedBB(0.4375, 0, 0.4375, 0.5625, 0.125, 0.5625),
			new AxisAlignedBB(0.375,  0, 0.375,  0.625,  0.25,  0.625 ),
			new AxisAlignedBB(0.3125, 0, 0.3125, 0.6875, 0.375, 0.6875),
			new AxisAlignedBB(0.25,   0, 0.25,   0.75,   0.5,   0.75  ),
			new AxisAlignedBB(0.1875, 0, 0.1875, 0.8125, 0.625, 0.8125),
			new AxisAlignedBB(0.125,  0, 0.125,  0.875,  0.75,  0.875 ),
			new AxisAlignedBB(0.0625, 0, 0.0625, 0.9375, 0.875, 0.9375)
	};
	public BlockCactusCrop() {
		super(Blocks.CACTUS, EnumPlantType.Desert, BOUNDS);
		this.setHardness(0.4F);
		this.setSoundType(SoundType.CLOTH);
		this.setUnlocalizedName("cactus");
	}

	@Override
	public Item getSeed() {
		return InspirationsTweaks.cactusSeeds;
	}

	@Override
	public boolean canBlockStay(World world, BlockPos pos, IBlockState state) {
		return Blocks.CACTUS.canBlockStay(world, pos);
	}

	@Override
	@Deprecated
	public Material getMaterial(IBlockState state) {
		return Material.CACTUS;
	}


	/* spiky! */

	@Override
	public AxisAlignedBB getCollisionBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos) {
		return BOUNDS[this.getAge(state)];
	}

	@Override
	public void onEntityCollidedWithBlock(World world, BlockPos pos, IBlockState state, Entity entity) {
		entity.attackEntityFrom(DamageSource.CACTUS, 1.0F);
	}
}
