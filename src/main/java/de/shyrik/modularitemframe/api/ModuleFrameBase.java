package de.shyrik.modularitemframe.api;

import de.shyrik.modularitemframe.common.container.ContainerCraftingFrame;
import de.shyrik.modularitemframe.common.tile.TileModularFrame;
import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.ProbeMode;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fml.common.Optional;

import javax.annotation.Nonnull;

public abstract class ModuleFrameBase implements INBTSerializable<NBTTagCompound> {

    protected TileModularFrame tile;

    public void setTile(TileModularFrame te) {
        tile = te;
    }

    @Nonnull
    public abstract ResourceLocation getModelLocation();

    public void specialRendering(double x, double y, double z, float partialTicks, int destroyStage, float alpha) {

    }

    public void onBlockClicked(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull EntityPlayer playerIn) {}

    public abstract void onBlockActivated(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull EntityPlayer playerIn, @Nonnull EnumHand hand, @Nonnull EnumFacing facing, float hitX, float hitY, float hitZ);

    public ContainerCraftingFrame createContainer(final EntityPlayer player) {
        return null;
    }

    public void tick(@Nonnull World world, @Nonnull BlockPos pos) {}

    @Optional.Method(modid = "theoneprobe")
    public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, EntityPlayer player, World world, IBlockState blockState, IProbeHitData data) {}
}
