package de.shyrik.modularitemframe.init;

import de.shyrik.modularitemframe.ModularItemFrame;
import de.shyrik.modularitemframe.api.ModuleRegistry;
import de.shyrik.modularitemframe.api.UpgradeRegistry;
import de.shyrik.modularitemframe.common.block.BlockModularFrame;
import de.shyrik.modularitemframe.common.item.ItemScrewdriver;
import de.shyrik.modularitemframe.common.module.t1.*;
import de.shyrik.modularitemframe.common.module.t2.*;
import de.shyrik.modularitemframe.common.module.t3.*;
import de.shyrik.modularitemframe.common.tile.TileModularFrame;
import de.shyrik.modularitemframe.common.upgrade.*;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

@Mod.EventBusSubscriber
public class Registrar {

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        event.getRegistry().registerAll(
                asDefault(new BlockModularFrame(), BlockModularFrame.LOC)
        );

        GameRegistry.registerTileEntity(TileModularFrame.class, BlockModularFrame.LOC);
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        event.getRegistry().registerAll(
                asItem(Blocks.MODULAR_FRAME, BlockModularFrame.LOC),

                asDefault(new ItemScrewdriver(), ItemScrewdriver.LOC),

                //Canvas
                asDefault(new Item(), new ResourceLocation(ModularItemFrame.MOD_ID, "canvas")),

                //Tier 1
                ModuleRegistry.registerCreate(ModuleCrafting.LOC, ModuleCrafting.class),
                ModuleRegistry.registerCreate(ModuleIO.LOC, ModuleIO.class),
                ModuleRegistry.registerCreate(ModuleItem.LOC, ModuleItem.class),
                ModuleRegistry.registerCreate(ModuleNullify.LOC, ModuleNullify.class),
                ModuleRegistry.registerCreate(ModuleTank.LOC, ModuleTank.class),
                ModuleRegistry.registerCreate(ModuleStorage.LOC, ModuleStorage.class),

                //Tier 2
                ModuleRegistry.registerCreate(ModuleCraftingPlus.LOC, ModuleCraftingPlus.class),
                ModuleRegistry.registerCreate(ModuleDispense.LOC, ModuleDispense.class),
                ModuleRegistry.registerCreate(ModuleVacuum.LOC, ModuleVacuum.class),
                ModuleRegistry.registerCreate(ModuleTrashCan.LOC, ModuleTrashCan.class),

                //Tier 3
                ModuleRegistry.registerCreate(ModuleAutoCrafting.LOC, ModuleAutoCrafting.class),
                ModuleRegistry.registerCreate(ModuleTeleport.LOC, ModuleTeleport.class),
                ModuleRegistry.registerCreate(ModuleItemTeleporter.LOC, ModuleItemTeleporter.class),
                ModuleRegistry.registerCreate(ModuleXP.LOC, ModuleXP.class),
                ModuleRegistry.registerCreate(ModuleFluidDispenser.LOC, ModuleFluidDispenser.class),

                //Upgrades
                UpgradeRegistry.registerCreate(UpgradeSpeed.LOC, UpgradeSpeed.class),
                UpgradeRegistry.registerCreate(UpgradeRange.LOC, UpgradeRange.class),
                UpgradeRegistry.registerCreate(UpgradeCapacity.LOC, UpgradeCapacity.class),
                UpgradeRegistry.registerCreate(UpgradeBlastResist.LOC, UpgradeBlastResist.class)
        );
    }

    private static Block asDefault(Block block, ResourceLocation loc) {
        return block.setTranslationKey(loc.toString()).setRegistryName(loc).setCreativeTab(ModularItemFrame.TAB);
    }

    private static Item asDefault(Item item, ResourceLocation loc) {
        return item.setTranslationKey(loc.toString()).setRegistryName(loc).setCreativeTab(ModularItemFrame.TAB);
    }

    private static Item asItem(Block block, ResourceLocation loc) {
        return new ItemBlock(block).setRegistryName(loc);
    }
}
