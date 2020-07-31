package knightminer.inspirations.recipes;

//@EventBusSubscriber(modid = Inspirations.modID, bus = Bus.FORGE)
@SuppressWarnings("WeakerAccess")
public class RecipesEvents {

  /*
   * Event to handle cauldron clicking.
   * Done though an event instead of the block so we can ensure it runs before other cauldron handlers, since we cancel for non-water.
   */
	/* TODO: reimplement
	@SubscribeEvent(priority = EventPriority.HIGH)
	public static void clickCauldron(RightClickBlock event) {
		if(!Config.enableCauldronRecipes()) {
			return;
		}

		PlayerEntity player = event.getPlayer();
		if(player.isCrouching()) {
			return;
		}

		// basic properties
		World world = event.getWorld();
		BlockPos pos = event.getPos();
		BlockState state = world.getBlockState(pos);
		if(!InspirationsRegistry.isNormalCauldron(state)) {
			return;
		}
		ItemStack stack = event.getItemStack();
		if(stack.isEmpty()) {
			return;
		}

		boolean result = CauldronTileEntity.interact(world, pos, state, player, event.getHand());
		if(result InspirationsRegistry.isCauldronBlacklist(stack)) {
			event.setCanceled(true);
			event.setCancellationResult(ActionResultType.SUCCESS);
		}
	} */

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
