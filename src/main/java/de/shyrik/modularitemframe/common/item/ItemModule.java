package de.shyrik.modularitemframe.common.item;

import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;

public class ItemModule extends Item {

    public ResourceLocation moduleId;

    public ItemModule(@Nonnull ResourceLocation loc) {
        moduleId = loc;
    }
}
