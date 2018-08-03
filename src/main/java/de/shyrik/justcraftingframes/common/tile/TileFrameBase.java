package de.shyrik.justcraftingframes.common.tile;

import com.teamwizardry.librarianlib.features.base.block.tile.TileMod;
import net.minecraft.item.ItemStack;

public class TileFrameBase extends TileMod {

    protected ItemStack displayedItem = ItemStack.EMPTY;

    public ItemStack getDisplayedItem() {
        return displayedItem;
    }
}
