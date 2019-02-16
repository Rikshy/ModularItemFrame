package de.shyrik.modularitemframe.common.network.packet;

import de.shyrik.modularitemframe.common.network.NetworkHandler;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class SpawnParticlesPacket implements IMessage, IMessageHandler<SpawnParticlesPacket, IMessage> {
    private int particleId;
    private BlockPos pos;
    private int amount;

    public SpawnParticlesPacket() {
    }

    public SpawnParticlesPacket(int particleId, BlockPos pos, int amount) {
        this.particleId = particleId;
        this.pos = pos;
        this.amount = amount;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        particleId = buf.readInt();
        pos = BlockPos.fromLong(buf.readLong());
        amount = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(particleId);
        buf.writeLong(pos.toLong());
        buf.writeInt(amount);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IMessage onMessage(SpawnParticlesPacket message, MessageContext ctx) {
        NetworkHandler.getThreadListener(ctx).addScheduledTask(() -> {
            EnumParticleTypes particle = EnumParticleTypes.getParticleFromId(message.particleId);
            if (particle != null) {
                Minecraft mc = Minecraft.getMinecraft();
                for (int i = 0; i < amount; i++) {
                    mc.world.spawnParticle(EnumParticleTypes.EXPLOSION_NORMAL, message.pos.getX(), message.pos.getY(), message.pos.getZ(), 0.0D, mc.world.rand.nextGaussian(), 0.0D);
                }
            }
        });
        return null;
    }
}
