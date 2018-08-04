package de.shyrik.justcraftingframes.init;

import de.shyrik.justcraftingframes.common.block.BlockCraftingFrame;
import de.shyrik.justcraftingframes.common.block.BlockNullifyFrame;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ModBlocks {
    public static BlockCraftingFrame FRAME_CRAFTING;
    public static BlockNullifyFrame FRAME_NULLIFY;

    public static void init() {
        FRAME_CRAFTING = new BlockCraftingFrame();
        FRAME_NULLIFY = new BlockNullifyFrame();
    }

    @SideOnly(Side.CLIENT)
    public static void initModels() {
        FRAME_CRAFTING.initModel();
    }
}
