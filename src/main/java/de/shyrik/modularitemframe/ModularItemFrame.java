package de.shyrik.modularitemframe;

import de.shyrik.modularitemframe.common.network.NetworkHandler;
import de.shyrik.modularitemframe.init.ConfigValues;
import de.shyrik.modularitemframe.init.Registrar;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod("modularitemframe")
public class ModularItemFrame {

    public static final String MOD_ID = "modularitemframe";
    public static final ResourceLocation CHANNEL = new ResourceLocation(MOD_ID, "networking");

    public static ItemGroup GROUP = new ItemGroup("modularitemframe") {
        @Override
        @OnlyIn(Dist.CLIENT)
        public ItemStack createIcon() {
            return new ItemStack(Registrar.MODULAR_FRAME_ITEM.get());
        }
    };

    // Directly reference a log4j logger.
    private static final Logger LOGGER = LogManager.getLogger();

    public ModularItemFrame() {
        // Register the setup method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::config);

        Registrar.init();

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, ConfigValues.SPEC);
    }

    private void setup(final FMLCommonSetupEvent event) {
        NetworkHandler.registerPackets();
    }

    public void config(ModConfig.ModConfigEvent event) {
        if (event.getConfig().getSpec() == ConfigValues.SPEC)
            ConfigValues.load();
    }
}
