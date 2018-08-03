package de.shyrik.justcraftingframes.common.tile;

import com.teamwizardry.librarianlib.features.autoregister.TileRegister;
import de.shyrik.justcraftingframes.common.container.ContainerCraftingFrame;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.swing.plaf.basic.BasicComboBoxUI;

@TileRegister("crafting_frame")
public class TileCraftingFrame extends TileFrameBase implements ContainerCraftingFrame.IContainerCallbacks {

    public void setDisplayedItem(ItemStack stack) {
        displayedItem = stack;
    }


    private ItemStackHandler inventory = new ItemStackHandler(9) {

        @Override
        @Nonnull
        public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
            if (!stack.isEmpty()) {
                ItemStack copy = stack.copy();
                copy.setCount(1);

                validateSlotIndex(slot);

                this.stacks.set(slot, copy);
                onContentsChanged(slot);
                TileCraftingFrame.super.markDirty();
            }
            return stack;
        }

        @Override
        @Nonnull
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            ItemStack existing = this.stacks.get(slot);
            if (!existing.isEmpty()) {
                this.stacks.set(slot, ItemStack.EMPTY);
                onContentsChanged(slot);
                TileCraftingFrame.super.markDirty();
            }
            return ItemStack.EMPTY;
        }
    };

    //public ItemStackHandler inventory = new ItemStackHandler(9);

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
    public void readFromNBT(@Nonnull NBTTagCompound compound) {
        super.readFromNBT(compound);
        this.inventory.deserializeNBT((NBTTagCompound)compound.getTag("inv"));
    }

    @Override
    @Nonnull
    public NBTTagCompound writeToNBT(@Nonnull NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setTag("inv", this.inventory.serializeNBT() );

        return compound;
    }
}
