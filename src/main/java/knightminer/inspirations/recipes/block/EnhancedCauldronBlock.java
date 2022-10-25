package knightminer.inspirations.recipes.block;

import knightminer.inspirations.common.Config;
import knightminer.inspirations.library.recipe.cauldron.util.CauldronTemperature;
import knightminer.inspirations.recipes.InspirationsRecipes;
import knightminer.inspirations.recipes.tileentity.CauldronTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.biome.Biome.Precipitation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import slimeknights.mantle.util.BlockEntityHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Random;

/**
 * Cauldron block exteneded to have a tile entity
 */
@SuppressWarnings("WeakerAccess")
public class EnhancedCauldronBlock extends LayeredCauldronBlock implements EntityBlock {
  public EnhancedCauldronBlock(Block.Properties props) {
    super(props, LayeredCauldronBlock.RAIN, CauldronInteraction.WATER);
  }

  /**
   * Gets the level of fluid in the cauldron
   * @param state  State to check
   * @return  Water level of state
   */
  public int getLevel(BlockState state) {
    return state.getValue(LEVEL);
  }

  // not an override anymore
  public void setWaterLevel(Level world, BlockPos pos, BlockState state, int level) {
    if (level != getLevel(state)) {
      world.setBlockAndUpdate(pos, state.setValue(LEVEL, level));
    }
  }

  @Override
  public void handlePrecipitation(BlockState state, Level world, BlockPos pos, Precipitation precipitation) {
    BlockEntity te = world.getBlockEntity(pos);
    // do not fill unless the current contents are water
    if (te instanceof CauldronTileEntity && !((CauldronTileEntity)te).getContents().isSimple()) {
      return;
    }

    // allow disabling the random 1/20 chance
    if ((Config.fasterCauldronRain.getAsBoolean() || world.random.nextInt(20) == 0) && world.getBiome(pos).value().getTemperature(pos) >= 0.15F) {
      int level = getLevel(state);
      if (level < 3) {
        setWaterLevel(world, pos, state, level + 1);
      }
    }
  }

  @Nullable
  @Override
  public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState pState, BlockEntityType<T> matchType) {
    return BlockEntityHelper.castTicker(matchType, InspirationsRecipes.tileCauldron, level.isClientSide ? CauldronTileEntity.CLIENT_TICKER : CauldronTileEntity.SERVER_TICKER);
  }

  /* TE behavior */

  @Override
  public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult ray) {
    if (Config.cauldronRecipes.getAsBoolean()) {
      // all moved to the cauldron registry
      return InteractionResult.SUCCESS;
    }
    return super.use(state, world, pos, player, hand, ray);
  }

  @Nullable
  @Override
  public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
    return new CauldronTileEntity(pos, state, this);
  }

  @Override
  public void entityInside(BlockState state, Level world, BlockPos pos, Entity entity) {
    if (world.isClientSide) {
      return;
    }

    // check if an entity hit within the water
    int level = getLevel(state);
    if (entity.getBoundingBox().minY <= (pos.getY() + (5.5F + (3 * Math.max(level, 1))) / 16.0F)) {
      // if so, have the TE handle it
      BlockEntityHelper.get(CauldronTileEntity.class, world, pos).ifPresent(te -> {
        int newLevel = te.onEntityCollide(entity, level, state);
        // if the level changed, update it
        if (level != newLevel) {
          this.setWaterLevel(world, pos, state, newLevel);
        }
      });
    }
  }

  @Nonnull
  @SuppressWarnings("deprecation")
  @Override
  @Deprecated
  public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor world, BlockPos currentPos, BlockPos facingPos) {
    // need a method called on both sides, neighborChanged is server only
    BlockEntityHelper.get(CauldronTileEntity.class, world, currentPos).ifPresent(te -> te.neighborChanged(facingPos));
    return state;
  }


  /* Particles */

  @Deprecated
  @Override
  public void animateTick(BlockState state, Level world, BlockPos pos, Random rand) {
    if (getLevel(state) == 0) {
      return;
    }

    // transform particles
    BlockEntity te = world.getBlockEntity(pos);
    if (te instanceof CauldronTileEntity cauldron) {
      int level = cauldron.getFluidLevel();

      // boiling particles if boiling
      if (cauldron.getTemperature() == CauldronTemperature.BOILING) {
        addParticles(InspirationsRecipes.boilingParticle, world, pos, 2, level, rand);
      }

      // transform particles if performing a recipe
      int count = cauldron.getTransformParticles();
      addParticles(ParticleTypes.HAPPY_VILLAGER, world, pos, count, level, rand);
    }
  }

  /**
   * Adds particles
   * @param type   Particle type
   * @param world  World instance
   * @param pos    Block position
   * @param level  Fluid level
   * @param rand   Random instance
   */
  private static void addParticles(ParticleOptions type, Level world, BlockPos pos, int count, int level, Random rand) {
    for (int i = 0; i < count; i++) {
      double x = pos.getX() + 0.1875D + (rand.nextFloat() * 0.625D);
      double y = pos.getY() + 0.1875D + (level * 0.0625);
      double z = pos.getZ() + 0.1875D + (rand.nextFloat() * 0.625D);
      world.addParticle(type, x, y, z, 0, 0, 0);
    }
  }
}
