package knightminer.inspirations.tweaks;

import knightminer.inspirations.common.Config;
import knightminer.inspirations.common.network.InspirationsNetwork;
import knightminer.inspirations.common.network.MilkablePacket;
import knightminer.inspirations.library.InspirationsRegistry;
import knightminer.inspirations.library.Util;
import knightminer.inspirations.shared.SharedEvents;
import net.minecraft.block.Blocks;
import net.minecraft.block.Block;
import net.minecraft.block.BushBlock;
import net.minecraft.block.CropsBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.entity.passive.PigEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.player.BonemealEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.EntityInteract;
import net.minecraftforge.event.world.BlockEvent.HarvestDropsEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.items.ItemHandlerHelper;

import java.util.List;

public class TweaksEvents {

	@SubscribeEvent
	public static void unsaddlePig(EntityInteract event) {
		if(!Config.enablePigDesaddle.get()) {
			return;
		}

		PlayerEntity player = event.getEntityPlayer();
		ItemStack stack = player.getHeldItem(event.getHand());
		// must be sneaking and holding nothing
		if(player.isSneaking() && stack.isEmpty()) {
			Entity target = event.getTarget();
			if(target instanceof PigEntity) {
				PigEntity pig = (PigEntity) target;
				if(pig.getSaddled()) {
					pig.setSaddled(false);
					pig.world.playSound(player, pig.posX, pig.posY, pig.posZ, SoundEvents.ENTITY_PIG_SADDLE, SoundCategory.NEUTRAL, 0.5F, 1.0F);
					ItemHandlerHelper.giveItemToPlayer(player, new ItemStack(Items.SADDLE), player.inventory.currentItem);
					event.setCanceled(true);
				}
			}
		}
	}

	@SubscribeEvent
	public static void loadLoad(LootTableLoadEvent event) {
		Util.addToLootTable(event, "entities/cave_spider");
		Util.addToLootTable(event, "entities/skeleton");
	}

	@SubscribeEvent
	public static void extraBonemeal(BonemealEvent event) {
		if(!Config.bonemealMushrooms.get() && !Config.bonemealDeadBush.get() && !Config.bonemealGrassSpread.get() && !Config.bonemealMyceliumSpread.get()) {
			return;
		}

		// running client side acts weird
		World world = event.getWorld();
		if(world.isRemote) {
			return;
		}

		BlockPos pos = event.getPos();
		BlockState state = world.getBlockState(pos);
		Block block = state.getBlock();
		// block must be mycelium for mushrooms or sand for dead bushes
		if((Config.bonemealMushrooms.get() && block == Blocks.MYCELIUM) || (Config.bonemealDeadBush.get() && block.isIn(BlockTags.SAND))) {
			bonemealPlants(block, world, pos);
			event.setResult(Event.Result.ALLOW);
		}
		// block must be dirt for grass/mycelium spread
		else if((Config.bonemealGrassSpread.get() || Config.bonemealMyceliumSpread.get()) && block == Blocks.DIRT) {
			if (bonemealDirt(world, pos)) {
				event.setResult(Event.Result.ALLOW);
			}
		}
	}

	/** Called when using bonemeal on mycelium or sand to produce a plant */
	private static void bonemealPlants(Block base, World world, BlockPos pos) {
		// this is mostly copied from grass block code, so its a bit weird
		BlockPos up = pos.up();
		BushBlock bush = (BushBlock) Blocks.DEAD_BUSH;
		BlockState state = bush.getDefaultState();

		// 128 chances, this affects how far blocks are spread
		boolean isMycelium = base == Blocks.MYCELIUM;
		for (int i = 0; i < 128; ++i) {
			BlockPos next = up;
			int j = 0;

			while (true)  {
				// the longer we go, the closer to old blocks we place the block
				if (j >= i / 16) {
					if (world.isAirBlock(next)) {
						if (world.rand.nextInt(128) == 0) {
							// mycelium randomly picks between red and brown
							if(isMycelium) {
								bush = (BushBlock) (world.rand.nextInt(2) == 0 ? Blocks.RED_MUSHROOM : Blocks.BROWN_MUSHROOM);
								state = bush.getDefaultState();
							}
							// if it can be planted here, plant it
							if(bush.isValidPosition(state, world, next)) {
								world.setBlockState(next, state);
							}
						}
					}

					break;
				}

				// randomly offset the position
				next = next.add(world.rand.nextInt(3) - 1, (world.rand.nextInt(3) - 1) * world.rand.nextInt(3) / 2, world.rand.nextInt(3) - 1);

				// if the new position is invalid, this cycle is done
				if (world.getBlockState(next.down()).getBlock() != base || world.getBlockState(next).isNormalCube(world, next)) {
					break;
				}

				++j;
			}
		}
	}

