package de.shyrik.modularitemframe.common.module;

import de.shyrik.modularitemframe.ModularItemFrame;
import de.shyrik.modularitemframe.api.ModuleBase;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;

public class ModuleEmpty extends ModuleBase {
    private static final ResourceLocation FG_LOC = new ResourceLocation(ModularItemFrame.MOD_ID, "blocks/default_front");
    private static final ResourceLocation BG_LOC = new ResourceLocation(ModularItemFrame.MOD_ID, "blocks/default_back");

    @Nonnull
    @Override
    @SideOnly(Side.CLIENT)
    public ResourceLocation frontTexture() {
        return FG_LOC;
    }

    @Nonnull
    @Override
    @SideOnly(Side.CLIENT)
    public ResourceLocation backTexture() {
        return BG_LOC;
    }

    @Override
    public String getModuleName() {
        return I18n.format("modularitemframe.module.empty");
    }

    @Override
    public boolean onBlockActivated(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull EntityPlayer playerIn, @Nonnull EnumHand hand, @Nonnull EnumFacing facing, float hitX, float hitY, float hitZ) {
        return false;
    }
}
