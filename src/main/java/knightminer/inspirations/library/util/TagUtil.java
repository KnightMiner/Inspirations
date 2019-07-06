package knightminer.inspirations.library.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;

public final class TagUtil {
	private TagUtil() {}

	/* Generic Tag Operations */
	public static NBTTagCompound getTagSafe(ItemStack stack) {
		// yes, the null checks aren't needed anymore, but they don't hurt either.
		// After all the whole purpose of this function is safety/processing possibly invalid input ;)
		if(stack == null || stack.getItem() == null || !stack.hasTagCompound()) {
			return new NBTTagCompound();
		}

		return stack.getTagCompound();
	}

	public static NBTTagCompound getTagSafe(NBTTagCompound tag, String key) {
		if(tag == null) {
			return new NBTTagCompound();
		}

		return tag.getCompoundTag(key);
	}

	/* BlockPos */

	/**
	 * Converts a block position to NBT
	 * @param pos  Position
	 * @return  NBT compound
	 */
	public static NBTTagCompound writePos(BlockPos pos) {
		NBTTagCompound tag = new NBTTagCompound();
		if(pos != null) {
			tag.setInteger("X", pos.getX());
			tag.setInteger("Y", pos.getY());
			tag.setInteger("Z", pos.getZ());
		}
		return tag;
	}

	/**
	 * Reads a block position from a given tag compound
	 * @param tag  Tag to read
	 * @return  BlockPos, or null if invalid
	 */
	public static BlockPos readPos(NBTTagCompound tag) {
		if(tag != null && tag.hasKey("X", 99) && tag.hasKey("Y", 99) && tag.hasKey("Z", 99)) {
			return new BlockPos(tag.getInteger("X"), tag.getInteger("Y"), tag.getInteger("Z"));
		}
		return null;
	}

	/* JSON */
	public static JsonElement getElement(JsonObject json, String memberName) {
		if(json.has(memberName)) {
			return json.get(memberName);
		}
		else {
			throw new JsonSyntaxException("Missing " + memberName + " from the current json, Invalid JSON!");
		}
	}
}
