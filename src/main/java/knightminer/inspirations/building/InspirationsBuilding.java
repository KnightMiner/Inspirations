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

	public static CommonProxy proxy = DistExecutor.runForDist(()->()->new BuildingClientProxy(), ()->()-> new CommonProxy());

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
	public void preInit(FMLCommonSetupEvent event) {
		proxy.preInit();
	}

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
				.create(Material.CARPET)
				.sound(SoundType.CLOTH)
				.hardnessAndResistance(0.5F)
		), "rope");
		vine = registerBlock(r, new BlockRope(Items.BAMBOO, Block.Properties
				.create(Material.CARPET)
				.sound(SoundType.PLANT)
				.hardnessAndResistance(0.5F)
		), "vine");
		chain = registerBlock(r, new BlockChain(Items.IRON_NUGGET, Block.Properties
				.create(Material.IRON)
				.sound(SoundType.METAL)
				.hardnessAndResistance(5.0F)
				.harvestTool(ToolType.PICKAXE)
				.harvestLevel(0)
		), "chain");

		glassDoor = registerBlock(r, new BlockGlassDoor(), "glass_door");
		glassTrapdoor = registerBlock(r, new BlockGlassTrapdoor(), "glass_trapdoor");

		plainMulch = registerBlock(r, new BlockMulch(), "plain_mulch");
		brownMulch = registerBlock(r, new BlockMulch(), "brown_mulch");
		yellowMulch = registerBlock(r, new BlockMulch(), "yellow_mulch");
		amberMulch = registerBlock(r, new BlockMulch(), "amber_mulch");
		rubyMulch = registerBlock(r, new BlockMulch(), "ruby_mulch");
		redMulch = registerBlock(r, new BlockMulch(), "red_mulch");
		blackMulch = registerBlock(r, new BlockMulch(), "black_mulch");
		blueMulch = registerBlock(r, new BlockMulch(), "blue_mulch");

		path_rock = registerBlock(r, new BlockPath(BlockPath.SHAPE_ROCK, MaterialColor.STONE), "rock_path");
		path_round = registerBlock(r, new BlockPath(BlockPath.SHAPE_ROUND, MaterialColor.STONE), "round_path");
		path_tile = registerBlock(r, new BlockPath(BlockPath.SHAPE_TILE, MaterialColor.STONE), "tile_path");
		path_brick = registerBlock(r, new BlockPath(BlockPath.SHAPE_BRICK, MaterialColor.RED), "brick_path");


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
		registerItemBlock(r, new ItemBlockTexture(shelf_normal, deco_props));
		registerItemBlock(r, new ItemBlockTexture(shelf_ancient, deco_props));
		registerItemBlock(r, new ItemBlockTexture(shelf_rainbow, deco_props));
		registerItemBlock(r, new ItemBlockTexture(shelf_tomes, deco_props));

		registerItemBlock(r, rope, ItemGroup.DECORATIONS);
		registerItemBlock(r, vine, ItemGroup.DECORATIONS);
		registerItemBlock(r, chain, ItemGroup.DECORATIONS);

		registerItemBlock(r, plainMulch, ItemGroup.BUILDING_BLOCKS);
		registerItemBlock(r, brownMulch, ItemGroup.BUILDING_BLOCKS);
		registerItemBlock(r, yellowMulch, ItemGroup.BUILDING_BLOCKS);
		registerItemBlock(r, amberMulch, ItemGroup.BUILDING_BLOCKS);
		registerItemBlock(r, rubyMulch, ItemGroup.BUILDING_BLOCKS);
		registerItemBlock(r, redMulch, ItemGroup.BUILDING_BLOCKS);
		registerItemBlock(r, blackMulch, ItemGroup.BUILDING_BLOCKS);
		registerItemBlock(r, blueMulch, ItemGroup.BUILDING_BLOCKS);

		registerItemBlock(r, path_rock, ItemGroup.DECORATIONS);
		registerItemBlock(r, path_round, ItemGroup.DECORATIONS);
		registerItemBlock(r, path_tile, ItemGroup.DECORATIONS);
		registerItemBlock(r, path_brick, ItemGroup.DECORATIONS);

		registerItemBlock(r, flower_cyan, ItemGroup.DECORATIONS);
		registerItemBlock(r, flower_syringa, ItemGroup.DECORATIONS);
		registerItemBlock(r, flower_paeonia, ItemGroup.DECORATIONS);
		registerItemBlock(r, flower_rose, ItemGroup.DECORATIONS);

//		registerItemBlock(r, new ItemBlockTexture(enlightenedBush, new Item.Properties().group(ItemGroup.DECORATIONS)));

		glassDoorItem = registerItem(r, new ItemGlassDoor(glassDoor, new Item.Properties().group(ItemGroup.REDSTONE)), "glass_door");
		registerItemBlock(r, glassTrapdoor, ItemGroup.REDSTONE);

	}

	@SubscribeEvent
	public void init(InterModEnqueueEvent event) {
		proxy.init();

		if(Config.enableFlowers.get() && Config.enableCauldronDyeing()) {
//			InspirationsRegistry.addCauldronRecipe(new CauldronDyeRecipe(
//				new ItemStack(flower_rose),
//				DyeColor.CYAN,
//				new ItemStack(flower_cyan))
//			);
		}
	}

	@SubscribeEvent
	public void postInit(InterModProcessEvent event) {
		proxy.postInit();

		MinecraftForge.EVENT_BUS.register(BuildingEvents.class);
	}
}
