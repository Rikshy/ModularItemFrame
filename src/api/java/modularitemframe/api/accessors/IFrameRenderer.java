package modularitemframe.api.accessors;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fluids.FluidStack;

public interface IFrameRenderer {
    //region <itemRender>
    default void renderItem(ItemStack stack, MatrixStack matrixStack, IRenderTypeBuffer buffer, int light, int overlay) {
        renderItem(stack, 0F, 0.5F, ItemCameraTransforms.TransformType.FIXED, matrixStack, buffer, light, overlay);
    }
    default void renderItem(ItemStack stack, float zRotation, MatrixStack matrixStack, IRenderTypeBuffer buffer, int light, int overlay) {
        renderItem(stack, zRotation, 0.5F, ItemCameraTransforms.TransformType.FIXED, matrixStack, buffer, light, overlay);
    }

    default void renderItem(ItemStack stack, float zRotation, float scale, ItemCameraTransforms.TransformType transformType, MatrixStack matrixStack, IRenderTypeBuffer buffer, int light, int overlay) {
        renderItem(stack, 0F, zRotation, scale, transformType, matrixStack, buffer, light, overlay);
    }

    void renderItem(ItemStack stack, float xRotation, float zRotation, float scale, ItemCameraTransforms.TransformType transformType, MatrixStack matrixStack, IRenderTypeBuffer buffer, int light, int overlay);
    //endregion <itemRender>

    //region <fluid>
    void renderFluid(FluidStack fluid, FluidRenderFace f, MatrixStack matrixStack, IRenderTypeBuffer buffer);

    class FluidRenderFace {

        public final double x0, y0, z0, u0, v0;
        public final double x1, y1, z1, u1, v1;
        public final double x2, y2, z2, u2, v2;
        public final double x3, y3, z3, u3, v3;

        public FluidRenderFace(
                double _x0, double _y0, double _z0, double _u0, double _v0, //
                double _x1, double _y1, double _z1, double _u1, double _v1, //
                double _x2, double _y2, double _z2, double _u2, double _v2, //
                double _x3, double _y3, double _z3, double _u3, double _v3 ) {
            x0 = _x0;
            y0 = _y0;
            z0 = _z0;
            u0 = _u0;
            v0 = _v0;

            x1 = _x1;
            y1 = _y1;
            z1 = _z1;
            u1 = _u1;
            v1 = _v1;

            x2 = _x2;
            y2 = _y2;
            z2 = _z2;
            u2 = _u2;
            v2 = _v2;

            x3 = _x3;
            y3 = _y3;
            z3 = _z3;
            u3 = _u3;
            v3 = _v3;
        }

        public static FluidRenderFace create(double x0, double y0, double z0, double x1, double y1, double z1) {
            return new FluidRenderFace(
                    x0, y0, z1, x0, y0, //
                    x1, y0, z1, x1, y0, //
                    x1, y1, z1, x1, y1, //
                    x0, y1, z1, x0, y1
            );
        }

        public float getU(TextureAtlasSprite still, double u) {
            return MathHelper.lerp((float) u, still.getMinU(), still.getMaxU());
        }

        public float getV(TextureAtlasSprite still, double v) {
            return MathHelper.lerp((float) v, still.getMinV(), still.getMaxV());
        }
    }
    //endregion <fluid>

    //region <ender>
    void renderEnder(MatrixStack matrixStack, IRenderTypeBuffer bufferBuilder, float offset1, float offset2, float offset3);
    //endregion <ender>
}
