package de.shyrik.modularitemframe;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod.EventBusSubscriber
public class EventHandler {

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public static void registerTex(TextureStitchEvent.Pre event) {
        event.getMap().registerSprite(new ResourceLocation(ModularItemFrame.MOD_ID, "blocks/crafting_frame_bg"));
        event.getMap().registerSprite(new ResourceLocation(ModularItemFrame.MOD_ID, "blocks/item_frame_bg"));
        event.getMap().registerSprite(new ResourceLocation(ModularItemFrame.MOD_ID, "blocks/nullify_frame_bg"));
        event.getMap().registerSprite(new ResourceLocation(ModularItemFrame.MOD_ID, "blocks/tank_frame_bg"));
        event.getMap().registerSprite(new ResourceLocation(ModularItemFrame.MOD_ID, "blocks/item_frame_bg"));
    }
}
