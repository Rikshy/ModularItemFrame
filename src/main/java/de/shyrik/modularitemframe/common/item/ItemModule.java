package de.shyrik.modularitemframe.common.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.*;

public class ItemModule extends Item {
    private Map<Integer, ResourceLocation> variants = new HashMap<>();

    public ItemModule(@Nonnull ResourceLocation loc) {
        hasSubtypes = true;
        addVariant(loc);
    }

    @Nonnull
    @Override
    public String getTranslationKey(ItemStack stack) {
        return "item." + variants.get(stack.getMetadata()).toString().replace(':', '.');
    }

    public ItemModule addVariant(int meta, @Nonnull ResourceLocation loc) {
        variants.put(meta, loc);
        return this;
    }

    public ItemModule addVariant(@Nonnull ResourceLocation loc) {
        return addVariant(variants.size(), loc);
    }

    public ResourceLocation getModuleId(@Nonnull ItemStack stack) {
        return variants.get(stack.getMetadata());
    }

    @Override
    public void getSubItems(@Nonnull CreativeTabs tab, @Nonnull NonNullList<ItemStack> items) {
        if (isInCreativeTab(tab)) {
            for (Integer meta : variants.keySet()) {
                items.add(new ItemStack(this, 1, meta));
            }
        }
    }
}
