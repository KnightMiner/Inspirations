package knightminer.inspirations.recipes;

import knightminer.inspirations.Inspirations;
import knightminer.inspirations.common.ClientEvents;
import knightminer.inspirations.library.MiscUtil;
import knightminer.inspirations.recipes.block.entity.DyeCauldronBlockEntity;
import knightminer.inspirations.recipes.block.entity.PotionCauldronBlockEntity;
import knightminer.inspirations.recipes.client.BoilingParticle;
import knightminer.inspirations.recipes.item.MixedDyedBottleItem;
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
public class RecipesClientEvents extends ClientEvents {
  @SubscribeEvent
  static void clientSetup(FMLClientSetupEvent event) {
    ItemBlockRenderTypes.setRenderLayer(InspirationsRecipes.honey, RenderType.translucent());
    ItemBlockRenderTypes.setRenderLayer(InspirationsRecipes.honey.getFlowing(), RenderType.translucent());
  }

  @SubscribeEvent
  static void registerBlockColors(ColorHandlerEvent.Block event) {
    BlockColors colors = event.getBlockColors();
    colors.register((state, level, pos, index) -> {
      if (index == 0 && level != null && pos != null) {
        BlockEntity be = level.getBlockEntity(pos);
        if (be != null && be.getType() == InspirationsRecipes.dyeCauldronEntity) {
          return ((DyeCauldronBlockEntity) be).getColor();
        }
      }
      return -1;
    }, InspirationsRecipes.dyeCauldron);
    colors.register((state, level, pos, index) -> {
      if (index == 0 && level != null && pos != null) {
        BlockEntity be = level.getBlockEntity(pos);
        if (be != null && be.getType() == InspirationsRecipes.potionCauldronEntity) {
          Potion potion = ((PotionCauldronBlockEntity) be).getPotion();
          return potion == Potions.EMPTY ? -1 : PotionUtils.getColor(potion);
        }
      }
      return -1;
    }, InspirationsRecipes.potionCauldron);
  }

  @SubscribeEvent
  static void registerParticleFactories(ParticleFactoryRegisterEvent event) {
    Minecraft.getInstance().particleEngine.register(InspirationsRecipes.boilingParticle, BoilingParticle.Factory::new);
  }

  @SubscribeEvent
  static void registerItemColors(ColorHandlerEvent.Item event) {
    ItemColors itemColors = event.getItemColors();

    // dyed water bottles
    InspirationsRecipes.simpleDyedWaterBottle.forEach((color, bottle) -> itemColors.register((stack, index) -> index == 0 ? MiscUtil.getColor(color) : -1, bottle));
    registerItemColors(itemColors, (stack, index) -> index == 0 ? MixedDyedBottleItem.dyeFromBottle(stack) : -1, InspirationsRecipes.mixedDyedWaterBottle);
  }
}
