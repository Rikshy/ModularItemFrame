package de.shyrik.modularitemframe.common.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.*;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nonnull;

public class ContainerCraftingFrame extends Container {

    private static final int FRAME_SLOTS_PER_ROW = 3;
    private static final int SLOTS_PER_ROW = 9;
    private static final int INV_ROWS = 3;
    /**
     * The object to send callbacks to.
     */
    private IContainerCallbacks callbacks;
    /**
     * The player inventory.
     */
    //private IItemHandlerModifiable playerInventory;
    private FrameCrafting matrix;
    private InventoryCraftResult craftResult = new InventoryCraftResult();
    private EntityPlayer player;

    public ContainerCraftingFrame(IItemHandlerModifiable playerInventory, @Nonnull IItemHandlerModifiable frameInventory, @Nonnull EntityPlayer player, @Nonnull IContainerCallbacks containerCallbacks) {
        //this.playerInventory = playerInventory;
        this.player = player;
        this.callbacks = containerCallbacks;

        matrix = new FrameCrafting(this, frameInventory, 3, 3);
        matrix.onCraftMatrixChanged();

        addSlot(new SlotCrafting(player, this.matrix, this.craftResult, 0, 124, 35) {
            @Override
            public boolean canTakeStack(EntityPlayer playerIn) {
                return false;
            }
        });
        for (int row = 0; row < FRAME_SLOTS_PER_ROW; ++row) {
            for (int col = 0; col < FRAME_SLOTS_PER_ROW; ++col) {
                addSlot(new GhostSlot(matrix, col + row * FRAME_SLOTS_PER_ROW, 30 + col * 18, 17 + row * 18));
            }
        }


        if (playerInventory != null) {
            for (int row = 0; row < INV_ROWS; ++row) {
                for (int col = 0; col < SLOTS_PER_ROW; ++col) {
                    addSlot(new SlotItemHandler(playerInventory, col + row * SLOTS_PER_ROW + SLOTS_PER_ROW, 8 + col * 18, 84 + row * 18));
                }
            }

            for (int col = 0; col < SLOTS_PER_ROW; ++col) {
                addSlot(new SlotItemHandler(playerInventory, col, 8 + col * 18, 142));
            }
        }
    }

    @Override
    @Nonnull
    public ItemStack transferStackInSlot(final EntityPlayer player, final int index) {
        final Slot slot = this.inventorySlots.get(index);

        if (slot != null && !slot.getStack().isEmpty()) {
            final ItemStack stack = slot.getStack();
            final ItemStack originalStack = stack.copy();

            if (index < INV_ROWS * SLOTS_PER_ROW) {
                if (!this.mergeItemStack(stack, INV_ROWS * SLOTS_PER_ROW, this.inventorySlots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.mergeItemStack(stack, 0, INV_ROWS * SLOTS_PER_ROW, false)) {
                return ItemStack.EMPTY;
            }

            if (stack.isEmpty()) {
                slot.putStack(ItemStack.EMPTY);
            } else {
                slot.onSlotChanged();
            }

            return originalStack;
        }

        return ItemStack.EMPTY;
    }

    @Override
    public boolean canInteractWith(@Nonnull EntityPlayer playerIn) {
        return callbacks.isUsableByPlayer(playerIn);
    }

    /**
     * Callback for when the matrix matrix is changed.
     */
    @Override
    public void onCraftMatrixChanged(IInventory inventoryIn) {
        this.slotChangedCraftingGrid(player.world, player, matrix, craftResult);
        callbacks.onContainerCraftingResultChanged(craftResult);
    }

    @Override
    @Nonnull
    public ItemStack slotClick(int slotId, int dragType, ClickType clickTypeIn, EntityPlayer player) {
        if (slotId >= 0 && getSlot(slotId) instanceof GhostSlot) {

            Slot stackSlot = getSlot(slotId);
            ItemStack stackHeld = player.inventory.getItemStack();

            if (stackHeld.isEmpty()) {
                stackSlot.putStack(ItemStack.EMPTY);
            } else {
                ItemStack s = stackHeld.copy();
                s.setCount(1);
                stackSlot.putStack(s);
            }
            stackSlot.onSlotChanged();
            detectAndSendChanges();

            return stackHeld;
        }
        return super.slotClick(slotId, dragType, clickTypeIn, player);
    }
}
