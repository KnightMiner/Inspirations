package knightminer.inspirations.building.client;

import knightminer.inspirations.Inspirations;
import knightminer.inspirations.building.block.entity.ShelfBlockEntity;
import knightminer.inspirations.building.block.entity.ShelfInventory;
import knightminer.inspirations.building.block.menu.ShelfContainerMenu;
import knightminer.inspirations.common.client.BackgroundContainerScreen;
import knightminer.inspirations.library.InspirationsRegistry;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import slimeknights.mantle.client.screen.ElementScreen;

/** Screen for bookshelves */
public class ShelfContainerScreen extends BackgroundContainerScreen<ShelfContainerMenu> {
	private static final ResourceLocation BACKGROUND = Inspirations.getResource("textures/gui/shelf.png");
	private static final ElementScreen SLOT_COVER = new ElementScreen(BACKGROUND, 176, 0, 18, 18, 256, 256);
	private static final ElementScreen BOOK_SLOT_ICON = new ElementScreen(BACKGROUND, 176, 18, 16, 16, 256, 256);

	/**
	 * Creates a new screen instance
	 * @param container  Container class
	 * @param inventory  Player inventory
	 * @param name       Container name
	 */
	public ShelfContainerScreen(ShelfContainerMenu container, Inventory inventory, Component name) {
		super(container, inventory, name, 156, BACKGROUND);
	}

	/** Checks if a slot should be covered, drawing the cover if needed */
	private void checkBookIcon(GuiGraphics graphics, ShelfInventory inventory, int index) {
		// draw icon if the slot is empty and the next is filled, means books only
		if (inventory.getStackInSlot(index).isEmpty() && ((index % 8 == 7) || !inventory.getStackInSlot(index + 1).isEmpty())) {
			Slot slot = getMenu().getSlot(index);
			BOOK_SLOT_ICON.draw(graphics, this.leftPos + slot.x, this.topPos + slot.y);
		}
	}

	@Override
	protected void renderBg(GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {
		super.renderBg(graphics, partialTicks, mouseX, mouseY);
		// draw book icon in slots that can only hold a book
		ShelfBlockEntity shelf = getMenu().getTile();
		if (shelf != null) {
			ShelfInventory inventory = shelf.getInventory();
			for (int i = 0; i < 16; i++) {
				checkBookIcon(graphics, inventory, i);
			}
		}
	}

	/** Checks if a slot should be covered, drawing the cover if needed */
	private void checkCoverSlot(GuiGraphics graphics, ShelfInventory inventory, int index) {
		// draw cover if the slot is empty and the previous is filled with a non-book
		if (inventory.getStackInSlot(index).isEmpty()) {
			ItemStack previous = inventory.getStackInSlot(index - 1);
			if (!previous.isEmpty() && !InspirationsRegistry.isBook(previous)) {
				Slot slot = getMenu().getSlot(index);
				SLOT_COVER.draw(graphics, slot.x - 1, slot.y - 1);
			}
		}
	}

	@Override
	protected void renderLabels(GuiGraphics graphics, int x, int y) {
		super.renderLabels(graphics, x, y);
		// draw cover to block invalid slots, drawn in foreground to cover the slot highlight
		ShelfBlockEntity shelf = getMenu().getTile();
		if (shelf != null) {
			ShelfInventory inventory = shelf.getInventory();
			// draw each of the two rows, skipping the first slot
			for (int i = 1; i < 8; i++) {
				checkCoverSlot(graphics, inventory, i);
			}
			for (int i = 9; i < 16; i++) {
				checkCoverSlot(graphics, inventory, i);
			}
		}
	}
}
