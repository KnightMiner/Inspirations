package knightminer.inspirations.tweaks;

import knightminer.inspirations.common.Config;
import knightminer.inspirations.common.network.InspirationsNetwork;
import knightminer.inspirations.common.network.MilkablePacket;
import knightminer.inspirations.library.ItemMetaKey;
import knightminer.inspirations.shared.InspirationsShared;
import knightminer.inspirations.shared.SharedEvents;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.BlockDirt;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.player.BonemealEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.EntityInteract;
import net.minecraftforge.event.world.BlockEvent.HarvestDropsEvent;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.items.ItemHandlerHelper;

import java.util.List;

public class TweaksEvents {

	@SubscribeEvent
	public static void unsaddlePig(EntityInteract event) {
		if(!Config.enablePigDesaddle) {
			return;
		}

		EntityPlayer player = event.getEntityPlayer();
		ItemStack stack = player.getHeldItem(event.getHand());
		// must be sneaking and holding nothing
		if(player.isSneaking() && stack.isEmpty()) {
			Entity target = event.getTarget();
			if(target instanceof EntityPig) {
				EntityPig pig = (EntityPig) target;
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
	public static void extraBonemeal(BonemealEvent event) {
		if(!Config.bonemealMushrooms && !Config.bonemealDeadBush && !Config.bonemealGrassSpread && !Config.bonemealMyceliumSpread) {
			return;
		}

		// running client side acts weird
		World world = event.getWorld();
		if(world.isRemote) {
			return;
		}

		BlockPos pos = event.getPos();
		IBlockState state = world.getBlockState(pos);
		Block block = state.getBlock();
		// block must be mycelium for mushrooms or sand for dead bushes
		if((Config.bonemealMushrooms && block == Blocks.MYCELIUM) || (Config.bonemealDeadBush && block == Blocks.SAND)) {
			bonemealPlants(block, world, pos);
			event.setResult(Result.ALLOW);
		}
		// block must be dirt for grass/mycelium spread
		else if((Config.bonemealGrassSpread || Config.bonemealMyceliumSpread) && block == Blocks.DIRT && state.getValue(BlockDirt.VARIANT) == BlockDirt.DirtType.DIRT) {
			if (bonemealDirt(world, pos)) {
				event.setResult(Result.ALLOW);
			}
		}
	}

	/** Called when using bonemeal on mycelium or sand to produce a plant */
	private static void bonemealPlants(Block base, World world, BlockPos pos) {
		// this is mostly copied from grass block code, so its a bit weird
		BlockPos up = pos.up();
		BlockBush bush = Blocks.DEADBUSH;
		IBlockState state = bush.getDefaultState();

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
								bush = world.rand.nextInt(2) == 0 ? Blocks.RED_MUSHROOM : Blocks.BROWN_MUSHROOM;
								state = bush.getDefaultState();
							}
							// if it can be planted here, plant it
							if(bush.canBlockStay(world, next, state)) {
								world.setBlockState(next, state);
							}
						}
					}

					break;
				}

				// randomly offset the position
				next = next.add(world.rand.nextInt(3) - 1, (world.rand.nextInt(3) - 1) * world.rand.nextInt(3) / 2, world.rand.nextInt(3) - 1);

				// if the new position is invalid, this cycle is done
				if (world.getBlockState(next.down()).getBlock() != base || world.getBlockState(next).isNormalCube()) {
					break;
				}

				++j;
			}
		}
	}

	/** Called when using bonemeal on a dirt block to spread grass */
	private static boolean bonemealDirt(World world, BlockPos pos) {
		if(world.getLightFromNeighbors(pos.up()) < 9) {
			return false;
		}

		// first, get a count of grass and mycelium on all sides
		int grass = 0;
		int mycelium = 0;
		for (EnumFacing side : EnumFacing.HORIZONTALS) {
			BlockPos offset = pos.offset(side);
			IBlockState state = world.getBlockState(offset);
			Block block = state.getBlock();

			// hill logic: go up for dirt, down for air
			if (block.isAir(state, world, pos)) {
				state = world.getBlockState(offset.down());
				block = state.getBlock();
			}
			else if (block != Blocks.GRASS && block != Blocks.MYCELIUM) {
				state = world.getBlockState(offset.up());
				block = state.getBlock();
			}

			// increment if the state is grass/mycelium
			if (Config.bonemealGrassSpread && block == Blocks.GRASS) {
				grass++;
			}
			else if (Config.bonemealMyceliumSpread && block == Blocks.MYCELIUM) {
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

		// place block based on which has more, grass wins ties
		world.setBlockState(pos, grass >= mycelium ? Blocks.GRASS.getDefaultState() : Blocks.MYCELIUM.getDefaultState());
		return true;
	}

	@SubscribeEvent
	public static void dropHeartbeet(HarvestDropsEvent event) {
		if(!Config.enableHeartbeet) {
			return;
		}

		// insure its fully grown beetroots
		IBlockState state = event.getState();
		Block block = state.getBlock();
		if(block != Blocks.BEETROOTS || !(block instanceof BlockCrops) || !((BlockCrops)block).isMaxAge(state)) {
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
						if(event.getWorld().rand.nextInt(Config.heartbeetChance) == 0) {
							stack.shrink(1);
							if(stack.isEmpty()) {
								drops.remove(stack);
							}
							drops.add(InspirationsShared.heartbeet.copy());
							// cap at one heartroot in case we get extras, plus prevents concurrent modification
							break iterator;
						}
					}
				}
			}
	}

	@SubscribeEvent
	public static void dropCarrotsPotatos(HarvestDropsEvent event) {
		if(!Config.nerfCarrotPotatoDrops) {
			return;
		}

		// validate block and ensure its not max age
		IBlockState state = event.getState();
		Block block = state.getBlock();
		if((block != Blocks.CARROTS && block != Blocks.POTATOES) || !(block instanceof BlockCrops) || ((BlockCrops)block).isMaxAge(state)) {
			return;
		}

		// replace the seed with our seed
		event.getDrops().clear();
		event.getDrops().add(new ItemStack(block == Blocks.CARROTS ? InspirationsTweaks.carrotSeeds : InspirationsTweaks.potatoSeeds));
	}

	@SubscribeEvent(priority = EventPriority.LOW)
	public static void onFall(LivingFallEvent event) {
		if(!Config.lilypadBreakFall) {
			return;
		}

		// no fall damage
		if(event.getDistance() < 4) {
			return;
		}

		// ensure client world
		EntityLivingBase entity = event.getEntityLiving();
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
			if(pos != null && world.getBlockState(pos).getBlock() == Blocks.WATERLILY) {
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
		if(!Config.milkCooldown) {
			return;
		}

		// only care about cows
		Entity target = event.getTarget();
		if(!(target instanceof EntityCow) || ((EntityCow)target).isChild()) {
			return;
		}

		// must be holding a milk container
		ItemStack stack = event.getEntityPlayer().getHeldItem(event.getHand());
		if(Config.milkContainers.contains(new ItemMetaKey(stack))) {
			// if has tag, cannot be milked
			NBTTagCompound tags = target.getEntityData();
			if (tags.getShort(SharedEvents.TAG_MILKCOOLDOWN) > 0) {
				event.setCancellationResult(EnumActionResult.PASS);
				event.setCanceled(true);
			} else {
				// no tag means we add it as part of milking
				tags.setShort(SharedEvents.TAG_MILKCOOLDOWN, Config.milkCooldownTime);
				if (!event.getWorld().isRemote) {
					InspirationsNetwork.sendToClients(event.getWorld(), target.getPosition(), new MilkablePacket(target, false));
				}
			}
		}
	}
}
