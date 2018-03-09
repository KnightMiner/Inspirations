package knightminer.inspirations.recipes.block;

import java.util.Locale;
import java.util.Random;

import javax.annotation.Nonnull;

import knightminer.inspirations.library.util.TextureBlockUtil;
import knightminer.inspirations.recipes.client.BoilingParticle;
import knightminer.inspirations.recipes.tileentity.TileCauldron;
import net.minecraft.block.BlockCauldron;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import slimeknights.mantle.property.PropertyString;

public class BlockEnhancedCauldron extends BlockCauldron implements ITileEntityProvider {

	public static final PropertyEnum<CauldronContents> CONTENTS = PropertyEnum.create("contents", CauldronContents.class);
	public static final PropertyBool BOILING = PropertyBool.create("boiling");
	public static final PropertyString TEXTURE = TextureBlockUtil.TEXTURE_PROP;

	public BlockEnhancedCauldron() {
		this.setDefaultState(this.blockState.getBaseState()
				.withProperty(LEVEL, 0)
				.withProperty(BOILING, false)
				.withProperty(CONTENTS, CauldronContents.FLUID));
		this.setHardness(2.0F);
		this.setUnlocalizedName("cauldron");
		this.hasTileEntity = true;
	}

	/* TE behavior */

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileCauldron();
	}

	@Override
	public void fillWithRain(World world, BlockPos pos) {
		TileEntity te = world.getTileEntity(pos);
		// do not fill unless the current contents are water
		if(te instanceof TileCauldron && !((TileCauldron) te).isWater()) {
			return;
		}
		super.fillWithRain(world, pos);
	}

	/**
	 * Called When an Entity Collided with the Block
	 */
	@Override
	public void onEntityCollidedWithBlock(World world, BlockPos pos, IBlockState state, Entity entity) {
		TileEntity te = world.getTileEntity(pos);
		// do not estinguish unless the current contents are water
		if(te instanceof TileCauldron && !((TileCauldron) te).isWater()) {
			return;
		}
		super.onEntityCollidedWithBlock(world, pos, state, entity);
	}

	/* Content texture */

	@Override
	protected ExtendedBlockState createBlockState() {
		return new ExtendedBlockState(this, new IProperty[]{LEVEL, CONTENTS, BOILING}, new IUnlistedProperty[]{TEXTURE});
	}

	@Override
	public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {
		TileEntity te = world.getTileEntity(pos);
		if(te instanceof TileCauldron) {
			state = state.withProperty(CONTENTS, ((TileCauldron)te).getContentType());
		}

		// add boiling
		state = state.withProperty(BOILING, world.getBlockState(pos.down()).getBlock() == Blocks.FIRE);

		return state;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void randomDisplayTick(IBlockState state, World world, BlockPos pos, Random rand) {
		if(!state.getActualState(world, pos).getValue(BOILING)) {
			return;
		}
		int level = state.getValue(LEVEL);
		if(level == 0) {
			return;
		}

		ParticleManager manager = Minecraft.getMinecraft().effectRenderer;
		for(int i = 0; i < 2; i++) {
			double x = pos.getX() + 0.1875D + (rand.nextFloat() * 0.625D);
			double y = pos.getY() + 0.375D + (level * 0.1875D);
			double z = pos.getZ() + 0.1875D + (rand.nextFloat() * 0.625D);
			manager.addEffect(new BoilingParticle(world, x, y, z, 0, 0, 0));
		}
	}

	@Nonnull
	@Override
	public IBlockState getExtendedState(@Nonnull IBlockState state, IBlockAccess world, BlockPos pos) {
		IExtendedBlockState extendedState = (IExtendedBlockState) state;

		TileEntity te = world.getTileEntity(pos);
		if(te instanceof TileCauldron) {
			return ((TileCauldron)te).writeExtendedBlockState(extendedState);
		}

		return super.getExtendedState(state, world, pos);
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
