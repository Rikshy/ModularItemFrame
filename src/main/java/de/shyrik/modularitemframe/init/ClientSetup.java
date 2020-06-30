package de.shyrik.modularitemframe.init;

import de.shyrik.modularitemframe.ModularItemFrame;
import de.shyrik.modularitemframe.client.render.FrameRenderer;
import de.shyrik.modularitemframe.common.block.BlockModularFrame;
import de.shyrik.modularitemframe.common.module.t1.*;
import de.shyrik.modularitemframe.common.module.t2.*;
import de.shyrik.modularitemframe.common.module.t3.*;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = ModularItemFrame.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientSetup {

    @SubscribeEvent
    public static void onRegisterModels(ModelRegistryEvent event) {
        ClientRegistry.bindTileEntityRenderer(Registrar.MODULAR_FRAME_TILE.get(), FrameRenderer::new );
    }

    @SubscribeEvent
    public static void onTextureStitch(TextureStitchEvent.Pre event) {
        registerAllTex(event,
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
                ModuleUse.BG_LOC,
                ModuleVacuum.BG_LOC,

                ModuleAutoCrafting.BG_LOC,
                ModuleFluidDispenser.BG_LOC,
                ItemModuleTeleporter.BG_IN,
                ItemModuleTeleporter.BG_OUT,
                ItemModuleTeleporter.BG_NONE,
                ModuleXP.BG_LOC
        );
    }


    private static void registerAllTex(TextureStitchEvent.Pre reg, ResourceLocation... locations) {
        for (ResourceLocation tex : locations)
            reg.addSprite(tex);
        //    map.stitch(Minecraft.getInstance().getResourceManager(), tex, Minecraft.getInstance().getProfiler(), 1);
    }
}
