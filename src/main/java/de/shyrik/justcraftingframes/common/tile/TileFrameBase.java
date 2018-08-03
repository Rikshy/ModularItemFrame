package de.shyrik.justcraftingframes.common.tile;

import com.teamwizardry.librarianlib.features.base.block.tile.TileMod;
import com.teamwizardry.librarianlib.features.base.block.tile.TileModInventory;
import com.teamwizardry.librarianlib.features.base.block.tile.module.ModuleInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

public class TileFrameBase extends TileMod {

    private NonNullList<ItemStack> itemStacks = NonNullList.withSize(9, ItemStack.EMPTY);
    protected ItemStack displayedItem = ItemStack.EMPTY;

    public ItemStack getDisplayedItem() {
        return displayedItem;
    }

    @Override
    public void readFromNBT(@Nonnull NBTTagCompound compound) {
        super.readFromNBT(compound);
        ItemStackHelper.loadAllItems(compound, this.itemStacks);
    }

    @Nonnull
    @Override
    public NBTTagCompound writeToNBT(@Nonnull NBTTagCompound compound) {
        super.writeToNBT(compound);
        if (!displayedItem.isEmpty()) {
            NBTTagCompound displayCompound = new NBTTagCompound();
            displayedItem.writeToNBT(displayCompound);
        }
        ItemStackHelper.saveAllItems(compound, itemStacks);
        return compound;
    }
}
