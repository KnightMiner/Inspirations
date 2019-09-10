package knightminer.inspirations.library.util;

import knightminer.inspirations.common.Config;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tags.Tag;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.client.model.data.ModelProperty;

public final class TextureBlockUtil {

	public static final String TAG_TEXTURE = "texture";
	public static final ModelProperty<String> TEXTURE_PROP = new ModelProperty<>();

	private TextureBlockUtil() {}

	/**
	 * Call in {@link Block#onBlockPlacedBy(World, BlockPos, BlockState, LivingEntity, ItemStack)}
	 * to set the texture tag to the Tile Entity
	 * @param world  World where the block was placed
	 * @param pos    Block position
	 * @param stack  Item stack
	 */
	public static void placeTextureBlock(World world, BlockPos pos, ItemStack stack) {
		CompoundNBT tag = TagUtil.getTagSafe(stack);
		TileEntity te = world.getTileEntity(pos);
		if(te != null) {
			CompoundNBT textureTag = tag.getCompound(TextureBlockUtil.TAG_TEXTURE);
			updateTextureBlock(te, textureTag);
		}
	}

	/**
	 * Called in blocks to get the item stack for the current block
	 * @param world  World
	 * @param pos    Pos
	 * @param state  State
	 * @return
	 */
	public static ItemStack getBlockItemStack(IBlockReader world, BlockPos pos, BlockState state) {
		Block block = state.getBlock();
		ItemStack stack = new ItemStack(block);
		TileEntity te = world.getTileEntity(pos);
		if(te != null) {
			CompoundNBT texture = getTextureBlock(te);
			if(texture.size() > 0) {
				CompoundNBT tags = new CompoundNBT();
				tags.put(TextureBlockUtil.TAG_TEXTURE, texture);
				stack.setTag(tags);
			}
		}
		return stack;
	}

	/**
	 * Updates the current texture block in the TE
	 * @param tag
	 */
	public static void updateTextureBlock(TileEntity te, CompoundNBT tag) {
		if(te != null) {
			te.getTileData().put(TextureBlockUtil.TAG_TEXTURE, tag);
		}
	}

	/**
	 * Gets the current texture block from the TE
	 * @return
	 */
	public static CompoundNBT getTextureBlock(TileEntity te) {
		if(te == null) {
			return new CompoundNBT();
		}
		return te.getTileData().getCompound(TextureBlockUtil.TAG_TEXTURE);
	}

	/**
	 * Creates a new item stack with the given block as it's texture tag
	 * @param texturable  Base block to texture
	 * @param block       Block to use as the texture
	 * @return  The item stack with the proper NBT
	 */
	public static ItemStack createTexturedStack(Block texturable, Block block) {
		ItemStack stack = new ItemStack(texturable);

		if(block != null) {
			ItemStack blockStack = new ItemStack(block);
			CompoundNBT tag = new CompoundNBT();
			CompoundNBT subTag = new CompoundNBT();
			blockStack.write(subTag);
			tag.put(TextureBlockUtil.TAG_TEXTURE, subTag);
			stack.setTag(tag);
		}

		return stack;
	}

	/**
	 * Gets the itemstack that determines the block's texture from the stack.
	 * @param stack  Input stack
	 * @return  The itemstack determining the block's texture, or EMPTY if none exists
	 */
	public static ItemStack getStackTexture(ItemStack stack) {
		CompoundNBT tag = TagUtil.getTagSafe(stack).getCompound(TextureBlockUtil.TAG_TEXTURE);
		return tag.size() > 0 ? ItemStack.read(tag) : ItemStack.EMPTY;
	}

	/**
	 * Adds all blocks from the block tag to the specified block for fillItemGroup
	 */
	public static void addBlocksFromTag(Tag<Block> tag, Block block, NonNullList<ItemStack> list) {
		for(Block textureBlock : tag.getAllElements()) {
			list.add(createTexturedStack(block, textureBlock));
			if(!Config.showAllVariants.get()) {
				return;
			}
		}
	}
}
