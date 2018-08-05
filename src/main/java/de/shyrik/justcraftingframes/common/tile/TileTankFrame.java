package de.shyrik.justcraftingframes.common.tile;

import com.teamwizardry.librarianlib.features.autoregister.TileRegister;
import de.shyrik.justcraftingframes.ConfigValues;

@TileRegister("tank_frame")
public class TileTankFrame extends TileFluidBaseFrame {

	public TileTankFrame() {
		super(ConfigValues.TankFrameCapacity);
	}
}
