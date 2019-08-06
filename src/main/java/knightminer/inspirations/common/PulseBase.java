package knightminer.inspirations.common;

import java.util.Locale;

import knightminer.inspirations.Inspirations;
import knightminer.inspirations.common.item.HidableBlockItem;
import knightminer.inspirations.library.Util;
//import knightminer.inspirations.building.InspirationsBuilding;
//import knightminer.inspirations.recipes.InspirationsRecipes;
//import knightminer.inspirations.tools.InspirationsTools;
//import knightminer.inspirations.tweaks.InspirationsTweaks;
//import knightminer.inspirations.utility.InspirationsUtility;
import net.minecraft.block.Block;
import net.minecraft.block.DispenserBlock;
import net.minecraft.dispenser.IDispenseItemBehavior;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootEntry;
import net.minecraft.world.storage.loot.LootPool;
import net.minecraft.world.storage.loot.RandomValueRange;
import net.minecraft.world.storage.loot.StandaloneLootEntry;
import net.minecraft.world.storage.loot.conditions.ILootCondition;
import net.minecraft.world.storage.loot.functions.ILootFunction;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import slimeknights.mantle.block.StairsBaseBlock;

public class PulseBase {
	/* Loaded */
	public static boolean isBuildingLoaded() {
		return true;
//		return Inspirations.pulseManager.isPulseLoaded(InspirationsBuilding.pulseID);
	}

	public static boolean isUtilityLoaded() {
		return true;
//		return Inspirations.pulseManager.isPulseLoaded(InspirationsUtility.pulseID);
	}

	public static boolean isTweaksLoaded() {
		return true;
//		return Inspirations.pulseManager.isPulseLoaded(InspirationsTweaks.pulseID);
	}

	public static boolean isRecipesLoaded() {
		return true;
//		return Inspirations.pulseManager.isPulseLoaded(InspirationsRecipes.pulseID);
	}

	public static boolean isToolsLoaded() {
		return true;
//		return Inspirations.pulseManager.isPulseLoaded(InspirationsTools.pulseID);
	}

	/* Normal registration */
	protected static <T extends Block> T registerBlock(IForgeRegistry<Block> registry, T block, String name) {
		if(!name.equals(name.toLowerCase(Locale.US))) {
			throw new IllegalArgumentException(
					String.format("Unlocalized names need to be all lowercase! Block: %s", name));
		}
		register(registry, block, name);
		return block;
	}

	/**
	 * Sets the correct registry name and registers the item.
	 */
	protected static <T extends Item> T registerItem(IForgeRegistry<Item> registry, T item, String name) {
		if(!name.equals(name.toLowerCase(Locale.US))) {
			throw new IllegalArgumentException(
					String.format("Unlocalized names need to be all lowercase! Item: %s", name));
		}
		item.setRegistryName(Util.resource(name));

		register(registry, item, name);
		return item;
	}

	// stairs
	protected static <E extends Enum<E> & EnumBlock.IEnumMeta & IStringSerializable> BlockStairsBase registerBlockStairsFrom(
			IForgeRegistry<Block> registry, EnumBlock<E> block, E value, String name) {
		return registerBlock(registry, new BlockStairsBase(block.getDefaultState().withProperty(block.prop, value)), name);
	}


	/* Itemblocks */

	protected static ItemBlockMeta registerEnumItemBlock(IForgeRegistry<Item> registry, EnumBlock<?> block) {
		return registerItemBlock(registry, block, block.prop);
	}

	protected static <T extends BlockItem> T registerItemBlock(IForgeRegistry<Item> registry, T itemBlock) {
		itemBlock.setRegistryName(itemBlock.getBlock().getRegistryName());
		register(registry, itemBlock, itemBlock.getBlock().getRegistryName());
		return itemBlock;
	}

	protected static BlockItem registerItemBlock(IForgeRegistry<Item> registry, Block block, ItemGroup group) {
		return registerItemBlock(registry, new BlockItem(block, (new Item.Properties()).group(group)));
	}

	protected static <T extends Enum<T> &EnumBlock.IEnumMeta & IStringSerializable> ItemBlockSlab<T> registerEnumItemBlockSlab(IForgeRegistry<Item> registry, EnumBlockSlab<T> block) {
		return registerItemBlock(registry, new ItemBlockSlab<T>(block), block.prop);
	}

	/* Base methods */
	protected static <C extends T, T extends IForgeRegistryEntry<T>> C register(IForgeRegistry<T> registry, C thing, String name) {
		return register(registry, thing, Util.getResource(name));
	}

	protected static <C extends T, T extends IForgeRegistryEntry<T>> C register(IForgeRegistry<T> registry, C thing, ResourceLocation name) {
		thing.setRegistryName(name);
		registry.register(thing);
		return thing;
	}

	protected static <X extends Entity> EntityType<X> buildEntity(EntityType.Builder<X> builder, String id) {
		EntityType<X> type = builder.build(id);
		type.setRegistryName(new ResourceLocation(Inspirations.modID, id));
		return type;
	}

	/* Other */
	protected static Fluid registerColoredFluid(String name, int color) {
		Fluid fluid = registerFluid(new Fluid(name, Util.getResource("blocks/fluid_colorless"), Util.getResource("blocks/fluid_colorless_flow"), color));
		FluidRegistry.addBucketForFluid(fluid);
		return fluid;
	}
	protected static <T extends Fluid> T registerFluid(T fluid) {
		fluid.setUnlocalizedName(Util.prefix(fluid.getName()));
		FluidRegistry.registerFluid(fluid);

		return fluid;
	}

	protected static void registerDispenserBehavior(Block block, IDispenseItemBehavior behavior) {
		if(block != null) {
			registerDispenserBehavior(Item.getItemFromBlock(block), behavior);
		}
	}
	protected static void registerDispenserBehavior(Item item, IDispenseItemBehavior behavior) {
		if(item != null) {
			DispenserBlock.registerDispenseBehavior(item, behavior);
		}
	}

	/**
	 * Adds entries from a loot table in the inspirations directory to a vanilla loot table
	 * @param event  LootTableLoadEvent
	 * @param name   Name of vanilla table and the inspirations table
	 */
	protected static void addToVanillaLoot(LootTableLoadEvent event, String name) {
		ResourceLocation extra = Util.getResource(name);
		event.getTable().addPool(new LootPool(
				new LootEntry[]{new LootEntryTable(extra, 1, 0, new LootCondition[0], Inspirations.modID)},
				new LootCondition[0],
				new RandomValueRange(1.0f),
				new RandomValueRange(0.0F),
				Inspirations.modID
				));
	}
}
