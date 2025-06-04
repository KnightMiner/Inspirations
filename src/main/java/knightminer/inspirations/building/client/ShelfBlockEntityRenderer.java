package knightminer.inspirations.building.client;

import com.mojang.blaze3d.vertex.PoseStack;
import knightminer.inspirations.building.block.entity.ShelfBlockEntity;
import knightminer.inspirations.library.InspirationsRegistry;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.IItemHandlerModifiable;
import slimeknights.mantle.client.render.RenderItem;
import slimeknights.mantle.client.render.RenderingHelper;

import java.util.List;

public class ShelfBlockEntityRenderer implements BlockEntityRenderer<ShelfBlockEntity> {
	public ShelfBlockEntityRenderer(BlockEntityRendererProvider.Context context) {}

	@Override
	public void render(ShelfBlockEntity shelf, float partialTicks, PoseStack matrices, MultiBufferSource buffer, int light, int combinedOverlay) {
		// first, find the model for item display locations
		BlockState state = shelf.getBlockState();
		List<RenderItem> renderItems = RenderItem.STATE_REGISTRY.get(state, List.of());
		IItemHandlerModifiable inventory = shelf.getInventory();
		if (!renderItems.isEmpty()) {
			// if the block is rotatable, rotate item display
			boolean isRotated = RenderingHelper.applyRotation(matrices, state);

			// render items
			for (int i = 0; i < renderItems.size(); i++) {
				ItemStack stack = inventory.getStackInSlot(i);
				// only render non-books, books are rendered in the model
				if (!stack.isEmpty() && !InspirationsRegistry.isBook(stack)) {
					RenderingHelper.renderItem(matrices, buffer, stack, renderItems.get(i), light);
				}
			}

			// pop back rotation
			if (isRotated) {
				matrices.popPose();
			}
		}
	}
}
