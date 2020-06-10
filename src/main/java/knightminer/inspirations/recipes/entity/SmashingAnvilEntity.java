package knightminer.inspirations.recipes.entity;

import knightminer.inspirations.recipes.InspirationsRecipes;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ConcretePowderBlock;
import net.minecraft.block.FallingBlock;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.item.FallingBlockEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.DirectionalPlaceContext;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tags.FluidTags;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameRules;
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

	// Copy most of the original wholesale, changing some things.
	@Override
	public void tick() {
		if(this.fallTile.isAir()) {
			this.remove();
		} else {
			Block block = this.fallTile.getBlock();
			if(this.fallTime++ == 0) {
				BlockPos blockpos = new BlockPos(this);
				if(this.world.getBlockState(blockpos).getBlock() == block) {
					this.world.removeBlock(blockpos, false);
				} else if(!this.world.isRemote) {
					this.remove();
					return;
				}
			}

			if(!this.hasNoGravity()) {
				this.setMotion(this.getMotion().add(0.0D, -0.04D, 0.0D));
			}

			this.move(MoverType.SELF, this.getMotion());
			if(!this.world.isRemote) {
				BlockPos blockpos = new BlockPos(this);

				if(!this.onGround) {
					if(this.fallTime > 100 && (blockpos.getY() < 1 || blockpos.getY() > 256) || this.fallTime > 600) {
						if(this.shouldDropItem && this.world.getGameRules().getBoolean(GameRules.DO_ENTITY_DROPS)) {
							this.entityDropItem(block);
						}

						this.remove();
					}
				} else {
					BlockState blockstate = this.world.getBlockState(blockpos);
					this.setMotion(this.getMotion().mul(0.7D, -0.5D, 0.7D));
					if(blockstate.getBlock() != Blocks.MOVING_PISTON) {
						this.remove();
						if(!this.dontSetBlock) {
							boolean flag2 = blockstate.isReplaceable(new DirectionalPlaceContext(this.world, blockpos, Direction.DOWN, ItemStack.EMPTY, Direction.UP));
							boolean flag3 = FallingBlock.canFallThrough(this.world.getBlockState(blockpos.down()));
							boolean flag4 = this.fallTile.isValidPosition(this.world, blockpos) && !flag3;
							if(flag2 && flag4) {
								if(this.world.setBlockState(blockpos, this.fallTile, 3)) {
									if(block instanceof FallingBlock) {
										((FallingBlock) block).onEndFalling(this.world, blockpos, this.fallTile, blockstate);
									}
								} else if(this.shouldDropItem && this.world.getGameRules().getBoolean(GameRules.DO_ENTITY_DROPS)) {
									this.entityDropItem(block);
								}
							} else if(this.shouldDropItem && this.world.getGameRules().getBoolean(GameRules.DO_ENTITY_DROPS)) {
								this.entityDropItem(block);
							}
						} else if(block instanceof FallingBlock) {
							// Anvil broke, play sounds.
							((FallingBlock) block).onBroken(this.world, blockpos);
						}
					}
				}
			}

			this.setMotion(this.getMotion().scale(0.98D));
		}
	}
}
