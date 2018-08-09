package de.shyrik.modularitemframe.client.gui;

import de.shyrik.modularitemframe.ModularItemFrame;
import de.shyrik.modularitemframe.common.container.ContainerCraftingFrame;
import de.shyrik.modularitemframe.common.container.GhostSlot;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class GuiCraftingFrame extends GuiContainer {

	/**
	 * The ResourceLocation containing the chest GUI texture.
	 */
	private static final ResourceLocation CF_GUI_TEXTURE = new ResourceLocation(ModularItemFrame.MOD_ID,"textures/gui/crafting_frame.png");

	public GuiCraftingFrame(final ContainerCraftingFrame container) {
		super(container);

		allowUserInput = true;
	}

	@Override
	protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
		Slot slot = this.getSlotAtPosition(mouseX, mouseY);
		ItemStack itemstack = mc.player.inventory.getItemStack();

		if (slot instanceof GhostSlot && Container.canAddItemToSlot(slot, itemstack, true) && slot.isItemValid(itemstack) && inventorySlots.canDragIntoSlot(slot)) {
			dragSplittingSlots.add(slot);
			dragSplitting = true;
		} else {
			super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
		}
	}

	private Slot getSlotAtPosition(int x, int y) {
		for (int i = 0; i < inventorySlots.inventorySlots.size(); ++i) {
			Slot slot = inventorySlots.inventorySlots.get(i);

			if (isPointInRegion(slot.xPos, slot.yPos, 16, 16, x, y) && slot.isEnabled()) {
				return slot;
			}
		}
		return null;
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

		mc.getTextureManager().bindTexture(CF_GUI_TEXTURE);

		final int x = (width - xSize) / 2;
		final int y = (height - ySize) / 2;
		drawTexturedModalRect(x, y, 0, 0, xSize, ySize);
	}
}
