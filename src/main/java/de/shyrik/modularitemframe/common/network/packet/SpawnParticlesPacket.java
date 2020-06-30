package de.shyrik.modularitemframe.common.network.packet;

import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

public class SpawnParticlesPacket {
    private ResourceLocation particleId;
    private BlockPos pos;
    private int amount;

    public SpawnParticlesPacket(ResourceLocation particleId, BlockPos pos, int amount) {
        this.particleId = particleId;
        this.pos = pos;
        this.amount = amount;
    }

    public static void encode(SpawnParticlesPacket msg, PacketBuffer buf) {
        buf.writeResourceLocation(msg.particleId);
        buf.writeLong(msg.pos.toLong());
        buf.writeInt(msg.amount);
    }

    public static SpawnParticlesPacket decode(PacketBuffer buf) {
        return new SpawnParticlesPacket(
                buf.readResourceLocation(),
                BlockPos.fromLong(buf.readLong()),
                buf.readInt()
        );
    }

    public static void handle(SpawnParticlesPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ParticleType particle = ForgeRegistries.PARTICLE_TYPES.getValue(msg.particleId);
            if (particle != null) {
                Minecraft mc = Minecraft.getInstance();
                for (int i = 0; i < msg.amount; i++) {
                    mc.world.addParticle((IParticleData)particle, msg.pos.getX(), msg.pos.getY(), msg.pos.getZ(), 0.0D, mc.world.rand.nextGaussian(), 0.0D);
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
