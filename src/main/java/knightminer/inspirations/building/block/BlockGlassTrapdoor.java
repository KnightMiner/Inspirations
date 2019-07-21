package knightminer.inspirations.building.block;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.TrapDoorBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;

public class BlockGlassTrapdoor extends TrapDoorBlock {

	public BlockGlassTrapdoor() {
		super(Block.Properties.create(Material.GLASS)
			.hardnessAndResistance(0.3F)
			.sound(SoundType.GLASS)
		);
		// this.disableStats();
	}
}
