package net.quackimpala7321.crafter.gui.screen.ingame;

import com.mojang.blaze3d.systems.RenderSystem;




import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.TranslatableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.quackimpala7321.crafter.CrafterMod;
import net.quackimpala7321.crafter.networking.ModMessages; import net.quackimpala7321.crafter.networking.SlotChangedPacket;
import net.quackimpala7321.crafter.networking.ModMessages;
import net.quackimpala7321.crafter.screen.CrafterScreenHandler;
import net.quackimpala7321.crafter.screen.slot.CrafterInputSlot;


public class CrafterScreen extends HandledScreen<CrafterScreenHandler> {
    private static final Identifier DISABLED_SLOT_TEXTURE = new Identifier(CrafterMod.MOD_ID, "textures/gui/sprites/container/crafter/disabled_slot.png");
    private static final Identifier POWERED_REDSTONE_TEXTURE = new Identifier(CrafterMod.MOD_ID, "textures/gui/sprites/container/crafter/powered_redstone.png");
    private static final Identifier UNPOWERED_REDSTONE_TEXTURE = new Identifier(CrafterMod.MOD_ID, "textures/gui/sprites/container/crafter/unpowered_redstone.png");
    private static final Identifier TEXTURE = new Identifier(CrafterMod.MOD_ID, "textures/gui/container/crafter.png");
    private static final Text TOGGLEABLE_SLOT_TEXT = new TranslatableText("crafter.gui.toggleable_slot");
    private final PlayerEntity player;

    public CrafterScreen(CrafterScreenHandler handler, PlayerInventory playerInventory, Text title) {
        super(handler, playerInventory, title);
        this.player = playerInventory.player;
    }

    @Override
    protected void init() {
        super.init();
        this.titleX = (this.backgroundWidth - this.textRenderer.getWidth(this.title)) / 2;
    }

    protected void onSlotChangedState(int slotId, int handlerId, boolean newState) {
        if (this.client == null || this.client.world == null) return;

        net.minecraft.network.PacketByteBuf buf = new net.minecraft.network.PacketByteBuf(io.netty.buffer.Unpooled.buffer());
        buf.writeInt(slotId);
        buf.writeBlockPos(this.handler.getPos());
        buf.writeBoolean(newState);
        ModMessages.sendToServer(new SlotChangedPacket(slotId));
    }

    @Override
    protected void onMouseClick(Slot slot, int slotId, int button, SlotActionType actionType) {
        if (this.player.isSpectator()) {
            super.onMouseClick(slot, slotId, button, actionType);
        } else {
            if (slotId > -1 && slotId < 9 && slot instanceof CrafterInputSlot) {
                boolean disabled = this.handler.isSlotDisabled(slotId);
                
                if (actionType == SlotActionType.PICKUP && !slot.hasStack() && this.playerInventory.getCursorStack().isEmpty()) {
                    this.handler.setSlotEnabled(slotId, disabled);
                    this.onSlotChangedState(slotId, this.handler.syncId, disabled);
                    if (disabled) {
                        this.player.playSound(SoundEvents.UI_BUTTON_CLICK, 0.4F, 1.0F);
                    } else {
                        this.player.playSound(SoundEvents.UI_BUTTON_CLICK, 0.4F, 0.75F);
                    }
                    return;
                }
                
                if (disabled) {
                    return;
                }
            }

            super.onMouseClick(slot, slotId, button, actionType);
        }
    }

    public void drawDisabledSlot(MatrixStack matrices, CrafterInputSlot slot) {
        
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        client.getTextureManager().bindTexture(DISABLED_SLOT_TEXTURE);
        drawTexture(matrices, slot.x - 1, slot.y - 1, 0, 0, 18, 18, 18, 18);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        super.render(matrices, mouseX, mouseY, delta);
        this.drawArrowTexture(matrices);
        this.drawMouseoverTooltip(matrices, mouseX, mouseY);
        if (this.focusedSlot instanceof CrafterInputSlot && !this.handler.isSlotDisabled(this.focusedSlot.id) && this.playerInventory.getCursorStack().isEmpty() && !this.focusedSlot.hasStack()) {
            this.renderTooltip(matrices, TOGGLEABLE_SLOT_TEXT, mouseX, mouseY);
        }
    }

    private void drawArrowTexture(MatrixStack matrices) {
        int i = this.width / 2 + 9;
        int j = this.height / 2 - 48;
        
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        client.getTextureManager().bindTexture(this.handler.isTriggered() ? POWERED_REDSTONE_TEXTURE : UNPOWERED_REDSTONE_TEXTURE);
        drawTexture(matrices, i, j, 0, 0, 16, 16, 16, 16);
    }

    @Override
    protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
        
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        client.getTextureManager().bindTexture(TEXTURE);
        int i = (this.width - this.backgroundWidth) / 2;
        int j = (this.height - this.backgroundHeight) / 2;
        this.drawTexture(matrices, i, j, 0, 0, this.backgroundWidth, this.backgroundHeight);
    }
}


