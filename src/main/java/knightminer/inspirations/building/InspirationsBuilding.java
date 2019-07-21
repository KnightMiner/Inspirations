package knightminer.inspirations.building;

import com.google.common.eventbus.Subscribe;

import com.mojang.datafixers.DataFixer;
import knightminer.inspirations.building.block.BlockBookshelf;
import knightminer.inspirations.building.block.BlockEnlightenedBush;
import knightminer.inspirations.building.block.BlockFlower;
import knightminer.inspirations.building.block.BlockGlassDoor;
import knightminer.inspirations.building.block.BlockGlassTrapdoor;
import knightminer.inspirations.building.block.BlockMulch;
import knightminer.inspirations.building.block.BlockPath;
import knightminer.inspirations.building.block.BlockRope;
import knightminer.inspirations.building.block.BlockFlower.FlowerType;
import knightminer.inspirations.building.inventory.ContainerBookshelf;
import knightminer.inspirations.building.tileentity.TileBookshelf;
import knightminer.inspirations.building.tileentity.TileEnlightenedBush;
import knightminer.inspirations.common.CommonProxy;
import knightminer.inspirations.common.Config;
import knightminer.inspirations.common.PulseBase;
import knightminer.inspirations.common.item.ItemBlockTexture;
import knightminer.inspirations.library.InspirationsRegistry;
import knightminer.inspirations.library.recipe.cauldron.CauldronDyeRecipe;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.*;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.registries.IForgeRegistry;
import slimeknights.mantle.item.ItemMetaDynamic;
import slimeknights.mantle.pulsar.pulse.Pulse;

