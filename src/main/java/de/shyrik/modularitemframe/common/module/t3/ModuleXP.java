package de.shyrik.modularitemframe.common.module.t3;

import de.shyrik.modularitemframe.ModularItemFrame;
import de.shyrik.modularitemframe.init.ConfigValues;
import de.shyrik.modularitemframe.api.ModuleBase;
import de.shyrik.modularitemframe.api.utils.XpUtils;
import de.shyrik.modularitemframe.common.block.BlockModularFrame;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.FakePlayer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class ModuleXP extends ModuleBase {
    public static final ResourceLocation LOC = new ResourceLocation(ModularItemFrame.MOD_ID,"module_t3_xp");
    public static final ResourceLocation BG_LOC = new ResourceLocation(ModularItemFrame.MOD_ID,"blocks/module_t3_xp");
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
    public void onBlockClicked(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull EntityPlayer playerIn) {
        if (!worldIn.isRemote) {
            if (playerIn.isSneaking()) drainContainerXpToReachPlayerLevel(playerIn, 0);
            else drainContainerXpToReachPlayerLevel(playerIn, playerIn.experienceLevel + 1);
            tile.markDirty();
        }
    }

    @Override
    public boolean onBlockActivated(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull EntityPlayer playerIn, @Nonnull EnumHand hand, @Nonnull EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (playerIn instanceof FakePlayer) return false;

        if (!worldIn.isRemote) {
            if (playerIn.isSneaking()) drainPlayerXpToReachPlayerLevel(playerIn, 0);
            else drainPlayerXpToReachPlayerLevel(playerIn, playerIn.experienceLevel - 1);
            tile.markDirty();
        }
        return true;
    }

    private void drainPlayerXpToReachPlayerLevel(@Nonnull EntityPlayer player, int level) {
        int targetXP = XpUtils.getExperienceForLevel(level);
        int drainXP = XpUtils.getPlayerXP(player) - targetXP;
        if (drainXP <= 0) {
            return;
        }
        drainXP = addExperience(drainXP);
        if (drainXP > 0) {
            XpUtils.addPlayerXP(player, -drainXP);
        }
    }

    private int addExperience(int xpToAdd) {
        int j = MAX_XP - experience;
        if (xpToAdd > j) {
            xpToAdd = j;
        }

        experience += xpToAdd;
        levels = XpUtils.getLevelForExperience(experience);
        //experience = (experience - XpUtils.getExperienceForLevel(levels)) / XpUtils.getXpBarCapacity(levels);
        return xpToAdd;
    }

    private void drainContainerXpToReachPlayerLevel(@Nonnull EntityPlayer player, int level) {
        int requiredXP = level == 0 ? experience : XpUtils.getExperienceForLevel(level) - XpUtils.getPlayerXP(player);

        requiredXP = Math.min(experience, requiredXP);

        addExperience(-requiredXP);
        XpUtils.addPlayerXP(player, requiredXP);
    }

    @Override
    public void onRemove(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull EnumFacing facing, @Nullable EntityPlayer playerIn) {
        super.onRemove(worldIn, pos, facing, playerIn);
        if (playerIn == null || playerIn instanceof FakePlayer)
            worldIn.spawnEntity(new EntityXPOrb(worldIn, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, experience));
        else playerIn.giveExperiencePoints(experience);
    }

    @Override
    public void tick(@Nonnull World world, @Nonnull BlockPos pos) {
        if (experience >= MAX_XP) return;
        if (world.getGameTime() % (60 - 10 * tile.getSpeedUpCount()) != 0) return;

        List<EntityXPOrb> entities = world.getEntitiesWithinAABB(EntityXPOrb.class, getVacuumBB(pos));
        for (EntityXPOrb entity : entities) {
            if (entity.removed) continue;

            addExperience(entity.getXpValue());
        }
    }

    private AxisAlignedBB getVacuumBB(@Nonnull BlockPos pos) {
        int range = ConfigValues.BaseVacuumRange + tile.getRangeUpCount();
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

    @Nonnull
    @Override
    public NBTTagCompound serializeNBT() {
        NBTTagCompound nbt = super.serializeNBT();
        nbt.putInt(NBT_XP, experience);
        nbt.putInt(NBT_LEVEL, levels);
        return nbt;
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt) {
        super.deserializeNBT(nbt);
        if (nbt.hasUniqueId(NBT_XP)) experience = nbt.getInt(NBT_XP);
        if (nbt.hasUniqueId(NBT_LEVEL)) levels = nbt.getInt(NBT_LEVEL);
    }
}
