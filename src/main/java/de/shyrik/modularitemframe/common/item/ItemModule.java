package de.shyrik.modularitemframe.common.item;

import com.teamwizardry.librarianlib.features.base.item.ItemMod;
import org.jetbrains.annotations.NotNull;

public class ItemModule extends ItemMod {

	public String moduleId;

	public ItemModule(@NotNull String name) {
		super(name);
		moduleId = name;
	}
}
