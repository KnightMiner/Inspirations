package knightminer.inspirations.library.event;

import knightminer.inspirations.library.recipe.cauldron.ICauldronRecipe;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * Base class for registering things to the Inspirations Registry
 * @param <T> Class being registered
 */
@Cancelable
public abstract class RegisterEvent<T> extends Event {
	private final T recipe;

	public RegisterEvent(T recipe) {
		this.recipe = recipe;
	}

	public T getRecipe() {
		return recipe;
	}

	/** Returns true on success, false if cancelled */
	public boolean fire() {
		return !MinecraftForge.EVENT_BUS.post(this);
	}

	/**
	 * Class for registering cauldron recipes. May be many different cauldron recipe types
	 */
	public static class RegisterCauldronRecipe extends RegisterEvent<ICauldronRecipe> {
		public RegisterCauldronRecipe(ICauldronRecipe recipe) {
			super(recipe);
		}
	}
}
