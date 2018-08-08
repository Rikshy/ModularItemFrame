package de.shyrik.justcraftingframes.client;

import de.shyrik.justcraftingframes.JustCraftingFrames;
import de.shyrik.justcraftingframes.common.CommonProxy;
import de.shyrik.justcraftingframes.init.ModBlocks;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ClientProxy extends CommonProxy {

    @Override
    public void preInit(FMLPreInitializationEvent event){
        super.preInit(event);

        ModBlocks.initModels();
    }

    @Override
    public void init(FMLInitializationEvent event) {
        super.init(event);

    }

    @Override
    public void postInit(FMLPostInitializationEvent event) {
        super.postInit(event);

    }

    @SubscribeEvent
    public void registerTex(TextureStitchEvent.Pre event) {
        event.getMap().registerSprite(new ResourceLocation(JustCraftingFrames.MOD_ID, "blocks/crafting_frame_bg"));
        event.getMap().registerSprite(new ResourceLocation(JustCraftingFrames.MOD_ID, "blocks/item_frame_bg"));
        event.getMap().registerSprite(new ResourceLocation(JustCraftingFrames.MOD_ID, "blocks/nullify_frame_bg"));
        event.getMap().registerSprite(new ResourceLocation(JustCraftingFrames.MOD_ID, "blocks/tank_frame_bg"));
        event.getMap().registerSprite(new ResourceLocation(JustCraftingFrames.MOD_ID, "blocks/item_frame_bg"));
    }
}
