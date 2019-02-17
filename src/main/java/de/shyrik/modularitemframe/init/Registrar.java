package de.shyrik.modularitemframe.init;

import de.shyrik.modularitemframe.ModularItemFrame;
import de.shyrik.modularitemframe.api.ItemModule;
import de.shyrik.modularitemframe.api.ItemUpgrade;
import de.shyrik.modularitemframe.api.ModuleBase;
import de.shyrik.modularitemframe.api.UpgradeBase;
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
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.registry.GameRegistry;

@Mod.EventBusSubscriber
public class Registrar {

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        event.getRegistry().registerAll(
                asDefault(new BlockModularFrame(), BlockModularFrame.LOC)
        );
    }

    @SubscribeEvent
    public void registerTileEntities(RegistryEvent.Register<TileEntityType<?>> event) {
        TileEntityType.register(BlockModularFrame.LOC.toString(), TileEntityType.Builder.create(TileModularFrame::new));
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        event.getRegistry().registerAll(asItem(Blocks.MODULAR_FRAME, BlockModularFrame.LOC),

                asDefault(new ItemScrewdriver(), ItemScrewdriver.LOC),

                //Canvas
                asDefault(new Item(new Item.Properties()), new ResourceLocation(ModularItemFrame.MOD_ID, "canvas")),

                //Tier 1
                asModule(ModuleIO.class, ModuleIO.LOC),
                asModule(ModuleItem.class, ModuleItem.LOC),
                asModule(ModuleCrafting.class, ModuleCrafting.LOC),
                asModule(ModuleNullify.class, ModuleNullify.LOC),
                asModule(ModuleStorage.class, ModuleStorage.LOC),
                asModule(ModuleTank.class, ModuleTank.LOC),

                //Tier 2
                asModule(ModuleCraftingPlus.class, ModuleCraftingPlus.LOC),
                asModule(ModuleDispense.class, ModuleDispense.LOC),
                asModule(ModuleTrashCan.class, ModuleTrashCan.LOC),
                asModule(ModuleUse.class, ModuleUse.LOC),
                asModule(ModuleVacuum.class, ModuleVacuum.LOC),

                //Tier 3
                asModule(ModuleAutoCrafting.class, ModuleAutoCrafting.LOC),
                asModule(ModuleFluidDispenser.class, ModuleFluidDispenser.LOC),
                asModule(ItemModuleTeleporter.class, ItemModuleTeleporter.LOC),
                asModule(ModuleTeleport.class, ModuleTeleport.LOC),
                asModule(ModuleXP.class, ModuleXP.LOC),

                //Upgrades
                asUpgrade(UpgradeBlastResist.class, UpgradeBlastResist.LOC),
                asUpgrade(UpgradeSpeed.class, UpgradeSpeed.LOC),
                asUpgrade(UpgradeRange.class, UpgradeRange.LOC),
                asUpgrade(UpgradeCapacity.class, UpgradeCapacity.LOC)

        );
    }

    private static Item asModule(Class<? extends ModuleBase> module, ResourceLocation id) {
        return asDefault(new ItemModule(module, id), id);
    }

    private static Item asUpgrade(Class<? extends UpgradeBase> upgrade, ResourceLocation id) {
        return asDefault(new ItemUpgrade(upgrade, id), id);
    }

    private static Block asDefault(Block block, ResourceLocation loc) {
        return block.setRegistryName(loc);
    }

    private static Item asDefault(Item item, ResourceLocation loc) {
        return item.setRegistryName(loc);
    }

    private static Item asItem(Block block, ResourceLocation loc) {
        return new ItemBlock(block, new Item.Properties()).setRegistryName(loc);
    }
}
