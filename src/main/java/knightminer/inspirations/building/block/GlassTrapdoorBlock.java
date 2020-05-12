package knightminer.inspirations.building.block;

import knightminer.inspirations.common.Config;
import knightminer.inspirations.common.IHidable;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.TrapDoorBlock;
import net.minecraft.block.material.Material;

public class GlassTrapdoorBlock extends TrapDoorBlock implements IHidable {

	public GlassTrapdoorBlock() {
		super(Block.Properties.create(Material.GLASS)
				.hardnessAndResistance(0.3F)
				.sound(SoundType.GLASS)
				.notSolid()
		);
	}

	@Override
	public boolean isEnabled() {
		return Config.enableGlassDoor.get();
	}
}
