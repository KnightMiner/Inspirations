package knightminer.inspirations.library.util;

import net.minecraft.item.crafting.Ingredient;
import net.minecraft.potion.PotionType;
import net.minecraftforge.registries.IRegistryDelegate;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public final class ReflectionUtil
{
	private ReflectionUtil() {}

	private static final Map<Integer, Field> FIELDS = new HashMap<>();

	public static IRegistryDelegate<PotionType> getMixPredicateInput(Object mixPredicate) {
		return getFieldValue(mixPredicate, "input", "field_185198_a", 0);
	}

	public static Ingredient getMixPredicateReagent(Object mixPredicate) {
		return getFieldValue(mixPredicate, "reagent", "field_185199_b", 1);
	}

	public static IRegistryDelegate<PotionType> getMixPredicateOutput(Object mixPredicate) {
		return getFieldValue(mixPredicate, "output", "field_185200_c", 2);
	}

	@SuppressWarnings("unchecked")
	private static <T> T getFieldValue(Object instance, String mcpName, String srgName, int index) {
		Field f = FIELDS.get(index);
		if(f == null) {
			try {
				f = instance.getClass().getDeclaredField(srgName);
			} catch(NoSuchFieldException e) {
				try {
					f = instance.getClass().getDeclaredField(mcpName);
				} catch(NoSuchFieldException ex) {
					throw new RuntimeException("Could not access Field " + instance.getClass().getName() + '#' + mcpName, ex);
				}
			}
			f.setAccessible(true);
			FIELDS.put(index, f);
		}

		try {
			return (T) f.get(instance);
		} catch( IllegalAccessException e ) {
			return null;
		}
	}
}
