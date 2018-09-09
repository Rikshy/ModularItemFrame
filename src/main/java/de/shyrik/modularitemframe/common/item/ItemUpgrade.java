package de.shyrik.modularitemframe.common.item;

import de.shyrik.modularitemframe.ModularItemFrame;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;

public class ItemUpgrade extends Item {

    public ResourceLocation upgradeId;

    public ItemUpgrade(@Nonnull ResourceLocation location) {
        super();
        setRegistryName(location);
        setTranslationKey(location.toString());
        setCreativeTab(ModularItemFrame.TAB);
        upgradeId = location;
    }
}
