package de.shyrik.modularitemframe;


import com.teamwizardry.librarianlib.features.base.ModCreativeTab;
import de.shyrik.modularitemframe.common.CommonProxy;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public class ModTab extends ModCreativeTab {

	private static boolean isInitialized = false;

	private ModTab() {
		super();
	}

	public static void init() {
		if (isInitialized) return;
		new ModTab().registerDefaultTab();
		isInitialized = true;
	}

	@Nonnull
	@Override
	public ItemStack getTabIconItem() {
		return new ItemStack(CommonProxy.MODULE_TANK);
	}

	@Nonnull
	@Override
	public ItemStack getIconStack() {
		return new ItemStack(CommonProxy.MODULE_TANK);
	}
}
