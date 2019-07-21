package knightminer.inspirations.building.client;

import knightminer.inspirations.building.inventory.ContainerBookshelf;
import knightminer.inspirations.building.tileentity.TileBookshelf;
import knightminer.inspirations.library.Util;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import slimeknights.mantle.client.screen.MultiModuleScreen;

public class GuiBookshelf extends MultiModuleScreen {

	private static final ResourceLocation BACKGROUND = Util.getResource("textures/gui/bookshelf.png");
	public GuiBookshelf(ContainerBookshelf container, PlayerInventory playerInv) {
		super(container, playerInv, TileBookshelf.TITLE);
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
