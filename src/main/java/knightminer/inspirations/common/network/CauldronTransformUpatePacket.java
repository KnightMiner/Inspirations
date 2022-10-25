package knightminer.inspirations.common.network;

import knightminer.inspirations.library.recipe.cauldron.recipe.ICauldronTransform;
import knightminer.inspirations.recipes.tileentity.CauldronTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent.Context;
import slimeknights.mantle.network.packet.IThreadsafePacket;
import slimeknights.mantle.recipe.helper.RecipeHelper;
import slimeknights.mantle.util.BlockEntityHelper;

import javax.annotation.Nullable;

/**
 * Packet to update the client when cauldron transform recipe changes
 */
public class CauldronTransformUpatePacket implements IThreadsafePacket {
  private final BlockPos pos;
  @Nullable
  private final ResourceLocation recipe;

  public CauldronTransformUpatePacket(BlockPos pos, @Nullable ICauldronTransform recipe) {
    this.pos = pos;
    this.recipe = recipe == null ? null : recipe.getId();
  }

  public CauldronTransformUpatePacket(FriendlyByteBuf buffer) {
    this.pos = buffer.readBlockPos();
    if (buffer.readBoolean()) {
      this.recipe = buffer.readResourceLocation();
    } else {
      this.recipe = null;
    }
  }

  @Override
  public void encode(FriendlyByteBuf buffer) {
    buffer.writeBlockPos(pos);
    if (recipe == null) {
      buffer.writeBoolean(false);
    } else {
      buffer.writeBoolean(true);
      buffer.writeResourceLocation(recipe);
    }
  }

  @Override
  public void handleThreadsafe(Context context) {
    HandleClient.handle(this);
  }

  /** Once removed client class */
  private static class HandleClient {
    private static void handle(CauldronTransformUpatePacket packet) {
      Level world = Minecraft.getInstance().level;
      if (world != null) {
        ICauldronTransform recipe = packet.recipe == null ? null : RecipeHelper.getRecipe(world.getRecipeManager(), packet.recipe, ICauldronTransform.class).orElse(null);
        BlockEntityHelper.get(CauldronTileEntity.class, world, packet.pos, true).ifPresent(te -> {
          te.setTransformRecipe(recipe);
        });
      }
    }
  }
}
