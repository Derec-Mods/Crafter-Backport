package net.quackimpala7321.crafter.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.quackimpala7321.crafter.CrafterMod;
import net.quackimpala7321.crafter.init.ModSounds;
import net.quackimpala7321.crafter.proxy.CommonProxy;
import net.quackimpala7321.crafter.tileentity.TileEntityCrafter;

import java.util.Random;

public class BlockCrafter extends Block {
    public static final PropertyDirection FACING = BlockDirectional.FACING;
    public static final PropertyBool TRIGGERED = PropertyBool.create("triggered");
    public static final PropertyBool CRAFTING = PropertyBool.create("crafting");

    public BlockCrafter() {
        super(Material.ROCK);
        this.setHardness(1.5F);
        this.setResistance(3.5F);
        this.setCreativeTab(CreativeTabs.REDSTONE);
        this.setDefaultState(this.blockState.getBaseState()
                .withProperty(FACING, EnumFacing.NORTH)
                .withProperty(TRIGGERED, false)
                .withProperty(CRAFTING, false));
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileEntityCrafter();
    }

    @Override
    public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        return this.getDefaultState()
                .withProperty(FACING, EnumFacing.getDirectionFromEntityLiving(pos, placer))
                .withProperty(TRIGGERED, worldIn.isBlockPowered(pos))
                .withProperty(CRAFTING, false);
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        if (stack.hasDisplayName()) {
            TileEntity te = worldIn.getTileEntity(pos);
            if (te instanceof TileEntityCrafter) {
                ((TileEntityCrafter) te).setCustomName(stack.getDisplayName());
            }
        }
    }

    @Override
    public int tickRate(World worldIn) {
        return 4;
    }

    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
        boolean hasPower = worldIn.isBlockPowered(pos);
        boolean isTriggered = state.getValue(TRIGGERED);

        if (hasPower && !isTriggered) {
            worldIn.scheduleUpdate(pos, this, this.tickRate(worldIn));
            worldIn.setBlockState(pos, state.withProperty(TRIGGERED, true), 4);
        } else if (!hasPower && isTriggered) {
            worldIn.setBlockState(pos, state.withProperty(TRIGGERED, false), 4);
        }
    }

    @Override
    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
        if (!worldIn.isRemote) {
            this.craft(worldIn, pos, state);
        }
    }

    protected void craft(World worldIn, BlockPos pos, IBlockState state) {
        TileEntity te = worldIn.getTileEntity(pos);
        if (!(te instanceof TileEntityCrafter)) return;
        TileEntityCrafter crafter = (TileEntityCrafter) te;

        InventoryCrafting craftInv = new InventoryCrafting(new Container() {
            @Override
            public boolean canInteractWith(EntityPlayer playerIn) {
                return false;
            }
        }, 3, 3);

        for (int i = 0; i < 9; i++) {
            craftInv.setInventorySlotContents(i, crafter.getStackInSlot(i));
        }

        IRecipe recipe = CraftingManager.findMatchingRecipe(craftInv, worldIn);
        EnumFacing facing = state.getValue(FACING);

        if (recipe == null) {
            worldIn.playSound(null, pos, ModSounds.CRAFTER_FAIL, SoundCategory.BLOCKS, 1.0F, 1.0F);
            this.spawnSmokeParticles(worldIn, pos, facing);
        } else {
            ItemStack result = recipe.getCraftingResult(craftInv);
            NonNullList<ItemStack> remaining = recipe.getRemainingItems(craftInv);

            crafter.setCraftingTicks(6);
            worldIn.playSound(null, pos, ModSounds.CRAFTER_CRAFT, SoundCategory.BLOCKS, 1.0F, 1.0F);
            this.spawnSmokeParticles(worldIn, pos, facing);

            this.transferOrSpawnStack(worldIn, pos, result.copy(), facing);
            for (ItemStack rem : remaining) {
                if (!rem.isEmpty()) {
                    this.transferOrSpawnStack(worldIn, pos, rem.copy(), facing);
                }
            }

            for (int i = 0; i < 9; i++) {
                ItemStack stack = crafter.getStackInSlot(i);
                if (!stack.isEmpty()) {
                    crafter.decrStackSize(i, 1);
                }
            }

            crafter.markDirty();
            worldIn.updateComparatorOutputLevel(pos, this);
        }
    }

    private void spawnSmokeParticles(World world, BlockPos pos, EnumFacing facing) {
        if (world instanceof WorldServer) {
            WorldServer ws = (WorldServer) world;
            double x = pos.getX() + 0.5D + (double) facing.getXOffset() * 0.6D;
            double y = pos.getY() + 0.5D + (double) facing.getYOffset() * 0.6D;
            double z = pos.getZ() + 0.5D + (double) facing.getZOffset() * 0.6D;
            ws.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, x, y, z, 10, 0.1D, 0.1D, 0.1D, 0.05D);
        }
    }

    private void transferOrSpawnStack(World world, BlockPos pos, ItemStack stack, EnumFacing facing) {
        BlockPos targetPos = pos.offset(facing);
        TileEntity targetTe = world.getTileEntity(targetPos);

        if (targetTe != null) {
            IItemHandler handler = targetTe.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, facing.getOpposite());
            if (handler != null) {
                stack = ItemHandlerHelper.insertItem(handler, stack, false);
                if (stack.isEmpty()) {
                    return;
                }
            }
        }

        if (!stack.isEmpty()) {
            double x = pos.getX() + 0.5D + (double) facing.getXOffset() * 0.7D;
            double y = pos.getY() + 0.5D + (double) facing.getYOffset() * 0.7D;
            double z = pos.getZ() + 0.5D + (double) facing.getZOffset() * 0.7D;

            if (facing.getAxis() == EnumFacing.Axis.Y) {
                y -= 0.125D;
            } else {
                y -= 0.15625D;
            }

            EntityItem entityItem = new EntityItem(world, x, y, z, stack);
            entityItem.setDefaultPickupDelay();

            double speed = world.rand.nextDouble() * 0.1D + 0.2D;
            entityItem.motionX = (double) facing.getXOffset() * speed + world.rand.nextGaussian() * 0.0075D;
            entityItem.motionY = (double) facing.getYOffset() * speed + 0.2D + world.rand.nextGaussian() * 0.0075D;
            entityItem.motionZ = (double) facing.getZOffset() * speed + world.rand.nextGaussian() * 0.0075D;
            world.spawnEntity(entityItem);
        }
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (!worldIn.isRemote) {
            playerIn.openGui(CrafterMod.instance, CommonProxy.GUI_CRAFTER, worldIn, pos.getX(), pos.getY(), pos.getZ());
        }
        return true;
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        TileEntity te = worldIn.getTileEntity(pos);
        if (te instanceof TileEntityCrafter) {
            InventoryHelper.dropInventoryItems(worldIn, pos, (TileEntityCrafter) te);
        }
        super.breakBlock(worldIn, pos, state);
    }

    @Override
    public boolean hasComparatorInputOverride(IBlockState state) {
        return true;
    }

    @Override
    public int getComparatorInputOverride(IBlockState blockState, World worldIn, BlockPos pos) {
        TileEntity te = worldIn.getTileEntity(pos);
        if (te instanceof TileEntityCrafter) {
            return ((TileEntityCrafter) te).getComparatorOutput();
        }
        return 0;
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        TileEntity te = worldIn.getTileEntity(pos);
        boolean isCrafting = (te instanceof TileEntityCrafter) && ((TileEntityCrafter) te).isCrafting();
        return state.withProperty(CRAFTING, isCrafting);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        EnumFacing facing = EnumFacing.byIndex(meta & 7);
        boolean triggered = (meta & 8) != 0;
        return this.getDefaultState()
                .withProperty(FACING, facing)
                .withProperty(TRIGGERED, triggered)
                .withProperty(CRAFTING, false);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        int meta = state.getValue(FACING).getIndex();
        if (state.getValue(TRIGGERED)) {
            meta |= 8;
        }
        return meta;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, FACING, TRIGGERED, CRAFTING);
    }
}
