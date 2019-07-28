package knightminer.inspirations.tools;

import knightminer.inspirations.common.ClientProxy;
import knightminer.inspirations.common.Config;
import knightminer.inspirations.tools.client.RenderModArrow;
import knightminer.inspirations.tools.entity.EntityModArrow;
import knightminer.inspirations.tools.item.ItemModArrow.ArrowType;
import knightminer.inspirations.tools.item.ItemWaypointCompass;
import knightminer.inspirations.utility.block.BlockRedstoneCharge;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ToolsClientProxy extends ClientProxy {

	@Override
	public void preInit() {
		super.preInit();
		RenderingRegistry.registerEntityRenderingHandler(EntityModArrow.class, RenderModArrow::new);
	}

	@SubscribeEvent
	public void registerModels(ModelRegistryEvent event) {
		setModelStateMapper(InspirationsTools.redstoneCharge, new StateMap.Builder().ignore(BlockRedstoneCharge.FACING, BlockRedstoneCharge.QUICK).build());

		// items
		registerItemModel(InspirationsTools.redstoneCharger);
		registerItemModel(InspirationsTools.woodenCrook);
		registerItemModel(InspirationsTools.stoneCrook);
		registerItemModel(InspirationsTools.boneCrook);
		registerItemModel(InspirationsTools.blazeCrook);
		registerItemModel(InspirationsTools.witherCrook);
		registerItemModel(InspirationsTools.northCompass);
		registerItemModel(InspirationsTools.barometer);
		registerItemModel(InspirationsTools.photometer);
		registerItemModel(InspirationsTools.waypointCompass);

		// items
		for(ArrowType type : ArrowType.values()) {
			registerItemModel(InspirationsTools.arrow, type.getMeta(), type.getName());
		}
	}

	@SubscribeEvent
	public void registerItemColors(ColorHandlerEvent.Item event) {
		ItemColors itemColors = event.getItemColors();

		// dyed compass
		registerItemColors(itemColors, (stack, tintIndex) -> {
			EnumDyeColor color = EnumDyeColor.byMetadata(stack.getMetadata());
			if(tintIndex == 0) {
				if (color == EnumDyeColor.BLACK) {
					return 0x444444;
				} else if (color == EnumDyeColor.WHITE) {
					return 0xDDDDDD;
				}
				return color.colorValue;
			}
			if(tintIndex == 1) {
				return ItemWaypointCompass.getNeedleColor(color);
			}
			return -1;
		}, InspirationsTools.waypointCompass);
	}

	@SubscribeEvent
	public static void fixShieldTooltip(ItemTooltipEvent event) {
		if (!Config.fixShieldTooltip) {
			return;
		}
		ItemStack stack = event.getItemStack();
		if (stack.getItem() != Items.SHIELD) {
			return;
		}
		NBTTagCompound tags = stack.getSubCompound("BlockEntityTag");
		if (tags != null && tags.hasKey("Patterns") && stack.isItemEnchanted()) {
			NBTTagList patterns = tags.getTagList("Patterns", 10);
			event.getToolTip().add(patterns.tagCount()+1, "");
		}
	}
}
