package de.shyrik.modularitemframe.common.upgrade;

import de.shyrik.modularitemframe.ModularItemFrame;
import de.shyrik.modularitemframe.api.UpgradeBase;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import org.jetbrains.annotations.NotNull;

public class SpeedUpgrade extends UpgradeBase {
    public static final ResourceLocation ID = new ResourceLocation(ModularItemFrame.MOD_ID, "upgrade_speed");
    private static final TextComponent NAME = new TranslationTextComponent("modularitemframe.upgrade.speed");

    @NotNull
    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @NotNull
    @Override
    public TextComponent getName() {
        return NAME;
    }

    @Override
    public int getMaxCount() {
        return 5;
    }
}

