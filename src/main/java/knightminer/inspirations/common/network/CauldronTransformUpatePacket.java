package knightminer.inspirations.common.network;

import knightminer.inspirations.library.recipe.cauldron.recipe.ICauldronTransform;
import knightminer.inspirations.recipes.tileentity.CauldronTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent.Context;
import slimeknights.mantle.network.packet.IThreadsafePacket;
import slimeknights.mantle.recipe.RecipeHelper;
import slimeknights.mantle.util.TileEntityHelper;

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

  public CauldronTransformUpatePacket(PacketBuffer buffer) {
    this.pos = buffer.readBlockPos();
    if (buffer.readBoolean()) {
      this.recipe = buffer.readResourceLocation();
    } else {
      this.recipe = null;
    }
  }

  @Override
  public void encode(PacketBuffer buffer) {
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
      World world = Minecraft.getInstance().level;
      if (world != null) {
        ICauldronTransform recipe = packet.recipe == null ? null : RecipeHelper.getRecipe(world.getRecipeManager(), packet.recipe, ICauldronTransform.class).orElse(null);
        TileEntityHelper.getTile(CauldronTileEntity.class, world, packet.pos, true).ifPresent(te -> {
          te.setTransformRecipe(recipe);
        });
      }
    }
  }
}
