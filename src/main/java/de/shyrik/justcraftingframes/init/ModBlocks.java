package de.shyrik.justcraftingframes.init;

import de.shyrik.justcraftingframes.ConfigValues;
import de.shyrik.justcraftingframes.common.block.*;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ModBlocks {
    public static BlockCraftingFrame FRAME_CRAFTING;
    public static BlockModularFrame FRAME_MODULAR;

    public static void init() {
        FRAME_CRAFTING = new BlockCraftingFrame();
        FRAME_MODULAR = new BlockModularFrame();
    }

    @SideOnly(Side.CLIENT)
    public static void initModels() {
        FRAME_CRAFTING.initModel();
        FRAME_MODULAR.initModel();
    }
}
