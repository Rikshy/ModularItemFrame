package de.shyrik.justcraftingframes.common.module;

import com.teamwizardry.librarianlib.features.saving.Save;
import de.shyrik.justcraftingframes.JustCraftingFrames;
import de.shyrik.justcraftingframes.api.ModuleFrameBase;
import de.shyrik.justcraftingframes.api.utils.RenderUtils;
import de.shyrik.justcraftingframes.common.tile.TileModularFrame;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class ModuleItem extends ModuleFrameBase {

    @Save
    public int rotation = 0;

    @Save
    public ItemStack displayItem = ItemStack.EMPTY;

    public ModuleItem(TileModularFrame te) {
        super(te);
    }

    @Nonnull
    public ResourceLocation getModelLocation() {
        return new ResourceLocation(JustCraftingFrames.MOD_ID, "blocks/item_frame_bg");
    }

    protected float scale = 0.9f;
    protected float offset = 0.05F;

    public void rotate(EntityPlayer player) {
        if (player.isSneaking()) {
            rotation += 20;
        } else {
            rotation -= 20;
        }
        tile.markDirty();
    }

    @Override
    public void specialRendering(double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(x + 0.5D, y + 0.5D, z + 0.5D);
        GlStateManager.scale(scale, scale, scale);
        GlStateManager.pushMatrix();

        RenderUtils.renderItem(displayItem, tile.blockFacing(), tile.rotation, offset);

        GlStateManager.popMatrix();
        GlStateManager.popMatrix();
    }

    @Override
    public void onBlockClicked(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull EntityPlayer playerIn) {
        if(!worldIn.isRemote) {
            rotate(playerIn);
            tile.markDirty();
        }
    }

    @Override
    public void onBlockActivated(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull EntityPlayer playerIn, @Nonnull EnumHand hand, @Nonnull EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (!worldIn.isRemote) {
            if(!playerIn.isSneaking()) {
                ItemStack copy = playerIn.getHeldItem(hand).copy();
                copy.setCount(1);
                displayItem = copy;
                tile.markDirty();
            }
        }
    }
}
