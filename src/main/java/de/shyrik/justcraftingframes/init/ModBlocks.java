package de.shyrik.justcraftingframes.init;

import de.shyrik.justcraftingframes.ConfigValues;
import de.shyrik.justcraftingframes.common.block.*;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ModBlocks {
    public static BlockCraftingFrame FRAME_CRAFTING;
    public static BlockNullifyFrame FRAME_NULLIFY;
    public static BlockItemFrame FRAME_ITEM;
    public static BlockTankFrame FRAME_TANK;
    public static BlockTeleportFrame FRAME_TELE;

    public static void init() {
        FRAME_CRAFTING = new BlockCraftingFrame();
        FRAME_NULLIFY = new BlockNullifyFrame();
        FRAME_ITEM = new BlockItemFrame();
        FRAME_TANK = new BlockTankFrame();
        FRAME_TELE = new BlockTeleportFrame();
    }

    @SideOnly(Side.CLIENT)
    public static void initModels() {
        FRAME_CRAFTING.initModel();
        if (ConfigValues.AnimateNulliFrame)
            FRAME_NULLIFY.initModel();
        FRAME_ITEM.initModel();
        FRAME_TANK.initModel();
    }
}
