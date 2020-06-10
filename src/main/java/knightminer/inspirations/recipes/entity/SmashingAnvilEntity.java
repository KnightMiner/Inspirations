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
				BlockPos blockpos1 = new BlockPos(this);
				boolean flag = this.fallTile.getBlock() instanceof ConcretePowderBlock;
				boolean flag1 = flag && this.world.getFluidState(blockpos1).isTagged(FluidTags.WATER);
				double d0 = this.getMotion().lengthSquared();
				if(flag && d0 > 1.0D) {
					BlockRayTraceResult blockraytraceresult = this.world.rayTraceBlocks(new RayTraceContext(new Vec3d(this.prevPosX, this.prevPosY, this.prevPosZ), this.getPositionVec(), RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.SOURCE_ONLY, this));
					if(blockraytraceresult.getType() != RayTraceResult.Type.MISS && this.world.getFluidState(blockraytraceresult.getPos()).isTagged(FluidTags.WATER)) {
						blockpos1 = blockraytraceresult.getPos();
						flag1 = true;
					}
				}

				if(!this.onGround && !flag1) {
					if(!this.world.isRemote && (this.fallTime > 100 && (blockpos1.getY() < 1 || blockpos1.getY() > 256) || this.fallTime > 600)) {
						if(this.shouldDropItem && this.world.getGameRules().getBoolean(GameRules.DO_ENTITY_DROPS)) {
							this.entityDropItem(block);
						}

						this.remove();
					}
				} else {
					BlockState blockstate = this.world.getBlockState(blockpos1);
					this.setMotion(this.getMotion().mul(0.7D, -0.5D, 0.7D));
					if(blockstate.getBlock() != Blocks.MOVING_PISTON) {
						this.remove();
						if(!this.dontSetBlock) {
							boolean flag2 = blockstate.isReplaceable(new DirectionalPlaceContext(this.world, blockpos1, Direction.DOWN, ItemStack.EMPTY, Direction.UP));
							boolean flag3 = FallingBlock.canFallThrough(this.world.getBlockState(blockpos1.down())) && (!flag || !flag1);
							boolean flag4 = this.fallTile.isValidPosition(this.world, blockpos1) && !flag3;
							if(flag2 && flag4) {
								if(this.fallTile.has(BlockStateProperties.WATERLOGGED) && this.world.getFluidState(blockpos1).getFluid() == Fluids.WATER) {
									this.fallTile = this.fallTile.with(BlockStateProperties.WATERLOGGED, Boolean.valueOf(true));
								}

								if(this.world.setBlockState(blockpos1, this.fallTile, 3)) {
									if(block instanceof FallingBlock) {
										((FallingBlock) block).onEndFalling(this.world, blockpos1, this.fallTile, blockstate);
									}

									if(this.tileEntityData != null && this.fallTile.hasTileEntity()) {
										TileEntity tileentity = this.world.getTileEntity(blockpos1);
										if(tileentity != null) {
											CompoundNBT compoundnbt = tileentity.write(new CompoundNBT());

											for(String s : this.tileEntityData.keySet()) {
												INBT inbt = this.tileEntityData.get(s);
												if(!"x".equals(s) && !"y".equals(s) && !"z".equals(s)) {
													compoundnbt.put(s, inbt.copy());
												}
											}

											tileentity.read(compoundnbt);
											tileentity.markDirty();
										}
									}
								} else if(this.shouldDropItem && this.world.getGameRules().getBoolean(GameRules.DO_ENTITY_DROPS)) {
									this.entityDropItem(block);
								}
							} else if(this.shouldDropItem && this.world.getGameRules().getBoolean(GameRules.DO_ENTITY_DROPS)) {
								this.entityDropItem(block);
							}
						} else if(block instanceof FallingBlock) {
							((FallingBlock) block).onBroken(this.world, blockpos1);
						}
					}
				}
			}

			this.setMotion(this.getMotion().scale(0.98D));
		}
	}
}
