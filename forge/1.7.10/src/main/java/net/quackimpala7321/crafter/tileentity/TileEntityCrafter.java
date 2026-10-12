package net.quackimpala7321.crafter.tileentity;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraftforge.common.util.ForgeDirection;
import net.quackimpala7321.crafter.network.PacketCrafterSoundEffect;
import net.quackimpala7321.crafter.network.PacketHandler;
import net.quackimpala7321.crafter.recipe.CrafterRecipeCache;

public class TileEntityCrafter extends TileEntity implements ISidedInventory {
    public static final int GRID_SIZE = 9;
    private ItemStack[] items = new ItemStack[GRID_SIZE];
    private int disabledSlotsMask = 0;
    public int craftingTicksRemaining = 0;

    @Override
    public void updateEntity() {
        if (craftingTicksRemaining > 0) {
            craftingTicksRemaining--;
            if (craftingTicksRemaining == 0 && !worldObj.isRemote) {
                worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
            }
        }
    }

    public boolean isSlotDisabled(int slot) {
        return (slot >= 0 && slot < GRID_SIZE) && ((disabledSlotsMask & (1 << slot)) != 0);
    }

    public void setSlotEnabled(int slot, boolean enabled) {
        if (slot >= 0 && slot < GRID_SIZE && items[slot] == null) {
            if (enabled) {
                disabledSlotsMask &= ~(1 << slot);
            } else {
                disabledSlotsMask |= (1 << slot);
            }
            markDirty();
            if (worldObj != null && !worldObj.isRemote) {
                worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
            }
        }
    }

    public int getDisabledSlotsMask() {
        return disabledSlotsMask;
    }

    public void setDisabledSlotsMask(int mask) {
        this.disabledSlotsMask = mask;
    }

    public int getComparatorOutput() {
        int count = 0;
        for (int i = 0; i < GRID_SIZE; i++) {
            if (items[i] != null || isSlotDisabled(i)) {
                count++;
            }
        }
        return count;
    }

    @Override
    public void markDirty() {
        super.markDirty();
        if (this.worldObj != null) {
            this.worldObj.func_147453_f(this.xCoord, this.yCoord, this.zCoord, this.getBlockType());
        }
    }

    @Override
    public Packet getDescriptionPacket() {
        NBTTagCompound tag = new NBTTagCompound();
        this.writeToNBT(tag);
        return new S35PacketUpdateTileEntity(this.xCoord, this.yCoord, this.zCoord, 1, tag);
    }

