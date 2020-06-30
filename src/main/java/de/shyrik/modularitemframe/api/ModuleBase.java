package de.shyrik.modularitemframe.api;

import com.mojang.blaze3d.matrix.MatrixStack;
import de.shyrik.modularitemframe.client.render.FrameRenderer;
import de.shyrik.modularitemframe.common.block.BlockModularFrame;
import de.shyrik.modularitemframe.common.block.TileModularFrame;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class ModuleBase implements INBTSerializable<CompoundNBT> {

    protected TileModularFrame tile;
    ItemModule parent;

    protected final String NBT_RELOADMODEL = "reloadmodel";

    public void setTile(TileModularFrame te) {
        tile = te;
    }

    public ItemModule getParent() { return parent; }

    public abstract ResourceLocation getId();

    /**
     * Is called when the {@link FrameRenderer} wants to render the module for the first time.
     * @see #bakeModel(IModel)
     *
     * @return [Nonnull] {@link ResourceLocation} to the Texture
     */
    @Nonnull
    @OnlyIn(Dist.CLIENT)
    public abstract ResourceLocation frontTexture();

    /**
     * Is called when the {@link FrameRenderer} wants to render the module for the first time.
     * @see #bakeModel(IModel)
     *
     * @return [Nonnull] {@link ResourceLocation} to the Texture
     */
    @Nonnull
    @OnlyIn(Dist.CLIENT)
    public ResourceLocation innerTexture() {
        return BlockModularFrame.INNER_DEF_LOC;
    }

    /**
     * Is called when the {@link FrameRenderer} wants to render the module for the first time.
     * @see #bakeModel(IModel)
     *
     * @return [Nonnull] {@link ResourceLocation} to the Texture
     */
    @Nonnull
    @OnlyIn(Dist.CLIENT)
    public ResourceLocation backTexture() {
        return new ResourceLocation("minecraft", "block/stripped_birch_log_top");
    }

    /**
     * TOP and WAILA are using this for display
     * Please use translation holders - raw strings are bad!
     *
     * @return the name of the module :O
     */
    public abstract String getModuleName();

    public boolean reloadModel = false;
    private IBakedModel bakedModel = null;

    /**
     * Called by the {@link FrameRenderer} to bake the Frame model
     * by default {@link #frontTexture()} and {@link #backTexture()} will be asked to be replaced
     * override this with caution.
     *
     * @param model Contains the model of the frame
     * @return baked model ofc
     */
    @OnlyIn(Dist.CLIENT)
    public IBakedModel bakeModel(ModelBakery bakery, IUnbakedModel model) {
        if (bakedModel == null || reloadModel) {
            bakedModel = model.bakeModel(bakery, mat -> {
                if (mat.getTextureLocation().toString().contains("default_front"))
                    return Minecraft.getInstance().getModelManager().getAtlasTexture(mat.getAtlasLocation()).getSprite(frontTexture());
                if (mat.getTextureLocation().toString().contains("default_back"))
                    return Minecraft.getInstance().getModelManager().getAtlasTexture(mat.getAtlasLocation()).getSprite(backTexture());
                if (mat.getTextureLocation().toString().contains("default_inner"))
                    return Minecraft.getInstance().getModelManager().getAtlasTexture(mat.getAtlasLocation()).getSprite(innerTexture());
                return Minecraft.getInstance().getModelManager().getAtlasTexture(mat.getAtlasLocation()).getSprite(mat.getTextureLocation());
            }, ModelRotation.X0_Y0, BlockModularFrame.LOC);

            reloadModel = false;
        }
        return bakedModel;
    }

    /**
     * Called by the {@link FrameRenderer} after rendering the frame.
     * Extra rendering can be don here
     * like the {@link ModuleItem ModuleItem} does the item thing)
     *
     * @param tesr instance of the current {@link FrameRenderer}
     */
    @OnlyIn(Dist.CLIENT)
    public void specialRendering(FrameRenderer tesr, @Nonnull MatrixStack matrixStack, float partialTicks, @Nonnull IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay) {
    }

    /**
     * Called when the frame got left clicked
     */
    public void onBlockClicked(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull PlayerEntity player) {
    }

    /**
     * Called when a {@link de.shyrik.modularitemframe.common.item.ItemScrewdriver screwdriver} in interaction mode clicks a frame
     * Implement behavior for {@link de.shyrik.modularitemframe.common.item.ItemScrewdriver screwdriver} interaction here
     *
     * @param driver the driver who was used
     */
    public void screw(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull PlayerEntity player, ItemStack driver) {

    }

    public void onFrameUpgradesChanged() {

    }

    /**
     * Called when a frame is simply right clicked
     */
    public abstract ActionResultType onBlockActivated(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nonnull PlayerEntity player, @Nonnull Hand hand, @Nonnull Direction facing, BlockRayTraceResult trace);

    /**
     * in case your module has a gui
     */
    public INamedContainerProvider getContainer(BlockState state, World world, BlockPos pos) {
        return null;
    }

    /**
     * called when the tile entity ticks
     */
    public void tick(@Nonnull World world, @Nonnull BlockPos pos) {
    }

    /**
     * Called when module is removed with the {@link de.shyrik.modularitemframe.common.item.ItemScrewdriver screwdriver}
     * or destroyed.
     */
    public void onRemove(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull Direction facing, @Nullable PlayerEntity player) {
    }

    /**
     * NBT serialization in case there are some data to be saved!
     * this gets synced automatically
     */
    @Nonnull
    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT cmp = new CompoundNBT();
        cmp.putBoolean(NBT_RELOADMODEL, reloadModel);
        return cmp;
    }

    /**
     * NBT deserialization in case there are some data to be saved!
     */
    @Override
    public void deserializeNBT(CompoundNBT cmp) {
        if (cmp.contains(NBT_RELOADMODEL)) reloadModel = cmp.getBoolean(NBT_RELOADMODEL);
    }
}
