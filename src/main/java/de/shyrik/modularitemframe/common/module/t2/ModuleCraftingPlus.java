package de.shyrik.modularitemframe.common.module.t2;

import de.shyrik.modularitemframe.api.ConfigValues;
import de.shyrik.modularitemframe.common.module.t1.ModuleCrafting;
import net.minecraft.client.resources.I18n;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;

public class ModuleCraftingPlus extends ModuleCrafting {

    @Override
    public String getModuleName() {
        return I18n.format("modularitemframe.module.crafting_plus");
    }

    @Override
    protected IItemHandlerModifiable getWorkingInventories(IItemHandlerModifiable playerInventory) {
        EnumFacing facing = tile.blockFacing();
        TileEntity neighbor = tile.getNeighbor(facing);
        IItemHandlerModifiable neighborInventory = null;
        if (neighbor != null) {
            neighborInventory = (IItemHandlerModifiable) neighbor.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, facing.getOpposite());
        }

        if (neighborInventory != null) {
            if (!ConfigValues.StillUsePlayerInv) return neighborInventory;
            else return new CombinedInvWrapper(neighborInventory, playerInventory);
        }
        return playerInventory;
    }
}
