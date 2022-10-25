package knightminer.inspirations.building.client;

import com.mojang.blaze3d.vertex.PoseStack;
import knightminer.inspirations.Inspirations;
import knightminer.inspirations.building.inventory.ShelfContainer;
import knightminer.inspirations.building.tileentity.ShelfInventory;
import knightminer.inspirations.building.tileentity.ShelfTileEntity;
import knightminer.inspirations.library.InspirationsRegistry;
import knightminer.inspirations.library.client.ClientUtil;
import knightminer.inspirations.shared.client.BackgroundContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import slimeknights.mantle.client.screen.ElementScreen;

/** Screen for bookshelves */
public class ShelfScreen extends BackgroundContainerScreen<ShelfContainer> {
	private static final ElementScreen SLOT_COVER = new ElementScreen(176, 0, 18, 18, 256, 256);
	private static final ElementScreen BOOK_SLOT_ICON = new ElementScreen(176, 18, 16, 16, 256, 256);

	/**
	 * Creates a new screen instance
	 * @param container  Container class
	 * @param inventory  Player inventory
	 * @param name       Container name
	 */
	public ShelfScreen(ShelfContainer container, Inventory inventory, Component name) {
		super(container, inventory, name, 156, Inspirations.getResource("textures/gui/shelf.png"));
	}

	/** Checks if a slot should be covered, drawing the cover if needed */
	private void checkBookIcon(PoseStack matrixStack, ShelfInventory inventory, int index) {
		// draw icon if the slot is empty and the next is filled, means books only
		if (inventory.getStackInSlot(index).isEmpty() && ((index % 8 == 7) || !inventory.getStackInSlot(index + 1).isEmpty())) {
			Slot slot = getMenu().getSlot(index);
			BOOK_SLOT_ICON.draw(matrixStack, this.leftPos + slot.x, this.topPos + slot.y);
		}
	}

	@Override
	protected void renderBg(PoseStack matrixStack, float partialTicks, int mouseX, int mouseY) {
		super.renderBg(matrixStack, partialTicks, mouseX, mouseY);
		// draw book icon in slots that can only hold a book
		ShelfTileEntity shelf = getMenu().getTile();
		if (shelf != null) {
			ShelfInventory inventory = shelf.getInventory();
			for (int i = 0; i < 16; i++) {
				checkBookIcon(matrixStack, inventory, i);
			}
		}
	}

	/** Checks if a slot should be covered, drawing the cover if needed */
	private void checkCoverSlot(PoseStack matrixStack, ShelfInventory inventory, int index) {
		// draw cover if the slot is empty and the previous is filled with a non-book
		if (inventory.getStackInSlot(index).isEmpty()) {
			ItemStack previous = inventory.getStackInSlot(index - 1);
			if (!previous.isEmpty() && !InspirationsRegistry.isBook(previous)) {
				Slot slot = getMenu().getSlot(index);
				SLOT_COVER.draw(matrixStack, slot.x - 1, slot.y - 1);
			}
		}
	}

	@Override
	protected void renderLabels(PoseStack matrixStack, int x, int y) {
		super.renderLabels(matrixStack, x, y);
		// draw cover to block invalid slots, drawn in foreground to cover the slot highlight
		ShelfTileEntity shelf = getMenu().getTile();
		if (shelf != null) {
			ShelfInventory inventory = shelf.getInventory();
			ClientUtil.bindTexture(this.background);
			// draw each of the two rows, skipping the first slot
			for (int i = 1; i < 8; i++) {
				checkCoverSlot(matrixStack, inventory, i);
			}
			for (int i = 9; i < 16; i++) {
				checkCoverSlot(matrixStack, inventory, i);
			}
		}
	}
}
