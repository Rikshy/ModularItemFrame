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
import de.shyrik.modularitemframe.common.module.t2.*;
import de.shyrik.modularitemframe.common.module.t3.*;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;

public class CommonProxy {
    public static BlockModularFrame FRAME_MODULAR;

    //Tier 1
    public static ItemModule MODULE_CRAFT;
    public static ItemModule MODULE_IO;
    public static ItemModule MODULE_ITEM;
    public static ItemModule MODULE_NULL;
    public static ItemModule MODULE_TANK;

    //
    public static ItemModule MODULE_TELE;
    public static ItemModule MODULE_DROP;
    public static ItemModule MODULE_XP;
    public static ItemModule MODULE_VACUUM;
    public static ItemModule MODULE_TRASHCAN;
    public static ItemModule MODULE_CRAFTINGPLUS;
    public static ItemModule MODULE_AUTOCRAFTING;

    public static ItemScrewdriver SCREWDRIVER;

    public static ItemMod ITEM_CANVAS;

    public void preInit(FMLPreInitializationEvent event) {
        FRAME_MODULAR = new BlockModularFrame();

        //Tier 1
        MODULE_CRAFT = ModuleRegistry.registerCreate("module_t1_craft", ModuleCrafting.class);
        MODULE_IO = ModuleRegistry.registerCreate("module_t1_io", ModuleIO.class);
        MODULE_ITEM = ModuleRegistry.registerCreate("module_t1_item", ModuleItem.class);
        MODULE_NULL = ModuleRegistry.registerCreate("module_t1_nullify", ModuleNullify.class);
        MODULE_TANK = ModuleRegistry.registerCreate("module_t1_tank", ModuleTank.class);
        //Tier 2
        MODULE_CRAFTINGPLUS = ModuleRegistry.registerCreate("module_t2_craft_plus", ModuleCraftingPlus.class);
        MODULE_DROP = ModuleRegistry.registerCreate("module_t2_dispense", ModuleDispense.class);
        MODULE_XP = ModuleRegistry.registerCreate("module_t2_xp", ModuleXP.class);
        MODULE_VACUUM = ModuleRegistry.registerCreate("module_t2_vacuum", ModuleVacuum.class);
        //MODULE_TRASHCAN = ModuleRegistry.registerCreate("module_trash_can", ModuleTrashCan.class);

        //Tier 3
        //MODULE_AUTOCRAFTING = ModuleRegistry.registerCreate("module_crafting_plus", ModuleAutoCrafting.class);
        MODULE_TELE = ModuleRegistry.registerCreate("module_t3_tele", ModuleTeleport.class);


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
