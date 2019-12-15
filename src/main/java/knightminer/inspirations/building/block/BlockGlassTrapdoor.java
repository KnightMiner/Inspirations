package knightminer.inspirations.building.block;

import net.minecraft.block.BlockTrapDoor;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;

import java.util.Random;

public class BlockGlassTrapdoor extends BlockTrapDoor {

	public BlockGlassTrapdoor() {
		super(Material.GLASS);
		this.setHardness(0.3F);
		this.setSoundType(SoundType.GLASS);
		this.disableStats();
	}

	/**
	 * Returns the quantity of items to drop on block destruction.
	 */
	@Override
	public int quantityDropped(Random random) {
		return 0;
	}

	@Deprecated
	@Override
	protected boolean canSilkHarvest() {
		return true;
	}

}
