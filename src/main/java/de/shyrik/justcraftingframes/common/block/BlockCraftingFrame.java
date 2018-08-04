package de.shyrik.justcraftingframes.common.block;

import de.shyrik.justcraftingframes.JustCraftingFrames;
import de.shyrik.justcraftingframes.client.gui.GuiHandler;
import de.shyrik.justcraftingframes.client.render.FrameItemRenderer;
import de.shyrik.justcraftingframes.common.tile.TileCraftingFrame;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;

public class BlockCraftingFrame extends BlockFrameBase {

    public BlockCraftingFrame() {
        super("crafting_frame");
    }

    @SideOnly(Side.CLIENT)
    public void initModel() {
        ClientRegistry.bindTileEntitySpecialRenderer(TileCraftingFrame.class, new FrameItemRenderer());
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(@NotNull World world, @NotNull IBlockState state) {
        return new TileCraftingFrame();
    }

    private TileCraftingFrame getTE(@Nonnull World world, @Nonnull BlockPos pos) {
        return (TileCraftingFrame)world.getTileEntity(pos);
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        if(!worldIn.isRemote && placer instanceof EntityPlayer) {
            ((EntityPlayer) placer).openGui(JustCraftingFrames.instance, GuiHandler.CRAFTING_FRAME, worldIn, pos.getX(), pos.getY(), pos.getZ());
            getTE(worldIn, pos).markDirty();
        }
    }

    @Override
    public boolean onBlockActivated(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull EntityPlayer playerIn, @Nonnull EnumHand hand, @Nonnull EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (!worldIn.isRemote) {
            TileCraftingFrame te = getTE(worldIn, pos);
            if (!te.hasValidRecipe(playerIn))
                playerIn.openGui(JustCraftingFrames.instance, GuiHandler.CRAFTING_FRAME, worldIn, pos.getX(), pos.getY(), pos.getZ());
            else {
                if (playerIn.isSneaking())
                    te.craft(playerIn, true);
                else
                    te.craft(playerIn, false);
            }
            te.markDirty();
        }
        return true;
    }
}
