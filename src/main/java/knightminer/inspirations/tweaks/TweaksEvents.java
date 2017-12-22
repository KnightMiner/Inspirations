package knightminer.inspirations.tweaks;

import knightminer.inspirations.common.Config;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.BonemealEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.EntityInteract;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
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
}
