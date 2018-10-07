package knightminer.inspirations.library.util;

import knightminer.inspirations.library.InspirationsRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.potion.PotionType;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
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

	@Nullable
	public static PotionType getMixPredicateInput(@Nonnull Object mixPredicate) {
		Object val = getFieldValue(mixPredicate, "input", "field_185198_a");
		if(val instanceof IRegistryDelegate) {
			return ((IRegistryDelegate<PotionType>) val).get();
		} else { // fallback for older Forge versions
			return (PotionType) val;
		}
	}

	@Nullable
	public static Ingredient getMixPredicateReagent(@Nonnull Object mixPredicate) {
		return getFieldValue(mixPredicate, "reagent", "field_185199_b");
	}

	@Nullable
	public static PotionType getMixPredicateOutput(@Nonnull Object mixPredicate) {
		Object val = getFieldValue(mixPredicate, "output", "field_185200_c");
		if(val instanceof IRegistryDelegate) {
			return ((IRegistryDelegate<PotionType>) val).get();
		} else { // fallback for older Forge versions
			return (PotionType) val;
		}
	}

	@Nullable
	public static ItemStack invokeGetSilkTouchDrop(@Nonnull Block block, IBlockState state) {
		ItemStack stack = invokeMethod(block, "getSilkTouchDrop", "func_180643_i", new Class[] {IBlockState.class}, state);
		if(stack != null) {
			return stack;
		} else {
			return ItemStack.EMPTY;
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
	 * @return The return value of the method or null, if it fails or the method is <tt>void</tt>
	 */
	@Nullable
	private static <T> T invokeMethod(@Nonnull final Object instance, final String mcpName, final String srgName, final Class[] paramTypes, final Object... params) {
		Method m = METHODS.computeIfAbsent(srgName, key -> ReflectionHelper.findMethod(instance.getClass(), key, mcpName, paramTypes));

		try {
			return (T) m.invoke(instance, params);
		} catch(IllegalAccessException | IllegalArgumentException | InvocationTargetException | ClassCastException e) {
			InspirationsRegistry.log.error(e);
			return null;
		}
	}

	/**
	 * Searches the instance for the occurrence of a field either named by its SRG name (obfuscated) or MCP name (development),
	 * caches the reference for further use and returns the value of the field.<br>
	 * If it can't find the field or something went wrong with getting the value, it will be logged and <tt>null</tt> is returned.
	 *
	 * @param instance The instance to be accessed. Cannot be <tt>null</tt>!
	 * @param mcpName The name used in a development environment
	 * @param srgName The name used in an obfuscated environment
	 * @param <T> The type of the return value. Must be the same as or a superclass of the type of the field!
	 *
	 * @return The value of the field or null, if it fails
	 */
	@Nullable
	private static <T> T getFieldValue(@Nonnull final Object instance, final String mcpName, final String srgName) {
		Field f = FIELDS.computeIfAbsent(srgName, key -> ReflectionHelper.findField(instance.getClass(), key, mcpName));

		try {
			return (T) f.get(instance);
		} catch(IllegalAccessException | ClassCastException e) {
			InspirationsRegistry.log.error(e);
			return null;
		}
	}
}
