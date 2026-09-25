package net.quackimpala7321.crafter.client.gui;

import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Slot;
import net.minecraft.util.ResourceLocation;
import net.quackimpala7321.crafter.CrafterMod;
import net.quackimpala7321.crafter.inventory.ContainerCrafter;
import net.quackimpala7321.crafter.network.PacketToggleSlot;
import net.quackimpala7321.crafter.tileentity.TileEntityCrafter;

public class GuiCrafter extends GuiContainer {
    private static final ResourceLocation TEXTURE = new ResourceLocation(CrafterMod.MODID, "textures/gui/container/crafter.png");
    private static final ResourceLocation DISABLED_SLOT_TEXTURE = new ResourceLocation(CrafterMod.MODID, "textures/gui/sprites/container/crafter/disabled_slot.png");
    private static final ResourceLocation POWERED_REDSTONE = new ResourceLocation(CrafterMod.MODID, "textures/gui/sprites/container/crafter/powered_redstone.png");
    private static final ResourceLocation UNPOWERED_REDSTONE = new ResourceLocation(CrafterMod.MODID, "textures/gui/sprites/container/crafter/unpowered_redstone.png");

    private final TileEntityCrafter crafter;
    private final InventoryPlayer playerInv;

    public GuiCrafter(InventoryPlayer playerInv, TileEntityCrafter crafter) {
        super(new ContainerCrafter(playerInv, crafter));
        this.crafter = crafter;
        this.playerInv = playerInv;
        this.xSize = 176;
        this.ySize = 166;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);

        Slot hovered = this.getSlotUnderMouse();
        if (hovered != null && hovered.getSlotIndex() < 9 && hovered.inventory == this.crafter) {
            if (this.crafter.isSlotDisabled(hovered.getSlotIndex())) {
                this.drawHoveringText(I18n.format("crafter.gui.disabled_slot"), mouseX, mouseY);
            } else if (!hovered.getHasStack() && this.mc.player.inventory.getItemStack().isEmpty()) {
                this.drawHoveringText(I18n.format("crafter.gui.toggleable_slot"), mouseX, mouseY);
            }
        }
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        String title = this.crafter.getDisplayName().getUnformattedText();
        this.fontRenderer.drawString(title, (this.xSize - this.fontRenderer.getStringWidth(title)) / 2, 6, 4210752);
        this.fontRenderer.drawString(this.playerInv.getDisplayName().getUnformattedText(), 8, this.ySize - 96 + 2, 4210752);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(TEXTURE);
        int x = (this.width - this.xSize) / 2;
        int y = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(x, y, 0, 0, this.xSize, this.ySize);

        // Draw redstone arrow indicator
        boolean powered = this.crafter.getWorld() != null && this.crafter.getWorld().isBlockPowered(this.crafter.getPos());
        this.mc.getTextureManager().bindTexture(powered ? POWERED_REDSTONE : UNPOWERED_REDSTONE);
        drawModalRectWithCustomSizedTexture(x + 97, y + 35, 0, 0, 16, 16, 16, 16);

        // Draw disabled slot overlays
        this.mc.getTextureManager().bindTexture(DISABLED_SLOT_TEXTURE);
        for (int i = 0; i < 9; i++) {
            if (this.crafter.isSlotDisabled(i)) {
                Slot slot = this.inventorySlots.inventorySlots.get(i);
                drawModalRectWithCustomSizedTexture(x + slot.xPos - 1, y + slot.yPos - 1, 0, 0, 18, 18, 18, 18);
            }
        }
    }

    @Override
    protected void handleMouseClick(Slot slotIn, int slotId, int mouseButton, ClickType type) {
        if (slotIn != null && slotIn.getSlotIndex() < 9 && slotIn.inventory == this.crafter) {
            boolean disabled = this.crafter.isSlotDisabled(slotIn.getSlotIndex());

            if (type == ClickType.PICKUP && !slotIn.getHasStack() && this.mc.player.inventory.getItemStack().isEmpty()) {
                CrafterMod.network.sendToServer(new PacketToggleSlot(this.crafter.getPos(), slotIn.getSlotIndex()));
                this.crafter.toggleSlot(slotIn.getSlotIndex());
                float pitch = disabled ? 0.75F : 1.0F;
                this.mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, pitch));
                return;
            }

            if (disabled) {
                return;
            }
        }

        super.handleMouseClick(slotIn, slotId, mouseButton, type);
    }
}
