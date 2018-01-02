package knightminer.inspirations.utility.block;

import net.minecraft.block.BlockTrapDoor;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;

public class BlockCarpetedTrapdoor extends BlockTrapDoor {

	public BlockCarpetedTrapdoor() {
		super(Material.WOOD);
		this.setHardness(3.0F);
		this.setSoundType(SoundType.WOOD);
		this.disableStats();
	}
}
