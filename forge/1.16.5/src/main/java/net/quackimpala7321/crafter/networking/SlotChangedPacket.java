package net.quackimpala7321.crafter.networking;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;
import net.quackimpala7321.crafter.block.entity.CrafterBlockEntity;

import java.util.function.Supplier;

public class SlotChangedPacket {
    public final int slotId;

    public SlotChangedPacket(int slotId) {
        this.slotId = slotId;
    }

    public SlotChangedPacket(PacketByteBuf buf) {
        this.slotId = buf.readInt();
    }

    public void toBytes(PacketByteBuf buf) {
        buf.writeInt(this.slotId);
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            ServerPlayerEntity player = context.getSender();
            if (player != null && player.currentScreenHandler instanceof net.quackimpala7321.crafter.screen.CrafterScreenHandler) {
                net.quackimpala7321.crafter.screen.CrafterScreenHandler handler = (net.quackimpala7321.crafter.screen.CrafterScreenHandler) player.currentScreenHandler;
                handler.setSlotEnabled(this.slotId, !handler.isSlotDisabled(this.slotId));
            }
        });
        context.setPacketHandled(true);
    }
}
