package knightminer.inspirations.utility.client;

import knightminer.inspirations.library.Util;
import knightminer.inspirations.utility.inventory.PipeContainer;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import slimeknights.mantle.client.screen.MultiModuleScreen;

public class PipeScreen extends MultiModuleScreen<PipeContainer> {

	private static final ResourceLocation BACKGROUND = Util.getResource("textures/gui/pipe.png");

	public PipeScreen(PipeContainer container, PlayerInventory playerInventory, ITextComponent title) {
		super(container, playerInventory, title);
	}


	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		drawBackground(BACKGROUND);
		super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
	}

	@Override
	protected void drawPlayerInventoryName() {
		String localizedName = Minecraft.getInstance().player.inventory.getDisplayName().getUnformattedComponentText();
		this.font.drawString(localizedName, 8, this.ySize - 129 + 2, 0x404040);
	}
}
