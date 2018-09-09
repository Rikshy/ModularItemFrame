package de.shyrik.modularitemframe.common.compat;

import de.shyrik.modularitemframe.common.block.BlockModularFrame;
import de.shyrik.modularitemframe.common.tile.TileModularFrame;
import mcp.mobius.waila.api.*;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

import javax.annotation.Nonnull;
import java.util.List;

@WailaPlugin
public class Waila implements IWailaPlugin {
    @Override
    public void register(IWailaRegistrar registrar) {
        registrar.registerBodyProvider(new HUDProvider(), BlockModularFrame.class);
    }

    public class HUDProvider implements IWailaDataProvider {

        @Nonnull
        @Override
        public List<String> getWailaBody(ItemStack itemStack, List<String> tooltip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
            TileEntity tile = accessor.getTileEntity();
            if (tile instanceof TileModularFrame) {
                tooltip.addAll(((TileModularFrame) tile).module.getWailaBody(itemStack, accessor, config));
            }
            return tooltip;
        }
    }
}
