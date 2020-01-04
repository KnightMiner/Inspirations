package knightminer.inspirations.building;

import knightminer.inspirations.building.block.BookshelfBlock;
import knightminer.inspirations.building.block.ChainBlock;
import knightminer.inspirations.building.block.ClimbablePaneBlock;
import knightminer.inspirations.building.block.EnlightenedBushBlock;
import knightminer.inspirations.building.block.FlowerBlock;
import knightminer.inspirations.building.block.GlassDoorBlock;
import knightminer.inspirations.building.block.GlassTrapdoorBlock;
import knightminer.inspirations.building.block.MulchBlock;
import knightminer.inspirations.building.block.PathBlock;
import knightminer.inspirations.building.block.RopeBlock;
import knightminer.inspirations.building.datagen.BuildingBlockModelProvider;
import knightminer.inspirations.building.datagen.BuildingRecipeProvider;
import knightminer.inspirations.building.inventory.BookshelfContainer;
import knightminer.inspirations.building.item.BookshelfItem;
import knightminer.inspirations.building.item.GlassDoorBlockItem;
import knightminer.inspirations.building.tileentity.BookshelfTileEntity;
import knightminer.inspirations.building.tileentity.EnlightenedBushTileEntity;
import knightminer.inspirations.common.Config;
import knightminer.inspirations.common.PulseBase;
import knightminer.inspirations.common.item.HidableItem;
import knightminer.inspirations.common.item.TextureBlockItem;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.ComposterBlock;
import net.minecraft.block.DoublePlantBlock;
import net.minecraft.block.FlowerPotBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.data.DataGenerator;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.DyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.Items;
import net.minecraft.tags.ItemTags;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;
import net.minecraftforge.registries.IForgeRegistry;
import slimeknights.mantle.pulsar.pulse.Pulse;

import java.util.function.Supplier;

@Pulse(id = InspirationsBuilding.pulseID, description = "A collection of features to improve building")
public class InspirationsBuilding extends PulseBase {
	public static final String pulseID = "InspirationsBuilding";

	public static Object proxy = DistExecutor.callWhenOn(Dist.CLIENT, ()->()->new BuildingClientProxy());

	// blocks
	public static BookshelfBlock shelf_normal;
	public static BookshelfBlock shelf_rainbow;
	public static BookshelfBlock shelf_tomes;
	public static BookshelfBlock shelf_ancient;

	public static RopeBlock rope;
	public static RopeBlock vine;
	public static RopeBlock chain;
	public static Block ironBars;

	public static Block glassDoor;
	public static Block glassTrapdoor;

	public static Block plainMulch;
	public static Block brownMulch;
	public static Block redMulch;
	public static Block blackMulch;
	public static Block blueMulch;

	public static PathBlock path_rock;
	public static PathBlock path_round;
	public static PathBlock path_tile;
	public static PathBlock path_brick;

	public static FlowerBlock flower_cyan;
	public static FlowerBlock flower_syringa;
	public static FlowerBlock flower_paeonia;
	public static FlowerBlock flower_rose;

	public static FlowerPotBlock potted_cyan;
	public static FlowerPotBlock potted_syringa;
	public static FlowerPotBlock potted_paeonia;
	public static FlowerPotBlock potted_rose;

	public static EnlightenedBushBlock whiteEnlightenedBush;
	public static EnlightenedBushBlock redEnlightenedBush;
	public static EnlightenedBushBlock greenEnlightenedBush;
	public static EnlightenedBushBlock blueEnlightenedBush;

	// items
	public static Item glassDoorItem;
	public static Item[] coloredBooks = new Item[16];
	public static Item redstoneBook;

	// Tile Entities
	public static TileEntityType<BookshelfTileEntity> tileBookshelf;
	public static TileEntityType<EnlightenedBushTileEntity> tileEnlightenedBush;

	// Container Types
	public static ContainerType<BookshelfContainer> contBookshelf;

	@SubscribeEvent
	public void registerTE(Register<TileEntityType<?>> event) {
		IForgeRegistry<TileEntityType<?>> r = event.getRegistry();

		tileBookshelf = register(r, TileEntityType.Builder.create(
				BookshelfTileEntity::new, shelf_normal, shelf_tomes, shelf_rainbow, shelf_ancient
		).build(null), "bookshelf");

		tileEnlightenedBush = register(r, TileEntityType.Builder.create(
				EnlightenedBushTileEntity::new,
				whiteEnlightenedBush,
				redEnlightenedBush,
				greenEnlightenedBush,
				blueEnlightenedBush
		).build(null), "enlightened_bush");
	}

