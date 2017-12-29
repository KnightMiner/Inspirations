package knightminer.inspirations.utility;

import java.util.LinkedHashMap;

import javax.annotation.Nonnull;

import com.google.common.collect.Maps;

import knightminer.inspirations.common.ClientProxy;
import knightminer.inspirations.library.Util;
import knightminer.inspirations.library.client.PropertyStateMapper;
import knightminer.inspirations.utility.block.BlockBricksButton;
import knightminer.inspirations.utility.block.BlockRedstoneBarrel;
import knightminer.inspirations.utility.block.BlockRedstoneCharge;
import knightminer.inspirations.utility.block.BlockTorchLever;
import net.minecraft.block.BlockRedstoneWire;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class UtilityClientProxy extends ClientProxy {

	@SubscribeEvent
	public void registerModels(ModelRegistryEvent event) {
		setModelStateMapper(InspirationsUtility.torchLever, new TorchLeverStateMapper());
		setModelStateMapper(InspirationsUtility.redstoneCharge, new StateMap.Builder().ignore(BlockRedstoneCharge.FACING, BlockRedstoneCharge.QUICK).build());
		setModelStateMapper(InspirationsUtility.bricksButton, new PropertyStateMapper(BlockBricksButton.TYPE));

		// items
		registerItemModel(InspirationsUtility.redstoneCharger);

		// blocks
		registerItemModel(InspirationsUtility.torchLever);
		registerItemModel(InspirationsUtility.redstoneBarrel);

		// uses a property state mapper, so just redirect to the sub files for inventory
		registerItemModel(InspirationsUtility.bricksButton, 0, Util.getResource("bricks_button/bricks"));
		registerItemModel(InspirationsUtility.bricksButton, 1, Util.getResource("bricks_button/nether"));
	}

	@SubscribeEvent
	public void registerBlockColors(ColorHandlerEvent.Block event) {
		BlockColors blockColors = event.getBlockColors();

		// coloring redstone inside the barrel
		registerBlockColors(blockColors, (state, world, pos, tintIndex) -> {
			if(tintIndex == 1) {
				int level = state.getValue(BlockRedstoneBarrel.LEVEL);
				if(level > 0) {
					return BlockRedstoneWire.colorMultiplier(level);
				}
			}

			return -1;
		}, InspirationsUtility.redstoneBarrel);
	}

	/**
	 * Mapper for torch levers, to simplify rotations for the floor state
	 */
	private static class TorchLeverStateMapper extends StateMapperBase {
		@Nonnull
		@Override
		protected ModelResourceLocation getModelResourceLocation(@Nonnull IBlockState state) {
			ResourceLocation base = state.getBlock().getRegistryName();
			LinkedHashMap<IProperty<?>, Comparable<?>> map = Maps.newLinkedHashMap(state.getProperties());
			String suffix = "";
			// if up, use the up file and ignore facing
			if(state.getValue(BlockTorchLever.FACING) == EnumFacing.UP) {
				map.remove(BlockTorchLever.FACING);
			} else {
				// otherwise ignore side
				map.remove(BlockTorchLever.SIDE);
				suffix = "_wall";
			}
			ResourceLocation res = new ResourceLocation(base.getResourceDomain(), base.getResourcePath() + suffix);
			return new ModelResourceLocation(res, this.getPropertyString(map));
		}
	}
}
