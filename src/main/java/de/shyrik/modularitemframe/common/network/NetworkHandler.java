package de.shyrik.modularitemframe.common.network;

import de.shyrik.modularitemframe.ModularItemFrame;
import de.shyrik.modularitemframe.common.network.packet.*;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class NetworkHandler {
    private static final SimpleChannel dispatcher = NetworkRegistry.ChannelBuilder.
            named(ModularItemFrame.CHANNEL)
            .clientAcceptedVersions(s -> true)
            .serverAcceptedVersions(s -> true)
            .networkProtocolVersion(() -> "1")
            .simpleChannel();

    private static int packetId = 0;

    public static void registerPackets() {
        dispatcher.registerMessage(packetId++, TeleportEffectPacket.class, TeleportEffectPacket::encode, TeleportEffectPacket::decode, TeleportEffectPacket::handle);
        dispatcher.registerMessage(packetId++, SpawnParticlesPacket.class, SpawnParticlesPacket::encode, SpawnParticlesPacket::decode, SpawnParticlesPacket::handle);
        dispatcher.registerMessage(packetId++, PlaySoundPacket.class, PlaySoundPacket::encode, PlaySoundPacket::decode, PlaySoundPacket::handle);
    }

    public static void sendTo(Object msg, ServerPlayerEntity player) {
        dispatcher.sendTo(msg, player.connection.getNetworkManager(), NetworkDirection.PLAY_TO_CLIENT);
    }

    public static void sendAround(Object msg, World world, BlockPos pos, int radius) {
        dispatcher.send(PacketDistributor.NEAR.with(() -> new PacketDistributor.TargetPoint(pos.getX(), pos.getY(), pos.getZ(), radius, world.dimension.getType())), msg);
    }
}
