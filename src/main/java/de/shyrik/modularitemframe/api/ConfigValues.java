package de.shyrik.modularitemframe.api;

import com.teamwizardry.librarianlib.features.config.ConfigIntRange;
import com.teamwizardry.librarianlib.features.config.ConfigProperty;

public final class ConfigValues {

	@ConfigIntRange(min = 0, max = 10)
	@ConfigProperty(category = "general", comment = "Maximum number of upgrades a frame can hold")
	public static int MaxFrameUpgrades = 5;

	@ConfigProperty(category = "general", comment = "Allow fake players to interact with frames")
	public static boolean AllowFakePlayers = false;

	@ConfigIntRange(min = 1000, max = 32000)
	@ConfigProperty(category = "general", comment = "Fluid Capacity of the tank frame (mB)")
	public static int TankFrameCapacity = 4000;

	@ConfigIntRange(min = 0, max = 1000)
	@ConfigProperty(category = "general", comment = "Transferrate of the tank (mB) [0=disabled]")
	public static int TankTransferRate = 100;

	@ConfigIntRange(min = 0, max = 1000)
	@ConfigProperty(category = "general", comment = "Maximum teleport distance of the teleport module")
	public static int BaseTeleportRange = 64;

	@ConfigIntRange(min = 1, max = 10)
	@ConfigProperty(category = "general", comment = "Base range of the vacuum frame")
	public static int BaseVacuumRange = 3;
}
