package de.shyrik.modularitemframe.common.network.packet;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.IOException;

public class FrameTileUpdatePacket extends SPacketUpdateTileEntity {

    private NBTTagCompound customTag;

    public FrameTileUpdatePacket() {}

    public FrameTileUpdatePacket(BlockPos blockPosIn, int tileEntityTypeIn, NBTTagCompound compoundIn, NBTTagCompound customTag) {
        super(blockPosIn, tileEntityTypeIn, compoundIn);
        this.customTag = customTag;
    }

    @Override
    public void readPacketData(PacketBuffer buf) throws IOException {
        super.readPacketData(buf);
        customTag = buf.readCompoundTag();
    }

    @Override
    public void writePacketData(PacketBuffer buf) throws IOException {
        super.writePacketData(buf);
        buf.writeCompoundTag(customTag);
    }

    @SideOnly(Side.CLIENT)
    public NBTTagCompound getCustomTag() {
        return customTag;
    }
}
