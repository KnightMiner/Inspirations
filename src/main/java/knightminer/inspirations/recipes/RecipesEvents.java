package knightminer.inspirations.recipes;

import knightminer.inspirations.Inspirations;
import knightminer.inspirations.common.Config;
import knightminer.inspirations.recipes.recipe.inventory.CauldronItemInventory;
import knightminer.inspirations.recipes.recipe.inventory.VanillaCauldronInventory;
import knightminer.inspirations.recipes.tileentity.CauldronTileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.CauldronBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import slimeknights.mantle.util.TileEntityHelper;

@EventBusSubscriber(modid = Inspirations.modID, bus = Bus.FORGE)
@SuppressWarnings("WeakerAccess")
public class RecipesEvents {
  /*
   * Event to handle cauldron clicking.
   * Done though an event instead of the block so we can ensure it runs before other cauldron handlers, since we cancel for non-water.
   */
  @SubscribeEvent(priority = EventPriority.HIGH)
  public static void clickCauldron(RightClickBlock event) {
    if(!Config.cauldronRecipes.get()) {
      return;
    }

    PlayerEntity player = event.getPlayer();
    if (player.isCrouching()) {
      return;
    }

    // basic properties
    World world = event.getWorld();
    BlockPos pos = event.getPos();
    BlockState state = world.getBlockState(pos);
    if (!(state.getBlock() instanceof CauldronBlock)) {
      return;
    }

    // extended uses TE
    Hand hand = event.getHand();
    if (Config.extendedCauldron.get()) {
      CauldronTileEntity cauldron = TileEntityHelper.getTile(CauldronTileEntity.class, world, pos).orElse(null);
      if (cauldron != null) {
        // TODO: blacklist?
        // stop further processing if we did a recipe or the cauldron cannot mimic vanilla cauldron
        if (cauldron.interact(player, hand) || !cauldron.canMimicVanilla()) {
          event.setCanceled(true);
          event.setCancellationResult(ActionResultType.SUCCESS);
        }
        return;
      }
    }

    // normal interaction if TE is missing
    VanillaCauldronInventory inventory = new VanillaCauldronInventory(world, pos, state, player.getItemInHand(hand),
                                                                      stack -> player.setItemInHand(hand, stack),
                                                                      CauldronItemInventory.getPlayerAdder(player));
    // if the recipe does something, stop further interaction
    if (inventory.handleRecipe()) {
      event.setCanceled(true);
      event.setCancellationResult(ActionResultType.SUCCESS);
    }
    // TODO: blacklist?
  }

	/* TODO: bottle does not exist
	@SubscribeEvent
	public static void milkSquid(EntityInteract event) {
		if(!Config.milkSquids.get()) {
			return;
		}

		// only care about cows
		Entity target = event.getTarget();
		if(!(target instanceof SquidEntity)) {
			return;
		}

		// must be holding a glass bottle
		PlayerEntity player = event.getPlayer();
		ItemStack stack = player.getHeldItem(event.getHand());
		if(stack.getItem() == Items.GLASS_BOTTLE) {
			// if has tag, cannot be milked
			CompoundNBT tags = target.getPersistentData();
			if (tags.getShort(SharedEvents.TAG_MILKCOOLDOWN) == 0) {
				// give ink bottle to player
				if (!player.isCreative()) {
					stack.shrink(1);
				}
				ItemHandlerHelper.giveItemToPlayer(player, new ItemStack(InspirationsRecipes.simpleDyedWaterBottle.get(DyeColor.BLACK)), player.inventory.currentItem);

				// sound
				player.playSound(SoundEvents.ITEM_BOTTLE_FILL_DRAGONBREATH, 1.0F, 1.0F);

				// set cooldown
				tags.putShort(SharedEvents.TAG_MILKCOOLDOWN, Config.milkSquidCooldown.get().shortValue());
				if (!event.getWorld().isRemote) {
					InspirationsNetwork.sendToClients(event.getWorld(), target.getPosition(), new MilkablePacket(target, false));
				}
				// set success
				event.setCancellationResult(ActionResultType.SUCCESS);
				event.setCanceled(true);
			}
		}
	}
	 */
}