	@SubscribeEvent
	public void registerContainers(Register<ContainerType<?>> event) {
		IForgeRegistry<ContainerType<?>> r = event.getRegistry();

		contBookshelf = register(r, new ContainerType<>(new BookshelfContainer.Factory()), "bookshelf");
	}

	@SubscribeEvent
	public void registerBlocks(Register<Block> event) {
		IForgeRegistry<Block> r = event.getRegistry();

		shelf_normal = registerBlock(r, new BookshelfBlock(), "bookshelf");
		shelf_ancient = registerBlock(r, new BookshelfBlock(), "ancient_bookshelf");
		shelf_rainbow = registerBlock(r, new BookshelfBlock(), "rainbow_bookshelf");
		shelf_tomes = registerBlock(r, new BookshelfBlock(), "tomes_bookshelf");

		rope = registerBlock(r, new RopeBlock(Items.STICK, Block.Properties
				.create(Material.CARPET, MaterialColor.OBSIDIAN)
				.sound(SoundType.CLOTH)
				.hardnessAndResistance(0.5F)
		), "rope");
		vine = registerBlock(r, new RopeBlock(Items.BAMBOO, Block.Properties
				.create(Material.CARPET, MaterialColor.FOLIAGE)
				.sound(SoundType.PLANT)
				.hardnessAndResistance(0.5F)
		), "vine");
		chain = registerBlock(r, new ChainBlock(Items.IRON_NUGGET, Block.Properties
				.create(Material.IRON, MaterialColor.STONE)
				.sound(SoundType.METAL)
				.hardnessAndResistance(5.0F)
				.harvestTool(ToolType.PICKAXE)
				.harvestLevel(0)
		), "chain");
		// iron bars override
		if (Config.climbableIronBars.get()) {
			ironBars = register(r, new ClimbablePaneBlock(Block.Properties.create(Material.IRON, MaterialColor.AIR).hardnessAndResistance(5.0F, 6.0F).sound(SoundType.METAL)), new ResourceLocation("iron_bars"));
		}

		glassDoor = registerBlock(r, new GlassDoorBlock(), "glass_door");
		glassTrapdoor = registerBlock(r, new GlassTrapdoorBlock(), "glass_trapdoor");

		plainMulch  = registerBlock(r, new MulchBlock(MaterialColor.LIGHT_GRAY), "plain_mulch");
		brownMulch  = registerBlock(r, new MulchBlock(MaterialColor.DIRT), "brown_mulch");
		redMulch    = registerBlock(r, new MulchBlock(MaterialColor.NETHERRACK), "red_mulch");
		blackMulch  = registerBlock(r, new MulchBlock(MaterialColor.GRAY), "black_mulch");
		blueMulch   = registerBlock(r, new MulchBlock(MaterialColor.BLUE), "blue_mulch");

		path_rock  = registerBlock(r, new PathBlock(PathBlock.SHAPE_ROCK, MaterialColor.STONE), "rock_path");
		path_round = registerBlock(r, new PathBlock(PathBlock.SHAPE_ROUND, MaterialColor.STONE), "round_path");
		path_tile  = registerBlock(r, new PathBlock(PathBlock.SHAPE_TILE, MaterialColor.STONE), "tile_path");
		path_brick = registerBlock(r, new PathBlock(PathBlock.SHAPE_BRICK, MaterialColor.RED), "brick_path");

		flower_cyan = registerBlock(r, new FlowerBlock(null), "cyan_flower");
		flower_syringa = registerBlock(r, new FlowerBlock((DoublePlantBlock) Blocks.LILAC), "syringa");
		flower_paeonia = registerBlock(r, new FlowerBlock((DoublePlantBlock) Blocks.PEONY), "paeonia");
		flower_rose = registerBlock(r, new FlowerBlock((DoublePlantBlock) Blocks.ROSE_BUSH), "rose");

		Supplier<FlowerPotBlock> emptyPot = () -> (FlowerPotBlock) Blocks.FLOWER_POT;
		Block.Properties props = Block.Properties.from(Blocks.FLOWER_POT);
		potted_cyan = registerBlock(r, new FlowerPotBlock(emptyPot, () -> flower_cyan, props), "potted_cyan");
		potted_syringa = registerBlock(r, new FlowerPotBlock(emptyPot, () -> flower_syringa, props), "potted_syringa");
		potted_paeonia = registerBlock(r, new FlowerPotBlock(emptyPot, () -> flower_paeonia, props), "potted_paeonia");
		potted_rose = registerBlock(r, new FlowerPotBlock(emptyPot, () -> flower_rose, props), "potted_rose");

		// Register the flower items with the empty flower pot block.
		FlowerPotBlock flowerPot = (FlowerPotBlock) Blocks.FLOWER_POT;
		flowerPot.addPlant(flower_cyan.getRegistryName(), () -> potted_cyan);
		flowerPot.addPlant(flower_syringa.getRegistryName(), () -> potted_syringa);
		flowerPot.addPlant(flower_paeonia.getRegistryName(), () -> potted_paeonia);
		flowerPot.addPlant(flower_rose.getRegistryName(), () -> potted_rose);

		whiteEnlightenedBush = registerBlock(r, new EnlightenedBushBlock(-1), "white_enlightened_bush");
		redEnlightenedBush = registerBlock(r, new EnlightenedBushBlock(0xBF0000), "red_enlightened_bush");
		greenEnlightenedBush = registerBlock(r, new EnlightenedBushBlock(0x267F00), "green_enlightened_bush");
		blueEnlightenedBush = registerBlock(r, new EnlightenedBushBlock(0x001CBF), "blue_enlightened_bush");
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
		registerBlockItem(r, new BookshelfItem(shelf_normal));
		registerBlockItem(r, new BookshelfItem(shelf_ancient));
		registerBlockItem(r, new BookshelfItem(shelf_rainbow));
		registerBlockItem(r, new BookshelfItem(shelf_tomes));

		registerBlockItem(r, rope, ItemGroup.DECORATIONS);
		registerBlockItem(r, vine, ItemGroup.DECORATIONS);
		registerBlockItem(r, chain, ItemGroup.DECORATIONS);
		registerBlockItem(r, ironBars, ItemGroup.DECORATIONS);

		registerBlockItem(r, plainMulch, ItemGroup.BUILDING_BLOCKS);
		registerBlockItem(r, brownMulch, ItemGroup.BUILDING_BLOCKS);
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

		Item.Properties deco_props = new Item.Properties().group(ItemGroup.DECORATIONS);
		registerBlockItem(r, new TextureBlockItem(whiteEnlightenedBush, deco_props, ItemTags.LEAVES));
		registerBlockItem(r, new TextureBlockItem(redEnlightenedBush, deco_props, ItemTags.LEAVES));
		registerBlockItem(r, new TextureBlockItem(greenEnlightenedBush, deco_props, ItemTags.LEAVES));
		registerBlockItem(r, new TextureBlockItem(blueEnlightenedBush, deco_props, ItemTags.LEAVES));

		glassDoorItem = registerItem(r, new GlassDoorBlockItem(glassDoor, new Item.Properties().group(ItemGroup.REDSTONE)), "glass_door");
		registerBlockItem(r, glassTrapdoor, ItemGroup.REDSTONE);

	}

