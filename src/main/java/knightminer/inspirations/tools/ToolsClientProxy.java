package knightminer.inspirations.tools;

import knightminer.inspirations.common.ClientProxy;
import knightminer.inspirations.tools.client.RenderModArrow;
import knightminer.inspirations.tools.entity.RedstoneArrow;
import knightminer.inspirations.tools.item.ItemWaypointCompass;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.item.DyeColor;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import java.util.Map;

public class ToolsClientProxy extends ClientProxy {

	@SubscribeEvent
	public void clientSetup(FMLClientSetupEvent event) {
		super.preInit();
		RenderingRegistry.registerEntityRenderingHandler(RedstoneArrow.class, RenderModArrow::new);
	}

	@SubscribeEvent
	public void registerItemColors(ColorHandlerEvent.Item event) {
		ItemColors itemColors = event.getItemColors();

		// dyed compass
        for (Map.Entry<DyeColor, ItemWaypointCompass> entry: InspirationsTools.waypointCompasses.entrySet()) {
            ItemWaypointCompass compass = entry.getValue();
            DyeColor color = compass.getColor();

            int needleColor = ItemWaypointCompass.getNeedleColor(color);
            int bodyColor;
            if (color == DyeColor.BLACK) {
                bodyColor = 0x444444;
            } else if (color == DyeColor.WHITE) {
                bodyColor = 0xDDDDDD;
            } else {
                bodyColor = color.colorValue;
            }
            itemColors.register((stack, tintIndex) -> {
                switch (tintIndex) {
                    case 0:
                        return bodyColor;
                    case 1:
                        return needleColor;
                    default:
                        return -1;
                }
            }, compass);
        }
	}
}
