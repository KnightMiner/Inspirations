package knightminer.inspirations.building.block;

import java.util.Random;

import knightminer.inspirations.common.Config;
import net.minecraft.block.Block;
import net.minecraft.block.TrapDoorBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

import javax.annotation.Nonnull;

public class BlockGlassTrapdoor extends TrapDoorBlock {

	public BlockGlassTrapdoor() {
		super(Block.Properties.create(Material.GLASS)
				.hardnessAndResistance(0.3F)
				.sound(SoundType.GLASS)
		);
		// this.disableStats();
	}

	@Override
	public void fillItemGroup(@Nonnull ItemGroup group, @Nonnull NonNullList<ItemStack> items) {
		if (group == ItemGroup.SEARCH || Config.enableGlassDoor.get()) {
			super.fillItemGroup(group, items);
		}
	}
}
