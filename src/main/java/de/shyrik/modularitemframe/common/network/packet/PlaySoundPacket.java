package de.shyrik.modularitemframe.common.network.packet;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.IRegistry;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class PlaySoundPacket {
    private ResourceLocation soundId;
    private BlockPos pos;
    private String soundCategory;
    private float volume;
    private float pitch;

    public PlaySoundPacket(BlockPos pos, ResourceLocation soundId, String soundCategory, float volume, float pitch) {
        this.soundId = soundId;
        this.pos = pos;
        this.soundCategory = soundCategory;
        this.volume = volume;
        this.pitch = pitch;
    }

    public static void encode(PlaySoundPacket msg, PacketBuffer buf) {
        buf.writeLong(msg.pos.toLong());
        buf.writeResourceLocation(msg.soundId);
        buf.writeString(msg.soundCategory);
        buf.writeFloat(msg.volume);
        buf.writeFloat(msg.pitch);
    }

    public static PlaySoundPacket decode(PacketBuffer buf) {
        return new PlaySoundPacket(
                BlockPos.fromLong(buf.readLong()),
                buf.readResourceLocation(),
                buf.readString(32767),
                buf.readFloat(),
                buf.readFloat()
        );
    }

    public static void handle(PlaySoundPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Minecraft mc = Minecraft.getInstance();
            SoundEvent sound = IRegistry.SOUND_EVENT.get(msg.soundId);
            if (sound != null)
                mc.getSoundHandler().play(new SimpleSound(sound, SoundCategory.valueOf(msg.soundCategory), msg.volume, msg.pitch, msg.pos));
        });
    }
}
