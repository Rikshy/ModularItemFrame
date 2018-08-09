package de.shyrik.modularitemframe.client.render;

import de.shyrik.modularitemframe.ModularItemFrame;
import de.shyrik.modularitemframe.api.ModuleFrameBase;
import de.shyrik.modularitemframe.common.tile.TileModularFrame;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import org.lwjgl.opengl.GL11;

/**
 * Created by Demoniaque
 */
public class FrameRenderer extends TileEntitySpecialRenderer<TileModularFrame> {

    private IBakedModel modelFrame;

    /*public RenderMirror() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void reload(ClientProxy.ResourceReloadEvent event) {
        modelFrame = null;
    }*/

    private void getBakedModels(TileModularFrame te) {
        IModel model = null;
        if (modelFrame == null || te.reloadModel) {
            try {
                model = ModelLoaderRegistry.getModel(new ResourceLocation(ModularItemFrame.MOD_ID, "block/modular_frame"));
            } catch (Exception e) {
                e.printStackTrace();
            }
            //modelFrame = model.bake(model.getDefaultState(), DefaultVertexFormats.ITEM,
            //        location -> Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(location.toString()));
            modelFrame = model.bake(model.getDefaultState(), DefaultVertexFormats.ITEM,
                    location -> {
                        if (location.getResourcePath().contains("dummy"))
                            return Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(te.module.getModelLocation().toString());
                        return Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(location.toString());
                    });
            te.reloadModel = false;
        }
    }

    @Override
    public void render(TileModularFrame te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        getBakedModels(te);

        bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        if (Minecraft.isAmbientOcclusionEnabled())
            GlStateManager.shadeModel(GL11.GL_SMOOTH);
        else
            GlStateManager.shadeModel(GL11.GL_FLAT);


        GlStateManager.translate(x, y, z); // Translate pad to coords here
        GlStateManager.disableRescaleNormal();
        rotateFrameOnFacing(te.blockFacing(), 0);

        Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelRenderer().renderModelBrightnessColor(
                modelFrame, 1.0F, 1, 1, 1);

        GlStateManager.disableBlend();
        GlStateManager.popMatrix();

        te.module.specialRendering(x, y, z, partialTicks, destroyStage, alpha);
    }

    private void rotateFrameOnFacing(EnumFacing facing, int rotation) {
        switch (facing) {
            case NORTH:
                break;
            case SOUTH:
                GlStateManager.translate(1.0F, 0.0F, 1.0F);
                GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
                break;
            case WEST:
                GlStateManager.translate(0.0F, 0F, 1.0F);
                GlStateManager.rotate(90.0F, 0.0F, 1.0F, 0.0F);
                break;
            case EAST:
                GlStateManager.translate(1.0F, 0.0F, 0.0F);
                GlStateManager.rotate(-90.0F, 0.0F, 1.0F, 0.0F);
                break;
            case DOWN:
                GlStateManager.translate(0.0F, 0.0F, 1.0F);
                GlStateManager.rotate(-90.0F, 1.0F, 0.0F, 0.0F);
                break;
            case UP:
                GlStateManager.translate(0.0F, 1.0F, 0.0F);
                GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
        }
        GlStateManager.rotate(rotation, 0.0F, 0.0F, 1.0F);
    }
}
