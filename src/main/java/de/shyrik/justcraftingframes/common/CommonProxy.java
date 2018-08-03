package de.shyrik.justcraftingframes.common;

import de.shyrik.justcraftingframes.JustCraftingFrames;
import de.shyrik.justcraftingframes.client.gui.GuiHandler;
import de.shyrik.justcraftingframes.init.Blocks;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;

public class CommonProxy {

    public void preInit(FMLPreInitializationEvent event){
        Blocks.init();

        NetworkRegistry.INSTANCE.registerGuiHandler(JustCraftingFrames.instance, new GuiHandler());
    }

    public void init(FMLInitializationEvent event) {

    }

    public void postInit(FMLPostInitializationEvent event) {

    }
}
