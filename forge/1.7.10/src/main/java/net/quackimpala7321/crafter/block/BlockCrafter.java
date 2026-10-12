package net.quackimpala7321.crafter.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.quackimpala7321.crafter.ClientProxy;
import net.quackimpala7321.crafter.CrafterMod;
import net.quackimpala7321.crafter.tileentity.TileEntityCrafter;

import java.util.Random;

public class BlockCrafter extends BlockContainer {
    @SideOnly(Side.CLIENT) public IIcon iconTop;
    @SideOnly(Side.CLIENT) public IIcon iconTopTriggered;
    @SideOnly(Side.CLIENT) public IIcon iconTopCrafting;
    @SideOnly(Side.CLIENT) public IIcon iconNorth;
    @SideOnly(Side.CLIENT) public IIcon iconNorthTriggered;
    @SideOnly(Side.CLIENT) public IIcon iconNorthCrafting;
    @SideOnly(Side.CLIENT) public IIcon iconSouth;
    @SideOnly(Side.CLIENT) public IIcon iconEast;
    @SideOnly(Side.CLIENT) public IIcon iconWest;
    @SideOnly(Side.CLIENT) public IIcon iconBottom;

    public BlockCrafter() {
        super(Material.rock);
        this.setHardness(1.5F);
        this.setResistance(3.5F);
        this.setStepSound(soundTypePiston);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileEntityCrafter();
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase placer, ItemStack stack) {
        int facing = getFacingFromEntity(placer);
        boolean powered = world.isBlockIndirectlyGettingPowered(x, y, z);
        int meta = (facing & 7) | (powered ? 8 : 0);
        world.setBlockMetadataWithNotify(x, y, z, meta, 2);
        if (powered) {
            world.scheduleBlockUpdate(x, y, z, this, 1);
        }
    }

    public static int getFacingFromEntity(EntityLivingBase entity) {
        if (entity.rotationPitch > 65.0F) return 1;
        if (entity.rotationPitch < -65.0F) return 0;
        int l = MathHelper.floor_double((double)(entity.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
        if (l == 0) return 2;
        if (l == 1) return 5;
        if (l == 2) return 3;
        return 4;
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, Block neighborBlock) {
        if (!world.isRemote) {
            int meta = world.getBlockMetadata(x, y, z);
            boolean isTriggered = (meta & 8) != 0;
            boolean isPowered = world.isBlockIndirectlyGettingPowered(x, y, z);
            if (isPowered && !isTriggered) {
                world.scheduleBlockUpdate(x, y, z, this, 1);
                world.setBlockMetadataWithNotify(x, y, z, meta | 8, 2);
            } else if (!isPowered && isTriggered) {
                world.setBlockMetadataWithNotify(x, y, z, meta & ~8, 2);
            }
        }
    }

    @Override
    public void updateTick(World world, int x, int y, int z, Random random) {
        if (!world.isRemote) {
            TileEntity te = world.getTileEntity(x, y, z);
            if (te instanceof TileEntityCrafter) {
                ((TileEntityCrafter) te).craft();
            }
        }
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
        if (!world.isRemote) {
            player.openGui(CrafterMod.instance, 0, world, x, y, z);
        }
        return true;
    }

    @Override
    public boolean hasComparatorInputOverride() {
        return true;
    }

    @Override
    public int getComparatorInputOverride(World world, int x, int y, int z, int side) {
        TileEntity te = world.getTileEntity(x, y, z);
        return (te instanceof TileEntityCrafter) ? ((TileEntityCrafter) te).getComparatorOutput() : 0;
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, Block block, int meta) {
        TileEntity te = world.getTileEntity(x, y, z);
        if (te instanceof TileEntityCrafter) {
            TileEntityCrafter crafter = (TileEntityCrafter) te;
            for (int i = 0; i < crafter.getSizeInventory(); ++i) {
                ItemStack stack = crafter.getStackInSlot(i);
                if (stack != null) {
                    float rx = world.rand.nextFloat() * 0.8F + 0.1F;
                    float ry = world.rand.nextFloat() * 0.8F + 0.1F;
                    float rz = world.rand.nextFloat() * 0.8F + 0.1F;
                    EntityItem entityItem = new EntityItem(world, x + rx, y + ry, z + rz, stack);
                    float f = 0.05F;
                    entityItem.motionX = (float)world.rand.nextGaussian() * f;
                    entityItem.motionY = (float)world.rand.nextGaussian() * f + 0.2F;
                    entityItem.motionZ = (float)world.rand.nextGaussian() * f;
                    world.spawnEntityInWorld(entityItem);
                }
            }
        }
        super.breakBlock(world, x, y, z, block, meta);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerBlockIcons(IIconRegister reg) {
        this.iconTop = reg.registerIcon("crafter:crafter_top");
        this.iconTopTriggered = reg.registerIcon("crafter:crafter_top_triggered");
        this.iconTopCrafting = reg.registerIcon("crafter:crafter_top_crafting");
        this.iconNorth = reg.registerIcon("crafter:crafter_north");
        this.iconNorthTriggered = reg.registerIcon("crafter:crafter_north_triggered");
        this.iconNorthCrafting = reg.registerIcon("crafter:crafter_north_crafting");
        this.iconSouth = reg.registerIcon("crafter:crafter_south");
        this.iconEast = reg.registerIcon("crafter:crafter_east");
        this.iconWest = reg.registerIcon("crafter:crafter_west");
        this.iconBottom = reg.registerIcon("crafter:crafter_bottom");
    }

    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side) {
        int meta = world.getBlockMetadata(x, y, z);
        int facing = meta & 7;
        boolean triggered = (meta & 8) != 0;
        TileEntity te = world.getTileEntity(x, y, z);
        boolean crafting = (te instanceof TileEntityCrafter) && (((TileEntityCrafter) te).craftingTicksRemaining > 0);
        return getCrafterIcon(side, facing, triggered, crafting);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIcon(int side, int meta) {
        int facing = meta & 7;
        if (facing == 0 && meta == 0) {
            facing = 3;
        }
        boolean triggered = (meta & 8) != 0;
        return getCrafterIcon(side, facing, triggered, false);
    }

    @SideOnly(Side.CLIENT)
    public IIcon getCrafterIcon(int side, int facing, boolean triggered, boolean crafting) {
        IIcon front = crafting ? iconNorthCrafting : (triggered ? iconNorthTriggered : iconNorth);
        IIcon top = crafting ? iconTopCrafting : (triggered ? iconTopTriggered : iconTop);

        if (side == facing) {
            return front;
        }
        if (facing == 0) {
            if (side == 1) return top;
            return iconSouth;
        }
        if (facing == 1) {
            if (side == 0) return iconBottom;
            return iconSouth;
        }
        if (side == 0) return iconBottom;
        if (side == 1) return top;
        if (side == ForgeDirection.OPPOSITES[facing]) return iconSouth;
        return (side == 4 || side == 5) ? iconEast : iconWest;
    }

    @Override
    public int getRenderType() {
        return ClientProxy.crafterRenderId;
    }
}
