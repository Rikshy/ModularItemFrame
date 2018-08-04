package de.shyrik.justcraftingframes;

import com.teamwizardry.librarianlib.features.config.ConfigProperty;

public final class ConfigValues {

	@ConfigProperty(category = "general", comment = "When the frame is put on a chest (or other inventory) it will also use player inventory to craft")
	public static boolean StillUsePlayerInv = true;
}
