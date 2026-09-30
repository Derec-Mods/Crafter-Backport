package net.quackimpala7321.crafter.networking;

import net.minecraft.util.Identifier;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import net.quackimpala7321.crafter.CrafterMod;

public class ModMessages {
    private static SimpleChannel INSTANCE;
    private static int packetId = 0;

    public static void register() {
        SimpleChannel net = NetworkRegistry.ChannelBuilder
                .named(new Identifier(CrafterMod.MOD_ID, "messages"))
                .networkProtocolVersion(() -> "1.0")
                .clientAcceptedVersions(s -> true)
                .serverAcceptedVersions(s -> true)
                .simpleChannel();

        INSTANCE = net;

        net.registerMessage(packetId++,
                SlotChangedPacket.class,
                SlotChangedPacket::toBytes,
                SlotChangedPacket::new,
                SlotChangedPacket::handle);
    }

    public static <MSG> void sendToServer(MSG message) {
        INSTANCE.sendToServer(message);
    }
}
