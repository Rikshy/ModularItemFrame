package de.shyrik.modularitemframe.client;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import de.shyrik.modularitemframe.ModularItemFrame;
import de.shyrik.modularitemframe.common.block.ModularFrameTile;
import de.shyrik.modularitemframe.common.module.EmptyModule;
import de.shyrik.modularitemframe.init.Blocks;
import modularitemframe.api.accessors.IFrameRenderer;
import modularitemframe.api.ModuleBase;
import modularitemframe.api.ModuleItem;
import modularitemframe.api.UpgradeBase;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Atlases;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.*;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.IntStream;

@OnlyIn(Dist.CLIENT)
public class FrameRenderer extends TileEntityRenderer<ModularFrameTile> implements IFrameRenderer {

    private IBakedModel model = null;
    private ResourceLocation currentFront = null;

    private final Random RANDOM = new Random(31100L);
    private final List<RenderType> layers =
            IntStream.range(0, 16).mapToObj((i) -> RenderType.getEndPortal(i + 1)).collect(ImmutableList.toImmutableList());

    private static final Map<ResourceLocation, IBakedModel> models = new HashMap<>();

    public FrameRenderer(TileEntityRendererDispatcher rendererDispatcherIn) {
        super(rendererDispatcherIn);
    }

    @SubscribeEvent
    public static void onModelBake(ModelBakeEvent event) {
        IUnbakedModel unbakedFrame = event.getModelLoader().getModelOrLogError(new ResourceLocation(ModularItemFrame.MOD_ID, "block/modular_frame"), "");
        ModelManager modelMan = event.getModelManager();

        ResourceLocation modelId = Blocks.MODULAR_FRAME.getId();

        ModuleItem.getModuleIds().forEach(id -> {
            ModuleBase module = ModuleItem.createModule(id);
            assert module != null;
            for (ResourceLocation front : module.getVariantFronts()) {
                IBakedModel bakedFrame = unbakedFrame.bakeModel(event.getModelLoader(), mat -> {
                    if (mat.getTextureLocation().toString().contains("default_front"))
                        return modelMan.getAtlasTexture(mat.getAtlasLocation()).getSprite(front);
                    if (mat.getTextureLocation().toString().contains("default_back"))
                        return modelMan.getAtlasTexture(mat.getAtlasLocation()).getSprite(module.backTexture());
                    if (mat.getTextureLocation().toString().contains("default_inner"))
                        return modelMan.getAtlasTexture(mat.getAtlasLocation()).getSprite(module.moduleTier().innerTex);
                    return modelMan.getAtlasTexture(mat.getAtlasLocation()).getSprite(mat.getTextureLocation());
                }, ModelRotation.X0_Y0, modelId);

                models.put(front, bakedFrame);
            }
        });

        models.put(EmptyModule.FG,
                unbakedFrame.bakeModel(event.getModelLoader(), mat ->
                        modelMan.getAtlasTexture(mat.getAtlasLocation()).getSprite(mat.getTextureLocation()),
                        ModelRotation.X0_Y0,
                        modelId));
    }

    private IBakedModel getBakedModel(ModuleBase module) {
        if (currentFront != module.frontTexture()) {
            currentFront = module.frontTexture();
            model = models.get(currentFront);
        }

        return model;
    }

    @Override
    public void render(ModularFrameTile frame, float partialTicks, MatrixStack matrixStack, @NotNull IRenderTypeBuffer buffer, int light, int overlay) {
        matrixStack.push();
        ModuleBase module = frame.getModule();
        IBakedModel modelFrame = getBakedModel(module);

        rotateFrameOnFacing(frame.getFacing(), matrixStack);

        Minecraft.getInstance()
                .getBlockRendererDispatcher()
                .getBlockModelRenderer()
                .renderModel(
                        matrixStack.getLast(),
                        buffer.getBuffer(RenderType.getTranslucentNoCrumbling()),
                        frame.getBlockState(),
                        modelFrame,
                        1,
                        1,
                        1,
                        light,
                        overlay,
                        frame.getModelData()
                );

        matrixStack.push();
        module.specialRendering(this, partialTicks, matrixStack, buffer, light, overlay);
        matrixStack.pop();

        renderUpgrades(frame, matrixStack, buffer, light, overlay);

        matrixStack.pop();
    }

    private void rotateFrameOnFacing(Direction facing, MatrixStack matrixStack) {
        matrixStack.translate(0.5F, 0.5F, 0.5F);
        switch (facing) {
            case UP:
                matrixStack.rotate(Vector3f.XP.rotationDegrees(-90F));
                break;
            case DOWN:
                matrixStack.rotate(Vector3f.XP.rotationDegrees(90F));
                break;
            default:
                matrixStack.rotate(Vector3f.YP.rotationDegrees(-facing.getHorizontalAngle()));
        }
        matrixStack.translate(-0.5F, -0.5F, -0.5F);
    }


    private void renderUpgrades(ModularFrameTile frame, MatrixStack matrixStack, IRenderTypeBuffer buffer, int light, int overlay) {
        if (frame.getUpgradeCount() == 0) return;

        matrixStack.push();

        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();

        int i = 0;
        for (UpgradeBase up : frame.getUpgrades()) {
            matrixStack.push();

            int side = i == 0 ? 0 : i / 5;
            int pos = i % 5;

            float sideOffset = side == 0 ? 0.85F : 0.15F;
            float posOffset = pos * 0.05F;

            if (side == 0 || side == 1) {
                matrixStack.translate(0.4F + posOffset, sideOffset, 0.13F);
            } else {
                matrixStack.translate(sideOffset, 0.6F - posOffset, 0.13F);
            }
            matrixStack.scale(0.05F, 0.05F, 0.05F);

            ItemStack renderStack = up.getItem().getDefaultInstance();
            itemRenderer.renderItem(renderStack, ItemCameraTransforms.TransformType.GUI, light, overlay, matrixStack, buffer);

            i++;
            matrixStack.pop();
        }

        matrixStack.pop();
    }

