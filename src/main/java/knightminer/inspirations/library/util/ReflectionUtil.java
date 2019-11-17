package knightminer.inspirations.library.util;

import knightminer.inspirations.library.InspirationsRegistry;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.potion.PotionBrewing;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper.UnableToFindFieldException;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper.UnableToFindMethodException;
import net.minecraftforge.registries.ForgeRegistryEntry;
import net.minecraftforge.registries.IRegistryDelegate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public final class ReflectionUtil {
	private ReflectionUtil() {}

	private static final Map<String, Field> FIELDS = new HashMap<>();
	private static final Map<String, Method> METHODS = new HashMap<>();

	/* PotionBrewing.MixPredicate */

	@Nullable
	public static <T extends ForgeRegistryEntry<T>> T getMixPredicateInput(@Nonnull PotionBrewing.MixPredicate<T> mixPredicate) {
		IRegistryDelegate<T> effect = getPrivateValue(PotionBrewing.MixPredicate.class, mixPredicate, "field_185198_a");
		return effect.get();
	}

	@Nullable
	public static Ingredient getMixPredicateReagent(@Nonnull PotionBrewing.MixPredicate mixPredicate) {
		return getPrivateValue(PotionBrewing.MixPredicate.class, mixPredicate, "field_185199_b");
	}

	@Nullable
	public static <T extends ForgeRegistryEntry<T>> T getMixPredicateOutput(@Nonnull PotionBrewing.MixPredicate<T> mixPredicate) {
		IRegistryDelegate<T> effect = getPrivateValue(PotionBrewing.MixPredicate.class, mixPredicate, "field_185200_c");
		return effect.get();
	}


	/* Base Methods */

	/**
	 * Searches the instance for the occurrence of a method either named by its SRG name (obfuscated) or MCP name (development),
	 * caches the reference for further use, invokes the method and returns the value returned by the invocation (or <tt>null</tt>, if the method is <tt>void</tt>).<br>
	 * If it can't find the method or something went wrong during invocation, it will be logged and <tt>null</tt> is returned.
	 *
	 * @param instance The instance to be accessed. Cannot be <tt>null</tt>!
	 * @param name The name used in a development environment
	 * @param <T> The type of the return value. Must be the same as or a superclass of the return type of the method! {@link Object}, if the method is <tt>void</tt>
	 *
	 * @return The return value of the method or <tt>null</tt>, if it fails or the method is <tt>void</tt>
	 */
	@Nullable
	private static <T> T invokeMethod(final Class<?> classToSearch, final Object instance, final String name, final Class<?>[] paramTypes, final Object... params) {
		try {
			Method m = METHODS.computeIfAbsent(name, key -> ObfuscationReflectionHelper.findMethod(classToSearch, name, paramTypes));
			return m != null ? (T) m.invoke(instance, params) : null;
		} catch(IllegalAccessException | IllegalArgumentException | InvocationTargetException | ClassCastException | UnableToFindMethodException e) {
			InspirationsRegistry.log.error(e);
			METHODS.putIfAbsent(name, null); // set cache of method to null if it errors trying to find the method in the first place
			return null;
		}
	}

	/**
	 * Searches the class for the occurrence of a field either named by its SRG name (obfuscated) or MCP name (development),
	 * caches the reference for further use and returns the value of the field from the instance (or statically, if the instance is <tt>null</tt>.<br>
	 * If it can't find the field or something went wrong with getting the value, it will be logged and <tt>null</tt> is returned.
	 *
	 * @param instance The instance to be accessed, <tt>null</tt> if the field is static
	 * @param name SRG name of the field to find
	 * @param <T> The type of the return value. Must be the same as or a superclass of the type of the field!
	 *
	 * @return The value of the field or <tt>null</tt>, if it fails
	 */
	@Nullable
	private static <C, T> T getPrivateValue(final Class<? super C> classToSearch, final Object instance, final String name) {
		try {
			Field f = FIELDS.computeIfAbsent(name, key -> ObfuscationReflectionHelper.findField(classToSearch, name));
			return f != null ? (T) f.get(instance) : null;
		} catch(IllegalAccessException | UnableToFindFieldException | ClassCastException e) {
			InspirationsRegistry.log.error(e);
			FIELDS.putIfAbsent(name, null); // set cache of field to null if it errors trying to find the field in the first place
			return null;
		}
	}
}
