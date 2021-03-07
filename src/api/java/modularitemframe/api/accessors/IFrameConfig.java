package modularitemframe.api.accessors;

public interface IFrameConfig {

    int getMaxUpgrades();

    int getBaseTankCapacity();

    int getBaseTankTransferRate();

    boolean dropFluidOnTankRemoval();

    int getBaseTeleportRange();

    int getBaseScanRadius();
}
