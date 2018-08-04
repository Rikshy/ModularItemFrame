package de.shyrik.justcraftingframes.common.block;

import de.shyrik.justcraftingframes.JustCraftingFrames;
import de.shyrik.justcraftingframes.client.gui.GuiHandler;
import de.shyrik.justcraftingframes.common.tile.TileCraftingFrame;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;

public class BlockCraftingFrame extends BlockFrameBase {

    public BlockCraftingFrame(@NotNull String name) {
        super(name);
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(@NotNull World world, @NotNull IBlockState state) {
        return new TileCraftingFrame();
    }

    public TileCraftingFrame getTE(@Nonnull World world, @Nonnull BlockPos pos) {
        return (TileCraftingFrame)world.getTileEntity(pos);
    }

    @Override
    public boolean onBlockActivated(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull EntityPlayer playerIn, @Nonnull EnumHand hand, @Nonnull EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (!worldIn.isRemote) {
            TileCraftingFrame te = getTE(worldIn, pos);
            ItemStack heldItem = playerIn.getHeldItem(hand);
            if (!heldItem.isEmpty()) {
                te.setDisplayedItem(heldItem);

            } else {
                playerIn.openGui(JustCraftingFrames.instance, GuiHandler.CRAFTING_FRAME, worldIn, pos.getX(), pos.getY(), pos.getZ());
            }
            te.markDirty();
        }
        return true;
    }
}
