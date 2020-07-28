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
import knightminer.inspirations.building.datagen.BuildingRecipeProvider;
import knightminer.inspirations.building.inventory.BookshelfContainer;
import knightminer.inspirations.building.item.BookshelfItem;
import knightminer.inspirations.building.item.GlassDoorBlockItem;
import knightminer.inspirations.building.tileentity.BookshelfTileEntity;
import knightminer.inspirations.building.tileentity.EnlightenedBushTileEntity;
import knightminer.inspirations.common.Config;
import knightminer.inspirations.common.ModuleBase;
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
import net.minecraftforge.fml.network.IContainerFactory;
import slimeknights.mantle.registration.adapter.BlockRegistryAdapter;
import slimeknights.mantle.registration.adapter.ContainerTypeRegistryAdapter;
import slimeknights.mantle.registration.adapter.ItemRegistryAdapter;
import slimeknights.mantle.registration.adapter.TileEntityTypeRegistryAdapter;

import java.util.function.Supplier;

/**
 * Module containing all the building blocks
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public class InspirationsBuilding extends ModuleBase {
	public static final String pulseID = "InspirationsBuilding";

	@SuppressWarnings("Convert2MethodRef")
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
	void registerTE(Register<TileEntityType<?>> event) {
		TileEntityTypeRegistryAdapter registry = new TileEntityTypeRegistryAdapter(event.getRegistry());

		tileBookshelf = registry.register(BookshelfTileEntity::new, "bookshelf", (blocks) ->
			blocks.add(shelf_normal, shelf_ancient, shelf_rainbow, shelf_tomes));
		tileEnlightenedBush = registry.register(EnlightenedBushTileEntity::new, "enlightened_bush", (blocks) ->
			blocks.add(whiteEnlightenedBush, redEnlightenedBush, greenEnlightenedBush, blueEnlightenedBush));
	}

	@SubscribeEvent
	void registerContainers(Register<ContainerType<?>> event) {
		ContainerTypeRegistryAdapter registry = new ContainerTypeRegistryAdapter(event.getRegistry());
		contBookshelf = registry.register((IContainerFactory<BookshelfContainer>)BookshelfContainer::new, "bookshelf");
	}

	@SuppressWarnings("ConstantConditions")
	@SubscribeEvent
	public void registerBlocks(Register<Block> event) {
		BlockRegistryAdapter registry = new BlockRegistryAdapter(event.getRegistry());

		shelf_normal = registry.register(new BookshelfBlock(), "bookshelf");
		shelf_ancient = registry.register(new BookshelfBlock(), "ancient_bookshelf");
		shelf_rainbow = registry.register(new BookshelfBlock(), "rainbow_bookshelf");
		shelf_tomes = registry.register(new BookshelfBlock(), "tomes_bookshelf");

		rope = registry.register(new RopeBlock(Items.STICK, Block.Properties
				.create(Material.CARPET, MaterialColor.OBSIDIAN)
				.sound(SoundType.CLOTH)
				.hardnessAndResistance(0.5F)
		), "rope");
		vine = registry.register(new RopeBlock(Items.BAMBOO, Block.Properties
				.create(Material.CARPET, MaterialColor.FOLIAGE)
				.sound(SoundType.PLANT)
				.hardnessAndResistance(0.5F)
		), "vine");
		chain = registry.register(new ChainBlock(Items.IRON_NUGGET, Block.Properties
				.create(Material.IRON, MaterialColor.STONE)
				.sound(SoundType.METAL)
				.hardnessAndResistance(5.0F)
				.harvestTool(ToolType.PICKAXE)
				.harvestLevel(0)
		), "chain");
		// iron bars override
		if (Config.climbableIronBars.get()) {
			ironBars = registry.register(new ClimbablePaneBlock(Block.Properties.create(Material.IRON, MaterialColor.AIR).hardnessAndResistance(5.0F, 6.0F).sound(SoundType.METAL)), new ResourceLocation("iron_bars"));
		}

		glassDoor = registry.register(new GlassDoorBlock(), "glass_door");
		glassTrapdoor = registry.register(new GlassTrapdoorBlock(), "glass_trapdoor");

		// TODO: register enum
		plainMulch  = registry.register(new MulchBlock(MaterialColor.LIGHT_GRAY), "plain_mulch");
		brownMulch  = registry.register(new MulchBlock(MaterialColor.DIRT), "brown_mulch");
		redMulch    = registry.register(new MulchBlock(MaterialColor.NETHERRACK), "red_mulch");
		blackMulch  = registry.register(new MulchBlock(MaterialColor.GRAY), "black_mulch");
		blueMulch   = registry.register(new MulchBlock(MaterialColor.BLUE), "blue_mulch");

		// TODO: register enum
		path_rock  = registry.register(new PathBlock(PathBlock.SHAPE_ROCK, MaterialColor.STONE), "rock_path");
		path_round = registry.register(new PathBlock(PathBlock.SHAPE_ROUND, MaterialColor.STONE), "round_path");
		path_tile  = registry.register(new PathBlock(PathBlock.SHAPE_TILE, MaterialColor.STONE), "tile_path");
		path_brick = registry.register(new PathBlock(PathBlock.SHAPE_BRICK, MaterialColor.RED), "brick_path");

		// TODO: register enum?
		flower_cyan = registry.register(new FlowerBlock(null), "cyan_flower");
		flower_syringa = registry.register(new FlowerBlock((DoublePlantBlock) Blocks.LILAC), "syringa");
		flower_paeonia = registry.register(new FlowerBlock((DoublePlantBlock) Blocks.PEONY), "paeonia");
		flower_rose = registry.register(new FlowerBlock((DoublePlantBlock) Blocks.ROSE_BUSH), "rose");

		// TODO: register enum?
		Supplier<FlowerPotBlock> emptyPot = () -> (FlowerPotBlock) Blocks.FLOWER_POT.delegate.get();
		Block.Properties props = Block.Properties.from(Blocks.FLOWER_POT);
		potted_cyan = registry.register(new FlowerPotBlock(emptyPot, () -> flower_cyan, props), "potted_cyan");
		potted_syringa = registry.register(new FlowerPotBlock(emptyPot, () -> flower_syringa, props), "potted_syringa");
		potted_paeonia = registry.register(new FlowerPotBlock(emptyPot, () -> flower_paeonia, props), "potted_paeonia");
		potted_rose = registry.register(new FlowerPotBlock(emptyPot, () -> flower_rose, props), "potted_rose");

		// Register the flower items with the empty flower pot block.
		FlowerPotBlock flowerPot = (FlowerPotBlock) Blocks.FLOWER_POT;
		flowerPot.addPlant(flower_cyan.getRegistryName(), () -> potted_cyan);
		flowerPot.addPlant(flower_syringa.getRegistryName(), () -> potted_syringa);
		flowerPot.addPlant(flower_paeonia.getRegistryName(), () -> potted_paeonia);
		flowerPot.addPlant(flower_rose.getRegistryName(), () -> potted_rose);

		// TODO: register enum?
		whiteEnlightenedBush = registry.register(new EnlightenedBushBlock(-1), "white_enlightened_bush");
		redEnlightenedBush = registry.register(new EnlightenedBushBlock(0xBF0000), "red_enlightened_bush");
		greenEnlightenedBush = registry.register(new EnlightenedBushBlock(0x267F00), "green_enlightened_bush");
		blueEnlightenedBush = registry.register(new EnlightenedBushBlock(0x001CBF), "blue_enlightened_bush");
	}

	@SubscribeEvent
	public void registerItems(Register<Item> event) {
		ItemRegistryAdapter registry = new ItemRegistryAdapter(event.getRegistry());
		// common props
		Item.Properties materialProps = new Item.Properties().group(ItemGroup.MATERIALS);
		Item.Properties decorationProps = new Item.Properties().group(ItemGroup.DECORATIONS);
		Item.Properties buildingProps = new Item.Properties().group(ItemGroup.BUILDING_BLOCKS);
		Item.Properties redstoneProps = new Item.Properties().group(ItemGroup.REDSTONE);

		// TODO: enum object
		for (DyeColor color : DyeColor.values()) {
			coloredBooks[color.getId()] = registry.register(new HidableItem(materialProps, Config::enableColoredBooks), color.getString() + "_book");
		}

		redstoneBook = registry.register(new HidableItem(materialProps, Config::enableRedstoneBook), "redstone_book");

		// itemblocks
		registry.registerBlockItem(new BookshelfItem(shelf_normal));
		registry.registerBlockItem(new BookshelfItem(shelf_ancient));
		registry.registerBlockItem(new BookshelfItem(shelf_rainbow));
		registry.registerBlockItem(new BookshelfItem(shelf_tomes));

		registry.registerBlockItem(rope, decorationProps);
		registry.registerBlockItem(vine, decorationProps);
		registry.registerBlockItem(chain, decorationProps);
		registry.registerBlockItem(ironBars, decorationProps);

		registry.registerBlockItem(plainMulch, buildingProps);
		registry.registerBlockItem(brownMulch, buildingProps);
		registry.registerBlockItem(redMulch, buildingProps);
		registry.registerBlockItem(blackMulch, buildingProps);
		registry.registerBlockItem(blueMulch, buildingProps);

		registry.registerBlockItem(path_rock, decorationProps);
		registry.registerBlockItem(path_round, decorationProps);
		registry.registerBlockItem(path_tile, decorationProps);
		registry.registerBlockItem(path_brick, decorationProps);

		registry.registerBlockItem(flower_cyan, decorationProps);
		registry.registerBlockItem(flower_syringa, decorationProps);
		registry.registerBlockItem(flower_paeonia, decorationProps);
		registry.registerBlockItem(flower_rose, decorationProps);

		registry.registerBlockItem(new TextureBlockItem(whiteEnlightenedBush, decorationProps, ItemTags.LEAVES));
		registry.registerBlockItem(new TextureBlockItem(redEnlightenedBush, decorationProps, ItemTags.LEAVES));
		registry.registerBlockItem(new TextureBlockItem(greenEnlightenedBush, decorationProps, ItemTags.LEAVES));
		registry.registerBlockItem(new TextureBlockItem(blueEnlightenedBush, decorationProps, ItemTags.LEAVES));

		glassDoorItem = registry.register(new GlassDoorBlockItem(glassDoor, redstoneProps), "glass_door");
		registry.registerBlockItem(glassTrapdoor, redstoneProps);
	}

	@SubscribeEvent
	void gatherData(GatherDataEvent event) {
		DataGenerator gen = event.getGenerator();
		if (event.includeServer()) {
			gen.addProvider(new BuildingRecipeProvider(gen));
		}
	}

	@SubscribeEvent
	void init(FMLCommonSetupEvent event) {
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

	@SubscribeEvent
	public static void loadLoad(LootTableLoadEvent event) {
		// Add the drops for the small flowers.
		flower_paeonia.injectLoot(event);
		flower_rose.injectLoot(event);
		flower_syringa.injectLoot(event);
	}

	private static void registerCompostables() {
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
