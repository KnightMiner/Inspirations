package knightminer.inspirations.building;

import knightminer.inspirations.building.block.ClimbablePaneBlock;
import knightminer.inspirations.building.block.EnlightenedBushBlock;
import knightminer.inspirations.building.block.FlowerBlock;
import knightminer.inspirations.building.block.GlassDoorBlock;
import knightminer.inspirations.building.block.GlassTrapdoorBlock;
import knightminer.inspirations.building.block.MulchBlock;
import knightminer.inspirations.building.block.PathBlock;
import knightminer.inspirations.building.block.RopeBlock;
import knightminer.inspirations.building.block.ShelfBlock;
import knightminer.inspirations.building.block.type.BushType;
import knightminer.inspirations.building.block.type.FlowerType;
import knightminer.inspirations.building.block.type.MulchType;
import knightminer.inspirations.building.block.type.PathType;
import knightminer.inspirations.building.block.type.ShelfType;
import knightminer.inspirations.building.datagen.BuildingRecipeProvider;
import knightminer.inspirations.building.inventory.ShelfContainer;
import knightminer.inspirations.building.item.GlassDoorBlockItem;
import knightminer.inspirations.building.item.ShelfItem;
import knightminer.inspirations.building.tileentity.EnlightenedBushTileEntity;
import knightminer.inspirations.building.tileentity.ShelfTileEntity;
import knightminer.inspirations.common.Config;
import knightminer.inspirations.common.ModuleBase;
import knightminer.inspirations.common.item.HidableBlockItem;
import knightminer.inspirations.common.item.HidableItem;
import knightminer.inspirations.common.item.HidableRetexturedBlockItem;
import net.minecraft.block.AbstractBlock;
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
import net.minecraftforge.common.ToolType;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;
import slimeknights.mantle.registration.adapter.BlockRegistryAdapter;
import slimeknights.mantle.registration.adapter.ContainerTypeRegistryAdapter;
import slimeknights.mantle.registration.adapter.ItemRegistryAdapter;
import slimeknights.mantle.registration.adapter.TileEntityTypeRegistryAdapter;
import slimeknights.mantle.registration.object.EnumObject;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * Module containing all the building blocks
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public class InspirationsBuilding extends ModuleBase {
  // blocks
  public static RopeBlock rope;
  public static RopeBlock vine;
  public static Block glassDoor;
  public static Block glassTrapdoor;
  // enum
  public static EnumObject<ShelfType,ShelfBlock> shelf = EnumObject.empty();
  public static EnumObject<MulchType,MulchBlock> mulch = EnumObject.empty();
  public static EnumObject<PathType,PathBlock> path = EnumObject.empty();
  public static EnumObject<BushType,EnlightenedBushBlock> enlightenedBush = EnumObject.empty();
  // flowers
  public static EnumObject<FlowerType,FlowerBlock> flower = EnumObject.empty();
  public static EnumObject<FlowerType,FlowerPotBlock> flowerPot = EnumObject.empty();
  // overrides
  public static Block ironBars;

  // items
  public static Item glassDoorItem;
  public static Item redstoneBook;
  // emum
  public static EnumObject<DyeColor,Item> coloredBooks;

  // Tile Entities
  public static TileEntityType<ShelfTileEntity> shelfTileEntity;
  public static TileEntityType<EnlightenedBushTileEntity> enlightenedBushTileEntity;

  // Container Types
  public static ContainerType<ShelfContainer> shelfContainer;

  @SubscribeEvent
  void registerTE(Register<TileEntityType<?>> event) {
    TileEntityTypeRegistryAdapter registry = new TileEntityTypeRegistryAdapter(event.getRegistry());

    shelfTileEntity = registry.register(ShelfTileEntity::new, shelf, "bookshelf");
    enlightenedBushTileEntity = registry.register(EnlightenedBushTileEntity::new, enlightenedBush, "enlightened_bush");
  }

  @SubscribeEvent
  void registerContainers(Register<ContainerType<?>> event) {
    ContainerTypeRegistryAdapter registry = new ContainerTypeRegistryAdapter(event.getRegistry());
    shelfContainer = registry.registerType(ShelfContainer::new, "shelf");
  }

  @SubscribeEvent
  public void registerBlocks(Register<Block> event) {
    BlockRegistryAdapter registry = new BlockRegistryAdapter(event.getRegistry());

    // normal shelf uses a less regular naming
    AbstractBlock.Properties shelfProps = Block.Properties.create(Material.WOOD).harvestTool(ToolType.AXE).hardnessAndResistance(2.0F, 5.0F).sound(SoundType.WOOD).notSolid();
    shelf = new EnumObject.Builder<ShelfType,ShelfBlock>(ShelfType.class)
        .putDelegate(ShelfType.NORMAL, registry.register(new ShelfBlock(shelfProps), "shelf").delegate)
        .putAll(registry.registerEnum(type -> new ShelfBlock(shelfProps), ShelfType.FANCY, "shelf"))
        .build();
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
    // iron bars override
    if (Config.climbableIronBars.get()) {
      ironBars = registry.register(new ClimbablePaneBlock(Block.Properties.create(Material.IRON, MaterialColor.AIR).hardnessAndResistance(5.0F, 6.0F).sound(SoundType.METAL)), new ResourceLocation("iron_bars"));
    }

    AbstractBlock.Properties glassDoorProps = Block.Properties.create(Material.GLASS).hardnessAndResistance(0.3F).sound(SoundType.GLASS).notSolid();
    glassDoor = registry.register(new GlassDoorBlock(glassDoorProps), "glass_door");
    glassTrapdoor = registry.register(new GlassTrapdoorBlock(glassDoorProps), "glass_trapdoor");

    mulch = registry.registerEnum(type -> new MulchBlock(type.getColor()), MulchType.values(), "mulch");
    path = registry.registerEnum(type -> new PathBlock(type.getShape(), type.getColor()), PathType.values(), "path");
    enlightenedBush = registry.registerEnum(type -> new EnlightenedBushBlock(type.getColor()), BushType.values(), "enlightened_bush");

    // flowers, have no base name
    flower = new EnumObject.Builder<FlowerType,FlowerBlock>(FlowerType.class)
        .putDelegate(FlowerType.CYAN, registry.register(new FlowerBlock(null), "cyan_flower").delegate)
        .putDelegate(FlowerType.SYRINGA, registry.register(new FlowerBlock((DoublePlantBlock)Blocks.LILAC), "syringa").delegate)
        .putDelegate(FlowerType.PAEONIA, registry.register(new FlowerBlock((DoublePlantBlock)Blocks.PEONY), "paeonia").delegate)
        .putDelegate(FlowerType.ROSE, registry.register(new FlowerBlock((DoublePlantBlock)Blocks.ROSE_BUSH), "rose").delegate)
        .build();
    // flower pots
    Supplier<FlowerPotBlock> emptyPot = () -> (FlowerPotBlock)Blocks.FLOWER_POT.delegate.get();
    FlowerPotBlock vanillaPot = (FlowerPotBlock)Blocks.FLOWER_POT;
    Block.Properties props = Block.Properties.from(Blocks.FLOWER_POT);
    flowerPot = registry.registerEnum(type -> {
      // create pot and register it with the vanilla pot.
      Block plant = flower.get(type);
      FlowerPotBlock pot = new FlowerPotBlock(emptyPot, plant.delegate, props);
      vanillaPot.addPlant(Objects.requireNonNull(plant.getRegistryName()), pot.delegate);
      return pot;
    }, "potted", FlowerType.values());
  }

  @SubscribeEvent
  public void registerItems(Register<Item> event) {
    ItemRegistryAdapter registry = new ItemRegistryAdapter(event.getRegistry());
    // common props
    Item.Properties materialProps = new Item.Properties().group(ItemGroup.MATERIALS);
    Item.Properties decorationProps = new Item.Properties().group(ItemGroup.DECORATIONS);
    Item.Properties buildingProps = new Item.Properties().group(ItemGroup.BUILDING_BLOCKS);
    Item.Properties redstoneProps = new Item.Properties().group(ItemGroup.REDSTONE);

    coloredBooks = registry.registerEnum(color -> new HidableItem(materialProps, Config.enableColoredBooks), DyeColor.values(), "book");
    redstoneBook = registry.register(new HidableItem(materialProps, Config.enableRedstoneBook), "redstone_book");

    // item blocks
    registry.registerBlockItem(shelf, ShelfItem::new);
    registry.registerBlockItem(rope, decorationProps);
    registry.registerBlockItem(vine, decorationProps);
    if (ironBars != null) {
      registry.registerBlockItem(ironBars, decorationProps);
    }

    registry.registerBlockItem(mulch, buildingProps);
    registry.registerBlockItem(path, decorationProps);
    registry.registerBlockItem(flower, decorationProps);
    registry.registerBlockItem(enlightenedBush, (bush) -> new HidableRetexturedBlockItem(bush, ItemTags.LEAVES, decorationProps));

    glassDoorItem = registry.register(new GlassDoorBlockItem(glassDoor, redstoneProps), glassDoor);
    registry.registerBlockItem(new HidableBlockItem(glassTrapdoor, redstoneProps));
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
  }

  @SubscribeEvent
  public static void loadLoad(LootTableLoadEvent event) {
    // Add the drops for the small flowers.
    flower.forEach((type, plant) -> {
      if (type != FlowerType.CYAN) {
        plant.injectLoot(event);
      }
    });
  }

  private static void registerCompostables() {
    for (Block bush : enlightenedBush.values()) {
      ComposterBlock.registerCompostable(0.3F, bush);
    }
    ComposterBlock.registerCompostable(0.5F, vine);
    for (Block plant : flower.values()) {
      ComposterBlock.registerCompostable(0.65F, plant);
    }
  }
}
