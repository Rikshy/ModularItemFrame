package de.shyrik.modularitemframe.common.module.t2;

import de.shyrik.modularitemframe.ModularItemFrame;
import de.shyrik.modularitemframe.api.ModuleBase;
import de.shyrik.modularitemframe.api.utils.XpUtils;
import de.shyrik.modularitemframe.common.block.BlockModularFrame;
import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.ProbeMode;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

//thx openblocks and enderio
public class ModuleXP extends ModuleBase {

    public static final ResourceLocation LOC = new ResourceLocation(ModularItemFrame.MOD_ID,"module_t2_xp");
    public static final ResourceLocation BG_LOC = new ResourceLocation(ModularItemFrame.MOD_ID,"blocks/module_t2_xp");
    protected static final int MAX_XP = 21862;

    private static final String NBT_XP = "xp";
    private static final String NBT_LEVEL = "level";

    protected int experience;
    protected int levels;

    @Nonnull
    @Override
    @SideOnly(Side.CLIENT)
    public ResourceLocation frontTexture() {
        return BG_LOC;
    }

    @Nonnull
    @Override
    @SideOnly(Side.CLIENT)
    public ResourceLocation innerTexture() {
        return BlockModularFrame.INNER_HARD_LOC;
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

    protected int addExperience(int xpToAdd) {
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
        else playerIn.addExperience(experience);
    }

    @Override
    @SideOnly(Side.CLIENT)
    @Optional.Method(modid = "theoneprobe")
    public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, EntityPlayer player, World world, IBlockState blockState, IProbeHitData data) {
        super.addProbeInfo(mode, probeInfo, player, world, blockState, data);
        probeInfo.horizontal().text(I18n.format("modularitemframe.tooltip.xp_level", levels));
    }

    @Nonnull
    @Override
    @SideOnly(Side.CLIENT)
    @Optional.Method(modid = "waila")
    public List<String> getWailaBody(ItemStack itemStack, IWailaDataAccessor accessor, IWailaConfigHandler config) {
        List<String> tooltips = super.getWailaBody(itemStack, accessor, config);
        tooltips.add(I18n.format("modularitemframe.tooltip.xp_level", levels));
        return tooltips;
    }

    @Nonnull
    @Override
    public NBTTagCompound serializeNBT() {
        NBTTagCompound nbt = super.serializeNBT();
        nbt.setInteger(NBT_XP, experience);
        nbt.setInteger(NBT_LEVEL, levels);
        return nbt;
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt) {
        super.deserializeNBT(nbt);
        if (nbt.hasKey(NBT_XP)) experience = nbt.getInteger(NBT_XP);
        if (nbt.hasKey(NBT_LEVEL)) levels = nbt.getInteger(NBT_LEVEL);
    }
}
