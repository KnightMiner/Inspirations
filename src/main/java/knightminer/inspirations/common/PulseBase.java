package knightminer.inspirations.common;

import java.util.Locale;

import knightminer.inspirations.Inspirations;
import knightminer.inspirations.building.InspirationsBuilding;
import knightminer.inspirations.library.Util;
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
	protected static <T extends IForgeRegistryEntry<T>> T register(IForgeRegistry<T> registry, T thing, String name) {
		return register(registry, thing, Util.getResource(name));
	}

	protected static <T extends IForgeRegistryEntry<T>> T register(IForgeRegistry<T> registry, T thing, ResourceLocation name) {
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
	protected static void registerDispenserBehavior(Item item, IBehaviorDispenseItem behavior) {
		if(item != null) {
			BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(item, behavior);
		}
	}
}
