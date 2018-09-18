package de.shyrik.modularitemframe.common.item;

import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;

public class ItemUpgrade extends Item {

    public ResourceLocation upgradeId;

    public ItemUpgrade(@Nonnull ResourceLocation location) {
        super();
        upgradeId = location;
    }
}
