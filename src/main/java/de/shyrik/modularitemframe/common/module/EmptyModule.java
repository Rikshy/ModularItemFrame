package de.shyrik.modularitemframe.common.module;

import de.shyrik.modularitemframe.ModularItemFrame;
import de.shyrik.modularitemframe.api.ModuleBase;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

public class EmptyModule extends ModuleBase {
    public static final ResourceLocation ID = new ResourceLocation(ModularItemFrame.MOD_ID, "module_empty");
    public static final ResourceLocation FG = new ResourceLocation(ModularItemFrame.MOD_ID, "block/default_front");
    public static final ResourceLocation BG = new ResourceLocation(ModularItemFrame.MOD_ID, "block/default_back");
    private static final TextComponent NAME = new TranslationTextComponent("modularitemframe.module.empty");

    @NotNull
    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @NotNull
    @Override
    @OnlyIn(Dist.CLIENT)
    public ResourceLocation frontTexture() {
        return FG;
    }

    @NotNull
    @Override
    @OnlyIn(Dist.CLIENT)
    public ResourceLocation backTexture() {
        return BG;
    }

    @NotNull
    @Override
    public TextComponent getName() {
        return NAME;
    }
}