import java.util.HashMap;
import java.util.Map;

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
	public static Map<BlockMulch.MulchColor, Block> mulch=new HashMap<>();

	public static BlockPath path_rock;
	public static BlockPath path_round;
	public static BlockPath path_tile;
	public static BlockPath path_brick;

	public static Block flower_cyan;
	public static Block flower_lilac;
	public static Block flower_peony;
	public static Block flower_rose;

	public static Block enlightenedBush;

	// items
	public static Item glassDoorItem;
	public static Map<DyeColor, Item> book_colors=new HashMap<>();

	// materials
	public static Item redstoneBook;

	// Tile Entities
	public static TileEntityType<TileBookshelf> tileBookshelf;
	public static TileEntityType<TileEnlightenedBush> tileEnlightenedBush;

	// Container Types
	public static ContainerType<ContainerBookshelf> contBookshelf;

	@Subscribe
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

		contBookshelf = register(r, new ContainerType<>(ContainerBookshelf::new), "bookshelf");
	}

	@SubscribeEvent
	public void registerBlocks(Register<Block> event) {
		IForgeRegistry<Block> r = event.getRegistry();

		if(Config.enableBookshelf) {
			shelf_normal = registerBlock(r, new BlockBookshelf(), "normal_bookshelf");
			shelf_ancient = registerBlock(r, new BlockBookshelf(), "ancient_bookshelf");
			shelf_rainbow = registerBlock(r, new BlockBookshelf(), "rainbow_bookshelf");
			shelf_tomes = registerBlock(r, new BlockBookshelf(), "tomes_bookshelf");
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
//			glassDoor = registerBlock(r, new BlockGlassDoor(), "glass_door");
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
			path_rock = registerBlock(r, new BlockPath(MaterialColor.STONE), "rock_path");
			path_round = registerBlock(r, new BlockPath(MaterialColor.STONE), "round_path");
			path_tile = registerBlock(r, new BlockPath(MaterialColor.STONE), "tile_path");
			path_brick = registerBlock(r, new BlockPath(MaterialColor.RED), "brick_path");
		}

		if(Config.enableFlowers) {
			flower_cyan = registerBlock(r, new BlockFlower(null), "cyan_flower");
			flower_lilac = registerBlock(r, new BlockFlower((DoublePlantBlock) Blocks.LILAC), "lilac");
			flower_peony = registerBlock(r, new BlockFlower((DoublePlantBlock) Blocks.PEONY), "peony");
			flower_rose = registerBlock(r, new BlockFlower((DoublePlantBlock) Blocks.ROSE_BUSH), "rose");
		}

		if(Config.enableEnlightenedBush) {
//			enlightenedBush = registerBlock(r, new BlockEnlightenedBush(), "enlightened_bush");
//			registerTE(TileEnlightenedBush.class, "enlightened_bush");
		}
	}

	@SubscribeEvent
	public void registerItems(Register<Item> event) {
		IForgeRegistry<Item> r = event.getRegistry();

		if (Config.enableColoredBooks) {
			for (DyeColor color : DyeColor.values()) {
				book_colors.put(color, registerItem(r, new Item(new Item.Properties().group(ItemGroup.MATERIALS)), color.getName() + "_book"));
			}
		}
		if (Config.enableRedstoneBook && isUtilityLoaded()) {
			redstoneBook = registerItem(r, new Item(new Item.Properties().group(ItemGroup.MATERIALS)), "redstone_book");
		}

		// itemblocks
		if (shelf_normal != null) {
			Item.Properties deco_props = new Item.Properties().group(ItemGroup.DECORATIONS);
			registerItemBlock(r, new ItemBlockTexture(shelf_normal, deco_props));
			registerItemBlock(r, new ItemBlockTexture(shelf_ancient, deco_props));
			registerItemBlock(r, new ItemBlockTexture(shelf_rainbow, deco_props));
			registerItemBlock(r, new ItemBlockTexture(shelf_tomes, deco_props));
		}

		if (rope != null) {
			registerItemBlock(r, rope, ItemGroup.DECORATIONS);
			registerItemBlock(r, vine, ItemGroup.DECORATIONS);
			registerItemBlock(r, chain, ItemGroup.DECORATIONS);
		}

		for (Block mulchBlock : mulch.values()) {
			registerItemBlock(r, mulchBlock, ItemGroup.BUILDING_BLOCKS);
		}
		if (path_rock != null) {
			registerItemBlock(r, path_rock, ItemGroup.DECORATIONS);
			registerItemBlock(r, path_round, ItemGroup.DECORATIONS);
			registerItemBlock(r, path_tile, ItemGroup.DECORATIONS);
			registerItemBlock(r, path_brick, ItemGroup.DECORATIONS);
		}

		if (flower_cyan != null) {
			registerItemBlock(r, flower_cyan, ItemGroup.DECORATIONS);
			registerItemBlock(r, flower_lilac, ItemGroup.DECORATIONS);
			registerItemBlock(r, flower_peony, ItemGroup.DECORATIONS);
			registerItemBlock(r, flower_rose, ItemGroup.DECORATIONS);
		}

		if(enlightenedBush != null) {
			registerItemBlock(r, new ItemBlockTexture(enlightenedBush, new Item.Properties().group(ItemGroup.DECORATIONS)));
		}

		if(Config.enableGlassDoor) {
			glassDoorItem = registerItem(r, new TallBlockItem(glassDoor, new Item.Properties().group(ItemGroup.REDSTONE)) {
				@Override
				public int getBurnTime(ItemStack itemStack) {
					return 0;
				}
			}, "glass_door");
			registerItemBlock(r, glassTrapdoor, ItemGroup.REDSTONE);
		}
	}

	@Subscribe
	public void init(InterModEnqueueEvent event) {
		proxy.init();

		if(Config.enableFlowers && Config.enableCauldronDyeing) {
			InspirationsRegistry.addCauldronRecipe(new CauldronDyeRecipe(
				new ItemStack(flower_rose),
				DyeColor.CYAN,
				new ItemStack(flower_cyan));
		}
	}

	@Subscribe
	public void postInit(InterModProcessEvent event) {
		proxy.postInit();

		MinecraftForge.EVENT_BUS.register(BuildingEvents.class);
	}
}
