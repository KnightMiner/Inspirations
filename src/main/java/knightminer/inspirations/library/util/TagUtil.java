package knightminer.inspirations.library.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

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
