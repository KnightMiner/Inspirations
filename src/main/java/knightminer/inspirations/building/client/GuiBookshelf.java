package knightminer.inspirations.building.client;

import knightminer.inspirations.building.inventory.ContainerBookshelf;
import knightminer.inspirations.library.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import slimeknights.mantle.client.gui.GuiMultiModule;

public class GuiBookshelf extends GuiMultiModule {

	private static final ResourceLocation BACKGROUND = Util.getResource("textures/gui/bookshelf.png");
	public GuiBookshelf(ContainerBookshelf container) {
		super(container);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		drawBackground(BACKGROUND);
		super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
	}

	@Override
	protected void drawPlayerInventoryName() {
		String localizedName = Minecraft.getMinecraft().player.inventory.getDisplayName().getUnformattedText();
		this.fontRenderer.drawString(localizedName, 8, this.ySize - 106 + 2, 0x404040);
	}
}
