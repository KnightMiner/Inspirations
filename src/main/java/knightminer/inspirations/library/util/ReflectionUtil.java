package knightminer.inspirations.library.util;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.potion.PotionType;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.registries.IRegistryDelegate;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unchecked")
public final class ReflectionUtil
{
	private ReflectionUtil() {}

	private static final Map<Integer, Field> FIELDS = new HashMap<>();
	private static final Map<Integer, Method> METHODS = new HashMap<>();

	public static PotionType getMixPredicateInput(Object mixPredicate) {
		Object val = getFieldValue(mixPredicate, "input", "field_185198_a", 0, false);
		if( val instanceof IRegistryDelegate ) {
			return ((IRegistryDelegate<PotionType>) val).get();
		} else { // fallback for older Forge versions
			return (PotionType) val;
		}
	}

	public static Ingredient getMixPredicateReagent(Object mixPredicate) {
		return getFieldValue(mixPredicate, "reagent", "field_185199_b", 1, false);
	}

	public static PotionType getMixPredicateOutput(Object mixPredicate) {
		Object val = getFieldValue(mixPredicate, "output", "field_185200_c", 2, false);
		if(val instanceof IRegistryDelegate) {
			return ((IRegistryDelegate<PotionType>) val).get();
		} else { // fallback for older Forge versions
			return (PotionType) val;
		}
	}

	public static ItemStack invokeGetSilkTouchDrop(Block block, IBlockState state) {
		ItemStack stack = invokeMethod(block, "getSilkTouchDrop", "func_180643_i", 0, true, new Class[] {IBlockState.class}, state);
		if(stack != null) {
			return stack;
		} else {
			return ItemStack.EMPTY;
		}
	}

	private static <T> T invokeMethod(Object instance, String mcpName, String srgName, int index, boolean throwException, Class[] paramTypes, Object... params) {
		Method m = METHODS.get(index);
		if(m == null) {
			m = ReflectionHelper.findMethod(instance.getClass(), srgName, mcpName, paramTypes);
			METHODS.put(index, m);
		}

		try {
			return (T) m.invoke(instance, params);
		} catch(IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			if(throwException) {
				throw new RuntimeException(e);
			} else {
				return null;
			}
		}
	}

	private static <T> T getFieldValue(Object instance, String mcpName, String srgName, int index, boolean throwException) {
		Field f = FIELDS.get(index);
		if(f == null) {
			f = ReflectionHelper.findField(instance.getClass(), srgName, mcpName);
			FIELDS.put(index, f);
		}

		try {
			return (T) f.get(instance);
		} catch(IllegalAccessException e) {
			if(throwException) {
				throw new RuntimeException(e);
			} else {
				return null;
			}
		}
	}
}
