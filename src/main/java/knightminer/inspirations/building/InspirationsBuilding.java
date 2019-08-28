package knightminer.inspirations.building;

import knightminer.inspirations.building.block.BlockBookshelf;
import knightminer.inspirations.building.block.BlockChain;
import knightminer.inspirations.building.block.BlockFlower;
import knightminer.inspirations.building.block.BlockGlassDoor;
import knightminer.inspirations.building.block.BlockGlassTrapdoor;
import knightminer.inspirations.building.block.BlockMulch;
import knightminer.inspirations.building.block.BlockPath;
import knightminer.inspirations.building.block.BlockRope;
import knightminer.inspirations.building.inventory.ContainerBookshelf;
import knightminer.inspirations.building.item.ItemGlassDoor;
import knightminer.inspirations.building.tileentity.TileBookshelf;
import knightminer.inspirations.building.tileentity.TileEnlightenedBush;
import knightminer.inspirations.common.CommonProxy;
import knightminer.inspirations.common.Config;
import knightminer.inspirations.common.PulseBase;
import knightminer.inspirations.common.item.HidableItem;
import knightminer.inspirations.common.item.ItemBlockTexture;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.DoublePlantBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.DyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.Items;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.registries.IForgeRegistry;
import slimeknights.mantle.pulsar.pulse.Pulse;

@Pulse(id = InspirationsBuilding.pulseID, description = "A collection of features to improve building")
public class InspirationsBuilding extends PulseBase {
	public static final String pulseID = "InspirationsBuilding";

	@SuppressWarnings("Convert2MethodRef")
	public static Object proxy = DistExecutor.callWhenOn(Dist.CLIENT, ()->()->new BuildingClientProxy());

	// blocks
	public static BlockBookshelf shelf_normal;
	public static BlockBookshelf shelf_rainbow;
	public static BlockBookshelf shelf_tomes;
	public static BlockBookshelf shelf_ancient;

	public static BlockRope rope;
	public static BlockRope vine;
	public static BlockRope chain;

	public static Block glassDoor;
	public static Block glassTrapdoor;

	public static Block plainMulch;
	public static Block brownMulch;
	public static Block yellowMulch;
	public static Block amberMulch;
	public static Block rubyMulch;
	public static Block redMulch;
	public static Block blackMulch;
	public static Block blueMulch;

	public static BlockPath path_rock;
	public static BlockPath path_round;
	public static BlockPath path_tile;
	public static BlockPath path_brick;

	public static Block flower_cyan;
	public static Block flower_syringa;
	public static Block flower_paeonia;
	public static Block flower_rose;

	public static Block enlightenedBush;

	// items
	public static Item glassDoorItem;
	public static Item[] coloredBooks = new Item[16];
	public static Item redstoneBook;

	// Tile Entities
	public static TileEntityType<TileBookshelf> tileBookshelf;
	public static TileEntityType<TileEnlightenedBush> tileEnlightenedBush;

	// Container Types
	public static ContainerType<ContainerBookshelf> contBookshelf;

	@SubscribeEvent
	public void registerTE(Register<TileEntityType<?>> event) {
		IForgeRegistry<TileEntityType<?>> r = event.getRegistry();

		tileBookshelf = register(r, TileEntityType.Builder.create(
				TileBookshelf::new, shelf_normal, shelf_tomes, shelf_rainbow, shelf_ancient
		).build(null), "bookshelf");
	}

	@SubscribeEvent
	public void registerContainers(Register<ContainerType<?>> event) {
		IForgeRegistry<ContainerType<?>> r = event.getRegistry();

		contBookshelf = register(r, new ContainerType<>(new ContainerBookshelf.Factory()), "bookshelf");
	}

