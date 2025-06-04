package knightminer.inspirations.building;

import knightminer.inspirations.Inspirations;
import knightminer.inspirations.building.block.entity.ShelfBlockEntity;
import knightminer.inspirations.building.block.type.BushType;
import knightminer.inspirations.building.block.type.ShelfType;
import knightminer.inspirations.building.client.ShelfBlockEntityRenderer;
import knightminer.inspirations.building.client.ShelfContainerScreen;
import knightminer.inspirations.common.AbstractClientEvents;
import knightminer.inspirations.library.MiscUtil;
import knightminer.inspirations.library.client.ClientUtil;
import knightminer.inspirations.library.client.model.ShelfModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.item.DyeableLeatherItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.FoliageColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.ModelEvent.RegisterGeometryLoaders;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import slimeknights.mantle.block.entity.IRetexturedBlockEntity;
import slimeknights.mantle.util.BlockEntityHelper;
import slimeknights.mantle.util.RetexturedHelper;

import java.util.Optional;

@EventBusSubscriber(modid = Inspirations.modID, value = Dist.CLIENT, bus = Bus.MOD)
public class BuildingClientEvents extends AbstractClientEvents {
  private static final Minecraft mc = Minecraft.getInstance();

  @SuppressWarnings("removal")  // not my block model, can't change easily
  @SubscribeEvent
  static void clientSetup(FMLClientSetupEvent event) {
    // ropes
    if (InspirationsBuilding.ironBars != null) {
      ItemBlockRenderTypes.setRenderLayer(InspirationsBuilding.ironBars, RenderType.cutoutMipped());
    }
  }

  @SubscribeEvent
  static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
    event.registerBlockEntityRenderer(InspirationsBuilding.shelfTileEntity, ShelfBlockEntityRenderer::new);
  }

  @SubscribeEvent
  static void registerModelLoaders(RegisterGeometryLoaders event) {
    event.register("shelf", ShelfModel.LOADER);
  }

  @SubscribeEvent
  static void commonSetup(FMLCommonSetupEvent event) {
    // Register GUIs.
    registerScreenFactory(InspirationsBuilding.shelfContainer, ShelfContainerScreen::new);
  }

  @SubscribeEvent
  static void registerBlockColors(RegisterColorHandlersEvent.Block event) {
    BlockColors blockColors = event.getBlockColors();

    // coloring of books for normal bookshelf
    registerBlockColors(blockColors, (state, world, pos, tintIndex) -> {
      if (tintIndex > 0 && tintIndex <= 16 && world != null && pos != null) {
        BlockEntity te = world.getBlockEntity(pos);
        if (te instanceof ShelfBlockEntity) {
          ItemStack stack = ((ShelfBlockEntity)te).getInventory().getStackInSlot(tintIndex - 1);
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
        event.register((state, world, pos, tintIndex) -> tintIndex == 0 ? color : -1, bush);
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
  static void registerItemColors(RegisterColorHandlersEvent.Item event) {
    ItemColors itemColors = event.getItemColors();

    // coloring of books for normal bookshelf
    registerItemColors(itemColors, (stack, tintIndex) -> {
      if (tintIndex > 0 && tintIndex <= 16) {
        return 0x654B17;
      }
      return -1;
    }, InspirationsBuilding.shelf.getOrNull(ShelfType.NORMAL));

    // book covers, too lazy to make 16 cover textures
    event.register((stack, index) -> index == 0 ? ((DyeableLeatherItem)stack.getItem()).getColor(stack) : -1, InspirationsBuilding.coloredBook);

    // bush block colors
    // First the three blocks which never change tint.
    InspirationsBuilding.enlightenedBush.forEach((type, bush) -> {
      if (type != BushType.WHITE) {
        int color = type.getColor();
        event.register((stack, tintIndex) -> tintIndex == 0 ? color : -1, bush);
      }
    });

    // The main one uses the tint of the textured stack
    registerItemColors(itemColors, (stack, tintIndex) -> {
      if (tintIndex != 0) {
        return -1;
      }
      // redirect to block for colors
      Block block = RetexturedHelper.getTexture(stack);
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
