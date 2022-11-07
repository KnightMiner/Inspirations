package knightminer.inspirations.cauldrons;

import knightminer.inspirations.Inspirations;
import knightminer.inspirations.cauldrons.block.entity.DyeCauldronBlockEntity;
import knightminer.inspirations.cauldrons.block.entity.PotionCauldronBlockEntity;
import knightminer.inspirations.cauldrons.client.BoilingParticle;
import knightminer.inspirations.cauldrons.item.MixedDyedBottleItem;
import knightminer.inspirations.common.ClientEvents;
import knightminer.inspirations.library.MiscUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@SuppressWarnings("unused")
@EventBusSubscriber(modid = Inspirations.modID, value = Dist.CLIENT, bus = Bus.MOD)
public class CauldronsClientEvents extends ClientEvents {
  @SubscribeEvent
  static void clientSetup(FMLClientSetupEvent event) {
    ItemBlockRenderTypes.setRenderLayer(InspirationsCaudrons.honey, RenderType.translucent());
    ItemBlockRenderTypes.setRenderLayer(InspirationsCaudrons.honey.getFlowing(), RenderType.translucent());
  }

  @SubscribeEvent
  static void registerBlockColors(ColorHandlerEvent.Block event) {
    BlockColors colors = event.getBlockColors();
    colors.register((state, level, pos, index) -> {
      if (index == 0 && level != null && pos != null) {
        BlockEntity be = level.getBlockEntity(pos);
        if (be != null && be.getType() == InspirationsCaudrons.dyeCauldronEntity) {
          return ((DyeCauldronBlockEntity) be).getColor();
        }
      }
      return -1;
    }, InspirationsCaudrons.dyeCauldron);
    colors.register((state, level, pos, index) -> {
      if (index == 0 && level != null && pos != null) {
        BlockEntity be = level.getBlockEntity(pos);
        if (be != null && be.getType() == InspirationsCaudrons.potionCauldronEntity) {
          Potion potion = ((PotionCauldronBlockEntity) be).getPotion();
          return potion == Potions.EMPTY ? -1 : PotionUtils.getColor(potion);
        }
      }
      return -1;
    }, InspirationsCaudrons.potionCauldron);
  }

  @SubscribeEvent
  static void registerParticleFactories(ParticleFactoryRegisterEvent event) {
    Minecraft.getInstance().particleEngine.register(InspirationsCaudrons.boilingParticle, BoilingParticle.Factory::new);
  }

  @SubscribeEvent
  static void registerItemColors(ColorHandlerEvent.Item event) {
    ItemColors itemColors = event.getItemColors();

    // dyed water bottles
    InspirationsCaudrons.simpleDyedWaterBottle.forEach((color, bottle) -> itemColors.register((stack, index) -> index == 0 ? MiscUtil.getColor(color) : -1, bottle));
    registerItemColors(itemColors, (stack, index) -> index == 0 ? MixedDyedBottleItem.dyeFromBottle(stack) : -1, InspirationsCaudrons.mixedDyedWaterBottle);
  }
}
