package de.shyrik.modularitemframe;

import de.shyrik.modularitemframe.common.CommonProxy;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import javax.annotation.Nonnull;

@Mod(
        modid = ModularItemFrame.MOD_ID,
        name = ModularItemFrame.MOD_NAME,
        version = ModularItemFrame.VERSION,
        dependencies = ModularItemFrame.DEPENDENCIES
)
public class ModularItemFrame {

    public static final String MOD_ID = "modularitemframe";
    public static final String MOD_NAME = "Modular Item Frame";
    public static final String VERSION = "@GRADLE:VERSION@";
    public static final String DEPENDENCIES = "";

    public static final String CLIENT_PROXY = "de.shyrik.modularitemframe.client.ClientProxy";
    public static final String SERVER_PROXY = "de.shyrik.modularitemframe.common.CommonProxy";

    public static final CreativeTabs TAB = new CreativeTabs("modularitemframe") {
        @Override
        public ItemStack createIcon() {
            return new ItemStack(Registrar.SCREWDRIVER);
        }
    };

    @SidedProxy(clientSide = ModularItemFrame.CLIENT_PROXY, serverSide = ModularItemFrame.SERVER_PROXY)
    public static CommonProxy proxy;
    @Mod.Instance
    public static ModularItemFrame instance;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        proxy.preInit(event);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init(event);
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        proxy.postInit(event);
    }
}
