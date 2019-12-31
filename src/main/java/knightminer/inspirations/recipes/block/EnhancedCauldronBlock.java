package knightminer.inspirations.recipes.block;

import knightminer.inspirations.common.Config;
import knightminer.inspirations.library.InspirationsRegistry;
import knightminer.inspirations.library.InspirationsTags;
import knightminer.inspirations.recipes.client.BoilingParticle;
import knightminer.inspirations.recipes.tileentity.CauldronTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CauldronBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.client.model.data.ModelProperty;

import javax.annotation.Nonnull;
import java.util.Locale;
import java.util.Random;

public class EnhancedCauldronBlock extends CauldronBlock {

	public static final EnumProperty<CauldronContents> CONTENTS = EnumProperty.create("contents", CauldronContents.class);
	public static final IntegerProperty LEVEL_EXT = IntegerProperty.create("levels", 0, 4);
	public static final BooleanProperty BOILING = BooleanProperty.create("boiling");
	public static final ModelProperty<String> TEXTURE = new ModelProperty<>();

	public EnhancedCauldronBlock() {
		super(Block.Properties.from(Blocks.CAULDRON));

		BlockState state = this.getDefaultState()
				.with(LEVEL, 0)
				.with(BOILING, false)
				.with(CONTENTS, CauldronContents.FLUID);
		if (Config.enableBiggerCauldron()) {
			state = state.with(LEVEL_EXT, 0);
		}
		this.setDefaultState(state);
		this.setRegistryName(Blocks.CAULDRON.getRegistryName());
	}


