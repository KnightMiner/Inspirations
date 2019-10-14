package knightminer.inspirations.utility.block;

import java.util.Random;

import knightminer.inspirations.common.Config;
import knightminer.inspirations.common.IHidable;
import knightminer.inspirations.utility.tileentity.TileCollector;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Mirror;
import net.minecraft.util.NonNullList;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import slimeknights.mantle.block.InventoryBlock;

import javax.annotation.Nonnull;

public class BlockCollector extends InventoryBlock implements IHidable {

	public static final DirectionProperty FACING = BlockStateProperties.FACING;
	public static final BooleanProperty TRIGGERED = BlockStateProperties.TRIGGERED;
	public BlockCollector() {
		super(Block.Properties.create(Material.ROCK)
				.hardnessAndResistance(3.5F)
				.sound(SoundType.STONE)
		);
		this.setDefaultState(this.getStateContainer().getBaseState()
				.with(FACING, Direction.NORTH)
				.with(TRIGGERED, false));
	}

	/* IHidable */

	@Override
	public boolean isEnabled() {
		return Config.enableCollector.get();
	}

	@Override
	public void fillItemGroup(@Nonnull ItemGroup group, NonNullList<ItemStack> stacks) {
		if (shouldAddtoItemGroup(group)) {
			super.fillItemGroup(group, stacks);
		}
	}

	/* Block state settings */

	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(FACING, TRIGGERED);
	}

	@Override
	public BlockState rotate(BlockState state, IWorld world, BlockPos pos, Rotation direction) {
		return state.with(FACING, direction.rotate(state.get(FACING)));
	}

	@Nonnull
	@Override
	public BlockState mirror(BlockState state, Mirror mirror) {
		return state.with(FACING, mirror.mirror(state.get(FACING)));
	}

	@Nonnull
	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		// place opposite since its more useful to face into what you clicked
		Direction facing = context.getNearestLookingDirection();
		if(context.isPlacerSneaking()) {
			facing = facing.getOpposite();
		}
		return this.getDefaultState().with(FACING, facing);
	}

	@Override
	public void onReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving) {
		// If destroyed, drop contents.
		if (state.getBlock() != newState.getBlock()) {
			TileEntity te = world.getTileEntity(pos);
			if (te instanceof IInventory) {
				InventoryHelper.dropInventoryItems(world, pos, (IInventory)te);
			}
		}
		super.onReplaced(state, world, pos, newState, isMoving);
	}

	/* Tile Entity */

	@Nonnull
	@Override
	public TileEntity createNewTileEntity(@Nonnull IBlockReader world) {
		return new TileCollector();
	}

	@Nonnull
	@Override
	public TileEntity createTileEntity(BlockState blockState, IBlockReader iBlockReader) {
		return new TileCollector();
	}

	@Override
	protected boolean openGui(PlayerEntity player, World world, BlockPos pos) {
		if (!(player instanceof ServerPlayerEntity)) {
			throw new AssertionError("Needs to be server!");
		}
		TileEntity te = world.getTileEntity(pos);
		if(te instanceof TileCollector) {
			NetworkHooks.openGui((ServerPlayerEntity) player, (INamedContainerProvider) te, pos);
			return true;
		}
		return false;
	}


	/* Comparator logic */

	@Override
	public int getComparatorInputOverride(BlockState blockState, World world, BlockPos pos) {
		TileEntity te = world.getTileEntity(pos);
		if(te != null) {
			return te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null).map(
					ItemHandlerHelper::calcRedstoneFromInventory
			).orElse(0);
		}
		return 0;
	}

	@Override
	public boolean hasComparatorInputOverride(BlockState state) {
		return true;
	}


	/* Collecting logic */

	@Override
	public void neighborChanged(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
		boolean powered = world.isBlockPowered(pos) || world.isBlockPowered(pos.up());
		boolean triggered = state.get(TRIGGERED);
		if (powered && !triggered) {
			world.getPendingBlockTicks().scheduleTick(pos, this, this.tickRate(world));
			world.setBlockState(pos, state.with(TRIGGERED, true), 4);
		}
		else if (!powered && triggered) {
			world.setBlockState(pos, state.with(TRIGGERED, false), 4);
		}
	}

	@Override
	public int tickRate(IWorldReader worldIn) {
		return 4;
	}

	@Override
	public void tick(BlockState state, World world, BlockPos pos, Random random) {
		if (world.isRemote) {
			return;
		}
		TileEntity te = world.getTileEntity(pos);
		if(te instanceof TileCollector) {
			((TileCollector)te).collect(state.get(FACING));
		}
	}
}
