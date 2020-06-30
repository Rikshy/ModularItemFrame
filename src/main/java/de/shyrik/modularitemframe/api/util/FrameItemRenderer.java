package de.shyrik.modularitemframe.api.util;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.Quaternion;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;

import javax.annotation.Nonnull;

public class FrameItemRenderer {
    private static void translate(MatrixStack matrixStack, @Nonnull Direction facing, float offset) {
        switch (facing) {
            case NORTH:
                matrixStack.translate(0.5F, 0.5F, 1 - offset);
                break;
            case SOUTH:
                matrixStack.translate(0.5F, 0.5F, offset);
                break;
            case WEST:
                matrixStack.translate(1 - offset, 0.5F, 0.5F);
                break;
            case EAST:
                matrixStack.translate(offset, 0.5F, 0.5F);
                break;
            case DOWN:
                matrixStack.translate(0.5F, 1 - offset, 0.5F);
                break;
            case UP:
                matrixStack.translate(0.5F, offset, 0.5F);
                break;
        }
    }
    private static void rotate(MatrixStack matrixStack, @Nonnull Direction facing, float rotation) {
        switch (facing) {
            case NORTH:
                matrixStack.rotate(new Quaternion(0.0F, 0.0F, -rotation, true));
                break;
            case SOUTH:
                matrixStack.rotate(new Quaternion(0.0F, 0.0F, rotation, true));
                break;
            case WEST:
                matrixStack.rotate(new Quaternion(-rotation, 0.0F, 0.0F, true));
                break;
            case EAST:
                matrixStack.rotate(new Quaternion(rotation, 0.0F, 0.0F, true));
                break;
            case DOWN:
                matrixStack.rotate(new Quaternion(0.0F, -rotation, 0.0F, true));
                break;
            case UP:
                matrixStack.rotate(new Quaternion(0.0F, rotation, 0.0F, true));
                break;
        }
    }

    public static void renderOnFrame(ItemStack stack, @Nonnull Direction facing, float rotation, float offset, ItemCameraTransforms.TransformType transformType, MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay) {
        if (!stack.isEmpty()) {
            matrixStack.push();

            ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();

            translate(matrixStack, facing, offset);
            rotate(matrixStack, facing, rotation);
            matrixStack.scale(0.5F, 0.5F, 0.5F);
            RenderHelper.enableStandardItemLighting();

            IBakedModel model = itemRenderer.getItemModelWithOverrides(stack, null, null);
            if (model.isGui3d()) {
                matrixStack.rotate(new Quaternion(0F, 180.0F, 0.0F, true));
            }
            itemRenderer.renderItem(stack, transformType, combinedLight, combinedOverlay, matrixStack, buffer);
            RenderHelper.disableStandardItemLighting();

            matrixStack.pop();
        }
    }
}
