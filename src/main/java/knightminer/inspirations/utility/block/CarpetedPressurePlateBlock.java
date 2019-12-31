package knightminer.inspirations.utility.block;

import knightminer.inspirations.Inspirations;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.PressurePlateBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;

public class CarpetedPressurePlateBlock extends PressurePlateBlock {
	protected static final VoxelShape PRESSED_AABB = VoxelShapes.or(
			Block.makeCuboidShape(0, 0, 0, 16, 1, 16),
			Block.makeCuboidShape(1, 1, 1, 15, 1.25, 15)
	);
	protected static final VoxelShape UNPRESSED_AABB = VoxelShapes.or(
			Block.makeCuboidShape(0, 0, 0, 16, 1, 16),
			Block.makeCuboidShape(1, 1, 1, 15, 1.5, 15)
	);

	private final DyeColor color;
	private final String transKey;

	public CarpetedPressurePlateBlock(DyeColor color) {
		super(Sensitivity.MOBS, Block.Properties.create(Material.CARPET, color)
				.hardnessAndResistance(0.5F)
				.sound(SoundType.CLOTH)
		);
		this.color = color;
		this.transKey = String.format("block.minecraft.%s_carpet", color.getTranslationKey());
	}

	@Nonnull
	@Override
	public VoxelShape getShape(@Nonnull BlockState state, @Nonnull IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		boolean flag = this.getRedstoneStrength(state) > 0;
		return flag ? PRESSED_AABB : UNPRESSED_AABB;
	}

	// Use the name of the carpet on top for the translation key.
	// This should never be seen normally, but other mods might display it
	// so ensure it's a valid value.
	@Nonnull
	@Override
	public String getTranslationKey() {
		return transKey;
	}

	// Since fitted carpets may replace the original carpet, we need to lookup the item to make sure we
	// get the right object. So defer until the first time we actually need it.
	private Item pickItem = Items.AIR;

	public Item getCarpet() {
		if(pickItem == Items.AIR) {
			pickItem = ForgeRegistries.ITEMS.getValue(new ResourceLocation(color.getTranslationKey() + "_carpet"));

			if(pickItem == Items.AIR) {
				Inspirations.log.warn("No carpet item registered under minecraft:{}_carpet!", color.getTranslationKey());
				return Items.AIR;
			}
		}
		return pickItem;
	}

	@Override
	public ItemStack getPickBlock(BlockState state, RayTraceResult target, IBlockReader world, BlockPos pos, PlayerEntity player) {
		return new ItemStack(getCarpet());
	}
}
