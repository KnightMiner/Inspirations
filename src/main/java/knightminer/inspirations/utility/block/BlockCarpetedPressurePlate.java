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
import net.minecraft.world.IBlockReader;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;

public class BlockCarpetedPressurePlate extends PressurePlateBlock {
	protected static final VoxelShape PRESSED_AABB = Block.makeCuboidShape(0, 0, 0, 16, 1.25, 16);
	protected static final VoxelShape UNPRESSED_AABB = Block.makeCuboidShape(0, 0, 0, 16, 1.5, 16);
	private final DyeColor color;

	public BlockCarpetedPressurePlate(DyeColor color) {
		super(Sensitivity.MOBS, Block.Properties.create(Material.CARPET, color)
				.hardnessAndResistance(0.5F)
				.sound(SoundType.CLOTH)
		);
		this.color = color;
	}

	@Nonnull
	@Override
	public VoxelShape getShape(@Nonnull BlockState state, @Nonnull IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		boolean flag = this.getRedstoneStrength(state) > 0;
		return flag ? PRESSED_AABB : UNPRESSED_AABB;
	}

	// Since fitted carpets may replace the original carpet, we need to lookup the item to make sure we get the right
	// object. So defer until the first time we actually are pick-blocked.
	private Item pickItem = Items.AIR;

	@Override
	public ItemStack getPickBlock(BlockState state, RayTraceResult target, IBlockReader world, BlockPos pos, PlayerEntity player) {
		if (pickItem == Items.AIR) {
			pickItem = ForgeRegistries.ITEMS.getValue(new ResourceLocation(color.getTranslationKey() + "_carpets"));
		}
		if(pickItem == Items.AIR) {
			Inspirations.log.warn("No carpet item registered under minecraft:{}_carpet!", color.getTranslationKey());
			return ItemStack.EMPTY;
		}
		return new ItemStack(pickItem);
	}
}
