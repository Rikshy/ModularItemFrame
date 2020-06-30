package de.shyrik.modularitemframe.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import de.shyrik.modularitemframe.ModularItemFrame;
import de.shyrik.modularitemframe.common.block.TileModularFrame;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Quaternion;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.*;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;

import javax.annotation.Nonnull;

public class FrameRenderer extends TileEntityRenderer<TileModularFrame> {

    private IUnbakedModel model = null;

    public FrameRenderer(TileEntityRendererDispatcher rendererDispatcherIn) {
        super(rendererDispatcherIn);
    }

    public TileEntityRendererDispatcher getDispatcher() { return renderDispatcher; }

    private IBakedModel getBakedModel(TileModularFrame te) {
        if (model == null) {
            try {
                model = ModelLoader.instance().getUnbakedModel(new ResourceLocation(ModularItemFrame.MOD_ID,"block/modular_frame"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return te.module.bakeModel(ModelLoader.instance(), model);
    }

    @Override
    public void render(@Nonnull TileModularFrame te, float partialTicks, @Nonnull MatrixStack matrixStack, @Nonnull IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay) {
        matrixStack.push();
        IBakedModel modelFrame = getBakedModel(te);

        rotateFrameOnFacing(te.blockFacing(), matrixStack);

        Minecraft.getInstance()
                .getBlockRendererDispatcher()
                .getBlockModelRenderer()
                .renderModel(
                        matrixStack.getLast(),
                        buffer.getBuffer(RenderType.getTranslucentNoCrumbling()),
                        te.getBlockState(),
                        modelFrame,
                        1,
                        1,
                        1,
                        combinedLight,
                        combinedOverlay,
                        te.getModelData()
                );

        matrixStack.pop();

        te.module.specialRendering(this, matrixStack, partialTicks, buffer, combinedLight, combinedOverlay);
    }

    private void rotateFrameOnFacing(Direction facing, @Nonnull MatrixStack matrixStack) {
        switch (facing) {
            case NORTH:
                matrixStack.translate(1.0F, 0.0F, 1.0F);
                matrixStack.rotate(new Quaternion( 0.0F, 180.0F, 0.0F, true));
                break;
            case SOUTH:
                break;
            case WEST:
                matrixStack.rotate(new Quaternion(90.0F, -90.0F, 90.0F, true));
                matrixStack.translate(0.0F, 0.0F, -1.0F);
                break;
            case EAST:
                matrixStack.rotate(new Quaternion(-90.0F, 90.0F, 90.0F, true));
                matrixStack.translate(-1.0F, 0.0F, 0.0F);
                break;
            case DOWN:
                matrixStack.translate(0.0F, 1.0F, 0.0F);
                matrixStack.rotate(new Quaternion(90.0F, 0.0F, 0.0F, true));
                break;
            case UP:
                matrixStack.translate(0.0F, 0.0F, 1.0F);
                matrixStack.rotate(new Quaternion(-90.0F, 0.0F, 0.0F, true));
                break;
        }
    }
}
