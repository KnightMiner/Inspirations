package knightminer.inspirations.common;

import knightminer.inspirations.Inspirations;
import knightminer.inspirations.building.InspirationsBuilding;
import knightminer.inspirations.common.item.HidableBlockItem;
import knightminer.inspirations.library.Util;
import knightminer.inspirations.tools.InspirationsTools;
import knightminer.inspirations.tweaks.InspirationsTweaks;
import knightminer.inspirations.utility.InspirationsUtility;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.ConstantRange;
import net.minecraft.world.storage.loot.LootPool;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraft.world.storage.loot.TableLootEntry;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.registries.IForgeRegistry;
import slimeknights.mantle.common.IRegisterUtil;

import java.util.Locale;

public class PulseBase implements IRegisterUtil {
	@Override
	public String getModId() {
		return Inspirations.modID;
	}

	/* Loaded */
	public static boolean isBuildingLoaded() {
		return Inspirations.pulseManager.isPulseLoaded(InspirationsBuilding.pulseID);
	}

	public static boolean isUtilityLoaded() {
		return Inspirations.pulseManager.isPulseLoaded(InspirationsUtility.pulseID);
	}

	public static boolean isTweaksLoaded() {
		return Inspirations.pulseManager.isPulseLoaded(InspirationsTweaks.pulseID);
	}

	public static boolean isRecipesLoaded() {
		return false;
//		return Inspirations.pulseManager.isPulseLoaded(InspirationsRecipes.pulseID);
	}

	public static boolean isToolsLoaded() {
		return Inspirations.pulseManager.isPulseLoaded(InspirationsTools.pulseID);
	}

	/* Normal registration */
	protected <T extends Block> T registerBlock(IForgeRegistry<Block> registry, T block, String name) {
		if (!name.equals(name.toLowerCase(Locale.US))) {
			throw new IllegalArgumentException(
					String.format("Unlocalized names need to be all lowercase! Block: %s", name));
		}
		register(registry, block, name);
		return block;
	}

	/**
	 * Sets the correct registry name and registers the item.
	 */
	protected <T extends Item> T registerItem(IForgeRegistry<Item> registry, T item, String name) {
		if (!name.equals(name.toLowerCase(Locale.US))) {
			throw new IllegalArgumentException(
					String.format("Unlocalized names need to be all lowercase! Item: %s", name));
		}
		register(registry, item, name);
		return item;
	}


	/**
	 * Override BlockItem registration to use the Hidable version.
	 * @param registry The item registry to register in.
	 * @param block The block to register an item version for.
	 * @param group The ItemGroup to assign the item to.
	 * @return The created BlockItem.
	 */
	@Override
	public BlockItem registerBlockItem(IForgeRegistry<Item> registry, Block block, ItemGroup group) {
		if (block == null) {
			return null;
		}
		return registerBlockItem(registry, new HidableBlockItem(block, (new Item.Properties()).group(group)));
	}

	/* Base methods */

	static protected <X extends Entity> EntityType<X> buildEntity(EntityType.Builder<X> builder, String id) {
		EntityType<X> type = builder.build(id);
		type.setRegistryName(Util.getResource(id));
		return type;
	}


	/**
	 * Adds entries from a loot table in the inspirations directory to a vanilla loot table
	 *
	 * @param event LootTableLoadEvent
	 * @param name  Name of vanilla table and the inspirations table
	 */
	protected static void addToVanillaLoot(LootTableLoadEvent event, String name) {
		if (!event.getName().getNamespace().equals("minecraft") || !event.getName().getPath().equals(name)) {
			return;
		}
		ResourceLocation base = new ResourceLocation(name);
		LootTable table = event.getTable();
		if (table != LootTable.EMPTY_LOOT_TABLE) {
			ResourceLocation location = Util.getResource(base.getPath());
			table.addPool(new LootPool.Builder()
					.name(location.toString())
					.rolls(ConstantRange.of(1))
					.addEntry(TableLootEntry.builder(location))
					.build()
			);
		}
	}
}
