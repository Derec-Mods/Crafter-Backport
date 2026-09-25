package net.quackimpala7321.crafter.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.quackimpala7321.crafter.tileentity.TileEntityCrafter;

public class PacketToggleSlot implements IMessage {
    private BlockPos pos;
    private int slot;

    public PacketToggleSlot() {}

    public PacketToggleSlot(BlockPos pos, int slot) {
        this.pos = pos;
        this.slot = slot;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.pos = BlockPos.fromLong(buf.readLong());
        this.slot = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeLong(this.pos.toLong());
        buf.writeInt(this.slot);
    }

    public static class Handler implements IMessageHandler<PacketToggleSlot, IMessage> {
        @Override
        public IMessage onMessage(PacketToggleSlot message, MessageContext ctx) {
            IThreadListener mainThread = (WorldServer) ctx.getServerHandler().player.world;
            mainThread.addScheduledTask(() -> {
                WorldServer world = (WorldServer) ctx.getServerHandler().player.world;
                TileEntity te = world.getTileEntity(message.pos);
                if (te instanceof TileEntityCrafter) {
                    ((TileEntityCrafter) te).toggleSlot(message.slot);
                }
            });
            return null;
        }
    }
}
