package knightminer.inspirations.redstone;

import java.util.LinkedHashMap;

import javax.annotation.Nonnull;

import com.google.common.collect.Maps;

import knightminer.inspirations.common.ClientProxy;
import knightminer.inspirations.redstone.block.BlockTorchLever;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class RedstoneClientProxy extends ClientProxy {
	@SubscribeEvent
	public void registerModels(ModelRegistryEvent event) {
		ModelLoader.setCustomStateMapper(InspirationsRedstone.torchLever, new TorchLeverStateMapper());

		registerItemModel(InspirationsRedstone.torchLever);
	}

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
