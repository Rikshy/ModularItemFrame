package de.shyrik.modularitemframe.common.network;

import de.shyrik.modularitemframe.ModularItemFrame;
import de.shyrik.modularitemframe.common.network.packet.*;
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
        //dispatcher.registerMessage(packetId++, SpawnParticlesPacket.class, SpawnParticlesPacket.class);
        //dispatcher.registerMessage(packetId++, PlaySoundPacket.class, PlaySoundPacket.class, Side.CLIENT);
    }

    public static void sendAround() {
        dispatcher.sendTo(null, null, NetworkDirection.PLAY_TO_CLIENT);
    }
}
