package de.shyrik.modularitemframe.api.Inventory;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.stream.Stream;

public class OpenItemStackHandler extends ItemStackHandler implements Iterable<ItemStack> {

    public OpenItemStackHandler() {
        super();
    }
    public OpenItemStackHandler(int slots) {
        super(slots);
    }

    @NotNull
    @Override
    public Iterator<ItemStack> iterator() {
        return stacks.iterator();
    }

    public Stream<ItemStack> stream() {
        return stacks.stream();
    }
}
