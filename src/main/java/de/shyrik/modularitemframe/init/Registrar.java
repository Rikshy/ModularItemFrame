package de.shyrik.modularitemframe.init;

import de.shyrik.modularitemframe.ModularItemFrame;
import de.shyrik.modularitemframe.common.block.BlockModularFrame;
import de.shyrik.modularitemframe.common.item.ItemModule;
import de.shyrik.modularitemframe.common.item.ItemScrewdriver;
import de.shyrik.modularitemframe.common.item.ItemUpgrade;
import de.shyrik.modularitemframe.common.module.t1.*;
import de.shyrik.modularitemframe.common.module.t2.*;
import de.shyrik.modularitemframe.common.module.t3.*;
import de.shyrik.modularitemframe.common.tile.TileModularFrame;
import de.shyrik.modularitemframe.common.upgrade.*;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemBucket;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;

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
                asDefault(new ItemModule(ModuleItem.LOC)
                                .addVariant(ModuleIO.LOC)
                                .addVariant(ModuleCrafting.LOC)
                                .addVariant(ModuleNullify.LOC)
                                .addVariant(ModuleStorage.LOC)
                                .addVariant(ModuleTank.LOC),
                        new ResourceLocation(ModularItemFrame.MOD_ID, "module_t1")),
                //Tier 2
                asDefault(new ItemModule(ModuleCraftingPlus.LOC)
                                .addVariant(ModuleDispense.LOC)
                                .addVariant(ModuleTrashCan.LOC)
                                .addVariant(ModuleUse.LOC)
                                .addVariant(ModuleVacuum.LOC),
                        new ResourceLocation(ModularItemFrame.MOD_ID, "module_t2")),
                //Tier 3
                asDefault(new ItemModule(ModuleAutoCrafting.LOC)
                                .addVariant(ModuleFluidDispenser.LOC)
                                .addVariant(ModuleItemTeleporter.LOC)
                                .addVariant(ModuleTeleport.LOC)
                                .addVariant(ModuleXP.LOC),
                        new ResourceLocation(ModularItemFrame.MOD_ID, "module_t3")),

                //Upgrades
                asDefault(new ItemUpgrade(UpgradeBlastResist.LOC)
                        .addVariant(UpgradeSpeed.LOC)
                        .addVariant(UpgradeRange.LOC)
                        .addVariant(UpgradeCapacity.LOC),
                        new ResourceLocation(ModularItemFrame.MOD_ID, "upgrade")
                )

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
