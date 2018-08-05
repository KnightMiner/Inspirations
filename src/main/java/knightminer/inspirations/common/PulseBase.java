package knightminer.inspirations.common;

import java.util.Locale;

import knightminer.inspirations.Inspirations;
import knightminer.inspirations.building.InspirationsBuilding;
import knightminer.inspirations.library.Util;
import knightminer.inspirations.recipes.InspirationsRecipes;
import knightminer.inspirations.tools.InspirationsTools;
import knightminer.inspirations.tweaks.InspirationsTweaks;
import knightminer.inspirations.utility.InspirationsUtility;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDispenser;
import net.minecraft.block.properties.IProperty;
import net.minecraft.dispenser.IBehaviorDispenseItem;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootEntry;
import net.minecraft.world.storage.loot.LootEntryTable;
import net.minecraft.world.storage.loot.LootPool;
import net.minecraft.world.storage.loot.RandomValueRange;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import slimeknights.mantle.block.BlockStairsBase;
import slimeknights.mantle.block.EnumBlock;
import slimeknights.mantle.block.EnumBlockSlab;
import slimeknights.mantle.item.ItemBlockMeta;
import slimeknights.mantle.item.ItemBlockSlab;

public class PulseBase {
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
		return Inspirations.pulseManager.isPulseLoaded(InspirationsRecipes.pulseID);
	}

	public static boolean isToolsLoaded() {
		return Inspirations.pulseManager.isPulseLoaded(InspirationsTools.pulseID);
	}

	/* Normal registration */
	protected static <T extends Block> T registerBlock(IForgeRegistry<Block> registry, T block, String name) {
		if(!name.equals(name.toLowerCase(Locale.US))) {
			throw new IllegalArgumentException(
					String.format("Unlocalized names need to be all lowercase! Block: %s", name));
		}

		String prefixedName = Util.prefix(name);
		block.setUnlocalizedName(prefixedName);

		register(registry, block, name);
		return block;
	}

	/**
	 * Sets the correct unlocalized name and registers the item.
	 */
	protected static <T extends Item> T registerItem(IForgeRegistry<Item> registry, T item, String name) {
		if(!name.equals(name.toLowerCase(Locale.US))) {
			throw new IllegalArgumentException(
					String.format("Unlocalized names need to be all lowercase! Item: %s", name));
		}
		item.setUnlocalizedName(Util.prefix(name));

		register(registry, item, name);
		return item;
	}

	// stairs
	protected static <E extends Enum<E> & EnumBlock.IEnumMeta & IStringSerializable> BlockStairsBase registerBlockStairsFrom(
			IForgeRegistry<Block> registry, EnumBlock<E> block, E value, String name) {
		return registerBlock(registry, new BlockStairsBase(block.getDefaultState().withProperty(block.prop, value)), name);
	}


	/* Itemblocks */
	protected static ItemBlockMeta registerItemBlock(IForgeRegistry<Item> registry, Block block) {
		return registerItemBlock(registry, new ItemBlockMeta(block));
	}

	protected static ItemBlockMeta registerEnumItemBlock(IForgeRegistry<Item> registry, EnumBlock<?> block) {
		return registerItemBlock(registry, block, block.prop);
	}

	protected static <T extends ItemBlockMeta> T registerItemBlock(IForgeRegistry<Item> registry, T itemBlock, IProperty<?> property) {
		registerItemBlock(registry, itemBlock);
		ItemBlockMeta.setMappingProperty(itemBlock.getBlock(), property);
		return itemBlock;
	}

	protected static <T extends ItemBlock> T registerItemBlock(IForgeRegistry<Item> registry, T itemBlock) {
		itemBlock.setUnlocalizedName(itemBlock.getBlock().getUnlocalizedName());
		register(registry, itemBlock, itemBlock.getBlock().getRegistryName());
		return itemBlock;
	}

	protected static ItemBlockMeta registerItemBlock(IForgeRegistry<Item> registry, Block block, IProperty<?> property) {
		return registerItemBlock(registry, new ItemBlockMeta(block), property);
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


	/* Tile Entity & Entity */
	protected static <T extends Entity> EntityEntryBuilder<T> getEntityBuilder(Class<T> clazz, String name, int id) {
		return EntityEntryBuilder.<T>create()
				.entity(clazz)
				.id(Util.getResource(name), id)
				.name(Util.prefix(name));
	}

	protected static void registerTE(Class<? extends TileEntity> teClazz, String name) {
		if(!name.equals(name.toLowerCase(Locale.US))) {
			throw new IllegalArgumentException(
					String.format("Unlocalized names need to be all lowercase! TE: %s", name));
		}

		GameRegistry.registerTileEntity(teClazz, Util.resource(name));
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

	protected static void registerDispenserBehavior(Block block, IBehaviorDispenseItem behavior) {
		if(block != null) {
			registerDispenserBehavior(Item.getItemFromBlock(block), behavior);
		}
	}
	protected static void registerDispenserBehavior(Item item, IBehaviorDispenseItem behavior) {
		if(item != null) {
			BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(item, behavior);
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
