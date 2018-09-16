package de.shyrik.modularitemframe.client.gui;

import de.shyrik.modularitemframe.common.compat.CompatHelper;
import de.shyrik.modularitemframe.common.container.ContainerCraftingFrame;
import de.shyrik.modularitemframe.common.module.t1.ModuleCrafting;
import de.shyrik.modularitemframe.common.tile.TileModularFrame;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class GuiHandler implements IGuiHandler {

    public static final int CRAFTING_FRAME = 1;

    public static int getMetaGuiId(int guiId, EnumFacing facing) {
        EnumFacing f2 = facing.getAxis().isHorizontal() ? facing.getOpposite() : facing;
        return (guiId << 4) + f2.getIndex();
    }

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        BlockPos pos = new BlockPos(x, y, z);
        final TileEntity tileEntity = CompatHelper.getTile(world, pos, EnumFacing.byIndex(ID & 7)).orElseThrow(() -> new RuntimeException("No valid tile entity at position " + pos));

        switch (ID >> 4) {
            case CRAFTING_FRAME:
                if (tileEntity instanceof TileModularFrame && ((TileModularFrame) tileEntity).module instanceof ModuleCrafting) {
                    return ((TileModularFrame) tileEntity).module.createContainer(player);
                }
            default:
                return null;
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        BlockPos pos = new BlockPos(x, y, z);
        final TileEntity tileEntity = CompatHelper.getTile(world, pos, EnumFacing.byIndex(ID & 7)).orElseThrow(() -> new RuntimeException("No valid tile entity at position " + pos));
        switch (ID >> 4) {
            case CRAFTING_FRAME:
                if (tileEntity instanceof TileModularFrame && ((TileModularFrame) tileEntity).module instanceof ModuleCrafting)
                    return new GuiCraftingFrame((ContainerCraftingFrame) ((TileModularFrame) tileEntity).module.createContainer(player));
                break;
        }

        return null;
    }
}
