package de.shyrik.modularitemframe.init;

import org.apache.commons.lang3.tuple.Pair;
import net.minecraftforge.common.ForgeConfigSpec;

public class ConfigValues {

    public static final Config CONFIG;
    public static final ForgeConfigSpec SPEC;
    static {
        final Pair<Config, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Config::new);
        SPEC = specPair.getRight();
        CONFIG = specPair.getLeft();
    }

    public static int MaxFrameUpgrades;
    public static boolean AllowFakePlayers;
    public static int TankFrameCapacity;
    public static int TankTransferRate;
    public static int BaseTeleportRange;
    public static int BaseVacuumRange;
    public static boolean DisableAutomaticItemTransfer;

    public static void load() {
        MaxFrameUpgrades = CONFIG.MaxFrameUpgrades.get();
        AllowFakePlayers = CONFIG.AllowFakePlayers.get();
        TankFrameCapacity = CONFIG.TankFrameCapacity.get();
        TankTransferRate = CONFIG.TankTransferRate.get();
        BaseTeleportRange = CONFIG.BaseTeleportRange.get();
        BaseVacuumRange = CONFIG.BaseVacuumRange.get();
        DisableAutomaticItemTransfer = CONFIG.DisableAutomaticItemTransfer.get();
    }

    public static class Config {

        public ForgeConfigSpec.IntValue MaxFrameUpgrades;
        public ForgeConfigSpec.BooleanValue AllowFakePlayers;
        public ForgeConfigSpec.IntValue TankFrameCapacity;
        public ForgeConfigSpec.IntValue TankTransferRate;
        public ForgeConfigSpec.IntValue BaseTeleportRange;
        public ForgeConfigSpec.IntValue BaseVacuumRange;
        public ForgeConfigSpec.BooleanValue DisableAutomaticItemTransfer;

        Config(ForgeConfigSpec.Builder builder) {
            builder.push("general");
            MaxFrameUpgrades = builder
                    .comment("Maximum number of upgrades a frame can hold")
                    //.translation("text.moduleitemframe.config.maxupgrades")
                    .defineInRange("maxupgrades",5, 0, 10);

            AllowFakePlayers = builder
                    .comment("Allow fake players to interact with frames")
                    //.translation("text.moduleitemframe.config.allowfakeplayers")
                    .define("allowfakeplayers", false);

            TankFrameCapacity = builder
                    .comment("Base Fluid Capacity of the tank frame (mB)")
                    //.translation("text.moduleitemframe.config.tankcapacity")
                    .defineInRange("tankcapacity",4000, 1000, 32000);

            TankTransferRate = builder
                    .comment("Transferrate of the tank (mB) [0=disabled]")
                    //.translation("text.moduleitemframe.config.tanktransfer")
                    .defineInRange("tanktransfer",100, 0, 1000);

            BaseTeleportRange = builder
                    .comment("Base teleport distance of the teleport module")
                    //.translation("text.moduleitemframe.config.telerange")
                    .defineInRange("telerange",64, 0, 1000);

            BaseVacuumRange = builder
                    .comment("Base range of the vacuum frame")
                    //.translation("text.moduleitemframe.config.vacuumrange")
                    .defineInRange("vacuumrange",3, 1, 16);

            DisableAutomaticItemTransfer = builder
                    .comment("Makes the Item Teleport Module to not vacuum items")
                    //.translation("text.moduleitemframe.config.vacuumrange")
                    .define("autoitemtrans", false);
            builder.pop();
        }
    }
}
