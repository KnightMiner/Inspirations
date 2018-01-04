package knightminer.inspirations.shared.item;

import java.util.HashSet;
import java.util.Set;

import gnu.trove.map.hash.TIntObjectHashMap;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import slimeknights.mantle.item.ItemMetaDynamic;

public class ItemMaterials extends ItemMetaDynamic {

	protected TIntObjectHashMap<CreativeTabs> creativeTabs;
	protected Set<CreativeTabs> allTabs;
	public ItemMaterials() {
		allTabs = new HashSet<>();
		creativeTabs = new TIntObjectHashMap<>();
	}

	/**
	 * returns this;
	 */
	@Override
	public Item setCreativeTab(CreativeTabs tab) {
		super.setCreativeTab(tab);
		allTabs.add(tab);
		return this;
	}

	/**
	 * Add a valid metadata with a specific creative tab set
	 *
	 * @param meta The metadata value
	 * @param name The name used for registering the itemmodel as well as the unlocalized name of the meta. The unlocalized name will be lowercased and stripped of whitespaces.
	 * @param tab  Creative tab to display the item in. If using {@link #addMeta(int, String)}, it will default to the items tab
	 * @return An itemstack representing the Item-Meta combination.
	 */
	public ItemStack addMeta(int meta, String name, CreativeTabs tab) {
		ItemStack stack = addMeta(meta, name);
		creativeTabs.put(meta, tab);
		allTabs.add(tab);
		return stack;
	}

	@Override
	public CreativeTabs[] getCreativeTabs() {
		return creativeTabs.values(new CreativeTabs[creativeTabs.size()]);
	}

	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> subItems) {
		if(this.isInCreativeTab(tab)) {
			for(int i = 0; i <= availabilityMask.length; i++) {
				if(isValid(i) && this.isInCreativeTab(tab, i)) {
					subItems.add(new ItemStack(this, 1, i));
				}
			}
		}
	}

	private boolean isInCreativeTab(CreativeTabs targetTab, int i) {
		if(targetTab == CreativeTabs.SEARCH) {
			return true;
		}

		CreativeTabs tab = creativeTabs.get(i);
		if(tab != null) {
			return targetTab == tab;
		}
		return targetTab == this.getCreativeTab();
	}
}
