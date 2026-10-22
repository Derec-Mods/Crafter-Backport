package net.quackimpala7321.crafter.network;

import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.world.World;

public class PacketHandler {
    public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel("crafter");

    public static void init() {
        INSTANCE.registerMessage(PacketToggleSlot.Handler.class, PacketToggleSlot.class, 0, Side.SERVER);
        INSTANCE.registerMessage(PacketCrafterSoundEffect.Handler.class, PacketCrafterSoundEffect.class, 1, Side.CLIENT);
    }

    public static void sendToServer(IMessage msg) {
        INSTANCE.sendToServer(msg);
    }

    public static void sendToAllAround(IMessage msg, World world, int x, int y, int z, double range) {
        INSTANCE.sendToAllAround(msg, new NetworkRegistry.TargetPoint(world.provider.dimensionId, x, y, z, range));
    }
}
