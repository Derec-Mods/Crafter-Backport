package net.quackimpala7321.crafter.inventory;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCraftResult;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.quackimpala7321.crafter.recipe.CrafterRecipeCache;
import net.quackimpala7321.crafter.tileentity.TileEntityCrafter;

public class ContainerCrafter extends Container {
    private final TileEntityCrafter crafter;
    private final InventoryCraftResult resultInventory = new InventoryCraftResult();
    private int lastDisabledSlotsMask = -1;

    public ContainerCrafter(InventoryPlayer playerInv, TileEntityCrafter crafter) {
        this.crafter = crafter;

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                this.addSlotToContainer(new SlotCrafterInput(crafter, col + row * 3, 26 + col * 18, 17 + row * 18));
            }
        }

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlotToContainer(new Slot(playerInv, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }

        for (int col = 0; col < 9; col++) {
            this.addSlotToContainer(new Slot(playerInv, col, 8 + col * 18, 142));
        }

        this.addSlotToContainer(new SlotCrafterOutput(resultInventory, 0, 134, 35));

        this.onCraftMatrixChanged(crafter);
    }

    @Override
    public void onCraftMatrixChanged(IInventory inv) {
        InventoryCrafting craftMatrix = new InventoryCrafting(new Container() {
            @Override
            public boolean canInteractWith(EntityPlayer player) {
                return false;
            }
        }, 3, 3);
        for (int i = 0; i < 9; i++) {
            craftMatrix.setInventorySlotContents(i, crafter.getStackInSlot(i));
        }
        ItemStack result = CrafterRecipeCache.findMatchingRecipe(craftMatrix, crafter.getWorldObj());
        resultInventory.setInventorySlotContents(0, result);
    }

    @Override
    public void addCraftingToCrafters(ICrafting listener) {
        super.addCraftingToCrafters(listener);
        listener.sendProgressBarUpdate(this, 0, crafter.getDisabledSlotsMask());
    }

    @Override
    public void detectAndSendChanges() {
        this.onCraftMatrixChanged(crafter);
        super.detectAndSendChanges();
        for (Object listenerObj : this.crafters) {
            ICrafting listener = (ICrafting) listenerObj;
            if (this.lastDisabledSlotsMask != crafter.getDisabledSlotsMask()) {
                listener.sendProgressBarUpdate(this, 0, crafter.getDisabledSlotsMask());
            }
        }
        this.lastDisabledSlotsMask = crafter.getDisabledSlotsMask();
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void updateProgressBar(int id, int value) {
        if (id == 0) {
            crafter.setDisabledSlotsMask(value);
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return crafter.isUseableByPlayer(player);
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int slotIndex) {
        ItemStack itemstack = null;
        Slot slot = (Slot) this.inventorySlots.get(slotIndex);
        if (slot != null && slot.getHasStack()) {
            ItemStack stackInSlot = slot.getStack();
            itemstack = stackInSlot.copy();

            if (slotIndex == 45) {
                return null;
            } else if (slotIndex < 9) {
                if (!this.mergeItemStack(stackInSlot, 9, 45, true)) {
                    return null;
                }
            } else if (!this.mergeItemStack(stackInSlot, 0, 9, false)) {
                return null;
            }

            if (stackInSlot.stackSize == 0) {
                slot.putStack(null);
            } else {
                slot.onSlotChanged();
            }

            if (stackInSlot.stackSize == itemstack.stackSize) {
                return null;
            }

            slot.onPickupFromSlot(player, stackInSlot);
        }
        return itemstack;
    }

    @Override
    protected boolean mergeItemStack(ItemStack stack, int startIndex, int endIndex, boolean reverse) {
        if (startIndex == 0 && endIndex == 9) {
            boolean merged = false;
            for (int i = 0; i < 9; i++) {
                if (crafter.isSlotDisabled(i)) continue;
                Slot slot = (Slot) this.inventorySlots.get(i);
                ItemStack existing = slot.getStack();
                if (existing != null && areItemsAndTagsEqual(existing, stack)) {
                    int max = Math.min(existing.getMaxStackSize(), slot.getSlotStackLimit());
                    int diff = max - existing.stackSize;
                    if (diff > 0) {
                        int toAdd = Math.min(diff, stack.stackSize);
                        existing.stackSize += toAdd;
                        stack.stackSize -= toAdd;
                        slot.onSlotChanged();
                        merged = true;
                        if (stack.stackSize <= 0) break;
                    }
                }
            }
            if (stack.stackSize > 0) {
                for (int i = 0; i < 9; i++) {
                    if (crafter.isSlotDisabled(i)) continue;
                    Slot slot = (Slot) this.inventorySlots.get(i);
                    if (!slot.getHasStack()) {
                        int toAdd = Math.min(stack.getMaxStackSize(), stack.stackSize);
                        ItemStack newStack = stack.copy();
                        newStack.stackSize = toAdd;
                        stack.stackSize -= toAdd;
                        slot.putStack(newStack);
                        slot.onSlotChanged();
                        merged = true;
                        if (stack.stackSize <= 0) break;
                    }
                }
            }
            return merged;
        }
        return super.mergeItemStack(stack, startIndex, endIndex, reverse);
    }

    private static boolean areItemsAndTagsEqual(ItemStack a, ItemStack b) {
        if (a == null && b == null) return true;
        if (a == null || b == null) return false;
        return a.getItem() == b.getItem()
            && a.getItemDamage() == b.getItemDamage()
            && ItemStack.areItemStackTagsEqual(a, b);
    }

    public TileEntityCrafter getCrafter() {
        return crafter;
    }
}
