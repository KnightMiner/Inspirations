package knightminer.inspirations.utility.client;

import knightminer.inspirations.utility.inventory.CollectorContainer;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import slimeknights.mantle.client.screen.MultiModuleScreen;

public class CollectorScreen extends MultiModuleScreen<CollectorContainer> {

	private static final ResourceLocation BACKGROUND = new ResourceLocation("textures/gui/container/dispenser.png");

	public CollectorScreen(CollectorContainer container, PlayerInventory playerInv, ITextComponent title) {
		super(container, playerInv, title);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		drawBackground(BACKGROUND);
		super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
	}

	@Override
	protected void drawPlayerInventoryName() {
		String localizedName = Minecraft.getInstance().player.inventory.getDisplayName().getUnformattedComponentText();
		this.font.drawString(localizedName, 8, this.ySize - 96 + 2, 0x404040);
	}
}
