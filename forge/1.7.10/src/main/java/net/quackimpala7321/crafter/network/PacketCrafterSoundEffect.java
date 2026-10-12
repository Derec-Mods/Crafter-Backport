package net.quackimpala7321.crafter.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.world.World;
import net.quackimpala7321.crafter.client.particle.EntityWhiteSmokeFX;

public class PacketCrafterSoundEffect implements IMessage {
    private int x, y, z;
    private byte eventType;
    private byte facing;

    public PacketCrafterSoundEffect() {}

    public PacketCrafterSoundEffect(int x, int y, int z, byte eventType, byte facing) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.eventType = eventType;
        this.facing = facing;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.x = buf.readInt();
        this.y = buf.readInt();
        this.z = buf.readInt();
        this.eventType = buf.readByte();
        this.facing = buf.readByte();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(x);
        buf.writeInt(y);
        buf.writeInt(z);
        buf.writeByte(eventType);
        buf.writeByte(facing);
    }

    public static class Handler implements IMessageHandler<PacketCrafterSoundEffect, IMessage> {
        @Override
        public IMessage onMessage(PacketCrafterSoundEffect msg, MessageContext ctx) {
            World world = Minecraft.getMinecraft().theWorld;
            if (world == null) return null;
            if (msg.eventType == 0) {
                world.playSound(msg.x + 0.5, msg.y + 0.5, msg.z + 0.5, "crafter:crafter_craft", 1.0F, 1.0F, false);
            } else if (msg.eventType == 1) {
                world.playSound(msg.x + 0.5, msg.y + 0.5, msg.z + 0.5, "crafter:crafter_fail", 1.0F, 1.0F, false);
            } else if (msg.eventType == 2) {
                for (int i = 0; i < 10; i++) {
                    Minecraft.getMinecraft().effectRenderer.addEffect(
                        new EntityWhiteSmokeFX(world, msg.x + 0.5, msg.y + 0.5, msg.z + 0.5, msg.facing)
                    );
                }
            }
            return null;
        }
    }
}
