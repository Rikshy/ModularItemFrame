package de.shyrik.modularitemframe.common.network;

import de.shyrik.modularitemframe.ModularItemFrame;
import de.shyrik.modularitemframe.common.network.packet.PlaySoundPacket;
import de.shyrik.modularitemframe.common.network.packet.SpawnParticlesPacket;
import de.shyrik.modularitemframe.common.network.packet.TeleportEffectPacket;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class NetworkHandler {
    private static final SimpleChannel dispatcher = NetworkRegistry.ChannelBuilder.
            named(ModularItemFrame.CHANNEL)
            .clientAcceptedVersions(s -> true)
            .serverAcceptedVersions(s -> true)
            .networkProtocolVersion(() -> NetworkHooks.NETVERSION)
            .simpleChannel();

    private static int packetId = 0;

    public static void registerPackets() {
        dispatcher.registerMessage(packetId++, TeleportEffectPacket.class, TeleportEffectPacket::encode, TeleportEffectPacket::decode, TeleportEffectPacket::handle);
        dispatcher.registerMessage(packetId++, SpawnParticlesPacket.class, SpawnParticlesPacket::encode, SpawnParticlesPacket::decode, SpawnParticlesPacket::handle);
        dispatcher.registerMessage(packetId++, PlaySoundPacket.class, PlaySoundPacket::encode, PlaySoundPacket::decode, PlaySoundPacket::handle);
    }

    public static void sendTo(Object msg, EntityPlayerMP player) {
        dispatcher.sendTo(msg, player.connection.getNetworkManager(), NetworkDirection.PLAY_TO_CLIENT);
    }

    public static void sendAround(Object msg, World world, BlockPos pos, int range) {
        for(EntityPlayerMP player : world.getPlayers(EntityPlayerMP.class, player -> pos.getDistance(player.getPosition()) < range))
            sendTo(msg, player);
    }
}
