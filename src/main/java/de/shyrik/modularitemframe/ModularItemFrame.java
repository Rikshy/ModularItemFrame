package de.shyrik.modularitemframe;

import de.shyrik.modularitemframe.client.FrameRenderer;
import de.shyrik.modularitemframe.common.block.ModularFrameBlock;
import de.shyrik.modularitemframe.init.Blocks;
import de.shyrik.modularitemframe.init.Client;
import de.shyrik.modularitemframe.init.Config;
import de.shyrik.modularitemframe.init.Items;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;


@Mod(ModularItemFrame.MOD_ID)
public class ModularItemFrame {

    public static final String MOD_ID = "modularitemframe";
    public static Config config;

    public ModularItemFrame() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        config = Config.build(ModLoadingContext.get().getActiveContainer());

        DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> {
            // Client setup
            modBus.addListener(this::setupClient);
            modBus.addListener(Client::onStitch);
            modBus.addListener(FrameRenderer::onModelBake);
        });

        Blocks.BLOCKS.register(modBus);
        Items.ITEMS.register(modBus);
        Blocks.TILE_ENTITIES.register(modBus);

        modBus.addListener(ModularFrameBlock::onExplosion);
    }

    public static final ItemGroup TAB = new ItemGroup("modularitemframe") {
        @Override
        @OnlyIn(Dist.CLIENT)
        public ItemStack createIcon() {
            return new ItemStack(Items.SCREWDRIVER.get());
        }
    };

    @OnlyIn(Dist.CLIENT)
    private void setupClient(final FMLClientSetupEvent event) {
        ClientRegistry.bindTileEntityRenderer(Blocks.MODULAR_FRAME_TILE_TYPE.get(), FrameRenderer::new);
    }
}
