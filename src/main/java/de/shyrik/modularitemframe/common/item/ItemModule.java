package de.shyrik.modularitemframe.common.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemModule extends Item {

    public ResourceLocation moduleId;
    private Map<Integer, ResourceLocation> variants = new HashMap<>(16);

    public ItemModule() {
    }

    public ItemModule addVariant(int meta, @Nonnull ResourceLocation loc) {
        hasSubtypes = true;
        variants.put(meta, loc);
        return this;
    }

    public static ResourceLocation getModuleId(@Nonnull ItemStack stack) {
        if (stack.getItem() instanceof ItemModule) {
            assert stack.getTagCompound() != null;
            new ResourceLocation(stack.getTagCompound().getString("moduleid"));
        }
        return null;
    }

    @Override
    public void getSubItems(@Nonnull CreativeTabs tab, @Nonnull NonNullList<ItemStack> items) {
        if (isInCreativeTab(tab)) {
            for (Map.Entry<Integer, ResourceLocation> id : variants.entrySet()) {
                ItemStack variant = new ItemStack(this, 1, id.getKey());
                NBTTagCompound nbt = new NBTTagCompound();
                nbt.setString("moduleid", id.getValue().toString());
                variant.setTagCompound(nbt);
                items.add(variant);
            }
        }
    }
}
