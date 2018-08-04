package de.shyrik.justcraftingframes.client.render;

import de.shyrik.justcraftingframes.common.block.BlockFrameBase;
import de.shyrik.justcraftingframes.common.tile.TileCraftingFrame;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;

public class FrameItemRenderer extends TileEntitySpecialRenderer<TileCraftingFrame> {

    @Override
    public void render(TileCraftingFrame te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(x + 0.5D, y + 0.5D, z + 0.5D);
        GlStateManager.scale(0.8F, 0.8F, 0.8F);
        GlStateManager.pushMatrix();

        rotateOnFacing(te);
        renderItem(te.displayedItem);

        GlStateManager.popMatrix();
        GlStateManager.popMatrix();
    }

    private void rotateOnFacing(TileCraftingFrame te) {
        EnumFacing facing = te.getWorld().getBlockState(te.getPos()).getValue(BlockFrameBase.FACING);

        switch (facing) {
            case NORTH:
                break;
            case SOUTH:
                GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
                break;
            case WEST:
                GlStateManager.rotate(90.0F, 0.0F, 1.0F, 0.0F);
                break;
            case EAST:
                GlStateManager.rotate(-90.0F, 0.0F, 1.0F, 0.0F);
                break;
            case DOWN:
                GlStateManager.rotate(-90.0F, 1.0F, 0.0F, 0.0F);
                break;
            case UP:
                GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
        }
        GlStateManager.translate(0.0F, 0.0F, -0.5125F);
    }

    private void renderItem(ItemStack stack) {
        if (!stack.isEmpty()) {
            RenderItem itemRenderer = Minecraft.getMinecraft().getRenderItem();

            GlStateManager.pushMatrix();
            GlStateManager.disableLighting();

            GlStateManager.scale(0.5F, 0.5F, 0.5F);
            GlStateManager.pushAttrib();
            RenderHelper.enableStandardItemLighting();
            if (itemRenderer.shouldRenderItemIn3D(stack)) {
                GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
            } else {
                GlStateManager.scale(0.9F, 0.9F, 0.9F);
            }
            itemRenderer.renderItem(stack, ItemCameraTransforms.TransformType.FIXED);
            RenderHelper.disableStandardItemLighting();
            GlStateManager.popAttrib();

            GlStateManager.enableLighting();
            GlStateManager.popMatrix();
        }
    }
}