	@SubscribeEvent
	public void registerBlocks(Register<Block> event) {
		IForgeRegistry<Block> r = event.getRegistry();

		shelf_normal = registerBlock(r, new BlockBookshelf(), "bookshelf");
		shelf_ancient = registerBlock(r, new BlockBookshelf(), "ancient_bookshelf");
		shelf_rainbow = registerBlock(r, new BlockBookshelf(), "rainbow_bookshelf");
		shelf_tomes = registerBlock(r, new BlockBookshelf(), "tomes_bookshelf");

		rope = registerBlock(r, new BlockRope(Items.STICK, Block.Properties
				.create(Material.CARPET, MaterialColor.OBSIDIAN)
				.sound(SoundType.CLOTH)
				.hardnessAndResistance(0.5F)
		), "rope");
		vine = registerBlock(r, new BlockRope(Items.BAMBOO, Block.Properties
				.create(Material.CARPET, MaterialColor.FOLIAGE)
				.sound(SoundType.PLANT)
				.hardnessAndResistance(0.5F)
		), "vine");
		chain = registerBlock(r, new BlockChain(Items.IRON_NUGGET, Block.Properties
				.create(Material.IRON, MaterialColor.STONE)
				.sound(SoundType.METAL)
				.hardnessAndResistance(5.0F)
				.harvestTool(ToolType.PICKAXE)
				.harvestLevel(0)
		), "chain");

		glassDoor = registerBlock(r, new BlockGlassDoor(), "glass_door");
		glassTrapdoor = registerBlock(r, new BlockGlassTrapdoor(), "glass_trapdoor");

		plainMulch  = registerBlock(r, new BlockMulch(MaterialColor.LIGHT_GRAY), "plain_mulch");
		brownMulch  = registerBlock(r, new BlockMulch(MaterialColor.DIRT),       "brown_mulch");
		yellowMulch = registerBlock(r, new BlockMulch(MaterialColor.YELLOW),     "yellow_mulch");
		amberMulch  = registerBlock(r, new BlockMulch(MaterialColor.OBSIDIAN),   "amber_mulch");
		rubyMulch   = registerBlock(r, new BlockMulch(MaterialColor.RED),        "ruby_mulch");
		redMulch    = registerBlock(r, new BlockMulch(MaterialColor.NETHERRACK), "red_mulch");
		blackMulch  = registerBlock(r, new BlockMulch(MaterialColor.GRAY),       "black_mulch");
		blueMulch   = registerBlock(r, new BlockMulch(MaterialColor.BLUE),       "blue_mulch");

		path_rock  = registerBlock(r, new BlockPath(BlockPath.SHAPE_ROCK,  MaterialColor.STONE), "rock_path");
		path_round = registerBlock(r, new BlockPath(BlockPath.SHAPE_ROUND, MaterialColor.STONE), "round_path");
		path_tile  = registerBlock(r, new BlockPath(BlockPath.SHAPE_TILE,  MaterialColor.STONE), "tile_path");
		path_brick = registerBlock(r, new BlockPath(BlockPath.SHAPE_BRICK, MaterialColor.RED),  "brick_path");


		flower_cyan = registerBlock(r, new BlockFlower(null), "cyan_flower");
		flower_syringa = registerBlock(r, new BlockFlower((DoublePlantBlock) Blocks.LILAC), "syringa");
		flower_paeonia = registerBlock(r, new BlockFlower((DoublePlantBlock) Blocks.PEONY), "paeonia");
		flower_rose = registerBlock(r, new BlockFlower((DoublePlantBlock) Blocks.ROSE_BUSH), "rose");


//		enlightenedBush = registerBlock(r, new BlockEnlightenedBush(), "enlightened_bush");
//		registerTE(TileEnlightenedBush.class, "enlightened_bush");
	}

	@SubscribeEvent
	public void registerItems(Register<Item> event) {
		IForgeRegistry<Item> r = event.getRegistry();

		for (DyeColor color : DyeColor.values()) {
			coloredBooks[color.getId()] = registerItem(r, new HidableItem(
					new Item.Properties().group(ItemGroup.MATERIALS),
					Config::enableColoredBooks
			), color.getName() + "_book");
		}

		redstoneBook = registerItem(r, new HidableItem(
				new Item.Properties().group(ItemGroup.MATERIALS),
				Config::enableRedstoneBook
		), "redstone_book");

		// itemblocks
		Item.Properties deco_props = new Item.Properties().group(ItemGroup.DECORATIONS);
		registerBlockItem(r, new ItemBlockTexture(shelf_normal, deco_props));
		registerBlockItem(r, new ItemBlockTexture(shelf_ancient, deco_props));
		registerBlockItem(r, new ItemBlockTexture(shelf_rainbow, deco_props));
		registerBlockItem(r, new ItemBlockTexture(shelf_tomes, deco_props));

		registerBlockItem(r, rope, ItemGroup.DECORATIONS);
		registerBlockItem(r, vine, ItemGroup.DECORATIONS);
		registerBlockItem(r, chain, ItemGroup.DECORATIONS);

		registerBlockItem(r, plainMulch, ItemGroup.BUILDING_BLOCKS);
		registerBlockItem(r, brownMulch, ItemGroup.BUILDING_BLOCKS);
		registerBlockItem(r, yellowMulch, ItemGroup.BUILDING_BLOCKS);
		registerBlockItem(r, amberMulch, ItemGroup.BUILDING_BLOCKS);
		registerBlockItem(r, rubyMulch, ItemGroup.BUILDING_BLOCKS);
		registerBlockItem(r, redMulch, ItemGroup.BUILDING_BLOCKS);
		registerBlockItem(r, blackMulch, ItemGroup.BUILDING_BLOCKS);
		registerBlockItem(r, blueMulch, ItemGroup.BUILDING_BLOCKS);

		registerBlockItem(r, path_rock, ItemGroup.DECORATIONS);
		registerBlockItem(r, path_round, ItemGroup.DECORATIONS);
		registerBlockItem(r, path_tile, ItemGroup.DECORATIONS);
		registerBlockItem(r, path_brick, ItemGroup.DECORATIONS);

		registerBlockItem(r, flower_cyan, ItemGroup.DECORATIONS);
		registerBlockItem(r, flower_syringa, ItemGroup.DECORATIONS);
		registerBlockItem(r, flower_paeonia, ItemGroup.DECORATIONS);
		registerBlockItem(r, flower_rose, ItemGroup.DECORATIONS);

//		registerBlockItem(r, new ItemBlockTexture(enlightenedBush, new Item.Properties().group(ItemGroup.DECORATIONS)));

		glassDoorItem = registerItem(r, new ItemGlassDoor(glassDoor, new Item.Properties().group(ItemGroup.REDSTONE)), "glass_door");
		registerBlockItem(r, glassTrapdoor, ItemGroup.REDSTONE);

	}

	@SubscribeEvent
	public void init(FMLCommonSetupEvent event) {
		if(Config.enableFlowers.get() && Config.enableCauldronDyeing()) {
//			InspirationsRegistry.addCauldronRecipe(new CauldronDyeRecipe(
//				new ItemStack(flower_rose),
//				DyeColor.CYAN,
//				new ItemStack(flower_cyan))
//			);
		}

		MinecraftForge.EVENT_BUS.register(BuildingEvents.class);
	}
}
