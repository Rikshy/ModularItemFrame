package de.shyrik.justcraftingframes.common.tile;

import com.teamwizardry.librarianlib.features.autoregister.TileRegister;
import com.teamwizardry.librarianlib.features.base.block.tile.TileModTickable;
import com.teamwizardry.librarianlib.features.saving.Save;
import de.shyrik.justcraftingframes.api.ModuleFrameBase;
import de.shyrik.justcraftingframes.common.block.BlockModularFrame;
import de.shyrik.justcraftingframes.common.module.ModuleItem;
import net.minecraft.util.EnumFacing;

@TileRegister("modular_frame")
public class TileModularFrame extends TileModTickable {

    @Save
    public int rotation = 0;

    //@Save
    public ModuleFrameBase module;

    public boolean reloadModel;

    public TileModularFrame() {
        module = new ModuleItem(this);
        reloadModel = true;
    }

    public EnumFacing blockFacing() {
        return world.getBlockState(pos).getValue(BlockModularFrame.FACING);
    }

    @Override
    public void tick() {
        module.tick(world, pos);
    }
}
