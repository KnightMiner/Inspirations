package knightminer.inspirations.building.block;

import knightminer.inspirations.Inspirations;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.FlowerBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootTableReference;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraftforge.event.LootTableLoadEvent;

import javax.annotation.Nullable;

public class GrowableFlowerBlock extends FlowerBlock implements BonemealableBlock {
  private final DoublePlantBlock largePlant;

  public GrowableFlowerBlock(MobEffect effect, int duration, @Nullable DoublePlantBlock largePlant, Properties props) {
    super(effect, duration, props);
    this.largePlant = largePlant;
  }


  /* Doubling up */

  @Override
  public boolean isValidBonemealTarget(LevelReader pLevel, BlockPos pPos, BlockState pState, boolean pIsClient) {
    return largePlant != null;
  }

  @Override
  public boolean isBonemealSuccess(Level pLevel, RandomSource pRandom, BlockPos pPos, BlockState pState) {
    return true;
  }

  @Override
  public void performBonemeal(ServerLevel level, RandomSource pRandom, BlockPos pos, BlockState state) {
    // should not happen, but catch anyways
    if (largePlant == null) {
      return;
    }

    if (level.isEmptyBlock(pos.above())) {
      DoublePlantBlock.placeAt(level, largePlant.defaultBlockState(), pos, 2);
    }
  }

  /** Injects the ability to drop this flower into the loot table for the large version. */
  @SuppressWarnings("deprecation")
  public void injectLoot(LootTableLoadEvent event) {
    // TODO: migrate to mantle loot injectors or GLMs?
    if (largePlant == null ||
        !event.getName().getNamespace().equals("minecraft") ||
        !event.getName().getPath().equals("blocks/" + BuiltInRegistries.BLOCK.getKey(largePlant).getPath())
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
    ResourceLocation location = Inspirations.getResource("blocks/inject/" + BuiltInRegistries.BLOCK.getKey(this).getPath());
    table.addPool(new LootPool.Builder()
                      .name(location.toString())
                      .setRolls(ConstantValue.exactly(1))
                      .add(LootTableReference.lootTableReference(location))
                      .build()
                 );
  }
}
