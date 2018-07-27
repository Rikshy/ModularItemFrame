package de.shyrik.justcraftingframes.common.network;

import com.teamwizardry.librarianlib.features.network.PacketBase;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import org.jetbrains.annotations.NotNull;

public class PacketCraftItem extends PacketBase {

    public  PacketCraftItem() {
        super();
    }


    @Override
    public void handle(@NotNull MessageContext ctx) {

    }
}
