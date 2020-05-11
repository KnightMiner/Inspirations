package knightminer.inspirations.building.block;

import knightminer.inspirations.building.tileentity.EnlightenedBushTileEntity;
import knightminer.inspirations.common.Config;
import knightminer.inspirations.common.IHidable;
import knightminer.inspirations.library.util.TextureBlockUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class EnlightenedBushBlock extends Block implements IHidable {
	private final int color;

	public EnlightenedBushBlock(int color) {
		super(Block.Properties.create(Material.LEAVES)
				.lightValue(15)
				.hardnessAndResistance(0.2F)
				.sound(SoundType.PLANT)
		);
		this.color = color;
	}

	public int getColor() {
		return color;
	}

	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}

	@Nullable
	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return new EnlightenedBushTileEntity();
	}

	@Override
	public boolean isEnabled() {
		return Config.enableEnlightenedBush.get();
	}

	/*
	 * Properties
	 */

	@Deprecated
	@Override
	public boolean causesSuffocation(@Nonnull BlockState state, @Nonnull IBlockReader worldIn, @Nonnull BlockPos pos) {
		return false;
	}

	@Deprecated
  @Override
	public boolean canEntitySpawn(BlockState state, @Nonnull IBlockReader worldIn, @Nonnull BlockPos pos, EntityType<?> type) {
		return type == EntityType.OCELOT || type == EntityType.PARROT;
  }


	/*
	 * Texturing
	 */

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
		super.onBlockPlacedBy(world, pos, state, placer, stack);
		TextureBlockUtil.updateTextureBlock(world, pos, stack);
	}

	@Nonnull
	@Override
	public ItemStack getPickBlock(BlockState state, RayTraceResult target, IBlockReader world, BlockPos pos, PlayerEntity player) {
		return TextureBlockUtil.getPickBlock(world, pos, state);
	}
}
