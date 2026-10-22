package net.quackimpala7321.crafter.inventory;

import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.quackimpala7321.crafter.tileentity.TileEntityCrafter;

public class SlotCrafterInput extends Slot {
    private final TileEntityCrafter crafter;

    public SlotCrafterInput(TileEntityCrafter crafter, int index, int x, int y) {
        super(crafter, index, x, y);
        this.crafter = crafter;
    }

    @Override
    public boolean isItemValid(ItemStack stack) {
        return !crafter.isSlotDisabled(this.getSlotIndex());
    }
}
