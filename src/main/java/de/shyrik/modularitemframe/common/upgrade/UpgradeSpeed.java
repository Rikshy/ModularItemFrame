package de.shyrik.modularitemframe.common.upgrade;

import de.shyrik.modularitemframe.ModularItemFrame;
import de.shyrik.modularitemframe.api.UpgradeBase;
import net.minecraft.util.ResourceLocation;

public class UpgradeSpeed extends UpgradeBase {
    public static final ResourceLocation LOC = new ResourceLocation(ModularItemFrame.MOD_ID, "upgrade_speed");

    @Override
    public int getMaxCount() {
        return 5;
    }
}

