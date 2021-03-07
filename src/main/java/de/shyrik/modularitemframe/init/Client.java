package de.shyrik.modularitemframe.init;

import de.shyrik.modularitemframe.ModularItemFrame;
import de.shyrik.modularitemframe.common.block.ModularFrameBlock;
import de.shyrik.modularitemframe.common.module.t1.*;
import de.shyrik.modularitemframe.common.module.t2.*;
import de.shyrik.modularitemframe.common.module.t3.*;
import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Arrays;

@Mod.EventBusSubscriber(modid = ModularItemFrame.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Client {

    @SubscribeEvent
    public static void registerContainers(RegistryEvent.Register<ContainerType<?>> event) {

    }

    @SubscribeEvent
    public static void onStitch(TextureStitchEvent.Pre event) {
        Arrays.asList(
                ModularFrameBlock.INNER_HARDEST,
                ModularFrameBlock.INNER_HARD,

                IOModule.BG,
                ItemModule.BG,
                StorageModule.BG,
                TankModule.BG,

                BlockBreakModule.BG,
                BlockPlaceModule.BG,
                CraftingModule.BG,
                DispenseModule.BG,
                SlayModule.BG,
                VacuumModule.BG,
                TrashCanModule.BG1,
                TrashCanModule.BG2,
                TrashCanModule.BG3,
                FanModule.BG1,
                FanModule.BG2,
                FanModule.BG3,

                AutoCraftingModule.BG,
                FluidDispenserModule.BG,
                TeleportModule.BG,
                ItemTeleportModule.BG_IN,
                ItemTeleportModule.BG_OUT,
                ItemTeleportModule.BG_NONE,
                XPModule.BG,
                JukeboxModule.BG
        ).forEach(event::addSprite);
    }
}
