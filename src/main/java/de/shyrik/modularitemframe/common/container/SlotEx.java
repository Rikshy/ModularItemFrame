package de.shyrik.modularitemframe.common.container;

import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class SlotEx extends SlotItemHandler {
    private final BaseContainer parent;

    public SlotEx(BaseContainer parent, IItemHandler itemHandler, int index, int xPosition, int yPosition) {
        super(itemHandler, index, xPosition, yPosition);
        this.parent = parent;
    }

    @Override
    public void onSlotChanged() {
        super.onSlotChanged();
        parent.onSlotChanged(this);
    }
}
