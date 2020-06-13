package knightminer.inspirations.recipes.entity;

import knightminer.inspirations.library.recipe.RecipeTypes;
import knightminer.inspirations.library.recipe.anvil.AnvilInventory;
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
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nonnull;
import java.util.Comparator;
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
		this.setMotion(Vector3d.ZERO);
		this.prevPosX = x;
		this.prevPosY = y;
		this.prevPosZ = z;
		this.setOrigin(this.getPosition());
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
			BlockPos blockpos = this.getPosition();
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

		Vector3d motion = this.getMotion();
		move(MoverType.SELF, motion);
		if(!world.isRemote) {
			BlockPos blockpos = this.getPosition();

			if(!onGround) {
				if(fallTime > 100 && (blockpos.getY() < 1 || blockpos.getY() > 256) || fallTime > 600) {
					tryDropItem();
					remove();
				}
			} else {
				// On the ground, place the block.
				this.setMotion(getMotion().mul(0.7D, -0.5D, 0.7D));

				if(dontSetBlock) { // It's a destroyed anvil, play sounds.
					if(block instanceof FallingBlock) {
						((FallingBlock) block).onBroken(this.world, blockpos, this);
					}
					remove();
				} else {
					BlockPos below = blockpos.down();
					// Original behaviour - only placed if replaceable && canBeHere.
					// Instead, if we destroy the block retain the entity.
					if(smashBlock(world, below, world.getBlockState(below)) == SmashResult.PASSTHROUGH) {
						// Restore velocity before the move() call, to preserve momentum.
						this.setMotion(motion);
					} else {
						placeAnvil(blockpos);
					}
				}
			}
		}
		this.setMotion(this.getMotion().scale(0.98D));
	}

	/**
	 * We landed, set the block if possible otherwise drop the item.
	 * This follows vanilla's rules for how it can be placed.
	 * */
	private void placeAnvil(BlockPos blockpos) {
		BlockState blockstate = world.getBlockState(blockpos);
		boolean replaceable = blockstate.isReplaceable(new DirectionalPlaceContext(world, blockpos, Direction.DOWN, ItemStack.EMPTY, Direction.UP));
		boolean canFallThrough = FallingBlock.canFallThrough(world.getBlockState(blockpos.down()));
		boolean canBeHere = this.fallTile.isValidPosition(this.world, blockpos) && !canFallThrough;
		if(replaceable && canBeHere) {
			if(!world.setBlockState(blockpos, this.fallTile, 3)) {
				tryDropItem();
			}
		} else {
			tryDropItem();
		}
		remove();
	}

	/* If allowed, drop our item right here. */
	private void tryDropItem() {
		if(this.shouldDropItem && this.world.getGameRules().getBoolean(GameRules.DO_ENTITY_DROPS)) {
			this.entityDropItem(this.fallTile.getBlock());
		}
	}

	/**
	 * Smashes the provided block by anvil.
	 * @param pos The position of the block.
	 * @param state The existing block at the position.
	 * @param world The world the block is in.
	 * @return The result of the operation.
	 */
	public static SmashResult smashBlock(World world, BlockPos pos, BlockState state) {
		// Always pass harmlessly through air.
		if(state.getBlock().isAir(state, world, pos)) {
			return SmashResult.PASSTHROUGH;
		}
		// If the block is unbreakable, always fail.
		if(state.getBlockHardness(world, pos) == -1) {
			return SmashResult.FAIL;
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
		
		return world.getRecipeManager()
				.getRecipes(RecipeTypes.ANVIL, inv, world)
				.stream()
				.max(Comparator.comparingInt(a -> a.getIngredients().size()))
				.map((recipe) -> {
			BlockState transformation = recipe.getBlockResult(inv);

			recipe.consumeItemEnts(items);

			// if the result is air, break the block
			if(transformation.getBlock() == Blocks.AIR) {
				world.destroyBlock(pos, true);
				return SmashResult.PASSTHROUGH;
			} else {
				// breaking particles
				world.playEvent(2001, pos, Block.getStateId(state));
				world.setBlockState(pos, transformation);
				return SmashResult.TRANSFORM;
			}
		}).orElse(SmashResult.FAIL);
	}

	enum SmashResult {
		/** Broke the block, fall through it. */
		PASSTHROUGH,
		/** No recipe found. */
		FAIL,
		/** Converted the block. */
		TRANSFORM
	}
}

