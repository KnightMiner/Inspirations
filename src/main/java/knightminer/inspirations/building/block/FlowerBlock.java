package knightminer.inspirations.building.block;

import knightminer.inspirations.Inspirations;
import knightminer.inspirations.common.Config;
import knightminer.inspirations.common.IHidable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.BushBlock;
import net.minecraft.block.DoublePlantBlock;
import net.minecraft.block.IGrowable;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.ConstantRange;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.TableLootEntry;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.LootTableLoadEvent;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.Random;

import net.minecraft.block.AbstractBlock.OffsetType;

public class FlowerBlock extends BushBlock implements IGrowable, IHidable {
  private static final VoxelShape SHAPE = Block.box(5.0D, 0.0D, 5.0D, 11.0D, 10.0D, 11.0D);
  private final DoublePlantBlock largePlant;


  public FlowerBlock(@Nullable DoublePlantBlock largePlant) {
    super(Block.Properties.of(Material.PLANT).strength(0F).sound(SoundType.GRASS));
    this.largePlant = largePlant;
  }

  @Override
  public boolean isEnabled() {
    return Config.enableFlowers.get();
  }

  @Override
  public void fillItemCategory(ItemGroup group, NonNullList<ItemStack> items) {
    if (shouldAddtoItemGroup(group)) {
      super.fillItemCategory(group, items);
    }
  }

  /* Planty stuff */

  @SuppressWarnings("deprecation")
  @Deprecated
  @Override
  public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
    Vector3d off = state.getOffset(world, pos);
    return SHAPE.move(off.x, off.y, off.z);
  }

  @SuppressWarnings("deprecation")
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
  public boolean isValidBonemealTarget(IBlockReader world, BlockPos pos, BlockState state, boolean isClient) {
    return largePlant != null;
  }

  @Override
  public boolean isBonemealSuccess(World worldIn, Random rand, BlockPos pos, BlockState state) {
    return true;
  }

  @Override
  public void performBonemeal(ServerWorld world, Random rand, BlockPos pos, BlockState state) {
    // should not happen, but catch anyways
    if (largePlant == null) {
      return;
    }

    if (world.isEmptyBlock(pos.above())) {
      largePlant.placeAt(world, pos, 2);
    }
  }

  /** Injects the ability to drop this flower into the loot table for the large version. */
  public void injectLoot(LootTableLoadEvent event) {
    if (largePlant == null ||
        !event.getName().getNamespace().equals("minecraft") ||
        !event.getName().getPath().equals("blocks/" + Objects.requireNonNull(largePlant.getRegistryName()).getPath())
    ) {
      return;
    }
    // We have the right table. Now we want to find the pool which drops the item, and
    // replace it with an alternatives check to drop us if hit by shears.
    // If anything doesn't match what we expect, don't change anything.
    LootTable table = event.getTable();
    //noinspection ConstantConditions  Annotations are wrong
    if (table.removePool("main") == null) {
      return; // Wasn't removed.
    }
    ResourceLocation location = Inspirations.getResource("blocks/inject/" + Objects.requireNonNull(getRegistryName()).getPath());
    table.addPool(new LootPool.Builder()
                      .name(location.toString())
                      .setRolls(ConstantRange.exactly(1))
                      .add(TableLootEntry.lootTableReference(location))
                      .build()
                 );
  }
}
