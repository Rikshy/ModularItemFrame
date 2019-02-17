package de.shyrik.modularitemframe.common.module.t1;

import de.shyrik.modularitemframe.ModularItemFrame;
import de.shyrik.modularitemframe.api.ModuleBase;
import de.shyrik.modularitemframe.api.utils.RenderUtils;
import de.shyrik.modularitemframe.client.render.FrameRenderer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

public class ModuleItem extends ModuleBase {

    public static final ResourceLocation LOC = new ResourceLocation(ModularItemFrame.MOD_ID, "module_t1_item");
    public static final ResourceLocation BG_LOC = new ResourceLocation(ModularItemFrame.MOD_ID, "blocks/module_t1_item");
    private static final String NBT_DISPLAY = "display";
    private static final String NBT_ROTATION = "rotation";

    private int rotation = 0;
    private ItemStack displayItem = ItemStack.EMPTY;

    @Nonnull
    @OnlyIn(Dist.CLIENT)
    public ResourceLocation frontTexture() {
        return BG_LOC;
    }

    @Override
    public String getModuleName() {
        return I18n.format("modularitemframe.module.item");
    }

    private void rotate(EntityPlayer player) {
        if (player.isSneaking()) {
            rotation += 20;
        } else {
            rotation -= 20;
        }
        if (rotation >= 360 || rotation <= -360) rotation = 0;
        tile.markDirty();
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void specialRendering(FrameRenderer renderer, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        GlStateManager.pushMatrix();
        GlStateManager.translated(x + 0.5D, y + 0.5D, z + 0.5D);
        GlStateManager.scalef(0.9f, 0.9f, 0.9f);
        GlStateManager.pushMatrix();

        RenderUtils.renderItem(displayItem, tile.blockFacing(), rotation, 0.05F, ItemCameraTransforms.TransformType.FIXED);

        GlStateManager.popMatrix();
        GlStateManager.popMatrix();
    }

    public void screw(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull EntityPlayer playerIn, ItemStack driver) {
        if (!world.isRemote) {
            rotate(playerIn);
            tile.markDirty();
        }
    }

    @Override
    public boolean onBlockActivated(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull EntityPlayer playerIn, @Nonnull EnumHand hand, @Nonnull EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (!worldIn.isRemote) {
            if (playerIn.isSneaking()) {
                ItemStack copy = playerIn.getHeldItem(hand).copy();
                copy.setCount(1);
                displayItem = copy;
                tile.markDirty();
            }
        }
        return true;
    }


    @Nonnull
    @Override
    public NBTTagCompound serializeNBT() {
        NBTTagCompound compound = super.serializeNBT();
        compound.put(NBT_DISPLAY, displayItem.serializeNBT());
        compound.putInt(NBT_ROTATION, rotation);
        return compound;
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt) {
        super.deserializeNBT(nbt);
        if (nbt.hasUniqueId(NBT_DISPLAY)) displayItem = new ItemStack(nbt.getCompound(NBT_DISPLAY));
        if (nbt.hasUniqueId(NBT_ROTATION)) rotation = nbt.getInt(NBT_ROTATION);
    }
}
