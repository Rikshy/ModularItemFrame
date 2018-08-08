package de.shyrik.justcraftingframes.client.gui;

import de.shyrik.justcraftingframes.common.module.ModuleCrafting;
import de.shyrik.justcraftingframes.common.tile.TileModularFrame;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class GuiHandler implements IGuiHandler {

	public static final int CRAFTING_FRAME = 0;

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		final TileEntity tileEntity = world.getTileEntity(new BlockPos(x, y, z));

		switch (ID) {
			case CRAFTING_FRAME:
				if (tileEntity instanceof TileModularFrame && ((TileModularFrame)tileEntity).module instanceof ModuleCrafting) {
					return ((TileModularFrame) tileEntity).module.createContainer(player);
				}

			default:
				return null;
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		final TileEntity tileEntity = world.getTileEntity(new BlockPos(x, y, z));
		switch (ID) {
			case CRAFTING_FRAME:
				if (tileEntity instanceof TileModularFrame && ((TileModularFrame)tileEntity).module instanceof ModuleCrafting)
					return new GuiCraftingFrame(((TileModularFrame) tileEntity).module.createContainer(player));
				break;
		}

		return null;
	}
}
