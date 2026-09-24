package net.quackimpala7321.crafter.tileentity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.SidedInvWrapper;

import javax.annotation.Nullable;

public class TileEntityCrafter extends TileEntity implements ISidedInventory, ITickable {
    public static final int GRID_SIZE = 9;
    private NonNullList<ItemStack> items = NonNullList.withSize(GRID_SIZE, ItemStack.EMPTY);
    private final boolean[] disabledSlots = new boolean[GRID_SIZE];
    private int craftingTicks = 0;
    private String customName;

    private static final int[] SLOTS = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8};
    private final IItemHandler itemHandler = new SidedInvWrapper(this, EnumFacing.UP);

    @Override
    public int getSizeInventory() {
        return GRID_SIZE;
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack stack : this.items) {
            if (!stack.isEmpty()) return false;
        }
        return true;
    }

    @Override
    public ItemStack getStackInSlot(int index) {
        if (index >= 0 && index < GRID_SIZE) {
            return this.items.get(index);
        }
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack decrStackSize(int index, int count) {
        ItemStack stack = ItemStackHelper.getAndSplit(this.items, index, count);
        if (!stack.isEmpty()) {
            this.markDirty();
        }
        return stack;
    }

    @Override
    public ItemStack removeStackFromSlot(int index) {
        return ItemStackHelper.getAndRemove(this.items, index);
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        if (index >= 0 && index < GRID_SIZE) {
            this.items.set(index, stack);
            if (stack.getCount() > this.getInventoryStackLimit()) {
                stack.setCount(this.getInventoryStackLimit());
            }
            this.markDirty();
        }
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    @Override
    public boolean isUsableByPlayer(EntityPlayer player) {
        if (this.world.getTileEntity(this.pos) != this) {
            return false;
        }
        return player.getDistanceSq((double) this.pos.getX() + 0.5D, (double) this.pos.getY() + 0.5D, (double) this.pos.getZ() + 0.5D) <= 64.0D;
    }

    @Override
    public void openInventory(EntityPlayer player) {}

    @Override
    public void closeInventory(EntityPlayer player) {}

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        if (this.isSlotDisabled(index)) {
            return false;
        }
        ItemStack current = this.getStackInSlot(index);
        if (current.getCount() >= current.getMaxStackSize()) {
            return false;
        } else if (current.isEmpty()) {
            return true;
        } else {
            return !this.betterSlotExists(current.getCount(), current, index);
        }
    }

    private boolean betterSlotExists(int count, ItemStack stack, int slot) {
        for (int i = slot + 1; i < GRID_SIZE; i++) {
            if (!this.isSlotDisabled(i)) {
                ItemStack itemStack = this.getStackInSlot(i);
                if (itemStack.isEmpty() || (itemStack.getCount() < count && ItemStack.areItemsEqual(itemStack, stack) && ItemStack.areItemStackTagsEqual(itemStack, stack))) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isSlotDisabled(int slot) {
        if (slot >= 0 && slot < GRID_SIZE) {
            return this.disabledSlots[slot];
        }
        return false;
    }

    public void setSlotDisabled(int slot, boolean disabled) {
        if (slot >= 0 && slot < GRID_SIZE) {
            this.disabledSlots[slot] = disabled;
            this.markDirty();
            if (this.world != null && !this.world.isRemote) {
                this.world.notifyBlockUpdate(this.pos, this.world.getBlockState(this.pos), this.world.getBlockState(this.pos), 3);
            }
        }
    }

    public void toggleSlot(int slot) {
        if (slot >= 0 && slot < GRID_SIZE && this.getStackInSlot(slot).isEmpty()) {
            setSlotDisabled(slot, !isSlotDisabled(slot));
        }
    }

    public int getComparatorOutput() {
        int count = 0;
        for (int i = 0; i < GRID_SIZE; i++) {
            ItemStack stack = this.getStackInSlot(i);
            if (!stack.isEmpty() || this.isSlotDisabled(i)) {
                count++;
            }
        }
        return count;
    }

    public boolean isCrafting() {
        return this.craftingTicks > 0;
    }

    public void setCraftingTicks(int ticks) {
        this.craftingTicks = ticks;
        if (this.world != null && !this.world.isRemote) {
            this.world.notifyBlockUpdate(this.pos, this.world.getBlockState(this.pos), this.world.getBlockState(this.pos), 3);
        }
    }

    @Override
    public void update() {
        if (this.craftingTicks > 0) {
            this.craftingTicks--;
            if (this.craftingTicks == 0 && this.world != null && !this.world.isRemote) {
                this.world.notifyBlockUpdate(this.pos, this.world.getBlockState(this.pos), this.world.getBlockState(this.pos), 3);
            }
        }
    }

    @Override
    public int[] getSlotsForFace(EnumFacing side) {
        return SLOTS;
    }

    @Override
    public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction) {
        return this.isItemValidForSlot(index, itemStackIn);
    }

    @Override
    public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
        return true;
    }

    @Override
    public int getField(int id) {
        if (id >= 0 && id < GRID_SIZE) {
            return this.disabledSlots[id] ? 1 : 0;
        } else if (id == 9) {
            return this.isCrafting() ? 1 : 0;
        }
        return 0;
    }

    @Override
    public void setField(int id, int value) {
        if (id >= 0 && id < GRID_SIZE) {
            this.disabledSlots[id] = (value == 1);
        } else if (id == 9) {
            this.craftingTicks = value > 0 ? 6 : 0;
        }
    }

    @Override
    public int getFieldCount() {
        return 10;
    }

    @Override
    public void clear() {
        this.items.clear();
    }

    @Override
    public String getName() {
        return this.hasCustomName() ? this.customName : "container.crafter";
    }

    @Override
    public boolean hasCustomName() {
        return this.customName != null && !this.customName.isEmpty();
    }

    public void setCustomName(String name) {
        this.customName = name;
    }

    @Override
    public ITextComponent getDisplayName() {
        return (ITextComponent)(this.hasCustomName() ? new TextComponentString(this.getName()) : new TextComponentTranslation(this.getName()));
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        this.items = NonNullList.withSize(this.getSizeInventory(), ItemStack.EMPTY);
        ItemStackHelper.loadAllItems(compound, this.items);

        int mask = compound.getInteger("DisabledSlots");
        for (int i = 0; i < GRID_SIZE; i++) {
            this.disabledSlots[i] = (mask & (1 << i)) != 0;
        }
        this.craftingTicks = compound.getInteger("CraftingTicks");

        if (compound.hasKey("CustomName", 8)) {
            this.customName = compound.getString("CustomName");
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        ItemStackHelper.saveAllItems(compound, this.items);

        int mask = 0;
        for (int i = 0; i < GRID_SIZE; i++) {
            if (this.disabledSlots[i]) {
                mask |= (1 << i);
            }
        }
        compound.setInteger("DisabledSlots", mask);
        compound.setInteger("CraftingTicks", this.craftingTicks);

        if (this.hasCustomName()) {
            compound.setString("CustomName", this.customName);
        }
        return compound;
    }

    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        return new SPacketUpdateTileEntity(this.pos, 1, this.getUpdateTag());
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        this.readFromNBT(pkt.getNbtCompound());
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        return this.writeToNBT(new NBTTagCompound());
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
    }

    @Nullable
    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(this.itemHandler);
        }
        return super.getCapability(capability, facing);
    }
}
