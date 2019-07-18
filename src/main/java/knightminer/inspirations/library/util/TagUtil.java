package knightminer.inspirations.library.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;

public final class TagUtil {
	private TagUtil() {}

	/* Generic Tag Operations */
	public static CompoundNBT getTagSafe(ItemStack stack) {
		// yes, the null checks aren't needed anymore, but they don't hurt either.
		// After all the whole purpose of this function is safety/processing possibly invalid input ;)
		if(stack == null || stack.getItem() == null || !stack.hasTag()) {
			return new CompoundNBT();
		}

		return stack.getTag();
	}

	public static CompoundNBT getTagSafe(CompoundNBT tag, String key) {
		if(tag == null) {
			return new CompoundNBT();
		}

		return tag.getCompound(key);
	}

	/* BlockPos */

	/**
	 * Converts a block position to NBT
	 * @param pos  Position
	 * @return  NBT compound
	 */
	public static CompoundNBT writePos(BlockPos pos) {
		CompoundNBT tag = new CompoundNBT();
		if(pos != null) {
			tag.putInt("X", pos.getX());
			tag.putInt("Y", pos.getY());
			tag.putInt("Z", pos.getZ());
		}
		return tag;
	}

	/**
	 * Reads a block position from a given tag compound
	 * @param tag  Tag to read
	 * @return  BlockPos, or null if invalid
	 */
	public static BlockPos readPos(CompoundNBT tag) {
		if(tag != null && tag.contains("X", 99) && tag.contains("Y", 99) && tag.contains("Z", 99)) {
			return new BlockPos(tag.getInt("X"), tag.getInt("Y"), tag.getInt("Z"));
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