	/** Called when using bonemeal on a dirt block to spread grass */
	private static boolean bonemealDirt(World world, BlockPos pos) {
		if(world.getLight(pos.up()) < 9) {
			return false;
		}

		// first, get a count of grass and mycelium on all sides
		int grass = 0;
		int mycelium = 0;
		for (Direction side : Direction.Plane.HORIZONTAL) {
			BlockPos offset = pos.offset(side);
			BlockState state = world.getBlockState(offset);
			Block block = state.getBlock();

			// hill logic: go up for dirt, down for air
			if (block.isAir(state, world, pos)) {
				state = world.getBlockState(offset.down());
				block = state.getBlock();
			}
			else if (block != Blocks.GRASS_BLOCK && block != Blocks.MYCELIUM) {
				state = world.getBlockState(offset.up());
				block = state.getBlock();
			}

			// increment if the state is grass/mycelium
			if (Config.bonemealGrassSpread.get() && block == Blocks.GRASS_BLOCK) {
				grass++;
			}
			else if (Config.bonemealMyceliumSpread.get() && block == Blocks.MYCELIUM) {
				mycelium++;
			}
		}

		// no results? exit
		if (grass == 0 && mycelium == 0) {
			return false;
		}

		// chance gets higher the more blocks of the type surround
		if (world.rand.nextInt(5) > (Math.max(grass, mycelium) - 1)) {
			return true;
		}

		//  place block based on which has more
		// if there is a tie, randomly choose
		if (grass == mycelium) {
			if(world.rand.nextBoolean()) {
				mycelium++;
			}
		}
		world.setBlockState(pos, grass >= mycelium ? Blocks.GRASS_BLOCK.getDefaultState() : Blocks.MYCELIUM.getDefaultState());
		return true;
	}

	@SubscribeEvent
	public static void dropHeartbeet(HarvestDropsEvent event) {
		if(!Config.enableHeartbeet.get()) {
			return;
		}

		// insure its fully grown beetroots
		BlockState state = event.getState();
		Block block = state.getBlock();
		if(block != Blocks.BEETROOTS || !(block instanceof CropsBlock) || !((CropsBlock)block).isMaxAge(state)) {
			return;
		}

		// we get a base of two chances, and each fortune level adds one more
		int rolls = event.getFortuneLevel() + 2;
		// up to fortune 4 we will keep, any higher just ignore
		if(rolls > 6) {
			rolls = 6;
		}

		List<ItemStack> drops = event.getDrops();
		// find the first beetroot from the drops
		iterator:
			for(ItemStack stack : drops) {
				// as soon as we find one, chance to replace it
				if(stack.getItem() == Items.BEETROOT) {
					// for each roll, try to get the drop once
					for(int i = 0; i < rolls; i++) {
						if(event.getWorld().getRandom().nextInt(Config.heartbeetChance.get()) == 0) {
							stack.shrink(1);
							if(stack.isEmpty()) {
								drops.remove(stack);
							}
							drops.add(new ItemStack(InspirationsTweaks.heartbeet));
							// cap at one heartroot in case we get extras, plus prevents concurrent modification
							break iterator;
						}
					}
				}
			}
	}

