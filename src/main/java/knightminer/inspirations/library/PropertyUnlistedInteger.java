package knightminer.inspirations.library;

import net.minecraftforge.common.property.IUnlistedProperty;

/**
 * Same as PropertyInteger, but does not cache nor validate results
 */
public class PropertyUnlistedInteger implements IUnlistedProperty<Integer> {

	private final String name;
	public PropertyUnlistedInteger(String name) {
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean isValid(Integer value) {
		return value >= 0;
	}

	@Override
	public Class<Integer> getType() {
		return Integer.class;
	}

	@Override
	public String valueToString(Integer value) {
		return value.toString();
	}
}
