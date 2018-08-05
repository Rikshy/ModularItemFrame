package de.shyrik.justcraftingframes.common.tile;

import com.teamwizardry.librarianlib.features.base.block.tile.TileMod;
import com.teamwizardry.librarianlib.features.base.block.tile.module.SerializableFluidTank;
import com.teamwizardry.librarianlib.features.saving.Save;

public class TileFluidBaseFrame extends TileMod {

	@Save
	public SerializableFluidTank tank = new SerializableFluidTank(1000);

	public TileFluidBaseFrame( int capacity) {
		tank.setCapacity(capacity);
	}
}