	/* TE behavior */
	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return new CauldronTileEntity();
	}

	@Override
	public void fillWithRain(World world, @Nonnull BlockPos pos) {
		TileEntity te = world.getTileEntity(pos);
		// do not fill unless the current contents are water
		if(te instanceof CauldronTileEntity && !((CauldronTileEntity) te).isWater()) {
			return;
		}

		// allow disabling the random 1/20 chance
		if((Config.fasterCauldronRain() || world.rand.nextInt(20) == 0)
				&& world.getBiome(pos).getTemperature(pos) >= 0.15F) {
			BlockState state = world.getBlockState(pos);
			int level = getLevel(state);
			if(level < (Config.enableBiggerCauldron() ? 4 : 3)) {
				setWaterLevel(world, pos, state, level+1);
			}
		}
	}

	/**
	 * Called When an Entity Collided with the Block
	 */
	@Override
	public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
		TileEntity te = world.getTileEntity(pos);
		// do not estinguish unless the current contents are water
		if(!(te instanceof CauldronTileEntity)) {
			return;
		}
		if(world.isRemote) {
			return;
		}

		// ensure the entity is touching the fluid inside
		int level = getLevel(state);
		float f = pos.getY() + ((Config.enableBiggerCauldron() ? 2.5F : 5.5F) + 3 * Math.max(level, 1)) / 16.0F;
		if (entity.getBoundingBox().minY <= f) {
			// if so, have the TE handle it
			int newLevel = ((CauldronTileEntity)te).onEntityCollide(entity, level, state);
			// if the level changed, update it
			if(level != newLevel) {
				this.setWaterLevel(world, pos, state, newLevel);
			}

		}
	}

	@Deprecated
	@Override
	public void onReplaced(BlockState state, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull BlockState newState, boolean isMoving) {
		if (newState.getBlock() != state.getBlock() && !isMoving) {
			int level = getLevel(state);
			if (Config.dropCauldronContents.get() && level > 0) {
				TileEntity te = world.getTileEntity(pos);
				if (te instanceof CauldronTileEntity) {
					((CauldronTileEntity) te).onBreak(pos, level);
				}
			}
		}
		super.onReplaced(state, world, pos, newState, isMoving);
	}

	/* TODO: reimplement
	@Override
	public boolean removedByPlayer(BlockState state, World world, BlockPos pos, PlayerEntity player, boolean willHarvest) {
		this.onBlockHarvested(world, pos, state, player);
		world.setBlockState(pos, Blocks.AIR.getDefaultState(), world.isRemote ? 11 : 3);
		return world.getBlockState(pos).getBlock() != Blocks.CAULDRON;
	}*/

	/* Content texture */

	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(CONTENTS, BOILING, LEVEL);
		if(Config.enableBiggerCauldron()) {
			builder.add(LEVEL_EXT);
		}
	}

	@Deprecated
	@Nonnull
	@Override
	public BlockState updatePostPlacement(@Nonnull BlockState state, Direction facing, BlockState facingState, IWorld world, BlockPos pos, BlockPos facingPos) {
		TileEntity te = world.getTileEntity(pos);
		if(te instanceof CauldronTileEntity) {
			state = state.with(CONTENTS, ((CauldronTileEntity)te).getContentType());
		}
		return state;
	}

	@Override
	public boolean onBlockActivated(@Nonnull BlockState state, @Nonnull World world, @Nonnull BlockPos pos, PlayerEntity player, @Nonnull Hand hand, BlockRayTraceResult ray) {
		return true; // all moved to the cauldron registry
	}


	/* boiling */

	@Deprecated
	@Override
	public void neighborChanged(BlockState state, World world, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
		setBoiling(world, pos, state);
	}

	private static void setBoiling(World world, BlockPos pos, BlockState state) {
		world.setBlockState(pos, state.with(BOILING,
				world.getBlockState(pos.down()).getBlock().isIn(InspirationsTags.Blocks.CAULDRON_FIRE)
		));
	}

	@Deprecated
	@Override
	public void randomTick(BlockState state, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull Random rand) {
		if(!state.get(BOILING)) {
			return;
		}

		int level = getLevel(state);
		if(level == 0) {
			return;
		}

		ParticleManager manager = Minecraft.getInstance().particles;
		for(int i = 0; i < 2; i++) {
			double x = pos.getX() + 0.1875D + (rand.nextFloat() * 0.625D);
			double y = pos.getY() + (Config.enableBiggerCauldron() ? 0.1875 : 0.375D) + (level * 0.1875D);
			double z = pos.getZ() + 0.1875D + (rand.nextFloat() * 0.625D);
			manager.addEffect(new BoilingParticle(world, x, y, z, 0, 0, 0));
		}
	}


	/* 4 bottle support */
	@Override
	public void setWaterLevel(World worldIn, @Nonnull BlockPos pos, BlockState state, int level) {
		// if 4, set 4 prop
		if(Config.enableBiggerCauldron()) {
			state = state.with(LEVEL_EXT, MathHelper.clamp(level, 0, 4));
		}
		worldIn.setBlockState(pos, state.with(LEVEL, MathHelper.clamp(level, 0, 3)), 2);
		worldIn.updateComparatorOutputLevel(pos, this);
	}

	/**
	 * Gets the level of a cauldron from the given state
	 * @param state  Block state
	 * @return  Cauldron level
	 */
	public static int getCauldronLevel(BlockState state) {
		Block block = state.getBlock();
		if(state.getBlock() instanceof EnhancedCauldronBlock) {
			return ((EnhancedCauldronBlock)block).getLevel(state);
		}
		if(state.getProperties().contains(LEVEL)) {
			return state.get(LEVEL);
		}
		return InspirationsRegistry.getCauldronMax();
	}

	public int getLevel(BlockState state) {
		if(Config.enableBiggerCauldron()) {
			return state.get(LEVEL_EXT);
		}
		return state.get(LEVEL);
	}

	@Deprecated
	@Override
	public int getComparatorInputOverride(BlockState state, World world, BlockPos pos) {
		return getLevel(state);
	}

	public static enum CauldronContents implements IStringSerializable {
		FLUID,
		DYE,
		POTION;

		private int meta;
		CauldronContents() {
			this.meta = ordinal();
		}

		@Override
		public String getName() {
			return name().toLowerCase(Locale.US);
		}

		public int getMeta() {
			return meta;
		}

		public static CauldronContents fromMeta(int meta) {
			if(meta > values().length) {
				meta = 0;
			}

			return values()[meta];
		}
	}
}
