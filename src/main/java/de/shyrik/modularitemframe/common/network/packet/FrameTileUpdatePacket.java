package de.shyrik.modularitemframe.common.network.packet;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.io.IOException;

public class FrameTileUpdatePacket extends SUpdateTileEntityPacket {

    private CompoundNBT customTag;

    public FrameTileUpdatePacket() {}

    public FrameTileUpdatePacket(BlockPos blockPosIn, int tileEntityTypeIn, CompoundNBT compoundIn, CompoundNBT customTag) {
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

    @OnlyIn(Dist.CLIENT)
    public CompoundNBT getCustomTag() {
        return customTag;
    }
}
