package de.shyrik.justcraftingframes;

import de.shyrik.justcraftingframes.common.CommonProxy;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(
        modid = JustCraftingFrames.MOD_ID,
        name = JustCraftingFrames.MOD_NAME,
        version = JustCraftingFrames.VERSION,
        dependencies = JustCraftingFrames.DEPENDENCIES
)
public class JustCraftingFrames {

    public static final String MOD_ID = "justcraftingframes";
    public static final String MOD_NAME = "Just Crafting Frames";
    public static final String VERSION = "@GRADLE:VERSION@";
    public static final String DEPENDENCIES = "required-before:librarianlib";

    public static final String CLIENT_PROXY = "de.shyrik.justcraftingframes.client.ClientProxy";
    public static final String SERVER_PROXY = "de.shyrik.justcraftingframes.common.CommonProxy";

    @SidedProxy(clientSide = JustCraftingFrames.CLIENT_PROXY, serverSide = JustCraftingFrames.SERVER_PROXY)
    public static CommonProxy proxy;
    @Mod.Instance
    public static JustCraftingFrames instance;

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
