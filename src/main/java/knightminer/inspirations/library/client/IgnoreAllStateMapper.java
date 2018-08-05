package knightminer.inspirations.library.client;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;

public class IgnoreAllStateMapper extends StateMapperBase {

	public static final IgnoreAllStateMapper INSTANCE = new IgnoreAllStateMapper();
	private IgnoreAllStateMapper() {}

	@Override
	protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
		return new ModelResourceLocation(state.getBlock().getRegistryName(), "normal");
	}
}
