package de.shyrik.justcraftingframes.client.Container;

import com.teamwizardry.librarianlib.features.base.block.tile.module.ModuleInventory;
import com.teamwizardry.librarianlib.features.container.ContainerBase;
import com.teamwizardry.librarianlib.features.container.InventoryWrapper;
import com.teamwizardry.librarianlib.features.saving.Module;
import net.minecraft.entity.player.EntityPlayer;
import org.jetbrains.annotations.NotNull;

public class FrameContainer extends ContainerBase {

    @Module
    ModuleInventory inventory = new ModuleInventory(9);

    public FrameContainer(@NotNull EntityPlayer player) {
        super(player);

        addSlots( new InventoryWrapper(inventory.getHandler()));
    }
}
