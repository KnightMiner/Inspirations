package knightminer.inspirations.utility;

import java.util.LinkedHashMap;
import java.util.Locale;

import javax.annotation.Nonnull;

import com.google.common.collect.Maps;

import knightminer.inspirations.common.ClientProxy;
import knightminer.inspirations.library.Util;
import knightminer.inspirations.library.client.NameStateMapper;
import knightminer.inspirations.library.client.PropertyStateMapper;
import knightminer.inspirations.utility.block.BlockBricksButton;
import knightminer.inspirations.utility.block.BlockRedstoneBarrel;
import knightminer.inspirations.utility.block.BlockRedstoneCharge;
import knightminer.inspirations.utility.block.BlockTorchLever;
import net.minecraft.block.Block;
import net.minecraft.block.BlockColored;
import net.minecraft.block.BlockLever;
import net.minecraft.block.BlockRedstoneWire;
import net.minecraft.block.BlockTrapDoor;
import net.minecraft.block.BlockTrapDoor.DoorHalf;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.IStateMapper;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class UtilityClientProxy extends ClientProxy {
	private static final ResourceLocation CARPETED_TRAPDOOR = Util.getResource("carpeted_trapdoor");

	@SubscribeEvent
	public void registerModels(ModelRegistryEvent event) {
		setModelStateMapper(InspirationsUtility.torchLever, new TorchLeverStateMapper());
		setModelStateMapper(InspirationsUtility.redstoneTorchLever, new RedstoneTorchLeverStateMapper(false));
		setModelStateMapper(InspirationsUtility.redstoneTorchLeverPowered, new RedstoneTorchLeverStateMapper(true));
		setModelStateMapper(InspirationsUtility.redstoneCharge, new StateMap.Builder().ignore(BlockRedstoneCharge.FACING, BlockRedstoneCharge.QUICK).build());
		setModelStateMapper(InspirationsUtility.bricksButton, new PropertyStateMapper(BlockBricksButton.TYPE));
		IStateMapper carpetedPressurePlate = new NameStateMapper(Util.getResource("carpeted_pressure_plate"));
		setModelStateMapper(InspirationsUtility.carpetedPressurePlate1, carpetedPressurePlate);
		setModelStateMapper(InspirationsUtility.carpetedPressurePlate2, carpetedPressurePlate);

		// items
		registerItemModel(InspirationsUtility.redstoneCharger);

		// blocks
		registerItemModel(InspirationsUtility.torchLever);
		registerItemModel(InspirationsUtility.redstoneTorchLever);
		registerItemModel(InspirationsUtility.redstoneBarrel);

		// uses a property state mapper, so just redirect to the sub files for inventory
		registerItemModel(InspirationsUtility.bricksButton, 0, Util.getResource("bricks_button/bricks"));
		registerItemModel(InspirationsUtility.bricksButton, 1, Util.getResource("bricks_button/nether"));

		registerCarpetedTrapdoorModels(InspirationsUtility.carpetedTrapdoors);
	}

	private void registerCarpetedTrapdoorModels(Block[] blocks) {
		if(blocks != null) {
			for(int i = 0; i < blocks.length; i++) {
				EnumDyeColor color = EnumDyeColor.byMetadata(i);
				registerItemModel(blocks[i], 0, CARPETED_TRAPDOOR, String.format("color=%s,facing=north,shape=bottom", color.getName()));
				setModelStateMapper(blocks[i], new CarpetedTrapdoorStateMapper(color));
			}
		}
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

	/**
	 * Mapper for redstone torch levers, to combine the two blocks as if its all one block
	 */
	private static class RedstoneTorchLeverStateMapper extends StateMapperBase {
		private boolean powered;
		public RedstoneTorchLeverStateMapper(boolean powered) {
			this.powered = powered;
		}

		@Nonnull
		@Override
		protected ModelResourceLocation getModelResourceLocation(@Nonnull IBlockState state) {
			ResourceLocation res = InspirationsUtility.redstoneTorchLever.getRegistryName();
			LinkedHashMap<IProperty<?>, Comparable<?>> map = Maps.newLinkedHashMap(state.getProperties());
			map.put(BlockLever.POWERED, powered);

			return new ModelResourceLocation(res, this.getPropertyString(map));
		}
	}

	/**
	 * Mapper for redstone torch levers, to combine the two blocks as if its all one block
	 */
	private static class CarpetedTrapdoorStateMapper extends StateMapperBase {

		private static final PropertyEnum<TrapdoorShape> SHAPE = PropertyEnum.create("shape", TrapdoorShape.class);
		private EnumDyeColor color;
		public CarpetedTrapdoorStateMapper(EnumDyeColor color) {
			this.color = color;
		}

		@Nonnull
		@Override
		protected ModelResourceLocation getModelResourceLocation(@Nonnull IBlockState state) {
			LinkedHashMap<IProperty<?>, Comparable<?>> map = Maps.newLinkedHashMap();
			map.put(BlockColored.COLOR, color);
			map.put(BlockTrapDoor.FACING, state.getValue(BlockTrapDoor.FACING));

			// combine open and bottom into one property to simplify the blockstate
			TrapdoorShape shape = null;
			boolean open = state.getValue(BlockTrapDoor.OPEN);
			boolean bottom = state.getValue(BlockTrapDoor.HALF) == DoorHalf.BOTTOM;
			if(bottom) {
				shape = open ? TrapdoorShape.BOTTOM_OPEN : TrapdoorShape.BOTTOM;
			} else {
				shape = open ? TrapdoorShape.TOP_OPEN : TrapdoorShape.TOP;
			}
			map.put(SHAPE, shape);

			return new ModelResourceLocation(CARPETED_TRAPDOOR, this.getPropertyString(map));
		}

		private static enum TrapdoorShape implements IStringSerializable {
			BOTTOM,
			BOTTOM_OPEN,
			TOP,
			TOP_OPEN;

			@Override
			public String getName() {
				return name().toLowerCase(Locale.US);
			}
		}
	}
}
