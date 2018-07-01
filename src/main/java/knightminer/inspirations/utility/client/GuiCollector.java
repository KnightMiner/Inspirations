package knightminer.inspirations.utility.client;

import knightminer.inspirations.utility.inventory.ContainerCollector;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import slimeknights.mantle.client.gui.GuiMultiModule;

public class GuiCollector extends GuiMultiModule {

	private static final ResourceLocation BACKGROUND = new ResourceLocation("textures/gui/container/dispenser.png");
	public GuiCollector(ContainerCollector container) {
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
		this.fontRenderer.drawString(localizedName, 8, this.ySize - 96 + 2, 0x404040);
	}
}
