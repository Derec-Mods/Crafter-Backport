package net.quackimpala7321.crafter.proxy;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.quackimpala7321.crafter.inventory.ContainerCrafter;
import net.quackimpala7321.crafter.tileentity.TileEntityCrafter;

public class CommonProxy implements IGuiHandler {
    public static final int GUI_CRAFTER = 0;

    public void preInit(FMLPreInitializationEvent event) {}
    public void init(FMLInitializationEvent event) {}
    public void postInit(FMLPostInitializationEvent event) {}

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        if (ID == GUI_CRAFTER) {
            TileEntity te = world.getTileEntity(new BlockPos(x, y, z));
            if (te instanceof TileEntityCrafter) {
                return new ContainerCrafter(player.inventory, (TileEntityCrafter) te);
            }
        }
        return null;
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        return null;
    }
}
