package knightminer.inspirations.building.client;

import knightminer.inspirations.building.inventory.BookshelfContainer;
import knightminer.inspirations.library.Util;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import slimeknights.mantle.client.screen.MultiModuleScreen;

public class BookshelfScreen extends MultiModuleScreen<BookshelfContainer> {

	private static final ResourceLocation BACKGROUND = Util.getResource("textures/gui/bookshelf.png");

	public BookshelfScreen(BookshelfContainer container, PlayerInventory playerInv, ITextComponent title) {
		super(container, playerInv, title);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		drawBackground(BACKGROUND);
		super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
	}

	@Override
	protected void drawPlayerInventoryName() {
		String localizedName = playerInventory.getDisplayName().getUnformattedComponentText();
		this.font.drawString(localizedName, 8, this.ySize - 106 + 2, 0x404040);
	}
}
