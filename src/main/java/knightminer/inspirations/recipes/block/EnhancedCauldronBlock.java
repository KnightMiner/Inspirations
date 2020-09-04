package knightminer.inspirations.recipes.block;

import knightminer.inspirations.common.Config;
import knightminer.inspirations.library.recipe.cauldron.util.CauldronTemperature;
import knightminer.inspirations.recipes.InspirationsRecipes;
import knightminer.inspirations.recipes.tileentity.CauldronTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CauldronBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import slimeknights.mantle.util.TileEntityHelper;

import java.util.Random;

/**
 * Cauldron block exteneded to have a tile entity
 */
@SuppressWarnings("WeakerAccess")
public class EnhancedCauldronBlock extends CauldronBlock {
  public EnhancedCauldronBlock(Block.Properties props) {
    super(props);
  }

  /**
   * Gets the level of fluid in the cauldron
   * @param state  State to check
   * @return  Water level of state
   */
  public int getLevel(BlockState state) {
    return state.get(LEVEL);
  }

  @Override
  public void setWaterLevel(World world, BlockPos pos, BlockState state, int level) {
    if (level != getLevel(state)) {
      super.setWaterLevel(world, pos, state, level);
    }
  }

  @Override
  public void fillWithRain(World world, BlockPos pos) {
    TileEntity te = world.getTileEntity(pos);
    // do not fill unless the current contents are water
    if (te instanceof CauldronTileEntity && !((CauldronTileEntity)te).getContents().isSimple()) {
      return;
    }

    // allow disabling the random 1/20 chance
    if ((Config.fasterCauldronRain.get() || world.rand.nextInt(20) == 0) && world.getBiome(pos).getTemperature(pos) >= 0.15F) {
      BlockState state = world.getBlockState(pos);
      int level = getLevel(state);
      if (level < 3) {
        setWaterLevel(world, pos, state, level + 1);
      }
    }
  }

  /* TE behavior */

  @Override
  public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult ray) {
    // all moved to the cauldron registry
    return ActionResultType.SUCCESS;
  }

  @Override
  public boolean hasTileEntity(BlockState state) {
    return true;
  }

  @Override
  public TileEntity createTileEntity(BlockState state, IBlockReader world) {
    return new CauldronTileEntity(this);
  }

  @Override
  public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
    if (world.isRemote) {
      return;
    }

    // check if an entity hit within the water
    int level = getLevel(state);
    if (entity.getBoundingBox().minY <= (pos.getY() + (5.5F + (3 * Math.max(level, 1))) / 16.0F)) {
      // if so, have the TE handle it
      TileEntityHelper.getTile(CauldronTileEntity.class, world, pos).ifPresent(te -> {
        int newLevel = te.onEntityCollide(entity, level, state);
        // if the level changed, update it
        if (level != newLevel) {
          this.setWaterLevel(world, pos, state, newLevel);
        }
      });
    }
  }

  @SuppressWarnings("deprecation")
  @Override
  @Deprecated
  public BlockState updatePostPlacement(BlockState state, Direction facing, BlockState facingState, IWorld world, BlockPos currentPos, BlockPos facingPos) {
    // need a method called on both sides, neighborChanged is server only
    TileEntityHelper.getTile(CauldronTileEntity.class, world, currentPos).ifPresent(te -> te.neighborChanged(facingPos));
    return state;
  }


  /* Particles */

  @Deprecated
  @Override
  @OnlyIn(Dist.CLIENT)
  public void animateTick(BlockState state, World world, BlockPos pos, Random rand) {
    int level = getLevel(state);
    if (level == 0) {
      return;
    }

    // transform particles
    TileEntity te = world.getTileEntity(pos);
    if (te instanceof CauldronTileEntity) {
      CauldronTileEntity cauldron = (CauldronTileEntity)te;

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
  private static void addParticles(IParticleData type, World world, BlockPos pos, int count, int level, Random rand) {
    for (int i = 0; i < count; i++) {
      double x = pos.getX() + 0.1875D + (rand.nextFloat() * 0.625D);
      double y = pos.getY() + 0.375D  + (level * 0.1875D);
      double z = pos.getZ() + 0.1875D + (rand.nextFloat() * 0.625D);
      world.addParticle(type, x, y, z, 0, 0, 0);
    }
  }
}
