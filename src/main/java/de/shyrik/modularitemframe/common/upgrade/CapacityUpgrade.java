package de.shyrik.modularitemframe.common.upgrade;


import de.shyrik.modularitemframe.ModularItemFrame;
import modularitemframe.api.UpgradeBase;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import org.jetbrains.annotations.NotNull;

public class CapacityUpgrade extends UpgradeBase {
    public static final ResourceLocation ID = new ResourceLocation(ModularItemFrame.MOD_ID, "upgrade_capacity");
    private static final TextComponent NAME = new TranslationTextComponent("modularitemframe.upgrade.capacity");

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

    @NotNull
    @Override
    public int getMaxCount() {
        return 5;
    }
}