    //region <itemRender>
    public void renderItem(ItemStack stack, float xRotation, float zRotation, float scale, ItemCameraTransforms.TransformType transformType, MatrixStack matrixStack, IRenderTypeBuffer buffer, int light, int overlay) {
        if (stack.isEmpty())
            return;

        matrixStack.push();

        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();

        matrixStack.translate(0.5F, 0.5F, 0.1F);
        matrixStack.rotate(new Quaternion(xRotation, 0.0F, zRotation, true));
        matrixStack.scale(scale, scale, scale);

        matrixStack.rotate(new Quaternion(0F, 180.0F, 0.0F, true));
        itemRenderer.renderItem(stack, transformType, light, overlay, matrixStack, buffer);

        matrixStack.pop();
    }
    //endregion <itemRender>

    //region <fluid>
    public void renderFluid(FluidStack fluid, FluidRenderFace f, MatrixStack matrixStack, IRenderTypeBuffer buffer) {
        int color = fluid.getFluid().getAttributes().getColor();
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = (color >> 0) & 0xFF;

        RenderType renderType;
        Fluid raw = fluid.getRawFluid();
        if (raw == null) {
            renderType = RenderType.getTranslucent();
        } else {
            renderType = Atlases.getTranslucentCullBlockType();
        }
        IVertexBuilder builder = buffer.getBuffer(renderType);

        AtlasTexture atlas = Minecraft.getInstance().getModelManager().getAtlasTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE);
        TextureAtlasSprite still = atlas.getSprite(fluid.getFluid().getAttributes().getStillTexture());

        vertex(builder, matrixStack, f.x0, f.y0, f.z0, f.getU(still, f.u0), f.getV(still, f.v0), r, g, b, f);
        vertex(builder, matrixStack, f.x1, f.y1, f.z1, f.getU(still, f.u1), f.getV(still, f.v1), r, g, b, f);
        vertex(builder, matrixStack, f.x2, f.y2, f.z2, f.getU(still, f.u2), f.getV(still, f.v2), r, g, b, f);
        vertex(builder, matrixStack, f.x3, f.y3, f.z3, f.getU(still, f.u3), f.getV(still, f.v3), r, g, b, f);
    }

    protected void vertex(
            IVertexBuilder vc, MatrixStack matrices, double x, double y, double z, float u, float v, int r, int g, int b,
            FluidRenderFace f
    ) {
        vc.pos(matrices.getLast().getMatrix(), (float) x, (float) y, (float) z);
        vc.color(r, g, b, 0xFF);
        vc.tex(u, v);
        vc.overlay(OverlayTexture.NO_OVERLAY);
        vc.lightmap(0x00F0_00F0);
        vc.normal(matrices.getLast().getNormal(), 0, 0, 1);
        vc.endVertex();
    }
    //endregion <fluid>

    //region <ender>
    public void renderEnder(MatrixStack matrixStack, IRenderTypeBuffer bufferBuilder, float offset1, float offset2, float offset3) {
        double distance = renderDispatcher.renderInfo.getBlockPos().distanceSq(renderDispatcher.cameraHitResult.getHitVec(), true);
        int val = getPasses(distance);
        Matrix4f matrix4f = matrixStack.getLast().getMatrix();

        enderMagic(0.15F, matrix4f, bufferBuilder.getBuffer(layers.get(0)), offset1, offset2, offset3);

        for (int i = 1; i < val; ++i) {
            enderMagic(2.0F / (float) (18 - i), matrix4f, bufferBuilder.getBuffer(layers.get(i)), offset1, offset2, offset3);
        }
    }

    private void enderMagic(float colorMultiplier, Matrix4f matrix, IVertexBuilder buffer, float offset1, float offset2, float offset3) {
        float red = (RANDOM.nextFloat() * 0.5F + 0.1F) * colorMultiplier;
        float blue = (RANDOM.nextFloat() * 0.5F + 0.4F) * colorMultiplier;
        float green = (RANDOM.nextFloat() * 0.5F + 0.5F) * colorMultiplier;

        buffer.pos(matrix, offset1, offset1, offset2).color(red, blue, green, 1.0F).endVertex();;
        buffer.pos(matrix, offset3, offset1, offset2).color(red, blue, green, 1.0F).endVertex();
        buffer.pos(matrix, offset3, offset3, offset2).color(red, blue, green, 1.0F).endVertex();
        buffer.pos(matrix, offset1, offset3, offset2).color(red, blue, green, 1.0F).endVertex();
    }

    private int getPasses(double d) {
        if (d > 36864.0D) {
            return 1;
        } else if (d > 25600.0D) {
            return 3;
        } else if (d > 16384.0D) {
            return 5;
        } else if (d > 9216.0D) {
            return 7;
        } else if (d > 4096.0D) {
            return 9;
        } else if (d > 1024.0D) {
            return 11;
        } else if (d > 576.0D) {
            return 13;
        } else {
            return d > 256.0D ? 14 : 15;
        }
    }
    //endregion <ender>
}
