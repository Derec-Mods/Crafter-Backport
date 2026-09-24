package net.quackimpala7321.crafter.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.*;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;
import net.quackimpala7321.crafter.tileentity.TileEntityCrafter;

public class ContainerCrafter extends Container {
    private final TileEntityCrafter crafter;
    private final InventoryCraftResult craftResult = new InventoryCraftResult();
    private final World world;

    public ContainerCrafter(InventoryPlayer playerInv, TileEntityCrafter crafter) {
        this.crafter = crafter;
        this.world = playerInv.player.world;

        // Crafter 3x3 grid (slots 0-8)
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 3; ++col) {
                int index = col + row * 3;
                this.addSlotToContainer(new Slot(crafter, index, 26 + col * 18, 17 + row * 18) {
                    @Override
                    public boolean isItemValid(ItemStack stack) {
                        return !crafter.isSlotDisabled(this.getSlotIndex()) && super.isItemValid(stack);
                    }
                });
            }
        }

        // Preview result slot (slot 9)
        this.addSlotToContainer(new Slot(this.craftResult, 0, 134, 35) {
            @Override
            public boolean isItemValid(ItemStack stack) {
                return false;
            }

            @Override
            public boolean canTakeStack(EntityPlayer playerIn) {
                return false;
            }
        });

        // Player Inventory (slots 10-36)
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                this.addSlotToContainer(new Slot(playerInv, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }

        // Player Hotbar (slots 37-45)
        for (int col = 0; col < 9; ++col) {
            this.addSlotToContainer(new Slot(playerInv, col, 8 + col * 18, 142));
        }

        this.onCraftMatrixChanged(this.crafter);
    }

    public TileEntityCrafter getCrafter() {
        return this.crafter;
    }

    @Override
    public void onCraftMatrixChanged(IInventory inventoryIn) {
        InventoryCrafting craftInv = new InventoryCrafting(new Container() {
            @Override
            public boolean canInteractWith(EntityPlayer playerIn) {
                return false;
            }
        }, 3, 3);

        for (int i = 0; i < 9; i++) {
            craftInv.setInventorySlotContents(i, this.crafter.getStackInSlot(i));
        }

        IRecipe recipe = CraftingManager.findMatchingRecipe(craftInv, this.world);
        ItemStack result = (recipe != null) ? recipe.getCraftingResult(craftInv) : ItemStack.EMPTY;
        this.craftResult.setInventorySlotContents(0, result);
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return this.crafter.isUsableByPlayer(playerIn);
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);

        if (slot != null && slot.getHasStack()) {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();

            if (index == 9) {
                // Result slot: cannot shift-click out
                return ItemStack.EMPTY;
            } else if (index < 9) {
                // Crafter grid -> Player inventory
                if (!this.mergeItemStack(itemstack1, 10, 46, true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                // Player inventory -> Crafter grid
                boolean merged = false;
                for (int i = 0; i < 9; i++) {
                    if (!this.crafter.isSlotDisabled(i)) {
                        Slot targetSlot = this.inventorySlots.get(i);
                        if (targetSlot.isItemValid(itemstack1) && this.mergeItemStack(itemstack1, i, i + 1, false)) {
                            merged = true;
                            if (itemstack1.isEmpty()) break;
                        }
                    }
                }
                if (!merged) {
                    return ItemStack.EMPTY;
                }
            }

            if (itemstack1.isEmpty()) {
                slot.putStack(ItemStack.EMPTY);
            } else {
                slot.onSlotChanged();
            }

            if (itemstack1.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(playerIn, itemstack1);
        }

        return itemstack;
    }
}
