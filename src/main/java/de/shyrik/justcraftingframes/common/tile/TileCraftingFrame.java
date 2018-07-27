package de.shyrik.justcraftingframes.common.tile;

import com.teamwizardry.librarianlib.features.autoregister.TileRegister;
import net.minecraft.item.ItemStack;

@TileRegister("crafting_frame")
public class TileCraftingFrame extends TileFrameBase {

    private ItemStack displayedItem = ItemStack.EMPTY;

    @Override
    public ItemStack getDisplayedItem() {
        return displayedItem;
    }

    public void setDisplayedItem(ItemStack stack) {
        displayedItem = stack;
    }
}
