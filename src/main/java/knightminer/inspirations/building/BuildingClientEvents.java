package knightminer.inspirations.building;

import knightminer.inspirations.Inspirations;
import knightminer.inspirations.building.block.type.BushType;
import knightminer.inspirations.building.block.type.ShelfType;
import knightminer.inspirations.building.client.ShelfScreen;
import knightminer.inspirations.building.client.ShelfTileEntityRenderer;
import knightminer.inspirations.building.tileentity.ShelfTileEntity;
import knightminer.inspirations.common.ClientEvents;
import knightminer.inspirations.library.MiscUtil;
import knightminer.inspirations.library.client.ClientUtil;
import knightminer.inspirations.library.client.model.ShelfModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.FoliageColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import slimeknights.mantle.block.entity.IRetexturedBlockEntity;
import slimeknights.mantle.item.RetexturedBlockItem;
import slimeknights.mantle.util.BlockEntityHelper;

import java.util.Optional;
import java.util.function.Consumer;

@SuppressWarnings("unused")
@EventBusSubscriber(modid = Inspirations.modID, value = Dist.CLIENT, bus = Bus.MOD)
public class BuildingClientEvents extends ClientEvents {
  private static final Minecraft mc = Minecraft.getInstance();

  @SubscribeEvent
  static void clientSetup(FMLClientSetupEvent event) {
    // set render types
    RenderType cutout = RenderType.cutout();
    Consumer<Block> setCutout = (block) -> ItemBlockRenderTypes.setRenderLayer(block, cutout);
    RenderType cutoutMipped = RenderType.cutoutMipped();
    Consumer<Block> setCutoutMipped = (block) -> ItemBlockRenderTypes.setRenderLayer(block, cutoutMipped);

    // general
    InspirationsBuilding.shelf.forEach(setCutout);
    InspirationsBuilding.enlightenedBush.forEach(setCutoutMipped);

    // ropes
    setRenderLayer(InspirationsBuilding.rope, cutout);
    setRenderLayer(InspirationsBuilding.vine, cutout);
    setRenderLayer(InspirationsBuilding.ironBars, cutoutMipped);

    // doors
    setRenderLayer(InspirationsBuilding.glassDoor, cutoutMipped);
    setRenderLayer(InspirationsBuilding.glassTrapdoor, cutoutMipped);

    // flower
    InspirationsBuilding.flower.forEach(setCutout);
    InspirationsBuilding.flowerPot.forEach(setCutout);
  }

  @SubscribeEvent
  static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
    event.registerBlockEntityRenderer(InspirationsBuilding.shelfTileEntity, ShelfTileEntityRenderer::new);
  }

  @SubscribeEvent
  static void registerModelLoaders(ModelRegistryEvent event) {
    ModelLoaderRegistry.registerLoader(Inspirations.getResource("shelf"), ShelfModel.LOADER);
  }

  @SubscribeEvent
  static void commonSetup(FMLCommonSetupEvent event) {
    // Register GUIs.
    registerScreenFactory(InspirationsBuilding.shelfContainer, ShelfScreen::new);
  }

  @SubscribeEvent
  static void registerBlockColors(ColorHandlerEvent.Block event) {
    BlockColors blockColors = event.getBlockColors();

    // coloring of books for normal bookshelf
    registerBlockColors(blockColors, (state, world, pos, tintIndex) -> {
      if (tintIndex > 0 && tintIndex <= 16 && world != null && pos != null) {
        BlockEntity te = world.getBlockEntity(pos);
        if (te instanceof ShelfTileEntity) {
          ItemStack stack = ((ShelfTileEntity)te).getInventory().getStackInSlot(tintIndex - 1);
          if (!stack.isEmpty()) {
            int color = ClientUtil.getItemColor(stack.getItem());
            int itemColors = mc.getItemColors().getColor(stack, 0);
            if (itemColors > -1) {
              // combine twice to make sure the item colors result is dominant
              color = MiscUtil.combineColors(color, itemColors, 3);
            }
            return color;
          }
        }
      }

      return -1;
    }, InspirationsBuilding.shelf.getOrNull(ShelfType.NORMAL));

    // rope vine coloring
    registerBlockColors(blockColors, (state, world, pos, tintIndex) -> {
      if (world != null && pos != null) {
        return BiomeColors.getAverageFoliageColor(world, pos);
      }
      return FoliageColor.getDefaultColor();
    }, InspirationsBuilding.vine);

    // bush block coloring
    // First the three which never change tint.
    InspirationsBuilding.enlightenedBush.forEach((type, bush) -> {
      if (type != BushType.WHITE) {
        int color = type.getColor(); // Make closure capture just the int.
        blockColors.register((state, world, pos, tintIndex) -> tintIndex == 0 ? color : -1, bush);
      }
    });

    // white copies the default leaf colors
    registerBlockColors(blockColors, (state, world, pos, tintIndex) -> {
      if (tintIndex != 0 || world == null || pos == null) {
        return -1;
      }
      // TODO: should probably pass block directly here
      Optional<IRetexturedBlockEntity> te = BlockEntityHelper.get(IRetexturedBlockEntity.class, world, pos);
      if (te.isPresent()) {
        Block block = te.get().getTexture();
        if (block != Blocks.AIR) {
          return ClientUtil.getStackBlockColorsSafe(new ItemStack(block), world, pos, 0);
        }
      }
      return FoliageColor.getDefaultColor();
    }, InspirationsBuilding.enlightenedBush.getOrNull(BushType.WHITE));
  }

  @SubscribeEvent
  static void registerItemColors(ColorHandlerEvent.Item event) {
    ItemColors itemColors = event.getItemColors();

    // coloring of books for normal bookshelf
    registerItemColors(itemColors, (stack, tintIndex) -> {
      if (tintIndex > 0 && tintIndex <= 16) {
        return 0x654B17;
      }
      return -1;
    }, InspirationsBuilding.shelf.getOrNull(ShelfType.NORMAL));

    // book covers, too lazy to make 16 cover textures
    InspirationsBuilding.coloredBooks.forEach((color, book) -> {
      int hexColor = MiscUtil.getColor(color);
      itemColors.register((stack, tintIndex) -> (tintIndex == 0) ? hexColor : -1, book);
    });

    // bush block colors
    // First the three blocks which never change tint.
    InspirationsBuilding.enlightenedBush.forEach((type, bush) -> {
      if (type != BushType.WHITE) {
        int color = type.getColor();
        itemColors.register((stack, tintIndex) -> tintIndex == 0 ? color : -1, bush);
      }
    });

    // The main one uses the tint of the textured stack
    registerItemColors(itemColors, (stack, tintIndex) -> {
      if (tintIndex != 0) {
        return -1;
      }
      // redirect to block for colors
      Block block = RetexturedBlockItem.getTexture(stack);
      if (block != Blocks.AIR) {
        return itemColors.getColor(new ItemStack(block), 0);
      } else {
        return FoliageColor.getDefaultColor();
      }
    }, InspirationsBuilding.enlightenedBush.getOrNull(BushType.WHITE));

    // We can't get the world position of the item, so use the default tint.
    registerItemColors(itemColors, (stack, tintIndex) -> FoliageColor.getDefaultColor(), InspirationsBuilding.vine);
  }
}
