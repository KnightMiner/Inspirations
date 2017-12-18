package knightminer.inspirations.plugins.jei.texture;

import net.minecraft.item.ItemStack;
import knightminer.inspirations.library.util.RecipeUtil;
import mezz.jei.api.ISubtypeRegistry.ISubtypeInterpreter;

// Hanldes table and rack subtypes
public class TextureSubtypeInterpreter implements ISubtypeInterpreter {
	@Override
	public String apply(ItemStack stack) {
		// we have to handle the metadata here
		String meta = stack.getMetadata() + ":";

		// if the legs exist, return that for the identification key
		ItemStack textureStack = RecipeUtil.getStackTexture(stack);
		if(!textureStack.isEmpty()) {
			return meta + textureStack.getItem().getRegistryName() + ":" + textureStack.getMetadata();
		}

		// otherwise, simply go back to the meta
		return meta;
	}

}
