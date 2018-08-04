package de.shyrik.justcraftingframes.common.tile;

import com.teamwizardry.librarianlib.features.autoregister.TileRegister;
import de.shyrik.justcraftingframes.common.container.ContainerCraftingFrame;
import de.shyrik.justcraftingframes.common.container.IContainerCallbacks;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryCraftResult;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;

@TileRegister("crafting_frame")
public class TileCraftingFrame extends TileFrameBase implements IContainerCallbacks {

    public ItemStackHandler inventory = new ItemStackHandler(9);

    public ContainerCraftingFrame createContainer(final EntityPlayer player) {
        final IItemHandlerModifiable playerInventory = (IItemHandlerModifiable)player.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.UP);

        return new ContainerCraftingFrame(playerInventory, inventory, player, this);
    }

    @Override
    public void onContainerOpened(EntityPlayer player) {

    }

    @Override
    public void onContainerClosed(EntityPlayer player) {
    }

    @Override
    public boolean isUsableByPlayer(EntityPlayer player) {
        return this.world.getTileEntity(this.pos) == this && player.getDistanceSq(this.pos.getX() + 0.5, this.pos.getY() + 0.5, this.pos.getZ() + 0.5) <= 64;
    }

    @Override
    public void onContainerSlotChanged(InventoryCraftResult result) {
        displayedItem = result.getStackInSlot(0);
    }

    @Override
    public void readCustomNBT(@Nonnull NBTTagCompound compound) {
        this.inventory.deserializeNBT(compound.getCompoundTag("inv"));
        if (compound.hasKey("display")) displayedItem = new ItemStack(compound.getCompoundTag("display"));
    }

    @Override
    public void writeCustomNBT(@Nonnull NBTTagCompound compound, boolean sync) {
        compound.setTag("display", displayedItem.serializeNBT());
        compound.setTag("inv", this.inventory.serializeNBT() );
    }
}
