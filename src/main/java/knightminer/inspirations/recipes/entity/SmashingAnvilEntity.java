package knightminer.inspirations.recipes.entity;

import knightminer.inspirations.recipes.InspirationsRecipes;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.FallingBlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class SmashingAnvilEntity extends FallingBlockEntity {
	public SmashingAnvilEntity(EntityType<FallingBlockEntity> entityType, World world) {
		super(entityType, world);
	}

	public SmashingAnvilEntity(World world, double x, double y, double z, BlockState fallingBlockState) {
		super(InspirationsRecipes.SMASHING_ANVIL, world);
		this.fallTile = fallingBlockState;
		this.preventEntitySpawning = true;
		this.setPosition(x, y + (double)((1.0F - this.getHeight()) / 2.0F), z);
		this.setMotion(Vec3d.ZERO);
		this.prevPosX = x;
		this.prevPosY = y;
		this.prevPosZ = z;
		this.setOrigin(new BlockPos(this));
	}

}
