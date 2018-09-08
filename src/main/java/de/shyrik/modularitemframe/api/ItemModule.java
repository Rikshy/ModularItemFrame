package de.shyrik.modularitemframe.api;

import de.shyrik.modularitemframe.ModularItemFrame;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;

public class ItemModule extends Item {

    public ResourceLocation moduleId;

    public ItemModule(@Nonnull ResourceLocation loc) {
        setTranslationKey(loc.toString());
        setRegistryName(loc);
        setCreativeTab(ModularItemFrame.TAB);
        moduleId = loc;
    }
}
