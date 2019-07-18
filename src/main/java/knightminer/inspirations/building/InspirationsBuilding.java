package knightminer.inspirations.building;

import com.google.common.eventbus.Subscribe;

import knightminer.inspirations.building.block.BlockBookshelf;
import knightminer.inspirations.building.block.BlockEnlightenedBush;
import knightminer.inspirations.building.block.BlockFlower;
import knightminer.inspirations.building.block.BlockGlassDoor;
import knightminer.inspirations.building.block.BlockGlassTrapdoor;
import knightminer.inspirations.building.block.BlockMulch;
import knightminer.inspirations.building.block.BlockPath;
import knightminer.inspirations.building.block.BlockRope;
import knightminer.inspirations.building.block.BlockFlower.FlowerType;
import knightminer.inspirations.building.tileentity.TileBookshelf;
import knightminer.inspirations.building.tileentity.TileEnlightenedBush;
import knightminer.inspirations.common.CommonProxy;
import knightminer.inspirations.common.Config;
import knightminer.inspirations.common.PulseBase;
import knightminer.inspirations.common.item.ItemBlockTexture;
import knightminer.inspirations.library.InspirationsRegistry;
import knightminer.inspirations.library.recipe.cauldron.CauldronDyeRecipe;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.DoublePlantBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.DyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemDoor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;
import slimeknights.mantle.item.ItemMetaDynamic;
import slimeknights.mantle.pulsar.pulse.Pulse;

import java.util.HashMap;
import java.util.Map;

@Pulse(id = InspirationsBuilding.pulseID, description = "A collection of features to improve building")
public class InspirationsBuilding extends PulseBase {
	public static final String pulseID = "InspirationsBuilding";

	@SidedProxy(clientSide = "knightminer.inspirations.building.BuildingClientProxy", serverSide = "knightminer.inspirations.common.CommonProxy")
	public static CommonProxy proxy;

	// blocks
	public static Block bookshelf;

	public static BlockRope rope;
	public static BlockRope vine;
	public static BlockRope chain;

	public static Block glassDoor;
	public static Block glassTrapdoor;
	public static Map<BlockMulch.MulchColor, Block> mulch=new HashMap<>();
	public static BlockPath path;

	public static Block flower_cyan;
	public static Block flower_lilac;
	public static Block flower_peony;
	public static Block flower_rose;

	public static BlockEnlightenedBush enlightenedBush;

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
			rope = registerBlock(r, new BlockRope(
				Block.Properties.create(Material.CARPET)
					.sound(SoundType.CLOTH)
					.hardnessAndResistance(0.5F)
			), "rope");
			vine = registerBlock(r, new BlockRope(
				Block.Properties.create(Material.CARPET)
					.sound(SoundType.PLANT)
					.hardnessAndResistance(0.5F)
			), "vine");
			chain = registerBlock(r, new BlockRope(
					Block.Properties.create(Material.IRON)
					.sound(SoundType.METAL)
					.hardnessAndResistance(5.0F)
					.harvestTool(ToolType.PICKAXE)
					.harvestLevel(0)
			), "chain");
		}

		if(Config.enableGlassDoor) {
			glassDoor = registerBlock(r, new BlockGlassDoor(), "glass_door");
			glassTrapdoor = registerBlock(r, new BlockGlassTrapdoor(), "glass_trapdoor");
		}

		if(Config.enableMulch) {
			for (BlockMulch.MulchColor mulch_color: BlockMulch.MulchColor.values()) {
				mulch.put(mulch_color,
					registerBlock(r, new BlockMulch(mulch_color),
						mulch_color.getName() + "_mulch"));
			}
		}

		if(Config.enablePath) {
			path = registerBlock(r, new BlockPath(), "path");
		}

		if(Config.enableFlowers) {
			flower_cyan = registerBlock(r, new BlockFlower(null), "cyan_flower");
			flower_lilac = registerBlock(r, new BlockFlower((DoublePlantBlock) Blocks.LILAC), "lilac");
			flower_peony = registerBlock(r, new BlockFlower((DoublePlantBlock) Blocks.PEONY), "peony");
			flower_rose = registerBlock(r, new BlockFlower((DoublePlantBlock) Blocks.ROSE_BUSH), "rose");
		}

		if(Config.enableEnlightenedBush) {
			enlightenedBush = registerBlock(r, new BlockEnlightenedBush(), "enlightened_bush");
			registerTE(TileEnlightenedBush.class, "enlightened_bush");
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

		if (rope != null) {
			registerItemBlock(r, rope, ItemGroup.DECORATIONS);
			registerItemBlock(r, vine, ItemGroup.DECORATIONS);
			registerItemBlock(r, chain, ItemGroup.DECORATIONS);
		}
		for (Block mulchBlock: mulch.values()) {
			registerItemBlock(r, mulchBlock, ItemGroup.BUILDING_BLOCKS);
		}
		if(path != null) {
			registerEnumItemBlock(r, path);
		}

		if (flower_cyan != null) {
			registerItemBlock(r, flower_cyan, ItemGroup.DECORATIONS);
			registerItemBlock(r, flower_lilac, ItemGroup.DECORATIONS);
			registerItemBlock(r, flower_peony, ItemGroup.DECORATIONS);
			registerItemBlock(r, flower_rose, ItemGroup.DECORATIONS);
		}

		if(enlightenedBush != null) {
			registerItemBlock(r, new ItemBlockTexture(enlightenedBush), BlockEnlightenedBush.LIGHTS);
		}

		if(Config.enableGlassDoor) {
			glassDoorItem = registerItem(r, new ItemDoor(glassDoor) {
				@Override
				public int getItemBurnTime(ItemStack stack) {
					return 0;
				}
			}, "glass_door");
			registerItemBlock(r, glassTrapdoor);
		}
	}

	@Subscribe
	public void init(FMLInitializationEvent event) {
		proxy.init();

		if(Config.enableFlowers && Config.enableCauldronDyeing) {
			InspirationsRegistry.addCauldronRecipe(new CauldronDyeRecipe(
					new ItemStack(flower, 1, FlowerType.ROSE.getMeta()),
					DyeColor.CYAN,
					new ItemStack(flower, 1, FlowerType.CYAN.getMeta())));
		}
	}

	@Subscribe
	public void postInit(FMLPostInitializationEvent event) {
		proxy.postInit();

		MinecraftForge.EVENT_BUS.register(BuildingEvents.class);
	}
}
