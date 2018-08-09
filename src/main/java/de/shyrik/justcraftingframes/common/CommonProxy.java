package de.shyrik.justcraftingframes.common;

import de.shyrik.justcraftingframes.JustCraftingFrames;
import de.shyrik.justcraftingframes.client.gui.GuiHandler;
import de.shyrik.justcraftingframes.common.block.BlockModularFrame;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;

public class CommonProxy {
    public static BlockModularFrame FRAME_MODULAR;

    public void preInit(FMLPreInitializationEvent event){
        FRAME_MODULAR = new BlockModularFrame();

        NetworkRegistry.INSTANCE.registerGuiHandler(JustCraftingFrames.instance, new GuiHandler());
    }

    public void init(FMLInitializationEvent event) {

    }

    public void postInit(FMLPostInitializationEvent event) {

    }
}
