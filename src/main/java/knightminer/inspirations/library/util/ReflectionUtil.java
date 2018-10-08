package knightminer.inspirations.library.util;

import knightminer.inspirations.Inspirations;
import knightminer.inspirations.library.InspirationsRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.potion.PotionType;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.ReflectionHelper.UnableToFindFieldException;
import net.minecraftforge.fml.relauncher.ReflectionHelper.UnableToFindMethodException;
import net.minecraftforge.fml.relauncher.ReflectionHelper.UnableToFindClassException;
import net.minecraftforge.registries.IRegistryDelegate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unchecked")
public final class ReflectionUtil {
	private ReflectionUtil() {}

	private static final Map<String, Field> FIELDS = new HashMap<>();
	private static final Map<String, Method> METHODS = new HashMap<>();
	private static final Map<String, Class> CLASS = new HashMap<>();

	@Nullable
	public static PotionType getMixPredicateInput(@Nonnull Object mixPredicate) {
		Object val = getFieldValue(getClass("net.minecraft.potion.PotionHelper$MixPredicate"), mixPredicate, "input", "field_185198_a");
		if(val instanceof IRegistryDelegate) {
			return ((IRegistryDelegate<PotionType>) val).get();
		} else { // fallback for older Forge versions
			return (PotionType) val;
		}
	}

	@Nullable
	public static Ingredient getMixPredicateReagent(@Nonnull Object mixPredicate) {
		return getFieldValue(getClass("net.minecraft.potion.PotionHelper$MixPredicate"), mixPredicate, "reagent", "field_185199_b");
	}

	@Nullable
	public static PotionType getMixPredicateOutput(@Nonnull Object mixPredicate) {
		Object val = getFieldValue(getClass("net.minecraft.potion.PotionHelper$MixPredicate"), mixPredicate, "output", "field_185200_c");
		if(val instanceof IRegistryDelegate) {
			return ((IRegistryDelegate<PotionType>) val).get();
		} else { // fallback for older Forge versions
			return (PotionType) val;
		}
	}

	@Nullable
	public static ItemStack invokeGetSilkTouchDrop(@Nonnull Block block, IBlockState state) {
		ItemStack stack = invokeMethod(Block.class, block, "getSilkTouchDrop", "func_180643_i", new Class[] {IBlockState.class}, state);
		if(stack != null) {
			return stack;
		} else {
			return ItemStack.EMPTY;
		}
	}

	/**
	 * Looks up a class by its name, caches it for further use and returns it.<br>
	 * If it can't find the class, it will be logged and <tt>null</tt> is returned.
	 *
	 * @param className The class name to be searched for
	 * @return The class found or <tt>null</tt>, if the class is unavailable
	 */
	public static Class getClass(String className) {
		try {
			return CLASS.computeIfAbsent(className, key -> ReflectionHelper.getClass(Inspirations.class.getClassLoader(), key));
		} catch(UnableToFindClassException e) {
			InspirationsRegistry.log.error(e);
			CLASS.putIfAbsent(className, null); // set cache of class to null if it errors trying to find the class
			return null;
		}
	}

	/**
	 * Searches the instance for the occurrence of a method either named by its SRG name (obfuscated) or MCP name (development),
	 * caches the reference for further use, invokes the method and returns the value returned by the invocation (or <tt>null</tt>, if the method is <tt>void</tt>).<br>
	 * If it can't find the method or something went wrong during invocation, it will be logged and <tt>null</tt> is returned.
	 *
	 * @param instance The instance to be accessed. Cannot be <tt>null</tt>!
	 * @param mcpName The name used in a development environment
	 * @param srgName The name used in an obfuscated environment
	 * @param <T> The type of the return value. Must be the same as or a superclass of the return type of the method! {@link Object}, if the method is <tt>void</tt>
	 *
	 * @return The return value of the method or <tt>null</tt>, if it fails or the method is <tt>void</tt>
	 */
	@Nullable
	private static <T> T invokeMethod(final Class classToSearch, final Object instance, final String mcpName, final String srgName, final Class[] paramTypes, final Object... params) {
		try {
			Method m = METHODS.computeIfAbsent(srgName, key -> ReflectionHelper.findMethod(classToSearch, mcpName, key, paramTypes));
			return (T) m.invoke(instance, params);
		} catch(IllegalAccessException | IllegalArgumentException | NullPointerException | InvocationTargetException | ClassCastException | UnableToFindMethodException e) {
			InspirationsRegistry.log.error(e);
			METHODS.putIfAbsent(srgName, null); // set cache of method to null if it errors trying to find the method in the first place
			return null;
		}
	}

	/**
	 * Searches the class for the occurrence of a field either named by its SRG name (obfuscated) or MCP name (development),
	 * caches the reference for further use and returns the value of the field from the instance (or statically, if the instance is <tt>null</tt>.<br>
	 * If it can't find the field or something went wrong with getting the value, it will be logged and <tt>null</tt> is returned.
	 *
	 * @param instance The instance to be accessed, <tt>null</tt> if the field is static
	 * @param mcpName The name used in a development environment
	 * @param srgName The name used in an obfuscated environment
	 * @param <T> The type of the return value. Must be the same as or a superclass of the type of the field!
	 *
	 * @return The value of the field or <tt>null</tt>, if it fails
	 */
	@Nullable
	private static <T> T getFieldValue(final Class classToSearch, final Object instance, final String mcpName, final String srgName) {
		try {
			Field f = FIELDS.computeIfAbsent(srgName, key -> ReflectionHelper.findField(classToSearch, key, mcpName));
			return f != null ? (T) f.get(instance) : null;
		} catch(IllegalAccessException | ClassCastException | NullPointerException | UnableToFindFieldException e) {
			InspirationsRegistry.log.error(e);
			FIELDS.putIfAbsent(srgName, null); // set cache of field to null if it errors trying to find the field in the first place
			return null;
		}
	}
}
