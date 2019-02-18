package de.shyrik.modularitemframe.init;

import de.shyrik.modularitemframe.client.render.FrameRenderer;
import de.shyrik.modularitemframe.common.block.BlockModularFrame;
import de.shyrik.modularitemframe.common.block.TileModularFrame;
import de.shyrik.modularitemframe.common.module.t1.*;
import de.shyrik.modularitemframe.common.module.t2.ModuleCraftingPlus;
import de.shyrik.modularitemframe.common.module.t2.ModuleDispense;
import de.shyrik.modularitemframe.common.module.t2.ModuleTrashCan;
import de.shyrik.modularitemframe.common.module.t2.ModuleVacuum;
import de.shyrik.modularitemframe.common.module.t3.ItemModuleTeleporter;
import de.shyrik.modularitemframe.common.module.t3.ModuleAutoCrafting;
import de.shyrik.modularitemframe.common.module.t3.ModuleFluidDispenser;
import de.shyrik.modularitemframe.common.module.t3.ModuleXP;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(Dist.CLIENT)
public class RegistrarClient {

    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent event) {
        ClientRegistry.bindTileEntitySpecialRenderer(TileModularFrame.class, new FrameRenderer());
    }

    @SubscribeEvent
    public static void registerTex(TextureStitchEvent.Pre event) {
        registerAllTex(event.getMap(),
                BlockModularFrame.INNER_HARDEST_LOC,
                BlockModularFrame.INNER_HARD_LOC,

                ModuleCrafting.BG_LOC,
                ModuleIO.BG_LOC,
                ModuleItem.BG_LOC,
                ModuleNullify.BG_LOC,
                ModuleStorage.BG_LOC,
                ModuleTank.BG_LOC,

                ModuleCraftingPlus.BG_LOC,
                ModuleDispense.BG_LOC,
                ModuleTrashCan.BG_LOC1,
                ModuleTrashCan.BG_LOC2,
                ModuleTrashCan.BG_LOC3,
                ModuleVacuum.BG_LOC,

                ModuleAutoCrafting.BG_LOC,
                ModuleFluidDispenser.BG_LOC,
                ItemModuleTeleporter.BG_IN,
                ItemModuleTeleporter.BG_OUT,
                ItemModuleTeleporter.BG_NONE,
                ModuleXP.BG_LOC
        );
    }


    private static void registerAllTex(TextureMap map, ResourceLocation... locations) {
        for (ResourceLocation tex : locations)
            map.registerSprite(Minecraft.getInstance().getResourceManager(), tex);
    }
}
