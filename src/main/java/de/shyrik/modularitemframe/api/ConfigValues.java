package de.shyrik.modularitemframe.api;

import de.shyrik.modularitemframe.ModularItemFrame;
import net.minecraftforge.common.config.Config;

@Config(modid = ModularItemFrame.MOD_ID)
public final class ConfigValues {

    @Config.RangeInt(min = 0, max = 10)
    @Config.Comment("Maximum number of upgrades a frame can hold")
    public static int MaxFrameUpgrades = 5;

    @Config.Comment("Allow fake players to interact with frames")
    public static boolean AllowFakePlayers = false;

    @Config.RangeInt(min = 1000, max = 32000)
    @Config.Comment("Base Fluid Capacity of the tank frame (mB)")
    public static int TankFrameCapacity = 4000;

    @Config.RangeInt(min = 0, max = 1000)
    @Config.Comment("Transferrate of the tank (mB) [0=disabled]")
    public static int TankTransferRate = 100;

    @Config.RangeInt(min = 0, max = 1000)
    @Config.Comment("Base teleport distance of the teleport module")
    public static int BaseTeleportRange = 64;

    @Config.RangeInt(min = 1, max = 10)
    @Config.Comment("Base range of the vacuum frame")
    public static int BaseVacuumRange = 3;

    @Config.Comment("Makes the Item Teleport Module to not vacuum items")
    public static boolean DisableAutomaticItemTransfer = false;
}
