package knightminer.inspirations.recipes.tileentity;

import knightminer.inspirations.Inspirations;
import knightminer.inspirations.common.Config;
import knightminer.inspirations.library.InspirationsRegistry;
import knightminer.inspirations.library.Util;
import knightminer.inspirations.library.recipe.cauldron.ICauldronRecipe;
import knightminer.inspirations.library.recipe.cauldron.ICauldronRecipe.CauldronState;
import knightminer.inspirations.recipes.block.BlockEnhancedCauldron;
import knightminer.inspirations.recipes.block.BlockEnhancedCauldron.CauldronContents;
import knightminer.inspirations.recipes.tank.CauldronTank;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CauldronBlock;
import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Items;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.potion.EffectInstance;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtils;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.data.ModelDataMap;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class TileCauldron extends TileEntity {
	public static final DamageSource DAMAGE_BOIL = new DamageSource(Util.prefix("boiling")).setDamageBypassesArmor();

	private CauldronState state;
	private CauldronTank tank;

	public TileCauldron() {
		super(TileEntityType.BARREL);
		this.state = CauldronState.WATER;
		this.tank = new CauldronTank(this);
	}

	@Nonnull
	public CauldronContents getContentType() {
		if(state.getFluid() != null) {
			return CauldronContents.FLUID;
		}
		if(state.getColor() > -1) {
			return CauldronContents.DYE;
		}
		if(state.getPotion() != null) {
			return CauldronContents.POTION;
		}

		return CauldronContents.FLUID;
	}

	/**
	 * Checks if this TE currently has water in it
	 * @return
	 */
	public boolean isWater() {
		return state.isWater();
	}

	/**
	 * Gets the current cauldron state
	 * @return  current state
	 */
	public CauldronState getState() {
		return state;
	}

	/**
	 * Returns the current color for tinting
	 * @return  block colors color
	 */
	public int getColor() {
		switch(getContentType()) {
			case DYE:
				return state.getColor();
			case POTION:
				return PotionUtils.getPotionColor(state.getPotion());
		}

		Fluid fluid = state.getFluid();
		if(fluid != null) {
			return state.getFluid().getColor();
		}

		return -1;
	}

	@Nonnull
	@Override
	public IModelData getModelData() {
		// just pull the texture right from the fluid
		if(getFluidLevel() > 0 && this.state != CauldronState.WATER && getContentType() == CauldronContents.FLUID) {
			Fluid fluid = this.state.getFluid();
			if(fluid != null) {
				return new ModelDataMap.Builder()
						.withInitial(BlockEnhancedCauldron.TEXTURE, fluid.getStill().toString())
						.build();
			}
		}
		return EmptyModelData.INSTANCE;
	}


	/* behavior */

	/**
	 * Method to run cauldron interaction code. Used for both TileCauldron and simple cauldron
	 * @return  True if successful, false for pass
	 */
	public static boolean interact(World world, BlockPos pos, BlockState blockState, PlayerEntity player, Hand hand) {
		// ensure we have a stack
		ItemStack stack = player.getHeldItem(hand);
		if(stack.isEmpty()) {
			return false;
		}

		// grab the TE if extended
		TileCauldron cauldron = null;
		CauldronState state = CauldronState.WATER;
		boolean boiling = false;
		Block block = blockState.getBlock();
		if(Config.enableExtendedCauldron.get() && block instanceof BlockEnhancedCauldron) {
			TileEntity te = world.getTileEntity(pos);
			if(te instanceof TileCauldron) {
				cauldron = (TileCauldron) te;
				state = cauldron.state;
				boiling = blockState.get(BlockEnhancedCauldron.BOILING);
			}
		} else {
			state = InspirationsRegistry.getCauldronState(blockState);
			boiling = InspirationsRegistry.isCauldronFire(world.getBlockState(pos.down()));
		}

		// other properties
		int level = BlockEnhancedCauldron.getCauldronLevel(blockState);

		// grab recipe
		ICauldronRecipe recipe = InspirationsRegistry.getCauldronResult(stack, boiling, level, state);
		if(recipe != null) {
			// update properties based on the recipe
			if(!world.isRemote) {
				// grab state first since we may need to back out
				CauldronState newState = recipe.getState(stack, boiling, level, state);

				// if its not a TE, stop right here and disallow any recipes which do not return water
				if(!Config.enableExtendedCauldron.get() && !CauldronState.WATER.matches(newState)) {
					return true;
				}

				// play sound
				SoundEvent sound = recipe.getSound(stack, boiling, level, state);
				if(sound != null) {
					world.playSound(null, pos, sound, SoundCategory.BLOCKS, recipe.getVolume(sound), 1.0F);
				}

				// update level
				int newLevel = recipe.getLevel(level);
				if(newLevel != level || !state.matches(newState)) {
					// overrides for full cauldrons, assuming we started with a "valid cauldron", in this context an iron one
					if(newLevel == InspirationsRegistry.getCauldronMax() && InspirationsRegistry.isNormalCauldron(blockState) && InspirationsRegistry.hasFullCauldron(newState)) {
						world.setBlockState(pos, InspirationsRegistry.getFullCauldron(newState));
						cauldron = null;
					} else {
						// if it was not a cauldron before, replace it with a vanilla cauldron
						if (!(block instanceof CauldronBlock)) {
							((CauldronBlock)Blocks.CAULDRON).setWaterLevel(world, pos, Blocks.CAULDRON.getDefaultState(), newLevel);

							// missing the tile entity
							if(Config.enableExtendedCauldron.get()) {
								TileEntity te = world.getTileEntity(pos);
								if(te instanceof TileCauldron) {
									cauldron = (TileCauldron)te;
								}
							}
						} else {
							((CauldronBlock)block).setWaterLevel(world, pos, blockState, newLevel);
						}
						if(newLevel == 0) {
							newState = CauldronState.WATER;
						}
					}
				}

				// update the state
				if(cauldron != null) {
					cauldron.setState(newState, true);
				}

				// result
				ItemStack result = recipe.getResult(stack, boiling, level, state);
				// update held item
				if(!player.isCreative()) {
					ItemStack container = recipe.getContainer(stack);
					int original = stack.getCount();

					// transform input
					ItemStack transform = recipe.transformInput(stack, boiling, level, state);
					// if nothing left, set container to main hand
					if(transform.isEmpty()) {
						if(!container.isEmpty()) {
							container.setCount(container.getCount() * original);
							player.setHeldItem(hand, container);
						}
					} else {
						// else give container to player
						player.setHeldItem(hand, transform);
						if(!container.isEmpty()) {
							container.setCount(container.getCount() * (original - transform.getCount()));
							ItemHandlerHelper.giveItemToPlayer(player, container, player.inventory.currentItem);
						}
					}
				}
				// and give the new item to the player
				if(!result.isEmpty()) {
					ItemHandlerHelper.giveItemToPlayer(player, result, player.inventory.currentItem);
				}
			}

			return true;
		}

		// if we have water, allow default actions to run, otherwise block
		if(state.isWater()) {
			// though skip default interactions for water bottles and water buckets if not pure water
			Item item = stack.getItem();
			return state != CauldronState.WATER && (item == Items.POTION || item == Items.WATER_BUCKET);
		}

		return true;
	}

	private static final String TAG_CAULDRON_CRAFTED = "cauldron_crafted";
	private static final String TAG_CAULDRON_COOLDOWN = "cauldron_cooldown";

	/**
	 * Called when an entity collides with the cauldron
	 * @param entity  Entity that collided
	 * @param level   Cauldron level
	 * @return  New cauldron level after the collision
	 */
	public int onEntityCollide(Entity entity, int level, BlockState currentState) {
		// if an entity item, try crafting with it
		if(entity instanceof ItemEntity) {
			// skip items that we have already processed
			ItemEntity entityItem = (ItemEntity) entity;
			CompoundNBT entityTags = entity.getEntityData();
			// if it was tagged, skip it
			if(entityTags.getBoolean(TAG_CAULDRON_CRAFTED)) {
				return level;
			} else {
				// otherwise, if it has a cooldown, reduce the cooldown
				int cooldown = entityTags.getInt(TAG_CAULDRON_COOLDOWN);
				if(cooldown > 0) {
					entityTags.putInt(TAG_CAULDRON_COOLDOWN, cooldown - 1);
					return level;
				}
			}

			// try and find a recipe
			boolean boiling = currentState.get(BlockEnhancedCauldron.BOILING);
			ItemStack stack = entityItem.getItem();
			ICauldronRecipe recipe = InspirationsRegistry.getCauldronResult(stack, boiling, level, state);
			if(recipe != null) {
				CauldronState state = this.state;

				// play sound first, so its not looped
				SoundEvent sound = recipe.getSound(stack, boiling, level, state);
				if(sound != null) {
					world.playSound(null, pos, sound, SoundCategory.BLOCKS, recipe.getVolume(sound), 1.0F);
				}

				int matches = 0;
				int oldCount;
				do {
					// update properties based on the recipe
					CauldronState newState = recipe.getState(stack, boiling, level, state);

					// update level
					level = recipe.getLevel(level);
					if(level == 0) {
						newState = CauldronState.WATER;
					}

					// spawn the new item in the world
					ItemStack result = recipe.getResult(stack, boiling, level, state);
					if(!result.isEmpty()) {
						spawnItem(result, entityItem);
					}

					// grab container data
					ItemStack container = recipe.getContainer(stack);
					oldCount = stack.getCount();

					// update the stack for later
					stack = recipe.transformInput(stack, boiling, level, state);

					// add container
					if (!container.isEmpty()) {
						container.setCount(container.getCount() * (oldCount - stack.getCount()));
						spawnItem(container, entityItem);
					}

					// update the state for the next round
					state = newState;
					matches++;
				} while(recipe.matches(stack, boiling, level, state) && matches < 10);

				// safety check, recipes should never really match more than 4 times, but 10 just in case
				// basically, they should either be lowering/raising the level (max 4 times), or changing the state (not repeatable)
				if(matches == 10) {
					Inspirations.log.warn("Recipe '{}' matched too many times in a single tick. Either the level or the state should change to make it no longer match.", recipe);
				}

				// kill the old item, or update its item
				if(stack.isEmpty()) {
					entityItem.remove();
				} else {
					entityItem.setItem(stack);
					entityTags.putBoolean(TAG_CAULDRON_CRAFTED, true);
				}

				// if the state changed, update that too
				if(!state.matches(this.state)) {
					this.state = state;
					world.notifyBlockUpdate(pos, currentState, currentState, 2);
				}
			} else {
				// set a cooldown to reduce lag, so we are not searching the registry every tick
				// we do not just set crafted as that would prevent dropping in items one at a time where multiple are required
				entityTags.putInt(TAG_CAULDRON_COOLDOWN, 60);
			}

			// otherwise apply fluid special effects
		} else if(level > 0) {
			switch(this.getContentType()) {
				case FLUID:
					// water estinguishs fire
					if(this.isWater()) {
						if(entity.isBurning()) {
							entity.extinguish();
							level = level - 1;
						}
					} else {
						// hot fluids set fire to the entity
						Fluid fluid = state.getFluid();
						if(fluid.getTemperature() > 450 && !entity.isImmuneToFire()) {
							entity.attackEntityFrom(DamageSource.LAVA, 4.0F);
							entity.setFire(15);
							break;
						}
					}
					// continue for boiling
				case DYE:
					// if the cauldron is boiling, boiling the entity
					if (currentState.get(BlockEnhancedCauldron.BOILING)) {
						entity.attackEntityFrom(DAMAGE_BOIL, 2.0F);
					}
					break;
				case POTION:
					// potions apply potion effects
					if(entity instanceof LivingEntity) {
						LivingEntity living = (LivingEntity) entity;
						List<EffectInstance> effects = state.getPotion().getEffects();
						// if any of the effects are not currently on the player, apply it and lower the level
						if(effects.stream().anyMatch(effect -> !living.isPotionActive(effect.getPotion()))) {
							for(EffectInstance effect : effects) {
								if (effect.getPotion().isInstant()) {
									effect.getPotion().affectEntity(null, null, living, effect.getAmplifier(), 1.0D);
								} else {
									living.addPotionEffect(new EffectInstance(effect));
								}
							}
							level = level - 1;
						}
					}
					break;
			}
		}
		return level;
	}

	private void spawnItem(ItemStack stack, ItemEntity base) {
		ItemEntity entityItem = new ItemEntity(world, base.posX, base.posY, base.posZ, stack);

		// tag the entity so it does not craft again
		// prevents something like a water bottle from emptying and filling constantly
		entityItem.getEntityData().putBoolean(TAG_CAULDRON_CRAFTED, true);
		world.addEntity(entityItem);
	}

	/**
	 * Called when the cauldron is broken
	 * @param pos    Position of the cauldron
	 * @param level  Cauldron level
	 */
	public void onBreak(BlockPos pos, int level) {
		switch(getContentType()) {
			case FLUID:
				Block block = state.getFluid().getBlock();
				if(block != null) {
					// switch to flowing states
					if(block == Blocks.WATER) {
						block = Fluids.FLOWING_WATER;
					} else if(block == Blocks.LAVA) {
						block = Blocks.FLOWING_LAVA;
					}

					// height varies based on what is left. Will place a source if the cauldron is full
					if(level == (Config.enableBiggerCauldron.get() ? 4 : 3)) {
						world.setBlockState(pos, block.getDefaultState());
					}
				}
				break;
			case POTION:
				Potion potion = state.getPotion();
				AreaEffectCloudEntity cloud = new AreaEffectCloudEntity(this.world, pos.getX() + 0.5F, pos.getY() + 0.5F, pos.getZ() + 0.5F);
				cloud.setRadius(0.5F * level + 0.5F);
				cloud.setDuration(20*(level+1));
				cloud.setRadiusOnUse(-0.5F);
				cloud.setWaitTime(10);
				cloud.setRadiusPerTick(-cloud.getRadius() / cloud.getDuration());
				cloud.setPotion(potion);

				for(EffectInstance effect : potion.getEffects()) {
					cloud.addEffect(new EffectInstance(effect));
				}

				this.world.addEntity(cloud);
				break;
		}
	}


	/* fluid tank stuff */

	@Nonnull
	@Override
	public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
		if(cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
			return LazyOptional.of(() -> (IFluidHandler)tank).cast();
		}
		return super.getCapability(cap, side);
	}

	public int getFluidLevel() {
		BlockState state = world.getBlockState(pos);
		Block block = state.getBlock();
		if(block instanceof BlockEnhancedCauldron) {
			return ((BlockEnhancedCauldron)block).getLevel(state);
		}
		return 0;
	}

	public void setFluidLevel(int levels) {
		BlockState state = world.getBlockState(pos);
		Block block = state.getBlock();
		if(block instanceof BlockEnhancedCauldron) {
			if(levels == 0) {
				this.state = CauldronState.WATER;
			}
			((BlockEnhancedCauldron)block).setWaterLevel(world, pos, state, levels);
		}
	}

	public void setState(CauldronState newState, boolean doBlockUpdate) {
		if(!state.matches(newState)) {
			this.state = newState;
			if(doBlockUpdate) {
				BlockState blockstate = world.getBlockState(pos);
				world.notifyBlockUpdate(pos, blockstate, blockstate, 2);
			} else {
				this.markDirty();
			}
		}
	}


	/* NBT */
	public static final String TAG_STATE = "state";

	@Nonnull
	@Override
	public CompoundNBT getUpdateTag() {
		// new tag instead of super since default implementation calls the super of writeToNBT
		return write(new CompoundNBT());
	}

	@Nonnull
	@Override
	public CompoundNBT write(CompoundNBT tags) {
		tags = super.write(tags);

		tags.put(TAG_STATE, state.writeToNBT());

		return tags;
	}

	@Override
	public void read(CompoundNBT tags) {
		super.read(tags);

		this.state = CauldronState.fromNBT(tags.getCompound(TAG_STATE));
	}

	@Override
	public SUpdateTileEntityPacket getUpdatePacket() {
		return new SUpdateTileEntityPacket(this.getPos(), 0, state.writeToNBT());
	}

	@Override
	public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
		CauldronState newState = CauldronState.fromNBT(pkt.getNbtCompound());
		if(!this.state.matches(newState)) {
			this.state = newState;
			if(world.isRemote) {
				Minecraft.getInstance().worldRenderer.notifyBlockUpdate(null, pos, null, null, 0);
			}
		}
	}
}
