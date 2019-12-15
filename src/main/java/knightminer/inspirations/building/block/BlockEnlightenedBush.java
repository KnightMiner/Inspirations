package knightminer.inspirations.building.block;

import knightminer.inspirations.building.tileentity.TileEnlightenedBush;
import knightminer.inspirations.library.client.ClientUtil;
import knightminer.inspirations.library.util.TextureBlockUtil;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import slimeknights.mantle.block.EnumBlock;
import slimeknights.mantle.property.PropertyString;

import javax.annotation.Nonnull;
import java.util.Locale;

public class BlockEnlightenedBush extends EnumBlock<BlockEnlightenedBush.LightsType> implements ITileEntityProvider {

	public static final PropertyEnum<LightsType> LIGHTS = PropertyEnum.create("lights", LightsType.class);
	public static final PropertyString TEXTURE = TextureBlockUtil.TEXTURE_PROP;
	public BlockEnlightenedBush() {
		super(Material.LEAVES, LIGHTS, LightsType.class);
		this.setCreativeTab(CreativeTabs.DECORATIONS);
		this.setHardness(0.2F);
		this.setLightOpacity(1);
		this.setLightLevel(1);
		this.setSoundType(SoundType.PLANT);
		this.hasTileEntity = true;
	}

	@Nonnull
	@Override
	protected ExtendedBlockState createBlockState() {
		return new ExtendedBlockState(this, new IProperty[]{LIGHTS}, new IUnlistedProperty[]{TEXTURE});
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileEnlightenedBush();
	}

	@Override
	public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> list) {
		for(LightsType type : LightsType.values()) {
			TextureBlockUtil.addBlocksFromOredict("treeLeaves", this, type.getMeta(), list);
		}
	}


	/*
	 * Properties
	 */
	@Deprecated
	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Deprecated
	@Override
	public boolean causesSuffocation(IBlockState state) {
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public BlockRenderLayer getBlockLayer() {
		return BlockRenderLayer.CUTOUT;
	}


	/*
	 * Texturing
	 */
	@Nonnull
	@Override
	public IBlockState getExtendedState(@Nonnull IBlockState state, IBlockAccess world, BlockPos pos) {
		return ClientUtil.writeTextureBlockState(world, pos, state);
	}


	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer,
			ItemStack stack) {
		super.onBlockPlacedBy(world, pos, state, placer, stack);
		TextureBlockUtil.placeTextureBlock(world, pos, stack);
	}

	@Override
	public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune){
		drops.add(TextureBlockUtil.getBlockItemStack(world, pos, state));
	}

	@Nonnull
	@Override
	public ItemStack getPickBlock(@Nonnull IBlockState state, RayTraceResult target, @Nonnull World world, @Nonnull BlockPos pos, EntityPlayer player) {
		return TextureBlockUtil.getBlockItemStack(world, pos, state);
	}

	@Override
	public boolean removedByPlayer(@Nonnull IBlockState state, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull EntityPlayer player, boolean willHarvest) {
		// we pull up a few calls to this point in time because we still have the TE here
		// the execution otherwise is equivalent to vanilla order
		this.onBlockDestroyedByPlayer(world, pos, state);
		if(willHarvest) {
			this.harvestBlock(world, player, pos, state, world.getTileEntity(pos), player.getHeldItemMainhand());
		}

		world.setBlockToAir(pos);
		// return false to prevent the above called functions to be called again
		// side effect of this is that no xp will be dropped. but it shoudln't anyway from a bookshelf :P
		return false;
	}

	public static enum LightsType implements IStringSerializable, EnumBlock.IEnumMeta {
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

		@Override
		public int getMeta() {
			return meta;
		}

		public int getColor() {
			return color;
		}

		@Override
		public String getName() {
			return this.name().toLowerCase(Locale.US);
		}

		public static LightsType fromMeta(int meta) {
			if(meta < 0 || meta >= values().length) {
				meta = 0;
			}
			return values()[meta];
		}
	}
}
