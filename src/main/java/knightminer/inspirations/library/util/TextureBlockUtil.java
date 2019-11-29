package knightminer.inspirations.library.util;

import knightminer.inspirations.common.Config;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tags.Tag;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.client.model.data.ModelProperty;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;

public final class TextureBlockUtil {

	/** Tag name for texture blocks. Should not be used directly, use the utils to interact */
	private static final String TAG_TEXTURE = "texture";
	public static final ModelProperty<String> TEXTURE_PROP = new ModelProperty<>();

	private TextureBlockUtil() {}

	/* Tile Entity Setting */

	/**
	 * Call in {@link Block#onBlockPlacedBy(World, BlockPos, BlockState, LivingEntity, ItemStack)}
	 * to set the texture tag to the Tile Entity
	 * @param world  World where the block was placed
	 * @param pos    Block position
	 * @param stack  Item stack
	 */
	public static void updateTextureBlock(World world, BlockPos pos, ItemStack stack) {
		TileEntity te = world.getTileEntity(pos);
		if(te != null && stack.hasTag()) {
			updateTextureBlock(te, stack.getTag());
		}
	}

	/**
	 * Updates the current texture block in the TE
	 * @param tags  NBT tags containing update information
	 */
	public static void updateTextureBlock(TileEntity te, CompoundNBT tags) {
		if(te != null) {
			String texture = tags.getString(TAG_TEXTURE);
			if (!texture.isEmpty()) {
				te.getTileData().putString(TAG_TEXTURE, texture);
			}
		}
	}


	/* Tile Entity Getting */

	/**
	 * Called in blocks to get the item stack for the current block
	 * @param world  World
	 * @param pos    Pos
	 * @param state  State
	 * @return
	 */
	public static ItemStack getPickBlock(IBlockReader world, BlockPos pos, BlockState state) {
		Block block = state.getBlock();
		ItemStack stack = new ItemStack(block);
		TileEntity te = world.getTileEntity(pos);
		if(te != null) {
			String texture = getTextureBlockName(te);
			if(!texture.isEmpty()) {
				CompoundNBT tags = new CompoundNBT();
				tags.putString(TAG_TEXTURE, texture);
				stack.setTag(tags);
			}
		}
		return stack;
	}

	/**
	 * Gets the current texture block from the TE
	 * @return  Block, or AIR if none is set
	 */
	public static Block getTextureBlock(TileEntity te) {
		if(te == null) {
			return Blocks.AIR;
		}
		String blockName = getTextureBlockName(te);
		if (blockName.isEmpty()) {
			return Blocks.AIR;
		}
		return ForgeRegistries.BLOCKS.getValue(new ResourceLocation(blockName));
	}

	/**
	 * Gets the current texture block from the TE
	 * @return
	 */
	public static String getTextureBlockName(TileEntity te) {
		if(te == null) {
			return "";
		}
		return te.getTileData().getString(TextureBlockUtil.TAG_TEXTURE);
	}


	/* Item Stack Setting */

	/**
	 * Creates a new item stack with the given block as it's texture tag
	 * @param texturable  Base block to texture
	 * @param block       Block to use as the texture
	 * @return  The item stack with the proper NBT
	 */
	public static ItemStack createTexturedStack(Block texturable, Block block) {
		ItemStack stack = new ItemStack(texturable);

		if(block != null && block != Blocks.AIR) {
			setStackTexture(stack, block.getRegistryName().toString());
		}

		return stack;
	}

	/**
	 * Creates a new item stack with the given block as it's texture tag
	 * @param stack      Stack to modify
	 * @param blockName  Block name to set
	 * @return  The item stack with the proper NBT
	 */
	public static ItemStack setStackTexture(ItemStack stack, @Nonnull String blockName) {
		if(!blockName.isEmpty()) {
			CompoundNBT tag = stack.getOrCreateTag();
			tag.putString(TextureBlockUtil.TAG_TEXTURE, blockName);
			stack.setTag(tag);
		}

		return stack;
	}

	/**
	 * Creates a new item stack with the given block as it's texture tag
	 * @param stack  Stack to modify
	 * @param block  Block to set
	 * @return  The item stack with the proper NBT
	 */
	public static ItemStack setStackTexture(ItemStack stack, Block block) {
		if (block == null || block == Blocks.AIR) {
			return stack;
		}
		return setStackTexture(stack, block.getRegistryName().toString());
	}

	/**
	 * Adds all blocks from the block tag to the specified block for fillItemGroup
	 */
	public static void addBlocksFromTag(Block block, Tag<Item> tag, NonNullList<ItemStack> list) {
		boolean added = false;
		// using item tags as that is what will be present in the recipe
		for(Item candidate : tag.getAllElements()) {
			// non-block items don't have the textures we need
			if (!(candidate instanceof BlockItem)) {
				continue;
			}
			Block textureBlock = ((BlockItem)candidate).getBlock();
			// Don't add instances of the block itself, see enlightened bushes
			if (block.getClass().isInstance(textureBlock)) {
				continue;
			}
			added = true;
			list.add(createTexturedStack(block, textureBlock));
			if(!Config.showAllVariants.get()) {
				return;
			}
		}
		// if we never got one, just add the textureless one
		if (!added) {
			list.add(new ItemStack(block));
		}
	}


	/* Item Stack Getting */

	/**
	 * Gets the itemstack that determines the block's texture from the stack.
	 * @param stack  Input stack
	 * @return  The block determining the blocks texture, or AIR if none
	 */
	public static String getTextureBlockName(ItemStack stack) {
		return TagUtil.getTagSafe(stack).getString(TextureBlockUtil.TAG_TEXTURE);
	}

	/**
	 * Gets the itemstack that determines the block's texture from the stack.
	 * @param stack  Input stack
	 * @return  The block determining the blocks texture, or AIR if none
	 */
	public static Block getTextureBlock(ItemStack stack) {
		String texture = getTextureBlockName(stack);
		if (texture.isEmpty()) {
			return Blocks.AIR;
		}
		return ForgeRegistries.BLOCKS.getValue(new ResourceLocation(texture));
	}
}
