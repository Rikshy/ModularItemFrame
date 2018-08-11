package de.shyrik.modularitemframe.common.module;

import de.shyrik.modularitemframe.ModularItemFrame;
import de.shyrik.modularitemframe.api.ModuleFrameBase;
import de.shyrik.modularitemframe.common.tile.TileModularFrame;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class ModuleTeleport extends ModuleFrameBase {

    private static final String NBT_LINK = "linked_pos";

    public BlockPos linkedLoc = null;

    @Nonnull
    @Override
    public ResourceLocation getModelLocation() {
        return new ResourceLocation(ModularItemFrame.MOD_ID, "blocks/item_frame_bg");
    }

    @Override
    public void onBlockActivated(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull EntityPlayer playerIn, @Nonnull EnumHand hand, @Nonnull EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (linkedLoc == null) {
            playerIn.sendMessage(new TextComponentTranslation("modularitemframe.message.no_target"));
            return;
        }
        if (!(worldIn.getTileEntity(linkedLoc) instanceof TileModularFrame) || !(((TileModularFrame)worldIn.getTileEntity(linkedLoc)).module instanceof ModuleTeleport)) {
            playerIn.sendMessage(new TextComponentTranslation("modularitemframe.message.invalid_target"));
            return;
        }
        if (!isTargetLocationValid(worldIn)) {
            playerIn.sendMessage(new TextComponentTranslation("modularitemframe.message.location_blocked"));
            return;
        }
        BlockPos target = linkedLoc.offset(EnumFacing.DOWN);
        for (int i = 0; i < 64; i++)
            worldIn.spawnParticle(EnumParticleTypes.PORTAL, playerIn.posX, playerIn.posY + worldIn.rand.nextDouble() * 2.0D, playerIn.posZ, worldIn.rand.nextGaussian(), 0.0D, worldIn.rand.nextGaussian());
        Minecraft.getMinecraft().getSoundHandler().playSound(new PositionedSoundRecord(SoundEvents.BLOCK_PORTAL_TRAVEL, SoundCategory.AMBIENT, 0.4F, 1F, pos));
        playerIn.setPositionAndUpdate(target.getX() + 0.5F, target.getY() + 0.5F, target.getZ() + 0.5F);
        for (int i = 0; i < 64; i++)
            worldIn.spawnParticle(EnumParticleTypes.PORTAL, target.getX(), target.getY() + worldIn.rand.nextDouble() * 2.0D, target.getZ(), worldIn.rand.nextGaussian(), 0.0D, worldIn.rand.nextGaussian());
    }

    private boolean isTargetLocationValid(@Nonnull World worldIn) {
        return worldIn.isAirBlock(linkedLoc.offset(EnumFacing.DOWN));
    }

    @Override
    public NBTTagCompound serializeNBT() {
        NBTTagCompound compound = new NBTTagCompound();
        compound.setLong(NBT_LINK, linkedLoc.toLong());
        return compound;
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt) {
        if (nbt.hasKey(NBT_LINK)) linkedLoc = BlockPos.fromLong(nbt.getInteger(NBT_LINK));
    }
}
