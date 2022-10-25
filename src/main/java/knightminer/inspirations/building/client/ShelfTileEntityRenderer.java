package knightminer.inspirations.building.client;

import com.mojang.blaze3d.vertex.PoseStack;
import knightminer.inspirations.building.tileentity.ShelfTileEntity;
import knightminer.inspirations.library.InspirationsRegistry;
import knightminer.inspirations.library.client.model.ShelfModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.IItemHandlerModifiable;
import slimeknights.mantle.client.model.inventory.ModelItem;
import slimeknights.mantle.client.model.util.ModelHelper;
import slimeknights.mantle.client.render.RenderingHelper;

import java.util.List;

public class ShelfTileEntityRenderer implements BlockEntityRenderer<ShelfTileEntity> {
	public ShelfTileEntityRenderer(BlockEntityRendererProvider.Context context) {}

	@Override
	public void render(ShelfTileEntity shelf, float partialTicks, PoseStack matrices, MultiBufferSource buffer, int light, int combinedOverlay) {
		// first, find the model for item display locations
		BlockState state = shelf.getBlockState();
		ShelfModel.Baked model = ModelHelper.getBakedModel(state, ShelfModel.Baked.class);
		IItemHandlerModifiable inventory = shelf.getInventory();
		if (model != null) {
			// if the block is rotatable, rotate item display
			boolean isRotated = RenderingHelper.applyRotation(matrices, state);

			// render items
			List<ModelItem> modelItems = model.getItems();
			for (int i = 0; i < modelItems.size(); i++) {
				ItemStack stack = inventory.getStackInSlot(i);
				// only render non-books, books are rendered in the model
				if (!stack.isEmpty() && !InspirationsRegistry.isBook(stack)) {
					RenderingHelper.renderItem(matrices, buffer, stack, modelItems.get(i), light);
				}
			}

			// pop back rotation
			if (isRotated) {
				matrices.popPose();
			}
		}
	}
}