	@SubscribeEvent
	public static void dropCarrotsPotatos(HarvestDropsEvent event) {
		if(!Config.nerfCarrotPotatoDrops()) {
			return;
		}

		// validate block and ensure its not max age
		BlockState state = event.getState();
		Block block = state.getBlock();
		if((block != Blocks.CARROTS && block != Blocks.POTATOES) || !(block instanceof CropsBlock) || ((CropsBlock)block).isMaxAge(state)) {
			return;
		}

		// replace the seed with our seed
		event.getDrops().clear();
		event.getDrops().add(new ItemStack(block == Blocks.CARROTS ? InspirationsTweaks.carrotSeeds : InspirationsTweaks.potatoSeeds));
	}

	@SubscribeEvent(priority = EventPriority.LOW)
	public static void onFall(LivingFallEvent event) {
		if(!Config.lilypadBreakFall.get()) {
			return;
		}

		// no fall damage
		if(event.getDistance() < 4) {
			return;
		}

		// ensure client world
		LivingEntity entity = event.getEntityLiving();
		World world = entity.getEntityWorld();
		if(world.isRemote) {
			return;
		}
		// actually hit the lily pad
		Vec3d vec = entity.getPositionVector();
		if(vec.y % 1 > 0.09375) {
			return;
		}

		// build a list of lily pads we hit
		BlockPos blockPos = entity.getPosition();
		BlockPos[] posList = new BlockPos[4];
		int i = 0;
		posList[i++] = blockPos;
		double x = vec.x % 1;
		if(x < 0) {
			x += 1;
		}
		double z = vec.z % 1;
		if(z < 0) {
			z += 1;
		}
		// about 0.3 out of the block is into another block
		if(x > 0.7) {
			posList[i++] = blockPos.east();
		} else if(x < 0.3) {
			posList[i++] = blockPos.west();
		}
		if(z > 0.7) {
			posList[i++] = blockPos.south();
			// make sure to get the corners
			if(i == 3) {
				posList[i++] = posList[1].south();
			}
		} else if(z < 0.3) {
			posList[i++] = blockPos.north();
			if(i == 3) {
				posList[i++] = posList[1].north();
			}
		}

		// loop through the position list and find any lily pads
		boolean safe = false;
		for(BlockPos pos : posList) {
			if(pos != null && world.getBlockState(pos).getBlock() == Blocks.LILY_PAD) {
				world.destroyBlock(pos, true);
				safe = true;
			}
		}
		// if we got one, this fall is safe
		if(safe) {
			event.setDistance(0);
		}
	}

	@SubscribeEvent
	public static void milkCow(EntityInteract event) {
		if(!Config.milkCooldown.get()) {
			return;
		}

		// only care about cows
		Entity target = event.getTarget();
		if(!(target instanceof CowEntity) || ((CowEntity)target).isChild()) {
			return;
		}

		// must be holding a milk container
		ItemStack stack = event.getEntityPlayer().getHeldItem(event.getHand());
		if(stack.getItem().isIn(InspirationsRegistry.TAG_MILK_CONTAINERS)) {
			// if has tag, cannot be milked
			CompoundNBT tags = target.getEntityData();
			if (tags.getShort(SharedEvents.TAG_MILKCOOLDOWN) > 0) {
				event.setCancellationResult(ActionResultType.PASS);
				event.setCanceled(true);
			} else {
				// no tag means we add it as part of milking
				tags.putShort(SharedEvents.TAG_MILKCOOLDOWN, Config.milkCooldownTime.get().shortValue());
				if (!event.getWorld().isRemote) {
					InspirationsNetwork.sendToClients(event.getWorld(), target.getPosition(), new MilkablePacket(target, false));
				}
			}
		}
	}

	@SubscribeEvent
	public static void fixShieldTooltip(ItemTooltipEvent event) {
		if (!Config.fixShieldTooltip.get()) {
			return;
		}
		ItemStack stack = event.getItemStack();
		if (stack.getItem() != Items.SHIELD) {
			return;
		}
		CompoundNBT tags = stack.getChildTag("BlockEntityTag");
		if (tags != null && tags.contains("Patterns") && stack.isEnchanted()) {
			ListNBT patterns = tags.getList("Patterns", 10);
			event.getToolTip().add(patterns.size() + 1, new StringTextComponent(""));
		}
	}
}
