package knightminer.inspirations.building.block;

import knightminer.inspirations.common.Config;
import knightminer.inspirations.common.IHidable;
import net.minecraft.block.Block;
import net.minecraft.block.TrapDoorBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

import javax.annotation.Nonnull;

public class BlockGlassTrapdoor extends TrapDoorBlock implements IHidable {

	public BlockGlassTrapdoor() {
		super(Block.Properties.create(Material.GLASS)
				.hardnessAndResistance(0.3F)
				.sound(SoundType.GLASS)
		);
	}

	@Override
	public boolean isEnabled() {
		return Config.enableGlassDoor.get();
	}
}
