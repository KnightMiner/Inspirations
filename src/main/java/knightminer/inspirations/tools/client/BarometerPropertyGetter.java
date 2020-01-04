package knightminer.inspirations.tools.client;

import knightminer.inspirations.library.Util;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;

public class BarometerPropertyGetter implements IItemPropertyGetter {
	public static final ResourceLocation ID = Util.getResource("height");

	@Override
	@OnlyIn(Dist.CLIENT)
	public float call(ItemStack stack, @Nullable World world, @Nullable LivingEntity entityIn) {
		Entity entity = entityIn != null ? entityIn : stack.getItemFrame();
		if(entity == null) {
			return 0;
		}
		if(world == null) {
			if(entity.world == null) {
				return 0;
			}
			world = entity.world;
		}

		// if negative position, just 0
		if(entity.posY < 0) {
			return 0;
		}

		double height = entity.posY;
		int worldHeight = world.getHeight();
		if(height > worldHeight) {
			return 1;
		}
		return (float)Math.sqrt(height/worldHeight);
	}
}