    @Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
        this.readFromNBT(pkt.func_148857_g());
    }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack) {
        if (isSlotDisabled(slot)) return false;
        ItemStack current = items[slot];
        if (current != null && current.stackSize >= current.getMaxStackSize()) return false;
        if (current == null) {
            for (int i = 0; i < slot; i++) {
                if (!isSlotDisabled(i) && items[i] == null) return false;
            }
            return true;
        }
        return !betterSlotExists(current.stackSize, current, slot);
    }

    private boolean betterSlotExists(int count, ItemStack stack, int slot) {
        for (int i = slot + 1; i < GRID_SIZE; ++i) {
            if (!isSlotDisabled(i)) {
                ItemStack other = items[i];
                if (other == null || (other.stackSize < count && areItemsAndTagsEqual(other, stack))) {
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean areItemsAndTagsEqual(ItemStack a, ItemStack b) {
        if (a == null && b == null) return true;
        if (a == null || b == null) return false;
        return a.getItem() == b.getItem()
            && a.getItemDamage() == b.getItemDamage()
            && ItemStack.areItemStackTagsEqual(a, b);
    }

    public void craft() {
        InventoryCrafting craftMatrix = new InventoryCrafting(new Container() {
            @Override
            public boolean canInteractWith(EntityPlayer player) { return false; }
        }, 3, 3);
        for (int i = 0; i < GRID_SIZE; i++) {
            craftMatrix.setInventorySlotContents(i, items[i]);
        }
        ItemStack result = CrafterRecipeCache.findMatchingRecipe(craftMatrix, worldObj);
        int facing = worldObj.getBlockMetadata(xCoord, yCoord, zCoord) & 7;
        if (result == null) {
            PacketHandler.sendToAllAround(
                new PacketCrafterSoundEffect(xCoord, yCoord, zCoord, (byte) 1, (byte) facing),
                worldObj, xCoord, yCoord, zCoord, 64.0D
            );
            return;
        }

        this.craftingTicksRemaining = 6;
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
        PacketHandler.sendToAllAround(
            new PacketCrafterSoundEffect(xCoord, yCoord, zCoord, (byte) 0, (byte) facing),
            worldObj, xCoord, yCoord, zCoord, 64.0D
        );
        PacketHandler.sendToAllAround(
            new PacketCrafterSoundEffect(xCoord, yCoord, zCoord, (byte) 2, (byte) facing),
            worldObj, xCoord, yCoord, zCoord, 64.0D
        );

        transferOrSpawnStack(result.copy(), facing);

        for (int i = 0; i < GRID_SIZE; i++) {
            if (items[i] != null) {
                ItemStack containerItem = items[i].getItem().hasContainerItem(items[i])
                    ? items[i].getItem().getContainerItem(items[i])
                    : null;
                if (containerItem != null) {
                    transferOrSpawnStack(containerItem, facing);
                }
                items[i].stackSize--;
                if (items[i].stackSize <= 0) {
                    items[i] = null;
                }
            }
        }
        markDirty();
    }

    private void transferOrSpawnStack(ItemStack stack, int facing) {
        ForgeDirection dir = ForgeDirection.getOrientation(facing);
        int targetX = xCoord + dir.offsetX;
        int targetY = yCoord + dir.offsetY;
        int targetZ = zCoord + dir.offsetZ;
        TileEntity targetTE = worldObj.getTileEntity(targetX, targetY, targetZ);
        ItemStack remaining = stack;
        if (targetTE instanceof IInventory) {
            remaining = TileEntityHopper.func_145889_a((IInventory) targetTE, stack, dir.getOpposite().ordinal());
        }
        if (remaining != null && remaining.stackSize > 0) {
            double spawnX = xCoord + 0.5 + dir.offsetX * 0.7;
            double spawnY = yCoord + 0.5 + dir.offsetY * 0.7;
            double spawnZ = zCoord + 0.5 + dir.offsetZ * 0.7;
            EntityItem entity = new EntityItem(worldObj, spawnX, spawnY, spawnZ, remaining);
            entity.motionX = dir.offsetX * 0.3 + (worldObj.rand.nextDouble() - 0.5) * 0.05;
            entity.motionY = dir.offsetY * 0.3 + 0.1;
            entity.motionZ = dir.offsetZ * 0.3 + (worldObj.rand.nextDouble() - 0.5) * 0.05;
            worldObj.spawnEntityInWorld(entity);
        }
    }

    @Override public int getSizeInventory() { return GRID_SIZE; }
    @Override public ItemStack getStackInSlot(int slot) { return items[slot]; }

    @Override
    public ItemStack decrStackSize(int slot, int count) {
        if (items[slot] != null) {
            ItemStack stack;
            if (items[slot].stackSize <= count) {
                stack = items[slot];
                items[slot] = null;
            } else {
                stack = items[slot].splitStack(count);
                if (items[slot].stackSize == 0) items[slot] = null;
            }
            markDirty();
            return stack;
        }
        return null;
    }

    @Override public ItemStack getStackInSlotOnClosing(int slot) { return null; }

    @Override
    public void setInventorySlotContents(int slot, ItemStack stack) {
        if (isSlotDisabled(slot)) setSlotEnabled(slot, true);
        items[slot] = stack;
        markDirty();
    }

    @Override public String getInventoryName() { return "container.crafter"; }
    @Override public boolean hasCustomInventoryName() { return false; }
    @Override public int getInventoryStackLimit() { return 64; }

    @Override
    public boolean isUseableByPlayer(EntityPlayer player) {
        return worldObj.getTileEntity(xCoord, yCoord, zCoord) == this &&
            player.getDistanceSq(xCoord + 0.5D, yCoord + 0.5D, zCoord + 0.5D) <= 64.0D;
    }

    @Override public void openInventory() {}
    @Override public void closeInventory() {}
    @Override public int[] getAccessibleSlotsFromSide(int side) { return new int[]{0,1,2,3,4,5,6,7,8}; }
    @Override public boolean canInsertItem(int slot, ItemStack stack, int side) { return isItemValidForSlot(slot, stack); }
    @Override public boolean canExtractItem(int slot, ItemStack stack, int side) { return true; }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        NBTTagList list = nbt.getTagList("Items", 10);
        items = new ItemStack[GRID_SIZE];
        for (int i = 0; i < list.tagCount(); ++i) {
            NBTTagCompound itemTag = list.getCompoundTagAt(i);
            int slot = itemTag.getByte("Slot") & 255;
            if (slot < GRID_SIZE) items[slot] = ItemStack.loadItemStackFromNBT(itemTag);
        }
        disabledSlotsMask = nbt.getInteger("DisabledSlotsMask");
        craftingTicksRemaining = nbt.getInteger("CraftingTicksRemaining");
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        NBTTagList list = new NBTTagList();
        for (int i = 0; i < GRID_SIZE; ++i) {
            if (items[i] != null) {
                NBTTagCompound itemTag = new NBTTagCompound();
                itemTag.setByte("Slot", (byte) i);
                items[i].writeToNBT(itemTag);
                list.appendTag(itemTag);
            }
        }
        nbt.setTag("Items", list);
        nbt.setInteger("DisabledSlotsMask", disabledSlotsMask);
        nbt.setInteger("CraftingTicksRemaining", craftingTicksRemaining);
    }
}
