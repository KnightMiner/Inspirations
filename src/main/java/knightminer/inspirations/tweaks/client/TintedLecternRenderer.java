package knightminer.inspirations.tweaks.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import knightminer.inspirations.Inspirations;
import knightminer.inspirations.common.network.InspirationsNetwork;
import knightminer.inspirations.library.MiscUtil;
import knightminer.inspirations.library.client.ClientUtil;
import knightminer.inspirations.tweaks.network.RequestLecternBookPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.BookModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.EnchantTableRenderer;
import net.minecraft.client.resources.model.Material;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.LecternBlock;
import net.minecraft.world.level.block.entity.LecternBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/** Based on {@link net.minecraft.client.renderer.blockentity.LecternRenderer}, but tints the book cover */
public class TintedLecternRenderer implements BlockEntityRenderer<LecternBlockEntity>  {
  private static final String TAG_REQUESTED = "inspirations_requested";
  public static final Material BOOK_LOCATION = new Material(InventoryMenu.BLOCK_ATLAS, Inspirations.getResource("entity/lectern_book"));

  private final Minecraft mc = Minecraft.getInstance();
  private final ModelPart book;
  private final ModelPart leftLid, rightLid, seam;
  public TintedLecternRenderer(BlockEntityRendererProvider.Context pContext) {
    this.book = pContext.bakeLayer(ModelLayers.BOOK);
    this.leftLid = book.getChild("left_lid");
    this.rightLid = book.getChild("right_lid");
    this.seam = book.getChild("seam");
    // we only need to call this once, so might as well call it in the constructor
    new BookModel(book).setupAnim(0.0f, 0.1f, 0.9f, 1.2f);
  }

  @Override
  public void render(LecternBlockEntity lectern, float pPartialTick, PoseStack poseStack, MultiBufferSource pBuffer, int pPackedLight, int pPackedOverlay) {
    BlockState state = lectern.getBlockState();
    if (state.getValue(LecternBlock.HAS_BOOK)) {
      poseStack.pushPose();
      poseStack.translate(0.5F, 1.0625F, 0.5F);
      float rotation = state.getValue(LecternBlock.FACING).getClockWise().toYRot();
      poseStack.mulPose(Axis.YP.rotationDegrees(-rotation));
      poseStack.mulPose(Axis.ZP.rotationDegrees(67.5F));
      poseStack.translate(0, -0.125f, 0);

      // reset the cover to render again in case last frame was a different book
      this.leftLid.visible = true;
      this.rightLid.visible = true;
      this.seam.visible = true;

      // if we have not requested it, request the book from the server
      CompoundTag persistent = lectern.getPersistentData();
      if (!persistent.getBoolean(TAG_REQUESTED)) {
        persistent.putBoolean(TAG_REQUESTED, true);
        InspirationsNetwork.INSTANCE.sendToServer(new RequestLecternBookPacket(lectern.getBlockPos()));
      }

      // check if we care about this book, use default for vanilla or unfetched
      ItemStack book = lectern.getBook();
      if (!book.isEmpty() && !book.is(Items.WRITTEN_BOOK) && !book.is(Items.WRITABLE_BOOK)) {
        // we need to tint the cover, so start tinting
        VertexConsumer vertexConsumer = BOOK_LOCATION.buffer(pBuffer, RenderType::entitySolid);
        // figure out our tint
        int color = ClientUtil.getItemColor(book.getItem());
        int itemColors = mc.getItemColors().getColor(book, 0);

        // if we have a color, use it
        if (color != -1 || itemColors != -1) {
          if (itemColors != -1) {
            // make item colors result dominate
            color = MiscUtil.combineColors(color, itemColors, 3);
          }

          // calculate final tint
          float r = (color >> 16 & 0xff) / 255f;
          float g = (color >>  8 & 0xff) / 255f;
          float b = (color       & 0xff) / 255f;
          // render the cover with the tint
          this.leftLid.render(poseStack, vertexConsumer, pPackedLight, pPackedOverlay, r, g, b, 1.0F);
          this.rightLid.render(poseStack, vertexConsumer, pPackedLight, pPackedOverlay, r, g, b, 1.0F);
          this.seam.render(poseStack, vertexConsumer, pPackedLight, pPackedOverlay, r, g, b, 1.0F);
          // render the rest of the book
          this.leftLid.visible = false;
          this.rightLid.visible = false;
          this.seam.visible = false;
          this.book.render(poseStack, vertexConsumer, pPackedLight, pPackedOverlay, 1, 1, 1, 1);
          poseStack.popPose();
          return;
        }
      }

      // render with default settings
      VertexConsumer vertexConsumer = EnchantTableRenderer.BOOK_LOCATION.buffer(pBuffer, RenderType::entitySolid);
      this.book.render(poseStack, vertexConsumer, pPackedLight, pPackedOverlay, 1, 1, 1, 1);
      poseStack.popPose();
    } else {
      // mark the book as not requested so changing books requests it again
      lectern.getPersistentData().remove(TAG_REQUESTED);
    }
  }
}
