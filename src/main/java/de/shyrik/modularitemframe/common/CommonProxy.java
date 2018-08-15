package de.shyrik.modularitemframe.common;

import com.teamwizardry.librarianlib.features.base.item.ItemMod;
import de.shyrik.modularitemframe.ModTab;
import de.shyrik.modularitemframe.ModularItemFrame;
import de.shyrik.modularitemframe.api.ModuleRegistry;
import de.shyrik.modularitemframe.client.gui.GuiHandler;
import de.shyrik.modularitemframe.common.block.BlockModularFrame;
import de.shyrik.modularitemframe.api.ItemModule;
import de.shyrik.modularitemframe.common.item.ItemScrewdriver;
import de.shyrik.modularitemframe.common.module.t1.*;
import de.shyrik.modularitemframe.common.module.t2.ModuleDrop;
import de.shyrik.modularitemframe.common.module.t2.ModuleVacuum;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;

public class CommonProxy {
    public static BlockModularFrame FRAME_MODULAR;

    public static ItemModule MODULE_TANK;
    public static ItemModule MODULE_ITEM;
    public static ItemModule MODULE_TELE;
    public static ItemModule MODULE_CRAFT;
    public static ItemModule MODULE_NULL;
    public static ItemModule MODULE_DROP;
    public static ItemModule MODULE_XP;
    public static ItemModule MODULE_VACUUM;
    public static ItemModule MODULE_IO;
    public static ItemModule MODULE_TRASHCAN;

    public static ItemScrewdriver SCREWDRIVER;

    public static ItemMod ITEM_CANVAS;

    public void preInit(FMLPreInitializationEvent event) {
        FRAME_MODULAR = new BlockModularFrame();
        MODULE_TANK = ModuleRegistry.registerCreate("module_tank", ModuleTank.class);
        MODULE_ITEM = ModuleRegistry.registerCreate("module_item", ModuleItem.class);
        MODULE_TELE = ModuleRegistry.registerCreate("module_tele", ModuleTeleport.class);
        MODULE_CRAFT = ModuleRegistry.registerCreate("module_craft", ModuleCrafting.class);
        MODULE_NULL = ModuleRegistry.registerCreate("module_nullify", ModuleNullify.class);
        MODULE_DROP = ModuleRegistry.registerCreate("module_drop", ModuleDrop.class);
        MODULE_XP = ModuleRegistry.registerCreate("module_xp", ModuleXP.class);
        MODULE_VACUUM = ModuleRegistry.registerCreate("module_vacuum", ModuleVacuum.class);
        //MODULE_IO = ModuleRegistry.registerCreate("module_io", ModuleIO.class);
        //MODULE_TRASHCAN = ModuleRegistry.registerCreate("module_trash_can", ModuleTrashCan.class);
        SCREWDRIVER = new ItemScrewdriver();

        ITEM_CANVAS = new ItemMod("canvas");

        ModTab.init();

        NetworkRegistry.INSTANCE.registerGuiHandler(ModularItemFrame.instance, new GuiHandler());
    }

    public void init(FMLInitializationEvent event) {

    }

    public void postInit(FMLPostInitializationEvent event) {

    }
}
