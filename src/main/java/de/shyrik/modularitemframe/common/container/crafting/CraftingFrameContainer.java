package de.shyrik.modularitemframe.common.container.crafting;

import de.shyrik.modularitemframe.common.container.FrameCrafting;
import de.shyrik.modularitemframe.common.container.GhostInventoryContainer;
import de.shyrik.modularitemframe.common.container.GhostSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.*;
import net.minecraft.inventory.container.*;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ICraftingRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.network.play.server.SSetSlotPacket;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.InvWrapper;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class CraftingFrameContainer extends GhostInventoryContainer {

    private static final int FRAME_SLOTS_PER_ROW = 3;

    private final IContainerCallbacks callbacks;
    private final FrameCrafting matrix;
    private final CraftResultInventory craftResult = new CraftResultInventory();

    public CraftingFrameContainer(int containerId, @NotNull IItemHandlerModifiable frameInventory, @NotNull PlayerEntity player, @NotNull IContainerCallbacks containerCallbacks) {
        super(ContainerType.CRAFTING, containerId, player);
        this.callbacks = containerCallbacks;

        matrix = new FrameCrafting(this, frameInventory, 3, 3);
        matrix.onCraftMatrixChanged();

        addSlot(new CraftingResultSlot(player, matrix, craftResult, 0, 124, 35) {
            @Override
            public boolean canTakeStack(@NotNull PlayerEntity playerIn) {
                return false;
            }
        });
        for (int row = 0; row < FRAME_SLOTS_PER_ROW; ++row) {
            for (int col = 0; col < FRAME_SLOTS_PER_ROW; ++col) {
                addSlot(new GhostSlot(this, new InvWrapper(matrix), col + row * FRAME_SLOTS_PER_ROW, 30 + col * 18, 17 + row * 18));
            }
        }
        addPlayerInventory(player);
    }

    @Override
    public void onCraftMatrixChanged(@NotNull IInventory inventory) {
        if (!player.world.isRemote) {
            World world = player.world;
            ServerPlayerEntity serverplayerentity = (ServerPlayerEntity)player;
            ItemStack itemstack = ItemStack.EMPTY;
            ICraftingRecipe recipe = null;
            Optional<ICraftingRecipe> optional = player.world.getServer().getRecipeManager().getRecipe(IRecipeType.CRAFTING, matrix, player.world);
            if (optional.isPresent()) {
                recipe = optional.get();
                if (craftResult.canUseRecipe(world, serverplayerentity, recipe)) {
                    itemstack = recipe.getCraftingResult(matrix);
                }
            }

            craftResult.setInventorySlotContents(0, itemstack);
            serverplayerentity.connection.sendPacket(new SSetSlotPacket(windowId, 0, itemstack));
            callbacks.setRecipe(recipe);
        }
    }

    @Override
    public boolean canInteractWith(@NotNull PlayerEntity playerIn) {
        return true;
    }
}
