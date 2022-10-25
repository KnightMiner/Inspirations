package knightminer.inspirations.building.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import knightminer.inspirations.building.tileentity.ShelfTileEntity;
import knightminer.inspirations.library.InspirationsRegistry;
import knightminer.inspirations.library.client.model.ShelfModel;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandlerModifiable;
import slimeknights.mantle.client.model.inventory.ModelItem;
import slimeknights.mantle.client.model.util.ModelHelper;
import slimeknights.mantle.client.render.RenderingHelper;

import java.util.List;

public class ShelfTileEntityRenderer extends TileEntityRenderer<ShelfTileEntity> {
	public ShelfTileEntityRenderer(TileEntityRendererDispatcher rendererDispatcherIn) {
		super(rendererDispatcherIn);
	}

	@Override
	public void render(ShelfTileEntity shelf, float partialTicks, MatrixStack matrices, IRenderTypeBuffer buffer, int light, int combinedOverlay) {
		// first, find the model for item display locations
		BlockState state = shelf.getBlockState();
		ShelfModel.BakedModel model = ModelHelper.getBakedModel(state, ShelfModel.BakedModel.class);
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
