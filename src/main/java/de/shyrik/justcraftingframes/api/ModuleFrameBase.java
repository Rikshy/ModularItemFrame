package de.shyrik.justcraftingframes.api;

import de.shyrik.justcraftingframes.common.tile.TileModularFrame;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public abstract class ModuleFrameBase {

    protected TileModularFrame tile;

    public ModuleFrameBase(TileModularFrame te) {
        this.tile = te;
    }

    @Nonnull
    public abstract ResourceLocation getBackgroundTexture();

    public void specialRendering(double x, double y, double z, float partialTicks, int destroyStage, float alpha) {

    }

    public void onBlockClicked(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull EntityPlayer playerIn) {}

    public abstract void onBlockActivated(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull EntityPlayer playerIn, @Nonnull EnumHand hand, @Nonnull EnumFacing facing, float hitX, float hitY, float hitZ);

    public void tick(@Nonnull World world, @Nonnull BlockPos pos) {}
}
