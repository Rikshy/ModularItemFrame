package de.shyrik.justcraftingframes.client.gui;

import de.shyrik.justcraftingframes.JustCraftingFrames;
import de.shyrik.justcraftingframes.common.container.ContainerCraftingFrame;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class GuiCraftingFrame extends GuiContainer {

	/**
	 * The ResourceLocation containing the chest GUI texture.
	 */
	private static final ResourceLocation CF_GUI_TEXTURE = new ResourceLocation(JustCraftingFrames.MOD_ID,"textures/gui/crafting_frame.png");

	public GuiCraftingFrame(final ContainerCraftingFrame container) {
		super(container);

		allowUserInput = true;
	}

	/**
	 * Draws the background layer of this container (behind the items).
	 *
	 * @param partialTicks How far into the current tick the game is, with 0.0 being the start of the tick and 1.0 being the end.
	 * @param mouseX       Mouse x coordinate
	 * @param mouseY       Mouse y coordinate
	 */
	@Override
	protected void drawGuiContainerBackgroundLayer(final float partialTicks, final int mouseX, final int mouseY) {
		GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);

		this.mc.getTextureManager().bindTexture(CF_GUI_TEXTURE);

		final int x = (width - xSize) / 2;
		final int y = (height - ySize) / 2;
		drawTexturedModalRect(x, y, 0, 0, xSize, ySize);
	}
}
