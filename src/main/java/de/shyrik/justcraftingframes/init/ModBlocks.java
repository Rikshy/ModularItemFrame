package de.shyrik.justcraftingframes.init;

import de.shyrik.justcraftingframes.common.block.BlockCraftingFrame;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ModBlocks {
    public static BlockCraftingFrame FRAME_CRAFTING;

    public static void init() {
        FRAME_CRAFTING = new BlockCraftingFrame("crafting_frame");
    }

    @SideOnly(Side.CLIENT)
    public static void initModels() {
        FRAME_CRAFTING.initModel();
    }
}
