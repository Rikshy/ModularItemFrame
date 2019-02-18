package de.shyrik.modularitemframe;

import de.shyrik.modularitemframe.common.network.NetworkHandler;
import de.shyrik.modularitemframe.init.ConfigValues;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(ModularItemFrame.MOD_ID)
public class ModularItemFrame {

    public static final String MOD_ID = "modularitemframe";
    public static final ResourceLocation CHANNEL = new ResourceLocation(MOD_ID, "??");

    public ModularItemFrame() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::config);

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, ConfigValues.SPEC);
    }

    @SubscribeEvent
    public void setup(FMLCommonSetupEvent event) {
        //NetworkRegistry.INSTANCE.registerGuiHandler(this, new GuiHandler());
        NetworkHandler.registerPackets();
    }

    public void config(ModConfig.ModConfigEvent event)
    {
        if (event.getConfig().getSpec() == ConfigValues.SPEC)
            ConfigValues.load();
    }
}
