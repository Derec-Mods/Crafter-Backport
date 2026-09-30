package net.quackimpala7321.crafter.networking;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.network.NetworkEvent;
import net.quackimpala7321.crafter.block.entity.CrafterBlockEntity;
import net.minecraft.server.network.ServerPlayerEntity;

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
        buf.writeInt(slotId);
        buf.writeBlockPos(pos);
        buf.writeBoolean(newState);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            ServerPlayerEntity player = context.getSender();
            if (player == null) return;
            BlockEntity blockEntity = player.getWorld().getBlockEntity(pos);
            if (blockEntity instanceof CrafterBlockEntity crafterBlockEntity) {
                crafterBlockEntity.setSlotEnabled(slotId, newState);
            }
        });
        return true;
    }
}
