package de.shyrik.modularitemframe.common.network.packet;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class TeleportEffectPacket {
    private BlockPos pos;

    public TeleportEffectPacket(BlockPos pos) {
        this.pos = pos;
    }

    public static void encode(TeleportEffectPacket msg, PacketBuffer buf) {
        buf.writeLong(msg.pos.toLong());
    }

    public static TeleportEffectPacket decode(PacketBuffer buf) {
        return new TeleportEffectPacket(
                BlockPos.fromLong(buf.readLong())
        );
    }

    public static void handle(TeleportEffectPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Minecraft mc = Minecraft.getInstance();
            mc.ingameGUI.getBossOverlay().clearBossInfos();
            mc.getSoundHandler().play(new SimpleSound(SoundEvents.BLOCK_PORTAL_TRAVEL, SoundCategory.AMBIENT, 0.4F, 1F, msg.pos));
            for (int i = 0; i < 128; i++) {
                mc.world.addParticle(ParticleTypes.PORTAL, msg.pos.getX() + (mc.world.rand.nextDouble() - 0.5) * 3, msg.pos.getY() + mc.world.rand.nextDouble() * 3, msg.pos.getZ() + (mc.world.rand.nextDouble() - 0.5) * 3, (mc.world.rand.nextDouble() - 0.5) * 2, -mc.world.rand.nextDouble(), (mc.world.rand.nextDouble() - 0.5) * 2);
            }
        });
    }
}
