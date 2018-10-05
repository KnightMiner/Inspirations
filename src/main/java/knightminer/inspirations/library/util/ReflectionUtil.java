package knightminer.inspirations.library.util;

import net.minecraft.item.crafting.Ingredient;
import net.minecraft.potion.PotionType;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.registries.IRegistryDelegate;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unchecked")
public final class ReflectionUtil
{
	private ReflectionUtil() {}

	private static final Map<Integer, Field> FIELDS = new HashMap<>();

	public static PotionType getMixPredicateInput(Object mixPredicate) {
		Object val = getFieldValue(mixPredicate, "input", "field_185198_a", 0);
		if( val instanceof IRegistryDelegate ) {
			return ((IRegistryDelegate<PotionType>) val).get();
		} else { // fallback for older Forge versions
			return (PotionType) val;
		}
	}

	public static Ingredient getMixPredicateReagent(Object mixPredicate) {
		return getFieldValue(mixPredicate, "reagent", "field_185199_b", 1);
	}

	public static PotionType getMixPredicateOutput(Object mixPredicate) {
		Object val = getFieldValue(mixPredicate, "output", "field_185200_c", 2);
		if( val instanceof IRegistryDelegate ) {
			return ((IRegistryDelegate<PotionType>) val).get();
		} else { // fallback for older Forge versions
			return (PotionType) val;
		}
	}

	private static <T> T getFieldValue(Object instance, String mcpName, String srgName, int index) {
		Field f = FIELDS.get(index);
		if(f == null) {
			f = ReflectionHelper.findField(instance.getClass(), srgName, mcpName);
			FIELDS.put(index, f);
		}

		try {
			return (T) f.get(instance);
		} catch( IllegalAccessException e ) {
			return null;
		}
	}
}
