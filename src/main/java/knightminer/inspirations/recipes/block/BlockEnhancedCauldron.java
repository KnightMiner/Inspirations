package knightminer.inspirations.recipes.block;

import java.util.Locale;

import javax.annotation.Nonnull;

import knightminer.inspirations.recipes.tileentity.TileCauldron;
import knightminer.inspirations.shared.InspirationsShared;
import net.minecraft.block.BlockCauldron;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import slimeknights.mantle.property.PropertyString;

public class BlockEnhancedCauldron extends BlockCauldron implements ITileEntityProvider {

	public static final PropertyEnum<CauldronContents> CONTENTS = PropertyEnum.create("contents", CauldronContents.class);
	public static final PropertyString TEXTURE = InspirationsShared.TEXTURE;

	public BlockEnhancedCauldron() {
		this.setDefaultState(this.blockState.getBaseState().withProperty(LEVEL, 0).withProperty(CONTENTS, CauldronContents.FLUID));
		this.setHardness(2.0F);
		this.setUnlocalizedName("cauldron");
		this.hasTileEntity = true;
	}

	/* TE behavior */

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileCauldron();
	}

	/**
	 * Called when the block is right clicked by a player.
	 */
	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		TileEntity te = world.getTileEntity(pos);
		if(!(te instanceof TileCauldron)) {
			return super.onBlockActivated(world, pos, state, player, hand, facing, hitX, hitY, hitZ);
		}

		TileCauldron cauldron = (TileCauldron) te;
		if(cauldron.interact(state, player, hand)) {
			return true;
		}

		if(cauldron.isWater()) {
			return super.onBlockActivated(world, pos, state, player, hand, facing, hitX, hitY, hitZ);
		}
		return false;
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
		return new ExtendedBlockState(this, new IProperty[]{LEVEL, CONTENTS}, new IUnlistedProperty[]{TEXTURE});
	}

	@Override
	public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {
		TileEntity te = world.getTileEntity(pos);
		if(te instanceof TileCauldron) {
			state = state.withProperty(CONTENTS, ((TileCauldron)te).getContentType());
		}

		return state;
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
