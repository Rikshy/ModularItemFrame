package de.shyrik.modularitemframe.api;

import com.teamwizardry.librarianlib.features.config.ConfigIntRange;
import com.teamwizardry.librarianlib.features.config.ConfigProperty;

public final class ConfigValues {

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
	public static int MaxTeleportRange = 64;

	@ConfigIntRange(min = 0, max = 5)
	@ConfigProperty(category = "general", comment = "Additional range the dropper can have for inventory checking")
	public static int AddDropperRange = 1;

	@ConfigIntRange(min = 1, max = 1000)
	@ConfigProperty(category = "general", comment = "Delay between entity pick ups in tile ticks (20~=1sec)")
	public static int VacuumCooldown = 20;

	@ConfigIntRange(min = 1, max = 10)
	@ConfigProperty(category = "general", comment = "Maximum range of the vacuum frame")
	public static int MaxVacuumRange = 5;
}
