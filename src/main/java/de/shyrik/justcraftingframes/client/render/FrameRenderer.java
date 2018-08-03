package de.shyrik.justcraftingframes.client.render;

import de.shyrik.justcraftingframes.common.block.BlockFrameBase;
import de.shyrik.justcraftingframes.common.tile.TileFrameBase;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;

public class FrameRenderer extends TileEntitySpecialRenderer<TileFrameBase> {

    @Override
    public void render(TileFrameBase te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(x + 0.5D, y + 0.5D, z + 0.5D);
        GlStateManager.scale(0.75F, 0.75F, 0.75F);
        GlStateManager.pushMatrix();

        rotateOnFacing(te);
        renderItem(te.getDisplayedItem());

        GlStateManager.popMatrix();
        GlStateManager.popMatrix();
    }

    private void rotateOnFacing(TileFrameBase te) {
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
        GlStateManager.translate(0.0F, 0.0F, -0.65F);
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
            }
            itemRenderer.renderItem(stack, ItemCameraTransforms.TransformType.FIXED);
            RenderHelper.disableStandardItemLighting();
            GlStateManager.popAttrib();

            GlStateManager.enableLighting();
            GlStateManager.popMatrix();
        }
    }
}
