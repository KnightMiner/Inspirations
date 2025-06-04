package knightminer.inspirations.utility;


import knightminer.inspirations.common.ModuleBase;
import knightminer.inspirations.utility.block.CarpetedPressurePlateBlock;
import knightminer.inspirations.utility.block.CarpetedTrapdoorBlock;
import knightminer.inspirations.utility.block.CollectorBlock;
import knightminer.inspirations.utility.block.PipeBlock;
import knightminer.inspirations.utility.block.TorchLeverBlock;
import knightminer.inspirations.utility.block.TorchLeverWallBlock;
import knightminer.inspirations.utility.block.entity.CollectorBlockEntity;
import knightminer.inspirations.utility.block.entity.PipeBlockEntity;
import knightminer.inspirations.utility.block.menu.CollectorContainerMenu;
import knightminer.inspirations.utility.block.menu.PipeContainerMenu;
import knightminer.inspirations.utility.datagen.UtilityRecipeProvider;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.StandingAndWallBlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;
import slimeknights.mantle.registration.adapter.BlockEntityTypeRegistryAdapter;
import slimeknights.mantle.registration.adapter.BlockRegistryAdapter;
import slimeknights.mantle.registration.adapter.ContainerTypeRegistryAdapter;
import slimeknights.mantle.registration.adapter.ItemRegistryAdapter;
import slimeknights.mantle.registration.object.EnumObject;

public class InspirationsUtility extends ModuleBase {
  // blocks
  public static Block torchLeverWall;
  public static Block torchLeverFloor;
  public static Block soulLeverWall;
  public static Block soulLeverFloor;
  //public static Block bricksButton;
  //public static Block netherBricksButton;
  public static EnumObject<DyeColor,CarpetedTrapdoorBlock> carpetedTrapdoors = EnumObject.empty();
  public static EnumObject<DyeColor,CarpetedPressurePlateBlock> carpetedPressurePlates = EnumObject.empty();
  public static Block collector;
  public static Block pipe;

  // Items
  public static Item torchLeverItem;
  public static Item soulLeverItem;

  // Tile entities
  public static BlockEntityType<CollectorBlockEntity> tileCollector;
  public static BlockEntityType<PipeBlockEntity> tilePipe;

  // Inventory containers
  public static MenuType<CollectorContainerMenu> contCollector;
  public static MenuType<PipeContainerMenu> contPipe;

  @SubscribeEvent
  public void register(RegisterEvent event) {
    ResourceKey<? extends Registry<?>> resourceKey = event.getRegistryKey();
    if (resourceKey == Registries.BLOCK) {
      BlockRegistryAdapter registry = new BlockRegistryAdapter(ForgeRegistries.BLOCKS);

      torchLeverFloor = registry.register(new TorchLeverBlock(
          BlockBehaviour.Properties.copy(Blocks.TORCH).sound(SoundType.WOOD),
          ParticleTypes.FLAME
      ), "torch_lever");
      torchLeverWall = registry.register(new TorchLeverWallBlock(
          BlockBehaviour.Properties.copy(Blocks.WALL_TORCH).lootFrom(() -> torchLeverFloor),
          ParticleTypes.FLAME
      ), "wall_torch_lever");

      soulLeverFloor = registry.register(new TorchLeverBlock(
          BlockBehaviour.Properties.copy(Blocks.SOUL_TORCH),
          ParticleTypes.SOUL_FIRE_FLAME
      ), "soul_torch_lever");
      soulLeverWall = registry.register(new TorchLeverWallBlock(
          BlockBehaviour.Properties.copy(Blocks.SOUL_WALL_TORCH).lootFrom(() -> soulLeverFloor),
          ParticleTypes.SOUL_FIRE_FLAME
      ), "wall_soul_torch_lever");

      //Block.Properties.of().mapColor(MapColor.STONE).instrument(NoteBlockInstrument.BASEDRUM).strength(1.5F, 10.0F).sound(SoundType.STONE).randomTicks()
      //bricksButton = registerBlock(r, new BricksButtonBlock(BricksButtonBlock.BRICK_BUTTON), "bricks_button");
      //netherBricksButton = registerBlock(r, new BricksButtonBlock(BricksButtonBlock.NETHER_BUTTON), "nether_bricks_button");

      carpetedTrapdoors = registry.registerEnum(color -> new CarpetedTrapdoorBlock(
        Block.Properties.of().mapColor(color).ignitedByLava().instrument(NoteBlockInstrument.BASS).strength(3.0F).sound(SoundType.WOOL)
      ), DyeColor.values(), "carpeted_trapdoor");
      carpetedPressurePlates = registry.registerEnum(color -> new CarpetedPressurePlateBlock(
        Block.Properties.of().mapColor(color).strength(0.5F).sound(SoundType.WOOL), color
      ), DyeColor.values(), "carpeted_pressure_plate");

      collector = registry.register(new CollectorBlock(
        Block.Properties.of().mapColor(MapColor.STONE).instrument(NoteBlockInstrument.BASEDRUM).strength(3.5F).sound(SoundType.STONE)
      ), "collector");
      pipe = registry.register(new PipeBlock(
        Block.Properties.of().mapColor(MapColor.STONE).strength(3.0F, 8.0F).sound(SoundType.METAL)
      ), "pipe");
    }
    else if (resourceKey == Registries.BLOCK_ENTITY_TYPE) {
      BlockEntityTypeRegistryAdapter registry = new BlockEntityTypeRegistryAdapter(ForgeRegistries.BLOCK_ENTITY_TYPES);

      tileCollector = registry.register(CollectorBlockEntity::new, collector, "collector");
      tilePipe = registry.register(PipeBlockEntity::new, pipe, "pipe");
    }
    else if (resourceKey == Registries.MENU) {
      ContainerTypeRegistryAdapter registry = new ContainerTypeRegistryAdapter(ForgeRegistries.MENU_TYPES);

      contCollector = registry.registerType(CollectorContainerMenu::new, "collector");
      contPipe = registry.registerType(PipeContainerMenu::new, "pipe");
    }
    else if (resourceKey == Registries.ITEM) {
      Item.Properties props = new Item.Properties();
      ItemRegistryAdapter registry = new ItemRegistryAdapter(ForgeRegistries.ITEMS, props);

      // itemblocks
      torchLeverItem = registry.register(new StandingAndWallBlockItem(InspirationsUtility.torchLeverFloor, InspirationsUtility.torchLeverWall, props, Direction.DOWN), "torch_lever");
      soulLeverItem = registry.register(new StandingAndWallBlockItem(InspirationsUtility.soulLeverFloor, InspirationsUtility.soulLeverWall, props, Direction.DOWN), "soul_torch_lever");
      //registerBlockItem(r, bricksButton, ItemGroup.REDSTONE);
      //registerBlockItem(r, netherBricksButton, ItemGroup.REDSTONE);
      registry.registerDefaultBlockItem(carpetedTrapdoors);
      registry.registerDefaultBlockItem(collector);
      registry.registerDefaultBlockItem(pipe);
    }
  }

  @SubscribeEvent
  public void gatherData(GatherDataEvent event) {
    DataGenerator gen = event.getGenerator();
    gen.addProvider(event.includeServer(), new UtilityRecipeProvider(gen.getPackOutput()));
  }
}
