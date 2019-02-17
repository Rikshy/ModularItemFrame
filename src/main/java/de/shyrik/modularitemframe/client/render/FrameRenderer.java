package de.shyrik.modularitemframe.client.render;

import de.shyrik.modularitemframe.ModularItemFrame;
import de.shyrik.modularitemframe.common.tile.TileModularFrame;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import org.lwjgl.opengl.GL11;

public class FrameRenderer extends TileEntityRenderer<TileModularFrame> {

    private IModel model = null;

    private IBakedModel getBakedModels(TileModularFrame te) {
        if (model == null) {
            try {
                model = ModelLoaderRegistry.getModel(new ResourceLocation(ModularItemFrame.MOD_ID, "block/modular_frame"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return te.module.bakeModel(model);
    }

    @Override
    public void render(TileModularFrame te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        GlStateManager.pushMatrix();
        GlStateManager.disableCull();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        IBakedModel modelFrame = getBakedModels(te);

        bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        if (Minecraft.isAmbientOcclusionEnabled()) GlStateManager.shadeModel(GL11.GL_SMOOTH);
        else GlStateManager.shadeModel(GL11.GL_FLAT);


        GlStateManager.translated(x, y, z); // Translate pad to coords here
        GlStateManager.disableRescaleNormal();
        rotateFrameOnFacing(te.blockFacing(), 0);

        Minecraft.getInstance().getBlockRendererDispatcher().getBlockModelRenderer().renderModelBrightnessColor(modelFrame, 1.0F, 1, 1, 1);

        GlStateManager.disableBlend();
        GlStateManager.enableCull();
        GlStateManager.popMatrix();

        te.module.specialRendering(this, x, y, z, partialTicks, destroyStage, alpha);
    }

    public void bindTex(ResourceLocation location) {
        bindTexture(location);
    }

    private void rotateFrameOnFacing(EnumFacing facing, int rotation) {
        int r = Math.abs(rotation);
        switch (facing) {
            case NORTH:
                break;
            case SOUTH:
                GlStateManager.translatef(1.0F, 0.0F, 1.0F);
                GlStateManager.rotatef(180.0F, 0.0F, 1.0F, 0.0F);
                break;
            case WEST:
                GlStateManager.translatef(0.0F, 0F, 1.0F);
                GlStateManager.rotatef(90.0F, 0.0F, 1.0F, 0.0F);
                break;
            case EAST:
                GlStateManager.translatef(1.0F, 0.0F, 0.0F);
                GlStateManager.rotatef(-90.0F, 0.0F, 1.0F, 0.0F);
                break;
            case DOWN:
                GlStateManager.translatef(0.0F, 0.0F, 1.0F);
                GlStateManager.rotatef(-90.0F, 1.0F, 0.0F, 0.0F);
                break;
            case UP:
                GlStateManager.translatef(0.0F, 1.0F, 0.0F);
                GlStateManager.rotatef(90.0F, 1.0F, 0.0F, 0.0F);
        }
        GlStateManager.rotatef(rotation * 90.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.translatef((r == 1 || r == 2) ? -1 : 0, (r == 3 || r == 2) ? -1 : 0, 0);
    }
}
