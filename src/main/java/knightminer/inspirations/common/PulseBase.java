package knightminer.inspirations.common;

import java.util.Locale;

import knightminer.inspirations.Inspirations;
import knightminer.inspirations.common.item.HidableBlockItem;
import knightminer.inspirations.library.Util;
import knightminer.inspirations.building.InspirationsBuilding;
//import knightminer.inspirations.recipes.InspirationsRecipes;
import knightminer.inspirations.tools.InspirationsTools;
import knightminer.inspirations.tweaks.InspirationsTweaks;
import knightminer.inspirations.utility.InspirationsUtility;
import net.minecraft.block.Block;
import net.minecraft.block.DispenserBlock;
import net.minecraft.dispenser.IDispenseItemBehavior;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import slimeknights.mantle.common.IRegisterUtil;

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
	public BlockItem registerBlockItem(IForgeRegistry<Item> registry, Block block, ItemGroup group) {
		return registerBlockItem(registry, new HidableBlockItem(block, (new Item.Properties()).group(group)));
	}

	/* Base methods */

	static protected <X extends Entity> EntityType<X> buildEntity(EntityType.Builder<X> builder, String id) {
		EntityType<X> type = builder.build(id);
		type.setRegistryName(Util.getResource(id));
		return type;
	}

	/* Other */
	protected Fluid registerColoredFluid(String name, int color) {
		Fluid fluid = registerFluid(new Fluid(name, Util.getResource("blocks/fluid_colorless"), Util.getResource("blocks/fluid_colorless_flow"), color));
//		FluidRegistry.addBucketForFluid(fluid);
		return fluid;
	}

	protected <T extends Fluid> T registerFluid(T fluid) {
		fluid.setUnlocalizedName(Util.prefix(fluid.getName()));
//		FluidRegistry.registerFluid(fluid);

		return fluid;
	}

	protected void registerDispenserBehavior(Block block, IDispenseItemBehavior behavior) {
		if (block != null) {
			registerDispenserBehavior(Item.getItemFromBlock(block), behavior);
		}
	}

	protected void registerDispenserBehavior(Item item, IDispenseItemBehavior behavior) {
		if (item != null) {
			DispenserBlock.registerDispenseBehavior(item, behavior);
		}
	}

	/**
	 * Adds entries from a loot table in the inspirations directory to a vanilla loot table
	 *
	 * @param event LootTableLoadEvent
	 * @param name  Name of vanilla table and the inspirations table
	 */
	protected static void addToVanillaLoot(LootTableLoadEvent event, String name) {
//		ResourceLocation extra = Util.getResource(name);
//		event.getTable().addPool(new LootPool(
//				new LootEntry[]{new LootEntryTable(extra, 1, 0, new LootCondition[0], Inspirations.modID)},
//				new LootCondition[0],
//				new RandomValueRange(1.0f),
//				new RandomValueRange(0.0F),
//				Inspirations.modID
//				));
	}
}
