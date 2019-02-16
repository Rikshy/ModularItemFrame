package de.shyrik.modularitemframe;

import de.shyrik.modularitemframe.api.ModuleRegistry;
import de.shyrik.modularitemframe.api.UpgradeRegistry;
import de.shyrik.modularitemframe.client.gui.GuiHandler;
import de.shyrik.modularitemframe.common.module.t1.*;
import de.shyrik.modularitemframe.common.module.t2.*;
import de.shyrik.modularitemframe.common.module.t3.*;
import de.shyrik.modularitemframe.common.network.NetworkHandler;
import de.shyrik.modularitemframe.common.upgrade.UpgradeBlastResist;
import de.shyrik.modularitemframe.common.upgrade.UpgradeCapacity;
import de.shyrik.modularitemframe.common.upgrade.UpgradeRange;
import de.shyrik.modularitemframe.common.upgrade.UpgradeSpeed;
import de.shyrik.modularitemframe.init.Items;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;

@Mod(
        modid = ModularItemFrame.MOD_ID,
        name = ModularItemFrame.MOD_NAME,
        version = ModularItemFrame.VERSION,
        dependencies = ModularItemFrame.DEPENDENCIES)
public class ModularItemFrame {

    public static final String MOD_ID = "modularitemframe";
    public static final String MOD_NAME = "Modular Item Frame";
    public static final String VERSION = "@GRADLE:VERSION@";
    public static final String DEPENDENCIES = "after:mcmultipart;";
    public static final String CHANNEL = MOD_ID;

    public static final CreativeTabs TAB = new CreativeTabs("modularitemframe") {
        @Override
        public ItemStack createIcon() {
            return new ItemStack(Items.SCREWDRIVER);
        }
    };

    @Mod.Instance
    public static ModularItemFrame instance;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        NetworkRegistry.INSTANCE.registerGuiHandler(this, new GuiHandler());
        NetworkHandler.registerPackets();

        ModuleRegistry.register(ModuleCrafting.LOC, ModuleCrafting.class);
        ModuleRegistry.register(ModuleIO.LOC, ModuleIO.class);
        ModuleRegistry.register(ModuleItem.LOC, ModuleItem.class);
        ModuleRegistry.register(ModuleNullify.LOC, ModuleNullify.class);
        ModuleRegistry.register(ModuleTank.LOC, ModuleTank.class);
        ModuleRegistry.register(ModuleStorage.LOC, ModuleStorage.class);

        ModuleRegistry.register(ModuleCraftingPlus.LOC, ModuleCraftingPlus.class);
        ModuleRegistry.register(ModuleDispense.LOC, ModuleDispense.class);
        ModuleRegistry.register(ModuleVacuum.LOC, ModuleVacuum.class);
        ModuleRegistry.register(ModuleTrashCan.LOC, ModuleTrashCan.class);
        ModuleRegistry.register(ModuleUse.LOC, ModuleUse.class);

        ModuleRegistry.register(ModuleAutoCrafting.LOC, ModuleAutoCrafting.class);
        ModuleRegistry.register(ModuleTeleport.LOC, ModuleTeleport.class);
        ModuleRegistry.register(ModuleItemTeleporter.LOC, ModuleItemTeleporter.class);
        ModuleRegistry.register(ModuleXP.LOC, ModuleXP.class);
        ModuleRegistry.register(ModuleFluidDispenser.LOC, ModuleFluidDispenser.class);

        //Upgrades
        UpgradeRegistry.register(UpgradeSpeed.LOC, UpgradeSpeed.class);
        UpgradeRegistry.register(UpgradeRange.LOC, UpgradeRange.class);
        UpgradeRegistry.register(UpgradeCapacity.LOC, UpgradeCapacity.class);
        UpgradeRegistry.register(UpgradeBlastResist.LOC, UpgradeBlastResist.class);
    }
}
