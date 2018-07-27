package de.shyrik.justcraftingframes.client.render;

import de.shyrik.justcraftingframes.JustCraftingFrames;
import de.shyrik.justcraftingframes.common.block.BlockFrameBase;
import de.shyrik.justcraftingframes.common.tile.TileFrameBase;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import org.lwjgl.opengl.GL11;

public class FrameRenderer extends TileEntitySpecialRenderer<TileFrameBase> {

    private final RenderItem itemRenderer;
    private IBakedModel backedModel;

    public FrameRenderer() {
        itemRenderer = Minecraft.getMinecraft().getRenderItem();
    }

    private void bakeModel(TileFrameBase te) {
        if (backedModel == null) {
            try {
                IModel model = ModelLoaderRegistry.getModel(new ResourceLocation(JustCraftingFrames.MOD_ID, "block/crafting_frame"));

                backedModel = model.bake(model.getDefaultState(), DefaultVertexFormats.ITEM,
                        location -> Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(location.toString()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void render(TileFrameBase te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {

        /*GlStateManager.pushMatrix();
        GlStateManager.translate(x + 0.5D, y + 0.5D, z + 0.5D);
        GlStateManager.scale(0.5F, 0.5F, 0.5F);
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        bakeModel(te);

        Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelRenderer().renderModelBrightnessColor(backedModel, 1.0F, 1.0F, 1.0F, 1.0F);

        GlStateManager.disableBlend();
        GlStateManager.popMatrix();*/
        renderItem(te);
    }

    private void rotateOnFacing(TileFrameBase te) {
        IBlockState state = te.getWorld().getBlockState(te.getPos());

        GlStateManager.rotate(-90.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.translate(0.0F, 0.0F, -0.9375F);

        switch (state.getValue(BlockFrameBase.FACING)) {
            case NORTH:
                break;
            case SOUTH:
                GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
                break;
            case EAST:
                GlStateManager.rotate(-90.0F, 0.0F, 0.0F, 1.0F);
                break;
            case WEST:
                GlStateManager.rotate(90.0F, 0.0F, 0.0F, 1.0F);
        }
    }

    private void renderItem(TileFrameBase te) {
        ItemStack itemstack = te.getDisplayedItem();

        if (!itemstack.isEmpty()) {
            GlStateManager.pushMatrix();
            GlStateManager.disableLighting();

            GlStateManager.scale(0.5F, 0.5F, 0.5F);
            GlStateManager.pushAttrib();
            RenderHelper.enableStandardItemLighting();
            if (itemRenderer.shouldRenderItemIn3D(itemstack)) {
                GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
            }
            itemRenderer.renderItem(itemstack, ItemCameraTransforms.TransformType.FIXED);
            RenderHelper.disableStandardItemLighting();
            GlStateManager.popAttrib();

            GlStateManager.enableLighting();
            GlStateManager.popMatrix();
        }
    }
}
