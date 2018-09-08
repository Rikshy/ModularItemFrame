package de.shyrik.modularitemframe.common.item;

import de.shyrik.modularitemframe.ModularItemFrame;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;

public class ItemUpgrade extends Item {

    public String upgradeId;

    public ItemUpgrade(@Nonnull String name) {
        super();
        setRegistryName(new ResourceLocation(ModularItemFrame.MOD_ID, name));
        setTranslationKey(name);
        setCreativeTab(ModularItemFrame.TAB);
        upgradeId = name;
    }
}
