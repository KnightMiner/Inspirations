package knightminer.inspirations.library.client;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;

public class IgnoreAllStateMapper extends StateMapperBase {

	private final ModelResourceLocation location;
	public IgnoreAllStateMapper(Block block) {
		this.location = new ModelResourceLocation(block.getRegistryName(), "normal");
	}

	@Override
	protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
		return location;
	}
}
