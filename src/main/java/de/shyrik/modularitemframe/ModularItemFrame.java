package de.shyrik.modularitemframe;

import de.shyrik.modularitemframe.client.gui.GuiHandler;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;

@Mod(
        modid = ModularItemFrame.MOD_ID,
        name = ModularItemFrame.MOD_NAME,
        version = ModularItemFrame.VERSION,
        dependencies = ModularItemFrame.DEPENDENCIES)
public class ModularItemFrame {

    public static final String MOD_ID = "modularitemframe";
    public static final String MOD_NAME = "Modular Item Frame";
    public static final String VERSION = "@GRADLE:VERSION@";
    public static final String DEPENDENCIES = "after:mcmultipart;";

    public static final CreativeTabs TAB = new CreativeTabs("modularitemframe") {
        @Override
        public ItemStack createIcon() {
            return new ItemStack(Registrar.SCREWDRIVER);
        }
    };

    @Mod.Instance
    public static ModularItemFrame instance;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        NetworkRegistry.INSTANCE.registerGuiHandler(this, new GuiHandler());
    }
}
