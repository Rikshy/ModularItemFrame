package de.shyrik.modularitemframe.common;

import de.shyrik.modularitemframe.ModTab;
import de.shyrik.modularitemframe.ModularItemFrame;
import de.shyrik.modularitemframe.api.ModuleRegistry;
import de.shyrik.modularitemframe.client.gui.GuiHandler;
import de.shyrik.modularitemframe.common.block.BlockModularFrame;
import de.shyrik.modularitemframe.common.item.ItemModule;
import de.shyrik.modularitemframe.common.item.ItemScrewdriver;
import de.shyrik.modularitemframe.common.module.*;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;

public class CommonProxy {
	public static BlockModularFrame FRAME_MODULAR;

	public static ItemModule MODULE_TANK;
	public static ItemModule MODULE_ITEM;
	public static ItemModule MODULE_TELE;
	public static ItemModule MODULE_CRAFT;
	public static ItemModule MODULE_NULL;
	public static ItemModule MODULE_DROP;

	public static ItemScrewdriver SCREWDRIVER;

	public void preInit(FMLPreInitializationEvent event) {
		FRAME_MODULAR = new BlockModularFrame();
		MODULE_TANK = ModuleRegistry.registerCreate("module_tank", ModuleTank.class);
		MODULE_ITEM = ModuleRegistry.registerCreate("module_item", ModuleItem.class);
		MODULE_TELE = ModuleRegistry.registerCreate("module_tele", ModuleTeleport.class);
		MODULE_CRAFT = ModuleRegistry.registerCreate("module_craft", ModuleCrafting.class);
		MODULE_NULL = ModuleRegistry.registerCreate("module_nullify", ModuleNullify.class);
		MODULE_DROP = ModuleRegistry.registerCreate("module_drop", ModuleDrop.class);
		SCREWDRIVER = new ItemScrewdriver();

		ModTab.init();

		NetworkRegistry.INSTANCE.registerGuiHandler(ModularItemFrame.instance, new GuiHandler());
	}

	public void init(FMLInitializationEvent event) {

	}

	public void postInit(FMLPostInitializationEvent event) {

	}
}
