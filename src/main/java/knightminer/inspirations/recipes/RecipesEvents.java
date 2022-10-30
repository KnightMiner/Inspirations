package knightminer.inspirations.recipes;

import knightminer.inspirations.Inspirations;
import knightminer.inspirations.common.Config;
import knightminer.inspirations.library.recipe.cauldron.CauldronRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractCauldronBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(modid = Inspirations.modID, bus = Bus.FORGE)
public class RecipesEvents {
  /*
   * Event to handle cauldron clicking.
   * Done though an event instead of the block so we can ensure it runs before other cauldron handlers, since we cancel for non-water.
   */
  @SubscribeEvent(priority = EventPriority.HIGH)
  static void clickCauldron(RightClickBlock event) {
    if(!Config.cauldronRecipes.getAsBoolean()) {
      return;
    }
    Player player = event.getPlayer();
    if (player.isCrouching()) {
      return;
    }

    // ignore non-cauldrons
    Level world = event.getWorld();
    BlockPos pos = event.getPos();
    BlockState state = world.getBlockState(pos);
    if (!(state.getBlock() instanceof AbstractCauldronBlock)) {
      return;
    }

    // this is a good spot to hook in JSON eventually
    InteractionHand hand = event.getHand();
    InteractionResult result = CauldronRegistry.attemptOverride(state, world, pos, player, hand, event.getItemStack());
    if (result.consumesAction()) {
      event.setCanceled(true);
      event.setCancellationResult(result);
    }
  }
}
