package de.shyrik.justcraftingframes.common.tile;

import com.teamwizardry.librarianlib.features.autoregister.TileRegister;
import de.shyrik.justcraftingframes.ConfigValues;
import de.shyrik.justcraftingframes.common.Utils;
import de.shyrik.justcraftingframes.common.block.BlockFrameBase;
import de.shyrik.justcraftingframes.common.container.ContainerCraftingFrame;
import de.shyrik.justcraftingframes.common.container.FrameCrafting;
import de.shyrik.justcraftingframes.common.container.IContainerCallbacks;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryCraftResult;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;

import javax.annotation.Nonnull;

@TileRegister("crafting_frame")
public class TileCraftingFrame extends TileFrameBase implements IContainerCallbacks {

    private IRecipe recipe;
    public ItemStackHandler inventory = new ItemStackHandler(9);

    public ContainerCraftingFrame createContainer(final EntityPlayer player) {
        final IItemHandlerModifiable playerInventory = (IItemHandlerModifiable)player.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.UP);

        return new ContainerCraftingFrame(playerInventory, inventory, player, this);
    }

    public void craft(EntityPlayer player, boolean fullStack) {
        final IItemHandlerModifiable workingInv = getWorkingInventories(player);

        if (recipe == null)
            reloadRecipe(player);

        if (workingInv == null || recipe == null || recipe.getRecipeOutput().isEmpty() || !Utils.canCraft(workingInv, recipe.getIngredients()))
            return;

        int craftAmount = fullStack ? Math.min(Utils.countPossibleCrafts(workingInv, recipe.getIngredients()), 64) : 1;
        do {
            ItemStack remain = Utils.giveStack(workingInv, recipe.getRecipeOutput());
            if (!remain.isEmpty()) Utils.ejectStack(world, pos, remain);

            for (Ingredient ingredient : recipe.getIngredients()) {
                if (ingredient.getMatchingStacks().length > 0) {
                    Utils.removeFromInventory(workingInv, ingredient.getMatchingStacks());
                }
            }
        } while (--craftAmount > 0);
    }

    private IItemHandlerModifiable getWorkingInventories(EntityPlayer player) {
        EnumFacing facing = world.getBlockState(pos).getValue(BlockFrameBase.FACING);
        TileEntity neighbor = world.getTileEntity(pos.offset(facing));
        IItemHandlerModifiable neighborInventory = null;
        if (neighbor != null) {
            neighborInventory = (IItemHandlerModifiable) neighbor.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, facing.getOpposite());
        }
        IItemHandlerModifiable playerInventory = (IItemHandlerModifiable) player.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.UP);

        if(neighborInventory != null) {
            if (!ConfigValues.StillUsePlayerInv) return neighborInventory;
            else return new CombinedInvWrapper(neighborInventory, playerInventory);
        }
        return playerInventory;
    }

    public boolean hasValidRecipe() {
        return recipe != null && !recipe.getRecipeOutput().isEmpty();
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
    public void onContainerCraftingResultChanged(InventoryCraftResult result) {
        displayedItem = result.getStackInSlot(0);
        recipe = result.getRecipeUsed();
    }

    private void reloadRecipe(EntityPlayer player) {
        FrameCrafting fc = new FrameCrafting(new ContainerCraftingFrame(null, inventory, player, this), inventory, 3, 3);
        fc.onCraftMatrixChanged();
    }

    @Override
    public void readCustomNBT(@Nonnull NBTTagCompound compound) {
        this.inventory.deserializeNBT(compound.getCompoundTag("inv"));
        if (compound.hasKey("display")) displayedItem = new ItemStack(compound.getCompoundTag("display"));
    }

    @Override
    public void writeCustomNBT(@Nonnull NBTTagCompound compound, boolean sync) {
        compound.setTag("display", displayedItem.serializeNBT());
        compound.setTag("inv", this.inventory.serializeNBT());
    }
}
