package de.shyrik.modularitemframe.init;

import de.shyrik.modularitemframe.ModularItemFrame;
import de.shyrik.modularitemframe.api.*;
import de.shyrik.modularitemframe.common.block.*;
import de.shyrik.modularitemframe.common.item.*;
import de.shyrik.modularitemframe.common.module.t1.*;
import de.shyrik.modularitemframe.common.module.t2.*;
import de.shyrik.modularitemframe.common.module.t3.*;
import de.shyrik.modularitemframe.common.upgrade.*;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class Registrar {

    private static final DeferredRegister<Block> BLOCKS = new DeferredRegister<>(ForgeRegistries.BLOCKS, ModularItemFrame.MOD_ID);
    private static final DeferredRegister<Item> ITEMS = new DeferredRegister<>(ForgeRegistries.ITEMS, ModularItemFrame.MOD_ID);
    private static final DeferredRegister<TileEntityType<?>> TILES = new DeferredRegister<>(ForgeRegistries.TILE_ENTITIES, ModularItemFrame.MOD_ID);

    public static void init() {
        BLOCKS.register(FMLJavaModLoadingContext.get().getModEventBus());
        ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
        TILES.register(FMLJavaModLoadingContext.get().getModEventBus());
        //CONTAINERS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

    public static final RegistryObject<Block> MODULAR_FRAME_BLOCK = BLOCKS.register(BlockModularFrame.LOC.getPath(), () -> new BlockModularFrame(BlockModularFrame.DEFAULT_PROPERTIES));
    public static final RegistryObject<TileEntityType<TileModularFrame>> MODULAR_FRAME_TILE = TILES.register(BlockModularFrame.LOC.getPath(), () -> TileEntityType.Builder.create(TileModularFrame::new, MODULAR_FRAME_BLOCK.get()).build(null));
    public static final RegistryObject<Item> MODULAR_FRAME_ITEM = ITEMS.register(BlockModularFrame.LOC.getPath(), () -> new BlockItem(MODULAR_FRAME_BLOCK.get(), new Item.Properties().group(ModularItemFrame.GROUP)));

    public static final RegistryObject<Item> SCREWDRIVER_ITEM = ITEMS.register(ItemScrewdriver.LOC.getPath(), () -> new ItemScrewdriver(new Item.Properties().group(ModularItemFrame.GROUP)));
    public static final RegistryObject<Item> CANVAS_ITEM = ITEMS.register("canvas", () -> new Item(new Item.Properties().group(ModularItemFrame.GROUP)));

    //Tier 1
    public static final RegistryObject<Item> MODULE_T1_IO_ITEM = registerModule(ModuleIO.class, ModuleIO.LOC);
    public static final RegistryObject<Item> MODULE_T1_ITEM_ITEM = registerModule(ModuleItem.class, ModuleItem.LOC);
    public static final RegistryObject<Item> MODULE_T1_CRAFT_ITEM = registerModule(ModuleCrafting.class, ModuleCrafting.LOC);
    public static final RegistryObject<Item> MODULE_T1_NULL_ITEM = registerModule(ModuleNullify.class, ModuleNullify.LOC);
    public static final RegistryObject<Item> MODULE_T1_STORAGE_ITEM = registerModule(ModuleStorage.class, ModuleStorage.LOC);
    public static final RegistryObject<Item> MODULE_T1_TANK_ITEM = registerModule(ModuleTank.class, ModuleTank.LOC);

    //Tier 2
    public static final RegistryObject<Item> MODULE_T2_CRAFT_PLUS_ITEM = registerModule(ModuleCraftingPlus.class, ModuleCraftingPlus.LOC);
    public static final RegistryObject<Item> MODULE_T2_DISPENSE_ITEM = registerModule(ModuleDispense.class, ModuleDispense.LOC);
    public static final RegistryObject<Item> MODULE_T2_TRASH_ITEM = registerModule(ModuleTrashCan.class, ModuleTrashCan.LOC);
    public static final RegistryObject<Item> MODULE_T2_USE_ITEM = registerModule(ModuleUse.class, ModuleUse.LOC);
    public static final RegistryObject<Item> MODULE_T2_VACUUM_ITEM = registerModule(ModuleVacuum.class, ModuleVacuum.LOC);

    //Tier 3
    public static final RegistryObject<Item> MODULE_T3_CRAFT_AUTO_ITEM = registerModule(ModuleAutoCrafting.class, ModuleAutoCrafting.LOC);
    public static final RegistryObject<Item> MODULE_T3_FLUID_DISPENSE_ITEM = registerModule(ModuleFluidDispenser.class, ModuleFluidDispenser.LOC);
    public static final RegistryObject<Item> MODULE_T3_ITEM_TELE_ITEM = registerModule(ItemModuleTeleporter.class, ItemModuleTeleporter.LOC);
    public static final RegistryObject<Item> MODULE_T3_TELE_ITEM = registerModule(ModuleTeleport.class, ModuleTeleport.LOC);
    public static final RegistryObject<Item> MODULE_T3_XP_ITEM = registerModule(ModuleXP.class, ModuleXP.LOC);

    //Upgrades
    public static final RegistryObject<Item> UPGRADE_RESIST_ITEM = registerUpgrade(UpgradeBlastResist.class, UpgradeBlastResist.LOC);
    public static final RegistryObject<Item> UPGRADE_SPEED_ITEM = registerUpgrade(UpgradeSpeed.class, UpgradeSpeed.LOC);
    public static final RegistryObject<Item> UPGRADE_RANGE_ITEM = registerUpgrade(UpgradeRange.class, UpgradeRange.LOC);
    public static final RegistryObject<Item> UPGRADE_CAPACITY_ITEM = registerUpgrade(UpgradeCapacity.class, UpgradeCapacity.LOC);


    private static RegistryObject<Item> registerModule(Class<? extends ModuleBase> module, ResourceLocation id) {
        return ITEMS.register(id.getPath(),() -> new ItemModule(new Item.Properties().group(ModularItemFrame.GROUP), module, id));
    }
    private static RegistryObject<Item> registerUpgrade(Class<? extends UpgradeBase> upgrade, ResourceLocation id) {
        return ITEMS.register(id.getPath(),() -> new ItemUpgrade(new Item.Properties().group(ModularItemFrame.GROUP), upgrade, id));
    }
}
