package knightminer.inspirations.building;

import com.google.common.eventbus.Subscribe;

import knightminer.inspirations.building.block.BlockBookshelf;
import knightminer.inspirations.building.block.BlockRope;
import knightminer.inspirations.building.block.BlockTorchLever;
import knightminer.inspirations.building.tileentity.TileBookshelf;
import knightminer.inspirations.common.CommonProxy;
import knightminer.inspirations.common.PulseBase;
import knightminer.inspirations.common.item.ItemBlockTexture;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
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

@Pulse(id = InspirationsBuilding.pulseID, description = "Adds features related to redstone")
public class InspirationsBuilding extends PulseBase {
	public static final String pulseID = "InspirationsBuilding";

	@SidedProxy(clientSide = "knightminer.inspirations.building.BuildingClientProxy", serverSide = "knightminer.inspirations.common.CommonProxy")
	public static CommonProxy proxy;

	// blocks
	public static Block bookshelf;
	public static BlockRope rope;

	// items
	public static ItemMetaDynamic books;

	// materials
	public static ItemStack redstoneBook;

	// blocks
	public static Block torchLever;

	@Subscribe
	public void preInit(FMLPreInitializationEvent event) {
		proxy.preInit();
	}

	@SubscribeEvent
	public void registerBlocks(Register<Block> event) {
		IForgeRegistry<Block> r = event.getRegistry();

		bookshelf = registerBlock(r, new BlockBookshelf(), "bookshelf");
		rope = registerBlock(r, new BlockRope(), "rope");
		torchLever = registerBlock(r, new BlockTorchLever(), "torch_lever");

		registerTE(TileBookshelf.class, "bookshelf");
	}

	@SubscribeEvent
	public void registerItems(Register<Item> event) {
		IForgeRegistry<Item> r = event.getRegistry();

		books = registerItem(r, new ItemMetaDynamic(), "books");
		books.setCreativeTab(CreativeTabs.MATERIALS);
		for(EnumDyeColor color : EnumDyeColor.values()) {
			books.addMeta(color.getMetadata(), color.getName());
		}
		redstoneBook = books.addMeta(16, "redstone");

		// itemblocks
		registerItemBlock(r, torchLever);
		registerItemBlock(r, new ItemBlockTexture(bookshelf), BlockBookshelf.TYPE);
		registerEnumItemBlock(r, rope);
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
