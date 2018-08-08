package de.shyrik.justcraftingframes.common.tile;

import com.teamwizardry.librarianlib.features.autoregister.TileRegister;
import com.teamwizardry.librarianlib.features.base.block.tile.TileModTickable;
import com.teamwizardry.librarianlib.features.saving.Save;
import de.shyrik.justcraftingframes.api.ModuleFrameBase;
import de.shyrik.justcraftingframes.common.block.BlockFrameBase;
import de.shyrik.justcraftingframes.common.module.ModuleItem;
import net.minecraft.util.EnumFacing;

@TileRegister("modular_frame")
public class TileModularFrame extends TileModTickable {

    @Save
    public int rotation = 0;

    public ModuleFrameBase module = null;

    public TileModularFrame() {
        module = new ModuleItem(this);
    }

    public EnumFacing blockFacing() {
        return world.getBlockState(pos).getValue(BlockFrameBase.FACING);
    }

    @Override
    public void tick() {
        module.tick(world, pos);
    }
}
