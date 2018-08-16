package de.shyrik.modularitemframe.common.module.t3;

import de.shyrik.modularitemframe.api.ConfigValues;
import de.shyrik.modularitemframe.common.module.t2.ModuleXP;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.List;

public class ModuleXPVacuum extends ModuleXP {


    @Override
    public void tick(@Nonnull World world, @Nonnull BlockPos pos) {
        if (experience >= MAX_XP) return;
        if (world.getTotalWorldTime() % ConfigValues.VacuumCooldown != 0) return;

        List<EntityXPOrb> entities = world.getEntitiesWithinAABB(EntityXPOrb.class, getVacuumBB(pos));
        for (EntityXPOrb entity : entities) {
            if (entity.isDead) continue;

            addExperience(entity.getXpValue());
        }
    }

    private AxisAlignedBB getVacuumBB(@Nonnull BlockPos pos) {
        int range = ConfigValues.MaxVacuumRange;
        switch (tile.blockFacing()) {
            case DOWN:
                return new AxisAlignedBB(pos.add(-range, 0, -range), pos.add(range, range, range));
            case UP:
                return new AxisAlignedBB(pos.add(-range, 0, -range), pos.add(range, -range, range));
            case NORTH:
                return new AxisAlignedBB(pos.add(-range, -range, 0), pos.add(range, range, range));
            case SOUTH:
                return new AxisAlignedBB(pos.add(-range, -range, 0), pos.add(range, range, -range));
            case WEST:
                return new AxisAlignedBB(pos.add(0, -range, -range), pos.add(-range, range, range));
            case EAST:
                return new AxisAlignedBB(pos.add(0, -range, -range), pos.add(range, range, range));
        }
        return new AxisAlignedBB(pos, pos.add(1, 1, 1));
    }
}
