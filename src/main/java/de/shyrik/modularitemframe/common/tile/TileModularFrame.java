package de.shyrik.modularitemframe.common.tile;

import com.teamwizardry.librarianlib.features.autoregister.TileRegister;
import com.teamwizardry.librarianlib.features.base.block.tile.TileModTickable;
import com.teamwizardry.librarianlib.features.saving.Save;
import de.shyrik.modularitemframe.api.ModuleFrameBase;
import de.shyrik.modularitemframe.common.block.BlockModularFrame;
import de.shyrik.modularitemframe.common.module.ModuleItem;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;

@TileRegister("modular_frame")
public class TileModularFrame extends TileModTickable {

    @Save
    public int rotation = 0;

    @Save
    public ModuleFrameBase module;

    public boolean reloadModel;

    public TileModularFrame() {
        setModule(new ModuleItem());
    }

    public void setModule(ModuleFrameBase mod) {
        module = mod;
        module.setTile(this);
        reloadModel = true;
    }

    public EnumFacing blockFacing() {
        return world.getBlockState(pos).getValue(BlockModularFrame.FACING);
    }

    public TileEntity getNeighbor(EnumFacing facing) {
        return world.getTileEntity(pos.offset(facing));
    }

    @Override
    public void tick() {
        module.tick(world, pos);
    }
}
