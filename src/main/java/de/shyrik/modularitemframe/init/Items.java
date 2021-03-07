package de.shyrik.modularitemframe.init;

import de.shyrik.modularitemframe.ModularItemFrame;
import de.shyrik.modularitemframe.api.ModuleBase;
import de.shyrik.modularitemframe.api.ModuleItem;
import de.shyrik.modularitemframe.api.UpgradeBase;
import de.shyrik.modularitemframe.api.UpgradeItem;
import de.shyrik.modularitemframe.common.item.FilterUpgradeItem;
import de.shyrik.modularitemframe.common.item.ScrewdriverItem;
import de.shyrik.modularitemframe.common.module.t1.IOModule;
import de.shyrik.modularitemframe.common.module.t1.ItemModule;
import de.shyrik.modularitemframe.common.module.t1.StorageModule;
import de.shyrik.modularitemframe.common.module.t1.TankModule;
import de.shyrik.modularitemframe.common.module.t2.*;
import de.shyrik.modularitemframe.common.module.t3.*;
import de.shyrik.modularitemframe.common.upgrade.*;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class Items {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, ModularItemFrame.MOD_ID);

    public static final RegistryObject<Item> SCREWDRIVER = create("screwdriver",
            new ScrewdriverItem(new Item.Properties().group(ModularItemFrame.TAB)));

    public static final RegistryObject<Item> CANVAS = create("canvas",
            new Item(new Item.Properties().group(ModularItemFrame.TAB)));

    public static final RegistryObject<Item> IO_MODULE = createMod(IOModule.ID, IOModule.class);
    public static final RegistryObject<Item> ITEM_MODULE = createMod(ItemModule.ID, ItemModule.class);
    public static final RegistryObject<Item> STORAGE_MODULE = createMod(StorageModule.ID, StorageModule.class);
    public static final RegistryObject<Item> TANK_MODULE = createMod(TankModule.ID, TankModule.class);

    public static final RegistryObject<Item> BLOCK_BREAK_MODULE = createMod(BlockBreakModule.ID, BlockBreakModule.class);
    public static final RegistryObject<Item> BLOCK_PLACE_MODULE = createMod(BlockPlaceModule.ID, BlockPlaceModule.class);
    public static final RegistryObject<Item> CRAFTING_MODULE = createMod(CraftingModule.ID, CraftingModule.class);
    public static final RegistryObject<Item> DISPENSE_MODULE = createMod(DispenseModule.ID, DispenseModule.class);
    public static final RegistryObject<Item> FAN_MODULE = createMod(FanModule.ID, FanModule.class);
    public static final RegistryObject<Item> SLAY_MODULE = createMod(SlayModule.ID, SlayModule.class);
    public static final RegistryObject<Item> TRASH_MODULE = createMod(TrashCanModule.ID, TrashCanModule.class);
    public static final RegistryObject<Item> VACUUM_MODULE = createMod(VacuumModule.ID, VacuumModule.class);

    public static final RegistryObject<Item> AUTO_CRAFT_MODULE = createMod(AutoCraftingModule.ID, AutoCraftingModule.class);
    public static final RegistryObject<Item> ITEM_TELEPORT_MODULE = createMod(ItemTeleportModule.ID, ItemTeleportModule.class);
    public static final RegistryObject<Item> JUKEBOX_MODULE = createMod(JukeboxModule.ID, JukeboxModule.class);
    public static final RegistryObject<Item> FLUID_DISPENSE_MODULE = createMod(FluidDispenserModule.ID, FluidDispenserModule.class);
    public static final RegistryObject<Item> TELEPORT_MODULE = createMod(TeleportModule.ID, TeleportModule.class);
    public static final RegistryObject<Item> XP_MODULE = createMod(XPModule.ID, XPModule.class);

    public static final RegistryObject<Item> BLAST_RESIST_UPGRADE = createUp(BlastResistUpgrade.ID, BlastResistUpgrade.class);
    public static final RegistryObject<Item> CAPACITY_UPGRADE = createUp(CapacityUpgrade.ID, CapacityUpgrade.class);
    public static final RegistryObject<Item> INFINITY_UPGRADE = createUp(InfinityUpgrade.ID, InfinityUpgrade.class);
    public static final RegistryObject<Item> RANGE_UPGRADE = createUp(RangeUpgrade.ID, RangeUpgrade.class);
    public static final RegistryObject<Item> SECURITY_UPGRADE = createUp(SecurityUpgrade.ID, SecurityUpgrade.class);
    public static final RegistryObject<Item> SPEED_UPGRADE = createUp(SpeedUpgrade.ID, SpeedUpgrade.class);
    public static final RegistryObject<Item> FILTER_UPGRADE = create(FilterUpgrade.ID.getPath(),
            new FilterUpgradeItem(new Item.Properties().group(ModularItemFrame.TAB), FilterUpgrade.class, FilterUpgrade.ID));

    private static RegistryObject<Item> create(String name, Item item){
        return ITEMS.register(name, () -> item);
    }

    private static RegistryObject<Item> createMod(ResourceLocation id, Class<? extends ModuleBase> moduleClass) {
        return ITEMS.register(
                id.getPath(),
                () -> new ModuleItem(new Item.Properties().group(ModularItemFrame.TAB), moduleClass, id)
        );
    }

    private static RegistryObject<Item> createUp(ResourceLocation id, Class<? extends UpgradeBase> upgradeClass){
        return ITEMS.register(
                id.getPath(),
                () -> new UpgradeItem(new Item.Properties().group(ModularItemFrame.TAB), upgradeClass, id)
        );
    }
}
