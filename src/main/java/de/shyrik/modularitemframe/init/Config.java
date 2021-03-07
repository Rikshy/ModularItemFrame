package de.shyrik.modularitemframe.init;

import modularitemframe.api.accessors.IFrameConfig;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.config.ModConfig;

public class Config implements IFrameConfig {

    public final ForgeConfigSpec.ConfigValue<Integer> maxFrameUpgrades;
    public final ForgeConfigSpec.ConfigValue<Integer> tankFrameCapacity;
    public final ForgeConfigSpec.ConfigValue<Integer> tankTransferRate;
    public final ForgeConfigSpec.ConfigValue<Boolean> dropFluidOnTankRemove;
    public final ForgeConfigSpec.ConfigValue<Integer> teleportRange;
    public final ForgeConfigSpec.ConfigValue<Integer> scanZoneRadius;

    final ForgeConfigSpec spec;

    private Config() {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        builder.comment("General Config. This config is synced from server to client.").push("general");
        maxFrameUpgrades = builder
                .comment("Maximum number of upgrades a frame can hold [default=5]")
                .defineInRange("maxFrameUpgrades", 5, 0, 10);

        tankFrameCapacity = builder
                .comment("Base fluid capacity of the tank frame (buckets) [default=4]")
                .define("tankFrameCapacity", 4);

        tankTransferRate = builder
                .comment("Base transfer rate of the tank (mB) [0=disabled | 1000=1Bucket] [default=100]")
                .defineInRange("tankTransferRate", 100, 0, 1000);

        dropFluidOnTankRemove = builder
                .comment("Tank module will spill content when removing the module. [default=false]")
                .define("dropFluidOnTankRemove", false);

        teleportRange = builder
                .comment("Base teleport distance of the teleport module")
                .defineInRange("teleportRange", 64, 16, 256);

        scanZoneRadius = builder
                .comment("Base radius of the world scanning modules (e.g. vacuum)")
                .defineInRange("scanZoneRadius", 2, 1, 16);

        builder.pop();
        spec = builder.build();
    }

    public static IFrameConfig build(ModContainer container) {
        Config cfg = new Config();
        container.addConfig(new ModConfig(ModConfig.Type.COMMON, cfg.spec, container));
        return cfg;
    }

    @Override
    public int getMaxUpgrades() {
        return maxFrameUpgrades.get();
    }

    @Override
    public int getBaseTankCapacity() {
        return tankFrameCapacity.get();
    }

    @Override
    public int getBaseTankTransferRate() {
        return tankTransferRate.get();
    }

    @Override
    public boolean dropFluidOnTankRemoval() {
        return dropFluidOnTankRemove.get();
    }

    @Override
    public int getBaseTeleportRange() {
        return teleportRange.get();
    }

    @Override
    public int getBaseScanRadius() {
        return scanZoneRadius.get();
    }
}
