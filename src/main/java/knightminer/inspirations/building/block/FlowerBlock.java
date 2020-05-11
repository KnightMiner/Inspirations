package knightminer.inspirations.building.block;

import knightminer.inspirations.common.Config;
import knightminer.inspirations.common.IHidable;
import knightminer.inspirations.library.Util;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.BushBlock;
import net.minecraft.block.DoublePlantBlock;
import net.minecraft.block.IGrowable;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.loot.ConstantRange;
import net.minecraft.world.storage.loot.LootPool;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraft.world.storage.loot.TableLootEntry;
import net.minecraftforge.event.LootTableLoadEvent;

import java.util.Random;

public class FlowerBlock extends BushBlock implements IGrowable, IHidable {
	private static final VoxelShape SHAPE = Block.makeCuboidShape(5.0D, 0.0D, 5.0D, 11.0D, 10.0D, 11.0D);
	private final DoublePlantBlock largePlant;


	public FlowerBlock(DoublePlantBlock largePlant) {
		super(Block.Properties.create(Material.PLANTS).hardnessAndResistance(0F).sound(SoundType.PLANT));
		this.largePlant = largePlant;
	}

	@Override
	public boolean isEnabled() {
		return Config.enableFlowers.get();
	}

	@Override
	public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
		if(shouldAddtoItemGroup(group)) {
			super.fillItemGroup(group, items);
		}
	}

	/* Planty stuff */

	@Deprecated
	@Override
	public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
		Vec3d off = state.getOffset(world, pos);
		return SHAPE.withOffset(off.x, off.y, off.z);
	}

	@Deprecated
	@Override
	public VoxelShape getCollisionShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		return VoxelShapes.empty();
	}

	@Override
	public OffsetType getOffsetType() {
		return OffsetType.XZ;
	}

	/* Doubling up */

	@Override
	public boolean canGrow(IBlockReader world, BlockPos pos, BlockState state, boolean isClient) {
		return largePlant != null;
	}


	@Override
	public boolean canUseBonemeal(World worldIn, Random rand, BlockPos pos, BlockState state) {
		return true;
	}
	
	@Override
	public void grow(ServerWorld world, Random rand, BlockPos pos, BlockState state) {
		// should not happen, but catch anyways
		if(largePlant == null) {
			return;
		}

		if (world.isAirBlock(pos.up())) {
			largePlant.placeAt(world, pos, 2);
		}
	}

	// Inject the ability to drop this flower into the loot table for the large version.
	public void injectLoot(LootTableLoadEvent event) {
		if (largePlant == null ||
				!event.getName().getNamespace().equals("minecraft") ||
				!event.getName().getPath().equals("blocks/" + largePlant.getRegistryName().getPath())
		) {
			return;
		}
		// We have the right table. Now we want to find the pool which drops the item, and
		// replace it with an alternatives check to drop us if hit by shears.
		// If anything doesn't match what we expect, don't change anything.
		LootTable table = event.getTable();
		if (table.removePool("main") == null) {
			return; // Wasn't removed.
		}
		ResourceLocation location = Util.getResource("blocks/inject/" + getRegistryName().getPath());
		table.addPool(new LootPool.Builder()
				.name(location.toString())
				.rolls(ConstantRange.of(1))
				.addEntry(TableLootEntry.builder(location))
				.build()
		);
	}
}
