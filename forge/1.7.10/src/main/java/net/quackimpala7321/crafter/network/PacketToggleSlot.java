package net.quackimpala7321.crafter.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.quackimpala7321.crafter.tileentity.TileEntityCrafter;

public class PacketToggleSlot implements IMessage {
    private int x, y, z;
    private int slotIndex;
    private boolean state;

    public PacketToggleSlot() {}

    public PacketToggleSlot(int x, int y, int z, int slotIndex, boolean state) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.slotIndex = slotIndex;
        this.state = state;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.x = buf.readInt();
        this.y = buf.readInt();
        this.z = buf.readInt();
        this.slotIndex = buf.readInt();
        this.state = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(x);
        buf.writeInt(y);
        buf.writeInt(z);
        buf.writeInt(slotIndex);
        buf.writeBoolean(state);
    }

    public static class Handler implements IMessageHandler<PacketToggleSlot, IMessage> {
        @Override
        public IMessage onMessage(PacketToggleSlot message, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().playerEntity;
            if (player != null && player.worldObj != null) {
                TileEntity te = player.worldObj.getTileEntity(message.x, message.y, message.z);
                if (te instanceof TileEntityCrafter) {
                    ((TileEntityCrafter) te).setSlotEnabled(message.slotIndex, message.state);
                }
            }
            return null;
        }
    }
}
