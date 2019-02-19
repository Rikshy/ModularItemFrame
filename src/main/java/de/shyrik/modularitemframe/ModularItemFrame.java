package de.shyrik.modularitemframe;

import de.shyrik.modularitemframe.common.network.NetworkHandler;
import de.shyrik.modularitemframe.init.ConfigValues;
import de.shyrik.modularitemframe.init.Items;
import de.shyrik.modularitemframe.init.Registrar;
import de.shyrik.modularitemframe.init.RegistrarClient;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(ModularItemFrame.MOD_ID)
public class ModularItemFrame {

    public static final String MOD_ID = "modularitemframe";
    public static final ResourceLocation CHANNEL = new ResourceLocation(MOD_ID, "networking");

    public static ItemGroup GROUP = new ItemGroup("itemGroup.modularitemframe") {
        @Override
        @OnlyIn(Dist.CLIENT)
        public ItemStack createIcon() {
            return new ItemStack(Items.MODULAR_FRAME);
        }
    };

    public ModularItemFrame() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::config);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setupClient);

        FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(Block.class, Registrar::registerBlocks);
        FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(Item.class, Registrar::registerItems);
        FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(TileEntityType.class, Registrar::registerTileEntities);


        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, ConfigValues.SPEC);
    }

    @SubscribeEvent
    public void setup(FMLCommonSetupEvent event) {
        //NetworkRegistry.INSTANCE.registerGuiHandler(this, new GuiHandler());
        NetworkHandler.registerPackets();
    }

    @SubscribeEvent
    public void setupClient(FMLClientSetupEvent event) {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(RegistrarClient::registerTex);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(RegistrarClient::registerModels);
    }

    public void config(ModConfig.ModConfigEvent event)
    {
        if (event.getConfig().getSpec() == ConfigValues.SPEC)
            ConfigValues.load();
    }
}
