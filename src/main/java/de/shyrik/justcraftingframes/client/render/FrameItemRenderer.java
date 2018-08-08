package de.shyrik.justcraftingframes.client.render;

import de.shyrik.justcraftingframes.common.block.BlockFrameBase;
import de.shyrik.justcraftingframes.common.tile.TileItemBaseFrame;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;

public class FrameItemRenderer extends TileEntitySpecialRenderer<TileItemBaseFrame> {

    @Override
    public void render(TileItemBaseFrame te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
    }
}
