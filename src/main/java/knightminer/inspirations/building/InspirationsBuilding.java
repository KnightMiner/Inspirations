package knightminer.inspirations.building;

import knightminer.inspirations.building.block.ClimbablePaneBlock;
import knightminer.inspirations.building.block.EnlightenedBushBlock;
import knightminer.inspirations.building.block.GlassDoorBlock;
import knightminer.inspirations.building.block.GlassTrapdoorBlock;
import knightminer.inspirations.building.block.GrowableFlowerBlock;
import knightminer.inspirations.building.block.MulchBlock;
import knightminer.inspirations.building.block.PathBlock;
import knightminer.inspirations.building.block.RopeBlock;
import knightminer.inspirations.building.block.ShelfBlock;
import knightminer.inspirations.building.block.entity.EnlightenedBushBlockEntity;
import knightminer.inspirations.building.block.entity.ShelfBlockEntity;
import knightminer.inspirations.building.block.menu.ShelfContainerMenu;
import knightminer.inspirations.building.block.type.BushType;
import knightminer.inspirations.building.block.type.FlowerType;
import knightminer.inspirations.building.block.type.MulchType;
import knightminer.inspirations.building.block.type.PathType;
import knightminer.inspirations.building.block.type.ShelfType;
import knightminer.inspirations.building.datagen.BuildingRecipeProvider;
import knightminer.inspirations.building.item.GlassDoorBlockItem;
import knightminer.inspirations.building.item.ShelfItem;
import knightminer.inspirations.common.Config;
import knightminer.inspirations.common.ModuleBase;
import knightminer.inspirations.common.item.HidableBlockItem;
import knightminer.inspirations.common.item.HidableItem;
import knightminer.inspirations.common.item.HidableRetexturedBlockItem;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ComposterBlock;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;
import slimeknights.mantle.registration.adapter.BlockEntityTypeRegistryAdapter;
import slimeknights.mantle.registration.adapter.BlockRegistryAdapter;
import slimeknights.mantle.registration.adapter.ContainerTypeRegistryAdapter;
import slimeknights.mantle.registration.adapter.ItemRegistryAdapter;
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
  public static EnumObject<FlowerType,GrowableFlowerBlock> flower = EnumObject.empty();
  public static EnumObject<FlowerType,FlowerPotBlock> flowerPot = EnumObject.empty();
  // overrides
  public static Block ironBars;

  // items
  public static Item glassDoorItem;
  public static Item redstoneBook;
  // emum
  public static EnumObject<DyeColor,Item> coloredBooks;

  // Tile Entities
  public static BlockEntityType<ShelfBlockEntity> shelfTileEntity;
  public static BlockEntityType<EnlightenedBushBlockEntity> enlightenedBushTileEntity;

  // Container Types
  public static MenuType<ShelfContainerMenu> shelfContainer;

  @SubscribeEvent
  void registerTE(Register<BlockEntityType<?>> event) {
    BlockEntityTypeRegistryAdapter registry = new BlockEntityTypeRegistryAdapter(event.getRegistry());

    shelfTileEntity = registry.register(ShelfBlockEntity::new, shelf, "bookshelf");
    enlightenedBushTileEntity = registry.register(EnlightenedBushBlockEntity::new, enlightenedBush, "enlightened_bush");
  }

  @SubscribeEvent
  void registerContainers(Register<MenuType<?>> event) {
    ContainerTypeRegistryAdapter registry = new ContainerTypeRegistryAdapter(event.getRegistry());
    shelfContainer = registry.registerType(ShelfContainerMenu::new, "shelf");
  }

  @SubscribeEvent
  public void registerBlocks(Register<Block> event) {
    BlockRegistryAdapter registry = new BlockRegistryAdapter(event.getRegistry());

    // normal shelf uses a less regular naming
    BlockBehaviour.Properties shelfProps = Block.Properties.of(Material.WOOD).strength(2.0F, 5.0F).sound(SoundType.WOOD).noOcclusion();
    shelf = new EnumObject.Builder<ShelfType,ShelfBlock>(ShelfType.class)
        .putDelegate(ShelfType.NORMAL, registry.register(new ShelfBlock(shelfProps), "shelf").delegate)
        .putAll(registry.registerEnum(type -> new ShelfBlock(shelfProps), ShelfType.FANCY, "shelf"))
        .build();
    rope = registry.register(new RopeBlock(Items.STICK, Block.Properties
        .of(Material.CLOTH_DECORATION, MaterialColor.PODZOL)
        .sound(SoundType.WOOL)
        .strength(0.5F)
    ), "rope");
    vine = registry.register(new RopeBlock(Items.BAMBOO, Block.Properties
        .of(Material.CLOTH_DECORATION, MaterialColor.PLANT)
        .sound(SoundType.GRASS)
        .strength(0.5F)
    ), "vine");
    // iron bars override
    if (Config.climbableIronBars.get()) {
      ironBars = registry.register(new ClimbablePaneBlock(Block.Properties.of(Material.METAL, MaterialColor.NONE).strength(5.0F, 6.0F).sound(SoundType.METAL)), new ResourceLocation("iron_bars"));
    }

    BlockBehaviour.Properties glassDoorProps = Block.Properties.of(Material.GLASS).strength(0.3F).sound(SoundType.GLASS).noOcclusion();
    glassDoor = registry.register(new GlassDoorBlock(glassDoorProps), "glass_door");
    glassTrapdoor = registry.register(new GlassTrapdoorBlock(glassDoorProps), "glass_trapdoor");

    mulch = registry.registerEnum(type -> new MulchBlock(
        Properties.of(Material.WOOD, type.getColor()).sound(SoundType.WET_GRASS).strength(0.6F)
    ), MulchType.values(), "mulch");
    path = registry.registerEnum(type -> new PathBlock(Block.Properties.of(Material.STONE, type.getColor()).strength(1.5F, 10F), type.getShape()), PathType.values(), "path");
    enlightenedBush = registry.registerEnum(type -> new EnlightenedBushBlock(type.getColor()), BushType.values(), "enlightened_bush");

    // flowers, have no base name
    BlockBehaviour.Properties flowerProps = Block.Properties.of(Material.PLANT).strength(0F).sound(SoundType.GRASS).noCollission();
    flower = new EnumObject.Builder<FlowerType,GrowableFlowerBlock>(FlowerType.class)
        .putDelegate(FlowerType.CYAN, registry.register(new GrowableFlowerBlock(MobEffects.SLOW_FALLING, 4, null, flowerProps), "cyan_flower").delegate)
        .putDelegate(FlowerType.SYRINGA, registry.register(new GrowableFlowerBlock(MobEffects.HUNGER, 8, (DoublePlantBlock)Blocks.LILAC, flowerProps), "syringa").delegate)
        .putDelegate(FlowerType.PAEONIA, registry.register(new GrowableFlowerBlock(MobEffects.WATER_BREATHING, 5, (DoublePlantBlock)Blocks.PEONY, flowerProps), "paeonia").delegate)
        .putDelegate(FlowerType.ROSE, registry.register(new GrowableFlowerBlock(MobEffects.DIG_SPEED, 7, (DoublePlantBlock)Blocks.ROSE_BUSH, flowerProps), "rose").delegate)
        .build();
    // flower pots
    Supplier<FlowerPotBlock> emptyPot = () -> (FlowerPotBlock)Blocks.FLOWER_POT.delegate.get();
    FlowerPotBlock vanillaPot = (FlowerPotBlock)Blocks.FLOWER_POT;
    Block.Properties props = Block.Properties.copy(Blocks.FLOWER_POT);
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
    Item.Properties materialProps = new Item.Properties().tab(CreativeModeTab.TAB_MATERIALS);
    Item.Properties decorationProps = new Item.Properties().tab(CreativeModeTab.TAB_DECORATIONS);
    Item.Properties buildingProps = new Item.Properties().tab(CreativeModeTab.TAB_BUILDING_BLOCKS);
    Item.Properties redstoneProps = new Item.Properties().tab(CreativeModeTab.TAB_REDSTONE);

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
    event.enqueueWork(InspirationsBuilding::registerCompostables);

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
      ComposterBlock.add(0.3F, bush);
    }
    ComposterBlock.add(0.5F, vine);
    for (Block plant : flower.values()) {
      ComposterBlock.add(0.65F, plant);
    }
  }
}
