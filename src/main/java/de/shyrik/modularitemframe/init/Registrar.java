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
                asDefault(ModuleRegistry.registerCreate(ModuleCrafting.LOC, ModuleCrafting.class), ModuleCrafting.LOC),
                asDefault(ModuleRegistry.registerCreate(ModuleIO.LOC, ModuleIO.class), ModuleIO.LOC),
                asDefault(ModuleRegistry.registerCreate(ModuleItem.LOC, ModuleItem.class), ModuleItem.LOC),
                asDefault(ModuleRegistry.registerCreate(ModuleNullify.LOC, ModuleNullify.class), ModuleNullify.LOC),
                asDefault(ModuleRegistry.registerCreate(ModuleTank.LOC, ModuleTank.class), ModuleTank.LOC),
                asDefault(ModuleRegistry.registerCreate(ModuleStorage.LOC, ModuleStorage.class), ModuleStorage.LOC),

                //Tier 2
                asDefault(ModuleRegistry.registerCreate(ModuleCraftingPlus.LOC, ModuleCraftingPlus.class), ModuleCraftingPlus.LOC),
                asDefault(ModuleRegistry.registerCreate(ModuleDispense.LOC, ModuleDispense.class), ModuleDispense.LOC),
                asDefault(ModuleRegistry.registerCreate(ModuleVacuum.LOC, ModuleVacuum.class), ModuleVacuum.LOC),
                asDefault(ModuleRegistry.registerCreate(ModuleTrashCan.LOC, ModuleTrashCan.class), ModuleTrashCan.LOC),
                asDefault(ModuleRegistry.registerCreate(ModuleUse.LOC, ModuleUse.class), ModuleUse.LOC),

                //Tier 3
                asDefault(ModuleRegistry.registerCreate(ModuleAutoCrafting.LOC, ModuleAutoCrafting.class), ModuleAutoCrafting.LOC),
                asDefault(ModuleRegistry.registerCreate(ModuleTeleport.LOC, ModuleTeleport.class), ModuleTeleport.LOC),
                asDefault(ModuleRegistry.registerCreate(ModuleItemTeleporter.LOC, ModuleItemTeleporter.class), ModuleItemTeleporter.LOC),
                asDefault(ModuleRegistry.registerCreate(ModuleXP.LOC, ModuleXP.class), ModuleXP.LOC),
                asDefault(ModuleRegistry.registerCreate(ModuleFluidDispenser.LOC, ModuleFluidDispenser.class), ModuleFluidDispenser.LOC),

                //Upgrades
                asDefault(UpgradeRegistry.registerCreate(UpgradeSpeed.LOC, UpgradeSpeed.class), UpgradeSpeed.LOC),
                asDefault(UpgradeRegistry.registerCreate(UpgradeRange.LOC, UpgradeRange.class), UpgradeRange.LOC),
                asDefault(UpgradeRegistry.registerCreate(UpgradeCapacity.LOC, UpgradeCapacity.class), UpgradeCapacity.LOC),
                asDefault(UpgradeRegistry.registerCreate(UpgradeBlastResist.LOC, UpgradeBlastResist.class), UpgradeBlastResist.LOC)
        );
    }

    private static Block asDefault(Block block, ResourceLocation loc) {
        return block.setRegistryName(loc).setCreativeTab(ModularItemFrame.TAB).setTranslationKey(loc.toString().replace(':', '.'));
    }

    private static Item asDefault(Item item, ResourceLocation loc) {
        return item.setRegistryName(loc).setCreativeTab(ModularItemFrame.TAB).setTranslationKey(loc.toString().replace(':', '.'));
    }

    private static Item asItem(Block block, ResourceLocation loc) {
        return new ItemBlock(block).setRegistryName(loc);
    }
}
