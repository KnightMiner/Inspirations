package knightminer.inspirations.recipes.entity;

import knightminer.inspirations.library.InspirationsRegistry;
import knightminer.inspirations.library.recipe.anvil.AnvilInventory;
import knightminer.inspirations.library.recipe.anvil.AnvilRecipe;
import knightminer.inspirations.recipes.InspirationsRecipes;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FallingBlock;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.item.FallingBlockEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.DirectionalPlaceContext;
import net.minecraft.item.ItemStack;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.stream.Collectors;

public class SmashingAnvilEntity extends FallingBlockEntity implements IEntityAdditionalSpawnData {
	public SmashingAnvilEntity(EntityType<SmashingAnvilEntity> entityType, World world) {
		super(entityType, world);
	}

	public SmashingAnvilEntity(World world, double x, double y, double z, BlockState fallingBlockState) {
		super(InspirationsRecipes.smashingAnvil, world);
		this.fallTile = fallingBlockState;
		this.preventEntitySpawning = true;
		this.setPosition(x, y, z);
		this.setMotion(Vec3d.ZERO);
		this.prevPosX = x;
		this.prevPosY = y;
		this.prevPosZ = z;
		this.setOrigin(new BlockPos(this));
		// From anvil:
		setHurtEntities(true);
	}

	@Nonnull
	@Override
	public IPacket<?> createSpawnPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}

	// We can't use vanilla's spawn data method, so implement it here.
	@Override
	public void writeSpawnData(PacketBuffer buffer) {
		buffer.writeInt(Block.getStateId(fallTile));
	}

	@Override
	public void readSpawnData(PacketBuffer additionalData) {
		fallTile = Block.getStateById(additionalData.readInt());
	}

	// Copy most of the original wholesale, changing some things.
	@Override
	public void tick() {
		if(this.fallTile.isAir()) {
			this.remove();
		}

		Block block = this.fallTile.getBlock();
		if(fallTime++ == 0) {
			BlockPos blockpos = new BlockPos(this);
			if(world.getBlockState(blockpos).getBlock() == block) {
				world.removeBlock(blockpos, false);
			} else if(!world.isRemote) {
				this.remove();
				return;
			}
		}

		if(!hasNoGravity()) {
			setMotion(this.getMotion().add(0.0D, -0.04D, 0.0D));
		}

		move(MoverType.SELF, this.getMotion());
		if(!world.isRemote) {
			BlockPos blockpos = new BlockPos(this);

			if(!onGround) {
				if(fallTime > 100 && (blockpos.getY() < 1 || blockpos.getY() > 256) || fallTime > 600) {
					tryDropItem(block);
					this.remove();
				}
			} else {
				// On the ground, place the block.
				BlockState blockstate = world.getBlockState(blockpos);
				this.setMotion(getMotion().mul(0.7D, -0.5D, 0.7D));

				this.remove();
				if(dontSetBlock) { // It's a destroyed anvil, play sounds.
					if(block instanceof FallingBlock) {
						((FallingBlock) block).onBroken(this.world, blockpos);
					}
				} else {
					BlockPos below = blockpos.down();
					BlockState belowBlock = world.getBlockState(below);

					boolean replaceable = blockstate.isReplaceable(new DirectionalPlaceContext(world, blockpos, Direction.DOWN, ItemStack.EMPTY, Direction.UP));
					boolean canFallThrough = FallingBlock.canFallThrough(belowBlock);
					boolean canBeHere = this.fallTile.isValidPosition(this.world, blockpos) && !canFallThrough;
					if(replaceable && canBeHere) {
						if(world.setBlockState(blockpos, this.fallTile, 3)) {
							smashBlock(world, below, belowBlock);
						} else {
							tryDropItem(block);
						}
					} else {
						tryDropItem(block);
					}
				}
			}
		}
		this.setMotion(this.getMotion().scale(0.98D));
	}

	// Common code in many places inside tick().
	private void tryDropItem(Block block) {
		if(this.shouldDropItem && this.world.getGameRules().getBoolean(GameRules.DO_ENTITY_DROPS)) {
			this.entityDropItem(block);
		}
	}

	public boolean smashBlock(World world, BlockPos pos, BlockState state) {
		// if we started on air, just return true
		if(state.getBlock() == Blocks.AIR) {
			return true;
		}
		// if the block is unbreakable, leave it
		if(state.getBlockHardness(world, pos) == -1) {
			return false;
		}

		// Find all the items on this block, plus the one above (where the anvil is).
		List<ItemEntity> items = world.getEntitiesWithinAABB(ItemEntity.class, new AxisAlignedBB(
			pos.getX(), pos.getY() + 0.5, pos.getZ(),
			pos.getX() + 1, pos.getY() + 2, pos.getZ() + 1)
		);

		// Dummy inventory, used to pass the items/state to the recipes.
		AnvilInventory inv = new AnvilInventory(
			items.stream().map(ItemEntity::getItem).collect(Collectors.toList()),
			state
		);
		AnvilRecipe recipe = world.getRecipeManager()
				.getRecipe(InspirationsRegistry.ANVIL_RECIPE_TYPE, inv, world).orElse(null);

		if(recipe == null) {
			return false;
		}

		// Kill the entities used in the recipe.
		for(int i = 0; i < items.size(); i++) {
			if (inv.used[i]) {
				items.get(i).remove();
			}
		}

		BlockState transformation = recipe.getBlockResult(inv);

		// if the result is air, break the block
		if(transformation.getBlock() == Blocks.AIR) {
			world.destroyBlock(pos, true);
		} else {
			// breaking particles
			world.playEvent(2001, pos, Block.getStateId(state));
			world.setBlockState(pos, transformation);
		}
		return true;
	}
}

