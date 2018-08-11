package de.shyrik.modularitemframe.common;

import com.teamwizardry.librarianlib.features.base.ModCreativeTab;
import de.shyrik.modularitemframe.ModTab;
import de.shyrik.modularitemframe.ModularItemFrame;
import de.shyrik.modularitemframe.api.ModuleRegistry;
import de.shyrik.modularitemframe.client.gui.GuiHandler;
import de.shyrik.modularitemframe.common.block.BlockModularFrame;
import de.shyrik.modularitemframe.common.item.ItemModule;
import de.shyrik.modularitemframe.common.module.ModuleItem;
import de.shyrik.modularitemframe.common.module.ModuleTank;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;

import javax.annotation.Nonnull;

public class CommonProxy {
    public static BlockModularFrame FRAME_MODULAR;

    public static ItemModule MODULE_TANK;

    public void preInit(FMLPreInitializationEvent event){
        FRAME_MODULAR = new BlockModularFrame();
        MODULE_TANK = ModuleRegistry.registerCreate("module_tank", ModuleTank.class);
        ModuleRegistry.register("module_item", ModuleItem.class);

        ModTab.init();

        NetworkRegistry.INSTANCE.registerGuiHandler(ModularItemFrame.instance, new GuiHandler());
    }

    public void init(FMLInitializationEvent event) {

    }

    public void postInit(FMLPostInitializationEvent event) {

    }
}
