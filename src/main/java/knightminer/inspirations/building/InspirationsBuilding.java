package knightminer.inspirations.building;

import com.google.common.eventbus.Subscribe;

import knightminer.inspirations.building.block.BlockBookshelf;
import knightminer.inspirations.building.block.BlockGlassDoor;
import knightminer.inspirations.building.block.BlockGlassTrapdoor;
import knightminer.inspirations.building.block.BlockMulch;
import knightminer.inspirations.building.block.BlockRope;
import knightminer.inspirations.building.tileentity.TileBookshelf;
import knightminer.inspirations.common.CommonProxy;
import knightminer.inspirations.common.Config;
import knightminer.inspirations.common.PulseBase;
import knightminer.inspirations.common.item.ItemBlockTexture;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemDoor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;
import slimeknights.mantle.item.ItemMetaDynamic;
import slimeknights.mantle.pulsar.pulse.Pulse;

@Pulse(id = InspirationsBuilding.pulseID, description = "A collection of features to improve building")
public class InspirationsBuilding extends PulseBase {
	public static final String pulseID = "InspirationsBuilding";

	@SidedProxy(clientSide = "knightminer.inspirations.building.BuildingClientProxy", serverSide = "knightminer.inspirations.common.CommonProxy")
	public static CommonProxy proxy;

	// blocks
	public static Block bookshelf;
	public static BlockRope rope;
	public static Block glassDoor;
	public static Block glassTrapdoor;
	public static Block mulch;

	// items
	public static Item glassDoorItem;
	public static ItemMetaDynamic books;

	// materials
	public static ItemStack redstoneBook;

	@Subscribe
	public void preInit(FMLPreInitializationEvent event) {
		proxy.preInit();
	}

	@SubscribeEvent
	public void registerBlocks(Register<Block> event) {
		IForgeRegistry<Block> r = event.getRegistry();

		if(Config.enableBookshelf) {
			bookshelf = registerBlock(r, new BlockBookshelf(), "bookshelf");
			registerTE(TileBookshelf.class, "bookshelf");
		}

		if(Config.enableRope) {
			rope = registerBlock(r, new BlockRope(), "rope");
		}

		if(Config.enableGlassDoor) {
			glassDoor = registerBlock(r, new BlockGlassDoor(), "glass_door");
			glassTrapdoor = registerBlock(r, new BlockGlassTrapdoor(), "glass_trapdoor");
		}

		if(Config.enableMulch) {
			mulch = registerBlock(r, new BlockMulch(), "mulch");
		}
	}

	@SubscribeEvent
	public void registerItems(Register<Item> event) {
		IForgeRegistry<Item> r = event.getRegistry();

		books = registerItem(r, new ItemMetaDynamic(), "books");
		books.setCreativeTab(CreativeTabs.MATERIALS);
		if(Config.enableColoredBooks) {
			for(EnumDyeColor color : EnumDyeColor.values()) {
				books.addMeta(color.getMetadata(), color.getName());
			}
		}
		if(Config.enableRedstoneBook && isUtilityLoaded()) {
			redstoneBook = books.addMeta(16, "redstone");
		}

		// itemblocks
		if(bookshelf != null) {
			registerItemBlock(r, new ItemBlockTexture(bookshelf), BlockBookshelf.TYPE);
		}
		if(rope != null) {
			registerEnumItemBlock(r, rope);
		}
		if(mulch != null) {
			registerItemBlock(r, mulch, BlockMulch.COLOR);
		}
		if(Config.enableGlassDoor) {
			glassDoorItem = registerItem(r, new ItemDoor(glassDoor), "glass_door");
			glassDoorItem.setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
			registerItemBlock(r, glassTrapdoor);
		}
	}

	@Subscribe
	public void init(FMLInitializationEvent event) {
		proxy.init();
	}

	@Subscribe
	public void postInit(FMLPostInitializationEvent event) {
		proxy.postInit();
	}
}
