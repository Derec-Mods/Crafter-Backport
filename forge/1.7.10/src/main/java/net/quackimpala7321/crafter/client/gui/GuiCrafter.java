package net.quackimpala7321.crafter.client.gui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import net.quackimpala7321.crafter.inventory.ContainerCrafter;
import net.quackimpala7321.crafter.inventory.SlotCrafterInput;
import net.quackimpala7321.crafter.network.PacketHandler;
import net.quackimpala7321.crafter.network.PacketToggleSlot;
import net.quackimpala7321.crafter.tileentity.TileEntityCrafter;

@SideOnly(Side.CLIENT)
public class GuiCrafter extends GuiContainer {
    private static final ResourceLocation GUI_TEXTURE = new ResourceLocation("crafter", "textures/gui/crafter.png");
    private static final ResourceLocation DISABLED_SLOT = new ResourceLocation("crafter", "textures/gui/sprites/disabled_slot.png");
    private static final ResourceLocation POWERED_REDSTONE = new ResourceLocation("crafter", "textures/gui/sprites/powered_redstone.png");
    private static final ResourceLocation UNPOWERED_REDSTONE = new ResourceLocation("crafter", "textures/gui/sprites/unpowered_redstone.png");

    private final TileEntityCrafter crafter;

    public GuiCrafter(InventoryPlayer playerInv, TileEntityCrafter crafter) {
        super(new ContainerCrafter(playerInv, crafter));
        this.crafter = crafter;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        this.fontRendererObj.drawString(I18n.format("container.crafter"), 28, 6, 4210752);
        this.fontRendererObj.drawString(I18n.format("container.inventory"), 8, this.ySize - 96 + 2, 4210752);

        Slot hovered = getSlotAtPosition(mouseX, mouseY);
        if (hovered instanceof SlotCrafterInput && !crafter.isSlotDisabled(hovered.getSlotIndex()) &&
            !hovered.getHasStack() && mc.thePlayer.inventory.getItemStack() == null) {
            this.drawCreativeTabHoveringText(I18n.format("crafter.gui.toggleable_slot"), mouseX - guiLeft, mouseY - guiTop);
        }
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        mc.getTextureManager().bindTexture(GUI_TEXTURE);
        int k = (this.width - this.xSize) / 2;
        int l = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(k, l, 0, 0, this.xSize, this.ySize);

        boolean isPowered = (crafter.getWorldObj().getBlockMetadata(crafter.xCoord, crafter.yCoord, crafter.zCoord) & 8) != 0;
        mc.getTextureManager().bindTexture(isPowered ? POWERED_REDSTONE : UNPOWERED_REDSTONE);
        drawCustomTexture(k + 134, l + 17, 16, 16);

        mc.getTextureManager().bindTexture(DISABLED_SLOT);
        for (int i = 0; i < 9; i++) {
            if (crafter.isSlotDisabled(i)) {
                Slot slot = (Slot) this.inventorySlots.inventorySlots.get(i);
                drawCustomTexture(k + slot.xDisplayPosition - 1, l + slot.yDisplayPosition - 1, 18, 18);
            }
        }
    }

    private void drawCustomTexture(int x, int y, int width, int height) {
        GL11.glEnable(GL11.GL_BLEND);
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV(x, y + height, 0, 0.0, 1.0);
        tessellator.addVertexWithUV(x + width, y + height, 0, 1.0, 1.0);
        tessellator.addVertexWithUV(x + width, y, 0, 1.0, 0.0);
        tessellator.addVertexWithUV(x, y, 0, 0.0, 0.0);
        tessellator.draw();
        GL11.glDisable(GL11.GL_BLEND);
    }

    @Override
    protected void handleMouseClick(Slot slot, int slotId, int clickedButton, int clickType) {
        if (slot instanceof SlotCrafterInput && slotId < 9) {
            boolean disabled = crafter.isSlotDisabled(slotId);
            if (!slot.getHasStack() && mc.thePlayer.inventory.getItemStack() == null) {
                crafter.setSlotEnabled(slotId, disabled);
                PacketHandler.sendToServer(new PacketToggleSlot(crafter.xCoord, crafter.yCoord, crafter.zCoord, slotId, disabled));
                mc.thePlayer.playSound("gui.button.press", 0.4F, disabled ? 1.0F : 0.75F);
                return;
            }
            if (disabled) return;
        }
        super.handleMouseClick(slot, slotId, clickedButton, clickType);
    }

    private Slot getSlotAtPosition(int x, int y) {
        for (int k = 0; k < this.inventorySlots.inventorySlots.size(); ++k) {
            Slot slot = (Slot) this.inventorySlots.inventorySlots.get(k);
            if (isMouseOverSlot(slot, x, y)) return slot;
        }
        return null;
    }

    private boolean isMouseOverSlot(Slot slot, int x, int y) {
        return this.func_146978_c(slot.xDisplayPosition, slot.yDisplayPosition, 16, 16, x, y);
    }
}
