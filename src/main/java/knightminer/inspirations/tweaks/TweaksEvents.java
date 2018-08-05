package knightminer.inspirations.tweaks;

import java.util.List;
import knightminer.inspirations.common.Config;
import knightminer.inspirations.shared.InspirationsShared;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
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
		if(!Config.enableExtraBonemeal) {
			return;
		}

		// running client side acts weird
		World world = event.getWorld();
		if(world.isRemote) {
			return;
		}

		BlockPos pos = event.getPos();
		Block block = world.getBlockState(pos).getBlock();
		boolean isMycelium = block == Blocks.MYCELIUM;
		// block must be mycelium for mushrooms or sand for dead bushes
		if(!isMycelium && block != Blocks.SAND) {
			return;
		}

		// this is mostly copied from grass block code, so its a bit weird
		BlockPos up = pos.up();
		BlockBush bush = Blocks.DEADBUSH;
		IBlockState state = bush.getDefaultState();

		// 128 chances, this affects how far blocks are spread
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
				if (world.getBlockState(next.down()).getBlock() != block|| world.getBlockState(next).isNormalCube()) {
					break;
				}

				++j;
			}
		}

		event.setResult(Result.ALLOW);
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
}
