package de.shyrik.justcraftingframes;

import com.teamwizardry.librarianlib.features.config.ConfigIntRange;
import com.teamwizardry.librarianlib.features.config.ConfigProperty;

public final class ConfigValues {

	@ConfigProperty(category = "general", comment = "When the crafting frame is put on a chest (or other inventory) it will also use player inventory to craft")
	public static boolean StillUsePlayerInv = true;

	@ConfigProperty(category = "general", comment = "Allow fake players to interact with frames")
	public static boolean AllowFakePlayers = false;

	@ConfigProperty(category = "general", comment = "Animated Nullify Frame")
	public static boolean AnimateNulliFrame = true;

	@ConfigProperty(category = "general", comment = "Can the Nullify frame suck items from attached inventory - making it a trash can (this also goes for Fluid and Energy Storage")
	public static boolean CanNulliFrameSuckFromInvent = false;

	@ConfigIntRange(min = 1000, max = 32000)
	@ConfigProperty(category = "general", comment = "Fluid Capacity of the tank frame (mB)")
	public static int TankFrameCapacity = 4000;
}
