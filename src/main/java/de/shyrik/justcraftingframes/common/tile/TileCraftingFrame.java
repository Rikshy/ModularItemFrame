package de.shyrik.justcraftingframes.common.tile;

import com.teamwizardry.librarianlib.features.autoregister.TileRegister;
import com.teamwizardry.librarianlib.features.base.block.tile.module.ModuleInventory;
import com.teamwizardry.librarianlib.features.saving.Module;
import de.shyrik.justcraftingframes.ConfigValues;
import de.shyrik.justcraftingframes.common.utils.Utils;
import de.shyrik.justcraftingframes.common.block.BlockFrameBase;
import de.shyrik.justcraftingframes.common.container.ContainerCraftingFrame;
import de.shyrik.justcraftingframes.common.container.FrameCrafting;
import de.shyrik.justcraftingframes.common.container.IContainerCallbacks;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.InventoryCraftResult;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;

import javax.annotation.Nonnull;

@TileRegister("crafting_frame")
public class TileCraftingFrame extends TileItemBaseFrame implements IContainerCallbacks {

    private IRecipe recipe;

    @Module
    public ModuleInventory inventory = new ModuleInventory(new ItemStackHandler(9));

    public TileCraftingFrame() {
        inventory.disallowSides(EnumFacing.VALUES);
        scale = 0.7F;
        offset = 0.0F;
    }

    public ContainerCraftingFrame createContainer(final EntityPlayer player) {
        final IItemHandlerModifiable playerInventory = (IItemHandlerModifiable)player.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.UP);

        return new ContainerCraftingFrame(playerInventory, inventory.getHandler(), player, this);
    }

    public void craft(EntityPlayer player, boolean fullStack) {
        if (player instanceof FakePlayer && !ConfigValues.AllowFakePlayers)
            return;

        final IItemHandlerModifiable playerInventory = (IItemHandlerModifiable) player.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.UP);
        final IItemHandlerModifiable workingInv = getWorkingInventories(playerInventory);

        if (recipe == null)
            reloadRecipe(player);

        if (workingInv == null || recipe == null || recipe.getRecipeOutput().isEmpty() || !Utils.canCraft(workingInv, recipe.getIngredients()))
            return;

        int craftAmount = fullStack ? Math.min(Utils.countPossibleCrafts(workingInv, recipe.getIngredients()), 64) : 1;
        do {
            ItemStack remain = Utils.giveStack(playerInventory, recipe.getRecipeOutput()); //use playerinventory here!
            if (!remain.isEmpty()) Utils.ejectStack(world, pos, remain);

            for (Ingredient ingredient : recipe.getIngredients()) {
                if (ingredient.getMatchingStacks().length > 0) {
                    Utils.removeFromInventory(workingInv, ingredient.getMatchingStacks());
                }
            }
        } while (--craftAmount > 0);
        world.playSound(null, pos, SoundEvents.BLOCK_LADDER_STEP, SoundCategory.BLOCKS, 0.4F, 0.7F);
    }

    private IItemHandlerModifiable getWorkingInventories(IItemHandlerModifiable playerInventory) {
        EnumFacing facing = world.getBlockState(pos).getValue(BlockFrameBase.FACING);
        TileEntity neighbor = world.getTileEntity(pos.offset(facing));
        IItemHandlerModifiable neighborInventory = null;
        if (neighbor != null) {
            neighborInventory = (IItemHandlerModifiable) neighbor.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, facing.getOpposite());
        }

        if(neighborInventory != null) {
            if (!ConfigValues.StillUsePlayerInv) return neighborInventory;
            else return new CombinedInvWrapper(neighborInventory, playerInventory);
        }
        return playerInventory;
    }

    public boolean hasValidRecipe(@Nonnull EntityPlayer player) {
        if ( recipe == null) reloadRecipe(player);
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
        return this.world.getTileEntity(pos) == this && player.getDistanceSq(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) <= 64;
    }

    @Override
    public void onContainerCraftingResultChanged(InventoryCraftResult result) {
        setDisplayItem(result.getStackInSlot(0));
        recipe = result.getRecipeUsed();
    }

    private void reloadRecipe(EntityPlayer player) {
        FrameCrafting fc = new FrameCrafting(new ContainerCraftingFrame(null, inventory.getHandler(), player, this), inventory.getHandler(), 3, 3);
        fc.onCraftMatrixChanged();
    }
}
