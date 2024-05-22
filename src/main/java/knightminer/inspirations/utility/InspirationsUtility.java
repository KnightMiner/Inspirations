package knightminer.inspirations.utility;


import knightminer.inspirations.common.ModuleBase;
import knightminer.inspirations.common.item.HidableBlockItem;
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
import knightminer.inspirations.utility.item.TorchLeverItem;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
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
    if (resourceKey == Registry.BLOCK_REGISTRY) {
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

      //bricksButton = registerBlock(r, new BricksButtonBlock(BricksButtonBlock.BRICK_BUTTON), "bricks_button");
      //netherBricksButton = registerBlock(r, new BricksButtonBlock(BricksButtonBlock.NETHER_BUTTON), "nether_bricks_button");

      carpetedTrapdoors = registry.registerEnum((color) -> new CarpetedTrapdoorBlock(), DyeColor.values(), "carpeted_trapdoor");
      carpetedPressurePlates = registry.registerEnum(CarpetedPressurePlateBlock::new, DyeColor.values(), "carpeted_pressure_plate");

      collector = registry.register(new CollectorBlock(), "collector");
      pipe = registry.register(new PipeBlock(), "pipe");
    }
    else if (resourceKey == Registry.BLOCK_ENTITY_TYPE_REGISTRY) {
      BlockEntityTypeRegistryAdapter registry = new BlockEntityTypeRegistryAdapter(ForgeRegistries.BLOCK_ENTITY_TYPES);

      tileCollector = registry.register(CollectorBlockEntity::new, collector, "collector");
      tilePipe = registry.register(PipeBlockEntity::new, pipe, "pipe");
    }
    else if (resourceKey == Registry.MENU_REGISTRY) {
      ContainerTypeRegistryAdapter registry = new ContainerTypeRegistryAdapter(ForgeRegistries.MENU_TYPES);

      contCollector = registry.registerType(CollectorContainerMenu::new, "collector");
      contPipe = registry.registerType(PipeContainerMenu::new, "pipe");
    }
    else if (resourceKey == Registry.ITEM_REGISTRY) {
      Item.Properties props = new Item.Properties().tab(CreativeModeTab.TAB_REDSTONE);
      ItemRegistryAdapter registry = new ItemRegistryAdapter(ForgeRegistries.ITEMS, props);

      // itemblocks
      torchLeverItem = registry.register(new TorchLeverItem(InspirationsUtility.torchLeverFloor, InspirationsUtility.torchLeverWall, props), "torch_lever");
      soulLeverItem = registry.register(new TorchLeverItem(InspirationsUtility.soulLeverFloor, InspirationsUtility.soulLeverWall, props), "soul_torch_lever");
      //registerBlockItem(r, bricksButton, ItemGroup.REDSTONE);
      //registerBlockItem(r, netherBricksButton, ItemGroup.REDSTONE);
      // TODO: never made a bifunction variant
      registry.registerBlockItem(carpetedTrapdoors, (block) -> new HidableBlockItem(block, props));
      registry.registerDefaultBlockItem(collector);
      registry.registerDefaultBlockItem(pipe);
    }
  }

  @SubscribeEvent
  public void gatherData(GatherDataEvent event) {
    DataGenerator gen = event.getGenerator();
    gen.addProvider(event.includeServer(), new UtilityRecipeProvider(gen));
  }

  /*
  @SubscribeEvent
  public void setup(FMLCommonSetupEvent event) {
    registerDispenserBehavior();
  }

  // Get access to the existing behaviours.
//  private static class DispenserRegAccess extends DispenserBlock {
//    DispenserRegAccess() { super(Block.Properties.of(Material.AIR));}
//
//    DispenseItemBehavior getRegisteredBehaviour(Item item) {
//      return super.getDispenseMethod(new ItemStack(item));
//    }
//  }
//
//  private final Lazy<DispenserRegAccess> dispenserReg = Lazy.of(DispenserRegAccess::new);

  private void registerDispenserBehavior() {
    if(Config.enableDispenserFluidTanks.get()) {
      for(Item item : InspirationsRegistry.TAG_DISP_FLUID_TANKS.getAllElements()) {
        if(item != null) {
          DispenserBlock.registerDispenseBehavior(item, new DispenseFluidTank(dispenserReg.getRegisteredBehaviour(item)));
        }
      }
    }
  }
  */
}
