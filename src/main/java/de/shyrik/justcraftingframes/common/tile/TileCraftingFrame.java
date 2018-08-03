package de.shyrik.justcraftingframes.common.tile;

import com.teamwizardry.librarianlib.features.autoregister.TileRegister;
import net.minecraft.item.ItemStack;

@TileRegister("crafting_frame")
public class TileCraftingFrame extends TileFrameBase {

    public void setDisplayedItem(ItemStack stack) {
        displayedItem = stack;
    }
}
