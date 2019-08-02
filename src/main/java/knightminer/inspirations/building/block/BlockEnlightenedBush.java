package knightminer.inspirations.building.block;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import javafx.beans.property.StringProperty;
import knightminer.inspirations.building.tileentity.TileEnlightenedBush;
import knightminer.inspirations.library.util.TextureBlockUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.state.EnumProperty;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootParameters;
import net.minecraftforge.client.model.data.ModelProperty;

public class BlockEnlightenedBush extends Block {

	public static final EnumProperty<LightsType> LIGHTS = EnumProperty.create("lights", LightsType.class);
	public static final ModelProperty TEXTURE = TextureBlockUtil.TEXTURE_PROP;
	private final int color;

	public BlockEnlightenedBush(int color) {
		super(Block.Properties.create(Material.LEAVES)
			.lightValue(15)
			.hardnessAndResistance(0.2F)
			.sound(SoundType.PLANT)
		);
		this.color = color;
	}

//	@Nullable
//	@Override
//	public TileEntity createNewTileEntity(IBlockReader worldIn) {
//		return new TileEnlightenedBush();
//	}

	@Override
	public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
		TextureBlockUtil.addBlocksFromTag(BlockTags.LEAVES, this, items);
	}

	public int getColor() {
		return color;
	}

	/*
	 * Properties
	 */

	@Override
	public boolean causesSuffocation(BlockState state, IBlockReader worldIn, BlockPos pos) {
		return false;
	}

	@Nonnull
	@Override
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.CUTOUT;
	}


	/*
	 * Texturing
	 */

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
		super.onBlockPlacedBy(world, pos, state, placer, stack);
		TextureBlockUtil.placeTextureBlock(world, pos, stack);
	}

	@Override
	public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder) {
		List<ItemStack> drops = new ArrayList<>();
		drops.add(TextureBlockUtil.getBlockItemStack(builder.getWorld(), builder.get(LootParameters.POSITION), state));
		return drops;
	}

	@Nonnull
	@Override
	public ItemStack getPickBlock(BlockState state, RayTraceResult target, IBlockReader world, BlockPos pos, PlayerEntity player) {
		return TextureBlockUtil.getBlockItemStack(world, pos, state);
	}

//	@Override
//	public boolean removedByPlayer(BlockState state, World world, BlockPos pos, PlayerEntity player, boolean willHarvest, IFluidState fluid) {
//		// we pull up a few calls to this point in time because we still have the TE here
//		// the execution otherwise is equivalent to vanilla order
//		this.onBlockHarvested(world, pos, state, player);
//		if(willHarvest) {
//			this.harvestBlock(world, player, pos, state, world.getTileEntity(pos), player.getHeldItemMainhand());
//		}
//
//		world.removeBlock(pos, false);
//		// return false to prevent the above called functions to be called again
//		// side effect of this is that no xp will be dropped. but it shoudln't anyway from a bookshelf :P
//		return false;
//	}

	public static enum LightsType implements IStringSerializable {
		WHITE,
		RED(0xBF0000),
		GREEN(0x267F00),
		BLUE(0x001CBF),
		RAINBOW,
		CHRISTMAS;

		private int meta;
		private int color;
		LightsType() {
			this(-1);
		}
		LightsType(int color) {
			this.meta = ordinal();
			this.color = color;
		}

		public int getColor() {
			return color;
		}

		@Override
		public String getName() {
			return this.name().toLowerCase(Locale.US);
		}
	}
}
