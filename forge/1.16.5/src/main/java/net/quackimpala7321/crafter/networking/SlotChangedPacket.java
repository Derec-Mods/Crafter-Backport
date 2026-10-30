package net.quackimpala7321.crafter.networking;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;
import net.quackimpala7321.crafter.block.entity.CrafterBlockEntity;

import java.util.function.Supplier;

public class SlotChangedPacket {
    private final int slotId;
    private final BlockPos pos;
    private final boolean newState;

    public SlotChangedPacket(int slotId, BlockPos pos, boolean newState) {
        this.slotId = slotId;
        this.pos = pos;
        this.newState = newState;
    }

    public SlotChangedPacket(PacketByteBuf buf) {
        this.slotId = buf.readInt();
        this.pos = buf.readBlockPos();
        this.newState = buf.readBoolean();
    }

    public void toBytes(PacketByteBuf buf) {
        buf.writeInt(this.slotId);
        buf.writeBlockPos(this.pos);
        buf.writeBoolean(this.newState);
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            ServerPlayerEntity player = context.getSender();
            if (player == null) return;
            BlockEntity blockEntity = player.getServerWorld().getBlockEntity(this.pos);
            if (blockEntity instanceof CrafterBlockEntity) {
                CrafterBlockEntity crafterBlockEntity = (CrafterBlockEntity) blockEntity;
                crafterBlockEntity.setSlotEnabled(this.slotId, this.newState);
            }
        });
        context.setPacketHandled(true);
    }
}
