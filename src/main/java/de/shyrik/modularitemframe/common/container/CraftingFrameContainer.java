package de.shyrik.modularitemframe.common.container;

import de.shyrik.modularitemframe.api.util.SlotHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.CraftResultInventory;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.*;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ICraftingRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.network.play.server.SSetSlotPacket;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nonnull;
import java.util.Optional;

public class CraftingFrameContainer extends Container {

    private static final int FRAME_SLOTS_PER_ROW = 3;
    private static final int SLOTS_PER_ROW = 9;
    private static final int INV_ROWS = 3;
    /**
     * The object to send callbacks to.
     */
    private IContainerCallbacks callbacks;

    private FrameCrafting matrix;
    private CraftResultInventory craftResult = new CraftResultInventory();
    private PlayerEntity player;

    public CraftingFrameContainer(int containerId, IItemHandlerModifiable playerInventory, @Nonnull IItemHandlerModifiable frameInventory, @Nonnull PlayerEntity player, @Nonnull IContainerCallbacks containerCallbacks) {
        super(ContainerType.CRAFTING, containerId);
        this.player = player;
        this.callbacks = containerCallbacks;

        matrix = new FrameCrafting(this, frameInventory, 3, 3);
        matrix.onCraftMatrixChanged();

        addSlot(new CraftingResultSlot(player, this.matrix, this.craftResult, 0, 124, 35) {
            @Override
            public boolean canTakeStack(PlayerEntity playerIn) {
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

    @Nonnull
    @Override
    public ItemStack transferStackInSlot(final PlayerEntity player, final int index) {
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

//    @Nonnull
//    @Override
//    public final ItemStack transferStackInSlot(PlayerEntity player, int slotIndex) {
//        return SlotHelper.transferStackInSlot(inventorySlots, player, slotIndex);
//    }

    @Override
    public boolean canInteractWith(@Nonnull PlayerEntity playerIn) {
        return callbacks.isUsableByPlayer(playerIn);
    }

    /**
     * Callback for when the matrix matrix is changed.
     */
    @Override
    public void onCraftMatrixChanged(IInventory inventoryIn) {
        slotChangedCraftingGrid(player.world, player, matrix, craftResult);
        callbacks.onContainerCraftingResultChanged(craftResult);
    }

//    @Nonnull
//    @Override
//    public ItemStack slotClick(int slotId, int dragType, ClickType clickTypeIn, PlayerEntity player) {
//        if (slotId >= 0 && getSlot(slotId) instanceof GhostSlot) {
//
//            Slot stackSlot = getSlot(slotId);
//            ItemStack stackHeld = player.inventory.getItemStack();
//
//            if (stackHeld.isEmpty()) {
//                stackSlot.putStack(ItemStack.EMPTY);
//            } else {
//                ItemStack s = stackHeld.copy();
//                s.setCount(1);
//                stackSlot.putStack(s);
//            }
//            stackSlot.onSlotChanged();
//            detectAndSendChanges();
//
//            return stackHeld;
//        }
//        return super.slotClick(slotId, dragType, clickTypeIn, player);
//    }

    @Nonnull
    @Override
    public ItemStack slotClick(int slotId, int dragType_or_button, ClickType clickTypeIn, PlayerEntity player) {
        Slot slot = slotId < 0 ? null : getSlot(slotId);
        if (slot instanceof GhostSlot) {
            ItemStack stack = SlotHelper.ghostSlotClick(slot, dragType_or_button, clickTypeIn, player);
            detectAndSendChanges();
            return stack;
        }
        return super.slotClick(slotId, dragType_or_button, clickTypeIn, player);
    }

    private void slotChangedCraftingGrid(World world, PlayerEntity player, CraftingInventory inv, CraftResultInventory craftResult) {
        if (!world.isRemote) {
            ServerPlayerEntity serverplayerentity = (ServerPlayerEntity)player;
            ItemStack stack = ItemStack.EMPTY;

            Optional<ICraftingRecipe> optional = world.getServer().getRecipeManager().getRecipe(IRecipeType.CRAFTING, inv, world);
            if (optional.isPresent()) {
                ICraftingRecipe icraftingrecipe = optional.get();
                if (craftResult.canUseRecipe(world, serverplayerentity, icraftingrecipe)) {
                    stack = icraftingrecipe.getCraftingResult(inv);
                }
            }

            craftResult.setInventorySlotContents(0, stack);
            serverplayerentity.connection.sendPacket(new SSetSlotPacket(this.windowId, 0, stack));
        }
    }
}
