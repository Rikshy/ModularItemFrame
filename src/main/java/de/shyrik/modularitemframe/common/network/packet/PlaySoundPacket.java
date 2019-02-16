package de.shyrik.modularitemframe.common.network.packet;

import de.shyrik.modularitemframe.common.network.NetworkHandler;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PlaySoundPacket implements IMessage, IMessageHandler<PlaySoundPacket, IMessage> {
    private String soundId;
    private BlockPos pos;
    private String soundCategory;
    private float volume;
    private float pitch;

    public PlaySoundPacket() {
    }

    public PlaySoundPacket(BlockPos pos, String soundId, String soundCategory, float volume, float pitch) {
        this.soundId = soundId;
        this.pos = pos;
        this.soundCategory = soundCategory;
        this.volume = volume;
        this.pitch = pitch;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        pos = BlockPos.fromLong(buf.readLong());
        soundId = ByteBufUtils.readUTF8String(buf);
        soundCategory = ByteBufUtils.readUTF8String(buf);
        volume = buf.readFloat();
        pitch = buf.readFloat();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeLong(pos.toLong());
        ByteBufUtils.writeUTF8String(buf, soundId);
        ByteBufUtils.writeUTF8String(buf, soundCategory);
        buf.writeFloat(volume);
        buf.writeFloat(pitch);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IMessage onMessage(PlaySoundPacket message, MessageContext ctx) {
        NetworkHandler.getThreadListener(ctx).addScheduledTask(() -> {
            SoundEvent sound = SoundEvent.REGISTRY.getObject(new ResourceLocation(message.soundId));
            SoundCategory category = SoundCategory.getByName(message.soundCategory);
            if (sound != null) {
                Minecraft.getMinecraft().getSoundHandler().playSound(new PositionedSoundRecord(sound, category, message.volume, message.pitch, message.pos));
            }
        });
        return null;
    }
}
