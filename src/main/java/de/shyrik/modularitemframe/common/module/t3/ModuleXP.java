package de.shyrik.modularitemframe.common.module.t3;

import de.shyrik.modularitemframe.ModularItemFrame;
import de.shyrik.modularitemframe.api.util.ExperienceHelper;
import de.shyrik.modularitemframe.init.ConfigValues;
import de.shyrik.modularitemframe.api.ModuleBase;
import de.shyrik.modularitemframe.common.block.BlockModularFrame;
import net.minecraft.block.BlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.FakePlayer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class ModuleXP extends ModuleBase {
    public static final ResourceLocation LOC = new ResourceLocation(ModularItemFrame.MOD_ID,"module_t3_xp");
    public static final ResourceLocation BG_LOC = new ResourceLocation(ModularItemFrame.MOD_ID,"block/module_t3_xp");
    private static final int MAX_XP = 21862;

    private static final String NBT_XP = "xp";
    private static final String NBT_LEVEL = "level";

    private int experience;
    private int levels;

    @Override
    public ResourceLocation getId() {
        return LOC;
    }

    @Nonnull
    @Override
    @OnlyIn(Dist.CLIENT)
    public ResourceLocation frontTexture() {
        return BG_LOC;
    }

    @Nonnull
    @Override
    @OnlyIn(Dist.CLIENT)
    public ResourceLocation innerTexture() {
        return BlockModularFrame.INNER_HARDEST_LOC;
    }

    @Override
    public String getModuleName() {
        return I18n.format("modularitemframe.module.xp");
    }

    @Override
    public void onBlockClicked(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull PlayerEntity playerIn) {
        if (!worldIn.isRemote) {
            if (playerIn.isSneaking()) drainContainerXpToReachPlayerLevel(playerIn, 0);
            else drainContainerXpToReachPlayerLevel(playerIn, playerIn.experienceLevel + 1);
            tile.markDirty();
        }
    }

    @Override
    public ActionResultType onBlockActivated(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nonnull PlayerEntity playerIn, @Nonnull Hand hand, @Nonnull Direction facing, BlockRayTraceResult hit) {
        if (playerIn instanceof FakePlayer) return ActionResultType.FAIL;

        if (!worldIn.isRemote) {
            if (playerIn.isSneaking()) drainPlayerXpToReachPlayerLevel(playerIn, 0);
            else drainPlayerXpToReachPlayerLevel(playerIn, playerIn.experienceLevel - 1);
            tile.markDirty();
        }
        return ActionResultType.SUCCESS;
    }

    private void drainPlayerXpToReachPlayerLevel(@Nonnull PlayerEntity player, int level) {
        int targetXP = ExperienceHelper.getExperienceForLevel(level);
        int drainXP = ExperienceHelper.getPlayerXP(player) - targetXP;
        if (drainXP <= 0) {
            return;
        }
        drainXP = addExperience(drainXP);
        if (drainXP > 0) {
            ExperienceHelper.addPlayerXP(player, -drainXP);
        }
    }

    private int addExperience(int xpToAdd) {
        int j = MAX_XP - experience;
        if (xpToAdd > j) {
            xpToAdd = j;
        }

        experience += xpToAdd;
        levels = ExperienceHelper.getLevelForExperience(experience);
        //experience = (experience - ExperienceHelper.getExperienceForLevel(levels)) / ExperienceHelper.getXpBarCapacity(levels);
        return xpToAdd;
    }

    private void drainContainerXpToReachPlayerLevel(@Nonnull PlayerEntity player, int level) {
        int requiredXP = level == 0 ? experience : ExperienceHelper.getExperienceForLevel(level) - ExperienceHelper.getPlayerXP(player);

        requiredXP = Math.min(experience, requiredXP);

        addExperience(-requiredXP);
        ExperienceHelper.addPlayerXP(player, requiredXP);
    }

    @Override
    public void onRemove(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull Direction facing, @Nullable PlayerEntity playerIn) {
        super.onRemove(worldIn, pos, facing, playerIn);
        if (playerIn == null || playerIn instanceof FakePlayer)
            worldIn.addEntity(new ExperienceOrbEntity(worldIn, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, experience));
        else playerIn.giveExperiencePoints(experience);
    }

    @Override
    public void tick(@Nonnull World world, @Nonnull BlockPos pos) {
        if (experience >= MAX_XP) return;
        if (world.getGameTime() % (60 - 10 * tile.getSpeedUpCount()) != 0) return;

        List<ExperienceOrbEntity> entities = world.getEntitiesWithinAABB(ExperienceOrbEntity.class, getVacuumBB(pos));
        for (ExperienceOrbEntity entity : entities) {
            if (!entity.isAlive()) continue;

            addExperience(entity.getXpValue());
        }
    }

    private AxisAlignedBB getVacuumBB(@Nonnull BlockPos pos) {
        int range = ConfigValues.BaseVacuumRange + tile.getRangeUpCount();
        switch (tile.blockFacing()) {
            case DOWN:
                return new AxisAlignedBB(pos.add(-range, 0, -range), pos.add(range, -range, range));
            case UP:
                return new AxisAlignedBB(pos.add(-range, 0, -range), pos.add(range, range, range));
            case NORTH:
                return new AxisAlignedBB(pos.add(-range, -range, 0), pos.add(range, range, -range));
            case SOUTH:
                return new AxisAlignedBB(pos.add(-range, -range, 0), pos.add(range, range, range));
            case WEST:
                return new AxisAlignedBB(pos.add(0, -range, -range), pos.add(range, range, range));
            case EAST:
                return new AxisAlignedBB(pos.add(0, -range, -range), pos.add(-range, range, range));
        }
        return new AxisAlignedBB(pos, pos.add(1, 1, 1));
    }

    @Nonnull
    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT nbt = super.serializeNBT();
        nbt.putInt(NBT_XP, experience);
        nbt.putInt(NBT_LEVEL, levels);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        super.deserializeNBT(nbt);
        if (nbt.contains(NBT_XP)) experience = nbt.getInt(NBT_XP);
        if (nbt.contains(NBT_LEVEL)) levels = nbt.getInt(NBT_LEVEL);
    }
}