	@SubscribeEvent
	public void gatherData(GatherDataEvent event) {
		DataGenerator gen = event.getGenerator();
		if (event.includeServer()) {
			gen.addProvider(new BuildingRecipeProvider(gen));
		}
		if (event.includeClient()) {
			gen.addProvider(new BuildingBlockModelProvider(gen, event.getExistingFileHelper()));
		}
	}

	@SubscribeEvent
	public static void loadLoad(LootTableLoadEvent event) {
		// Add the drops for the small flowers.
		flower_paeonia.injectLoot(event);
		flower_rose.injectLoot(event);
		flower_syringa.injectLoot(event);
	}

	@SubscribeEvent
	public void init(FMLCommonSetupEvent event) {
		registerCompostables();

		/*if(Config.enableFlowers.get() && Config.enableCauldronDyeing()) {
			InspirationsRegistry.addCauldronRecipe(new DyeCauldronRecipe(
				new ItemStack(flower_rose),
				DyeColor.CYAN,
				new ItemStack(flower_cyan))
			);
		}*/

		MinecraftForge.EVENT_BUS.register(BuildingEvents.class);
	}

	private void registerCompostables() {
		ComposterBlock.registerCompostable(0.3F, whiteEnlightenedBush);
		ComposterBlock.registerCompostable(0.3F, redEnlightenedBush);
		ComposterBlock.registerCompostable(0.3F, greenEnlightenedBush);
		ComposterBlock.registerCompostable(0.3F, blueEnlightenedBush);
		ComposterBlock.registerCompostable(0.5F, vine);
		ComposterBlock.registerCompostable(0.65F, flower_cyan);
		ComposterBlock.registerCompostable(0.65F, flower_syringa);
		ComposterBlock.registerCompostable(0.65F, flower_paeonia);
		ComposterBlock.registerCompostable(0.65F, flower_rose);
	}
}
