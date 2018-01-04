package knightminer.inspirations.library.client;

import java.util.LinkedHashMap;

import javax.annotation.Nonnull;

import com.google.common.collect.Maps;

import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;

public class BlockItemStateMapper extends StateMapperBase {

	private final IProperty<?>[] ignore;
	public BlockItemStateMapper(IProperty<?>... ignore) {
		this.ignore = ignore;
	}

	@Nonnull
	@Override
	protected ModelResourceLocation getModelResourceLocation(@Nonnull IBlockState state) {
		LinkedHashMap<IProperty<?>, Comparable<?>> map = Maps.newLinkedHashMap(state.getProperties());
		for(IProperty<?> ignored : ignore) {
			map.remove(ignored);
		}

		return new ModelResourceLocation(state.getBlock().getRegistryName(), "block=true," + this.getPropertyString(map));
	}
}
